#include "config.h"


uchar digiBuf[SEG_COUNT]={0};

uchar  irTime=0;//������ȫ�ֱ���
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

	for(i=0;i<4;i++)      //����4���ֽ�
	{
		for(j=1;j<=8;j++) //����1���ֽ�8λ
		{
			cord=irData[k];
	//		if(cord>7)//����ĳֵΪ1������Ϊ7���뾧���йأ����ֵ��Ҫ�������	  0.278*7=1.946	0.256*7=    1.792
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
	for(m=0;m<CONFIG_LEN-1;m++){	  //���һ��Ϊ�˳�ѡ����豣����rom
		IapProgramByte(CONFIG_ROM_ADDR+m,configArr[m]);
		configArr[m]= IapReadByte(CONFIG_ROM_ADDR+m);
	}
}
void retreiveConfig(){
	uchar m;
	for(m=0;m<CONFIG_LEN-1;m++){	  //���һ��Ϊ�˳�ѡ����豣����rom
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
 	TL0 = 0xD7;		//���ö�ʱ��ֵ   16λ 600us ���1%
	TH0 = 0xFD;		//���ö�ʱ��ֵ
//	TL0=0x00;
//	TH0=0xff;
	if(irReceiveFlag==1)
	{
		irTime++;  //���ڼ���2���½���֮���ʱ��
	}
}
void irExProcess(){
	if(irProcessFlag==0)                      
	{
		if(irReceiveFlag==1)
		{
	   // 	if(irTime>=33&&irTime<63){//������ TC9012��ͷ�룬9ms+4.5ms		  0.278*33=9.174  0.278*63=17.514  0.256*33= 8.448 0.256*63= 16.128
	       if(irTime>=15&&irTime<28){ //  0.6*15=9 0.6*27=16.2
				irCount=0;
			}
	    	irData[irCount]=irTime;//�洢ÿ����ƽ�ĳ���ʱ�䣬�����Ժ��ж���0����1
	    	irTime=0;
	    	irCount++;
	   		if(irCount==33)
	      	{
				irReceiveFlag=0;
		  		irProcessFlag=1;
				irCount=0;
				ET0=0;    //���ж�
		     	TR0 = 0;		//��ʱ��0ֹͣ��ʱ
		  	}
		
		}
		else{
			 irReceiveFlag=1;
			 irTime=0;
			 ET0=1;    //���ж�
		     TR0 = 1;		//��ʱ��0��ʼ��ʱ
		}
    }
}
void irInit(){	//δ�����ж�
  
	AUXR &= 0x7F;		//��ʱ��ʱ��12Tģʽ
	TMOD &= 0xF0;		//���ö�ʱ��ģʽ
	TMOD |= 0x01;		//���ö�ʱ��ģʽ
	TL0 = 0xD7;		//���ö�ʱ��ֵ   16λ 600us ���1%
	TH0 = 0xFD;		//���ö�ʱ��ֵ

	TF0 = 0;		//���TF0��־

 	IT0 = 1;   //ָ���ⲿ�ж�0�½��ش�����INT0 (P3.2)
 	EX0 = 1;   //ʹ���ⲿ�ж�
 

}