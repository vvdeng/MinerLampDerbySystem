
#include <reg52.h>  
#include<intrins.h> 

#include "lcd12864.h" 
#include "vvspi.h"         




sbit LED_TXD=P1^2;//2;
sbit LED_RXD=P1^4;//4;
sbit isLedDisplay=P2^7;
bit infoChanged;


void init() {

}

void  main(void)
{ 

	uchar xdata str[] = "∆¡ƒª≤‚ ‘";
	init_spi();
	 
	LCD_DispIni();
	ClrScreen();

	LCD_PutString(0,0,str);
	
	

	while(1)
   {  

	
    
   }
}

