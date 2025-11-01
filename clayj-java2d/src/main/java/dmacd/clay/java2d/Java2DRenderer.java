package dmacd.clay.java2d;

import dmacd.clay.Clay;
import dmacd.ffm.clay.Clay_Color;
import dmacd.ffm.clay.Clay_RenderCommand;

import java.awt.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;

// modified renderer to work with Swing Graphics2D
// https://github.com/nicbarker/clay/blob/v0.14/renderers/raylib/clay_renderer_raylib.c
public class Java2DRenderer {

    public static Color clayColorToJava(Clay.Color clayColor) {
        return clayColorToJava(clayColor.ms());
    }
    public static Color clayColorToJava(MemorySegment clayColor) {
        return new Color(
                (int)Clay_Color.r(clayColor),
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
        var textSize = Clay.Dimensions.scoped(0, 0);

        float maxTextWidth = 0.0f;
        float lineTextWidth = 0;
        int maxLineCharCount = 0;
        int lineCharCount = 0;
        var textConfig = new Clay.TextElementConfig(config);

        float textHeight = textConfig.fontSize();// config->fontSize;

        // todo: cache derived fonts in a map, id->font -> [] sizes
        var str = new Clay.StringSlice(text).toJavaString();
        var font = theFont.deriveFont(Font.PLAIN, textHeight);
        var metrics = graphics.getFontMetrics(font);
        var bounds = metrics.getStringBounds(str, graphics);
//
        maxTextWidth = Math.round(bounds.getWidth());
//
        var letterSpacing = textConfig.letterSpacing();
        textSize.width(maxTextWidth);// + (lineCharCount * letterSpacing));
        textSize.height(textHeight);
        return textSize.ms();
    }

    public static Arena java2DInitialize(int width, int height, String title, int flags) {
        var arena = Arena.ofConfined();
        Clay.initialize(arena);
        return arena;
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
                        var textRD = new Clay.TextRenderData(renderData);
                        var contents = textRD.stringContents().toJavaString();
                        var txtColor = textRD.textColor();
                        var fontSize = textRD.fontSize();
                        gfx.setColor(clayColorToJava(txtColor));
                        // todo: use fontId() and fontMap
                        Font f = theFont.deriveFont(Font.PLAIN, fontSize);
                        gfx.setFont(f);
                        // todo: this is off by a bit (too low)
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
                        var rectRD = new Clay.RectangleRenderData(renderData);
                        var bgColor = clayColorToJava(rectRD.backgroudnColor());
                        gfx.setColor(bgColor);
                        var cornerRadius = rectRD.cornerRadius();
                        if (cornerRadius.topLeft() > 0) {
                            float r = cornerRadius.topLeft() ;
                            // todo: radius needs some scaling based on width and height but raylib
                            //       example is way off, this is much closer
                            gfx.fillRoundRect(bbX, bbY, bbWidth, bbHeight, (int)r, (int)r*2);
                        } else {
                            gfx.fillRect(bbX, bbY, bbWidth, bbHeight);
                        }
                        break;
                    }
                    case RENDER_COMMAND_TYPE_BORDER: {
                        var borderRD = new Clay.BorderRenderData(renderData);
                        var bw = borderRD.width();
                        var cornerRadius = borderRD.cornerRadius();
                        var color = clayColorToJava(borderRD.color());
                        gfx.setColor(color);
                        // Left border
                        if (bw.left() > 0) {
                            gfx.fillRect(bbX, Math.round(boundingBox.y() + cornerRadius.topLeft()),
                                    bw.left(), Math.round(boundingBox.height() - cornerRadius.topLeft() - cornerRadius.bottomLeft()));
                        }
                        // Right border
                        if (bw.right() > 0) {
                            gfx.fillRect(Math.round(boundingBox.x() + boundingBox.width() - bw.right()),
                                    Math.round(boundingBox.y() + cornerRadius.topRight()),
                                    bw.right(), Math.round(boundingBox.height() - cornerRadius.topRight() - cornerRadius.bottomRight()));
                        }
                        // Top border
                        if (bw.top() > 0) {
                            gfx.fillRect(Math.round(boundingBox.x() + cornerRadius.topLeft()), bbY,
                                    Math.round(boundingBox.width() - cornerRadius.topLeft() - cornerRadius.topRight()),
                                    bw.top());
                        }
                        // Bottom border
                        if (bw.bottom() > 0) {
                            gfx.fillRect((Math.round(boundingBox.x() + cornerRadius.bottomLeft())),
                                    Math.round(boundingBox.y() + boundingBox.height() - bw.bottom()),
                                    Math.round(boundingBox.width() - cornerRadius.bottomLeft() - cornerRadius.bottomRight()),
                                    bw.bottom());
                        }
                        // todo: the adjustments to the border arc (-1/+1) were arbitrary to get the demo working
                        //       probably just need to adjust based on border width.. also, drawArc probably wont
                        //       work with wider borders either (need to test with line thickness)
                        if (cornerRadius.topLeft() > 0) {
                            var width = Math.round(cornerRadius.topLeft() - bw.top());
                            var height = (int)cornerRadius.bottomLeft();
                            gfx.drawArc(bbX, bbY, width*2, height*2,90, 90);
                        }
                        if (cornerRadius.topRight() > 0) {
                            var width = Math.round(cornerRadius.topRight() - bw.top()) * 2;
                            var height = (int)cornerRadius.topRight() * 2;
                            gfx.drawArc(bbX + bbWidth - width - 2, bbY, width + 1, height, 0, 90);
                        }
                        if (cornerRadius.bottomLeft() > 0) {
                            var width = Math.round(cornerRadius.bottomLeft() - bw.bottom()) * 2;
                            var height = (int)cornerRadius.bottomLeft() * 2;
                            gfx.drawArc(bbX, bbY + bbHeight - height -1, width, height, 180, 90);
                        }
                        if (cornerRadius.bottomRight() > 0) {
                            var width = Math.round(cornerRadius.bottomRight() - bw.bottom()) * 2;
                            var height = (int)cornerRadius.bottomRight() * 2;
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
