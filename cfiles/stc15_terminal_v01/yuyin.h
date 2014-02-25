#ifndef _YUYIN_H_
#define _YUYIN_H_
#include "reg52.h"
#include "useful.h"

sbit rst=P3^6;
sbit dat=P3^5;
void initYuyin();
void yuyin(uchar cnt);
#endif