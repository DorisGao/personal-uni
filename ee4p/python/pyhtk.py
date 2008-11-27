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

import sys, os, time, csv, pwd, subprocess, shutil
from optparse import OptionParser

__version__ = "0.1"

class HtkConfig:

	def __init__(self, binpath="/usr/bin/", configpath="./", projname="testHTK"):
		self.configfile = open(configpath + projname + ".conf", 'r')
		self.configs = {
			"binpath" : binpath,
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
	def __init__(self, binpath, confpath, projname):
		self.mode = "normal"
		self.realtime = False
		if not binpath.endswith("/"):
			binpath = binpath + "/"
		if not confpath.endswith("/"):
			confpath = confpath + "/"
		self.config = HtkConfig(binpath, confpath, projname)

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
		print "Starting HTK with following options:"
		if self.config.getSetting("project") != False:
			print "Project: " + self.config.getSetting("project")
		print "Running in %s mode" % self.mode
		print "HTK binary path: " + self.config.getSetting("binpath")

	def testing(self):
		pass

	def training(self):
	# Run HCopy to create initial HMMs
		try:
			hcopy1 = [self.config.getSetting("binpath") + "HCopy",
				"-C %s -S %s" %
				(self.config.getSetting("configHCopy"), self.config.getSetting("listTrainHCopy"))]
			subprocess.check_call(hcopy1)
			hcopy2 = [self.config.getSetting("binpath") + "HCopy",
				"-C %s -S %s" %
				(self.config.getSetting("configHCopy"), self.config.getSetting("listTestHCopy"))]
			subprocess.check_call(hcopy2)
		except CalledProcessError, e:
			print e
			sys.exit(1)
	# Now training
		print "Training"
		for direc in range(0, self.config.getSetting("space_step")):
			if not os.path.isdir(self.config.getSetting("hmmdir") + "/hmm" + direc):
				os.mkdir(self.config.getSetting("hmmdir") + "/hmm" + direc)

		try:
			hcompv1 = [self.config.getSetting("binpath") + "HCompV",
				"-C %s -o hmmdef -f 0.01 -m -S %s -M %s/hmm0 %s" %
				(self.config.getSetting("configTrain"), self.config.getSetting("listTrain"),
				self.config.getSetting("hmmdir"), self.config.getSetting("proto"))
				]
			subprocess.check_call(hcompv1)
		except CalledProcessError, e:
			print e
			sys.exit(1)

		print "Seeding complete"

		print "Copying HMM word models"
		hmmdef = open(self.config.getSetting("hmmdir") + "hmm0/hmmdef", 'r')
		record = False
		tmpdata = []
		for line in hmmdef:
			if line.contains("BEGINHMM"):
				record = True
			elif line.contains("ENDHMM"):
				record = False
				tmpdata.append(line)
			if record:
				tmpdata.append(line)

		model0 = open(self.config.getSetting("hmmdir") + "hmm0/models", 'a')
		wordlist = open(self.config.getSetting("worddict"), 'r')
		for line in wordlist:
			if not line.contains("sp"):
				model0.append('~h "%s"' % line.split()[0])
				for part in tmpdata:
					model0.append(part)
		model0.close()
		wordlist.close()

		vfloor = open(self.config.getSetting("hmmdir") + "hmm0/vFloors", 'r')
		macros = open(self.config.getSetting("hmmdir") + "hmm0/macros", 'a')
		tmpmacro = []
		for line in hmmdef:
			if not line.contains("~h"):
				tmpmacro.append(line)
		for line in tmpmacro:
			if line.contains("DIAGC"):
				tmpline = line.split("<")
			tmpmacro.remove(line)
			for item in tmpline:
				if not item.contains("DIAGC"):
					tmpmacro.append("<" + item)
		for line in tmpmacro:
			macros.append(line)
		for line in vfloor:
			macros.append(line)
		print "Finished HMM word models"

		for iteration in range(1, 3):
			print "Iteration %d" % iteration

			herest = [self.config.getSetting("binpath") + "HERest",
				"-D -C $CONFIG_train -I $LABELS -t 250.0 150.0 1000.0 -S $LIST_TRAIN -H $HMM_DIR/hmm$j/macros -H $HMM_DIR/hmm$j/models -M $HMM_DIR/hmm$i $WORD_LIST" %
				(self.config.getSetting("configTrain"),
				self.config.getSetting("wordLabel"),
				self.config.getSetting("listTrain"),
				self.config.getSetting("hmmdir"),
				iteration - 1,
				self.config.getSetting("hmmdir"),
				iteration - 1,
				self.config.getSetting("hmmdir"),
				iteration,
				self.config.getSetting("wordList"))
			]
			subprocess.check_call(herest)
		print "Copying for 4th iteration"
		shutil.copytree(self.config.getSetting("hmmdir") + "hmm3", self.config.getSetting("hmmdir") + "hmm4")
		print "Copied 4th iteration"

	# create silence model
		print "Correcting silence model"

		print "Corrected silence model"

	def recognition(self):
		pass

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

	app = Htk(options.binpath, options.configpath, options.project)

	if options.version:
		print "%s version %s" % (os.path.basename(sys.argv[0]),__version__)
	if options.training:
		app.setTraining(True)
	elif options.testing:
		app.setTesting(True)
	elif options.recog:
		app.setRecog(True, options.realtime)

	app.run()
