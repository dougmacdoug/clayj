cmake_minimum_required(VERSION 3.27)
project(libclay C)

set(CMAKE_C_STANDARD 99)

add_subdirectory(clay)
include_directories(clay)

# Adding Raylib
include(FetchContent)
set(FETCHCONTENT_QUIET FALSE)
set(BUILD_EXAMPLES OFF CACHE BOOL "" FORCE) # don't build the supplied examples
set(BUILD_GAMES    OFF CACHE BOOL "" FORCE) # don't build the supplied example games
set(BUILD_SHARED_LIBS "" ON)
set(GLFW_BUILD_JOYSTICK OFF CACHE BOOL "" FORCE)

FetchContent_Declare(
        raylib
        GIT_REPOSITORY "https://github.com/raysan5/raylib.git"
        GIT_TAG "5.5"
        GIT_PROGRESS TRUE
        GIT_SHALLOW TRUE
)

FetchContent_MakeAvailable(raylib)

add_library(clay SHARED clay.c)
target_compile_definitions(clay PUBLIC CLAY_DLL_FIX)

add_executable(my_demo main.c)
target_compile_definitions(my_demo PUBLIC CLAY_DLL_FIX)
target_include_directories(my_demo PUBLIC .)

target_link_libraries(my_demo PRIVATE clay)
target_link_libraries(my_demo PRIVATE raylib)

