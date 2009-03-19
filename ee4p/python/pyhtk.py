#!/usr/bin/python
#       pyhtk.py
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

import sys, os, pwd, subprocess, shutil
import htk
from subprocess import CalledProcessError
from optparse import OptionParser
sys.float_output_precision = 25

try:
    import visual_feature_extraction
except:
    print("Cannot load visual feature extraction")

__version__ = "0.1"

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
        conf = htk.htkconfig.HtkConfig(list=True)
        conf.listSettings()
    else:
        app = htk.htkwrapper.HtkWrapper(options.binpath, options.configpath, options.project, visual_feature_extraction.VisualFeatureExtraction)

        if options.training:
            app.setTraining(True)
        elif options.testing:
            app.setTesting(True)
        elif options.recog:
            app.setRecog(True, options.realtime)

        app.run()
