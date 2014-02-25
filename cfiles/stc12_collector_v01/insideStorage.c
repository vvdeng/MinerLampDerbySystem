#include <reg52.h>  
#include<intrins.h>
#include "insideStorage.h"

void IapIdle()
{
      IAP_CONTR = 0;       
      IAP_CMD = 0;              
      IAP_TRIG = 0;                       
      IAP_ADDRH = 0x80;                  
      IAP_ADDRL = 0;                      
}

BYTE IapReadByte(WORD addr)
{
      BYTE dat;                           
      IAP_CONTR = ENABLE_IAP;   
      IAP_CMD = CMD_READ;         
      IAP_ADDRL = addr;                 
      IAP_ADDRH = addr >> 8;        
      IAP_TRIG = 0x5a;                  
      IAP_TRIG = 0xa5;                    
      _nop_();                           
          
      dat = IAP_DATA;              
      IapIdle();                          
      return dat;                          
}
void IapProgramByte(WORD addr, BYTE dat)
{
  IAP_CONTR = ENABLE_IAP;          
      IAP_CMD = CMD_PROGRAM;     
      IAP_ADDRL = addr;                  
      IAP_ADDRH = addr >> 8;             
      IAP_DATA = dat;                    
      IAP_TRIG = 0x5a;                   
      IAP_TRIG = 0xa5;                    
      _nop_();                            
         
      IapIdle();
}
void IapEraseSector(WORD addr)
{
      IAP_CONTR = ENABLE_IAP;   
      IAP_CMD = CMD_ERASE;       
      IAP_ADDRL = addr;                
      IAP_ADDRH = addr >> 8;          
      IAP_TRIG = 0x5a;                   
      IAP_TRIG = 0xa5;                    
      _nop_();                            
          //operation complete
      IapIdle();
}
