
// __stdcall causing error on vs2022 .. the macro puts it out of order
// undef for quick fix, probably a better (correct) way
#ifdef CLAY_DLL_FIX
#define CLAY_DLL
#define __stdcall
#endif

#define CLAY_IMPLEMENTATION
#include "clay.h"

// todo: make optional downcall versions of the 3 upcall methods

#ifdef CLAY_DLL_FIX
#undef __stdcall
#endif

