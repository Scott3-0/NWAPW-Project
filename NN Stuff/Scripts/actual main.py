##Libraries & modules
from __future__ import absolute_import, division, print_function, unicode_literals
import tensorflow as tf
print(tf.__version__)
from tensorflow import keras
from keras.models import Sequential
from keras.layers import Dense, Dropout
from keras.utils import to_categorical
from keras.utils.vis_utils import model_to_dot

import numpy as np
import wx
import matplotlib.pyplot as plt
import csv

import PIL
from PIL import Image, ImageOps
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

testNum = 78
################################################################################
##Imports and reshapes data
datasetPath = 'C:\\Users\\bremm\\Anaconda3\\envs\\tensorflow\\Plant ID NN Scripts\\Flower Imgs\\flower_images\\'
labelsPath = 'C:\\Users\\bremm\\Anaconda3\\envs\\tensorflow\\Plant ID NN Scripts\\Flower Imgs\\flower_labels.csv'
importData = []
for root, dirs, files in os.walk(os.path.normpath(datasetPath)):
    importData.append(files)

importData = np.asarray(importData)
print(importData)
print(importData.shape)
importData = importData.reshape(480,)
print(importData)
print(importData.shape)
################################################################################
##converts the string filepaths into images
images = []
for i in range(len(importData)):
    images.append(Image.open(datasetPath + importData[i]))

plt.imshow(images[testNum])
plt.show()
################################################################################
################################################################################
##Preprocesses the image into grayscale and resizes to 32x32px
for i in range(len(images)):

    images[i] = images[i].convert('L')
    images[i] = images[i].resize((32, 32), Image.ANTIALIAS)
    print(importData[i])

plt.imshow(images[testNum])
plt.show()
################################################################################
##Turns the preprocesssed images into 1d arrays of floats([0,1])
data = []

for i in range(len(images)):
    data.append(np.array(images[i]))
data = np.float32(data)
data /= 255
#They are now floats
data = np.reshape(data, (1024,len(data)))
#they are now 1d arrays

print(data[testNum])
print(data[testNum].shape)
################################################################################
##Takes all the labels from the .csv and puts them into an array
labels = []

with open(labelsPath, 'r') as f:
    reader = csv.reader(f,  delimiter=',',  quoting=csv.QUOTE_NONE)
    for row in reader:
        labels.append(row[1])
print(labels)
print(len(labels))

labels = np.int32(labels)
labels = np.asarray(labels)
#labels are now formatted
################################################################################
##divides data into trainingand testing
data = np.swapaxes(data,0,1) #now the array is data[img number][pixel vals]

trainData = []
testData = []

trainLabels = []
testLabels = []

trainData = data[len(data)//4:]
testData = data[:len(data)//4]

trainLabels = labels[len(labels)//4:]
testLabels = labels[:len(labels)//4]

print(data.shape)
trainData = np.asarray(trainData)
testData = np.asarray(testData)
print(len(data))
print(len(trainData))
print(trainData.shape)
print(len(testData))

print(len(trainLabels))
print(len(testLabels)) #labels & data are now divided into training and testing

################################################################################
################################################################################
epock = 50
batchSize = 10
##model
model = Sequential()

model.add(Dense(512, activation='relu', input_shape=(1024,)))
model.add(Dropout(0.5))
model.add(Dense(256, activation='relu'))
model.add(Dropout(0.25))
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
################################################################################
################################################################################
