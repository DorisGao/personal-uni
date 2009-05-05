#!/usr/bin/env python
#       mono_door_control.py
#
#       Copyright 2009 Sam Black <samwwwblack@lapwing.org>
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

import time
#import Blender

# Controller
controller = GameLogic.getCurrentController()

# Sensors
#proximity = controller.getSensor("door_proximity")
activate = controller.getSensor("door_activate")
#activate = controller.getSensor("door_open_act")

# Objects
#ldoor = Blender.Object.Get("l_door")
#print(ldoor.loc)
#rdoor = Blender.Object.Get("r_door")
#print(rdoor.loc)

# Actuators
#ldoor_act = controller.getActuator("l_door_move")
#rdoor_act = controller.getActuator("r_door_move")
open_snd = controller.getActuator("mono_door_open_snd")
close_snd = controller.getActuator("mono_door_close_snd")

#door_step = 0.001
doors_open = False

#open_snd.stopSound()
#close_snd.stopSound()

#if proximity.triggered:
#    print("Proximity")
#else:
#    print("Proximity false")

#if activate.triggered:
print("Door open triggered")
open_snd.startSound()
#for steps in range(0,10000):
#    ldoor_act.setDLoc(0.0, 0.0, door_step, True)
#    rdoor_act.setDLoc(0.0, 0.0, (0 - door_step), True)
doors_open = True
print("Doors opened")

time.sleep(3)

if doors_open:
    print("Doors triggered shut")
    close_snd.startSound()
    #for steps in range(0,10000):
    #    ldoor_act.setDLoc( 0.0, 0.0, (ldoor.loc[0] - door_step), True)
    #    rdoor_act.setDLoc( 0.0, 0.0, (rdoor.loc[0] + door_step), True)
    doors_open = False
    print("Doors closed")

# Use them
#GameLogic.addActiveActuator(ldoor_act, True)
#GameLogic.addActiveActuator(rdoor_act, True)
GameLogic.addActiveActuator(open_snd, True)
GameLogic.addActiveActuator(close_snd, True)
