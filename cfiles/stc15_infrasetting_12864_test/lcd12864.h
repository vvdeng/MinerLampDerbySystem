#ifndef __LCD12864_H__
#define __LCD12864_H__
#include <reg52.h>
#include "useful.h"

sbit    CS1=P3^7;
sbit    CS2=P2^1;
sbit    LCD_RST= P2^2;
sbit    RW=P2^3;
sbit    DI=P2^4;
sbit    EN=P2^5;

sfr     LCD=0x80;  

#define DISPLAY_DELAY_TIME 200
#define	LCD_DISPON	0x3f
#define    LCD_STARTROW	0xc0	
#define	LCD_ADDRSTRY	0xb8	
#define	LCD_ADDRSTRX	0x40 

void  LCD_DispIni(void);
void lcd_disp_sz_char(uchar cy,uchar cx,uchar* chr);
void lcd_disp_sz_char_24(uchar cy,uchar cx,uchar* chr,uchar* buf);
void LCD_PutString(unsigned char cx,unsigned char cy,unsigned char  *s);
void LCD_PutString_24(unsigned char cx,unsigned char cy,unsigned char  *s);
void LCD_PutString_24_cy(unsigned char cy,unsigned char  *s);
void LCD_PutName_24_cy(unsigned char cy,unsigned char  *s);
void LCD_PutSingleByteString(unsigned char cx,unsigned char cy,unsigned char  *s);
void LCD_PutChar(unsigned char cx,unsigned char cy,unsigned char  chr);

void FullCGRAM();
void HalfCGRAM();
void EmptyCGRAM();
void ErrorCGRAM();
void NullCGRAM();
void DisplayCGRAM(unsigned char cx,unsigned char cy);
void DisplayUnderline(unsigned char cx,unsigned char cy,bit display);
void resetUnderline();
void LCD_PutPosition(unsigned char x,unsigned char y);
void ClrScreen();
extern bit infoChanged;
 
#endif