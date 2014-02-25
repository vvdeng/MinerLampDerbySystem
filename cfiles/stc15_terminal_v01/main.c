
#include <reg52.h>  
#include<intrins.h> 
#include "insideStorage.h"    
#include "lcd12864.h" 
#include "vvspi.h" 
#include "useful.h"        
#include "config.h"
#include "yuyin.h"
sfr IP2=0xb5;
sfr S2CON = 0x9a;               //UART2 ���ƼĴ���
sfr S2BUF = 0x9b;               //UART2 ���ݼĴ���
sfr T2H   = 0xd6;               //��ʱ��2��8λ
sfr T2L   = 0xd7;               //��ʱ��2��8λ
sfr IE2   = 0xaf;               //�жϿ��ƼĴ���2

#define S2RI  0x01              //S2CON.0
#define S2TI  0x02              //S2CON.1
#define S2RB8 0x04              //S2CON.2
#define S2TB8 0x08              //S2CON.3



#define RS_T 1
#define RS_R 0
#define DATA_PRE 0x50 //0101 ����ǰ��
#define DATA_PRE_UPDATE_STAFF 0x60 //0110 ����Ա����Ϣ����ǰ��
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

#define ADC_STATE_CHANNEL 3 //״̬adͨ��ѡ��P1.3  
#define ADC_BRIGHT_CHANNEL 4 //��������adͨ��ѡ��P1.4  


#define LOCAL_ADDRESS_ADDR  0x0
#define BRIGHT_THRESHOLD_ADDR  0x02
#define IS_SETTED_ADDR  0x04
#define VOLTAGE_THRESHOLD_ADDR  0x06
#define STAFF_INFO_ADDR 0x8



#define FIELD_COUNT 3
#define STAFF_INFO_FIELD_LENGTN 8
//�Զ��������ʽ��xxxd dddd  �{3λ��ʾ�����5λ��ʾ�Ƽܵ�ַ
#define CMD_OPEN_DOOR 0xE0
#define CMD_UPDATE_STAFF 0xC0
#define CMD_REQ_UNIT_INFO 0x80
#define COMM_STATE_NOTHING 0
#define COMM_STATE_UPDATE_STAFF 1
#define COMM_STATE_REQ_UNIT_INFO 2
#define COMM_STATE_UPDATE_STAFF_WAIT_ADDRESS 3
#define COMM_STATE_REQ_UNIT_INFO_WAIT_ADDRESS 4
#define COMM_STATE_OPEN_DOOR_WAIT_ADDRESS 5
             
#define BAT_VAL_ARR_LEN 10

#define MENU_LEN 4

sfr P4=0xc0;

sfr P3M1=0xb1;
sfr P3M0=0xb2;
sfr P2M1=0x95;
sfr P2M0=0x96;
sbit ledControl=P2^7;
sbit IR=P3^2; 
//sbit ledTest=P3^4;
sbit k0=P3^5;
//����2 485��д����
sbit RS_485_2=P2^0;
//����
/*sbit k1=P1^4;
sbit k3=P1^5;
sbit k5=P1^6;
sbit k7=P1^7;*/

sbit LED_TXD=P4^2;//2;
sbit LED_RXD=P4^4;//4;
sbit isLedDisplay=P2^7;
sfr P5=0xc8;
sfr P5M1=0xc9;
sfr P5M0=0xca;
sbit pLock=P5^5; //�����ź�


uchar* xdata fieldName[]={"������","���ţ�","���֣�"};

uchar xdata staffInfoBuf[FIELD_COUNT][STAFF_INFO_FIELD_LENGTN+1]={'\0'};
uchar xdata buf[2*FIELD_COUNT*STAFF_INFO_FIELD_LENGTN];
uchar* xdata batStateDesc[]={"���״̬","���ȡ��","��Ƴ��","��Ƴ���","��������"};
int xdata batAdValArr[BAT_VAL_ARR_LEN];

uchar tready,rready,batState=0,brightValue=0,commState=0,bufIndex=0,updateStaffInteval,curSentenceIndex,currentChannel=0,isSetted,isInfoSetted=0,batAdValArrIndex=0;
unsigned int stateValue=0,msCount=0,mCount=0;
bit isSelected=0,updateStaffFlag=0,stateAdFlag=0,brightAdFlag=0,inAddrSetting=0,isInfraredAllowed=0,timeChanged=0,lockFlag=0;
void storageBaseInfo();

uint yuyinTimeCount=0;
   	


//�Զ��������ʽ��xxdd dddd  �{2λ��ʾ�����6λ��ʾ�Ƽܵ�ַ ���64λ 0xC0
//����Ա����Ϣ 1100 0000 �������� 1000 0000 
/*bit isUpdateInfoCmd() {
	return (SBUF&0xC0)==0xC0;
}
bit isReqUnitInfo() {
	return (SBUF&0xC0)==0x80;
}
*/
void showAddress(uchar x,uchar y){
	uchar localAddressArr[4];
	configValueArr[CONFIG_ADDR]=configValueArr[CONFIG_ADDR]%1000;
	localAddressArr[0]='0'+configValueArr[CONFIG_ADDR]/100;
	localAddressArr[1]='0'+((configValueArr[CONFIG_ADDR]%100)/10);
	localAddressArr[2]='0'+(configValueArr[CONFIG_ADDR]%10);
	localAddressArr[3]='\0';
	LCD_PutSingleByteString(x,y,localAddressArr);
}
void displayStaffInfo(){
	uchar m, indexi=0,indexj=0,count=0,isChinese=0;

	if(inSetting){
		return;	
	}
	ClrScreen();
	if(configValueArr[CONFIG_DEBUG]>0)
	{
		for(m=0;m<FIELD_COUNT;m++){
			LCD_PutString(0,m,fieldName[m]);							
			LCD_PutString(3,m,staffInfoBuf[m]);
		}
	}
	else{
	//	LCD_PutString_24_cy(0,staffInfoBuf[0]);
	LCD_PutName_24_cy(0,staffInfoBuf[0]);	
	LCD_PutString(0,2,staffInfoBuf[1]);
	}
}
void redisplayStaffInfoIfNecessary(bit instanceFlag){
	if(inSetting==1){
		return;
	}
	
	if(isSetted==1){
		if((instanceFlag==1)){
			displayStaffInfo();
		}
	}
	if(configValueArr[CONFIG_DEBUG]>0)
	{
		showAddress(13,0);
	}
	else{
		showAddress(13,2);
	}

}

void updateStaffInfoIfNeccesary(){
	uchar m,n,i=0;
	if(updateStaffFlag==1){
		updateStaffFlag=0;
		isInfoSetted=1;
		infoChanged=1;
		for (m = 0; m < FIELD_COUNT; m++) {

			for (n = 0; n < STAFF_INFO_FIELD_LENGTN; n++) {
				

				staffInfoBuf[m][n] = buf[i++] << 4;
			
				staffInfoBuf[m][n] += (buf[i++]  & 0x0f);
			}

		}
		/*������storageBaseInfo��redisplayStaffInfoIfNecessary��˳��
		  �����һ�θ�����Ա��Ϣ����isSetted��δ������Ϊ1��redisplayStaffInfoIfNecessary
		   �������displayStaffInfo����ʾ��Ա��Ϣ
		*/
	//	redisplayStaffInfoIfNecessary(1);
		storageBaseInfo();
        redisplayStaffInfoIfNecessary(1);

	}
}

void sendData(uchar dat)
{	
	RS_485_2=RS_T;
	
	S2BUF=dat;	
	while(!(S2CON&S2TI));
 	S2CON &= ~S2TI;  
	RS_485_2=RS_R;
}

#define UNIT_STATE_NULL  0
#define UNIT_STATE_EMPTY  1
#define UNIT_STATE_CHARGING 2
#define UNIT_STATE_FULL  3
#define UNIT_STATE_ERROR  4

void Uart2() interrupt 8 using 1
{
	
	if (S2CON & S2RI){
		
		S2CON &= ~S2RI; 
		switch(S2BUF&0x80){
			case 0x80:{
				switch(S2BUF){
					case CMD_UPDATE_STAFF:{
						if(inSetting){//Ϊ�˱����޸ĵ�ַʱ��ͬʱ�޸���������
							return;
						}
						
						commState=COMM_STATE_UPDATE_STAFF_WAIT_ADDRESS;
					}
					break;
					case CMD_OPEN_DOOR:{
						if(inSetting){//Ϊ�˱����޸ĵ�ַʱ��ͬʱ�޸���������
							return;
						}
						
						commState=COMM_STATE_OPEN_DOOR_WAIT_ADDRESS;
					}
					break;
					case CMD_REQ_UNIT_INFO:{
						commState=COMM_STATE_REQ_UNIT_INFO_WAIT_ADDRESS;
						
					}
					break;
				}
			}
			break;
			case 0x00:{
				switch(commState){
					case COMM_STATE_UPDATE_STAFF_WAIT_ADDRESS:{
						
						if(S2BUF==configValueArr[CONFIG_ADDR]){
							LED_RXD=~LED_RXD;
							commState=COMM_STATE_UPDATE_STAFF;
							bufIndex=0;	
						}else{
							commState=COMM_STATE_NOTHING;
							LED_RXD=1;//ָʾ����
						}
					}
					break;
					case COMM_STATE_OPEN_DOOR_WAIT_ADDRESS:{
						
						if(S2BUF==configValueArr[CONFIG_ADDR]){
							LED_RXD=~LED_RXD;
							lockFlag=1;
							commState=COMM_STATE_NOTHING;
								
						}else{
							commState=COMM_STATE_NOTHING;
							LED_RXD=1;//ָʾ����
						}
					}
					break;
					case COMM_STATE_REQ_UNIT_INFO_WAIT_ADDRESS:{
						
						if(S2BUF==configValueArr[CONFIG_ADDR]){
							LED_RXD=~LED_RXD;
							commState=COMM_STATE_REQ_UNIT_INFO;
							LED_TXD=0;
							sendData((batState|DATA_PRE));
		                   
							LED_TXD=1;//ָʾ����
							LED_RXD=1;//ָʾ����

						}else{
							commState=COMM_STATE_NOTHING;
							LED_RXD=1;//ָʾ����
						}
					}
					break;
					case COMM_STATE_UPDATE_STAFF:{
							if((S2BUF&0xF0)!=DATA_PRE_UPDATE_STAFF){
								return;
							}
							buf[bufIndex++]=S2BUF;
		  					if(bufIndex==48){
								LED_RXD=1;//ָʾ����
		 	 					commState=COMM_STATE_NOTHING;
							 	updateStaffFlag=1;
//								ledTest=~ledTest;
							}
					}
					break;
				}	
			}
			break;
		
		}

	}
	else if (S2CON & S2TI){                       
		S2CON &= ~S2TI; 
		if(commState==COMM_STATE_REQ_UNIT_INFO){
			commState=COMM_STATE_NOTHING;	 
		}
		RS_485_2=RS_R;
	}

}



#define CHARGE_TIME_ARR_LEN 5
#define SHOW_TIME 1
#define CLEAR_TIME 0
uchar  chargeTimeArr[CHARGE_TIME_ARR_LEN+1]={'\0'}; 
void setChargeTimeArr(uint lmCount,bit showTime){
	uchar m=0; 
	if(showTime==CLEAR_TIME){
 		for(m=0;m<CHARGE_TIME_ARR_LEN;m++){
			chargeTimeArr[m]=' ';
		}
		
	}
	else{
	  uchar minute=lmCount%60; 
	  uchar hour=lmCount/60;
	  chargeTimeArr[0]='0'+(hour/10);
	  chargeTimeArr[1]='0'+(hour%10);
	  chargeTimeArr[2]='-';
	  chargeTimeArr[3]='0'+(minute/10);
	  chargeTimeArr[4]='0'+(minute%10);
	 
	}
}
void showTimeIfNeccesary(){
 	uchar m=0;
	if( inSetting==1){
		return;
	}
	if(batState==UNIT_STATE_CHARGING||batState==UNIT_STATE_FULL){
 		setChargeTimeArr(mCount,SHOW_TIME);
 	}
 	
 	else{
 		setChargeTimeArr(0,CLEAR_TIME);
 	}
 	LCD_PutSingleByteString(11,3,chargeTimeArr);
	//LCD_PutString(6,4,"");
}
void clearTime(){
	msCount=0;
	mCount=0;
}
void init() {
	uchar m;
	//��
	P5M1=0x00;
	P5M0=0x20;
	//����
	P3M1=0x10;//0b00010000;
	P3M0=0x60;//0b01100000;
	//ͨѶʹ�ܣ�
	P2M1=0x00;
	P2M0=0x01;
	RS_485_2=RS_R; //485��ʼ���ڽ���״̬
  	
	//��ʱ��0��ʼ�� 50ms
/*	TMOD |= 0x01;	  		     
    TH0=(65536-45872)/256;	      
	TL0=(65536-45872)%256; 
	ET0=1;           
	TR0=1; 
*/ 
  	AUXR &= 0x7F;		//��ʱ��ʱ��12Tģʽ
	TMOD &= 0xF0;		//���ö�ʱ��ģʽ
	TMOD |= 0x01;		//���ö�ʱ��ģʽ
	//50ms
	//TL0 = 0x00;		//���ö�ʱ��ֵ
	//TH0 = 0x4C;		//���ö�ʱ��ֵ
	//600us
	TL0 = 0xD7;		//���ö�ʱ��ֵ   16λ 600us ���1%
	TH0 = 0xFD;		//���ö�ʱ��ֵ
	TF0 = 0;		//���TF0��־
	ET0=1;
	TR0 = 1;		//��ʱ��0��ʼ��ʱ	

   //����2��ʼ��
	
	
 	S2CON = 0x50;		//8λ����,�ɱ䲨����
	AUXR |= 0x04;		//��ʱ��2ʱ��ΪFosc,��1T
	T2L = 0xE0;		//�趨��ʱ��ֵ
	T2H = 0xFE;		//�趨��ʱ��ֵ
	AUXR |= 0x10;		//������ʱ��2
	
	LCD_DispIni(); //�ȳ�ʼ��12864��
	//Ƭ��AD��ʼ��
	P1ASF = 0x18;  //����P1.4��P1.3��                     
    ADC_RES = 0;                        
	ADC_CONTR = ADC_POWER | ADC_SPEEDLL;
//  delayMs(50); 
//	EADC=1; //��ADC�ж� �������жϷ�ʽ

//	IP=0x10;//�趨����1Ϊ�{���ȼ�
//	ES=1; //������1�ж�

	IP=0x00;
	IP2=0x01;//�趨����2Ϊ�{���ȼ�
	IE2 = 0x01;             //ʹ�ܴ���2�ж�		
/*	//�ⲿ�ж�1 ��ʼ��
  	EX1=1;         
    IT1=1;  
*/
//�ⲿ�ж�0 ��ʼ�� ʹ��������Ч
	IT0 = 1;   
 	EX0 = 1;
	EA = 1;
		
	init_spi(); 
	

	for(m=0;m<BAT_VAL_ARR_LEN;m++){
	 	batAdValArr[m]=-1;
	}

	isSelected=0;
	isLedDisplay=1; //������LEDĬ����
	initYuyin();
}





void displayADC(){
	
		uchar stateValueStr[5]={'\0'};
		uchar adcBrightStr[4]={'\0'};
		if(inSetting==1){
			return;
		}

		if(stateAdFlag==1){
			stateAdFlag=0;
			stateValueStr[0]='0'+(stateValue/1000);
			stateValueStr[1]='0'+((stateValue%1000)/100);
			stateValueStr[2]='0'+((stateValue%100)/10);
			stateValueStr[3]='0'+(stateValue%10);
			stateValueStr[4]='\0';
		//	LCD_PutString(0,3,"ģ������");
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
void displayBatState(){
		
	
		if(inSetting==1){
			return;
		}

		{
		
			switch(batState){
			case UNIT_STATE_NULL :
				NullCGRAM();
				break;
			case UNIT_STATE_EMPTY :
				EmptyCGRAM();
				break;
			case UNIT_STATE_CHARGING :
				HalfCGRAM();
				
				break;
			case UNIT_STATE_FULL :
				FullCGRAM();
				break;
			case UNIT_STATE_ERROR:
				ErrorCGRAM();
				break;
			default :
				break;
			}
			DisplayCGRAM(0,4); 
			LCD_PutString(1,3,batStateDesc[batState]);
		//	delayMs(100);
		}

}
void storageBaseInfo(){
	uchar m,n;
	IapEraseSector(IAP_BASEINFO_ADDRESS); 

	if(isInfoSetted==1){
		isSetted=1;
	}
	IapProgramByte(IAP_BASEINFO_ADDRESS+IS_SETTED_ADDR, isSetted);
	
	for (m = 0; m < FIELD_COUNT; m++) {
		for (n = 0; n < STAFF_INFO_FIELD_LENGTN; n++) {
			IapProgramByte(IAP_BASEINFO_ADDRESS+STAFF_INFO_ADDR+m*STAFF_INFO_FIELD_LENGTN+n, staffInfoBuf[m][n]);
		}
	}
     
}
void retreiveBaseInfo(){
	uchar m,n;
	

	isSetted=IapReadByte(IAP_BASEINFO_ADDRESS+IS_SETTED_ADDR);
	if(isSetted>1){
		isSetted=0;
	}

	for (m = 0; m < FIELD_COUNT; m++) {
		for (n = 0; n < STAFF_INFO_FIELD_LENGTN; n++) {
			staffInfoBuf[m][n]=IapReadByte(IAP_BASEINFO_ADDRESS+STAFF_INFO_ADDR+m*STAFF_INFO_FIELD_LENGTN+n);
		}
	}
}
void storageInfo(){
	saveConfig();
	storageBaseInfo();
}
void retreiveInfo(){
	retreiveConfig();
	retreiveBaseInfo();
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
bit oldTipFlag=0,tipFlag=0,brightChangeFlag=0;
#define YUYIN_TIME_COUNT_THRESHOLD 200  //50ms*200=10s
void getAdValue(){
		
	stateValue=getAdValueByChannel(ADC_STATE_CHANNEL,10);
	batAdValArr[(batAdValArrIndex++)%BAT_VAL_ARR_LEN]=stateValue;
	stateAdFlag=1;
	delayMs(100);

	brightValue=getAdValueByChannel(ADC_BRIGHT_CHANNEL,8);

	brightAdFlag=1;
	if(brightValue<configValueArr[CONFIG_BRIGHT]){
		isLedDisplay=0;	
		
		oldTipFlag=tipFlag;
		tipFlag=1;//��Ϊ���Ѵ򿪣�����������ʾ
		
	}
	else{
		isLedDisplay=1;
		oldTipFlag=tipFlag;
		tipFlag=0;//��Ϊ���Ѵ򿪣�����������ʾ
	}

	delayMs(100);

}

uchar oldBatState=UNIT_STATE_NULL;
uchar emptyCount=0;fullCount=0,chargingCount=0; //����ĳ��״̬�������ֵĴ���
void stateTest(){
	//BAT_VAL_ARR_LEN
	uchar m,zeroCount=0,usefulCount=0,emptyRangeCount=0,fullRangeCount=0,chargingRangeCount=0;//fullRangeCount��Ϊ�˽������ʱ��ʱƽ��ֵҲ��С��10�����⣬���������һ��10���ɼ��������ٻ���3������10�������ز��ᣬ����Ҫ��ʵ���������֤�����ڳ�״̬�᲻��������ֵ����50���¡�
	unsigned int sum=0,everage=0;
	for(m=0;m<BAT_VAL_ARR_LEN;m++){
		if(batAdValArr[m]==-1){
			batState=UNIT_STATE_NULL;
			return;
		}else if(batAdValArr[m]==0){
			zeroCount++;
		}
		else{
			if(batAdValArr[m]>configValueArr[CONFIG_BATFULL_UP]){	 //���������ڳ� ��ֵ 30��
				chargingRangeCount++;
			}
			else if(batAdValArr[m]>configValueArr[CONFIG_BATFULL_DOWN]){ 
				fullRangeCount++;
			}
			else{
				emptyRangeCount++;
			}
			usefulCount++;
			sum+=batAdValArr[m];
		}
	}
		everage=(usefulCount==0?0:sum/usefulCount);
		if(zeroCount>7||everage<=configValueArr[CONFIG_BATFULL_DOWN]){
				if(emptyCount<250){
					emptyCount++;
				}
				fullCount=0;
				chargingCount=0;
				
			
		} 
	

		if(fullRangeCount>4 ||everage>configValueArr[CONFIG_BATFULL_DOWN]){
			emptyCount=0;
			if(fullCount<250){
				fullCount++;
			}
			//����״̬�Ĺ����ڣ�Ҳ����Ϊ�Ǵ������ڳ��״̬
			if(chargingCount<250){
				chargingCount++;
			}
			
		}
		if(chargingRangeCount>7||everage>=configValueArr[CONFIG_BATFULL_UP]){
			emptyCount=0;
			fullCount=0;
			if(chargingCount<250){
				chargingCount++;
			}
			
		}
		if(emptyCount>3){
			oldBatState=batState; //todoӦ��batState������ֵ�����趨oldBatState״̬
			batState=UNIT_STATE_EMPTY;
			clearTime();
		}
		else if(fullCount>20){
			chargingCount=0; //����״̬�Ĺ����ڽ��������϶��������״̬
			oldBatState=batState;
			batState=UNIT_STATE_FULL;
		}
 		else if(chargingCount>3){
			oldBatState=batState;
			batState=UNIT_STATE_CHARGING;
		}

		else {
			batState=oldBatState;
		}
		
	
}

unsigned int exCount;
void startSettingIfNecessary(){
    if(startSetting==1){
		 
		startSetting=0;
		setState=SET_STATE_INIT; //��������setInfraredFlag()��������
	    inSetting=1;
		ClrScreen();
		LCD_PutString(3,0,"����");
		setInfraredFlag();
	
		
	}
	exCount=0; //�κ�����¶����������ⲿ�ն˼�������ֹ���ŵ��źš�
}


void EX0_ISR (void) interrupt 0 
{
   //50msģʽ��          
  	  	 
 /* if(!inSetting){
  	
     exCount++;
	  if(exCount>=60){  //��ΰ�������Ч�����������
	  	  exCount=0;
	      startSetting=1;
	  }
	  return;
	
  }
  else
  {
 	irExProcess();
  }
 */
  //600usģʽ��
 irExProcess();
}


/*
void Timer0_isr(void) interrupt 1 using 1
{
	if(isInfraredAllowed==1){
		irTime0Process();
	}
	else{
	  //50ms
	  TL0 = 0x00;		//���ö�ʱ��ֵ
	  TH0 = 0x4C;		//���ö�ʱ��ֵ
	  yuyinTimeCount++;
 	  if(batState==UNIT_STATE_CHARGING||batState==UNIT_STATE_FULL){
		msCount++;
		if(msCount>=1200){//50ms*20*60
			msCount=0;
			mCount++;
			if(mCount>=1440){//60*24
				mCount=0;
			}
		}
	  }
	}

	
}
*/
//ͳһʹ��600us��ʱ
//�漰�޸Ĵ���	1��Timer0_isr 2��init��ʱ�� 3��config��clearInfraredFlag()
//ͬʱ�޸Ĵ򿪺������÷�ʽ �漰�޸Ĵ�  1��void EX0_ISR (void) interrupt 0 2��  config��processSettingIfNecessary() 3��Ir_work�м���IR_CHANNEL��֧
uint ms50Count=0;
void Timer0_isr(void) interrupt 1 using 1
{
	
	irTime0Process();
	//�Ĵ���������irTime0Process��
	if(ms50Count++>=83){   //Լ51ms
		ms50Count=0;
		yuyinTimeCount++;
		if(batState==UNIT_STATE_CHARGING||batState==UNIT_STATE_FULL){
		msCount++;
		if(msCount>=1200){//50ms*20*60
			msCount=0;
			mCount++;
			if(mCount>=1440){//60*24
				mCount=0;
			}
		}
		}
	}


	
}
void dispLockInfo(bit dis){
	if(configValueArr[CONFIG_DEBUG]==0)
	{
		if(dis==1){
			LCD_PutString(4,2,"����");		 
		}
		else{
			LCD_PutString(4,2,"  ");		
		}
	}
	else{
	   	if(dis==1){
	
			LCD_PutSingleByteString(12,1,"1");
 
		}
		else{
	
			LCD_PutSingleByteString(12,1,"0");

		}
	}
}
void lockIfNeccessary(){
	if(lockFlag==1){
		lockFlag=0;
		dispLockInfo(1);
		pLock=1;
		delayMs(1);
		pLock=0;
		//delayMS(1500);//�����Կ���
		delayMs(2000);
		pLock=1;
		dispLockInfo(0);
	}
}
void yuyinIfOk(uchar sel,bit instant){
	if(instant||(yuyinTimeCount>=YUYIN_TIME_COUNT_THRESHOLD)){
		yuyin(sel);
		yuyinTimeCount=0;
	}
/*	else if(yuyinTimeCount>=YUYIN_TIME_COUNT_THRESHOLD){
	
	}
*/	
}
#define YUYIN_MENYIDAKAI 4
#define YUYIN_QINGGUANMEN 2
void yuyinIfNeccessary(){
	if(tipFlag==1){
		if(oldTipFlag!=tipFlag){	
			yuyinIfOk(YUYIN_MENYIDAKAI,1);
		}
		else{
		   yuyinIfOk(YUYIN_QINGGUANMEN,0);
		}
	}
}

void main()
{

	retreiveInfo();
	
	init();

	redisplayStaffInfoIfNecessary(1);
 	while(1){
		tready=1;
  		rready=1;
		getAdValue();
		yuyinIfNeccessary();
		
		updateStaffInfoIfNeccesary();
//		redisplayStaffInfoIfNecessary(0);
		if(configValueArr[CONFIG_DEBUG]>0){
			
			displayADC();
		}
		stateTest();
		displayBatState();
		showTimeIfNeccesary();
//		keyScan();
		startSettingIfNecessary();
		processSettingIfNecessary();
		lockIfNeccessary();
	}
}

/*void  main(void)
{ 

	uchar xdata str[] = "������װ",str2[] = "μ�Ϲ��Ƽ�";
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


