#include "config.h"
#include "lcd12864.h"
uchar code shumaMap[16]={0x3f,0x06,0x5b,0x4f,0x66,0x6d,0x7d,0x07,0x7f,0x6f,0x77,0x7c,0x39,0x5e,0x79,0x71};
uchar code shumaDotMap[16]={0xbf,0x86,0xdb,0xcf,0xe6,0xed,0xfd,0x87,0xff,0xef,0xf7,0xfc,0xb9,0xde,0xf9,0xf1};
uchar duanInteval[4]={3,12,48,192};
uchar digiBuf[BITS_COUNT]={0};
uchar digiDispBuf[BITS_COUNT+1]={0};
uchar dotPos=DOT_POS_NONE;
uchar  irTime=0;//红外用全局变量
bit inSetting=0,irProcessFlag=0,irReceiveFlag=0;
uchar IRcord[4];
uchar irData[33];
uchar irCount=0;
uchar setState=SET_STATE_NONE;

uchar configMenuArr[CONFIG_LEN]={CONFIG_ADDR,CONFIG_BRIGHT,CONFIG_BATFULL,CONFIG_DEBUG};
uchar configValueArr[CONFIG_LEN]={0};
uchar  *  configMenuStrArr[CONFIG_LEN]={"地址设置","亮度阈值","充满阈值","调试"};


void showMenuItem(uchar index,bit selected){
	ClrScreen();
	if(configValueArr[CONFIG_DEBUG]!=0){
		LCD_PutString(0,0,"调试");
	}
	LCD_PutString(3,0,"设置");
	LCD_PutString(2,1,configMenuStrArr[index]);
	if(selected){
		LCD_PutChar(1,2,26);//输出"->"
	}
} 
void dispDigit(){
	
	  	
	  	digiDispBuf[0]='0'+digiBuf[0];
		digiDispBuf[1]='0'+digiBuf[1];
		digiDispBuf[2]='0'+digiBuf[2];
		digiDispBuf[3]='\0';
		LCD_PutString(3,2,digiDispBuf);
	
}

uchar  curMenuIndex=0,curSelBit=0;
void Ir_work(void)
{
	switch (IRcord[2]){
	case IR_CHANNEL:
	if(setState==SET_STATE_NONE){
		setState=SET_STATE_MENU;
		curMenuIndex=0;
		showMenuItem(curMenuIndex,1);
		
	}
	break;
	case IR_EQ:
	if(setState==SET_STATE_MENU){
		setState=SET_STATE_MENU_ITEM_SEL;
	
		curSelBit=0;
		
		
		showMenuItem(curMenuIndex,1);
		digiBuf[0]=configValueArr[curMenuIndex]/100;
		digiBuf[1]=(configValueArr[curMenuIndex]%100)/10;
		digiBuf[2]=configValueArr[curMenuIndex]%10;
		dispDigit();
	}
	else if(setState==SET_STATE_MENU_ITEM_SEL){
		setState=SET_STATE_MENU_ITEM_BIT_SEL;
		showMenuItem(curMenuIndex,0);
		LCD_PutChar(2,3,26);//输出"->"
		dispDigit();
		curSelBit=0;
		LCD_PutPosition(3+curSelBit,3);
	}
	else if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
		if(curMenuIndex==CONFIG_EXIT){
			setState=SET_STATE_NONE;
			
			inSetting=0;			
		}
		else{
			configValueArr[curMenuIndex]=digiBuf[0]*100+digiBuf[1]*10+digiBuf[2];
			saveConfig();
			LCD_PutString(2,3,"设置完毕!");
			setState=SET_STATE_MENU_ITEM_FINISHED;
		
		
		}
	}		

		
	break;
	case IR_CHANNEL_MINUS:
	if(setState==SET_STATE_MENU){
		setState=SET_STATE_NONE;
		ClrScreen();
	}
	else if(setState==SET_STATE_MENU_ITEM_SEL){
		setState=SET_STATE_MENU;
		showMenuItem(curMenuIndex,1);
	
	}
	else if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
		setState=SET_STATE_MENU;
		showMenuItem(curMenuIndex,1);
	}
	else if(setState==SET_STATE_MENU_ITEM_FINISHED){
		setState=SET_STATE_MENU_ITEM_BIT_SEL;
		showMenuItem(curMenuIndex,0);
		LCD_PutChar(2,3,26);//输出"->"
		dispDigit();
		curSelBit=0;

		LCD_PutPosition(3+curSelBit,3);
	}
	break;
	case IR_NEXT:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
		curSelBit=(curSelBit+1)%BITS_COUNT; 
		LCD_PutPosition(3+curSelBit,3);

	
		
	}
	break;
	case IR_PREV:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
		curSelBit=(curSelBit+BITS_COUNT-1)%BITS_COUNT;
		LCD_PutPosition(3+curSelBit,3);
		
		
		
	}
	break;
	case IR_VOL_ADD:
	if(setState==SET_STATE_MENU){
		
		curMenuIndex=(curMenuIndex+1)%MENU_LEN;
	//	curMenu=curMenuArr[curMenuIndex];
		showMenuItem(curMenuIndex,0);
		
	} else 	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
		
			digiBuf[curSelBit]=(digiBuf[curSelBit]+1)%10;
		  	dispDigit();
	}
	break;
	case IR_VOL_MINUS:
	if(setState==SET_STATE_MENU){
		
		curMenuIndex=(curMenuIndex+MENU_LEN-1)%MENU_LEN;
	//	curMenu=curMenuArr[curMenuIndex];
		showMenuItem(curMenuIndex,0);
		
	}else 	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
		
			digiBuf[curSelBit]=(digiBuf[curSelBit]+10-1)%10;
			dispDigit();
	}
	break;
	case IR_ZERO:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
			digiBuf[curSelBit]=0;
			dispDigit();
	}
	break;
	case IR_ONE:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
			digiBuf[curSelBit]=1;
			dispDigit();
	}
	break;
	case IR_TWO:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
			digiBuf[curSelBit]=2;
			dispDigit();
	}
	break;
	case IR_THREE:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
			digiBuf[curSelBit]=3;
			dispDigit();
	}
	break;
	case IR_FOUR:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
			digiBuf[curSelBit]=4;
			dispDigit();
	}
	break;
	case IR_FIVE:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
			digiBuf[curSelBit]=5;
			dispDigit();
	}
	break;
	case IR_SIX:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
			digiBuf[curSelBit]=6;
			dispDigit();
	}
	break;
	case IR_SEVEN:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
			digiBuf[curSelBit]=7;
			dispDigit();
	}
	break;
	case IR_EIGHT:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
			digiBuf[curSelBit]=8;
			dispDigit();
	}
	break;
	case IR_NINE:
	if(setState==SET_STATE_MENU_ITEM_BIT_SEL){
			digiBuf[curSelBit]=9;
			dispDigit();
	}
	break;

	default:
		break;
	
	}
	
}

void Ircordpro(void)
{ 
	uchar i, j, k=1;
	uchar cord,value;

	for(i=0;i<4;i++)      //处理4个字节
	{
		for(j=1;j<=8;j++) //处理1个字节8位
		{
			cord=irData[k];
	//		if(cord>7)//大于某值为1，这里为7，与晶振有关，这个值需要具体调试	  0.278*7=1.946	0.256*7=    1.792
			if(cord>2)       // 0.6*3=1.8
			{
				value|=0x80;
			}
			if(j<8)
			{
				value>>=1;
			}
			k++;
		}
		IRcord[i]=value;
		value=0;     
	} 
}


void saveConfig(){
	uchar m;
	IapEraseSector(IAP_ADDRESS); 
	for(m=0;m<CONFIG_LEN-1;m++){	  //最后一项为退出选项，无需保存至rom
		IapProgramByte(CONFIG_ROM_ADDR+m,configValueArr[m]);
		configValueArr[m]= IapReadByte(CONFIG_ROM_ADDR+m);
	}
}
void retreiveConfig(){
	uchar m;
	for(m=0;m<CONFIG_LEN-1;m++){	  //最后一项为退出选项，无需保存至rom
	//	if(m==CONFIG_XXX){}
		configValueArr[m]= IapReadByte(CONFIG_ROM_ADDR+m);
		if(configValueArr[m]==0xff){
			configValueArr[m]=0;
		}
	}
}
void processSettingIfNecessary(){
    if(irProcessFlag)                        
	  {   
	   Ircordpro();
	   Ir_work();
 	   irProcessFlag=0;
	  }

	   
  
}
void irTime0Process(){
 	TL0 = 0xD7;		//设置定时初值   16位 600us 误差1%
	TH0 = 0xFD;		//设置定时初值
//	TL0=0x00;
//	TH0=0xff;
	if(irReceiveFlag==1)
	{
		irTime++;  //用于计数2个下降沿之间的时间
	}
}
void irExProcess(){
	if(irProcessFlag==0)                      
	{
		if(irReceiveFlag==1)
		{
	   // 	if(irTime>=33&&irTime<63){//引导码 TC9012的头码，9ms+4.5ms		  0.278*33=9.174  0.278*63=17.514  0.256*33= 8.448 0.256*63= 16.128
	       if(irTime>=15&&irTime<28){ //  0.6*15=9 0.6*27=16.2
				irCount=0;
			}
	    	irData[irCount]=irTime;//存储每个电平的持续时间，用于以后判断是0还是1
	    	irTime=0;
	    	irCount++;
	   		if(irCount==33)
	      	{
				irReceiveFlag=0;
		  		irProcessFlag=1;
				irCount=0;
		//		ET0=0;    //关中断
		//     	TR0 = 0;		//定时器0停止计时
		  	}
		
		}
		else{
			 irReceiveFlag=1;
			 irTime=0;
	//		 ET0=1;    //开中断
	//	     TR0 = 1;		//定时器0开始计时
		}
    }
}
void irInit(){	//未打开总中断
  
	AUXR &= 0x7F;		//定时器时钟12T模式
	TMOD &= 0xF0;		//设置定时器模式
	TMOD |= 0x01;		//设置定时器模式
	TL0 = 0xD7;		//设置定时初值   16位 600us 误差1%
	TH0 = 0xFD;		//设置定时初值
	TF0 = 0;		//清除TF0标志
	ET0=1;
	TR0=1;

 	IT0 = 1;   //指定外部中断0下降沿触发，INT0 (P3.2)
 	EX0 = 1;   //使能外部中断
 

}