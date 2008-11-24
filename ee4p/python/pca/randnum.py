#!/usr/bin/python
#	   randnum.py
#
#	   Copyright 2008 Sam Black <samwwwblack@lapwing.org>
#
#	   This program is free software; you can redistribute it and/or modify
#	   it under the terms of the GNU General Public License as published by
#	   the Free Software Foundation; either version 2 of the License, or
#	   (at your option) any later version.
#
#	   This program is distributed in the hope that it will be useful,
#	   but WITHOUT ANY WARRANTY; without even the implied warranty of
#	   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#	   GNU General Public License for more details.
#
#	   You should have received a copy of the GNU General Public License
#	   along with this program; if not, write to the Free Software
#	   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
#	   MA 02110-1301, USA.

import sys, os, random, numpy
from math import sqrt

print "Creating 25 3D vectors for 1000 iterations"

pca = open ("randpca", 'w')
pca.write(" ")
pca.close()

pca = open("randpca", 'a')

ct = 0

while ct < 1000:
	a = random.uniform(1, 10)
	b = random.uniform(3, 9)
	c = random.uniform(-2, 2)
	d = random.uniform(1, 15)
	e = random.uniform(6, 12)

	pca.write("%f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f \n" %
	(a, b, c, d, e, a*b, b*c, c*d, d*e, e*a, a/b, b/c, c/d, d/e, e/a, a-b, b-c, c-d, d-e, e-a, a*c, b*d, c*e, d*a, e*b, a/c, b/d, c/e, d/a, e/b,
	a*d, b*e, c*a, d*b, e*c, a/d, b/e, c/a, d/b, e/c, a+b, b+c, c+d, d+e, e+a, a/e, b/d, c/2, d/b, e/a, a*a, b*b, c*c, d*d, e*e, a/2, b/2, 2*c,
	d/2, e/2, a*(b-c), b*(c-d), c*(d-e), d*(e-a), e*(a-b), (a*b*c)/d, (b*c*d)/e, (c*d*e)/a, (d*e*a)/b, (e*a*b)/c, sqrt(a), sqrt(b), sqrt(c*c),
	sqrt(d), sqrt(e)))

	ct += 1

pca.close()
print "Done random number generation"

pca = open("randpca")
tmpdata = []

for line in pca:
	tmpdata.append(line.split(" "))

print len(tmpdata)

for line in tmpdata:
	if " " in line:
		line.remove(" ")
	elif '\n' in line:
		line.remove('\n')
	for i in range(0, len(line) - 1):
		if len(line[i]) == 0:
			del line[i]

vectordata = []
for line in range(0, len(tmpdata) - 1):
	for col in range(0,71, 3):
		if line == 0:
			vectordata.append([float(tmpdata[line][col]), float(tmpdata[line][col+1]), float(tmpdata[line][col+2])])
		else:
			vectordata[col/3].append([float(tmpdata[line][col]), float(tmpdata[line][col+1]), float(tmpdata[line][col+2])])
			print vectordata[col/3]

tmpdataarrays = []
array_desc = [('x',numpy.float32),('y',numpy.float32),('z',numpy.float32)]
for data in vectordata:
	for col in range(0, len(data) - 1):
		print data[col]
		tmpdataarrays.append(numpy.array(data[col], dtype=array_desc))

print tmpdataarrays
print "Created tmp arrays"

dataarrays = []
for arr in tmpdataarrays:
	print arr
	print arr.mean()
	dataarrays.append[[arr, arr.mean(), arr.std(), arr.var()]]
print dataarrays
