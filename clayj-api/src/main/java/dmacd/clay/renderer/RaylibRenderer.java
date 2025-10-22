package dmacd.clay.renderer;

import dmacd.clay.Clay;
import dmacd.ffm.clay.*;
import dmacd.ffm.raylib.*;

import java.lang.foreign.*;
import java.util.ArrayList;
import java.util.List;

import static dmacd.ffm.raylib.RayFFM.*;

// copied from clay_renderer. converted from C to Java FFM nearly verbatim initially
// and then javafied over time
// https://github.com/nicbarker/clay/blob/v0.14/renderers/raylib/clay_renderer_raylib.c
public class RaylibRenderer {
    private static final MemorySegment Raylib_camera = Raylib.Camera.allocate(Arena.global());
    public static final MemorySegment RAYLIB_COLOR_WHITE = globalRaylibColor(255, 255, 255, 255);
    public static MemorySegment globalRaylibColor(int r, int g, int b, int a) {
        var color = Raylib.Color.allocate(Arena.global());
        Raylib.Color.r(color, (byte)r);
        Raylib.Color.g(color, (byte)g);
        Raylib.Color.b(color, (byte)b);
        Raylib.Color.a(color, (byte)a);
        return color;
    }

    public static void ClayColorToRaylibColor(MemorySegment clayColor, MemorySegment raylibColor) {
        var r = Math.round(Clay_Color.r(clayColor));
        var g = Math.round(Clay_Color.g(clayColor));
        var b = Math.round(Clay_Color.b(clayColor));
        var a = Math.round(Clay_Color.a(clayColor));
        Raylib.Color.r(raylibColor, (byte) r);
        Raylib.Color.g(raylibColor, (byte) g);
        Raylib.Color.b(raylibColor, (byte) b);
        Raylib.Color.a(raylibColor, (byte) a);
    }

    // copied from clay_renderer. converted from C to Java FFM
    public static /* Clay_Dimensions */ MemorySegment Raylib_MeasureText(
            /* (Clay_StringSlice) */ MemorySegment text,
            /* (Clay_TextElementConfig *) */  MemorySegment config,
            /* (void *) */ MemorySegment userData) {
        try (Arena arena = Clay.scopedArena()) {
            var msTextSize = Clay_Dimensions.allocate(arena);

            float maxTextWidth = 0.0f;
            float lineTextWidth = 0;
            int maxLineCharCount = 0;
            int lineCharCount = 0;
            var msConfig = Clay_TextElementConfig.reinterpret(config, arena, null);

            float textHeight = Clay_TextElementConfig.fontSize(msConfig);// config->fontSize;

            int fontId = Clay_TextElementConfig.fontId(msConfig);
            // there might be more fonts than fontId + 1,
            // but we need to get FFM to actuate at least that much memory to be accessible
            var msFontArray = Raylib.Font.reinterpret(userData, fontId + 1, arena, null);
            var msFont = Raylib.Font.asSlice(msFontArray, fontId);

            // Font failed to load, likely the fonts are in the wrong place relative to the execution dir.
            // RayLib ships with a default font, so we can continue with that built in one.
            var msGlyphs = Raylib.Font.glyphs(msFontArray);
            if (msGlyphs == MemorySegment.NULL) {
                msFont = RayFFM.GetFontDefault(arena);
                msGlyphs = Raylib.Font.glyphs(msFont);
            }

            float scaleFactor = textHeight / (float) Raylib.Font.baseSize(msFont);

            int length = Clay_StringSlice.length(text);
            var chars = Clay_StringSlice.chars(text).reinterpret(length);


            for (int i = 0; i < length; ++i, lineCharCount++) {
                int c = chars.getAtIndex(ValueLayout.JAVA_BYTE, i) & 0xFF;
                if (c == 0) continue;
                if (c == '\n') {
                    maxTextWidth = Math.max(maxTextWidth, lineTextWidth);
                    maxLineCharCount = Math.max(maxLineCharCount, lineCharCount);
                    lineTextWidth = 0;
                    lineCharCount = 0;
                    continue;
                }
                int index = c - 32;
                msGlyphs = Raylib.GlyphInfo.reinterpret(msGlyphs, index + 1, arena, null);

                var msGlyph = Raylib.GlyphInfo.asSlice(msGlyphs, index);
                var msRecs = Raylib.Font.recs(msFont);
                msRecs = Raylib.Rectangle.reinterpret(msRecs, index + 1, arena, null);
                var msRec = Raylib.Rectangle.asSlice(msRecs, index);

                if (Raylib.GlyphInfo.advanceX(msGlyph) != 0) lineTextWidth += Raylib.GlyphInfo.advanceX(msGlyph);
                else lineTextWidth += (Raylib.Rectangle.width(msRec) + Raylib.GlyphInfo.offsetX(msGlyph));
            }

            maxTextWidth = Math.max(maxTextWidth, lineTextWidth);
            maxLineCharCount = Math.max(maxLineCharCount, lineCharCount);

            var letterSpacing = Clay_TextElementConfig.letterSpacing(msConfig);
            Clay_Dimensions.width(msTextSize, maxTextWidth * scaleFactor + (lineCharCount * letterSpacing));
            Clay_Dimensions.height(msTextSize, textHeight);
            return msTextSize;
        }
    }

    public static
        /*(Ray)*/ MemorySegment GetScreenToWorldPointWithZDistance(
            Arena arena, // new
            MemorySegment /*(Vector2)*/ position, MemorySegment /*(Camera)*/ camera,
            int screenWidth, int screenHeight, float zDistance) {
        var ray = Raylib.Ray.allocate(arena);

        // Calculate normalized device coordinates
        // NOTE: y value is negative
        float x = (2.0f * Raylib.Vector2.x(position)) / (float) screenWidth - 1.0f;
        float y = 1.0f - (2.0f * Raylib.Vector2.y(position)) / (float) screenHeight;
        float z = 1.0f;

        // Store values in a vector
        var deviceCoords = Raylib.Vector3.allocate(arena);
//        { x, y, z };
        Raylib.Vector3.x(deviceCoords, x);
        Raylib.Vector3.x(deviceCoords, y);
        Raylib.Vector3.x(deviceCoords, z);

        // Calculate view matrix from camera look at
        var msMatView = MatrixLookAt(arena,
                Raylib.Camera.position(camera), Raylib.Camera.target(camera), Raylib.Camera.up(camera));

        var msMMatProj = MatrixIdentity(arena);

        if (Raylib.Camera.projection(camera) == CAMERA_PERSPECTIVE()) {
            // Calculate projection matrix from perspective
            msMMatProj = MatrixPerspective(arena, Raylib.Camera.fovy(camera) * DEG2RAD(),
                    ((double) screenWidth / (double) screenHeight), 0.01f, zDistance);
        } else if (Raylib.Camera.projection(camera) == CAMERA_ORTHOGRAPHIC()) {
            double aspect = (double) screenWidth / (double) screenHeight;
            double top = Raylib.Camera.fovy(camera) / 2.0;
            double right = top * aspect;

            // Calculate projection matrix from orthographic
            msMMatProj = MatrixOrtho(arena, -right, right, -top, top, 0.01, 1000.0);
        }
        var tmpV3 = Raylib.Vector3.allocate(arena);
        Raylib.Vector3.x(tmpV3, Raylib.Vector3.x(deviceCoords));
        Raylib.Vector3.y(tmpV3, Raylib.Vector3.y(deviceCoords));
        Raylib.Vector3.z(tmpV3, 0);
        // Unproject far/near points
        var nearPoint = Vector3Unproject(arena, tmpV3, msMMatProj, msMatView);

        Raylib.Vector3.z(tmpV3, 1.0f);
        var farPoint = Vector3Unproject(arena, tmpV3, msMMatProj, msMatView);

        // Calculate normalized direction vector
        var direction = Vector3Normalize(arena, Vector3Subtract(arena, farPoint, nearPoint));

        Raylib.Ray.position(ray, farPoint);

        // Apply calculated vectors to ray
        Raylib.Ray.direction(ray, direction);

        return ray;
    }
    // todo:  this is a stand in for a growable buffer
    /// Used to hold the buffer that we copy clay strings to raylib strings to measure text
    private static class GrowableBufferArena {
        private Arena arena = Arena.ofConfined();
        MemorySegment ms = arena.allocate(1024);
        MemorySegment getBuffer(long size) {
            if (size > ms.byteSize()) {
                var newSize = Math.max(ms.byteSize() * 2, size);
                arena.close();
                arena = Arena.ofConfined();
                ms = arena.allocate(newSize);
            }
            return ms;
        }
        void close() { arena.close(); }
    }

    private static GrowableBufferArena tempRenderBufferArena;

    /**
     * main arena for raylib version
     * <p>
     * this should only be used directly to allocate variables
     * that do not get re-allocated. It can also be used for
     * reinterpret() calls.
     * <p>
     * Use {@link Clay#scopedArena()} for reusable allocations
     */
    private static Arena mainArena;

    public static Arena Clay_Raylib_Initialize(int width, int height, String title, int flags) {
        mainArena = Arena.ofConfined();
        tempRenderBufferArena = new GrowableBufferArena();
        Clay.initialize(mainArena);
        var msTitle = mainArena.allocateFrom(title);
        SetConfigFlags(flags);
        InitWindow(width, height, msTitle);
        //    EnableEventWaiting();

        return mainArena;
    }

    // Call after closing the window to clean up the render buffer
    public static void Clay_Raylib_Close() {
        CloseWindow();
        mainArena.close();
        tempRenderBufferArena.close();
        tempRenderBufferArena = null;
        mainArena = null;
    }

    private static final Clay.RenderCommandType[] RENDER_COMMAND_TYPES = Clay.RenderCommandType.values();

    public static void Clay_Raylib_Render(/*(Clay_RenderCommandArray)*/ MemorySegment renderCommands, /*(Font*)*/ MemorySegment fonts) {
        var renderCommandsLength = Clay_RenderCommandArray.length(renderCommands);
        List<Clay.RenderCommand> list = new ArrayList<>();
        for (int i = 0; i < renderCommandsLength; i++) {
            var cmd = ClayFFM.Clay_RenderCommandArray_Get(renderCommands, i);
            cmd = Clay_RenderCommand.reinterpret(cmd, mainArena, null);
            list.add(new Clay.RenderCommand(cmd));
        }
        Clay_Raylib_Render(list, fonts);
    }
    public static void Clay_Raylib_Render(/*(Clay_RenderCommandArray)*/ List< Clay.RenderCommand> renderCommands, /*(Font*)*/ MemorySegment fonts) {
        // todo: with sufficiently large list of render commands it could be better to put the scopedArena inside the loop
        try (Arena arena = Clay.scopedArena()) {
            for (var renderCommand : renderCommands) {
                // todo: these should be methods of RenderCommand
                var command = RENDER_COMMAND_TYPES[Clay_RenderCommand.commandType(renderCommand.ms())];
                var renderData = Clay_RenderCommand.renderData(renderCommand.ms());
                var boundingBox = Clay_RenderCommand.boundingBox(renderCommand.ms());

                var bbX = Math.round(Clay_BoundingBox.x(boundingBox));
                var bbY = Math.round(Clay_BoundingBox.y(boundingBox));
                var bbWidth = Math.round(Clay_BoundingBox.width(boundingBox));
                var bbHeight = Math.round(Clay_BoundingBox.height(boundingBox));

                switch (command) {
                    case RENDER_COMMAND_TYPE_TEXT: {
                        var fontId = Clay_TextRenderData.fontId(renderData);
                        var msFontToUse = Raylib.Font.asSlice(fonts, fontId);

                        var msContents = Clay_TextRenderData.stringContents(renderData);
                        var msChars = Clay_StringSlice.chars(msContents);
                        int strlen = Clay_StringSlice.length(msContents) + 1;

                        // Raylib uses standard C strings so isn't compatible with cheap slices,
                        // we need to clone the string to append null terminator
                        var tempBuffer = tempRenderBufferArena.getBuffer(strlen);
                        MemorySegment.copy(msChars, 0, tempBuffer, 0, strlen - 1);
                        tempBuffer.setAtIndex(ValueLayout.JAVA_BYTE, strlen - 1, (byte) 0);

                        var v2 = Raylib.Vector2.allocate(arena);
                        Raylib.Vector2.x(v2, bbX);
                        Raylib.Vector2.y(v2, bbY);

                        var rayColor = Raylib.Color.allocate(arena);
                        var txtColor = Clay_TextRenderData.textColor(renderData);
                        ClayColorToRaylibColor(txtColor, rayColor);
                        DrawTextEx(msFontToUse, tempBuffer, v2, Clay_TextRenderData.fontSize(renderData),
                                Clay_TextRenderData.letterSpacing(renderData), rayColor);
                        break;
                    }
                    case RENDER_COMMAND_TYPE_IMAGE:// ClayFFM.CLAY_RENDER_COMMAND_TYPE_IMAGE(): {
                        var imageData = Clay_ImageRenderData.imageData(renderData);
                        var imageTexture = Raylib.Texture2D.reinterpret(imageData, arena, null);
                        var tintColor = Clay_ImageRenderData.backgroundColor(imageData);
                        if (Clay_Color.r(tintColor) == 0 && Clay_Color.g(tintColor) == 0 && Clay_Color.b(tintColor) == 0 && Clay_Color.a(tintColor) == 0) {
                            Clay_Color.r(tintColor, 255);
                            Clay_Color.g(tintColor, 255);
                            Clay_Color.b(tintColor, 255);
                            Clay_Color.a(tintColor, 255);
                        }
                        var rect = Raylib.Rectangle.allocate(arena);
                        var boundRect = Raylib.Rectangle.allocate(arena);
                        Raylib.Rectangle.x(boundRect, bbX);
                        Raylib.Rectangle.y(boundRect, bbX);
                        Raylib.Rectangle.width(boundRect, bbWidth);
                        Raylib.Rectangle.height(boundRect, bbHeight);
                        Raylib.Rectangle.x(rect, 0);
                        Raylib.Rectangle.y(rect, 0);
                        Raylib.Rectangle.width(rect, Raylib.Texture2D.width(imageTexture));
                        Raylib.Rectangle.height(rect, Raylib.Texture2D.height(imageTexture));
                        var rayColor = Raylib.Color.allocate(arena);
                        ClayColorToRaylibColor(tintColor, rayColor);
                        var emptyV2 = Raylib.Vector2.allocate(arena);
                        emptyV2.fill((byte) 0);
                        DrawTexturePro(imageTexture, rect, boundRect, emptyV2,
                                0, rayColor);
                        break;
                    case RENDER_COMMAND_TYPE_SCISSOR_START: {
                        BeginScissorMode(bbX, bbY, bbWidth, bbHeight);
                        break;
                    }
                    case RENDER_COMMAND_TYPE_SCISSOR_END: {
                        EndScissorMode();
                        break;
                    }
                    case RENDER_COMMAND_TYPE_RECTANGLE: {
                        var cornerRadius = Clay_RectangleRenderData.cornerRadius(renderData);
                        var clayBgc = Clay_RectangleRenderData.backgroundColor(renderData);
                        var bgColor = Raylib.Color.allocate(arena);
                        ClayColorToRaylibColor(clayBgc, bgColor);

                        if (Clay_CornerRadius.topLeft(cornerRadius) > 0) {
                            float radius = (Clay_CornerRadius.topLeft(cornerRadius) * 2) / (float) (Math.min(bbWidth, bbHeight));
                            var rectBB = Raylib.Rectangle.allocate(arena);
                            Raylib.Rectangle.x(rectBB, bbX);
                            Raylib.Rectangle.y(rectBB, bbY);
                            Raylib.Rectangle.width(rectBB, bbWidth);
                            Raylib.Rectangle.height(rectBB, bbHeight);
                            DrawRectangleRounded(rectBB, radius, 8, bgColor);
                        } else {
                            DrawRectangle(bbX, bbY, bbWidth, bbHeight, bgColor);
                        }
                        break;
                    }
                    case RENDER_COMMAND_TYPE_BORDER: {
                        var bw = Clay_BorderRenderData.width(renderData);
                        var cornerRadius = Clay_BorderRenderData.cornerRadius(renderData);
                        var clayColor = Clay_BorderRenderData.color(renderData);
                        var color = Raylib.Color.allocate(arena);
                        ClayColorToRaylibColor(clayColor, color);
                        // Left border
                        if (Clay_BorderWidth.left(bw) > 0) {
                            DrawRectangle(bbX, Math.round(Clay_BoundingBox.y(boundingBox) + Clay_CornerRadius.topLeft(cornerRadius)),
                                    Clay_BorderWidth.left(bw), Math.round(Clay_BoundingBox.height(boundingBox) - Clay_CornerRadius.topLeft(cornerRadius) - Clay_CornerRadius.bottomLeft(cornerRadius)),
                                    color);
                        }
                        // Right border
                        if (Clay_BorderWidth.right(bw) > 0) {
                            DrawRectangle(Math.round(Clay_BoundingBox.x(boundingBox) + Clay_BoundingBox.width(boundingBox) - Clay_BorderWidth.right(bw)),
                                    Math.round(Clay_BoundingBox.y(boundingBox) + Clay_CornerRadius.topRight(cornerRadius)),
                                    Clay_BorderWidth.right(bw), Math.round(Clay_BoundingBox.height(boundingBox) - Clay_CornerRadius.topRight(cornerRadius) - Clay_CornerRadius.bottomRight(cornerRadius)),
                                    color);
                        }
                        // Top border
                        if (Clay_BorderWidth.top(bw) > 0) {
                            DrawRectangle(Math.round(Clay_BoundingBox.x(boundingBox) + Clay_CornerRadius.topLeft(cornerRadius)), bbY,
                                    Math.round(Clay_BoundingBox.width(boundingBox) - Clay_CornerRadius.topLeft(cornerRadius) - Clay_CornerRadius.topRight(cornerRadius)),
                                    Clay_BorderWidth.top(bw), color);
                        }
                        // Bottom border
                        if (Clay_BorderWidth.bottom(bw) > 0) {
                            DrawRectangle(Math.round(Clay_BoundingBox.x(boundingBox) + Clay_CornerRadius.bottomLeft(cornerRadius)),
                                    Math.round(Clay_BoundingBox.y(boundingBox) + Clay_BoundingBox.height(boundingBox) - Clay_BorderWidth.bottom(bw)),
                                    Math.round(Clay_BoundingBox.width(boundingBox) - Clay_CornerRadius.bottomLeft(cornerRadius) - Clay_CornerRadius.bottomRight(cornerRadius)),
                                    Clay_BorderWidth.bottom(bw), color);
                        }
                        var center = Raylib.Vector2.allocate(arena);
                        if (Clay_CornerRadius.topLeft(cornerRadius) > 0) {
                            Raylib.Vector2.x(center, Math.round(Clay_BoundingBox.x(boundingBox) + Clay_CornerRadius.topLeft(cornerRadius)));
                            Raylib.Vector2.y(center, Math.round(Clay_BoundingBox.y(boundingBox) + Clay_CornerRadius.topLeft(cornerRadius)));

                            DrawRing(center, Math.round(Clay_CornerRadius.topLeft(cornerRadius) - Clay_BorderWidth.top(bw)),
                                    Clay_CornerRadius.topLeft(cornerRadius), 180, 270, 10, color);
                        }
                        if (Clay_CornerRadius.topRight(cornerRadius) > 0) {
                            Raylib.Vector2.x(center, Math.round(Clay_BoundingBox.x(boundingBox) + Clay_BoundingBox.width(boundingBox) - Clay_CornerRadius.topRight(cornerRadius)));
                            Raylib.Vector2.y(center, Math.round(Clay_BoundingBox.y(boundingBox) + Clay_CornerRadius.topRight(cornerRadius)));

                            DrawRing(center, Math.round(Clay_CornerRadius.topRight(cornerRadius) - Clay_BorderWidth.top(bw)),
                                    Clay_CornerRadius.topRight(cornerRadius), 270, 360, 10, color);
                        }
                        if (Clay_CornerRadius.bottomLeft(cornerRadius) > 0) {
                            Raylib.Vector2.x(center, Math.round(Clay_BoundingBox.x(boundingBox) + Clay_CornerRadius.bottomLeft(cornerRadius)));
                            Raylib.Vector2.y(center, Math.round(Clay_BoundingBox.y(boundingBox) + Clay_BoundingBox.height(boundingBox) - Clay_CornerRadius.bottomLeft(cornerRadius)));
                            DrawRing(center, Math.round(Clay_CornerRadius.bottomLeft(cornerRadius) - Clay_BorderWidth.bottom(bw)),
                                    Clay_CornerRadius.bottomLeft(cornerRadius), 90, 180, 10, color);
                        }
                        if (Clay_CornerRadius.bottomRight(cornerRadius) > 0) {
                            Raylib.Vector2.x(center, Math.round(Clay_BoundingBox.x(boundingBox) + Clay_BoundingBox.width(boundingBox) - Clay_CornerRadius.bottomRight(cornerRadius)));
                            Raylib.Vector2.y(center, Math.round(Clay_BoundingBox.y(boundingBox) + Clay_BoundingBox.height(boundingBox) - Clay_CornerRadius.bottomRight(cornerRadius)));

                            DrawRing(center, Math.round(Clay_CornerRadius.bottomRight(cornerRadius) - Clay_BorderWidth.bottom(bw)),
                                    Clay_CornerRadius.bottomRight(cornerRadius), 0.1f, 90, 10, color);
                        }
                        break;
                    }
                    case RENDER_COMMAND_TYPE_CUSTOM: {
                        var customElement = Clay_CustomRenderData.customData(renderData);
                        if (customElement.equals(MemorySegment.NULL)) continue;
                        customElement = CustomLayoutElement.reinterpret(customElement, arena, null);
                        final int CUSTOM_LAYOUT_ELEMENT_TYPE_3D_MODEL = 0;
                        switch (CustomLayoutElement.type(customElement)) {
                            case CUSTOM_LAYOUT_ELEMENT_TYPE_3D_MODEL: {
                                var rootCommand = renderCommands.get(0);
                                var rootBox = Clay_RenderCommand.boundingBox(rootCommand.ms());
                                var rootWidth = Clay_BoundingBox.width(rootBox);
                                var rootHeight = Clay_BoundingBox.height(rootBox);
                                float scaleValue = Math.min(Math.min(1.0f, 768 / rootHeight) * Math.max(1.0f, rootWidth / 1024), 1.5f);
                                var bbox = Raylib.Vector2.allocate(arena);
                                Raylib.Vector2.x(bbox,bbX + (float)(bbWidth / 2)); // @IDE cast
                                Raylib.Vector2.y(bbox,bbY + (float)(bbHeight / 2) + 20); // @IDE cast
                                var positionRay = GetScreenToWorldPointWithZDistance(arena, bbox,
                                        Raylib_camera, Math.round(rootWidth), Math.round(rootHeight), 140);
                                var modelData  = CustomLayoutElement.customData(customElement);
                                modelData = CustomLayoutElement_3DModel.reinterpret(modelData, arena, null);
                                var model = CustomLayoutElement_3DModel.model(modelData);
                                var modelScale = CustomLayoutElement_3DModel.scale(modelData);
                                BeginMode3D(Raylib_camera);
                                    DrawModel(model, Raylib.Ray.position(positionRay), modelScale * scaleValue, RAYLIB_COLOR_WHITE);        // Draw 3d model with texture
                                EndMode3D();
                                break;
                            }
                            default: break;
                    }
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
