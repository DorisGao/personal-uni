#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// size of buffer
#define RS232BUFFSIZE 32
// LEDs set
#define ON 0
#define OFF 1
// FIFO input buffer
char RS232_buffer[RS232BUFFSIZE];
// Start and end pointers for buffer
int head=0, tail=0;
// buffer full flag
int buffer_full = 0;
// command waiting to be executed flag
int command_waiting = 0;

// pin maps
struct serial_port_pin_map {
   int led1;
   int led2;
   int led3;
   int led4;
};

struct serial_port_pin_map serial_port;

// add data to the buffer from the RS232
int add_to_buffer(char c) {

   if(!buffer_full) {
   // add char to the buffer
      RS232_buffer[head] = c;
   // is it a command??
      if(RS232_buffer[head] == 0x0D)
         command_waiting = 1;
   // increment the head counter
      head = (head + 1) % RS232BUFFSIZE;
   // run buffer full check
      if(head == tail)
         buffer_full = 1; // buffer full
      else
         buffer_full = 0;
   // successfully added character to buffer
      return 1;
   }
   else
   // the buffer is full and we can't add to it
      return 0;
}

// remove character from buffer to PIC program
char remove_from_buffer() {

   char c;
   // run buffer empty check
   if(buffer_full == 0 && head == tail)
      return 0; // buffer empty
   else {
   // read a character from the buffer
      c = RS232_buffer[tail];
   // tail chases the head
      tail = (tail + 1) % RS232BUFFSIZE;
   // the buffer isn't full any more
      buffer_full = 0;
   // return the character
      return c;
   }
}

// command parser and implementer
void command_pi() {
	
	char command[RS232BUFFSIZE];
	char c='Z';
	int i=0;
// remove leading white space
	while(c != 0 && c == 0x20) {
   		c = remove_from_buffer();
    }
	
// copy command from buffer until
// we hit the carriage return
	while (buffer_full !=0 || head != tail) 
	{
		command[i] = remove_from_buffer();
		i++;
	}
// add the terminating character so the string comparison
// below works
	command[i] = '\0';

// actually implement the command now
// look ben, lovely if...if else... statements :-P
	if(!(strcmp(command, "help")))
		printf("Help called");
	else if(!(strcmp(command, "about")))
		printf("This is an RS232 to PIC interface program");
	else if(!(strcmp(command, "turn led 1 on"))) {
		if(serial_port.led1 == ON)
			printf("Error: LED 1 is already ON");
		else {
			serial_port.led1 = ON;
			printf("OK: LED 1 is now ON");
		}
	}
	else if(!(strcmp(command, "turn led 2 on"))) {
		if(serial_port.led2 == ON)
			printf("Error: LED 2 is already ON");
		else {
			serial_port.led2 = ON;
			printf("OK: LED 2 is now ON");
		}
	}
	else if(!(strcmp(command, "turn led 3 on"))) {
		if(serial_port.led3 == ON)
			printf("Error: LED 3 is already ON");
		else {
			serial_port.led3 = ON;
			printf("OK: LED 3 is now ON");
		}
	}
	else if(!(strcmp(command, "turn led 4 on"))) {
		if(serial_port.led4 == ON)
			printf("Error: LED 4 is already ON");
		else {
			serial_port.led4 = ON;
			printf("OK: LED 4 is now ON");
		}
	}
	else if(!(strcmp(command, "turn led 1 off"))) {
		if(serial_port.led1 == OFF)
			printf("Error: LED 1 is already OFF");
		else {
			serial_port.led1 = OFF;
			printf("OK: LED 1 is now OFF");
		}
	}
	else if(!(strcmp(command, "turn led 2 off"))) {
		if(serial_port.led2 == OFF)
			printf("Error: LED 2 is already OFF");
		else {
			serial_port.led2 = OFF;
			printf("OK: LED 2 is now OFF");
		}
	}
	else if(!(strcmp(command, "turn led 3 off"))) {
		if(serial_port.led3 == OFF)
			printf("Error: LED 3 is already ON");
		else {
			serial_port.led3 = OFF;
			printf("OK: LED 3 is now OFF");
		}
	}
	else if(!(strcmp(command, "turn led 4 off"))) {
		if(serial_port.led4 == OFF)
			printf("Error: LED 4 is already OFF");
		else {
			serial_port.led4 = OFF;
			printf("OK: LED 4 is now OFF");
		}
	}
	else
		printf("What? Are you fahrbot?");
	
// reset the command waiting flag
	command_waiting = 0;
	
}

int main(void) {
	
	char input[RS232BUFFSIZE];
	int i;
	
	// turn LEDs off
	serial_port.led1 = OFF;
	serial_port.led2 = OFF;
	serial_port.led3 = OFF;
	serial_port.led4 = OFF;

	while(1) {
		printf("Please input a string less than 32 chars\n");
		printf("Input: ");

		scanf(" %[^\012]", input);
		for (i = 0; i < strlen(input); i++)
			add_to_buffer(input[i]);
		
		command_pi();
		printf("\n");
	}
	return 0;
}
