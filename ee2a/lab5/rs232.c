/**********************************************************************/
/*    Sam Black, Sam Burras and Ben Francis                           */
/*    10/02/07  rs232.c   Fuse: INTRC RA6-IO, PUT                     */
/*                                                                    */
/*    RS232 controller for the 16F648A PIC                            */
/*                                                                    */
/* Device pin Layout         ___________                              */
/*                     LED3 |1   \/   18| LED2                        */
/*                     LED4 |2        17| LED1                        */
/*                          |3        16|                             */
/*                          |4        15|                             */
/*                      0V  |5        14| +5V                         */
/*                          |6        13|                             */
/*        MAX232 R2OUT pin9 |7        12|                             */
/*        MAX232 T2IN pin10 |8        11|                             */
/*                          |9        10|                             */
/*                          |___________|                             */
/*                                                                    */
/**********************************************************************/

#include <16F648A.H>
#device icd=true
#include <stdio.h>
#include <string.h>
#use delay(clock=4000000)
#fuses NOWDT, INTRC_IO, PUT, NOPROTECT, NOLVP, NOMCLR

// define RS232 compiler directive
#use RS232(baud=2400, parity=N, xmit=PIN_B2, rcv=PIN_B1, bits=8, errors)

// size of buffer
#define RS232BUFFSIZE 32
// LEDs set
// LEDs are active LOW
#define ON 0
#define OFF 1
// FIFO input buffer
char RS232_buffer[RS232BUFFSIZE];
// Start and end pointers for buffer
int head=0, tail=0;
// buffer full/empty flags
boolean buffer_full = FALSE;
boolean buffer_empty = TRUE;
// command waiting to be executed flag
boolean command_waiting = FALSE;

// pin maps
struct serial_port_pin_map {
   boolean led1;
   boolean led2;
   boolean led3;
   boolean led4;
   int unused : 4;
};

// Create structs to map I/O
struct serial_port_pin_map serial_port;
struct serial_port_pin_map serial_port_dir;

// Set ports up correctly
#byte serial_port=0x05
#byte serial_port_dir=0x85

// this is the interrupt routine
#INT_RDA
// add data to the buffer from the RS232
boolean add_to_buffer() {

   if(!buffer_full) {
   // add char to the buffer
      RS232_buffer[head] = getc();
   // is it a command??
      if(RS232_buffer[head] == 0x0D)
         command_waiting = TRUE;
   // increment the head counter
      head = (head + 1) % RS232BUFFSIZE;
   // run buffer full check
      if(head == tail) {
         buffer_full = TRUE; // buffer full
         buffer_empty = FALSE; // buffer can't be empty!!
      }
      else {
         buffer_full = FALSE;
         buffer_empty = FALSE;
      }
   // successfully added character to buffer
      return TRUE;
   }
   else
   // the buffer is full and we can't add to it
      return FALSE;
}

// remove character from buffer to PIC program
char remove_from_buffer() {

   char c;

   if(!buffer_empty) {
   // remove char from the buffer
      c = RS232_buffer[tail];
   // increment the tail counter
      tail = (tail + 1) % RS232BUFFSIZE;
   // run buffer empty check
      if(head == tail) {
         buffer_full = FALSE; // buffer can't be full
         buffer_empty = TRUE; // buffer empty
      }
      else {
         buffer_full = FALSE;
         buffer_empty = FALSE;
      }
   // successfully added character to buffer
      return c;
   }
   else
   // the buffer is empty and we can't remove from it
      return FALSE;
}

int cmd_compare(char string[]) {

   int i=0,j=0,k=0;
   char c;
   char tmp[RS232BUFFSIZE] = {""};
   boolean found = FALSE;
   char cmds[6][5] = {
      {"on"},
      {"off"},
      {"led1"},
      {"led2"},
      {"led3"},
      {"led4"}
   };
// remove leading white space and drop everything to lower case
   strlwr(string);

   do {
      c = string[i];
      i++;
   } while(c == ' ');

   for(i=(i-1); i<strlen(string); i++) {
   // terminate string and exit if we hit white space, null or a CR
      if(string[i] == ' ' || string[i] == '\0' || string[i] == 0x0D) {
         tmp[j] = '\0';
         break;
      }
   // else copy chars across
      else {
         tmp[j] = string[i];
         j++;
      }
   }

// run the parser
   for(i=0; i<7; i++){
      for(j=0; j<4; j++) {
         if(cmds[i][j] == tmp[j]) {
            found = TRUE;
         }
         else if(cmds[i][j] != tmp[j]) {
         // theres a non sequential letter in here
         // break out! reach out!
            found = FALSE;
            break;
         }
         else if(tmp[j] == '\0') {
         // we've reached the end of the string
            break;
         }
      }
      if(found) {
         return i;
      }
   }

// we should only get here if there is no comparison
   return 255;
}


// command parser and implementer
void command_pi() {

	char command[RS232BUFFSIZE]={" "};
	char c='Z';
	int i=0, j;

// copy command from buffer until we hit the carriage return
	while(command[i] != 0x0D)
	{
      if(buffer_empty != TRUE) {
	      command[i] = remove_from_buffer();
         delay_ms(1);
   		i++;
      }
      else
         break;
	}

// add the terminating character so the string comparison
// below works
	command[i] = '\0';

// actually implement the command now

   switch(cmd_compare(command)) {
      case 2:
      // LED 1
         if(cmd_compare(strchr(command, ' ')) == ON && serial_port.led1 == OFF) {
            serial_port.led1 = ON;
            puts("OK: LED 1 is now ON");
         }
         else if(cmd_compare(strchr(command, ' ')) == OFF && serial_port.led1 == ON){
            serial_port.led1 = OFF;
            puts("OK: LED 1 is now OFF");
         }
         else {
            puts("Error with LED1");
         }

         break;
      case 3:
      // LED 2
         if(cmd_compare(strchr(command, ' ')) == ON && serial_port.led2 == OFF) {
            serial_port.led2 = ON;
            puts("OK: LED 2 is now ON");
         }
         else if(cmd_compare(strchr(command, ' ')) == OFF && serial_port.led2 == ON){
            serial_port.led2 = OFF;
            puts("OK: LED 2 is now OFF");
         }
         else {
            puts("Error with LED2");
         }

         break;
      case 4:
      // LED 3
         if(cmd_compare(strchr(command, ' ')) == ON && serial_port.led3 == OFF) {
            serial_port.led3 = ON;
            puts("OK: LED 3 is now ON");
         }
         else if(cmd_compare(strchr(command, ' ')) == OFF && serial_port.led3 == ON){
            serial_port.led3 = OFF;
            puts("OK: LED 3 is now OFF");
         }
         else {
            puts("Error with LED3");
         }

         break;
      case 5:
      // LED 4
         if(cmd_compare(strchr(command, ' ')) == ON && serial_port.led4 == OFF) {
            serial_port.led4 = ON;
            puts("OK: LED 4 is now ON");
         }
         else if(cmd_compare(strchr(command, ' ')) == OFF && serial_port.led4 == ON){
            serial_port.led4 = OFF;
            puts("OK: LED 4 is now OFF");
         }
         else {
            puts("Error with LED4");
         }

         break;

      default:
         puts("Commands:");
         puts(" help");
         puts(" led# on / off");
         puts("  where # is a number between 1 and 4");
         break;
      }

// reset the command waiting flag
	command_waiting = FALSE;
// ask for next command
	puts("\nPlease input a command: ");
}

void init() {

// Control the LEDs
   serial_port_dir.led1 = 0;
   serial_port_dir.led2 = 0;
   serial_port_dir.led3 = 0;
   serial_port_dir.led4 = 0;

// turn LEDs off
   serial_port.led1 = OFF;
   serial_port.led2 = OFF;
   serial_port.led3 = OFF;
   serial_port.led4 = OFF;

// flash for testing
   serial_port.led1 = ON;
   delay_ms(500);
   serial_port.led2 = ON;
   delay_ms(500);
   serial_port.led3 = ON;
   delay_ms(500);
   serial_port.led4 = ON;
   delay_ms(500);
   serial_port.led1 = OFF;
   serial_port.led2 = OFF;
   serial_port.led3 = OFF;
   serial_port.led4 = OFF;
// test the serial connection
   puts("\tHello computer!");
   puts("\nType 'help' for more information");
   puts("\nPlease input a command:");

}

void main(void) {

// enable interrupts
   enable_interrupts(INT_RDA);
   enable_interrupts(GLOBAL);

// start the system
   init();

   while(TRUE) {

      if(command_waiting == TRUE)
         command_pi();
   }

}
