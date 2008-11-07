/**********************************************************************/
/*    Sam Black, Sam Burras and Ben Francis                           */
/*    18/01/07  stepper_motor_half.c   Fuse: INTRC RA6-IO             */
/*                                                                    */
/*    Stepper motor controller for the 16F648A.h PIC                  */
/*    Using half step with variable speed                             */
/*                                                                    */
/* Device pin Layout         ___________                              */
/*                Speed MSB |1   \/   18| Input clockwise             */
/*                Speed LSB |2        17| Input anticlockwise         */
/*                          |3        16|                             */
/*                          |4        15|                             */
/*                      V++ |5        14| 0V                          */
/*              Motor pin 1 |6        13|                             */
/*              Motor pin 2 |7        12|                             */
/*              Motor pin 3 |8        11|                             */
/*              Motor pin 4 |9        10|                             */
/*                          |___________|                             */
/*                                                                    */
/**********************************************************************/

#include <16F648A.h>
#use delay(clock=4000000)
#fuses NOWDT, INTRC_IO, NOPUT, NOPROTECT, NOLVP, NOMCLR

void main()
{
   /* Input pin map */
   struct input_pin_map {
      boolean anticlockwise;
      boolean clockwise;
      int speed: 2;
      int unused: 4;
   };

   /* Output pin map */
   struct output_pin_map {
      int motor1: 4;
      int unused: 4;

   };

   int speed=0;   //delay in ms
   int i=0;
   boolean direction=0;
   struct input_pin_map buttons;
   struct input_pin_map buttons_setup;
   struct output_pin_map motor;
   struct output_pin_map motor_setup;

   // lookup table
   int const FULL_LOOKUP_TABLE[4] = {
      0x07, 0x06, 0x08, 0x09
   };

   int const HALF_LOOKUP_TABLE[8] = {
      0x08, 0x0C, 0x04, 0x06, 0x02, 0x03, 0x01, 0x09
   };

   #byte buttons=0x05         // assign buttons to port a
   #byte motor=0x06           // assign motor to port b
   #byte buttons_setup=0x85   // assign buttons_setup for port a TRIS
   #byte motor_setup=0x86     // assign motor_setup for port b TRIS

   motor_setup.motor1=0;            // set as Output
   buttons_setup.anticlockwise=1;   // set as Input
   buttons_setup.clockwise=1;       // set as Input
   buttons_setup.speed=1;           // set as Input


   setup_timer_0(RTCC_INTERNAL|RTCC_DIV_1);

   while (1) {                              // REPEAT indefinitely

      //determine if a button has been pressed
      if((buttons.anticlockwise==FALSE || buttons.clockwise==FALSE) && !(buttons.anticlockwise==FALSE && buttons.clockwise==FALSE)) {

         // read speed setting from PORT A pins 2 & 3. Pin 2 is the MSB.
         switch(buttons.speed) {
            case 0x00:
               speed = 16;
               break;
            case 0x01:
               speed = 8;
               break;
            case 0x02:
               speed = 4;
               break;
            case 0x03:
               speed = 2;
               break;
            default:
               speed = 200;
               break;
         }


         // determine which button was pressed
         if(buttons.clockwise==FALSE)
            direction=1;
         else if (buttons.anticlockwise==FALSE)
            direction=0;

         for(i=0;i<200;i++) {                // REPEAT 100 times
            if(direction==1) {               // IF button 'C' was pressed THEN
               //rotate the motor shaft clockwise
               motor.motor1 = HALF_LOOKUP_TABLE[i%8];
            }
            else if(direction==0) {          // OTHERWISE
               //rotate the motor shaft anti-clockwise
               motor.motor1 = HALF_LOOKUP_TABLE[7-(i%8)];
            }

            delay_ms(speed);              //insert time delay of one step period
         }
     }
     else
     /* Don't move the rotor */
        motor.motor1 = 0x00;

   }
}

