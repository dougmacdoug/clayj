package dmacd.clay.java2d;

import dmacd.clay.Clay;
import dmacd.ffm.clay.*;

import java.awt.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;

// modified renderer to work with Swing Graphics2D
// https://github.com/nicbarker/clay/blob/v0.14/renderers/raylib/clay_renderer_raylib.c
public class Java2DRenderer {

    static Color clayColorToJava(MemorySegment clayColor) {
        return new Color(
                (int) Clay_Color.r(clayColor),
                (int)Clay_Color.g(clayColor),
                (int)Clay_Color.b(clayColor),
                (int)Clay_Color.a(clayColor)
        );
    }
    public  static Graphics graphics;
    public static Font theFont;
    public  static FontMetrics fontMetrics;

    // copied from clay_renderer. converted from C to Java FFM
    public static /* Clay_Dimensions */ MemorySegment measureText(
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

            // todo: cache derived fonts in a map, id->font -> [] sizes
            var str = Clay.sliceToString(text);
            var font = theFont.deriveFont(Font.PLAIN, textHeight);
            var metrics = graphics.getFontMetrics(font);
            var bounds =metrics.getStringBounds(str, graphics);
//
            maxTextWidth = Math.round(bounds.getWidth());
//
            var letterSpacing = Clay_TextElementConfig.letterSpacing(msConfig);
            Clay_Dimensions.width(msTextSize, maxTextWidth );// + (lineCharCount * letterSpacing));
            Clay_Dimensions.height(msTextSize, textHeight);
            return msTextSize;
        }
    }

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

    public static Arena swingInitialize(int width, int height, String title, int flags) {
        mainArena = Arena.ofConfined();
        Clay.initialize(mainArena);
        var msTitle = mainArena.allocateFrom(title);
        return mainArena;
    }

    // Call after closing the window to clean up the render buffer
    public static void swingClose() {
        mainArena.close();
        mainArena = null;
    }

    private static final Clay.RenderCommandType[] RENDER_COMMAND_TYPES = Clay.RenderCommandType.values();

    public static void swingRender(/*(Clay_RenderCommandArray)*/ List< Clay.RenderCommand> renderCommands, Graphics2D gfx) {
        // todo: with sufficiently large list of render commands it could be better to put the scopedArena inside the loop
        try (Arena arena = Clay.scopedArena()) {
            gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            for (var renderCommand : renderCommands) {
                // todo: these should be methods of RenderCommand
                var command = RENDER_COMMAND_TYPES[Clay_RenderCommand.commandType(renderCommand.ms())];
                var renderData = renderCommand.renderData();
                var boundingBox = renderCommand.boundingBox();

                var bbX = Math.round(boundingBox.x());
                var bbY = Math.round(boundingBox.y());
                var bbWidth = Math.round(boundingBox.width());
                var bbHeight = Math.round(boundingBox.height());

                switch (command) {
                    case RENDER_COMMAND_TYPE_TEXT: {
                        var msContents = Clay_TextRenderData.stringContents(renderData);
                        var contents = Clay.sliceToString(msContents);
                        var txtColor = Clay_TextRenderData.textColor(renderData);
                        var fontSize = Clay_TextRenderData.fontSize(renderData);
                        gfx.setColor(clayColorToJava(txtColor));
                        Font f = theFont.deriveFont(Font.PLAIN, fontSize);
                        gfx.setFont(f);
                        gfx.drawString(contents, bbX , bbY + bbHeight);
                        break;
                    }
                    case RENDER_COMMAND_TYPE_IMAGE:// ClayFFM.CLAY_RENDER_COMMAND_TYPE_IMAGE(): {
                        break;
                    case RENDER_COMMAND_TYPE_SCISSOR_START: {
                        Rectangle r = new Rectangle(bbX, bbY, bbWidth, bbHeight);
                        gfx.setClip(r);
                        break;
                    }
                    case RENDER_COMMAND_TYPE_SCISSOR_END: {
                        gfx.setClip(null);
                        break;
                    }
                    case RENDER_COMMAND_TYPE_RECTANGLE: {
                        var cornerRadius = Clay_RectangleRenderData.cornerRadius(renderData);
                        var clayBgc = Clay_RectangleRenderData.backgroundColor(renderData);
                        var bgColor = clayColorToJava(clayBgc);
                        gfx.setColor(bgColor);
                        if (Clay_CornerRadius.topLeft(cornerRadius) > 0) {
                            float r = Clay_CornerRadius.topLeft(cornerRadius) ;
                            // todo: radius needs some scaling based on width and height but raylib
                            //       example is way off, this is much closer
                            gfx.fillRoundRect(bbX, bbY, bbWidth, bbHeight, (int)r, (int)r*2);
                        } else {
                            gfx.fillRect(bbX, bbY, bbWidth, bbHeight);
                        }
                        break;
                    }
                    case RENDER_COMMAND_TYPE_BORDER: {
                        var bw = Clay_BorderRenderData.width(renderData);
                        var cornerRadius = Clay_BorderRenderData.cornerRadius(renderData);
                        var clayColor = Clay_BorderRenderData.color(renderData);
                        var color = clayColorToJava(clayColor);
                        gfx.setColor(color);
                        // Left border
                        if (Clay_BorderWidth.left(bw) > 0) {
                            gfx.fillRect(bbX, Math.round(boundingBox.y() + Clay_CornerRadius.topLeft(cornerRadius)),
                                    Clay_BorderWidth.left(bw), Math.round(boundingBox.height() - Clay_CornerRadius.topLeft(cornerRadius) - Clay_CornerRadius.bottomLeft(cornerRadius)));
                        }
                        // Right border
                        if (Clay_BorderWidth.right(bw) > 0) {
                            gfx.fillRect(Math.round(boundingBox.x() + boundingBox.width() - Clay_BorderWidth.right(bw)),
                                    Math.round(boundingBox.y() + Clay_CornerRadius.topRight(cornerRadius)),
                                    Clay_BorderWidth.right(bw), Math.round(boundingBox.height() - Clay_CornerRadius.topRight(cornerRadius) - Clay_CornerRadius.bottomRight(cornerRadius)));
                        }
                        // Top border
                        if (Clay_BorderWidth.top(bw) > 0) {
                            gfx.fillRect(Math.round(boundingBox.x() + Clay_CornerRadius.topLeft(cornerRadius)), bbY,
                                    Math.round(boundingBox.width() - Clay_CornerRadius.topLeft(cornerRadius) - Clay_CornerRadius.topRight(cornerRadius)),
                                    Clay_BorderWidth.top(bw));
                        }
                        // Bottom border
                        if (Clay_BorderWidth.bottom(bw) > 0) {
                            gfx.fillRect((Math.round(boundingBox.x() + Clay_CornerRadius.bottomLeft(cornerRadius))),
                                    Math.round(boundingBox.y() + boundingBox.height() - Clay_BorderWidth.bottom(bw)),
                                    Math.round(boundingBox.width() - Clay_CornerRadius.bottomLeft(cornerRadius) - Clay_CornerRadius.bottomRight(cornerRadius)),
                                    Clay_BorderWidth.bottom(bw));
                        }
                        // todo: the adjustments to the border arc (-1/+1) were arbitrary to get the demo working
                        //       probably just need to adjust based on border width.. also, drawArc probably wont
                        //       work with wider borders either (need to test with line thickness)
                        if (Clay_CornerRadius.topLeft(cornerRadius) > 0) {
                            var width = Math.round(Clay_CornerRadius.topLeft(cornerRadius) - Clay_BorderWidth.top(bw));
                            var height = (int)Clay_CornerRadius.bottomLeft(cornerRadius);
                            gfx.drawArc(bbX, bbY, width*2, height*2,90, 90);
                        }
                        if (Clay_CornerRadius.topRight(cornerRadius) > 0) {
                            var width = Math.round(Clay_CornerRadius.topRight(cornerRadius) - Clay_BorderWidth.top(bw)) * 2;
                            var height = (int)Clay_CornerRadius.topRight(cornerRadius) * 2;
                            gfx.drawArc(bbX + bbWidth - width - 2, bbY, width + 1, height, 0, 90);
                        }
                        if (Clay_CornerRadius.bottomLeft(cornerRadius) > 0) {
                            var width = Math.round(Clay_CornerRadius.bottomLeft(cornerRadius) - Clay_BorderWidth.bottom(bw)) * 2;
                            var height = (int)Clay_CornerRadius.bottomLeft(cornerRadius) * 2;
                            gfx.drawArc(bbX, bbY + bbHeight - height -1, width, height, 180, 90);
                        }
                        if (Clay_CornerRadius.bottomRight(cornerRadius) > 0) {
                            var width = Math.round(Clay_CornerRadius.bottomRight(cornerRadius) - Clay_BorderWidth.bottom(bw)) * 2;
                            var height = (int)Clay_CornerRadius.bottomRight(cornerRadius) * 2;
                            gfx.drawArc(bbX + bbWidth - width -1, bbY + bbHeight - height -1, width, height, 270, 90);

                        }
                        break;
                    }
                    case RENDER_COMMAND_TYPE_CUSTOM:
                        break;
                    default: {
                        System.err.println("Error: unhandled render command.");
                        System.exit(1);
                    }
                }
            }
        }
    }
}
