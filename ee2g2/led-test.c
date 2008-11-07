#include <16f648a.h>
#fuses INTRC, PUT, BROWNOUT, NOPROTECT, NOMCLR, NOLVP, NOCPD, NOWDT
/* Fuses set for operation with few external components.
   Fuses: Internal clock, resets on brownout and power-up only, wait
          at powerup for the voltage to stabilise, no code/data protection,
          high voltage programming */
          
#use delay(clock=4000000)

int main(void)
{
  while(1)
  {
    output_toggle(PIN_A0);
    delay_ms(500);
  }
}
