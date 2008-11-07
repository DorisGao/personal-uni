/*
 * Authors: Sam Black, Sam Burras, Ben Francis
 *
 * Version 1.0  Date: 07/02/07
 *
 * Program to send strings to the LCD using
 * a 4-bit driver
 *
 */

#include <16F648A.h>
#include <string.h>
#use delay(clock=4000000)
#fuses NOWDT, INTRC_IO, NOPUT, NOPROTECT, NOLVP, NOMCLR
#include "LCD_4BIT.H"

char INITMESSAGE[16] = "How can I help?";
char MESSAGE1[16] = "The answer?";
char MESSAGE2[16] = "Forty Two.";
char MESSAGE3[16] = "  ?noitseuQ ehT";

void send_string(int delay, char string[])
{
   int i;

/* send the string */
   for(i=0; string[i] != '\0'; i++)
   {
      lcd_putc(string[i]);
      delay_ms(delay);
   }
}

void send_string_reverse(int delay, char string[])
{
   int i;

/* send the string */
   for(i=14; i>=0; i--)
   {
      lcd_putc(string[i]);
      delay_ms(delay);
   }
}

int main(void)
{
/* Init the Display */
   lcd_init(2);
/* Clear the screen */
   lcd_putc('\f');
/* Send the message */
   send_string(5, INITMESSAGE);

/* Display on the first line after button press */
   while(lcd.button == 1)
   { }
   lcd_putc('\f');
   send_string(1000, MESSAGE1);

   while(lcd.button == 1)
   { }
   lcd_putc('\f');
   /* New line */
   lcd_putc('\n');
   send_string(5, MESSAGE2);
   /* Add delay for button debouncing */
   delay_ms(500);

   /*wait for button press*/
   while(lcd.button == 1)
   { }
   /* Re-initialise the display to scroll */
   lcd_init(3);

   while(1) {
     send_string(200, MESSAGE3);
   }

   return 0;
}
