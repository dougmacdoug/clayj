// copied from clay_renderer.
// https://github.com/nicbarker/clay/blob/v0.14/renderers/raylib/clay_renderer_raylib.c
// these are used for the raylib_renderer
#define WIN32_LEAN_AND_MEAN
#include "raylib.h"
typedef enum
{
    CUSTOM_LAYOUT_ELEMENT_TYPE_3D_MODEL
} CustomLayoutElementType;

typedef struct
{
    Model model;
    float scale;
    Vector3 position;
    Matrix rotation;
} CustomLayoutElement_3DModel;

typedef struct
{
    CustomLayoutElementType type;
    union {
        CustomLayoutElement_3DModel model;
    } customData;
} CustomLayoutElement;
