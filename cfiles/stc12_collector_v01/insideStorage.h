#ifndef __INSIDE_STORAGE_H__
#define __INSIDE_STORAGE_H__
typedef unsigned char BYTE;
typedef unsigned int WORD;
sfr  IAP_DATA      =   0xC2;       
sfr  IAP_ADDRH     =   0xC3;             
sfr  IAP_ADDRL     =   0xC4;               
sfr  IAP_CMD       =   0xC5;               
sfr  IAP_TRIG       =   0xC6;                
sfr  IAP_CONTR     =   0xC7;            

#define   CMD_IDLE      0                       
#define   CMD_READ      1                      
#define   CMD_PROGRAM   2                
#define   CMD_ERASE      3                     

//#define   ENABLE_IAP   0x80              //系统时钟频率<30MHz
//#define   ENABLE_IAP   0x8              //系统时钟频率<24MHz
//#define   ENABLE_IAP    0x82              //系统时钟频率<20MHz
#define   ENABLE_IAP   0x83              //系统时钟频率< 12MHz
//#define   ENABLE_IAP   0x84              //系统时钟频率<6MHz
//#define   ENABLE_IAP   0x85              //系统时钟频率<3MHz
//#define   ENABLE_IAP   0x86              //系统时钟频率<2MHz
//#define   ENABLE_IAP   0x87              //系统时钟频率< MHz

#define    IAP_ADDRESS   0x0000

void IapIdle();
BYTE IapReadByte(WORD addr);
void IapProgramByte(WORD addr, BYTE dat);
void IapEraseSector(WORD addr);

#endif
