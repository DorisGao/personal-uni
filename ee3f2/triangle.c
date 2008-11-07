/*
 *      triangle.c : This creates a 2D Sierpinski Gasket
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
 *   gcc triangle.c -o triangle -lX11 -lXi -lXmu -lglut -lGL -lGLU
 */

#define MAX_COUNT_STEPS 100000

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <GL/glut.h>

int check_point(float x, float y) {

/* this returns radians */
	if(x < 0.5 && x > 0) {
		if(atanf(y / x) < 0.333) {
			return 0;
		}
		else {
			return 1;
		}
	}
	else if(x > 0.5 && x < 1) {
		if(atanf(y / (1 - x)) < 0.333) {
			return 0;
		}
		else {
			return 1;
		}
	}
	else if(x == 0.5) {
		if(y < 1 && y > 0) {
			return 0;
		}
		else {
			return 1;
		}
	}
	else {
		return 1;
	}
}

int random_point() {

	/* Continue going round and round till we get a sane number */
	while (1) {

		if(rand() % 7 == 0) {
			return 0;
		}
		else if(rand() % 3 == 0) {
			return 1;
		}
		else if(rand() % 5 == 0) {
			return 2;
		}
	}
}


void display() {

	/* if i is 0 or 1, modulus breaks */
	int i = 2;
	float p[2] = {0, 0};
	float m[2] = {0, 0};

	glClear(GL_COLOR_BUFFER_BIT);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluOrtho2D(-0.2, 1.2, -0.2, 1.2);

	glBegin(GL_POLYGON);
	/* Must set colour before each vertex, not before polygon */
	/* Maybe for the whole polygon? */
	/* Doesn't like glColor3i : use glColor3f instead */
		glColor3f(0.0, 1.0, 0.0);
		glVertex2f(0.0, 0.0);
		glVertex2f(1.0, 0.0);
		glVertex2f(0.5, 1.0);
	/* Pretty green triangle!! */
	glEnd();

	/* Generate one sane random place in the triangle */
	do {
		/* This +1 removes divide by 0 errors */
		p[0] = (float) 1 / ((rand() % i) + 1);
		p[1] = (float) 1 / ((rand() % i) + 1);

		i++;
	} while (check_point(p[0], p[1]) > 0);

	/* This becomes the start, from which we branch */
	i = 0;
	printf("final: %f, %f\n", p[0], p[1]);
	while(i < MAX_COUNT_STEPS) {

		switch(random_point()) {
			case 0:
			/* Point 0.0,0.0 */
				m[0] = p[0] / 2;
				m[1] = p[1] / 2;
				break;
			case 1:
			/* Point 0.5,1.0 */
				m[0] = ((0.5 - p[0]) / 2) + p[0];
				m[1] = ((1.0 - p[1]) / 2) + p[1];
				break;
			case 2:
			/* Point 1.0,0.0 */
				m[0] = ((1.0 - p[0]) / 2) + p[0];
				m[1] = p[1] / 2;
				break;
			default:
				printf("\nMassive error occurred, exiting\n");
				exit(1);
		}

		/* Fix typo */
		glBegin(GL_POINTS);
			glColor3f(1.0, 0.0, 0.0);
			glVertex2f(m[0], m[1]);
		glEnd();

		p[0] = m[0];
		p[1] = m[1];

		i++;
	}


	glFlush();
}

int main(int argc, char **argv) {

	/* Init random number generator */
	srand( (unsigned int)time( NULL ) );

	glutInit(&argc, argv);
	glutInitWindowSize(640, 480);
	glutInitDisplayMode(GLUT_RGB);
	glutCreateWindow("triangle");
	glutDisplayFunc(display);
	glutMainLoop();
}
