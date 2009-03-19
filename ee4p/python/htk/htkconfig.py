#!/usr/bin/python
#       htkconfig.py
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

import sys, os, pwd, shutil

__version__ = "0.1"

class HtkConfig:
	def __init__(self, binpath="/usr/bin/", configpath="./", projname="testHTK", list=False):
		if not list:
			self.configfile = open(configpath + projname + ".conf", 'r')
	# Don't add the config path to these options
		self.no_conf = ["binpath", "project", "confpath", "inputtype", "space_step", "flags"]
		self.configs = {
			"binpath" : binpath,
			"project" : projname,
			"confpath" : configpath,
			"inputtype" : "audio",
			"hmmdir" : configpath + "hmmsTrained/",
			"mfccdir" : configpath + "mfccGen/",
			"typeList" : configpath + "list/typeList_" + projname,
			"listTrain" : configpath + "list/listTrain_" + projname + ".scp",
			"listTrainHCopy" : configpath + "list/listTrain_" + projname + "_HCopy.scp",
			"listTest" : configpath + "list/listTest_" + projname + ".scp",
			"listTestHCopy" : configpath + "list/listTest_" + projname + "_HCopy.scp",
			"configHList" : configpath + "config/configHList_" + projname + "_mfcc",
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
			"resultdir" : configpath + "results/",
			"space_step" : 8
		}

		if not list:
			for line in self.configfile:
				if len(line.split()) > 1:
					if line.split()[0] in self.configs:
						if line.split()[0] in self.no_conf:
							self.configs[line.split()[0]] = line.split()[1]
						else:
							self.configs[line.split()[0]] = configpath + line.split()[1]

			for part in self.configs:
				if not self.configs[part].isdigit():
					if not os.path.exists(self.configs[part]) and not part in self.no_conf:
						print "%s does not exist, dieing" % self.configs[part]
						sys.exit(1)
					elif not part in self.no_conf:
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
		print "Default settings:"
		for item in self.configs:
			print item + " %s" % self.configs[item]
