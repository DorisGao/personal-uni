#!/usr/bin/python
#       htkwrite.py
#
#       Copyright 2008-2009 Sam Black <samwwwblack@lapwing.org>
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

__version__ = "0.1"

import sys, os, pwd, subprocess, shutil
from subprocess import CalledProcessError

try:
    import htkwritefile
except Exception, e:
    print e
    sys.exit(1)

class HtkWrite:
    def __init__(self, config, processor, data):
        self.config = config
        self.processor = processor
        self.data = data
        self.options = {
            "mfcc" : "",
            "wavfile" : "",
            "videofile" : "",
            "wavtime" : "",
            "videotime" : ""
        }
        print(self.data)
        print(len(self.data))
        self._validateData()

    def _validateData(self):
        if self.config.getSetting("inputtype").find("visual") >= 0:
            for part in self.data:
                print("part is :%s:" % part)
                if part.endswith(".mfcc"):
                    self.options["mfcc"] = part
                elif part.endswith((".mpg")):
                    self.options["videofile"] = part
                elif part.endswith(".wav"):
                    self.options["wavfile"] = part
                elif part.startswith("v"):
                    self.options["videotime"] = part.lstrip("v")
                elif part.startswith("a"):
                    self.options["wavtime"] = part.lstrip("a")
                else:
                    print("Nothing found")
            print(self.options)
        else:
            print("No inputtype specified")

        for key in self.options:
            if self.options[key] is None:
                print('Error, "%s" is not set' % key)
                raise ValueError

    def _runHcopy(self):
        hcopy1 = [self.config.getSetting("binpath") + "HCopy",
            '-C', self.config.getSetting("configHCopy"),
            '-e', self.options["wavtime"],
            self.options["wavfile"],
            self.options["mfcc"].split(".")[0] + ".wav.mfcc"
        ]
        try:
            ret1 = subprocess.check_call(hcopy1)
            if ret1 != 0:
                print "Error, return codes wrong"
                sys.exit(1)
        except:
            raise CalledProcessError

    def _runExtraProcess(self):
        return self.processor.run(self.options["videofile"], self.options["videotime"])

    def _runHlist(self):
        hlist1 = [self.config.getSetting("binpath") + "HList",
            '-C', self.config.getSetting("configHList"),
            '-r',
            '-h',
            self.options["mfcc"].split(".")[0] + ".wav.mfcc"
        ]
        try:
            ret1 = subprocess.Popen(hlist1, stdout=subprocess.PIPE)
            output = ret1.communicate()[0]
        except:
            raise CalledProcessError

        return output

    def run(self):
        self._runHcopy()
        #altdata = self._runExtraProcess()
        altdata = [153, 195, 374, 234, 485, 178, 209, 278, 243, 310, 381, 326, 290, 237, 226, 168, 190, 188, 240, 243, 449, 277, 461, 328, 348, 584, 230, 385, -1, 170, 242, 180, 293, 238, 352, 324, 539, 226, 164, 188, 187, 240, 511, 246, 296, 232, 188, 499, 146, 186, 208, 205, 256, 253, 292, 229, 407, 246, 378, 176, 203, 217, 191, 443, 297, 294, 465, 237, 185, 224, 212, 187, 149, 218, -1, -1, 221, 364, 223, 694, 166, 247, 235, 411, 313, 465, 266, 369, 233, 185, 569, 174, 226, 162, 229, 274, 362, 335, -1, 349, 205, 205, 180, 170, 166, 272, 521, 241, 283, 530, 248, 318, 881, 513, 366, 390, 230, 589, 1028, 429, 264, 200, 636, 1174, 992, 698, 360, 628, 857, 998, 367, 243, 609, 1258, 937, 216, 371, 414, 934, 370, 581, 789, 1276, 1501, 843, 515, 204, 280, 919, 640, 927, 1181, 596, 1170, 1374, 1190, 1012, 936, 207, -1, 484, 310, 342, 799, 1217, 1371, 1405, 1195, 320, 676, 547, 739, 430, 561, 1130, 1330, 974, 218, 477, 327, 312, 649, 548, 375, 440, 240, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 564, 476, 411, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 427, 700, 301, 1019, 808, 235, 290, 307, 332, 461, 508, 443, 743, 709, 340, 366, 396, 457, 417, 615, -1, 475, 666, -1, 704, 271, 288, 312, 650, 435, 358, 348, 299, 397, 425, 591, 710, 412, 418, 340, 520, 758, 708, 666, 774, 936, 1173, 1068, 1001, 628, 466, 723, 762, 701, 371, 954, 1031, 678, 1439, 1227, 780, 989, 1405, 814, 1164, 752, 685, 728, 909, 1116, 1656, 1007, 838, 1177, 1117, 1290, 750, 610, 903, 535, 400, 327, 443, 511, 373]
        tmpwav = self._runHlist()

        wavmfcc = tmpwav.split("\n")
        mfccheader = []
        mfccdata = []

        for i in range(0, 3):
            tmp = wavmfcc[i].strip().split('  ')
            while "" in tmp:
                for part in tmp:
                    if part == "":
                        tmp.pop(tmp.index(part))
            for part in tmp:
                if part.find(':') > 0:
                    if part.split(":")[1] == "":
                        mfccheader.append([tmp[tmp.index(part)].strip(" :"), tmp[tmp.index(part) + 1].strip()])
                    else:
                        mfccheader.append([tmp[tmp.index(part)].split(":")[0].strip(), tmp[tmp.index(part)].split(":")[1].strip()])

        for i in range(3, len(wavmfcc) - 1):
            stringtmp = wavmfcc[i].strip().split()
            floattmp = []
            for string in stringtmp:
                floattmp.append(float(string))
            mfccdata.append(floattmp)

    # Calculate number of audio frames per alt frame
        if len(mfccdata) / len(altdata) > 1:
            ratio = len(mfccdata) / len(altdata)
            sdata = altdata
            combined = mfccdata
        elif len(altdata) / len(mfccdata) > 1:
            ratio = len(altdata) / len(mfccdata)
            sdata = mfccdata
            combined = altdata
        else:
            for line in mfccdata:
                mfccdata[mfccdata.index(line)].append(altdata[altdata.index(line)])

        slist = 0
        diff = int(ratio / 2)
        while slist < len(sdata):
            midpoint = ratio * slist + diff
            for i in range(midpoint - diff, midpoint + diff):
                combined[i].append(float(sdata[slist]))
            slist += 1

        del(sdata)
        del(altdata)
        del(mfccdata)

    # Now we can write out to binary
        htkwriteargs = dict(mfccheader)
        #try:
        ret = htkwritefile.writebinfile(int(htkwriteargs["Num Comps"]) + 1, int(htkwriteargs["Num Samples"]), float(htkwriteargs["Sample Period"].split()[0]), htkwriteargs["Sample Kind"].lstrip("MFCC"), combined, self.options["mfcc"])
        #   if ret > 0:
        #       print("Something went titsup")
        #   else:
        #       print("Executed")
        #except:
        #   raise Exception
