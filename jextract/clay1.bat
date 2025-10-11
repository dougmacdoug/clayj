jextract -D __clang__ ^
--include-constant CLAY_ATTACH_POINT_CENTER_BOTTOM                        ^
--include-constant CLAY_ATTACH_POINT_CENTER_CENTER                        ^
--include-constant CLAY_ATTACH_POINT_CENTER_TOP                           ^
--include-constant CLAY_ATTACH_POINT_LEFT_BOTTOM                          ^
--include-constant CLAY_ATTACH_POINT_LEFT_CENTER                          ^
--include-constant CLAY_ATTACH_POINT_LEFT_TOP                             ^
--include-constant CLAY_ATTACH_POINT_RIGHT_BOTTOM                         ^
--include-constant CLAY_ATTACH_POINT_RIGHT_CENTER                         ^
--include-constant CLAY_ATTACH_POINT_RIGHT_TOP                            ^
--include-constant CLAY_RENDER_COMMAND_TYPE_NONE ^
--include-constant CLAY_RENDER_COMMAND_TYPE_RECTANGLE ^
--include-constant CLAY_RENDER_COMMAND_TYPE_BORDER ^
--include-constant CLAY_RENDER_COMMAND_TYPE_TEXT ^
--include-constant CLAY_RENDER_COMMAND_TYPE_IMAGE ^
--include-constant CLAY_RENDER_COMMAND_TYPE_SCISSOR_START ^
--include-constant CLAY_RENDER_COMMAND_TYPE_SCISSOR_END ^
--include-constant CLAY_RENDER_COMMAND_TYPE_CUSTOM ^
--include-typedef Clay_RenderCommandType ^
--include-constant     CLAY__SIZING_TYPE_FIT      ^
--include-constant     CLAY__SIZING_TYPE_GROW      ^
--include-constant     CLAY__SIZING_TYPE_PERCENT      ^
--include-constant     CLAY__SIZING_TYPE_FIXED      ^
--include-typedef    Clay__SizingType ^
--include-typedef Clay_FloatingAttachPointType ^
  --include-struct Clay_TextRenderData        ^
  --include-function Clay__HashString ^
  --include-function Clay_MinMemorySize ^
  --include-function Clay_CreateArenaWithCapacityAndMemory ^
  --include-function Clay_SetMeasureTextFunction ^
  --include-function Clay_ResetMeasureTextCache ^
  --include-function Clay_SetMaxElementCount ^
  --include-function Clay_SetMaxMeasureTextCacheWordCount ^
  --include-function Clay_Initialize ^
  --include-function Clay_SetCurrentContext ^
  --include-function Clay_GetCurrentContext ^
  --include-function Clay_SetLayoutDimensions ^
  --include-function Clay_SetPointerState ^
  --include-function Clay_UpdateScrollContainers ^
  --include-function Clay_GetScrollOffset ^
  --include-function Clay_BeginLayout ^
  --include-function Clay_EndLayout ^
  --include-function Clay_Hovered ^
  --include-function Clay_OnHover ^
  --include-function Clay_PointerOver ^
  --include-function Clay_GetScrollContainerData ^
  --include-function Clay_GetElementData ^
  --include-function Clay_GetElementId ^
  --include-function Clay__OpenElement ^
  --include-function Clay__ConfigureOpenElement ^
  --include-function Clay__CloseElement ^
  --include-function  Clay_SetDebugModeEnabled ^
  --include-function Clay_RenderCommandArray_Get ^
  --include-function Clay__StoreTextElementConfig ^
  --include-function Clay__OpenTextElement ^
  --include-struct Clay_ElementId ^
  --include-struct Clay_ElementDeclaration ^
  --include-struct Clay_StringSlice ^
  --include-struct Clay_RenderCommandArray ^
  --include-struct Clay_RectangleRenderData        ^
  --include-struct Clay_TextRenderData        ^
  --include-struct Clay_ImageRenderData        ^
  --include-struct Clay_CustomRenderData        ^
  --include-struct Clay_BorderRenderData        ^
  --include-struct Clay_ClipRenderData        ^
  --include-struct Clay_ScrollRenderData ^
  --include-union Clay_RenderData ^
  --include-struct Clay_RenderCommand ^
   --include-struct Clay_BorderRenderData  ^
   --include-struct Clay_CustomRenderData ^
   ^
  --include-struct Clay_LayoutConfig ^
  --include-struct Clay_CornerRadius ^
  --include-struct Clay_AspectRatioElementConfig ^
  --include-struct Clay_ImageElementConfig ^
  --include-struct Clay_CustomElementConfig ^
  --include-struct Clay_BorderElementConfig ^
--include-struct Clay_Padding ^
--include-struct Clay_ChildAlignment ^
--include-struct Clay_BorderWidth ^
   ^
  --include-struct Clay_ScrollContainerData ^
  --include-struct Clay_TextElementConfig ^
  --include-struct Clay_Color ^
  --include-struct Clay_Arena ^
  --include-struct Clay_ErrorData ^
  --include-struct Clay_String ^
  --include-typedef Clay_ErrorHandler  ^
  --include-struct Clay_Dimensions  ^
  --include-struct Clay_ClipElementConfig ^
  --include-struct Clay_Vector2 ^
  --include-struct Clay_RenderCommandArray ^
  --include-struct Clay_ElementData ^
  --include-struct Clay_PointerData ^
  --include-struct Clay_BoundingBox ^
  --include-struct Clay_SizingAxis ^
  --include-struct Clay_Sizing ^
  --include-struct Clay_SizingMinMax ^
  --include-struct Clay_FloatingAttachPoints ^
   --include-struct Clay_FloatingElementConfig ^
  -I lib/clay --output src/main/java -t dmacd.ffm.clay ^
  --header-class-name ClayFFM clay.h


REM jextract -D __clang__ ^
REM --include-typedef Document ^
REM --include-struct Clay_String ^
REM --include-typedef DocumentArray ^
REM --include-typedef ClayVideoDemo_Arena ^
REM --include-typedef ClayVideoDemo_Data ^
REM --include-typedef SidebarClickData ^
REM  -I lib --output src/main/java -t dmacd.ffm.clay.demo ^
REM  --header-class-name DemoFFM demo.h



