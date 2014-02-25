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


void EX0_ISR (void) interrupt 0 //�ⲿ�ж�0������
{
	irExProcess();
}


void init(){
	init_spi();
	LCD_DispIni(); //�ȳ�ʼ��12864��
	ClrScreen();
	irInit();
	retreiveConfig();
	EA = 1;    //�����ж�

}






void main(void)
{
	uchar xdata str[] = "������װ",str2[] = "μ�Ϲ��Ƽ�";
	init();
   	LCD_PutString(1,1,str);
 

	while(1)//��ѭ��
	{
		   processSettingIfNecessary();
   	  // displayShuma(digiBuf,SEG_COUNT);
	}
}







  
  