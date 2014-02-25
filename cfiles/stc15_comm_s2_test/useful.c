#include "useful.h"
void delayUs(uint t) {
	while (--t)
		;
}

void delayMs(uint t) {

	while (t--) {

		delayUs(1000);
		
	}

}
