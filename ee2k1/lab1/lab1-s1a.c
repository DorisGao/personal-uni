#include <stdio.h>
#include "timer.h"

// Recursive


float logtwo(float N)
{
    float x;

	if(N>=2)
		x = logtwo(N/2) + 1;
	else if(N<2 && N>=1)
		x=0;

    return x;
}


int main(void)
{
	float N, result;
	
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

	printf("Result is %0.f\n", result);
	printf("Time is %f\n\n", timer_microseconds());

	return 0;
}
