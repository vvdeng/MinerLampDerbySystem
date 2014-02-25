#ifndef __VV_SPI_H__
#define __VV_SPI_H__
#include <reg52.h>
#define u8 unsigned char
#define u16 unsigned short
#define u32 unsigned long

sfr     AUXR   =   0x8e;             //Auxiliary register
sfr   SPSTAT    =   0xcd;             //SPI status register
#define   SPIF    0x80              //SPSTAT.7
#define   WCOL   0x40              //SPSTAT.6
sfr   SPCTL   =   0xce;             //SPI control register
#define   SSIG   0x80              //SPCTL.7
#define   SPEN      0x40              //SPCTL.6
#define   DORD      0x20              //SPCTL.5
#define   MSTR     0x10              //SPCTL.4
#define   CPOL    0x08              //SPCTL.3
#define   CPHA  0x04              //SPCTL.2
#define   SPDHH   0x00              //CPU_CLK/4
#define   SPDH    0x01             //CPU_CLK/ 16
#define   SPDL    0x02              //CPU_CLK/64
#define   SPDLL    0x03              //CPU_CLK/ 128
sfr   SPDAT   =   0xcf;             //SPI data register
sbit CS = P1^3;

void init_spi();

void WriteByte(u8 temp);

u8 ReadByte(void);
#endif
