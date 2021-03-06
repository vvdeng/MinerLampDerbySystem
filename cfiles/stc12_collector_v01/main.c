#include <reg52.h>
#include<intrins.h>
#include "insideStorage.h"
#include "useful.h"
#include "lcdhz12864.h"
#include "config.h"
#define DATA_PRE 0x50 //二进制0101作为数据前导
#define DATA_PRE_UPDATE_RACK_STAFF 0x60 //0110 更新员工信息前导
#define  _Nop()  _nop_()

sfr WDT_CONTR = 0xc1; //看门狗地址
              
sfr   S2CON   = 0x9a;                
sfr   S2BUF   = 0x9b;                 
sfr   BRT     = 0x9c;
sfr IPH    = 0xB7;                  
sfr   IE2     = 0xaf;  //中断允许寄存器2 最低位为ES2            
#define   S2RI    0x01                 
#define   S2TI    0x02                
               

sbit dispEnableIO=P3^5;


//sbit SEL_INH=P4^4;
//sbit LED4=P4^5;
//指示灯
sbit LED_TXD1 =P1^0;

sbit LED_RXD1 =P1^1;
sbit LED_TXD2 =P3^3;

sbit LED_RXD2 =P3^4;
//sbit ledTest =P1^1;
sbit k0=P3^5;
//串口1 485读写控制
sbit RW_485_1=P3^6;
sbit RW_485_2=P3^7;

/*sbit k1=P1^4;
sbit k3=P1^5;
sbit k5=P1^6;
sbit k7=P1^7;
*/
sbit SEL_A=P1^4;
sbit SEL_B=P1^5;
sbit SEL_C=P1^6;
sbit SEL_D=P1^7;
sbit LED_TEST=P2^7;
sbit LED_TEST2=P2^7;

#define UNIT_STATE_EMPTY  1
#define UNIT_STATE_CHARGING 2
#define UNIT_STATE_FULL  3
#define UNIT_STATE_ERROR  4
#define UNIT_STATE_OFFLINE 5

#define RECEIVE 0x01
#define SEND 0x02
#define SENTENCE_MAX_NUM 6
#define SENTENCE_LEN  32
//自定义命令格式：xxxd dddd  髙3位表示命令，后5位表示灯架地址
#define CMD_OPEN_DOOR 0xE0
#define CMD_UPDATE_STAFF_INFO 0xC0
#define CMD_REQ_UNIT_INFO 0x80
#define CMD_BRORDCAST 0xf0
#define CMD_SEND_STA 0x80
#define SYM_DATA_END 0xff

#define COMM_STATE_NOTHING 0
#define COMM_STATE_REQ_UNIT_INFO 1
#define COMM_STATE_REQ_UNIT_INFO_RECEIVE 2
#define COMM_STATE_UPDATE_STAFF 3
#define COMM_STATE_BROADCAST_MESSAGES 4
#define COMM_STATE_BROADCAST_MESSAGES_PREPARE_STEP_1 5
#define COMM_STATE_BROADCAST_MESSAGES_PREPARE_STEP_2 6
#define COMM_STATE_BROADCAST_MESSAGES_PREPARE_STEP_3 7
#define COMM_STATE_REQ_RACK_UNITS_INFO 8
#define COMM_STATE_UPDATE_RACK_STAFF 9
#define COMM_STATE_SEND_STA 10
#define COMM_STATE_OPEN_DOOR 11
#define COMM_STATE_BROADCAST_PREPARE_1 0x01
#define COMM_STATE_BROADCAST_PREPARE_2 0x03
#define COMM_STATE_BROADCAST_PREPARE_3 0x05

#define DISPLAY_MESSAGE 0
//#define DISPLAY_STATISTICS 1

uchar  curSentenceIndex,changeMessageTimer=0,currentUnitIndex=1,currentUnitTotalIndex,commState1,commState2,reqUnitInfoInteval,oldCommState=COMM_STATE_NOTHING,staffBufIndex=0,oldTH0=0,oldTL0=0,oldGroup=255,retryCount=0,reqStateTimeout=0,lockAddr=0;
uint messagesBufIndex=0,reqStateTimer=0;
bit changeMessageFlag=0,readStateFlag=0,receiveMessagesFlag=0,updateRackStaffFlag=0,inAddrSetting=0,isInfraredAllowed=0,totalRefreshed=0,lockFlag=0;
uchar xdata unitStateArr[101],tempUnitStateArr[101];


#define LOCAL_ADDRESS_ADDR  0x0
#define UNITS_NUM_ADDR  0x2
#define MESSAGE_NUM_ADDR  0x4
#define MESSAGE_INTEVAL_ADDR  0x6
#define TIMEOUT_THRESHOLD_ADDR  0x8
#define MESSAGE_LIST_ADDR  0x100

#define UNIT_STATE_NULL  0
#define UNIT_STATE_EMPTY  1
#define UNIT_STATE_CHARGING 2
#define UNIT_STATE_FULL  3
#define UNIT_STATE_ERROR  4

uchar xdata stateStat[6]={0};//索引0未用（巡检总数） ，1 表示矿灯取走 2表示矿灯充电 3表示矿灯充满 5表示断线

uchar xdata messageBuf[SENTENCE_MAX_NUM][SENTENCE_LEN+1]={'\0'};
uchar tempMessageNum=0;
uchar* xdata menu[]={"地址设置","灯架设置","红外设置","采集间隔"};

bit tready,tready2,rready,rready2;
bit isSelected;

#define FIELD_COUNT 3
#define STAFF_INFO_FIELD_LENGTN 8
#define MESSAGE_BUF_LEN 400
uchar xdata staff_buf[FIELD_COUNT][STAFF_INFO_FIELD_LENGTN];
uchar xdata messagesBuf[MESSAGE_BUF_LEN]={'\0'};
uchar xdata staffBuf[60];

uchar queryStateInteval=5,queryUnitsNum=20,groupNum=0,groupIndex;
void displayCurrentLedMessage(bit isFirst);


/*
//串口2输出数据：
void OutByteS2(uchar k)
{
	S2BUF=k;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;
}
//串口2读取数据
uchar GetByteS2(void)
{
	while(!(S2CON&S2RI));
	S2CON&=~S2RI;
	return S2BUF;			
}
uchar GetByteS2Instance(void)
{	
	delayMs(50);
	if(S2CON&S2RI){
		S2CON&=~S2RI;
		return S2BUF;
	}
	else{
		return 4;//表示无响应
	}	
}*/
void switchIfNecessary(uchar lampNo){
	uchar group=(lampNo-1)/20;//每二十个一组 cd4067
//	if(group==oldGroup){
//		return;
//	}
	oldGroup=group;

	SEL_A=(group&0x01);
	SEL_B=((group>>1)&0x01);
	SEL_C=((group>>2)&0x01);
	SEL_D=((group>>3)&0x01);

	
}
void sendCmdAndAddrInstance(uchar cmd,uchar addr){
//	switchIfNecessary(addr);
	RW_485_2=1;

	
	S2BUF=cmd;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;

	S2BUF=addr;

	while(!(S2CON&S2TI));
	S2CON&=~S2TI;
	
	RW_485_2=0;
}

void sendDataInstance(uchar ldata,uchar pre){
	LED_TXD2=~LED_TXD2;
	RW_485_2=1;
		
	S2BUF=((ldata>>4)&0x0f)|pre;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;

	S2BUF=(ldata&0x0f)|pre;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;
	
	RW_485_2=0;
	
	
}
void sendDataWithoutSplitInstance(uchar ldata){
		LED_TXD2=~LED_TXD2;
	RW_485_2=1;
		
	S2BUF=ldata;
	while(!(S2CON&S2TI));
	S2CON&=~S2TI;

	
	RW_485_2=0;
}
void sendCmdInstance(uchar ldata){
	sendDataWithoutSplitInstance(ldata);	
}
/*
void sendData(uchar ldata){
	RW_485_2=1;
	S2BUF=ldata;
}*/
/*
void sendCmdAndAddr(uchar cmd,uchar addr){
	uchar temp;


	RW_485_2=1;
//	temp=cmd;
	S2BUF=temp;
//	S2CON&=~S2TI;
	while(!(S2CON&S2TI));

	RW_485_2=1;
	temp=addr;
	if(temp==addr&&(addr==3)){
			LED_RXD1=~LED_RXD1;
		}
		if(temp==3){
			LED_TXD1=~LED_TXD1;
		}
	S2BUF=addr;

//	S2CON&=~S2TI;
	while(!(S2CON&S2TI));
	RW_485_2=0;
}
void sendData(uchar ldata){
	
	RW_485_2=1;
		
	S2BUF=(ldata>>4)&0x0f;
	S2CON&=~S2TI;
	while(!(S2CON&S2TI));
	S2BUF=ldata&0x0f;
	S2CON&=~S2TI;
	while(!(S2CON&S2TI));
	RW_485_2=0;
	
	
}*/
void reqCurrentUnitInfo(){
	
	if(commState2==COMM_STATE_UPDATE_STAFF){
		return;
	}
	if(currentUnitIndex<=queryUnitsNum){
		LED_TXD2=0;
		currentUnitTotalIndex=groupIndex*queryUnitsNum+currentUnitIndex;
		TR0=0;

		IE2 = 0x00;
		switchIfNecessary(currentUnitTotalIndex);
		sendCmdAndAddrInstance(CMD_REQ_UNIT_INFO,currentUnitTotalIndex);
	




		IE2 = 0x01;
		if(isInfraredAllowed==0){
			TL0 = 0xD7;		//设置定时初值   16位 600us 误差1%
			TH0 = 0xFD;		//设置定时初�
			
		}
		reqUnitInfoInteval=0;

		TR0=1;
	

	}
	else{
	//	readStateFlag=0;
		groupIndex++;
		if(groupIndex==groupNum){
			groupIndex=0;
		//	totalRefreshed=1;

		}
		//每结束20个就更新下充电状态
		totalRefreshed=1;
		commState2=COMM_STATE_NOTHING;
		currentUnitIndex=1;
		LED_TXD2=1;
		LED_RXD2=1;
		TR0=1;
	
	}
}
void reqNextUnitInfo(){
	currentUnitIndex++;
	reqCurrentUnitInfo();
}

void S2INT() interrupt 8
{
	uchar temp;		
	if(S2CON&S2RI)
	{	

		S2CON&=~S2RI;
		temp=S2BUF;
	
		if(commState2==COMM_STATE_REQ_UNIT_INFO)
		{
		//	LED_RXD2=~LED_RXD2; 
			LED_RXD2=0; //接收到数据改为常量，不闪烁
			if((temp&0xf0)!=DATA_PRE){
				if(retryCount<10)//最多尝试10次
				{
					retryCount++;
					reqCurrentUnitInfo();
					
				}
				else{
					//当前充电架状态保持不变，请求下一个状态
					retryCount=0;
					reqNextUnitInfo();
				}
			}
			else{
			reqStateTimeout=0;
			tempUnitStateArr[currentUnitTotalIndex]=(temp&0x0f);
						
			reqNextUnitInfo();
		   }
		}
		
	}
	if(S2CON&S2TI)
	{
		
/*		if(commState2==COMM_STATE_REQ_UNIT_INFO)
		{
			
			
		}
		else if(commState2==COMM_STATE_UPDATE_STAFF)
		{
		
		}
*/	
		S2CON&=~S2TI;
		RW_485_2=0;

	}

}



void makeInfo() {
	uchar m;
	unitStateArr[0]=configValueArr[CONFIG_ADDR];
	for (m = 1; m <= configValueArr[CONFIG_UNIT_NUM]; m++) {
	//	unitStateArr[m] = (unitStateArr[m]+1)%4;
		unitStateArr[m] = 0;
	}
}

//自定义命令格式：xxxd dddd  髙3位表示命令，后5位表示灯架地址 最多32位 0e0
// 接收命令：广播1000 0000 ；获取充电状态1010 00000；更新员工信息1100 0000；通信正常结束 1110 0000
//发送命令： 获取充电状态1000 0000 更新员工信息1100 0000
bit isBroadCast4Addr() {
	return (SBUF&0xe0)==0x80;
}
bit isReqUnitInfo() {
	return (SBUF&0xe0)==0xA0;
}

bit isUpdateStaffInfo() {
	return (SBUF&0xe0)==CMD_UPDATE_STAFF_INFO;
}

bit isOpenDoorCmd(){
	return (SBUF&0xe0)==CMD_OPEN_DOOR;
}

void sendUnitInfo(){
	uchar m;
	if(tready==0){ //发送忙碌
		RW_485_1=1;
		SBUF=0x80;
		while(TI!=1);
		TI=0; 
		RW_485_1=0;
	}
	else{
		tready=0 ;
	//	makeInfo();
	//	reqUnitInfo();
		{
			//发送地址,不加数据前缀
			RW_485_1=1;
			SBUF=(unitStateArr[0]);	
	    	while(TI!=1);
		    TI=0; 
			RW_485_1=0;
		}
	 	for(m=1;m<=configValueArr[CONFIG_UNIT_NUM];m++) 
	 	{
			LED_TXD1=~LED_TXD1;
			RW_485_1=1;
			SBUF=(unitStateArr[m]|DATA_PRE);	
	    	while(TI!=1);
		    TI=0; 
			RW_485_1=0;
	 	}
		tready=1;
		LED_TXD1=1; //指示灯灭
	}
}
void prepareUpdateStaffInfo(void){
	RW_485_2=1 ;
	oldCommState=commState2;
	commState2=COMM_STATE_UPDATE_STAFF;	
}
void endUpdateStaffInfo(void){
	commState2=oldCommState;
	if(commState2==COMM_STATE_REQ_UNIT_INFO){
		reqCurrentUnitInfo();
	}
}

void makeUpdateStaff(){
	
	uchar m,n,lampNo,tempStaffBufIndex=0;

	

				lampNo = (staffBuf[tempStaffBufIndex++] & 0x0f) << 4;
				lampNo += (staffBuf[tempStaffBufIndex++] & 0x0f);
	
		for (m = 0; m < FIELD_COUNT; m++) {

			for (n = 0; n < STAFF_INFO_FIELD_LENGTN; n++) {

				staff_buf[m][n] = (staffBuf[tempStaffBufIndex++] & 0x0f) << 4;
				staff_buf[m][n] += (staffBuf[tempStaffBufIndex++] & 0x0f);
			}

		}	
		
		prepareUpdateStaffInfo();
	//	LED_TXD1=0;
		switchIfNecessary(lampNo);
		sendCmdAndAddrInstance(CMD_UPDATE_STAFF_INFO,lampNo);


		
		for (m = 0; m < FIELD_COUNT; m++) {

			for (n = 0; n < STAFF_INFO_FIELD_LENGTN; n++) {
				sendDataInstance(staff_buf[m][n],DATA_PRE_UPDATE_RACK_STAFF);
				
			}

		}
			
		
		endUpdateStaffInfo();
	//	LED_TEST=~LED_TEST;


}

void storageBaseInfo(){
	uchar m,n;
	IapEraseSector(IAP_BASEINFO_ADDRESS); 
	
    for (m = 0; m < configValueArr[CONFIG_MSG_NUM]; m++) {
		for (n = 0; n < SENTENCE_LEN; n++) {
			IapProgramByte(IAP_BASEINFO_ADDRESS+MESSAGE_LIST_ADDR+m*SENTENCE_LEN+n, messageBuf[m][n]);
		}
	}
}
void storageInfo(){

	saveConfig();
	storageBaseInfo();

}
void prepareSendMessage(void){
//	RW_485_2=1 ;
	oldCommState=commState2;
	commState2=COMM_STATE_BROADCAST_MESSAGES;	
}
void endSendMessage(void){
	commState2=oldCommState;
	if(commState2==COMM_STATE_REQ_UNIT_INFO){
		reqCurrentUnitInfo();
	}
}
void sendLedMessages(){
	uint m=0,n=0;
	TR0=0;
	IE2 = 0x00;
	ES=0; 				
	switchIfNecessary(101);//文字广播在第10组 //20个一组后为第5组 索引从0开始

	sendCmdInstance(0xf0);
	sendDataWithoutSplitInstance(COMM_STATE_BROADCAST_PREPARE_1);
	sendDataWithoutSplitInstance(COMM_STATE_BROADCAST_PREPARE_2);
	sendDataWithoutSplitInstance(COMM_STATE_BROADCAST_PREPARE_3);
	for (m = 0; m < MESSAGE_BUF_LEN; m++) {
		sendDataWithoutSplitInstance(messagesBuf[m]);
	}

	LED_TXD2=1;//指示灯灭
	TR0=1;
	IE2 = 0x01;
	ES=1; 

}
void makeMessages(){
	uchar m,n,tempMessageInteval,tempBufIndex=2;//0,1存储消息数，已获取
	
	
		configValueArr[CONFIG_MSG_NUM]=tempMessageNum;

		tempMessageInteval = (messagesBuf[tempBufIndex++] & 0x0f) << 4;
	
		tempMessageInteval+= (messagesBuf[tempBufIndex++]  & 0x0f);
	
		for (m = 0; m < configValueArr[CONFIG_MSG_NUM]; m++) {

			for (n = 0; n < SENTENCE_LEN; n++) {
				
				messageBuf[m][n] = (messagesBuf[tempBufIndex++] & 0x0f) << 4;
	
				messageBuf[m][n] += (messagesBuf[tempBufIndex++] & 0x0f);
			}

		}	
	configValueArr[CONFIG_MSG_INTEVAL]=tempMessageInteval;
    if(configValueArr[CONFIG_DEBUG]==DISPLAY_MESSAGE)
	{
		displayCurrentLedMessage(1);
	}
	storageInfo();
	prepareSendMessage();
	sendLedMessages();
	endSendMessage();
}


void ssio(void)
interrupt 4
{
          
	if(RI){
         RI=0;

	 	if(isBroadCast4Addr()){
			commState1=COMM_STATE_BROADCAST_MESSAGES_PREPARE_STEP_1;
			return;
		}

	 	if(isReqUnitInfo()&&((SBUF&0x1F)==configValueArr[CONFIG_ADDR]))
		{
			commState1=COMM_STATE_REQ_RACK_UNITS_INFO;

			TR0=0;
			ES=0; 
	//		P1=255-4;
	//		SBUF=1;
	//		while(TI!=1);
	//	    	TI=0; 
	//		
			sendUnitInfo();
			commState1=COMM_STATE_NOTHING;
			
			TR0=1;
			ES=1; 
			return;
		}
	 	if(isUpdateStaffInfo()&&((SBUF&0x1F)==configValueArr[CONFIG_ADDR])){
			commState1=COMM_STATE_UPDATE_RACK_STAFF;
			LED_TEST2=~LED_TEST2;
			staffBufIndex=0;
			return;
		}
	 	if(isOpenDoorCmd()&&((SBUF&0x1F)==configValueArr[CONFIG_ADDR])){
		//	ledTest=0;
			commState1=COMM_STATE_OPEN_DOOR;
			LED_TEST2=~LED_TEST2;
			
			return;
		}
		if((commState1==COMM_STATE_BROADCAST_MESSAGES_PREPARE_STEP_1)){
			if(SBUF==COMM_STATE_BROADCAST_PREPARE_1){
				commState1=COMM_STATE_BROADCAST_MESSAGES_PREPARE_STEP_2;
			}
			else{
				commState1=COMM_STATE_NOTHING;
			}
			return;
		}
		if((commState1==COMM_STATE_BROADCAST_MESSAGES_PREPARE_STEP_2)){
			if(SBUF==COMM_STATE_BROADCAST_PREPARE_2){
				commState1=COMM_STATE_BROADCAST_MESSAGES_PREPARE_STEP_3;
			}
			else{
				commState1=COMM_STATE_NOTHING;
			}
			return;
		}
		if((commState1==COMM_STATE_BROADCAST_MESSAGES_PREPARE_STEP_3)){
			if(SBUF==COMM_STATE_BROADCAST_PREPARE_3){
				commState1=COMM_STATE_BROADCAST_MESSAGES;
				messagesBufIndex=0;
				tempMessageNum=0;
			}
			else{
				commState1=COMM_STATE_NOTHING;
			}
			return;
	
		}
		if(commState1==COMM_STATE_BROADCAST_MESSAGES){
			LED_RXD1=~LED_RXD1;
			messagesBuf[messagesBufIndex++]=SBUF;
			if(messagesBufIndex==2){
				tempMessageNum=(((messagesBuf[0]<<4)&0x0f)+messagesBuf[1]);

			}
			if(messagesBufIndex>=(tempMessageNum*SENTENCE_LEN+2)*2){
				LED_RXD1=1;//指示灯灭
				receiveMessagesFlag=1;
			}
		
		}else if(commState1==COMM_STATE_UPDATE_RACK_STAFF){
			if((SBUF&0xf0)!=DATA_PRE_UPDATE_RACK_STAFF){
				commState1=COMM_STATE_NOTHING;
				return;
			}
			staffBuf[staffBufIndex++]=SBUF;
			
			if(staffBufIndex>=(FIELD_COUNT*STAFF_INFO_FIELD_LENGTN+1)*2){
				LED_TEST=~LED_TEST;
				TR0=0;
				IE2 = 0x00;
				ES=0; 		
				makeUpdateStaff();
				commState1=COMM_STATE_NOTHING;
				LED_TXD2=1;//指示灯灭
				TR0=1;
				IE2 = 0x01;
				ES=1; 
			}
		}
		else if(commState1==COMM_STATE_OPEN_DOOR){
			 	TR0=0;
				IE2 = 0x00;
				ES=0;
				lockAddr=SBUF; 		
				switchIfNecessary(lockAddr);
				sendCmdAndAddrInstance(CMD_OPEN_DOOR,lockAddr);

				commState1=COMM_STATE_NOTHING;
				LED_TXD2=1;//指示灯灭
				TR0=1;
				IE2 = 0x01;
				ES=1; 
		}

               
	}
	else if(TI){                       
		TI=0;
	}

}


void showAddress(uchar x,uchar y){
	uchar localAddressArr[3];
	configValueArr[CONFIG_ADDR]=configValueArr[CONFIG_ADDR]%100;
	localAddressArr[0]='0'+(configValueArr[CONFIG_ADDR]/10);
	localAddressArr[1]='0'+(configValueArr[CONFIG_ADDR]%10);
	localAddressArr[2]='\0';
	LCD_PutString(x,y,localAddressArr);
}
char * itostr(uchar num,char * str, uchar strLen){
	uchar m;
 	for(m=0;m<strLen;m++){
		str[m]='0';
 	}
	while (num!=0){
	    str[--m]='0'+(num%10);
		num/=10;
	}
	str[strLen]='\0';
 	return str;
}
char  xdata emptyStat[4]={'\0'};
char  xdata chargingStat[4]={'\0'};
char  xdata fullStat[4]={'\0'};
void displayStatisticsMessage(){
    if(inSetting==1)
		return;
	

	LCD_PutString(0,1,"下井：");
	LCD_PutString(0,2,"充电：");
	LCD_PutString(0,3,"充满：");
	LCD_PutString(4,1,itostr(stateStat[1],emptyStat,3));
	LCD_PutString(4,2,itostr(stateStat[2],chargingStat,3));
	LCD_PutString(4,3,itostr(stateStat[3],fullStat,3));
//  LCD_PutString(0,1,itostr(stateStat[1],emptyStat,3));
//	LCD_PutString(0,2,itostr(stateStat[2],chargingStat,3));
//	LCD_PutString(0,3,itostr(stateStat[3],fullStat,3)); 
//	LCD_PutString(4,1,itostr(stateStat[5],chargingStat,3));
//	LCD_PutString(4,2,itostr(stateStat[0],fullStat,3));
	LCD_PutString(5,4,"灯架");
	showAddress(7,4);
}
void displayLEDMessage(uchar message[],uchar len){
	uchar m,indexi=0,indexj=0,count=0,isChinese=0,engliseAl=0,totalPrintCount=0;
	uchar xdata displayArray[4][10]={'\0'};

	for(m=0;m<len;m++){
		if((isChinese==0)&&(message[m]&0x80)==0x80){
			if(engliseAl%2!=0){
				displayArray[indexi][indexj++]=' ';
				count++;
				totalPrintCount++;
				engliseAl=0;
				m--;//重新读取该字节
			}
			else
			{
				displayArray[indexi][indexj++]=message[m];
				isChinese=1;
			}
		}
		else{
			if(isChinese==0){
				engliseAl++;
			}
			displayArray[indexi][indexj++]=message[m];
			count++;
			isChinese=0;
			totalPrintCount++;
		}
		if(totalPrintCount==len){
			displayArray[indexi][indexj]='\0';
		}
		if(count==4){
			displayArray[indexi][indexj]='\0';
			indexi++;
			indexj=0;
			count=0;
		}
		if(indexi>=4){ //最多显示4行
			break;
		}
		
	}
	for(m=0;m<4;m++){
		LCD_PutString(0,m+1,displayArray[m]);
	}
	/*		//调试
			{
			
				uchar firstState[4]={'\0'};
				firstState[0]='0'+(unitStateArr[1]/100);
				firstState[1]='0'+((unitStateArr[1]%100)/10);
				firstState[2]='0'+(unitStateArr[1]%10);
				firstState[3]='\0';
				LCD_PutString(0,4,firstState);
			}
	
	*/
}

void displayCurrentLedMessage(bit isFirst){
		//功能设置中
	if(inSetting==1){
		return;
	}
	
	ClrScreen(); 
	if(configValueArr[CONFIG_MSG_NUM]>0){
		if(isFirst==1){
			curSentenceIndex=0;
		}else{
			curSentenceIndex=(++curSentenceIndex)%configValueArr[CONFIG_MSG_NUM];
		}
		displayLEDMessage(messageBuf[curSentenceIndex],SENTENCE_LEN);
	}
	LCD_PutString(5,4,"灯架");
	showAddress(7,4);
}
void startSettingIfNecessary(){
    if(startSetting==1){
		 
		startSetting=0;

	    inSetting=1;
				
		ClrScreen();
		LCD_PutString(3,1,"设置");
		setInfraredFlag();
		setState=SET_STATE_INIT;
	
		
	}
	
}


void EX0_ISR (void) interrupt 0 
{

 irExProcess();
}
//定时器0 函数
uchar ms50Count=0;
void Timer0_isr(void) interrupt 1 using 1
{
	irTime0Process();
	if(ms50Count++>83)// 约51ms
	{
		ms50Count=0;
	
		if(changeMessageFlag==0){
			changeMessageTimer++;
			if(changeMessageTimer>=20*configValueArr[CONFIG_MSG_INTEVAL]){
			    changeMessageTimer=0;
				changeMessageFlag=1;
			}
		}
	
		if(commState2==COMM_STATE_REQ_UNIT_INFO){
			reqUnitInfoInteval++;
			if(reqUnitInfoInteval==configValueArr[CONFIG_TIMEOUT_THRESHOLD]){
			    reqStateTimeout++;
				if(reqStateTimeout>4){
				   tempUnitStateArr[currentUnitTotalIndex]=UNIT_STATE_OFFLINE;
				   reqNextUnitInfo();
				}
				else{
				   reqCurrentUnitInfo();
				}
			}
		}
	//	else if(readStateFlag==0)//注意！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！1 考虑是否else
		else if(commState2==COMM_STATE_NOTHING)
		{	
			reqStateTimer++;
			if(reqStateTimer>=2*queryStateInteval){	
				reqStateTimer=0;
				readStateFlag=1;
				currentUnitIndex=1;
			}
	/*		else{
				changeMessageTimer++;
				if(changeMessageTimer==20*configValueArr[CONFIG_MSG_INTEVAL]){
					if(configValueArr[CONFIG_MSG_NUM]>0){
						curSentenceIndex=(++curSentenceIndex)%configValueArr[CONFIG_MSG_NUM];
						changeMessageFlag=1;
						changeMessageTimer=0;
					}
				}
			}  
	*/
		}
	 }
  
}
void init() {

	RW_485_1=0 ;
 	RW_485_2=1 ;
 	//定时器0 方式1 ，50ms。
//	SBUF=0x0;
	TMOD |= 0x01;	  		     
	TL0 = 0xD7;		//设置定时初值   16位 600us 误差1%
	TH0 = 0xFD;		//设置定时初值
	ET0=1;           
	TR0=1;   

 	//串口1 方式1 9600		  			
//	SM0 = 0;
//	SM1 = 1;
//	REN = 1;
	SCON  = 0x50;    
	TMOD |= 0x20;
	TH1 = 0xfd;
	TL1 = 0xfd;
	TR1 = 1;

	//串口2 9600bps@11.0592MHz
	AUXR &= 0xf7;		//波特率不倍速
	S2CON = 0x50;		//8位数据,可变波特率
	BRT = 0xDC;		//设定独立波特率发生器重装值
	AUXR |= 0x04;		//独立波特率发生器时钟为Fosc,即1T
	AUXR |= 0x10;		//启动独立波特率发生器
	IE2 = 0x01;     //最低位ES2置0，关闭串口2中断
	
	//12864初始化
	if(dispEnableIO==1){ //是否使用屏，高电平使用，低电平不使用
		dispFlag=1;
	}
	else{
		dispFlag=0;
	}
	LCD_Init();
	

 	IPH=0x10;
	IP=0x10;//设置串口1中断优先级为髙优先级
	ES = 1;

	//外部中断0 初始化 使红外检测生效
	IT0 = 1;   
 	EX0 = 1;
	EA = 1;
	
	isSelected=0;
    
                                      
}
void afterInitAndRetrieveSetting(){
	 groupNum=configValueArr[CONFIG_UNIT_NUM]/queryUnitsNum; 
	 groupIndex=0;
}
/*
	configValueArr[CONFIG_ADDR]=IapReadByte(IAP_ADDRESS+LOCAL_ADDRESS_ADDR);
	if(configValueArr[CONFIG_ADDR]==0xff){
		configValueArr[CONFIG_ADDR]=0;
	}
	
	configValueArr[CONFIG_UNIT_NUM]=IapReadByte(IAP_ADDRESS+UNITS_NUM_ADDR);
	if(configValueArr[CONFIG_UNIT_NUM]==0xff){
		configValueArr[CONFIG_UNIT_NUM] = 100;
	}

	configValueArr[CONFIG_MSG_NUM]=IapReadByte(IAP_ADDRESS+MESSAGE_NUM_ADDR);
	if(configValueArr[CONFIG_MSG_NUM]==0xff){
		configValueArr[CONFIG_MSG_NUM]=0;
	}
	configValueArr[CONFIG_MSG_INTEVAL]=IapReadByte(IAP_ADDRESS+MESSAGE_INTEVAL_ADDR);
	if(configValueArr[CONFIG_MSG_INTEVAL]==0xff){
		configValueArr[CONFIG_MSG_INTEVAL]=10;
	}
	configValueArr[CONFIG_TIMEOUT_THRESHOLD]=IapReadByte(IAP_ADDRESS+TIMEOUT_THRESHOLD_ADDR);
	if(configValueArr[CONFIG_TIMEOUT_THRESHOLD]==0xff){
		configValueArr[CONFIG_TIMEOUT_THRESHOLD]=1;
	}
*/
void retrieveBaseInfo(){
	uchar m,n;


	for (m = 0; m < configValueArr[CONFIG_MSG_NUM]; m++) {
		for (n = 0; n < SENTENCE_LEN; n++) {
			messageBuf[m][n]=IapReadByte(IAP_BASEINFO_ADDRESS+MESSAGE_LIST_ADDR+m*SENTENCE_LEN+n);
		}
	}
} 
void retrieveInfo(){
	retreiveConfig();
	retrieveBaseInfo();
}
uchar xdata oldStateStat[6];
bit send2LEDFlag=0;
void refreshUnitsInfo(){
	uchar m,val;
	//保存采集之前的取走、充电、充满状态，如果采集前后没有变化，则不用更新点阵屏显示内容
	for(m=1;m<4;m++){
		oldStateStat[m]=stateStat[m];
	}
	stateStat[0]=stateStat[1]=stateStat[2]=stateStat[3]=stateStat[4]=stateStat[5]=0;
	for(m=1;m<=configValueArr[CONFIG_UNIT_NUM];m++){
		val=tempUnitStateArr[m];
		unitStateArr[m]=val;
		stateStat[val]++;
	}
	stateStat[0]=m;
	if((oldStateStat[1]==stateStat[1])&&(oldStateStat[2]==stateStat[2])&&(oldStateStat[3]==stateStat[3])){
		send2LEDFlag=0;
	}
	else{
		send2LEDFlag=1;
	}
}
char testNum=0;
void sendSta2LEDIfNecessary(){

	if(send2LEDFlag==1){
	   commState2=COMM_STATE_SEND_STA;
	   	TR0=0;

		IE2 = 0x00;
		switchIfNecessary(121); //20个一组切换到第6组 索引从0开始
		sendCmdInstance(CMD_SEND_STA);
		sendDataWithoutSplitInstance(configValueArr[CONFIG_ADDR]);
		sendDataWithoutSplitInstance(stateStat[1]);
		sendDataWithoutSplitInstance(stateStat[2]);
		sendDataWithoutSplitInstance(stateStat[3]);
		sendCmdInstance(SYM_DATA_END);
		IE2 = 0x01;

		TR0=1;
		commState2=COMM_STATE_NOTHING;
	}
}

void main() {
	//configValueArr[CONFIG_DEBUG]=DISPLAY_MESSAGE;
	  
	init();
	retrieveInfo();
	afterInitAndRetrieveSetting();
	makeInfo();
	if(configValueArr[CONFIG_DEBUG]==DISPLAY_MESSAGE){
		displayCurrentLedMessage(1);
	}
	else{
		ClrScreen();	
	}
	WDT_CONTR = 0x3f;//设置看门狗，溢出时间9.1022s

	while (1) {
		
		tready = 1;
		rready = 1;
		tready2 = 1;
		rready2 = 1;
		if(readStateFlag){
	//		LED_TEST=0;
			readStateFlag=0;
			tready2=0 ;
			commState2=COMM_STATE_REQ_UNIT_INFO;
	//		makeInfo();
			reqCurrentUnitInfo();			
			tready2=1;
	
		}

		if(totalRefreshed==1){
			totalRefreshed=0;
			refreshUnitsInfo();
			sendSta2LEDIfNecessary();

	
		}
		if(receiveMessagesFlag==1){
			receiveMessagesFlag=0;
			commState1=COMM_STATE_NOTHING;
			makeMessages();
		}
		if(configValueArr[CONFIG_DEBUG]==DISPLAY_MESSAGE)
		{
			if(changeMessageFlag==1){
				changeMessageFlag=0;
			//	changeMessageTimer=0;//考虑在displayCurrentLedMessage();之后调用
				displayCurrentLedMessage(0);
			}
		}
		else{
			 displayStatisticsMessage();
		}
		startSettingIfNecessary();
		processSettingIfNecessary();
		WDT_CONTR = 0x3f;//设置看门狗，溢出时间9.1022s

		
	}

}



