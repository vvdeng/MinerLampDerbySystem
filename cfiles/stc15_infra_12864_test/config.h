#ifndef _CONFIG_H_
#define _CONFIG_H_
#include <reg52.h>
#include "useful.h"
#include "eeprom.h"
#define IR_CHANNEL_MINUS 0x45
#define IR_CHANNEL 0x46
#define IR_CHANNEL_ADD 0x47
#define IR_PREV 0x44
#define IR_NEXT 0x40
#define IR_PLAY 0x43
#define IR_VOL_ADD 	0x15
#define IR_VOL_MINUS 0x07
#define IR_EQ 0x09
#define IR_ZERO 0x16
#define IR_ONE 0x0C
#define IR_TWO 0x18
#define IR_THREE 0x5E
#define IR_FOUR 0x08
#define IR_FIVE 0x1C
#define IR_SIX 0x5A
#define IR_SEVEN 0x42
#define IR_EIGHT 0x52
#define IR_NINE 0x4A
#define IP_100 0x19
#define IP_200 0x0D
#define CONFIG_ADDR 0
#define CONFIG_INTEVAL 1
#define CONFIG_DEBUG 2
#define CONFIG_EXIT 3
#define SET_STATE_NONE 0
#define SET_STATE_MENU 1
#define SET_STATE_MENU_ITEM_SEL 2
#define MENU_LEN 4
#define CONFIG_LEN 4
#define SEG_COUNT 4
#define DOT_POS_NONE 255
#define CONFIG_ROM_ADDR 0x00
#define VAL_LEN 2
extern uchar IRcord[4];
void displayShuma(uchar digiArr[],uchar len);
void Ir_work(void);
void Ircordpro(void);
void saveConfig();
void retreiveConfig();
void processSettingIfNecessary();
void irTime0Process();
void irExProcess();
void irInit();//未打开总中断
#endif