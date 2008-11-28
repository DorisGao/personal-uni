#!/usr/bin/python
"""
This program is demonstration for face and object detection using haar-like features.
The program finds faces in a camera image or video stream and displays a red box around them.

Original C implementation by:  ?
Python implementation by: Roman Stanchak
"""
import sys
import os
from opencv.cv import *
from opencv.highgui import *
from PIL import Image

# Global Variables
cascade = None
storage = cvCreateMemStorage(0)
cascade_name = "/usr/share/opencv/haarcascades/haarcascade_frontalface_alt.xml"
input_name = "../c/lena.jpg"

# Parameters for haar detection
# From the API:
# The default parameters (scale_factor=1.1, min_neighbors=3, flags=0) are tuned
# for accurate yet slow object detection. For a faster operation on real video
# images the settings are:
# scale_factor=1.2, min_neighbors=2, flags=CV_HAAR_DO_CANNY_PRUNING,
# min_size=<minimum possible face size
min_size = cvSize(20,20)
image_scale = 1.2
haar_scale = 1.2
min_neighbors = 2
haar_flags = CV_HAAR_DO_CANNY_PRUNING


def crop_count():

#load image
	im = Image.open("face.jpg")
#get image dimensions
	images = im.size
	iheight = images[0] / 2
	iwidth = images [1]
#set cropping area
	box = (0,iheight,iwidth,iwidth)
#crop image in half
	im = im.crop(box)
#get cropped image dimensions
	crops = im.size
	cheight = crops[0] / 4
	cwidth = crops[1]
#set new crop area
	box2 = (60,cheight, iwidth - 60, cwidth)
#crop mouth
	im2 = im.crop(box2)
#save picture
	im2.save("current.jpg")
	os.remove("face.jpg")
	source = cvLoadImage ("current.jpg")
	grey = cvCreateImage (cvSize (source.width, source.height), 8, 1)
	output = cvCreateImage (cvSize (source.width, source.height), 8, 1)

# Convert to greyscale
	cvCvtColor (source, grey, CV_BGR2GRAY)

# Convert to black and white based on threshold of 50
	cvThreshold(grey, output, 50, 255, CV_THRESH_BINARY)

	count = 0

# Count number of dark pixels
	for i in output:
		for j in i:
			if j == 255:
				count += 1

	print "Number of dark pixels: " + str(count)

def detect_and_draw( img ):
	# allocate temporary images
	gray = cvCreateImage( cvSize(img.width,img.height), 8, 1 )
	small_img = cvCreateImage( cvSize( cvRound (img.width/image_scale),
	cvRound (img.height/image_scale)), 8, 1 )

	# convert color input image to grayscale
	cvCvtColor( img, gray, CV_BGR2GRAY )

	# scale input image for faster processing
	cvResize( gray, small_img, CV_INTER_LINEAR )

	cvEqualizeHist( small_img, small_img )

	cvClearMemStorage( storage )

	if( cascade ):
		t = cvGetTickCount()
		faces = cvHaarDetectObjects( small_img, cascade, storage,
						haar_scale, min_neighbors, haar_flags, min_size )
		t = cvGetTickCount() - t
		print "detection time = %gms" % (t/(cvGetTickFrequency()*1000.))
		if faces:
			for face_rect in faces:
				x = int(face_rect.x*image_scale)
		y = int(face_rect.y*image_scale)
		h = int(face_rect.height*image_scale)
		w = int(face_rect.width*image_scale)
		face = cvRect(x,y,w,h)
		crop = cvGetSubRect(img,face)
		cvSaveImage("face.jpg", crop)

if __name__ == '__main__':

	if len(sys.argv) > 1:

		if sys.argv[1].startswith("--cascade="):
			cascade_name = sys.argv[1][ len("--cascade="): ]
			if len(sys.argv) > 2:
				input_name = sys.argv[2]

		elif sys.argv[1] == "--help" or sys.argv[1] == "-h":
			print "Usage: facedetect --cascade=\"<cascade_path>\" [filename|camera_index]\n"
			sys.exit(-1)

		else:
			input_name = sys.argv[1]

	# the OpenCV API says this function is obsolete, but we can't
	# cast the output of cvLoad to a HaarClassifierCascade, so use this anyways
	# the size parameter is ignored
	cascade = cvLoadHaarClassifierCascade( cascade_name, cvSize(1,1) )

	if not cascade:
		print "ERROR: Could not load classifier cascade"
		sys.exit(-1)


	if input_name.isdigit():
		capture = cvCreateCameraCapture( int(input_name) )
		print "Capturing from camera"
	else:
		capture = cvCreateFileCapture( input_name )
		print "Capturing from file"

	if( capture ):
		frame_copy = None
		i = 0
		while True:
			frame = cvQueryFrame( capture )
			print i
			if( not frame ):
				break
			if( not frame_copy ):
				frame_copy = cvCreateImage( cvSize(frame.width,frame.height), IPL_DEPTH_8U, frame.nChannels )
			if( frame.origin == IPL_ORIGIN_TL ):
				cvCopy( frame, frame_copy )
			else:
				cvFlip( frame, frame_copy, 0 )
				detect_and_draw( frame_copy )
				print "detecting and drawing!"
			crop_count()
			i = i+1
			if( cvWaitKey( 10 ) >= 0 ):
				break

	else:
		image = cvLoadImage( input_name, 1 )

		if( image ):

			detect_and_draw( image )
			cvWaitKey(0)

	cvDestroyWindow("result")
