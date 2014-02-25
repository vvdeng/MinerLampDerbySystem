#include <reg52.h>
#include "useful.h"
//sfr P2M1=0x95;
//sfr P2M0=0x96;
sbit rst=P2^1;
sbit dat=P2^2;
sbit busy=P2^3;
sbit ledTest=P2^0;
void yuyin(){
//	while(busy==0);
	rst=1;
	delayUs(1000);
//	delayMs(1);
	rst=0;
//	delayMs(1);
	delayUs(1000);
//	while(cnt>0){
		dat=1;
//		delayMs(1);
		delayUs(200);
		dat=0;
//		delayMs(1);
		delayUs(200);

		dat=1;
//		delayMs(1);
		delayUs(200);
		dat=0;
//		delayMs(1);
		delayUs(200);
//		cnt--;
//	}

}
void main(){
//	ledTest=0;
//	P2M1=0x08;//0b00001000;
//	P2M0=0x07;//0b00000111;
	  delayMs(3000);
	   ledTest=0;
	   //yuyin();
	while(1){
	 	delayMs(3000);
	//	 ledTest=~ledTest;
		yuyin();
	}
}