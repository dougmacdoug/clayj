TODO
===================

* collect todos from code
* mutli-module maven build (will /lib need to move?)
  * started - probably best to move raylib to a module
  * will need to add a module for java renderer
* demo module (might need 1 per demo especially if we make different renderers)
  * basically done, considered multiple modules but we can
    probably just use one project. depends on how large it gets
  * the main issue will be if people dont want to (or cant)
   build all the renderers if we continue to add more
* instructions for jextract folder
* build instructions for lib/
* get raylib.dll/so and clay.dll/so into the correct folders
* maybe add maven build step to create dll/so.. this should be profiled
* current-scope-arena
* arena with no allocation but used for reinterpret
* java demo
  * currently have a working demo..
  * leaving this open until it gets a bit more idiomatic
  * a lot of the code in raylib renderer can be made more javish
  * might be easier to reason about after creating the LWJGL renderer 
* LWJGL renderer and demo https://www.lwjgl.org/guide
* graalvm native-image demo (this will require making 3 upcall functions in C)
* make sure the entire allocation stack is cleared each frame
* fix color
* make a raylib/clay converter (v2/color/other?) for renderer/demo that can use temp alloc
* javadoc on all functions
* fixup the #regions (add more) and reorganize Clay.java cleanly
* add some unit tests for the allocators/

## Futurer

* build a demo app from a separate project
  * this will exercise the clay/raylib dll builds etc.
  * might still need some manual building or profiles
  * ultimately one could just add clayj-api and clayj-x-renderer
     to a maven project and copy the demo while loop 
     to get a working app started
  