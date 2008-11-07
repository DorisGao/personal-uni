/*
 *      rotating_cube.c : This creates a rotating 3D cube
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
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *      MA 02110-1301, USA.
 */

/* To get gcc to compile OpenGL correctly, you must include the following in the
 * gcc options;
 *
 *   gcc rotating_cube.c -o rotating_cube -lX11 -lXi -lXmu -lglut -lGL -lGLU
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <GL/glut.h>

#define THETA_STEP 0.2
GLfloat theta = 0.0;

GLfloat vertices[][3] =
	{{-1.0, -1.0, -1.0}, {-1.0, -1.0, 1.0}, {-1.0, 1.0, -1.0},
	{-1.0, 1.0, 1.0}, {1.0, -1.0, -1.0}, {1.0, -1.0, 1.0},
	{1.0, 1.0, -1.0}, {1.0, 1.0, 1.0}};

GLfloat colours[][4] =
	{{1.0, 0.0, 0.0, 1.0}, {0.0, 1.0, 1.0, 1.0}, {1.0, 1.0, 0.0, 1.0},
	{0.0, 1.0, 0.0, 1.0}, {0.0, 0.0, 1.0, 1.0}, {1.0, 0.0, 1.0, 1.0}};

GLfloat light_ambient[] = {0.2, 0.2, 0.2, 1.0};
GLfloat light_diffuse[] = {1.0, 1.0, 1.0, 1.0};
//GLfloat light_specular[] = {1.0, 1.0, 1.0, 1.0};
GLfloat light_position[] = {2.0, 2.0, 2.0, 1.0};

void polygon(float colours[][4], int col, float vertices[][3], int a, int b, int c, int d) {
	// Set colour with respect of lighting
	glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, colours[col]);
	//glMaterialfv(GL_FRONT, GL_SPECULAR, colours[col]);

	// The face of the cube
	glBegin(GL_POLYGON);
		glVertex3fv(vertices[a]);
		glVertex3fv(vertices[b]);
		glVertex3fv(vertices[c]);
		glVertex3fv(vertices[d]);
	glEnd();
}

void cube() {
	polygon(colours,0,vertices,3,7,6,2); // top
	polygon(colours,2,vertices,0,4,5,1); // bottom
	polygon(colours,1,vertices,5,4,6,7); // side 1
	polygon(colours,4,vertices,4,0,2,6); // side 2
	polygon(colours,3,vertices,0,1,3,2); // side 3
	polygon(colours,5,vertices,1,5,7,3); // side 4
}

void display(void) {

	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	//glClearColor(0.4, 0.1, 0.5, 1.0);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();

	gluLookAt(1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

	glPushMatrix();
		glRotatef(theta, 0.0, 0.5, 0.0);
		//glRotatef(theta, -0.2, 0.5, 0.2);
		//glRotatef(theta, 0.0, -0.2, 0.8);
		cube();
	glPopMatrix();

	glutSwapBuffers();
}

void reshaper(int w, int h) {
	glViewport(0, 0, w, h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glOrtho(-5.0, 5.0, -5.0, 5.0, -5.0, 5.0);

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

void idling(void) {

	theta = theta + THETA_STEP;
	if(theta == 360.0) theta = 0.0;
	//sleep(1);
	glutPostRedisplay();
}

int main(int argc, char **argv) {

	glutInit(&argc, argv);
	glutInitWindowSize(800, 600);
	glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE | GLUT_DEPTH);
	glutCreateWindow("rotating_cube");

	glEnable(GL_NORMALIZE);
	glEnable(GL_AUTO_NORMAL);

	glShadeModel(GL_SMOOTH);
	glEnable(GL_LIGHTING);
	glEnable(GL_LIGHT0);
	glLightfv(GL_LIGHT0, GL_AMBIENT, light_ambient);
	//glLightfv(GL_LIGHT0, GL_SPECULAR, light_specular);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
	glLightfv(GL_LIGHT0, GL_POSITION, light_position);

	glEnable(GL_DEPTH_TEST);
	glClearDepth(1.0);
	glutReshapeFunc(reshaper);
	glutIdleFunc(idling);

	glutDisplayFunc(display);
	glutMainLoop();
}
