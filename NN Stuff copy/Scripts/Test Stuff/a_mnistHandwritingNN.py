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

import PIL
import cv2
from PIL import Image, ImageOps, ImageTk
import Tkinter as tki
import threading
import datetime
import imutils
import os
################################################################################
##[y/n] user input simplified
def userChoice(x): #input, output
    if  x == 'n':
        return False
    else:
        return True
################################################################################
##Importing dataset

mnist = keras.datasets.mnist

(train_data, train_labels), (test_data, test_labels) = mnist.load_data()
################################################################################
##Dataset details
print("Training entries: {}, labels: {}".format(len(train_data), len(train_labels)))
#60,000 28x28px images, 60,0000 labels
#print('\n', train_data[0])
#print(train_data[0][0], '\n', train_data[0][6])
#print(train_data[0][6][12])

#plt.imshow(train_data[0])
#plt.show()
len(train_data[0]), len(train_data[1])
################################################################################
##useful f(x)
def data_summary(train_data, train_labels, test_data, test_labels):
    """Summarize current state of dataset"""
    print('Train images shape:', train_data.shape)
    print('Train labels shape:', train_labels.shape)
    print('Test images shape:', test_data.shape)
    print('Test labels shape:', test_labels.shape)
    print('Train labels:', train_labels)
    print('Test labels:', test_labels)
################################################################################
##Preprocessing
##Reshaping
train_data = train_data.reshape(train_data.shape[0], 784) #28*28
train_data = np.float32(train_data)
train_data /= 255

test_data = test_data.reshape(test_data.shape[0], 784) #28*28
test_data = np.float32(test_data)
test_data /= 255

##Categorizing
train_labels = to_categorical(train_labels, 10) #10 = # of classes aka options
test_labels = to_categorical(test_labels, 10) # makes an array -> (0,1,...,10)

##Data Summary
data_summary(train_data, train_labels, test_data, test_labels)
################################################################################
################################################################################
batchSize = 128
epock = input('How many epochs?: ')
epock = np.int32(epock)
##Info from: https://www.kdnuggets.com/2018/06/basic-keras-neural-network-sequential-model.html
model = Sequential()
model.add(Dense(512, activation='relu', input_shape=(784,)))
model.add(Dropout(0.5))
model.add(Dense(256, activation='relu'))
model.add(Dropout(0.25))
model.add(Dense(10, activation='softmax'))

#Compiling
model.compile(optimizer='rmsprop',
              loss='categorical_crossentropy',
              metrics=['accuracy'])

#Training
model.fit(train_data, train_labels,
          batch_size=batchSize,
          epochs=epock,
          verbose=1,
          validation_data=(test_data, test_labels))
################################################################################
################################################################################
##Results
results = model.evaluate(test_data, test_labels)
print(results)
################################################################################
##Prediction results
predictions = model.predict_classes(train_data)


##Postprocessing
train_data *= 255
train_data = np.int32(train_data)
train_data = train_data.reshape(train_data.shape[0], 28, 28) #28*28

test_data *= 255
test_data = np.int32(test_data)
test_data = test_data.reshape(test_data.shape[0], 28, 28) #28*28

print('\n')
for i in range(10):
    print('\n')
    print('Data #', i, "\n", predictions[i], train_labels[i])
#    plt.imshow(train_data[i])
#    plt.show()
#User chooses dataset
userTestChoice = True

while userTestChoice == True:
    userTest = input('Pick a piece of data to test (0-59999): ')
    userTest = np.int32(userTest)

    print('Data #', userTest, "\n", predictions[userTest], train_labels[userTest])
    plt.imshow(train_data[userTest])
    plt.show()

    tempUserTestChoice = input('Another Test? [y/n]: ')
    userTestChoice = userChoice(tempUserTestChoice)
################################################################################
##User inputs own 28x28 image
userImageChoice = True

while userImageChoice == True:
    userImage = input('Enter file name of image to test (On white background): ')

    dir='C:\\Users\\bremm\\Anaconda3\\envs\\myenv\\Images\\'
    path=dir+userImage
    img = Image.open(path)
    plt.imshow(img)
    plt.show()
    ##image Preprocessing
    img = img.convert('L')
    img = PIL.ImageOps.invert(img)
    img = img.resize((28, 28), Image.ANTIALIAS)

    plt.imshow(img)
    plt.show()
    #converts to an array of ints
    userData = np.array(img)
    userData = np.int32(userData)
    print(userData)
    print(userData.dtype)
    print(userData.shape)
    #userData = np.int32(userData)

    #from [0,255] to [0,1]
    userData = np.float32(userData)
    userData /= 255
    #userData = userData.flatten()
    userData = np.reshape(userData, (1,784))
#    print(userData)
#    print(userData.shape)

    #Makes a prediction
    userGuess = model.predict(userData)
    #print("Predicted: ", userGuess) #results
    userGuess *= 100
    userGuess = np.round(userGuess, 3)
    userGuess = np.reshape(userGuess, (10, 1))
    ############################################################################
    ##print out it's guess for the inputted image
    print(userGuess.shape)
    print('\n')
    for i in range(len(userGuess)):
    #print(userGuess)
        print(userGuess[i], '% sure the number is a', i)
    ############################################################################
    tempUserImageChoice = input('Another Image? [y/n]: ')
    userImageChoice = userChoice(tempUserImageChoice)
################################################################################
input('Press ENTER to exit')
