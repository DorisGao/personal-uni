/*
 *      girl_on_swing.c : This creates a girl on a swing model
 *
 *      Copyright 2008 Sam Black <samwwwblack@lapwing.org>
 *
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, *

 *      MA 02110-1301, USA.
 */

/* To get gcc to compile OpenGL correctly, you must include the following in the
 * gcc options;
 *
 *   rm girl_on_swing && gcc girl_on_swing.c -o girl_on_swing -lX11 -lXi -lXmu -lglut
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <GL/glut.h>

#define THETA_STEP 0.025
#define MAX_THETA 25
GLfloat angle = 0.0;
int scene_accel_x=0, scene_accel_y=0;

GLfloat colours[][4] =
	{	{1.0, 0.0, 0.0, 1.0}, // 0 red
		{0.0, 1.0, 1.0, 1.0}, // 1 turquoise
		{1.0, 1.0, 0.0, 1.0}, // 2 brown/yellow
		{0.0, 1.0, 0.0, 1.0}, // 3 green
		{0.0, 0.0, 1.0, 1.0}, // 4 blue
		{1, 0.855, 0.725, 1.0}, // 5 pink/skin
		{0.477, 0.477, 0.477, 1.0}, // 6 grey
		{1, 1, 1, 1} // 7 black
	};

void drawRectangle(GLfloat width, GLfloat height, int colour) {

	glPushMatrix();
		glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[colour]);
		glBegin(GL_POLYGON);
			glVertex2f(0, 0);
			glVertex2f(width, 0);
			glVertex2f(width, height);
			glVertex2f(0, height);
		glEnd();
	glPopMatrix();
}

void drawCuboid(GLfloat width, GLfloat height, GLfloat length, int colour) {

	if(width <= 0 || height <= 0 || length <= 0) {
		printf("Error; lengths must be greater than 0\n");
		exit(1);
	}

	glPushMatrix();
		glPushMatrix();
		// Front
			drawRectangle(width, length, colour);
		// Back
		// Rotate about the Y axis
			glRotatef(180.0, 0, 1.0, 0);
		// Move back in place
			glTranslatef(-width, 0, -height);
			drawRectangle(width, length, colour);
		glPopMatrix();

		glPushMatrix();
			glRotatef(-90, 0, 10, 0);
		// Left side
			drawRectangle(height, length, colour);
		// Right side
		// Rotate about the Z axis
			glRotatef(180.0, 0, 0, 1.0);
		// Move back in place
			glTranslatef(-height, -length, -width);
			drawRectangle(height, length, colour);
		glPopMatrix();

		glPushMatrix();
			glTranslatef(0, length, height);
			glRotatef(-90, 10, 0, 0);
		// Top
			drawRectangle(width, height, colour);
		// Bottom
		// Rotate about the Z axis
			glRotatef(180.0, 0, 0, 1.0);
		// Move back in place
			glTranslatef(-width, -height, -length);
			drawRectangle(width, height, colour);
		glPopMatrix();
	glPopMatrix();
}

void drawCylinder(GLfloat radius, GLfloat length, int slices, int colour) {

	GLUquadricObj *p = gluNewQuadric();

	glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[colour]);
	gluCylinder(p, radius, radius, length, slices, slices/10);
}

void swing_frame(void) {

	// Uprights
	glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[0]);
	glLineWidth(4.0);
	glBegin(GL_LINES);
		glVertex3f(0,-6,-1.6);
		glVertex3f(0,0,0);
		glVertex3f(0,0,0);
		glVertex3f(0,-6,1.6);
		glVertex3f(3,-6,-1.6);
		glVertex3f(3,0,0);
		glVertex3f(3,0,0);
		glVertex3f(3,-6,1.6);
	glEnd();

	// Top bar
	glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[4]);
	glLineWidth(4.0);
	glBegin(GL_LINES);
		glVertex3f(0,0,0);
		glVertex3f(3,0,0);
	glEnd();

	// Grass
	glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[3]);
	glBegin(GL_POLYGON);
		glVertex3f(-1, -6, -2.6);
		glVertex3f(-1, -6, 2.6);
		glVertex3f(4, -6, 2.6);
		glVertex3f(4, -6, -2.6);
	glEnd();
}

void rope(void) {

	glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[6]);
	glLineWidth(2.0);
	glBegin(GL_LINES);
		glVertex3f(0,0,0);
		glVertex3f(0,5,0);
	glEnd();
}

void seat_rope(void) {

	glPushMatrix();
		glPushMatrix();
			glTranslatef(0.75, -5, 0);
			rope();
		glPopMatrix();
		glPushMatrix();
			glTranslatef(0.75, -5.2, -0.5);
			drawCuboid(1.5, 1, 0.2, 2);
		glPopMatrix();
		glPushMatrix();
			glTranslatef(2.25, -5, 0);
			rope();
		glPopMatrix();
	glPopMatrix();
}

void head(void) {

	glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[5]);
	glutSolidSphere(0.4, 50, 50);
}

void lowerarm(int lr, float theta) {

	glPushMatrix();
		glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[5]);
		glutSolidSphere(0.1, 50, 50);
		glRotatef(114.5+24.5*sin(angle), 0, 1, 0);
		glRotatef(lr*5, 1, 0, 0);
		drawCylinder(0.1, 0.8, 500, 5);
		glTranslatef(0, 0, 0.8);
		glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[5]);
		glutSolidSphere(0.15, 50, 50);
	glPopMatrix();
}

void arm(int colour, int lr) {

	float theta = 45-45*sin(angle);

	if(lr != 1 && lr != -1) {
		printf("LR must be either 1 or -1\n");
		exit(1);
	}

	glPushMatrix();
		glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[colour]);
		glutSolidSphere(0.2, 50, 50);
		glRotatef(theta, 0, 1, 0);
		glRotatef(lr*5, 1, 0, 0);
		drawCylinder(0.1, 0.8, 50, 5);
		glTranslatef(0, 0, 0.8);
		lowerarm(lr, theta);
	glPopMatrix();
}

void lowerleg(void) {

	glPushMatrix();
		glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[5]);
		glutSolidSphere(0.15, 50, 50);
		drawCylinder(0.15, 1, 500, 5);
		glTranslatef(-0.1, -0.15, 1);
		glRotatef(90, 0, 10, 0);
		drawCuboid(0.16, 0.5, 0.25, 6);
	glPopMatrix();
}

void leg(int colour) {

	glPushMatrix();
		drawCylinder(0.15, 1.2, 50, colour);
		glRotatef((-45*sin(angle)+90), 0.0, 1.0, 0.0);
		lowerleg();
	glPopMatrix();
}

void girl(int colour) {

	glPushMatrix();
	// Body
		glPushMatrix();
		// Torso
			glTranslatef(1.2, -5, 0.2);
			glRotatef(-35*sin(angle), 10, 0, 0);
			drawCuboid(0.75, 0.5, 1.5, colour);
		// Arms
			glPushMatrix();
				glRotatef(-90.0, 0.0, 0.0, 1.0);
				glTranslatef(-1.4, -0.2, 0.3);
				arm(colour, 1);
				glTranslatef(0, 1.1, 0);
				arm(colour, -1);
			glPopMatrix();
		// Head
			glTranslatef(0.4, 1.8, 0.25);
			head();
		glPopMatrix();
	// Legs
		glPushMatrix();
			glRotatef(-90.0, 0.0, 0.0, 1.0);
			glTranslatef(4.9, 1.3, -0.8);
			leg(colour);
			glTranslatef(0, 0.5, 0);
			leg(colour);
		glPopMatrix();
	glPopMatrix();
}

void keyboard (unsigned char key, int x, int y) {

	switch(key) {
	// Quit: ESC
		case 27:
			exit(0);
			break;
	// Left: a
		case 97:
			scene_accel_x++;
			break;
	// Right: d
		case 100:
			scene_accel_x--;
			break;
	// Up: w
		case 119:
			scene_accel_y--;
			break;
	// Down: s
		case 115:
			scene_accel_y++;
			break;
	// Reset: BKSPACE
		case 8:
			scene_accel_y = 0;
			scene_accel_x = 0;
			break;
		default:
			printf("Key pressed %c, %d\n", key, key);
			break;
	}
}

void display(void) {

// Clear previous bits
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();

// Set camera
	gluLookAt(-5, 5, -5, 0, 0, 0, 0, 1, 0);

// Start scene hierarchy
	glPushMatrix();
	// Move scene back to 0,0,0
		glTranslatef(-2, 6, 0);
	// Rotate according to ket presses
		glRotatef(scene_accel_x*10, 0, 10, 0);
		glRotatef(scene_accel_y*10, 0, 0, 10);
		swing_frame();
		glPushMatrix();
			glRotatef(MAX_THETA * cos(angle), 3, 0, 0);
			girl(0);
			seat_rope();
		glPopMatrix();
	glPopMatrix();

// Display
	glutSwapBuffers();
}

void reshaper(int w, int h) {
// Set the viewer and scene size
	glViewport(0, 0, w, h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glOrtho(-7, 7, -7, 7, -5, 20.0);

// Goto model making mode
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

void idling(void) {

// Increase angle
	angle = angle + THETA_STEP;
	glutPostRedisplay();
}

int main(int argc, char **argv) {

// Set lighting parameters
	GLfloat light_ambient[] = {0.2, 0.2, 0.2, 1.0};
	GLfloat light_diffuse[] = {1.0, 1.0, 1.0, 1.0};
	GLfloat light_position[] = {4, 4, 4, 1};

// Create window
	glutInit(&argc, argv);
	glutInitWindowSize(800, 600);
	glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE | GLUT_DEPTH);
	glutCreateWindow("girl_on_swing");

	glEnable(GL_NORMALIZE);
	glEnable(GL_AUTO_NORMAL);

// Add light
	glLightfv(GL_LIGHT0, GL_AMBIENT, light_ambient);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
	glLightfv(GL_LIGHT0, GL_POSITION, light_position);

	glShadeModel(GL_SMOOTH);
	glEnable(GL_LIGHTING);
	glEnable(GL_LIGHT0);

// Depth enabled
	glEnable(GL_DEPTH_TEST);
	glClearDepth(1.0);

// Associate functions
	glutReshapeFunc(reshaper);
	glutIdleFunc(idling);
	glutKeyboardFunc(keyboard);

// Start
	glutDisplayFunc(display);
	glutMainLoop();
}
