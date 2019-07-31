# import the necessary packages
from __future__ import print_function
from PIL import Image
from PIL import ImageTk
import tkinter as tki
import threading
import datetime
import imutils
import cv2
import os
################################################################################
#follow this tutorial:
#https://www.pyimagesearch.com/2016/05/30/displaying-a-video-feed-with-opencv-and-tkinter/

#how to call it in the other .py file:
#https://duckduckgo.com/?q=how+to+call+a+class+from+one+.py+file+to+another&t=ffab&atb=v158-1&ia=qa&iax=qa
################################################################################
class PhotoBoothClass:
    def __init__(self, vs, outputPath):
		# store the video stream object and output path, then initialize
		# the most recently read frame, thread for reading frames, and
		# the thread stop event
        self.vs = vs
        self.outputPath = outputPath
        self.frame = None
        self.thread = None
        self.stopEvent = None

        #init the window and img panel
        self.root = tki.Tk()
        self.panel = None
        ########################################################################
        # create a button, that when pressed, will take the current
		# frame and save it to file
        btn = tki.Button(self.root, text="Snapshot", command=self.takeSnapshot)
        btn.pack(side="bottom", fill="both", expand="yes", padx=10, pady=10)

        # start a thread that constantly pools the video sensor for
		# the most recently read frame
        self.stopEvent = threading.Event()
        self.thread = threading.Thread(target=self.videoLoop, args=())
        self.thread.start()
