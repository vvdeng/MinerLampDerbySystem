#include "vvspi.h"

void init_spi()
{
  ACC = P_SW1;
  ACC &= ~(SPI_S0 | SPI_S1);  //SPI_S0=0 SPI_S1=1
  ACC |= SPI_S1;              //(P5.4/SS_3, P4.0/MOSI_3, P4.1/MISO_3, P4.3/SCLK_3)
  P_SW1 = ACC;  
	//SSIG = 1;   //忽略SS脚
	//SPEN = 1;   //允许SPI工作
	//DORD = 0;   //先传高位MSB
	//MSTR = 1;   //设置单片机为主机
	SPCTL = 0xD0; //SPI Control Register SSIG SPEN DORD MSTR CPOL CPHA SPR1 SPR0 0000,0100
	SPSTAT = 0xC0; //
	//IE2 |= 0x02; //允许SPI中断控制位
}

void WriteByte(u8 temp)
{
	SPDAT = temp;
	while(!(SPSTAT & 0x80));
	SPSTAT = 0xC0;
}

u8 ReadByte(void)
{
	u8 temp;
	//SPSTAT = 0xC0;
	SPDAT = 0x00;
	while(!(SPSTAT & 0x80));
	temp = SPDAT;
	SPSTAT = 0xC0;
	return temp;
}

