#!/usr/bin/python
#
#       transcribe.py
#
#       Copyright 2009 Sam Black <samwwwblack@lapwing.org>
#
#       This program is free software; you can redistribute it and/or modify
#       it under the terms of the GNU General Public License as published by
#       the Free Software Foundation; either version 2 of the License, or
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

import os, sys, string
import xml.sax

from xml.sax.handler import feature_namespaces, ContentHandler
from optparse import OptionParser

__version__ = "0.1"

class FindIssue(ContentHandler):
    def __init__(self, fname):
        self.times = []
        self.items = []
        self.endtime = -1

        self.labfile = fname + ".lab"

        self.interest = []
        self.inInterest = False

    def startElement(self, name, attrs):
        # If it's not a comic element, ignore it
        if name != "Turn" and name != "Sync":
            return
        elif name == "Turn":
            self.endtime = attrs.get('endTime', None)
        elif name == "Sync":
            self.times.append(attrs.get('time', None))

        if self.interest:
            self.inInterest = False
            item = string.join(self.interest)
            self.items.append(item)

    def endElement(self, name):
        if name == "Turn":
            item = string.join(self.interest)
            self.items.append(item)
        self.inInterest = True
        self.interest = []

    def endDocument(self):
        self.times.append(self.endtime)
        f = open(self.labfile, 'w')
        #f.write("#!MLF!#\n")
        s = str('"*/' + self.labfile + '"\n')
        f.write(s)
        f.write("000000 ")
        s = str(str(int(float(self.times[1]) * 1000000)) + " " + self.items[0] + "\n")
        f.write(s)

        for num in range(1, len(self.items)):
            s = str(str(int(float(self.times[num]) * 1000000)) + " " + str(int(float(self.times[num + 1]) * 1000000)) + " " + self.items[num] + "\n")
            f.write(s)
        f.write(".\n")

        f.close()
        print("Finished " + self.labfile)

    def characters(self, chrs):
        if self.inInterest:
            if chrs != "\n" and chrs != " ":
                self.interest.append(chrs)

    def print_data(self):
        if self.endtime != -1:
            print(self.times)
            print(self.items)
        else:
            print("XML traversal not complete.")

if __name__ == '__main__':
    # Create an XML parser
    xmlparser = xml.sax.make_parser()

    # Tell the parser we are not interested in XML namespaces
    xmlparser.setFeature(feature_namespaces, 0)

    parser = OptionParser()

    parser.add_option("-V", "--version", action="store_true", default=False, dest="version", help="version information")

    (options, args) = parser.parse_args()

    if options.version:
        print(os.path.basename(sys.argv[0]) + " version " + __version__)

    elif args:
        #print(args)
        if len(args) == 1:
            f = args[0]
            if os.path.exists(f):
                trs = open(f, 'r')
                fs = f.split("/")
                for part in fs:
                    if part.count(".trs") > 0:
                        lab = part.rstrip(".trs")
                        break
                dh = FindIssue(lab)
                xmlparser.setContentHandler(dh)
                xmlparser.parse(trs)
                #dh.print_data()
            else:
                print("File '" + f + "' does not exist")
                print("Try a different file.")
