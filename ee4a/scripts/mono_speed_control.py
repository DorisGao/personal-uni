#!/usr/bin/env python
#       mono_speed_control.py
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

controller = GameLogic.getCurrentController()
owner = controller.getOwner()

slow = controller.getSensor("mono_train_msg_slow")
stop = controller.getSensor("mono_train_msg_stop")
full = controller.getSensor("mono_train_msg_full")
restart = controller.getSensor("mono_train_msg_restart")

setspeed = controller.getActuator("mono_train_move_motion")

if owner.updown == "down":
    print("down")
    speed_norm = -0.20
    speed_slow = -0.10
elif owner.updown == "up":
    print("up")
    speed_norm = 0.20
    speed_slow = 0.10

if not stop.triggered and not slow.triggered and not restart.triggered and not full.triggered:
    print("running normally")
    setspeed.setDLoc(0.0, speed_norm, 0.0, True)
else:
    if restart.triggered:
        print("Restarting train")
        setspeed.setDLoc(0.0, speed_slow, 0.0, True)

    if slow.triggered:
        print("slow triggered")
        setspeed.setDLoc(0.0, speed_slow, 0.0, True)

    if full.triggered:
        print("full line speed")
        setspeed.setDLoc(0.0, speed_norm, 0.0, True)

    if stop.triggered:
        print("stop triggered")
        setspeed.setDLoc(0.0, 0.0, 0.0, True)

GameLogic.addActiveActuator(setspeed, True)
