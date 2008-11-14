#!/usr/bin/python
#       pyhtk.py
#
#       Copyright 2008 Sam Black <samwwwblack@lapwing.org>
#
#       This program is free software; you can redistribute it and/or modify
#       it under the terms of the GNU General Public License as published by
#       the Free Software Foundation; either version 3 of the License, or
#       (at your option) any later version.
#
#       This program is distributed in the hope that it will be useful,
#       but WITHOUT ANY WARRANTY; without even the implied warranty of
#       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#       GNU General Public License for more details.
#
#       You should have received a copy of the GNU General Public License
#       along with this program; if not, write to the Free Software
#       Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
#       MA 02110-1301, USA.

import sys, os, time, csv, pwd
from optparse import OptionParser

__version__ = "0.1"

class Htk:
	def __init__(self):
		self.htkpath = "/usr/bin/"
		self.training = True
		self.testing = True
		self.recog = False
		self.realtime = False

	def testing(self):
		pass

	def training(self):
		pass

	def setHTKpath(self, path):
		if not path.endswith("/"):
			self.htkpath = path + "/"
		else:
			self.htkpath = path

	def setTraining(self, flag):
		if flag != True or flag != False:
			print "Must be True or False"
			sys.exit(1)
		else:
			self.training = flag

	def setTesting(self, flag):
		if flag != True or flag != False:
			print "Must be True or False"
			sys.exit(1)
		else:
			self.testing = flag

	def setRecog(self, flag, rt=False):
		if flag != True or flag != False:
			print "Must be True or False"
			sys.exit(1)
		else:
			self.testing = flag

	def run(self):
		print "Starting HTK with following options:"
		print "HTK binary path: " + self.htkpath

if __name__ == "__main__":
	parser = OptionParser()

	parser.add_option("-V", "--version", action="store_true", default=False, dest="version", help="version information")
	parser.add_option("-b", "--binary", action="store", type="string", default="/usr/bin/", dest="binpath", help="path to the HTK binaries")
	parser.add_option("-a", "--training", action="store_true", default=False, dest="training", help="run training only")
	parser.add_option("-t", "--testing", action="store_true", default=False, dest="testing", help="run testing only")
	parser.add_option("-r", "--recog", action="store_true", default=False, dest="recog", help="run in recognition mode")
	parser.add_option("-R", "--realtime", action="store_true", default=False, dest="realtime", help="run in realtime mode [EXPERIMENTAL]")

	(options, args) = parser.parse_args()

	app = Htk()

	if options.version:
		print "%s version %s" % (os.path.basename(sys.argv[0]),__version__)
	elif options.binpath != "/usr/bin/":
		app.setHTKpath(options.binpath)
	else:
		app.run()
