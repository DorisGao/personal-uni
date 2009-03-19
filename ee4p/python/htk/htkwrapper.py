#!/usr/bin/python
#       htk.py
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
import htkconfig, htkwrite
from subprocess import CalledProcessError

__version__ = "0.1"

class HtkWrapper:
	def __init__(self, binpath, confpath, projname, visual=None):
		self.mode = "normal"
		self.realtime = False
		if not binpath.endswith("/"):
			binpath = binpath + "/"
		if not confpath.endswith("/"):
			confpath = confpath + "/"
		self.config = htkconfig.HtkConfig(binpath, confpath, projname)
		if visual != None:
			self.visual = visual
		else:
			print("Alternate extraction not loaded")
			sys.exit(1)

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
	# Clean directories first
		if self.config.getSetting("inputtype").find("audio") >= 0:
			for root, dirs, files in os.walk(self.config.getSetting("mfccdir"), topdown=False):
				for name in files:
					os.remove(os.path.join(root, name))

			if self.config.getSetting("inputtype") != "audio":
				print("Multi input processing")
				typeconf = open(self.config.getSetting("typeList"), 'r')
				tmpjointconf = typeconf.readlines()
				typeconf.close()
				jointconf = []
				for line in tmpjointconf:
					jointconf.append(line.split())
				del(tmpjointconf)

				for line in jointconf:
					if self.config.getSetting("inputtype").find("visual") >= 0:
						if self.visual != None:
						# Video processor detected
							#try:
							print("%s %s %s" % (self.config, self.visual, line))
							audiovisual = htkwrite.HtkWrite(self.config, self.visual, line)
							print(audiovisual)
							audiovisual.run()
							#except Exception, e:
							#	print e
							#	sys.exit(1)
						else:
							print("No other visual processor found, dieing")
							sys.exit(1)
					else:
						print("No extra processing found, dieing")
						sys.exit(1)
			elif self.config.getSetting("inputtype") == "audio":
			# We are processing audio only, no vodoo required
				print("Audio processing only")
				hcopy1 = [self.config.getSetting("binpath") + "HCopy",
					'-C', self.config.getSetting("configHCopy"),
					'-S', self.config.getSetting("listTrainHCopy")
				]
				hcopy2 = [self.config.getSetting("binpath") + "HCopy",
					'-C', self.config.getSetting("configHCopy"),
					'-S', self.config.getSetting("listTestHCopy")
				]
				try:
					ret1 = subprocess.check_call(hcopy1)
					ret2 = subprocess.check_call(hcopy2)
					if ret1 != 0 or ret2 != 0:
						print "Error, return codes wrong"
						sys.exit(1)
				except CalledProcessError, e:
					print e
					sys.exit(1)
			else:
				print("Something has gone badly, badly wrong.")
				sys.exit(1)

		else:
			print("Error, pyhtk is not setup to handle none audio based work.")
			print("Dieing now.")
			sys.exit(1)

	# Now training
		print "Training"
		if os.path.isdir(self.config.getSetting("hmmdir")):
			for root, dirs, files in os.walk(self.config.getSetting("hmmdir"), topdown=False):
				for name in files:
					os.remove(os.path.join(root, name))
				for name in dirs:
					os.rmdir(os.path.join(root, name))
		if not os.path.isdir(self.config.getSetting("hmmdir")):
			os.mkdir(self.config.getSetting("hmmdir"))
		for direc in range(0, int(self.config.getSetting("space_step"))+1):
			if not os.path.isdir(self.config.getSetting("hmmdir") + "hmm%d" % direc):
				os.mkdir(self.config.getSetting("hmmdir") + "hmm%d" % direc)

		try:
			hcompv = [self.config.getSetting("binpath") + "HCompV",
				'-C', self.config.getSetting("configTrain"),
				'-o', 'hmmdef',
				'-f', '0.01',
				'-m',
				'-S', self.config.getSetting("listTrain"),
				'-M', self.config.getSetting("hmmdir") + "hmm0",
				self.config.getSetting("proto")
			]
			ret = subprocess.check_call(hcompv)
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
			if line.find("BEGINHMM") >= 0:
				record = True
			elif line.find("ENDHMM") >= 0:
				record = False
				tmpdata.append(line)
			if record:
				tmpdata.append(line)

		model0 = open(self.config.getSetting("hmmdir") + "hmm0/models", 'a')
		wordlist = open(self.config.getSetting("worddict"), 'r')
		for line in wordlist:
			if not line.find("sp") >= 0:
				model0.write('~h "%s"\n' % line.split()[0])
				for part in tmpdata:
					model0.write(part)
		model0.close()
		wordlist.close()
		hmmdef.close()

		vfloor = open(self.config.getSetting("hmmdir") + "hmm0/vFloors", 'r')
		hmmdef = open(self.config.getSetting("hmmdir") + "hmm0/hmmdef", 'r')
		macros = open(self.config.getSetting("hmmdir") + "hmm0/macros", 'a')
		tmpmacro = []
		macroterms = ["~o", "STREAMINFO", "DIAGC"]
		for line in hmmdef:
			for items in macroterms:
				if items in line:
					tmpmacro.append(line.strip('\n'))
		tmpline = tmpmacro.pop().split("<")
		for item in tmpline:
			if item.find("DIAGC") == -1 and len(item) > 0:
				tmpmacro.append("<" + item)
		for line in tmpmacro:
			macros.write(line + "\n")
		for line in vfloor:
			macros.write(line)
		print "Finished HMM word models"
		vfloor.close()
		macros.close()
		hmmdef.close()
		del tmpmacro

		for iteration in range(1,4):
			print "Iteration %d" % iteration

			try:
				herest = [self.config.getSetting("binpath") + "HERest",
					'-D',
					'-C', self.config.getSetting("configTrain"),
					'-I', self.config.getSetting("wordLabel"),
					'-t', str(250.0), str(150.0), str(1000.0),
					'-S', self.config.getSetting("listTrain"),
					'-H', "%shmm%d/macros" % (self.config.getSetting("hmmdir"), iteration - 1),
					'-H', "%shmm%d/models" % (self.config.getSetting("hmmdir"), iteration - 1),
					'-M', "%shmm%d" % (self.config.getSetting("hmmdir"), iteration),
					self.config.getSetting("wordList")
				]
				ret = subprocess.check_call(herest)
				if ret != 0:
					print "Error, return codes wrong"
					sys.exit(1)
			except CalledProcessError, e:
				print e
				sys.exit(1)

		print "Copying for 4th iteration"
		for root, dirs, files in os.walk(self.config.getSetting("hmmdir") + "hmm3", topdown=False):
				for name in files:
					shutil.copy(self.config.getSetting("hmmdir") + "hmm3/" + name, self.config.getSetting("hmmdir") + "hmm4")
		print "Copied 4th iteration"

	# create silence model
		print "Correcting silence model"
		model3 = open(self.config.getSetting("hmmdir") + "hmm3/models", 'r')
		model4 = open(self.config.getSetting("hmmdir") + "hmm4/models", 'a')
		tmpmodel = []
		record = False
		for line in model3:
			if line.find("sil") >= 0:
				record = True
			if record and line.find("ENDHMM") >= 0:
				record = False
			if record:
				tmpmodel.append(line)
		states = []
		midstate = []
		for line in tmpmodel:
			if line.find("STATE") >= 0:
				states.append([tmpmodel[tmpmodel.index(line):tmpmodel.index(line) + 6]])
		for stat in states[int(len(states)/2)][0]:
			midstate.append(stat)
		model4.write('~h "sp"\n<BEGINHMM>\n<NUMSTATES> 3\n<STATE> 2\n')
		for line in midstate:
			if line.find("STATE") == -1:
				model4.write(line)
		model4.write("<TRANSP> 3\n")
		model4.write("0.000000e+00 5.000000e-01 5.000000e-01\n")
		model4.write("0.000000e+00 5.000000e-01 5.000000e-01\n")
		model4.write("0.000000e+00 0.000000e+00 0.000000e+00\n")
		model4.write("<ENDHMM>\n")

		model3.close()
		model4.close()

		try:
			hhed = [self.config.getSetting("binpath") + "HHEd",
				'-T', "3",
				'-H', self.config.getSetting("hmmdir") + "hmm4/macros",
				'-H', self.config.getSetting("hmmdir") + "hmm4/models",
				'-M', self.config.getSetting("hmmdir") + "hmm5",
				self.config.getSetting("hedsilsp"),
				self.config.getSetting("wordList")
			]
			ret = subprocess.check_call(hhed)
			if ret != 0:
				print "Error, return codes wrong"
				sys.exit(1)
		except CalledProcessError, e:
			print e
			sys.exit(1)
		print "Corrected silence model"

		for iteration in range(5, int(self.config.getSetting("space_step")) + 1):
			print "Iteration %d" % iteration

			try:
				herest = [self.config.getSetting("binpath") + "HERest",
					'-D',
					'-C', self.config.getSetting("configTrain"),
					'-I', self.config.getSetting("wordLabelSP"),
					'-S', self.config.getSetting("listTrain"),
					'-H', "%shmm%d/macros" % (self.config.getSetting("hmmdir"), iteration - 1),
					'-H', "%shmm%d/models" % (self.config.getSetting("hmmdir"), iteration - 1),
					'-M', "%shmm%d" % (self.config.getSetting("hmmdir"), iteration),
					self.config.getSetting("wordListSP")
				]
				subprocess.check_call(herest)
				if ret != 0:
					print "Error, return codes wrong"
					sys.exit(1)
			except CalledProcessError, e:
				print e
				sys.exit(1)

		print "%d Iterations completed" % (int(self.config.getSetting("space_step")) - 5)
		print "Training complete"

	def testing(self):
		print "Testing"

		if os.path.isfile(self.config.getSetting("resultdir") + self.config.getSetting("project") + ".mlf"):
			os.remove(self.config.getSetting("resultdir") + self.config.getSetting("project") + ".mlf")
		if os.path.isfile(self.config.getSetting("resultdir") + self.config.getSetting("project") + ".res"):
			os.remove(self.config.getSetting("resultdir") + self.config.getSetting("project") + ".res")

		try:
			hvite = [self.config.getSetting("binpath") + "HVite",
				'-H', "%shmm%d/macros" % (self.config.getSetting("hmmdir"), int(self.config.getSetting("space_step"))),
				'-H', "%shmm%d/models" % (self.config.getSetting("hmmdir"), int(self.config.getSetting("space_step"))),
				'-S', self.config.getSetting("listTest"),
				'-C', self.config.getSetting("configTest"),
				'-w', self.config.getSetting("wordnet"),
				'-i', self.config.getSetting("resultdir") + self.config.getSetting("project") + ".mlf",
				'-p', '10',
				'-s', '0.0',
				self.config.getSetting("worddict"),
				self.config.getSetting("wordListSP")
			]
			ret = subprocess.check_call(hvite)
			if ret != 0:
				print "Error, return codes wrong"
				sys.exit(1)
		except CalledProcessError, e:
			print e
			sys.exit(1)

		try:
			hresult = [self.config.getSetting("binpath") + "HResults",
				'-e', '\"???\"', 'sil',
				'-e', '\"???\"', 'sp',
				'-I', self.config.getSetting("wordLabelSP"),
				self.config.getSetting("wordListSP"),
				self.config.getSetting("resultdir") + self.config.getSetting("project") + ".mlf"
			]
			result = open(self.config.getSetting("resultdir") + self.config.getSetting("project") + ".res", 'w')
			ret = subprocess.check_call(hresult, stdout=result)
			if ret != 0:
				print "Error, return codes wrong"
				sys.exit(1)
			else:
				result.close()
		except CalledProcessError, e:
			print e
			sys.exit(1)

		print "Testing finished"
		print "Results can be found at %s" % self.config.getSetting("resultdir") + self.config.getSetting("project") + ".res"

	def recognition(self):
		pass
