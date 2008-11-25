#! /usr/bin/env python

import sys

# import OpenCV bindings
from opencv.cv import *
from opencv.highgui import *

# Give Window a name
win_name = "Test"

# Load source image and create blank image objects
if sys.argv[1] != None:
	file = sys.argv[1]
else:
	file = "../../face.bmp"
source = cvLoadImage (file)
grey = cvCreateImage (cvSize (source.width, source.height), 8, 1)
output = cvCreateImage (cvSize (source.width, source.height), 8, 1)

# Convert to greyscale
cvCvtColor (source, grey, CV_BGR2GRAY)

# Convert to black and white based on threshold of 50
cvThreshold(grey, output, 50, 255, CV_THRESH_BINARY);

count = 0

# Count number of dark pixels
for i in output:
  for j in i:
    if j == 255:
      count += 1

print "Number of dark pixels: " + str(count)

# Display output
cvNamedWindow (win_name, CV_WINDOW_AUTOSIZE)
cvShowImage(win_name, output)

# Quit on keypress
cvWaitKey (0)
