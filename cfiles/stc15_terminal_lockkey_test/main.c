#include "useful.h"
sfr P5=0xc8;
sfr P4=0xc0;
sfr P5M1=0xc9;
sfr P5M0=0xca;
sbit p55=P5^5;
sbit ledTest=P4^2;//2;
//�ߵ������俪��
void main(){
	P5M1=0x00;
	P5M0=0x20;
	ledTest=0;
	p55=1;
	while(1){
		delayMs(10000);
		ledTest=~ledTest;
		p55=1;
		delayMs(1);
		p55=0;
		//delayMS(1500);//�����Կ���
		delayMs(2000);
		p55=1;

	}
}