#include "lcdhz12864.h"
#include "useful.h"
bit dispFlag=1;
void Check_Busy()
{  
	if(dispFlag==0){
		return;
	}
    RS=0;
    RW=1;
    E=1;
    DataPort=0xff;
    while((DataPort&0x80)==0x80);
    E=0;
}

void Write_Cmd(unsigned char Cmd)
{
	if(dispFlag==0){
		return;
	}
	Check_Busy();
	RS=0;
	RW=0;
	E=1;
	DataPort=Cmd;
	delayUs(5);
	E=0;
	delayUs(5);
}

void Write_Data(unsigned char Data)
{
	if(dispFlag==0){
		return;
	}
	Check_Busy();
	RS=1;
	RW=0;
	E=1;
	DataPort=Data;
	delayUs(5);
	E=0;
	delayUs(5);
}


        

void LCD_PutString(unsigned char x,unsigned char y,unsigned char  *s)
{ 
	if(dispFlag==0){
		return;
	}
 switch(y)
     {
	  case 1: Write_Cmd(0x80+x);break;
	  case 2: Write_Cmd(0x90+x);break;
	  case 3: Write_Cmd(0x88+x);break;
	  case 4: Write_Cmd(0x98+x);break;
      default:break;
	 }
 while(*s>0)
   { 
      Write_Data(*s);
      s++;
      delayUs(50);
   }
}
void LCD_Init(){
	if(dispFlag==0){
		return;
	}
	//12864 初始化 
   	delayMs(40);           //大于40MS的延时程序
   	PSB=1;                 //设置为8BIT并口工作模式
	delayMs(1);            //延时
  	RES=0;                 //复位
  	delayMs(1);            //延时
  	RES=1;                 //复位置高
  	delayMs(10);
  	Write_Cmd(0x30);       //选择基本指令集
  	delayUs(50);         //延时大于100us
  	Write_Cmd(0x30);       //选择8bit数据流
  	delayUs(20);         //延时大于37us
  	Write_Cmd(0x0c);       //开显示(无游标、不反白)
  	delayUs(50);         //延时大于100us
  	Write_Cmd(0x01);       //清除显示，并且设定地址指针为00H
  	delayMs(15);           //延时大于10ms
  	Write_Cmd(0x06);       //指定在资料的读取及写入时，设定游标的移动方向及指定显示的移位，光标从右向左加1位移动
  	delayUs(50);         //延时大于100us

}
void LCD_PutChar(unsigned char x,unsigned char y,unsigned char c)
{ 
	if(dispFlag==0){
		return;
	}
 	switch(y)
     {
	  case 1: Write_Cmd(0x80+x);break;
	  case 2: Write_Cmd(0x90+x);break;
	  case 3: Write_Cmd(0x88+x);break;
	  case 4: Write_Cmd(0x98+x);break;
      default:break;
	 } 
      Write_Data(c);
    
      delayUs(50);
 
}
uchar oldUnderlineX=0,oldUnderlineY=0;
/*
void resetUnderline(){
	oldUnderlineX=0;
	oldUnderlineY=0;
} */

void DisplayUnderline(unsigned char cx,unsigned char cy,bit display)  //实际输出个上三角
{
	if(dispFlag==0){
		return;
	}	
	if(display==0){
	//擦去下划线
	}
	if(oldUnderlineX!=0||oldUnderlineY!=0){	
		switch(oldUnderlineY)
		{
			case 1: Write_Cmd(0x80+oldUnderlineX);break;
			case 2: Write_Cmd(0x90+oldUnderlineX);break;
			case 3: Write_Cmd(0x88+oldUnderlineX);break;
			case 4: Write_Cmd(0x98+oldUnderlineX);break;
			default:break;
		} 
		Write_Data(' ');//
	}
	oldUnderlineX=cx;
	oldUnderlineY=cy;
	switch(cy)
	{
			case 1: Write_Cmd(0x80+cx);break;
			case 2: Write_Cmd(0x90+cx);break;
			case 3: Write_Cmd(0x88+cx);break;
			case 4: Write_Cmd(0x98+cx);break;
			default:break;
	}
	Write_Data(30);//30是个上三角符号
	 
} 
void LCD_PutPosition(unsigned char x,unsigned char y)
{ 
	if(dispFlag==0){
		return;
	}
	DisplayUnderline(x,y+1,1);	
}
void ClrScreen()
{ 
	if(dispFlag==0){
		return;
	}
	Write_Cmd(0x01);
	delayMs(15);
}