#include <stdio.h>
#include "timer.h"

// Iterative

int logtwo(float N)
{
	int count=0, temp=2;

	while((float)temp<=N)
	{
		temp=temp*2;
		count++;
	}

	return count;
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
