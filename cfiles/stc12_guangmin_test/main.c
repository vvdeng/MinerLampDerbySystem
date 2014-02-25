
#include <reg52.h>  
#include<intrins.h> 

#include "lcd12864.h" 
#include "vvspi.h"         
#define uchar unsigned char
uchar stateValue=0,brightValue=0;
bit infoChanged;//用于LCD显示
bit stateAdFlag=0,brightAdFlag=0;
sfr   ADC_CONTR      =   0xBC; 
sfr   ADC_RES       =   0xBD;                 
sfr   ADC_LOW2     =   0xBE;                
sfr   P1ASF         =   0x9D; 
sbit  EADC =IE^5;
#define   ADC_POWER      0x80                 
#define   ADC_FLAG      0x10                  
#define   ADC_START    0x08                 
#define   ADC_SPEEDLL   0x00                  
#define   ADC_SPEEDL     0x20                 
#define   ADC_SPEEDH     0x40                  
#define   ADC_SPEEDHH   0x60

#define ADC_STATE_CHANNEL 0 //状态ad通道选用P1.0  
#define ADC_BRIGHT_CHANNEL 1 //光线亮度ad通道选用P1.1  


sbit LED_TXD=P1^2;//2;
sbit LED_RXD=P1^4;//4;


void DelayUs2x(unsigned char t) {
	while (--t)
		;
}

void delayMs(unsigned char t) {

	while (t--) {

		DelayUs2x(245);
		DelayUs2x(245);
	}
}

void init() {
	 LCD_DispIni(); //先初始化12864屏
	 ClrScreen();
	//片内AD初始化
	P1ASF = 0x03;  //设置P1.0、P1.1口                     
    ADC_RES = 0;                        
	ADC_CONTR = ADC_POWER | ADC_SPEEDLL;
	init_spi();
}
unsigned int getAdValueByChannel(uchar channel,uchar bits){
	unsigned int result;
	bit adFinished=0;

	ADC_CONTR = ADC_POWER | ADC_SPEEDLL | ADC_START | channel;
	while(adFinished==0){
		adFinished=(ADC_CONTR&ADC_FLAG);
	}
	if(bits==10)
	{
		result=(ADC_RES*4+(ADC_LOW2&0x03));
	}
	else{
		result=ADC_RES;
	}
	ADC_CONTR &= !ADC_FLAG; 
	return result;
}
void getAdValue(){
		
	stateValue=getAdValueByChannel(ADC_STATE_CHANNEL,10);
	
	stateAdFlag=1;
	delayMs(100);

	brightValue=getAdValueByChannel(ADC_BRIGHT_CHANNEL,8);
	brightAdFlag=1;


	delayMs(100);

}
void displayADC(){
	
		uchar stateValueStr[5]={'\0'};
		uchar adcBrightStr[4]={'\0'};


		if(stateAdFlag==1){
			stateAdFlag=0;
			stateValueStr[0]='0'+(stateValue/1000);
			stateValueStr[1]='0'+((stateValue%1000)/100);
			stateValueStr[2]='0'+((stateValue%100)/10);
			stateValueStr[3]='0'+(stateValue%10);
			stateValueStr[4]='\0';
		//	LCD_PutString(0,3,"模拟量：");
  			LCD_PutSingleByteString(12,2,stateValueStr);
	
		}
		if(brightAdFlag==1){
			brightAdFlag=0;
			adcBrightStr[0]='0'+(brightValue/100);
			adcBrightStr[1]='0'+((brightValue%100)/10);
			adcBrightStr[2]='0'+((brightValue%100)%10);
			adcBrightStr[3]='\0';
			LCD_PutSingleByteString(13,1,adcBrightStr);
		}

}
void  main(void)
{ 
	uchar xdata str[] = "AD测试";
	init();


	LCD_PutString(0,0,str);
	
	

	while(1)
   {  

	 getAdValue();
     displayADC();
   }
}

