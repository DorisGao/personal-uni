#include <16F648A.h>
#use delay(clock=4000000)
#fuses NOWDT, INTRC_IO, NOPUT, NOPROTECT, NOLVP, NOMCLR

void main()
{
   struct input_pin_map {
      boolean anticlockwise;
      boolean clockwise;
      boolean speed1;
      boolean speed2;
      int unused: 4;
   };

   struct output_pin_map {
      int motor1: 4;
      int unused: 4;

   };

   int speed=500, i=0;
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
      0x01, 0x09, 0x0A, 0x02, 0x06, 0x04, 0x05
   };


   #byte buttons=0x05         // assign buttons to port a
   #byte motor=0x06           // assign motor to port b
   #byte buttons_setup=0x85   // assign buttons_setup for port a TRIS
   #byte motor_setup=0x86     // assign motor_setup for port b TRIS

   motor_setup.motor1=0;            // set as Output
   buttons_setup.anticlockwise=1;   // set as Input
   buttons_setup.clockwise=1;       // set as Input
   buttons_setup.speed1=1;           // set as Input
   buttons_setup.speed2=1;           // set as Input


   setup_timer_0(RTCC_INTERNAL|RTCC_DIV_1);

   while (1) {                              // REPEAT indefinitely

      //determine if a button has been pressed
      if((buttons.anticlockwise==FALSE || buttons.clockwise==FALSE) && !(buttons.anticlockwise==FALSE && buttons.clockwise==FALSE)) {
         
         if(buttons.speed1==FALSE && buttons.speed2==FALSE)
            speed=40;
         else if(buttons.speed1==FALSE && buttons.speed2=TRUE)
            speed=20;
         else if(buttons.speed1==TRUE && buttons.speed2==FALSE)
            speed=10;
         else if(buttons.speed1==TRUE && buttons.speed2==TRUE)
            speed=5;
         else
            speed=250;

         // determine which button was pressed
         if(buttons.clockwise==FALSE)
            direction=1;
         else if (buttons.anticlockwise==FALSE)
            direction=0;

         for(i=0;i<100;i++)   {              // REPEAT 100 times
            if(direction==1) {               // IF button 'C' was pressed THEN
               //rotate the motor shaft clockwise
               motor.motor1 = HALF_LOOKUP_TABLE[i%4];
            }
            else if(direction==0) {          // OTHERWISE
               //rotate the motor shaft anti-clockwise
               motor.motor1 = HALF_LOOKUP_TABLE[3-(i%4)];
            }

            Delay_ms(speed);              //insert time delay of one step period
         }
     }
     else
      motor.motor1 = 0x00;

   }
}

