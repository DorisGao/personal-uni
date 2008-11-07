#include <stdio.h>
#define _USE_MATH_DEFINES
#include <math.h>
#include "timer.h"

// using math.h

int logtwo(float N)
{
	return log(N)/M_LN2;
}

int main(void)
{

	float N;
	int result;
	
	printf("Enter a number greater than 1: ");
	scanf("%f", &N);

	if(N>1)
	{
		timer_start();
		result = logtwo(N);
		timer_stop();
	}
	else
		printf("Please enter a number greater than 1\n");

	printf("Result is %d\n", result);
	printf("Time is %f\n\n", timer_microseconds());

	return 0;
}
