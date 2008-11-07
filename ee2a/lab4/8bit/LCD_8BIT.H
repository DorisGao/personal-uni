/* LCD 8-bit driver
 *
 * Authors: Sam Black, Sam Burras, Ben Francis
 *
 * Derived from Custom Computer Services
 * (C) Copyright 1996, 2003 LCD.C
 *
 * Version 1.0  Date: 07/02/07
 *
 */

struct lcd_pin_map {
   boolean rs;
   boolean rw;
   boolean enable;
   boolean button;
   int unused : 4;
   int data : 8;
};

/* Create structs to map I/O */
struct lcd_pin_map lcd;
struct lcd_pin_map lcd_dir;

/* Set ports up correctly */
#byte lcd=0x05
#byte lcd_dir=0x85

/* Send initialization sequence */
/* No cursor */
byte CONST lcd_init_nocursor[4] = {0x3C, 0x0C, 0x01, 0x06};
/* Cursor */
byte CONST lcd_init_cursor[4] = {0x3C, 0x0E, 0x01, 0x06};

/* Set direction of pins to send chars to LCD */
struct lcd_pin_map CONST lcd_write = {0, 0, 0, 1, 0, 0x00};

/* Set direction of pins to read status from LCD */
struct lcd_pin_map CONST lcd_read = {1, 0, 0, 1, 0, 0xFF};

/* Based on lcd.c */
void lcd_send_byte( byte address, byte n ) {

/* Set port direction */
      lcd_dir = lcd_write;
/* Set what we are writing to */
/* 0 is Instruction */
/* 1 is Data */
      lcd.rs = address;
/* Write to LCD */
      lcd.rw = 0;
      delay_us(1);
      lcd.enable = 1;
      delay_us(1);
/* Send data */
      lcd.data = n;
      delay_us(1);
      lcd.enable = 0;
      delay_us(1);
/* Reset RW/RS */
      lcd.rw = 1;
      lcd.rs = 0;
      delay_us(1);

}

/* Based on lcd.c */
boolean lcd_read_status() {

      boolean status;
/* Set port direction */
      lcd_dir = lcd_read;
/* Read from the LCD */
      lcd.rw = 1;
      delay_us(1);
      lcd.enable = 1;
      delay_us(1);
      status = lcd.rs;
      lcd.enable = 0;
      delay_us(2);
/* Return the status */
      return(status);
}

/* Based on lcd.c */
void lcd_init(int init) {

    int i;

    lcd_dir = lcd_write;
    lcd.rs = 0;
    lcd.rw = 0;
    lcd.enable = 0;
    delay_ms(15);

    for(i=0; i<=3; i++) {
       lcd_send_byte(0, 0x30);
       delay_ms(5);
    }

	switch (init) {
		case 1 :	for(i=0; i<=3; i++)
       					lcd_send_byte(0, lcd_init_nocursor[i]);	break;
       	case 2 :	for(i=0; i<=3; i++)
       					lcd_send_byte(0, lcd_init_cursor[i]); break;


       	default :	for(i=0; i<=3; i++)
       					lcd_send_byte(0, lcd_init_nocursor[i]); break;
	}
}

/* Based on lcd.c */
void lcd_putc( char c ) {
   switch (c) {
   /* clear the screen */
      case '\f'   : lcd_send_byte(0, 0x01);
                  delay_ms(2);                  break;
   /* back space */
      case '\b'   : lcd_send_byte(0, 0x08);     break;
   /* new line */
      case '\n'   : lcd_send_byte(0, 0xC0);     break;

      default     : lcd_send_byte(1, c);        break;
   }
}

