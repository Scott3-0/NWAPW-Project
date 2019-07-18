##Setup & Packages
from __future__ import absolute_import, division, print_function, unicode_literals

import tensorflow as tf
print('test')
from tensorflow import keras

import numpy as np

print(tf.__version__)


import matplotlib.pyplot as plt

################################################################################
##Importing dataset
imdb = keras.datasets.imdb
# save np.load5
np_load_old = np.load

# modify the default parameters of np.load
np.load = lambda *a,**k: np_load_old(*a, allow_pickle=True, **k)

# call load_data with allow_pickle implicitly set to true
(train_data, train_labels), (test_data, test_labels) = imdb.load_data(num_words=10000)

# restore np.load for future normal usage
np.load = np_load_old
################################################################################
print("Training entries: {}, labels: {}".format(len(train_data), len(train_labels)))
print(train_data[0])
len(train_data[0]), len(train_data[1])
################################################################################
##Data encoding (Preprocessing)
# A dictionary mapping words to an integer index
word_index = imdb.get_word_index()

# The first indices are reserved
word_index = {k:(v+3) for k,v in word_index.items()}
word_index["<PAD>"] = 0
word_index["<START>"] = 1
word_index["<UNK>"] = 2  # unknown
word_index["<UNUSED>"] = 3

reverse_word_index = dict([(value, key) for (key, value) in word_index.items()])

def decode_review(text):
    return ' '.join([reverse_word_index.get(i, '?') for i in text])

decode_review(train_data[0])
print(decode_review(train_data[0]))
################################################################################
##Data formatting
#This pads the reviews so that they can only be 256 words long
train_data = keras.preprocessing.sequence.pad_sequences(train_data,
                                                        value=word_index["<PAD>"],
                                                        padding='post',
                                                        maxlen=256)

test_data = keras.preprocessing.sequence.pad_sequences(test_data,
                                                       value=word_index["<PAD>"],
                                                       padding='post',
                                                       maxlen=256)
print(decode_review(train_data[0]))
################################################################################

#input shape is the vocab count used for the reviews (10,000 words)
vocab_size = 10000
##Model
model = keras.Sequential()

model.add(keras.layers.Embedding(vocab_size, 16))
model.add(keras.layers.GlobalAveragePooling1D())
model.add(keras.layers.Dense(16, activation=tf.nn.relu))
model.add(keras.layers.Dense(1, activation=tf.nn.sigmoid))

model.summary()

model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['acc'])
################################################################################
##Model validation
x_val = train_data[:10000]
partial_x_train = train_data[10000:]

y_val = train_labels[:10000]
partial_y_train = train_labels[10000:]
################################################################################
##Model Training
history = model.fit(partial_x_train,
                    partial_y_train,
                    epochs=40,
                    batch_size=512,
                    validation_data=(x_val, y_val),
                    verbose=1)
################################################################################
##Model Evaluation
results = model.evaluate(test_data, test_labels)

print(results)
################################################################################
##Graph setup
history_dict = history.history
history_dict.keys()

acc = history_dict['acc']
val_acc = history_dict['val_acc']
loss = history_dict['loss']
val_loss = history_dict['val_loss']

epochs = range(1, len(acc) + 1)

##Loss Graph
#'bo' is short for 'blue dot'
plt.plot(epochs, loss, 'bo', label='Training loss')
#'b' is short for 'solid blue line'
plt.plot(epochs, val_loss, 'b', label='Validation loss')
plt.title('Training and validation loss')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.legend()

plt.show()


##Accuracy graph
plt.clf()   #clear figure

plt.plot(epochs, acc, 'bo', label='Training acc')
plt.plot(epochs, val_acc, 'b', label='Validation acc')
plt.title('Training and validation accuracy')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.legend()

plt.show()
################################################################################
##Prediction results
predictions = model.predict_classes(train_data)

print('\n')
for i in range(5):
    print('\n')
    print(decode_review(train_data[i]), "\n", predictions[i], train_labels[i])
