#ifndef __HANZI_H__
#define __HANZI_H__
#define u8 unsigned char
#define u16 unsigned short
#define u32 unsigned long
// 点阵类型
#define TYPE_8  8
#define TYPE_12 12
#define TYPE_16 16
#define TYPE_24 24
#define TYPE_32 32
//半角ASCII起始地址 (本例不用)
#define ASC0808YZF_ADDR 0x00
#define ASC0812YZF_ADDR 0x00
#define ASC0816YZF_ADDR 0x1A7FCC
#define ASC1224YZF_ADDR 0x1DFF00
#define ASC1632YZF_ADDR 0x1E5A50
//全角字符 起始地址 (已经定义)
#define ZF1212Y_ADDR 0x0000
#define ZF1616Y_ADDR 0x5F4C0
#define ZF2424Y_ADDR 0xDE5C0
#define ZF3232Y_ADDR 0xEDF00
/* 
//汉字的起始地址    (已经定义)
#define HZ1212Y_ADDR ZF1212Y_ADDR+846*24
#define HZ1616Y_ADDR ZF1616Y_ADDR+846*32
#define HZ2424Y_ADDR ZF2424Y_ADDR+846*72
#define HZ3232Y_ADDR ZF3232Y_ADDR+846*128
*/
u8 GB_HZZF_len(u8 *hzStr);
u32  GB_ADDR(u8 *HZ,u8 HZ_type);
#endif
