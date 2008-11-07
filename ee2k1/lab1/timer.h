#include <windows.h>

static LARGE_INTEGER __timer_begin;
static LARGE_INTEGER __timer_end;

void timer_start()
{
	// query the counter
	QueryPerformanceCounter(&__timer_begin);
}

void timer_stop()
{
	// query the counter
	QueryPerformanceCounter(&__timer_end);
}

double timer_seconds()
{
	LARGE_INTEGER ticks_per_sec;
	__int64 diff;
	double f_freq, f_time;

	QueryPerformanceFrequency(&ticks_per_sec);

	// calculate the elapsed time
	diff = __timer_end.QuadPart - __timer_begin.QuadPart;
	// divide by frequency to get seconds
	f_freq = (double) ticks_per_sec.QuadPart;
	f_time = (double) diff;

	return f_time / f_freq;
}

double timer_milliseconds()
{ 
	return timer_seconds() * 1000.0;
}

double timer_microseconds()
{
	return timer_milliseconds() * 1000.0;
}

double timer_nanoseconds()
{
	 return timer_microseconds() * 1000.0;
}
