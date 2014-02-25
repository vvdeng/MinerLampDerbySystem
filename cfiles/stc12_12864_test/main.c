
#include <reg52.h>  
#include<intrins.h> 
 
#include "lcd12864.h" 
      





sbit LED_TXD=P1^2;//2;
sbit LED_RXD=P1^4;//4;
sbit isLedDisplay=P2^7;








void main()
{

	
	LCD_DispIni();
	ClrScreen();	
	FullCGRAM();
	DisplayCGRAM(1,1);
 	while(1){

	}
}

/*void  main(void)
{ 

	uchar xdata str[] = "西安重装",str2[] = "渭南光电科技";
		init_spi();
	 
	LCD_DispIni();
	ClrScreen();
		LCD_PutString(0,0,str);
		LCD_PutString_24(0,1,str);
	//	LCD_PutString(4,0,str2);
	//	FullCGRAM();
	//	DisplayCGRAM(1,3);

	while(1)
   {  
  //  delayus(10000);	
    //  lcd_disp_sz_str(0,0,str);
	
    
   }
}
*/
