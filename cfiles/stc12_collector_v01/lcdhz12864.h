#ifndef _LCD_HZ_12864_H_
#define _LCD_HZ_12864_H_
#include "useful.h"
#include "reg52.h"
#define DataPort P0 
//12864œ‘ æ∆¡
sbit RS = P2 ^ 0;
sbit RW = P2 ^ 1;
sbit E  = P2^2;
sbit PSB = P2^3;
sbit RES = P2^4;
void LCD_Init();
void LCD_PutString(unsigned char x,unsigned char y,unsigned char  *s);
void LCD_PutChar(unsigned char x,unsigned char y,unsigned char c);
void LCD_PutPosition(unsigned char x,unsigned char y);
void ClrScreen();
void Write_Cmd(unsigned char Cmd);
extern bit dispFlag;
#endif