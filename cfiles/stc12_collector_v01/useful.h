#ifndef _USEFUL_H_
#define _USEFUL_H_
sfr   AUXR    = 0x8e; 
#define uchar unsigned char
#define uint unsigned int
#define  _Nop()  _nop_()
void delayUs(uint t) ;

void delayMs(uint t);
#endif
