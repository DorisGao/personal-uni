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

import sys, os, pwd, subprocess, shutil
from subprocess import CalledProcessError
from optparse import OptionParser

__version__ = "0.1"

class HtkConfig:

	def __init__(self, binpath="/usr/bin/", configpath="./", projname="testHTK", list=False):
		self.configfile = open(configpath + projname + ".conf", 'r')
		self.configs = {
			"binpath" : binpath,
			"project" : projname,
			"confpath" : configpath,
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
			"hedsilsp" : configpath + "lib/hedsilsp_" + projname + ".hed",
			"proto" : configpath + "lib/proto_" + projname,
			"results" : configpath + "results/results_" + projname,
			"flags" : "",
			"space_step" : 8
		}

		if not list:
			for line in self.configfile:
				if line.split()[0] in self.configs:
					self.configs[line.split()[0]] = line.split()[1]

			for part in self.configs:
				if not self.configs[part].isdigit():
					if not os.path.exists(self.configs[part]):
						print "%s does not exist, dieing" % self.configs[part]
						sys.exit(1)
					else:
						if self.configs[part].endswith("/"):
							if not os.path.isdir(self.configs[part]):
								print "%s is not a directory and it is configured as such, dieing" % self.configs[part]
								sys.exit(1)
						else:
							if not os.path.isfile(self.configs[part]) or not os.path.exists(self.configs[part]):
								print "%s is not a file and it is configured as such, dieing" % self.configs[part]
								sys.exit(1)

	def getSetting(self, setting):
		if setting in self.configs:
			return self.configs[setting]
		else:
			return False

	def listSettings(self):
		for item in self.configs:
			print item

class Htk:
	def __init__(self, binpath, confpath, projname):
		self.mode = "normal"
		self.realtime = False
		if not binpath.endswith("/"):
			binpath = binpath + "/"
		if not confpath.endswith("/"):
			confpath = confpath + "/"
		self.config = HtkConfig(binpath, confpath, projname)

	def listConfig(self):
		self.config.listSettings()

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
		print "HTK config path: " + self.config.getSetting("confpath")

		if self.mode == "normal":
			self.training()
			self.testing()
		elif self.mode == "training":
			self.training()
		elif self.mode == "testing":
			self.testing()
		elif self.mode == "recog":
			self.recognition()

	def training(self):
	# Run HCopy to create initial HMMs
		try:
			ret1 = subprocess.check_call("-C %s -S %s" %
				(self.config.getSetting("configHCopy"),
				self.config.getSetting("listTrainHCopy")),
				executable=self.config.getSetting("binpath") + "HCopy"
			)
			ret2 = subprocess.check_call("-C %s -S %s" % (self.config.getSetting("configHCopy"),
				self.config.getSetting("listTestHCopy")),
				executable=self.config.getSetting("binpath") + "HCopy"
			)
			if ret1 != 0 or ret2 != 0:
				print "Error, return codes wrong"
				sys.exit(1)
		except CalledProcessError, e:
			print e
			sys.exit(1)
	# Now training
		print "Training"
		for direc in range(0, self.config.getSetting("space_step")+1):
			if not os.path.isdir(self.config.getSetting("hmmdir") + "hmm%d" % direc):
				os.mkdir(self.config.getSetting("hmmdir") + "hmm%d" % direc)

		try:
			ret = subprocess.check_call("-C %s -o hmmdef -f 0.01 -m -S %s -M %shmm0 %s" %
				(self.config.getSetting("configTrain"),
				self.config.getSetting("listTrain"),
				self.config.getSetting("hmmdir"),
				self.config.getSetting("proto")),
				executable=self.config.getSetting("binpath") + "HCompV"
			)
			if ret != 0:
				print "Error, return codes wrong"
				sys.exit(1)
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
		vfloor.close()
		macros.close()
		del tmpmacro

		for iteration in range(1, 3):
			print "Iteration %d" % iteration

			try:
				ret = subprocess.check_call("-D -C %s -I %s -t 250.0 150.0 1000.0 -S %s -H %shmm%d/macros -H %shmm%d/models -M %shmm%d %s" %
					(self.config.getSetting("configTrain"),
					self.config.getSetting("wordLabel"),
					self.config.getSetting("listTrain"),
					self.config.getSetting("hmmdir"),
					iteration - 1,
					self.config.getSetting("hmmdir"),
					iteration - 1,
					self.config.getSetting("hmmdir"),
					iteration,
					self.config.getSetting("wordList")),
					executable=self.config.getSetting("binpath") + "HERest"
				)
				if ret != 0:
					print "Error, return codes wrong"
					sys.exit(1)
			except CalledProcessError, e:
				print e
				sys.exit(1)

		print "Copying for 4th iteration"
		shutil.copytree(self.config.getSetting("hmmdir") + "hmm3", self.config.getSetting("hmmdir") + "hmm4")
		print "Copied 4th iteration"

	# create silence model
		print "Correcting silence model"
		macro3 = open(self.config.getSetting("hmmdir") + "hmm3/macros", 'r')
		macro4 = open(self.config.getSetting("hmmdir") + "hmm4/macros", 'a')
		tmpmacro = []
		record = False
		for line in macro3:
			if line.contains("sil"):
				record = True
			if record and line.contains("ENDHMM"):
				record = False
			if record:
				tmpmacro.append(line)
		states = []
		for line in tmpmacro:
			if line.contains("STATE"):
				states.append([tmpmacro[tmpmacro.index(line):tmpmacro.index(line) + 6]])
		midstate = states[int(len(states)/2)]
		macro4.append('~h "sp"\n<BEGINHMM>\n<NUMSTATES> 3\n<STATE> 2\n')
		for line in midstate:
			macro4.append(line)
		macro4.append("<TRANSP> 3")
		for i in range(1,3):
			macro4.append("0.000000e+00 0.000000e+00 0.000000e+00")
		macro4.append("<ENDHMM>")

		macro3.close()
		macro4.close()

		try:
			ret = subprocess.check_call("-T 2 -H %shmm4/macros -H %shmm4/models -M %shmm5 $ED_CMDFILE1 $WORD_LISTSP" %
				(self.config.getSetting("hmmdir"),
				self.config.getSetting("hmmdir"),
				self.config.getSetting("hmmdir"),
				self.config.getSetting("hedsilsp"),
				self.config.getSetting("wordList")),
				executable=self.config.getSetting("binpath") + "HHEd"
			)
			if ret != 0:
				print "Error, return codes wrong"
				sys.exit(1)
		except CalledProcessError, e:
			print e
			sys.exit(1)
		print "Corrected silence model"

		for iteration in range(6, 8):
			print "Iteration %d" % iteration

			try:
				subprocess.check_call("-D -C %s -I %s -S %s -H %shmm%d/macros -H %shmm%d/models -M %shmm%d %s" %
					(self.config.getSetting("configTrain"),
					self.config.getSetting("wordLabelSP"),
					self.config.getSetting("listTrain"),
					self.config.getSetting("hmmdir"),
					iteration - 1,
					self.config.getSetting("hmmdir"),
					iteration - 1,
					self.config.getSetting("hmmdir"),
					iteration,
					self.config.getSetting("wordListSP")),
					executable=self.config.getSetting("binpath") + "HERest"
				)
				if ret != 0:
					print "Error, return codes wrong"
					sys.exit(1)
			except CalledProcessError, e:
				print e
				sys.exit(1)

		print "6 Iterations completed"
		print "Training complete"

	def testing(self):
		print "Testing"

		try:
			ret = subprocess.check_call(
				"-H %s -H %s -S %s -C %s -w %s -i %s.mlf %s %s %s" %
				("%shmm%d/macros" % (self.config.getSetting("hmmdir"), self.config.getSetting("space_step")),
				"%shmm%d/models" % (self.config.getSetting("hmmdir"), self.config.getSetting("space_step")),
				self.config.getSetting("listTest"),
				self.config.getSetting("configTest"),
				self.config.getSetting("wordnet"),
				self.config.getSetting("results"),
				self.config.getSetting("flags"),
				self.config.getSetting("worddict"),
				self.config.getSetting("wordListSP")),
				executable=self.config.getSetting("binpath") + "HVite"
			)
			if ret != 0:
					print "Error, return codes wrong"
					sys.exit(1)
		except CalledProcessError, e:
			print e
			sys.exit(1)

		try:
			subprocess.check_call(
				'-e "???" sil -e "???" sp -I %s %s %s.mlf >> %s' %
				(self.config.getSetting("wordLabelSP"),
				self.config.getSetting("wordListSP"),
				self.config.getSetting("results"),
				self.config.getSetting("results")),
				executable=self.config.getSetting("binpath") + "HResults"
			)
			if ret != 0:
					print "Error, return codes wrong"
					sys.exit(1)
		except CalledProcessError, e:
			print e
			sys.exit(1)

		print "Testing finished"

	def recognition(self):
		pass

if __name__ == "__main__":
	parser = OptionParser()

	parser.add_option("-V", "--version", action="store_true", default=False, dest="version", help="version information")
	parser.add_option("-l", "--list", action="store_true", default=False, dest="listconfig", help="List config options")
	parser.add_option("-b", "--binary", action="store", type="string", default="/usr/bin/", dest="binpath", help="path to the HTK binaries")
	parser.add_option("-c", "--config", action="store", type="string", default="./", dest="configpath", help="path to config file")
	parser.add_option("-p", "--project", action="store", type="string", default="testHTK", dest="project", help="project name")
	parser.add_option("-a", "--training", action="store_true", default=False, dest="training", help="run training only")
	parser.add_option("-t", "--testing", action="store_true", default=False, dest="testing", help="run testing only")
	parser.add_option("-r", "--recog", action="store_true", default=False, dest="recog", help="run in recognition mode")
	parser.add_option("-R", "--realtime", action="store_true", default=False, dest="realtime", help="run in realtime mode [EXPERIMENTAL]")

	(options, args) = parser.parse_args()

	if options.version:
		print "%s version %s" % (os.path.basename(sys.argv[0]),__version__)
	elif options.listconfig:
		conf = HtkConfig(list=True)
		conf.listSettings()
	else:
		app = Htk(options.binpath, options.configpath, options.project)

		if options.training:
			app.setTraining(True)
		elif options.testing:
			app.setTesting(True)
		elif options.recog:
			app.setRecog(True, options.realtime)

		app.run()
