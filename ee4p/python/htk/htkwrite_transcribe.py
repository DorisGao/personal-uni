#!/usr/bin/python
#       htkwrite_tracker.py
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
from optparse import OptionParser

try:
    import htkwritefile
except Exception, e:
    print e
    sys.exit(1)

class HtkWrite:
    def __init__(self, filename):
        self.filename = filename
        self.output = self.filename.rstrip(".tsv") + ".mfcc"
        self.samples = -1
        self.combined = []

    def readfile(self):
        f = open(self.filename, 'r')
        data = False
        for line in f.readlines():
            #print(line)
            if data:
                print("adding data")
                self.combined.append(line.split())
                print(self.combined)

            if line.startswith("NO_OF_MARKERS"):
                self.samples = int(line.split()[1])
                print(self.samples)
            elif line.startswith("MARKER_NAMES"):
                data = True
                print("appending data")

    def run(self):
        #print(self.combined)
        print("passing to htwritefile")
        ret = htkwritefile.writebinfile(6000, self.samples, float(50), " ", self.combined, self.output)
        print(ret)

if __name__ == '__main__':
    parser = OptionParser()

    #parser.add_option("-V", "--version", action="store_true", default=False, dest="version", help="version information")

    (options, args) = parser.parse_args()

    htk = HtkWrite(args[0])
    htk.readfile()
    htk.run()

