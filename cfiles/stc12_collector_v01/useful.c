#include "useful.h"
void delayUs(uint t) {
	while (--t)
		;
}

void delayMs(unsigned char t) {

	while (t--) {

		delayUs(245);
		delayUs(245);
	}
}
