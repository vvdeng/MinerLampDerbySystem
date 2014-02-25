

#include <reg52.h>
#include <intrins.h>
#include "useful.h"
#include "lcd12864.h"
#include "vvspi.h"
#define FOSC 11059200L          //系统频率
#define BAUD 9600             //串口波特率
sfr P4=0xc0;
sfr P2M1=0x95;
sfr P2M0=0x96;


sfr S2CON = 0x9a;               //UART2 控制寄存器
sfr S2BUF = 0x9b;               //UART2 数据寄存器
sfr T2H   = 0xd6;               //定时器2高8位
sfr T2L   = 0xd7;               //定时器2低8位
sfr IE2   = 0xaf;               //中断控制寄存器2

#define S2RI  0x01              //S2CON.0
#define S2TI  0x02              //S2CON.1
#define S2RB8 0x04              //S2CON.2
#define S2TB8 0x08              //S2CON.3



#define RS_T 1
#define RS_R 0
sbit RS_485_2=P2^0;
sbit ledTest0=P4^2;
sbit ledTest1=P4^4;
void sendDataDown(uchar dat);

void main()
{

//	uchar n;
	uchar xdata str[] = "通信测试";
	init_spi();
	 
	LCD_DispIni();
	ClrScreen();

	LCD_PutString(0,0,str);
	
	
	P2M1=0x00;
	P2M0=0x01;
	ledTest0=0;
	ledTest1=0;
	RS_485_2=RS_R;
 	S2CON = 0x50;		//8位数据,可变波特率
	AUXR |= 0x04;		//定时器2时钟为Fosc,即1T
	T2L = 0xE0;		//设定定时初值
	T2H = 0xFE;		//设定定时初值
	AUXR |= 0x10;		//启动定时器2
	IE2 = 0x01;             //使能串口2中断
   
    EA = 1;
	
    
    while(1){
		delayMs(2000);
		ledTest0=~ledTest0;
/*		for(n=0;n<100;n++){
			sendDataDown(65);
		}
*/	}
}

void sendDataDown(uchar dat){
	RS_485_2=RS_T;
	IE2 = 0x00;
	S2BUF=dat;	
	while(!(S2CON&S2TI));
 	S2CON &= ~S2TI; 

	IE2 = 0x01;
	RS_485_2=RS_R;
}

uchar m,temp;
void Uart2() interrupt 8 using 1
{
    if (S2CON & S2RI)
    {
        S2CON &= ~S2RI;         
    	ledTest1=1;     
		temp=S2BUF;

       	for(m=0;m<100;m++){
			  sendDataDown(temp);
		}  
    }
    if (S2CON & S2TI)
    {
        S2CON &= ~S2TI;         
        
    }
}




