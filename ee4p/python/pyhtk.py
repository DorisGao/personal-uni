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

class HtkConfig:
	def __init__(self, configpath="./", projname="testHTK"):
		self.configfile = open(configpath + projname + ".conf", 'r')
		self.configs = {
			"project" : projname,
			"hmmdir" : configpath + "hmmsTrained/",
			"listTrain" : configpath + "list/listTrain_" + projname + ".scp",
			"listTrainHCopy" : configpath + "list/listTrain_" + projname + "_HCopy.scp",
			"listTest" : configpath + "list/listTest_" + projname + ".scp",
			"listTestHCopy" : configpath + "list/listTest_" + projname + "_HCopy.scp",
			"configHCopy" : configpath + "config/configHCopy_" + projname,
			"configTrain" : configpath + "config/configTrain_" + projname,
			"configTest" : configpath + "config/configTest_" + projname,
			"wordList" : configpath + "lib/wordList_" + projname,
			"wordListSP" : configpath + "lib/wordList_" + projname + "_withsp",
			"wordLabel" : configpath + "label/wordLabel_" + projname + ".mlf",
			"wordLabelSP" : configpath + "label/wordLabel_" + projname + "_withsp.mlf",
			"wordnet" : configpath + "lib/wordnet_" + projname,
			"worddict" : configpath + "lib/worddict_" + projname,
			"proto" : configpath + "lib/proto_" + projname,
			"results" : configpath + "results/results_" + projname,
			"flags" : "",
			"space_step" : 8
		}

		for line in self.configfile:
			if line.split()[0] in self.configs:
				self.configs[line.split()[0]] = line.split()[1]

	def getSetting(self, setting):
		if setting in self.configs:
			return self.configs[setting]
		else:
			return False

class Htk:
	def __init__(self):
		self.htkpath = "/usr/bin/"
		self.mode = "normal"
		self.realtime = False

	def testing(self):
		pass

	def training(self):
		pass

	def recognition(self):
		pass

	def setHTKpath(self, path):
		if not path.endswith("/"):
			self.htkpath = path + "/"
		else:
			self.htkpath = path

	def setConfig(self, path):
		if not path.endswith("/"):
			self.config = HtkConfig(path + "/")
		else:
			self.config = HtkConfig(path)

	def setTraining(self, flag):
		if flag:
			self.mode = "training"

	def setTesting(self, flag):
		if flag:
			self.mode = "testing"

	def setRecog(self, flag, rt=False):
		if flag:
			self.mode = "recog"
			if rt:
				self.realtime = False
				print "Real time proecessing not yet supported"

	def run(self):
		try:
			self.config
		except:
			self.config = HtkConfig()
		print "Starting HTK with following options:"
		print "Project: " + self.config.getSetting("project")
		print "Running in %s mode" % self.mode
		print "HTK binary path: " + self.htkpath

if __name__ == "__main__":
	parser = OptionParser()

	parser.add_option("-V", "--version", action="store_true", default=False, dest="version", help="version information")
	parser.add_option("-b", "--binary", action="store", type="string", default="/usr/bin/", dest="binpath", help="path to the HTK binaries")
	parser.add_option("-c", "--config", action="store", type="string", default="./", dest="configpath", help="path to config file")
	parser.add_option("-p", "--project", action="store", type="string", default="testHTK", dest="project", help="project name")
	parser.add_option("-a", "--training", action="store_true", default=False, dest="training", help="run training only")
	parser.add_option("-t", "--testing", action="store_true", default=False, dest="testing", help="run testing only")
	parser.add_option("-r", "--recog", action="store_true", default=False, dest="recog", help="run in recognition mode")
	parser.add_option("-R", "--realtime", action="store_true", default=False, dest="realtime", help="run in realtime mode [EXPERIMENTAL]")

	(options, args) = parser.parse_args()

	app = Htk()

	if options.version:
		print "%s version %s" % (os.path.basename(sys.argv[0]),__version__)
	if options.binpath != "/usr/bin/":
		app.setHTKpath(options.binpath)
	if options.configpath != "./" or options.project != "testHTK":
		app.setConfig(options.configpath, options.project)
	if options.training:
		app.setTraining(True)
	if options.testing:
		app.setTesting(True)
	if options.recog:
		app.setRecog(True, options.realtime)

	app.run()
