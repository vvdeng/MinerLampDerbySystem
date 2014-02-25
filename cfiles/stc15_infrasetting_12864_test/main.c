#include <reg52.h>
#include "useful.h"
#include "config.h"
#include "insideStorage.h"
#include "display.h"
#include "lcd12864.h"
#include "vvspi.h"




void tim0_isr (void) interrupt 1 using 1
{
	irTime0Process();
}


void EX0_ISR (void) interrupt 0 //外部中断0服务函数
{
	irExProcess();
}


void init(){
	init_spi();
	LCD_DispIni(); //先初始化12864屏
	ClrScreen();
	irInit();
	retreiveConfig();
	EA = 1;    //开总中断

}






void main(void)
{
	uchar xdata str[] = "西安重装",str2[] = "渭南光电科技";
	init();
   	LCD_PutString(1,1,str);
 

	while(1)//主循环
	{
		   processSettingIfNecessary();
   	  // displayShuma(digiBuf,SEG_COUNT);
	}
}







  
  