##Libraries & modules
from __future__ import absolute_import, division, print_function, unicode_literals
import tensorflow as tf
print(tf.__version__)
from tensorflow import keras
from keras.models import Sequential
from keras.layers import Dense, Dropout, BatchNormalization, Input
from keras.utils import to_categorical
from keras.utils.vis_utils import model_to_dot
from tensorflow.python.tools import freeze_graph
from tensorflow.python.tools import optimize_for_inference_lib
from keras import backend as K

import numpy as np
import wx
import matplotlib.pyplot as plt
import csv

import time
from time import sleep

import PIL
from PIL import Image, ImageOps
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

testNum = 35
MODEL_NAME = "plantID"
################################################################################

def export_model(saver, model, input_node_names, output_node_name):
    tf.io.write_graph(K.get_session().graph_def, 'out', \
        MODEL_NAME + '_graph.pbtxt')

    saver.save(K.get_session(), 'out/' + MODEL_NAME + '.chkp')

    freeze_graph.freeze_graph('out/' + MODEL_NAME + '_graph.pbtxt', None, \
        False, 'out/' + MODEL_NAME + '.chkp', output_node_name, \
        "save/restore_all", "save/Const:0", \
        'out/frozen_' + MODEL_NAME + '.pb', True, "")

    input_graph_def = tf.GraphDef()
    with tf.gfile.Open('out/frozen_' + MODEL_NAME + '.pb', "rb") as f:
        input_graph_def.ParseFromString(f.read())

    output_graph_def = optimize_for_inference_lib.optimize_for_inference(
            input_graph_def, input_node_names, [output_node_name],
            tf.float32.as_datatype_enum)

    with tf.gfile.FastGFile('out/opt_' + MODEL_NAME + '.pb', "wb") as f:
        f.write(output_graph_def.SerializeToString())

    print("graph saved!")
################################################################################
##Imports and reshapes data
datasetPath = 'C:\\Users\\bremm\\Anaconda3\\envs\\tensorflow\\Plant ID NN Scripts\\Flower Imgs\\jpg\\'
labelsPath = 'C:\\Users\\bremm\\Anaconda3\\envs\\tensorflow\\Plant ID NN Scripts\\Flower Imgs\\imagelabelsGood.csv'
importData = []
for root, dirs, files in os.walk(os.path.normpath(datasetPath)):
    importData.append(files)

importData = np.asarray(importData)
print(importData)
print(importData.shape)
importData = importData.reshape(8189,)
print(importData)
print(importData.shape)
################################################################################
##converts the string filepaths into images
images = []
r = []
g = []
b =[]

for i in range(len(importData)):
    images.append(Image.open(datasetPath + importData[i]))
    print("Importing Image", i)

plt.imshow(images[testNum])
plt.show()
################################################################################
################################################################################
##Preprocesses the image into grayscale and resizes to 32x32px
rb = []
for i in range(len(images)):
    images[i] = images[i].resize((32, 32), Image.ANTIALIAS)

    tempR, tempG, tempB = images[i].split()
    r.append(tempR)
    g.append(tempG)
    b.append(tempB)

    print(importData[i])

plt.imshow(images[testNum])
plt.show()
################################################################################
##Turns the preprocesssed images into 1d arrays of floats([0,1])
data = []
rData = []
bData = []
rbData = []
for i in range(len(images)):
    data.append(np.array(images[i]))
    rData.append(np.array(r[i]))
    bData.append(np.array(b[i]))

data = np.float32(data)
rData = np.float32(rData)
bData = np.float32(bData)

rbData = (rData + bData) / 2
data = rbData
plt.imshow(Image.fromarray(data[testNum]))
for i in range(100):
    plt.imshow(Image.fromarray(data[i*80]))
plt.show()
data /= 255
#They are now floats
data = np.reshape(data, (1024,len(data)))
#they are now 1d arrays

print(data[testNum])
print(data[testNum].shape)
################################################################################
##Takes all the labels from the .csv and puts them into an array
labels = []

reader = csv.reader(open(labelsPath, 'r', encoding='utf8'))
for key in reader:
    labels.append(key)
print(labels)
print(len(labels))

labels = np.int32(labels)
labels = np.asarray(labels)
np.reshape(labels, (1,8189))
#labels are now formatted
################################################################################
##divides data into training and testing
labels = np.swapaxes(labels,0,1)
labels.flatten()
print(labels)
for i in range(100):
    print(labels[i*80])
#print(labels.shape())]
goodLabels = []
data = np.swapaxes(data,0,1) #now the array is data[img number][pixel vals]
#labels = np.swapaxes(labels,0,1) #now the array is correctly formatted
for i in range(len(labels)):
    goodLabels.append(labels[i][0]) ######USE THIS

goodLabels = np.int32(goodLabels)
goodLabels = np.asarray(goodLabels)

tempLabels = labelsPath
for i in range(1000):
    randX = np.random.randint(0,8188)
    randY = np.random.randint(0,8188)

    x = data[randX]
    y = data[randY]
    a = goodLabels[randX]
    b = goodLabels[randY]
    print(goodLabels[b])
    tempData = x
    tempLabels = a

    x = y
    a = b

    y = tempData
    print(goodLabels[b])
    b = tempLabels
    print('Swapped Indeces', randX, 'and', randY)
    print(goodLabels[b], '\n')

trainData = []
testData = []

trainLabels = []
testLabels = []

trainData = data[len(data)//4:]
testData = data[:len(data)//4]

trainLabels = goodLabels[len(goodLabels)//4:]
testLabels = goodLabels[:len(goodLabels)//4]

print(data.shape)
trainData = np.asarray(trainData)
testData = np.asarray(testData)
print(len(data))
print(len(trainData))
print(trainData.shape)
print(len(testData))

print('\n')
print(goodLabels.shape)
print(len(trainLabels))
print(len(testLabels)) #labels & data are now divided into training and testing

################################################################################
print(trainData[1])
print(trainLabels[5000])
print(trainLabels)
################################################################################
epock = 1000
batchSize = 10
##model
model = Sequential()

model = Sequential()

model.add(Dense(512, activation='relu', input_shape=(1024,)))
model.add(Dense(512, activation='relu'))
model.add(Dense(256, activation='relu'))
model.add(Dense(256, activation='relu'))
model.add(Dropout(0.25))
model.add(Dense(128, activation='relu'))
model.add(Dense(128, activation='relu'))
model.add(Dense(1, activation='softmax'))

    #Compiling
model.compile(optimizer='rmsprop',
              loss='binary_crossentropy',
              metrics=['accuracy'])

#Training
model.fit(trainData, trainLabels,
          batch_size=batchSize,
          epochs=epock,
          verbose=1,
          validation_data=(testData, testLabels))
################################################################################
################################################################################
results = model.evaluate(testData, testLabels)
print(results)
print(goodLabels[0])
#model.save("model.h5")
################################################################################
################################################################################
