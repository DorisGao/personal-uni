/* Quicksort. */
#include <stdio.h>
#include <stdlib.h>
#include "timer.h"

int printarray(int a[], int size)
{
	int i;
	for(i=0;i<size;i++)
		printf(" %d ",a[i]);
	printf("\n\n");
	return 0;
}

int quicksort(int a[], int l, int r)
{
	int v,i,j,t;

	if (r>l)
	{

		v=a[r]; 
		i=l-1; 
		j=r; /* Choose right hand value as pivot */

		for(;;)
		{
			/* Moves from left to right through array until a value greater than pivot is found */
			while (a[++i]<v); 
			/* Moves from right to left through array until a value less than pivot is found */
			while (a[--j]>v);
			/* Breaks out of for loop when pointers cross */
			if (i>=j) break; 

			t=a[i]; 
			a[i]=a[j]; 
			a[j]=t;

			/* Swaps the first element from the left which is greater than the pivot value, with the first
			element from the right which is less than the pivot value. */
		}

		t=a[i]; a[i]=a[r]; a[r]=t; 
		/* Swaps the first element where the pointers crossed, with the pivot, thus
		putting the pivot element into the correct place! */
		quicksort(a,l,i-1);
		quicksort(a,i+1,r);
		return 0;
	}

	else 
		return 0;
}

int bubblesort(int a[], int l, int r)
{
	int i, j, temp;

	for(i=l; i<=r-1; i++)
	{
		for(j=l; j<=r-1-i; j++)
		{
			if(a[j + 1]<a[j])
			{
				/* swap */
				temp = a[j];
				a[j] = a[j + 1];
				a[j + 1] = temp;
			}
		}
	}

	return 0;
}

int hybridsort(int a[], int l, int r)
{
	int v,i,j,t, c, b;

	//if ((r-l)>25)
	if(r>l)
	{
		/* median method */
		//for(c=r-(r/10); c<=r; c++)
		//{
		//	b=a[c];
		//	if(b>a[c+1])
		//		r=c;
		//}

		
		v=a[r];
		i=l-1;
		j=r;

		for(;;)
		{
			while (a[++i]<v); 
			while (a[--j]>v);
			if (i>=j) break; 

			t=a[i]; 
			a[i]=a[j]; 
			a[j]=t;
		}

		t=a[i];
		a[i]=a[r];
		a[r]=t;

		hybridsort(a,l,i-1);
		hybridsort(a,i+1,r);
		return 0;
	}

	else {
		//bubblesort(a, l, r);
		return 0;
	}
}

int main(void)
{
	int* base;
	int* quick;
	int* bubble;
	int* hybrid;
	int i, array_size;

	printf("Please enter the number of items you wish to have in the array: ");
	scanf("%d", &array_size);

	base = malloc(array_size*sizeof(int));
	quick = malloc(array_size*sizeof(int));
	bubble = malloc(array_size*sizeof(int));
	hybrid = malloc(array_size*sizeof(int));

	srand(rand());
	
	for(i=0; i<array_size; i++)
	{
		base[i]=rand();
	};
	
	for(i=0; i<array_size; i++)
	{
		quick[i] = base[i];
		bubble[i] = base[i];
		hybrid[i] = base[i];
	}

	timer_start();
	hybridsort(hybrid, 0, array_size-1);
	timer_stop();
	printf("Time for hybridsort is %f\n\n", timer_microseconds());
	printarray(hybrid, array_size);

	timer_start();
	quicksort(quick,0,array_size-1);
	timer_stop();
	printf("Time for quicksort is %f\n\n", timer_microseconds());
	printarray(quick, array_size);

	timer_start();
	bubblesort(bubble, 0, array_size-1);
	timer_stop();
	printf("Time for bubblesort is %f\n\n", timer_microseconds());
	printarray(bubble, array_size);


	system("PAUSE");
	return 0;
}