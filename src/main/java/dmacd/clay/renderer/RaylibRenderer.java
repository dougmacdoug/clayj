package dmacd.clay.renderer;

import dmacd.ffm.clay.*;
import dmacd.ffm.raylib.*;

import java.lang.foreign.*;

import static dmacd.ffm.clay.ClayFFM.*;
import static dmacd.ffm.raylib.RayFFM.*;


public class RaylibRenderer {

    public static void ClayColorToRaylibColor(MemorySegment clayColor, MemorySegment raylibColor) {
        var r = Math.round(Clay_Color.r(clayColor));
        var g = Math.round(Clay_Color.g(clayColor));
        var b = Math.round(Clay_Color.b(clayColor));
        var a = Math.round(Clay_Color.a(clayColor));
        Color.r(raylibColor, (byte) r);
        Color.g(raylibColor, (byte) g);
        Color.b(raylibColor, (byte) b);
        Color.a(raylibColor, (byte) a);
    }
    // copied from clay_renderer. converted from C to Java FFM

    public static /* Clay_Dimensions */ MemorySegment Raylib_MeasureText(Arena arena,
            /* (Clay_StringSlice) */ MemorySegment text,
            /* (Clay_TextElementConfig *) */  MemorySegment config,
            /* (void *) */ MemorySegment userData) {
        var msTextSize = Clay_Dimensions.allocate(arena);

        float maxTextWidth = 0.0f;
        float lineTextWidth = 0;
        int maxLineCharCount = 0;
        int lineCharCount = 0;
        MemorySegment ms; // tmp
        ms = config.get(ValueLayout.ADDRESS, 0);
        var msConfig = Clay_TextElementConfig.reinterpret(ms, arena, (p) -> {
        });

        float textHeight = Clay_TextElementConfig.fontSize(msConfig);// config->fontSize;

        ms = userData.get(ValueLayout.ADDRESS, 0);
        int fontId = Clay_TextElementConfig.fontId(msConfig);
        // there might be more fonts than fontId + 1,
        // but we need to get FFM to actuate at least that much memory to be accessible
        var msFontArray = Font.reinterpret(ms, fontId + 1, arena, (p) -> {
        });
        var msFont = msFontArray.asSlice(Font.layout().byteSize() * fontId, Font.layout().byteSize());

        // Font failed to load, likely the fonts are in the wrong place relative to the execution dir.
        // RayLib ships with a default font, so we can continue with that built in one.
        var msGlyphs = Font.glyphs(msFont);
        if (msGlyphs == MemorySegment.NULL) {
            msFont = RayFFM.GetFontDefault(arena);
            msGlyphs = Font.glyphs(msFont);
        }

        float scaleFactor = textHeight / (float) Font.baseSize(msFont);

        int length = Clay_StringSlice.length(text);
        var chars = Clay_StringSlice.chars(text).reinterpret(length);


        for (int i = 0; i < length; ++i, lineCharCount++) {
            var c = chars.getAtIndex(ValueLayout.JAVA_BYTE, i);
            if (c == '\n') {
                maxTextWidth = Math.max(maxTextWidth, lineTextWidth);
                maxLineCharCount = Math.max(maxLineCharCount, lineCharCount);
                lineTextWidth = 0;
                lineCharCount = 0;
                continue;
            }
            int index = c - 32;
            msGlyphs = GlyphInfo.reinterpret(msGlyphs, index + 1, arena, (p) -> {
            });
            var msGlyph = GlyphInfo.asSlice(msGlyphs, index);
            var msRecs = Font.recs(msFont);
            msRecs = GlyphInfo.reinterpret(msRecs, index + 1, arena, (p) -> {
            });
            var msRec = msRecs.asSlice(index);

            if (GlyphInfo.advanceX(msGlyph) != 0) lineTextWidth += GlyphInfo.advanceX(msGlyph);
            else lineTextWidth += (Rectangle.width(msRec) + GlyphInfo.offsetX(msGlyph));
        }

        maxTextWidth = Math.max(maxTextWidth, lineTextWidth);
        maxLineCharCount = Math.max(maxLineCharCount, lineCharCount);

        var letterSpacing = Clay_TextElementConfig.letterSpacing(msConfig);
        Clay_Dimensions.width(msTextSize, maxTextWidth * scaleFactor + (lineCharCount * letterSpacing));
        Clay_Dimensions.height(msTextSize, textHeight);

        return msTextSize;
    }

    public static
        /*(Ray)*/ MemorySegment GetScreenToWorldPointWithZDistance(
            Arena arena, // new
            MemorySegment /*(Vector2)*/ position, MemorySegment /*(Camera)*/ camera,
            int screenWidth, int screenHeight, float zDistance) {
        var ray = Ray.allocate(arena);

        // Calculate normalized device coordinates
        // NOTE: y value is negative
        float x = (2.0f * Vector2.x(position)) / (float) screenWidth - 1.0f;
        float y = 1.0f - (2.0f * Vector2.y(position)) / (float) screenHeight;
        float z = 1.0f;

        // Store values in a vector
        var deviceCoords = Vector3.allocate(arena);
//        { x, y, z };
        Vector3.x(deviceCoords, x);
        Vector3.x(deviceCoords, y);
        Vector3.x(deviceCoords, z);

        // Calculate view matrix from camera look at
        var msMatView = MatrixLookAt(arena,
                Camera.position(camera), Camera.target(camera), Camera.up(camera));

        var msMMatProj = MatrixIdentity(arena);

        if (Camera.projection(camera) == CAMERA_PERSPECTIVE()) {
            // Calculate projection matrix from perspective
            msMMatProj = MatrixPerspective(arena, Camera.fovy(camera) * DEG2RAD(),
                    ((double) screenWidth / (double) screenHeight), 0.01f, zDistance);
        } else if (Camera.projection(camera) == CAMERA_ORTHOGRAPHIC()) {
            double aspect = (double) screenWidth / (double) screenHeight;
            double top = Camera.fovy(camera) / 2.0;
            double right = top * aspect;

            // Calculate projection matrix from orthographic
            msMMatProj = MatrixOrtho(arena, -right, right, -top, top, 0.01, 1000.0);
        }
        var tmpV3 = Vector3.allocate(arena);
        Vector3.x(tmpV3, Vector3.x(deviceCoords));
        Vector3.y(tmpV3, Vector3.y(deviceCoords));
        Vector3.z(tmpV3, 0);
        // Unproject far/near points
        var nearPoint = Vector3Unproject(arena, tmpV3, msMMatProj, msMatView);

        Vector3.z(tmpV3, 1.0f);
        var farPoint = Vector3Unproject(arena, tmpV3, msMMatProj, msMatView);

        // Calculate normalized direction vector
        var direction = Vector3Normalize(arena, Vector3Subtract(arena, farPoint, nearPoint));

        Ray.position(ray, farPoint);

        // Apply calculated vectors to ray
        Ray.direction(ray, direction);

        return ray;
    }

    public static void Clay_Raylib_Initialize(int width, int height, MemorySegment title, int flags) {
        SetConfigFlags(flags);
        InitWindow(width, height, title);
        //    EnableEventWaiting();
    }

    // A MALLOC'd buffer, that we keep modifying inorder to save from so many Malloc and Free Calls.
    // Call Clay_Raylib_Close() to free
//         static char *temp_render_buffer = NULL;
//         static int temp_render_buffer_len = 0;

    // Call after closing the window to clean up the render buffer
    public static void Clay_Raylib_Close() {
//              if(temp_render_buffer) free(temp_render_buffer);
//              temp_render_buffer_len = 0;

        CloseWindow();
    }

    public enum CLAY_RENDER_COMMAND {
        CLAY_RENDER_COMMAND_TYPE_NONE(ClayFFM.CLAY_RENDER_COMMAND_TYPE_NONE()),
        CLAY_RENDER_COMMAND_TYPE_RECTANGLE(ClayFFM.CLAY_RENDER_COMMAND_TYPE_RECTANGLE()),
        CLAY_RENDER_COMMAND_TYPE_BORDER(ClayFFM.CLAY_RENDER_COMMAND_TYPE_BORDER()),
        CLAY_RENDER_COMMAND_TYPE_TEXT(ClayFFM.CLAY_RENDER_COMMAND_TYPE_TEXT()),
        CLAY_RENDER_COMMAND_TYPE_IMAGE(ClayFFM.CLAY_RENDER_COMMAND_TYPE_IMAGE()),
        CLAY_RENDER_COMMAND_TYPE_SCISSOR_START(ClayFFM.CLAY_RENDER_COMMAND_TYPE_SCISSOR_START()),
        CLAY_RENDER_COMMAND_TYPE_SCISSOR_END(ClayFFM.CLAY_RENDER_COMMAND_TYPE_SCISSOR_END()),
        CLAY_RENDER_COMMAND_TYPE_CUSTOM(ClayFFM.CLAY_RENDER_COMMAND_TYPE_CUSTOM());

        private final int value;

        CLAY_RENDER_COMMAND(int value) {
            this.value = value;
        }

        public static CLAY_RENDER_COMMAND fromValue(int value) {
            for (CLAY_RENDER_COMMAND cmd : values()) {
                if (cmd.value == value) {
                    return cmd;
                }
            }
            throw new IllegalArgumentException("No enum constant with id " + value);
        }
    }

    public static void Clay_Raylib_Render(/*(Clay_RenderCommandArray)*/ MemorySegment renderCommands, /*(Font*)*/ MemorySegment fonts) {
        try (Arena arena = Arena.ofConfined()) { // todo: nicey nice arenas
            var renderCommandsLength = Clay_RenderCommandArray.length(renderCommands);
            for (int j = 0; j < renderCommandsLength; j++) {
                var /*(Clay_RenderCommand *)*/ msRC = Clay_RenderCommandArray_Get(renderCommands, j);
                msRC = renderCommands.get(ValueLayout.ADDRESS, 0);
                msRC = Clay_RenderCommand.reinterpret(msRC, arena, (p) -> {
                });
                var msBB = Clay_RenderCommand.boundingBox(msRC);
                var bbX = Math.round(Clay_BoundingBox.x(msBB));
                var bbY = Math.round(Clay_BoundingBox.y(msBB));
                var bbWidth = Math.round(Clay_BoundingBox.width(msBB));
                var bbHeight = Math.round(Clay_BoundingBox.height(msBB));

                CLAY_RENDER_COMMAND command = CLAY_RENDER_COMMAND.fromValue(Clay_RenderCommand.commandType(msRC));
                var renderData = Clay_RenderCommand.renderData(msRC);

                switch (command) {
                    case CLAY_RENDER_COMMAND_TYPE_TEXT: {
//                    var msTxtData = Clay_RenderCommand.renderData(msRC);
                        var fontId = Clay_TextRenderData.fontId(renderData);
                        var msFontToUse = Font.asSlice(fonts, fontId);

                        var msContents = Clay_TextRenderData.stringContents(renderData);
                        int strlen = Clay_StringSlice.length(msContents) + 1;

//                    if(strlen > temp_render_buffer_len) {
//                         Grow the temp buffer if we need a larger string
//                        if(temp_render_buffer) free(temp_render_buffer);
//                        temp_render_buffer = (char *) malloc(strlen);
//                        temp_render_buffer_len = strlen;
//                    }
                        // todo: something like the temp_buffer solution for memory allocation reduction /reuse
                        var msTemp = arena.allocate(strlen);
                        MemorySegment.copy(msContents, 0, msTemp, 0, strlen - 1);
                        msTemp.setAtIndex(ValueLayout.JAVA_BYTE, strlen - 1, (byte) 0);
                        // Raylib uses standard C strings so isn't compatible with cheap slices, we need to clone the string to append null terminator
//                    memcpy(temp_render_buffer, textData->stringContents.chars, textData->stringContents.length);
//                    temp_render_buffer[textData->stringContents.length] = '\0';
                        var v2 = Vector2.allocate(arena);
                        Vector2.x(v2, bbX);
                        Vector2.y(v2, bbY);

//                    CLAY_COLOR_TO_RAYLIB_COLOR(color) (Color) { .r = (unsigned char)roundf(color.r), .g = (unsigned char)roundf(color.g), .b = (unsigned char)roundf(color.b), .a = (unsigned char)roundf(color.a) }
                        var rayColor = Color.allocate(arena);
                        var txtColor = Clay_TextRenderData.textColor(renderData);
                        ClayColorToRaylibColor(txtColor, rayColor);
                        DrawTextEx(msFontToUse, msTemp, v2, (float) Clay_TextRenderData.fontSize(renderData),
                                (float) Clay_TextRenderData.letterSpacing(renderData),
                                rayColor);

                        break;
                    }
                    case CLAY_RENDER_COMMAND_TYPE_IMAGE:// ClayFFM.CLAY_RENDER_COMMAND_TYPE_IMAGE(): {
                        var imageData = Clay_ImageRenderData.imageData(renderData);
                        var imageTexture = Texture2D.reinterpret(imageData, arena, (p) -> {
                        });
                        var tintColor = Clay_ImageRenderData.backgroundColor(imageData);
                        if (Clay_Color.r(tintColor) == 0 && Clay_Color.g(tintColor) == 0 && Clay_Color.b(tintColor) == 0 && Clay_Color.a(tintColor) == 0) {
                            Clay_Color.r(tintColor, 255);
                            Clay_Color.g(tintColor, 255);
                            Clay_Color.b(tintColor, 255);
                            Clay_Color.a(tintColor, 255);
                        }
                        var rect = Rectangle.allocate(arena);
                        var boundRect = Rectangle.allocate(arena);
                        Rectangle.x(boundRect, bbX);
                        Rectangle.y(boundRect, bbX);
                        Rectangle.width(boundRect, bbWidth);
                        Rectangle.height(boundRect, bbHeight);
                        Rectangle.x(rect, 0);
                        Rectangle.y(rect, 0);
                        Rectangle.width(rect, Texture2D.width(imageTexture));
                        Rectangle.height(rect, Texture2D.height(imageTexture));
                        var rayColor = Color.allocate(arena);
                        ClayColorToRaylibColor(tintColor, rayColor);
                        var emptyV2 = Vector2.allocate(arena);
                        emptyV2.fill((byte) 0);
                        DrawTexturePro(imageTexture, rect, boundRect, emptyV2,
                                0, rayColor);
                        break;
                    case CLAY_RENDER_COMMAND_TYPE_SCISSOR_START: {
                        BeginScissorMode(bbX, bbY, bbWidth, bbHeight);
                        break;
                    }
                    case CLAY_RENDER_COMMAND_TYPE_SCISSOR_END: {
                        EndScissorMode();
                        break;
                    }
                    case CLAY_RENDER_COMMAND_TYPE_RECTANGLE: {
                        var config = Clay_RenderData.rectangle(renderData);
                        var cornerRadius = Clay_RectangleRenderData.cornerRadius(config);
                        var clayBgc = Clay_RectangleRenderData.backgroundColor(config);
                        var bgColor = Color.allocate(arena);
                        ClayColorToRaylibColor(clayBgc, bgColor);

                        if (Clay_CornerRadius.topLeft(cornerRadius) > 0) {
                            float radius = (Clay_CornerRadius.topLeft(cornerRadius) * 2) / (float) (Math.min(bbWidth, bbHeight));
                            var rectBB = Rectangle.allocate(arena);
                            Rectangle.x(rectBB, bbX);
                            Rectangle.y(rectBB, bbY);
                            Rectangle.width(rectBB, bbWidth);
                            Rectangle.height(rectBB, bbHeight);
                            DrawRectangleRounded(rectBB, radius, 8, bgColor);
                        } else {
                            DrawRectangle(bbX, bbY, bbWidth, bbHeight, bgColor);
                        }
                        break;
                    }
                    case CLAY_RENDER_COMMAND_TYPE_BORDER: {
                    var config = Clay_RenderData.rectangle(renderData);
                    var bw = Clay_BorderRenderData.width(config);
                    var cornerRadius = Clay_BorderRenderData.cornerRadius(config);
                    var clayColor = Clay_BorderRenderData.color(config);
                    var color = Color.allocate(arena);
                    ClayColorToRaylibColor(clayColor, color);
                        // Left border
                    if (Clay_BorderWidth.left(bw) > 0) {
                        DrawRectangle(bbX, Math.round(Clay_BoundingBox.y(msBB) + Clay_CornerRadius.topLeft(cornerRadius)),
                                Clay_BorderWidth.left(bw), Math.round(Clay_BoundingBox.height(msBB) - Clay_CornerRadius.topLeft(cornerRadius) - Clay_CornerRadius.bottomLeft(cornerRadius)),
                                color);
                    }
                    // Right border
                    if (Clay_BorderWidth.right(bw)  > 0) {
                        DrawRectangle(Math.round(Clay_BoundingBox.x(msBB) + Clay_BoundingBox.width(msBB) - Clay_BorderWidth.right(bw)),
                                Math.round(Clay_BoundingBox.y(msBB) + Clay_CornerRadius.topRight(cornerRadius)),
                                Clay_BorderWidth.right(bw), Math.round(Clay_BoundingBox.height(msBB) - Clay_CornerRadius.topRight(cornerRadius) - Clay_CornerRadius.bottomRight(cornerRadius)),
                                color);
                    }
                    // Top border
                    if (Clay_BorderWidth.top(bw) > 0) {
                        DrawRectangle(Math.round(Clay_BoundingBox.x(msBB) + Clay_CornerRadius.topLeft(cornerRadius)), bbY,
                                Math.round(Clay_BoundingBox.width(msBB) - Clay_CornerRadius.topLeft(cornerRadius) - Clay_CornerRadius.topRight(cornerRadius)),
                                Clay_BorderWidth.top(bw), color);
                    }
                    // Bottom border
                    if (Clay_BorderWidth.bottom(bw) > 0) {
                        DrawRectangle(Math.round(Clay_BoundingBox.x(msBB) + Clay_CornerRadius.bottomLeft(cornerRadius)),
                                Math.round(Clay_BoundingBox.y(msBB) + Clay_BoundingBox.height(msBB) - Clay_BorderWidth.bottom(bw)),
                                Math.round(Clay_BoundingBox.width(msBB) - Clay_CornerRadius.bottomLeft(cornerRadius) - Clay_CornerRadius.bottomRight(cornerRadius)),
                                Clay_BorderWidth.bottom(bw), color);
                    }
                    var center = Vector2.allocate(arena);
                    if (Clay_CornerRadius.topLeft(cornerRadius) > 0) {
                        Vector2.x(center, Math.round(Clay_BoundingBox.x(msBB) + Clay_CornerRadius.topLeft(cornerRadius)));
                        Vector2.y(center, Math.round(Clay_BoundingBox.y(msBB) + Clay_CornerRadius.topLeft(cornerRadius)));

                        DrawRing(center, Math.round(Clay_CornerRadius.topLeft(cornerRadius) - Clay_BorderWidth.top(bw)),
                                Clay_CornerRadius.topLeft(cornerRadius), 180, 270, 10, color);
                    }
                    if (Clay_CornerRadius.topRight(cornerRadius) > 0) {
                        Vector2.x(center, Math.round(Clay_BoundingBox.x(msBB) + Clay_BoundingBox.width(msBB) - Clay_CornerRadius.topRight(cornerRadius)));
                        Vector2.y(center, Math.round(Clay_BoundingBox.y(msBB) + Clay_CornerRadius.topRight(cornerRadius)));

                        DrawRing(center, Math.round(Clay_CornerRadius.topRight(cornerRadius) - Clay_BorderWidth.top(bw)),
                                Clay_CornerRadius.topRight(cornerRadius), 270, 360, 10, color);
                    }
                    if (Clay_CornerRadius.bottomLeft(cornerRadius) > 0) {
                        Vector2.x(center, Math.round(Clay_BoundingBox.x(msBB) + Clay_CornerRadius.bottomLeft(cornerRadius)));
                        Vector2.y(center, Math.round(Clay_BoundingBox.y(msBB) + Clay_BoundingBox.height(msBB) - Clay_CornerRadius.bottomLeft(cornerRadius)));
                        DrawRing(center, Math.round(Clay_CornerRadius.bottomLeft(cornerRadius) - Clay_BorderWidth.bottom(bw)),
                                Clay_CornerRadius.bottomLeft(cornerRadius), 90, 180, 10, color);
                    }
                    if (Clay_CornerRadius.bottomRight(cornerRadius) > 0) {
                        Vector2.x(center, Math.round(Clay_BoundingBox.x(msBB) + Clay_BoundingBox.width(msBB) - Clay_CornerRadius.bottomRight(cornerRadius)));
                        Vector2.y(center, Math.round(Clay_BoundingBox.y(msBB) + Clay_BoundingBox.height(msBB) - Clay_CornerRadius.bottomRight(cornerRadius)));

                        DrawRing(center, Math.round(Clay_CornerRadius.bottomRight(cornerRadius) - Clay_BorderWidth.bottom(bw)),
                                Clay_CornerRadius.bottomRight(cornerRadius), 0.1f, 90, 10, color);
                    }
                        break;
                    }
                    case CLAY_RENDER_COMMAND_TYPE_CUSTOM: {
//                    Clay_CustomRenderData *config = &renderCommand->renderData.custom;
//                    CustomLayoutElement *customElement = (CustomLayoutElement *)config->customData;
//                    if (!customElement) continue;
//                    switch (customElement->type) {
//                        case CUSTOM_LAYOUT_ELEMENT_TYPE_3D_MODEL: {
//                            Clay_BoundingBox rootBox = renderCommands.internalArray[0].boundingBox;
//                            float scaleValue = CLAY__MIN(CLAY__MIN(1, 768 / rootBox.height) * CLAY__MAX(1, rootBox.width / 1024), 1.5f);
//                            Ray positionRay = GetScreenToWorldPointWithZDistance((Vector2) { renderCommand->boundingBox.x + renderCommand->bbWidth / 2, renderCommand->boundingBox.y + (renderCommand->bbHeight / 2) + 20 }, Raylib_camera, (int)roundf(rootBox.width), (int)roundf(rootBox.height), 140);
//                            BeginMode3D(Raylib_camera);
//                                DrawModel(customElement->customData.model.model, positionRay.position, customElement->customData.model.scale * scaleValue, WHITE);        // Draw 3d model with texture
//                            EndMode3D();
//                            break;
//                        }
//                        default: break;
//                    }
                        break;
                    }
                    default: {
                        System.err.println("Error: unhandled render command.");
                        System.exit(1);
                    }
                }
            }
        }
    }


}


//    public static final int CAMERA_PERSPECTIVE = 0;
//    public static final int CAMERA_ORTHOGRAPHIC = 1;
//    public static final double DEG2RAD = Math.PI / 180.0;
//

/**
 * copied from clay/renderers/raylib/clay_renderer_raylib.c
 * <p>
 * #include "raylib.h"
 * #include "raymath.h"
 * #include "stdint.h"
 * #include "string.h"
 * #include "stdio.h"
 * #include "stdlib.h"
 * <p>
 * #define CLAY_RECTANGLE_TO_RAYLIB_RECTANGLE(rectangle) (Rectangle) { .x = rectangle.x, .y = rectangle.y, .width = rectangle.width, .height = rectangle.height }
 * #define CLAY_COLOR_TO_RAYLIB_COLOR(color) (Color) { .r = (unsigned char)roundf(color.r), .g = (unsigned char)roundf(color.g), .b = (unsigned char)roundf(color.b), .a = (unsigned char)roundf(color.a) }
 * <p>
 * Camera Raylib_camera;
 * <p>
 * typedef enum
 * {
 * CUSTOM_LAYOUT_ELEMENT_TYPE_3D_MODEL
 * } CustomLayoutElementType;
 * <p>
 * typedef struct
 * {
 * Model model;
 * float scale;
 * Vector3 position;
 * Matrix rotation;
 * } CustomLayoutElement_3DModel;
 * <p>
 * typedef struct
 * {
 * CustomLayoutElementType type;
 * union {
 * CustomLayoutElement_3DModel model;
 * } customData;
 * } CustomLayoutElement;
 * <p>
 * // Get a ray trace from the screen position (i.e mouse) within a specific section of the screen
 * Ray GetScreenToWorldPointWithZDistance(Vector2 position, Camera camera, int screenWidth, int screenHeight, float zDistance)
 * {
 * Ray ray = { 0 };
 * <p>
 * // Calculate normalized device coordinates
 * // NOTE: y value is negative
 * float x = (2.0f*position.x)/(float)screenWidth - 1.0f;
 * float y = 1.0f - (2.0f*position.y)/(float)screenHeight;
 * float z = 1.0f;
 * <p>
 * // Store values in a vector
 * Vector3 deviceCoords = { x, y, z };
 * <p>
 * // Calculate view matrix from camera look at
 * Matrix matView = MatrixLookAt(camera.position, camera.target, camera.up);
 * <p>
 * Matrix matProj = MatrixIdentity();
 * <p>
 * if (camera.projection == CAMERA_PERSPECTIVE)
 * {
 * // Calculate projection matrix from perspective
 * matProj = MatrixPerspective(camera.fovy*DEG2RAD, ((double)screenWidth/(double)screenHeight), 0.01f, zDistance);
 * }
 * else if (camera.projection == CAMERA_ORTHOGRAPHIC)
 * {
 * double aspect = (double)screenWidth/(double)screenHeight;
 * double top = camera.fovy/2.0;
 * double right = top*aspect;
 * <p>
 * // Calculate projection matrix from orthographic
 * matProj = MatrixOrtho(-right, right, -top, top, 0.01, 1000.0);
 * }
 * <p>
 * // Unproject far/near points
 * Vector3 nearPoint = Vector3Unproject((Vector3){ deviceCoords.x, deviceCoords.y, 0.0f }, matProj, matView);
 * Vector3 farPoint = Vector3Unproject((Vector3){ deviceCoords.x, deviceCoords.y, 1.0f }, matProj, matView);
 * <p>
 * // Calculate normalized direction vector
 * Vector3 direction = Vector3Normalize(Vector3Subtract(farPoint, nearPoint));
 * <p>
 * ray.position = farPoint;
 * <p>
 * // Apply calculated vectors to ray
 * ray.direction = direction;
 * <p>
 * return ray;
 * }
 * <p>
 * <p>
 * inline Clay_Dimensions Raylib_MeasureText(Clay_StringSlice text, Clay_TextElementConfig *config, void *userData) {
 * // Measure string size for Font
 * Clay_Dimensions textSize = { 0 };
 * <p>
 * float maxTextWidth = 0.0f;
 * float lineTextWidth = 0;
 * int maxLineCharCount = 0;
 * int lineCharCount = 0;
 * <p>
 * float textHeight = config->fontSize;
 * Font* fonts = (Font*)userData;
 * Font fontToUse = fonts[config->fontId];
 * // Font failed to load, likely the fonts are in the wrong place relative to the execution dir.
 * // RayLib ships with a default font, so we can continue with that built in one.
 * if (!fontToUse.glyphs) {
 * fontToUse = GetFontDefault();
 * }
 * <p>
 * float scaleFactor = config->fontSize/(float)fontToUse.baseSize;
 * <p>
 * for (int i = 0; i < text.length; ++i, lineCharCount++)
 * {
 * if (text.chars[i] == '\n') {
 * maxTextWidth = fmax(maxTextWidth, lineTextWidth);
 * maxLineCharCount = CLAY__MAX(maxLineCharCount, lineCharCount);
 * lineTextWidth = 0;
 * lineCharCount = 0;
 * continue;
 * }
 * int index = text.chars[i] - 32;
 * if (fontToUse.glyphs[index].advanceX != 0) lineTextWidth += fontToUse.glyphs[index].advanceX;
 * else lineTextWidth += (fontToUse.recs[index].width + fontToUse.glyphs[index].offsetX);
 * }
 * <p>
 * maxTextWidth = fmax(maxTextWidth, lineTextWidth);
 * maxLineCharCount = CLAY__MAX(maxLineCharCount, lineCharCount);
 * <p>
 * textSize.width = maxTextWidth * scaleFactor + (lineCharCount * config->letterSpacing);
 * textSize.height = textHeight;
 * <p>
 * return textSize;
 * }
 * <p>
 * void Clay_Raylib_Initialize(int width, int height, const char *title, unsigned int flags) {
 * SetConfigFlags(flags);
 * InitWindow(width, height, title);
 * //    EnableEventWaiting();
 * }
 * <p>
 * // A MALLOC'd buffer, that we keep modifying inorder to save from so many Malloc and Free Calls.
 * // Call Clay_Raylib_Close() to free
 * static char *temp_render_buffer = NULL;
 * static int temp_render_buffer_len = 0;
 * <p>
 * // Call after closing the window to clean up the render buffer
 * void Clay_Raylib_Close()
 * {
 * if(temp_render_buffer) free(temp_render_buffer);
 * temp_render_buffer_len = 0;
 * <p>
 * CloseWindow();
 * }
 * <p>
 * <p>
 * void Clay_Raylib_Render(Clay_RenderCommandArray renderCommands, Font* fonts)
 * {
 * for (int j = 0; j < renderCommands.length; j++)
 * {
 * Clay_RenderCommand *renderCommand = Clay_RenderCommandArray_Get(&renderCommands, j);
 * Clay_BoundingBox boundingBox = {roundf(renderCommand->boundingBox.x), roundf(renderCommand->boundingBox.y), roundf(renderCommand->boundingBox.width), roundf(renderCommand->boundingBox.height)};
 * switch (renderCommand->commandType)
 * {
 * case CLAY_RENDER_COMMAND_TYPE_TEXT: {
 * Clay_TextRenderData *textData = &renderCommand->renderData.text;
 * Font fontToUse = fonts[textData->fontId];
 * <p>
 * int strlen = textData->stringContents.length + 1;
 * <p>
 * if(strlen > temp_render_buffer_len) {
 * // Grow the temp buffer if we need a larger string
 * if(temp_render_buffer) free(temp_render_buffer);
 * temp_render_buffer = (char *) malloc(strlen);
 * temp_render_buffer_len = strlen;
 * }
 * <p>
 * // Raylib uses standard C strings so isn't compatible with cheap slices, we need to clone the string to append null terminator
 * memcpy(temp_render_buffer, textData->stringContents.chars, textData->stringContents.length);
 * temp_render_buffer[textData->stringContents.length] = '\0';
 * DrawTextEx(fontToUse, temp_render_buffer, (Vector2){boundingBox.x, boundingBox.y}, (float)textData->fontSize, (float)textData->letterSpacing, CLAY_COLOR_TO_RAYLIB_COLOR(textData->textColor));
 * <p>
 * break;
 * }
 * case CLAY_RENDER_COMMAND_TYPE_IMAGE: {
 * Texture2D imageTexture = *(Texture2D *)renderCommand->renderData.image.imageData;
 * Clay_Color tintColor = renderCommand->renderData.image.backgroundColor;
 * if (tintColor.r == 0 && tintColor.g == 0 && tintColor.b == 0 && tintColor.a == 0) {
 * tintColor = (Clay_Color) { 255, 255, 255, 255 };
 * }
 * DrawTexturePro(
 * imageTexture,
 * (Rectangle) { 0, 0, imageTexture.width, imageTexture.height },
 * (Rectangle){boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height},
 * (Vector2) {},
 * 0,
 * CLAY_COLOR_TO_RAYLIB_COLOR(tintColor));
 * break;
 * }
 * case CLAY_RENDER_COMMAND_TYPE_SCISSOR_START: {
 * BeginScissorMode((int)roundf(boundingBox.x), (int)roundf(boundingBox.y), (int)roundf(boundingBox.width), (int)roundf(boundingBox.height));
 * break;
 * }
 * case CLAY_RENDER_COMMAND_TYPE_SCISSOR_END: {
 * EndScissorMode();
 * break;
 * }
 * case CLAY_RENDER_COMMAND_TYPE_RECTANGLE: {
 * Clay_RectangleRenderData *config = &renderCommand->renderData.rectangle;
 * if (config->cornerRadius.topLeft > 0) {
 * float radius = (config->cornerRadius.topLeft * 2) / (float)((boundingBox.width > boundingBox.height) ? boundingBox.height : boundingBox.width);
 * DrawRectangleRounded((Rectangle) { boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height }, radius, 8, CLAY_COLOR_TO_RAYLIB_COLOR(config->backgroundColor));
 * } else {
 * DrawRectangle(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height, CLAY_COLOR_TO_RAYLIB_COLOR(config->backgroundColor));
 * }
 * break;
 * }
 * case CLAY_RENDER_COMMAND_TYPE_BORDER: {
 * Clay_BorderRenderData *config = &renderCommand->renderData.border;
 * // Left border
 * if (config->width.left > 0) {
 * DrawRectangle((int)roundf(boundingBox.x), (int)roundf(boundingBox.y + config->cornerRadius.topLeft), (int)config->width.left, (int)roundf(boundingBox.height - config->cornerRadius.topLeft - config->cornerRadius.bottomLeft), CLAY_COLOR_TO_RAYLIB_COLOR(config->color));
 * }
 * // Right border
 * if (config->width.right > 0) {
 * DrawRectangle((int)roundf(boundingBox.x + boundingBox.width - config->width.right), (int)roundf(boundingBox.y + config->cornerRadius.topRight), (int)config->width.right, (int)roundf(boundingBox.height - config->cornerRadius.topRight - config->cornerRadius.bottomRight), CLAY_COLOR_TO_RAYLIB_COLOR(config->color));
 * }
 * // Top border
 * if (config->width.top > 0) {
 * DrawRectangle((int)roundf(boundingBox.x + config->cornerRadius.topLeft), (int)roundf(boundingBox.y), (int)roundf(boundingBox.width - config->cornerRadius.topLeft - config->cornerRadius.topRight), (int)config->width.top, CLAY_COLOR_TO_RAYLIB_COLOR(config->color));
 * }
 * // Bottom border
 * if (config->width.bottom > 0) {
 * DrawRectangle((int)roundf(boundingBox.x + config->cornerRadius.bottomLeft), (int)roundf(boundingBox.y + boundingBox.height - config->width.bottom), (int)roundf(boundingBox.width - config->cornerRadius.bottomLeft - config->cornerRadius.bottomRight), (int)config->width.bottom, CLAY_COLOR_TO_RAYLIB_COLOR(config->color));
 * }
 * if (config->cornerRadius.topLeft > 0) {
 * DrawRing((Vector2) { roundf(boundingBox.x + config->cornerRadius.topLeft), roundf(boundingBox.y + config->cornerRadius.topLeft) }, roundf(config->cornerRadius.topLeft - config->width.top), config->cornerRadius.topLeft, 180, 270, 10, CLAY_COLOR_TO_RAYLIB_COLOR(config->color));
 * }
 * if (config->cornerRadius.topRight > 0) {
 * DrawRing((Vector2) { roundf(boundingBox.x + boundingBox.width - config->cornerRadius.topRight), roundf(boundingBox.y + config->cornerRadius.topRight) }, roundf(config->cornerRadius.topRight - config->width.top), config->cornerRadius.topRight, 270, 360, 10, CLAY_COLOR_TO_RAYLIB_COLOR(config->color));
 * }
 * if (config->cornerRadius.bottomLeft > 0) {
 * DrawRing((Vector2) { roundf(boundingBox.x + config->cornerRadius.bottomLeft), roundf(boundingBox.y + boundingBox.height - config->cornerRadius.bottomLeft) }, roundf(config->cornerRadius.bottomLeft - config->width.bottom), config->cornerRadius.bottomLeft, 90, 180, 10, CLAY_COLOR_TO_RAYLIB_COLOR(config->color));
 * }
 * if (config->cornerRadius.bottomRight > 0) {
 * DrawRing((Vector2) { roundf(boundingBox.x + boundingBox.width - config->cornerRadius.bottomRight), roundf(boundingBox.y + boundingBox.height - config->cornerRadius.bottomRight) }, roundf(config->cornerRadius.bottomRight - config->width.bottom), config->cornerRadius.bottomRight, 0.1, 90, 10, CLAY_COLOR_TO_RAYLIB_COLOR(config->color));
 * }
 * break;
 * }
 * case CLAY_RENDER_COMMAND_TYPE_CUSTOM: {
 * Clay_CustomRenderData *config = &renderCommand->renderData.custom;
 * CustomLayoutElement *customElement = (CustomLayoutElement *)config->customData;
 * if (!customElement) continue;
 * switch (customElement->type) {
 * case CUSTOM_LAYOUT_ELEMENT_TYPE_3D_MODEL: {
 * Clay_BoundingBox rootBox = renderCommands.internalArray[0].boundingBox;
 * float scaleValue = CLAY__MIN(CLAY__MIN(1, 768 / rootBox.height) * CLAY__MAX(1, rootBox.width / 1024), 1.5f);
 * Ray positionRay = GetScreenToWorldPointWithZDistance((Vector2) { renderCommand->boundingBox.x + renderCommand->boundingBox.width / 2, renderCommand->boundingBox.y + (renderCommand->boundingBox.height / 2) + 20 }, Raylib_camera, (int)roundf(rootBox.width), (int)roundf(rootBox.height), 140);
 * BeginMode3D(Raylib_camera);
 * DrawModel(customElement->customData.model.model, positionRay.position, customElement->customData.model.scale * scaleValue, WHITE);        // Draw 3d model with texture
 * EndMode3D();
 * break;
 * }
 * default: break;
 * }
 * break;
 * }
 * default: {
 * printf("Error: unhandled render command.");
 * exit(1);
 * }
 * }
 * }
 * }
 *
 */