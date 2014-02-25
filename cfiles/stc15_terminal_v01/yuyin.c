#include "yuyin.h"
#include "config.h"
void initYuyin(){
	rst=0;
	dat=0;
}
void yuyin(uchar cnt){
	if(configValueArr[CONFIG_SOUND]==0){
		return;
	}
//	while(busy==0);
	rst=1;
	delayUs(1000);

	rst=0;

	delayUs(1000);
	while(cnt>0){
		dat=1;

		delayUs(500);
		dat=0;

		delayUs(500);

		cnt--;
	}

}