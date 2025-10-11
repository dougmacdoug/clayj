//
// Created by macdo on 9/29/2025.
//

#include <string.h>

#ifdef CLAY_DLL_FIX
#define CLAY_DLL
#define __stdcall
#endif

#define CLAY_IMPLEMENTATION
#include "clay.h"


static Clay_ErrorData lastError = {0};

CLAY_DLL_EXPORT void HandleClayErrors(Clay_ErrorData errorData) {
    memcpy(&lastError, &errorData, sizeof(Clay_ErrorData));
}

 void ClearErrors() {
    memset(&lastError, 0, sizeof(Clay_ErrorData));
}

 Clay_ErrorData GetAndClearError() {
    Clay_ErrorData errorData = {0};
    memcpy(&errorData, &lastError, sizeof(Clay_ErrorData));
    memset(&lastError, 0, sizeof(Clay_ErrorData));
    return errorData;
}

#ifdef CLAY_DLL_FIX
#undef __stdcall
#endif

