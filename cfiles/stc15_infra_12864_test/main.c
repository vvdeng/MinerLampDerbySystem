#include <reg52.h>
#include "useful.h"
#include "config.h"
#include "eeprom.h"
#include "lcd12864.h"
#include "vvspi.h" 
uchar seqArr[16]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};













void tim0_isr (void) interrupt 1 using 1
{
	irTime0Process();
}


void EX0_ISR (void) interrupt 0 //外部中断0服务函数
{
	irExProcess();
}


void init(){
   irInit();
 //  retreiveConfig();
   	EA = 1;    //开总中断

}






void main(void)
{

	uchar xdata str[] = "红外测试";
	uchar disArr[3];
	disArr[0]='0';
	disArr[1]='0';
	disArr[2]='\0';
	init_spi();
	 
	LCD_DispIni();
	ClrScreen();

	LCD_PutString(0,0,str);
	
	init();
   	 
 

	while(1)//主循环
	{
	   processSettingIfNecessary();
	   disArr[0]=seqArr[IRcord[2]/16];
	   disArr[1]=seqArr[IRcord[2]%16];
	   LCD_PutString(0,1,disArr);
   //	   displayShuma(digiBuf,SEG_COUNT);
	}
}







  
  