#!/usr/bin/env python
#       mouselook.py
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

################################################## ####
#
# MouseLook.py Blender 2.45
#
# Clark R Thames
# Released under Creative Commons Attribution License
#
# Tutorial for using MouseLook.py can be found at
#
# www.tutorialsforblender3D.com
#
################################################## ####


########################### Logic Bricks

# Get controller
controller = GameLogic.getCurrentController()

# Get sensor named Mouse
mouse = controller.getSensor("Mouse")

# Get the actuators
rotLeftRight = controller.getActuator("LookLeftRight")
rotUpDown = controller.getActuator("LookUpDown")


############################## Need the size of the game window

import Rasterizer

width = Rasterizer.getWindowWidth()
height = Rasterizer.getWindowHeight()


############################### Get the mouse movement

def mouseMove():

    # distance moved from screen center
    #x = width/2 - mouse.getXPosition()
    #y = height/2 - mouse.getYPosition()
    x = width/2 - mouse.position[0]
    y = height/2 - mouse.position[1]

    # intialize mouse so it doesn't jerk first time
    if hasattr(GameLogic, 'init') == False:
        x = 0
        y = 0
    GameLogic.init = True

    return (x, y)

pos = mouseMove()


######## Figure out how much to rotate camera and player ########

# Mouse sensitivity
sensitivity = 0.0005

# Amount, direction and sensitivity
leftRight = pos[0] * sensitivity
upDown = pos[1] * sensitivity

# invert upDown
#upDown = -upDown


######### Use actuators to rotate camera and player #############

# Set the rotation values
rotLeftRight.setDRot( 0.0, 0.0, leftRight, False)
rotUpDown.setDRot( upDown, 0.0, 0.0, True)

# Use them
GameLogic.addActiveActuator(rotLeftRight, True)
GameLogic.addActiveActuator(rotUpDown, True)


############# Center mouse pointer in game window ###############

# Center mouse in game window
Rasterizer.setMousePosition(width/2, height/2)
