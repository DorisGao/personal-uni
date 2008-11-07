/****************************************************************/
/*    Sam Black, Sam Burras and Ben Francis                     */
/*    10/02/07  optical-rs232.c   Fuse: INTRC RA6-IO, PUT       */
/*                                                              */
/*    Optical Encoder Interface                                 */
/*                                                              */
/* Device pin Layout         ___________                        */
/*                     LED3 |1   \/   18| LED2                  */
/*                     LED4 |2        17| LED1                  */
/*                          |3        16|                       */
/*                          |4        15|                       */
/*                      0V  |5        14| +5V                   */
/*                          |6        13|                       */
/*        MAX232 R2OUT pin9 |7        12|                       */
/*        MAX232 T2IN pin10 |8        11| TSL3301 SDOUT         */
/*             TSL3301 SCLK |9        10| TSL3301 SDIN          */
/*                          |___________|                       */
/*                                                              */
/****************************************************************/

#include <16F648A.H>
#device icd=true
#include <stdio.h>
#include <string.h>
#use delay(clock=4000000)
#fuses NOWDT, INTRC_IO, PUT, NOPROTECT, NOLVP, NOMCLR

// define RS232 compiler directive
#use RS232(baud=4800, parity=N, xmit=PIN_B2, rcv=PIN_B1, bits=8, errors)

// LEDs on/off
#define ON 0
#define OFF 1

// pin maps
struct pin_map {
   boolean led1;
   boolean led2;
   boolean led3;
   boolean led4;
   int unused : 4;
   boolean un1;
   boolean rx;
   boolean tx;
   boolean SCLK;
   boolean SDIN;
   boolean SDOUT;
   int un2 : 2;
};

// Create structs to map I/O
struct pin_map pins;
struct pin_map pins_dir;

// Set ports up correctly
#byte pins=0x05
#byte pins_dir=0x85

void init() {

// Control the LEDs
   pins_dir.led1 = 0;
   pins_dir.led2 = 0;
   pins_dir.led3 = 0;
   pins_dir.led4 = 0;

// control for TSL3301
   pins_dir.SCLK = 0;
   pins_dir.SDOUT = 1;
   pins_dir.SDIN = 0;

// turn LEDs off
   pins.led1 = OFF;
   pins.led2 = OFF;
   pins.led3 = OFF;
   pins.led4 = OFF;

// flash for testing
   pins.led1 = ON;
   delay_ms(500);
   pins.led2 = ON;
   delay_ms(500);
   pins.led3 = ON;
   delay_ms(500);
   pins.led4 = ON;
   delay_ms(500);
   pins.led1 = OFF;
   pins.led2 = OFF;
   pins.led3 = OFF;
   pins.led4 = OFF;
// test the serial connection
   puts("Start");
}

// send Value number of pulses to the TSL3301
void pulse_TSL_SCLK(int Value) {

   int i;

   for(i=1; i<=Value; i++) {
      pins.SCLK = 1;
      delay_us(1);
      pins.SCLK = 0;
      delay_us(1);
   }
}

// send data to TSL3301
void send_TSL(int Value) {

   int i;

   pins.SDIN = 0;                      // start bit
   pulse_TSL_SCLK(1);

   for(i=0; i<8; i++) {
      pins.SDIN = bit_test(Value, i);   // send data
      pulse_TSL_SCLK(1);
   }
   pins.SDIN = 1;                       // stop bit
   pulse_TSL_SCLK(1);
}

// reset the TSL3301
void TSL_reset() {

// clear SLCK and SDIN
   pins.SCLK = 0;
   pins.SDIN = 0;
// pulse SLCK 30 times
   pulse_TSL_SCLK(30);
// ready for writing, pulse to confirm
   pins.SDIN = 1;
   pulse_TSL_SCLK(10);
// send reset command
   send_TSL(0x1B);
   pulse_TSL_SCLK(5);
   send_TSL(0x5F);
   send_TSL(0x00);
}

// setup the TSL3301
void TSL_setup() {

   send_TSL(0x40);
   send_TSL(0x81);
   send_TSL(0x41);
   send_TSL(0x05);
   send_TSL(0x42);
   send_TSL(0x81);
   send_TSL(0x43);
   send_TSL(0x05);
   send_TSL(0x44);
   send_TSL(0x81);
   send_TSL(0x45);
   send_TSL(0x05);

}

// integration stuff
void TSL_integration(int time) {

   send_TSL(0x08);
   pulse_TSL_SCLK(22);
   delay_us(time);
   send_TSL(0x10);
   pulse_TSL_SCLK(5);

}

void TSL_pixel_readout() {

   send_TSL(0x02);
   while(pins.SDOUT == 1) {
      pulse_TSL_SCLK(1);
   }
}

void main(void) {

   int i;
   int pixels;
   int pixel_value;

// start the system
   init();

// setup the TSL3301
   TSL_reset();
   puts("Reset done");
   TSL_setup();
   puts("Setup done");

   while(true) {
      TSL_integration(5);
      TSL_pixel_readout();
      puts("Start");

      for(pixels = 102; pixels > 0; pixels--) {

         pulse_TSL_SCLK(1);

         pixel_value = 0;

         for(i=0; i<8; i++) {

            if(pins.SDOUT == 1)
               bit_set(pixel_value, i);
            else
               bit_clear(pixel_value, i);

            pulse_TSL_SCLK(1);

         }

         pulse_TSL_SCLK(1);

         printf("%u\n", pixel_value);
      }
   // delay to allow MATLAB to catch up
      delay_ms(500);
   }

}
