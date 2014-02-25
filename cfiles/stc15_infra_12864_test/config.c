#include "config.h"


uchar digiBuf[SEG_COUNT]={0};

uchar  irTime=0;//红外用全局变量
bit irProcessFlag=0,irReceiveFlag=0;
uchar IRcord[4]={0};
uchar irData[33];
uchar irCount=0;
uchar setState=SET_STATE_NONE;

uchar configMenuArr[CONFIG_LEN]={CONFIG_ADDR,CONFIG_INTEVAL,CONFIG_DEBUG,CONFIG_EXIT};
uchar configArr[CONFIG_LEN]={55,44,33,0};


uchar  curMenuIndex=0,curSelBit=0,curSelItem=0;
void Ir_work(void)
{

	
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
	for(m=0;m<CONFIG_LEN-1;m++){	  //最后一项为退出选项，无需保存至rom
		IapProgramByte(CONFIG_ROM_ADDR+m,configArr[m]);
		configArr[m]= IapReadByte(CONFIG_ROM_ADDR+m);
	}
}
void retreiveConfig(){
	uchar m;
	for(m=0;m<CONFIG_LEN-1;m++){	  //最后一项为退出选项，无需保存至rom
	//	if(m==CONFIG_XXX){}
		configArr[m]= IapReadByte(CONFIG_ROM_ADDR+m);
		if(configArr[m]==0xff){
			configArr[m]=0;
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
				ET0=0;    //关中断
		     	TR0 = 0;		//定时器0停止计时
		  	}
		
		}
		else{
			 irReceiveFlag=1;
			 irTime=0;
			 ET0=1;    //开中断
		     TR0 = 1;		//定时器0开始计时
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

 	IT0 = 1;   //指定外部中断0下降沿触发，INT0 (P3.2)
 	EX0 = 1;   //使能外部中断
 

}