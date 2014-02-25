#include "hanzi.h"
//---------------------------------------------------------
//函数名称：u8 GB_HZZF_len(u8 *hzStr)
//说明：    由汉字内码计算汉字内码占用的字节数
//参数说明：hzStr 汉字内码指针
//返回：	获得内码所占用的字节数
//---------------------------------------------------------
//---------------------------------------------------------
u8 GB_HZZF_len(u8 *hzStr)//获得GB汉字所占用的字节数
{
	u8 c1,c2;
	c1 = *hzStr++;
	if( c1 < 0x20 )
		return(0);
	if( c1 < 0x80)
		return(1);
	c2= *hzStr++;
	if( (c1==0x81 && c2>=0x39)||(c1==0x82) )
		return(4);
	return(2);

}
//----------------------------------------------------------
//----------------------------------------------------------
//函数名称：u32  GB_ADDR(u8 *HZ,u8 HZ_type)
//说明：    由汉字内码计算汉字地址
//参数说明：
//			HZ_type  支持8点，12点，16点，24点，32点汉字
//			HZ       内码可以是单字节，双字节，四字节
//			BaseAddr 该类型的点阵的起始地址
//返回：	汉字点阵在字库中的绝对地址
//----------------------------------------------------------
u32  GB_ADDR(u8 *HZ,u8 HZ_type)//获得汉字点阵的地址
{
	u32 temp;
	u8 c1,c2;
	if(HZ[0]<0x80)//ASCII ,半角
	{

            if( HZ[0] < ' ' )
				HZ[0] = ' ';
			temp = HZ[0] - ' ';
			if( HZ_type == TYPE_8 )
				temp = temp*8 +  ASC0808YZF_ADDR;
			else if( HZ_type == TYPE_12 )
				temp = temp*12 + ASC0812YZF_ADDR;
			else if( HZ_type == TYPE_16 )
				temp = temp*16 + ASC0816YZF_ADDR;
			else if( HZ_type == TYPE_24 )
				temp = temp*48 + ASC1224YZF_ADDR;
			else if( HZ_type == TYPE_32 )
				temp = temp*64 + ASC1632YZF_ADDR;

	}
    else //chinese  for BG2312,GBK,GB18030,GB12345
    {
        	c1=HZ[0];
        	c2=HZ[1];
       		//符号区,全角 846+96 = 1038
        	if( (c1>=0xA1 && c1 <= 0xAB && c2>=0xa1)||(c1>=0xa8 && c1 <= 0xa9 && c2<0xa1) )
        	{
                if( c1>=0xA1 && c1 <= 0xAB && c2>=0xa1 )//双字节字符1区 846
					temp = (c1 - 0xA1) * 94 + (c2 - 0xA1);
				else									//双字节字符5区 96
					{if(c2>0x7f) c2--;temp=(c1-0xa8)*96 +(c2-0x40)+846;}

                if( HZ_type == TYPE_12 )
					return( temp *24 + ZF1212Y_ADDR);
				else if( HZ_type == TYPE_16 )
					return( temp *32 + ZF1616Y_ADDR);
				else if( HZ_type == TYPE_24 )
					return( temp *72 + ZF2424Y_ADDR);
				else if( HZ_type == TYPE_32 )
					return( temp *128 + ZF3232Y_ADDR);
				else
					return(0x00);
        	}

        	//GBK双字节2区 6768
        	else if(c1>=0xb0 && c1 <= 0xf7 && c2>=0xa1)
        	{
                	temp= (c1 - 0xB0) * 94 + (c2 - 0xA1);
        	}
		if( HZ_type == TYPE_12 )
			temp = (temp+846-184)*24 + ZF1212Y_ADDR;
		else if( HZ_type == TYPE_16 )
			temp = (temp+846-184)*32 + ZF1616Y_ADDR;
		else if( HZ_type == TYPE_24 )
			temp = (temp+846-184)*72 + ZF2424Y_ADDR;
		else if( HZ_type == TYPE_32 )
			temp=(temp+846-184)*128+ZF3232Y_ADDR;
		else
			return(0x00);
	}
	return temp;
}
