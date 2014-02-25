#include "useful.h"
void delayUs(uint t) {
	while (--t)
		;
}

void delayMs(uint t) {

	while (t--) {

		delayUs(245);
		delayUs(245);
		delayUs(245);
		delayUs(245);
	}

}
