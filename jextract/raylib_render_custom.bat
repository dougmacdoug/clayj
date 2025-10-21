jextract -D __clang__                               ^
-I lib/cmake-build-release/_deps/raylib-build/raylib/include ^
--output src/main/java -t dmacd.clay.renderer ^
--include-constant CUSTOM_LAYOUT_ELEMENT_TYPE_3D_MODEL ^
--include-struct Matrix                             ^
--include-struct Vector3                            ^
--include-struct Vector4                            ^
--include-struct Model                            ^
--include-typedef CustomLayoutElement                  ^
--include-typedef CustomLayoutElementType              ^
--include-typedef CustomLayoutElement_3DModel          ^
--header-class-name DoNotCommitMe lib\raylib_render_custom.h






