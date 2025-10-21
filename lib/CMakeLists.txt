cmake_minimum_required(VERSION 3.27)
project(libclay C)

set(CMAKE_C_STANDARD 99)

option(CLAY_INCLUDE_ALL_EXAMPLES "Build all examples" OFF)
option(CLAY_INCLUDE_DEMOS "Build video demo and website" OFF)
option(CLAY_INCLUDE_CPP_EXAMPLE "Build C++ example" OFF)
option(CLAY_INCLUDE_RAYLIB_EXAMPLES "Build raylib examples" ON)
option(CLAY_INCLUDE_SDL2_EXAMPLES "Build SDL 2 examples" OFF)
option(CLAY_INCLUDE_SDL3_EXAMPLES "Build SDL 3 examples" OFF)
option(CLAY_INCLUDE_WIN32_GDI_EXAMPLES "Build Win32 GDI examples" OFF)
option(CLAY_INCLUDE_SOKOL_EXAMPLES "Build Sokol examples" OFF)
option(CLAY_INCLUDE_PLAYDATE_EXAMPLES "Build Playdate examples" OFF)


add_subdirectory(clay)
include_directories(clay)

add_library(clay SHARED clay.c)
target_compile_definitions(clay PUBLIC CLAY_DLL_FIX)

