package dmacd.clay.renderer;

import dmacd.ffm.clay.*;
import dmacd.ffm.raylib.*;
import org.w3c.dom.css.Rect;

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
        var msConfig = Clay_TextElementConfig.reinterpret(config, arena, (p) -> {
        });

        float textHeight = Clay_TextElementConfig.fontSize(msConfig);// config->fontSize;

        int fontId = Clay_TextElementConfig.fontId(msConfig);
        // there might be more fonts than fontId + 1,
        // but we need to get FFM to actuate at least that much memory to be accessible
        var msFontArray = Font.reinterpret(userData, fontId + 1, arena, (p) -> {
        });
        var msFont = Font.asSlice(msFontArray, fontId);

        // Font failed to load, likely the fonts are in the wrong place relative to the execution dir.
        // RayLib ships with a default font, so we can continue with that built in one.
        var msGlyphs = Font.glyphs(msFontArray);
        if (msGlyphs == MemorySegment.NULL) {
            msFont = RayFFM.GetFontDefault(arena);
            msGlyphs = Font.glyphs(msFont);
        }

        float scaleFactor = textHeight / (float) Font.baseSize(msFont);

        int length = Clay_StringSlice.length(text);
        var chars = Clay_StringSlice.chars(text).reinterpret(length);


        for (int i = 0; i < length; ++i, lineCharCount++) {
            int c = chars.getAtIndex(ValueLayout.JAVA_BYTE, i) & 0xFF;
            // todo: setting Clay_String to 1 byte too many (including null terminator)
            if(c == 0) continue;
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
            msRecs = Rectangle.reinterpret(msRecs, index + 1, arena, (p) -> {
            });
            var msRec = Rectangle.asSlice(msRecs, index);

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
//                msRC = renderCommands.get(ValueLayout.ADDRESS, 0);
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
                        var msChars = Clay_StringSlice.chars(msContents);
                        int strlen = Clay_StringSlice.length(msContents) + 1;

//                    if(strlen > temp_render_buffer_len) {
//                         Grow the temp buffer if we need a larger string
//                        if(temp_render_buffer) free(temp_render_buffer);
//                        temp_render_buffer = (char *) malloc(strlen);
//                        temp_render_buffer_len = strlen;
//                    }
                        // todo: something like the temp_buffer solution for memory allocation reduction /reuse
                        var msTemp = arena.allocate(strlen);
                        MemorySegment.copy(msChars, 0, msTemp, 0, strlen - 1);
                        msTemp.setAtIndex(ValueLayout.JAVA_BYTE, strlen - 1, (byte)0);

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
                        var cornerRadius = Clay_RectangleRenderData.cornerRadius(renderData);
                        var clayBgc = Clay_RectangleRenderData.backgroundColor(renderData);
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
//                    var config = Clay_RenderData.rectangle(renderData);
                    var bw = Clay_BorderRenderData.width(renderData);
                    var cornerRadius = Clay_BorderRenderData.cornerRadius(renderData);
                    var clayColor = Clay_BorderRenderData.color(renderData);
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
                        System.out.println("Custom");
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
