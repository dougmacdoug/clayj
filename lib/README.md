## Lib folder

This folder is for the development of this library. You can use it to build your
own versions of clay.dll/libclay.so or raylib or whatever else you need.


Get clay in this directory.

clone clay into folder `clay`
you can use git clone or zip, it doesn't matter, but only this version has been tested

#### VERSION: 0.14
(from `lib/` folder)
```
git clone https://github.com/nicbarker/clay.git
cd clay
git checkout v0.14
cd ..
```

### Clay DLL fix
There is a fix dll define to remove `__stdcall` from the Clay macro as it 
is incompatible with my setup. Anyone who wants to use this library on 
their own is of course free to rebuild the dlls, etc in any way they see fit.

### Includes
Some of the header files in this folder are just used by jextract to build the
FFM helper classes.