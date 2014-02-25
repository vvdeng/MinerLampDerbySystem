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
	//12864 ��ʼ�� 
   	delayMs(40);           //����40MS����ʱ����
   	PSB=1;                 //����Ϊ8BIT���ڹ���ģʽ
	delayMs(1);            //��ʱ
  	RES=0;                 //��λ
  	delayMs(1);            //��ʱ
  	RES=1;                 //��λ�ø�
  	delayMs(10);
  	Write_Cmd(0x30);       //ѡ�����ָ�
  	delayUs(50);         //��ʱ����100us
  	Write_Cmd(0x30);       //ѡ��8bit������
  	delayUs(20);         //��ʱ����37us
  	Write_Cmd(0x0c);       //����ʾ(���αꡢ������)
  	delayUs(50);         //��ʱ����100us
  	Write_Cmd(0x01);       //�����ʾ�������趨��ַָ��Ϊ00H
  	delayMs(15);           //��ʱ����10ms
  	Write_Cmd(0x06);       //ָ�������ϵĶ�ȡ��д��ʱ���趨�α���ƶ�����ָ����ʾ����λ�������������1λ�ƶ�
  	delayUs(50);         //��ʱ����100us

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

void DisplayUnderline(unsigned char cx,unsigned char cy,bit display)  //ʵ�������������
{
	if(dispFlag==0){
		return;
	}	
	if(display==0){
	//��ȥ�»���
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
	Write_Data(30);//30�Ǹ������Ƿ���
	 
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