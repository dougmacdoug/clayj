package dmacd.clay;

import dmacd.ffm.clay.*;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

import static dmacd.clay.Clay.FloatingElementConfig.AttachPoint.CLAY_ATTACH_POINT_CENTER_BOTTOM;
import static dmacd.clay.Clay.FloatingElementConfig.AttachPoint.CLAY_ATTACH_POINT_LEFT_TOP;
import static dmacd.clay.Clay.LayoutConfig.Sizing.fixed;
import static dmacd.clay.Clay.LayoutConfig.Sizing.percent;
import static dmacd.clay.Clay.LayoutConfig.Sizing.grow;
import static dmacd.clay.Clay.LayoutConfig.Sizing.fit;
import static dmacd.ffm.clay.ClayFFM.Clay__OpenTextElement;
import static dmacd.ffm.clay.ClayFFM.Clay__StoreTextElementConfig;

/**
 * Main library class works as a util for all access to Clay library
 * In its instance form it uses builder pattern to store Clay_ElementDeclaration
 */
public class Clay {
    public static final Arena SHARED_ARENA = Arena.ofShared();

    public static MemorySegment SHARED_COLOR(float r, float g, float b, float a) {
        return createColor(r, g, b, a, SHARED_ARENA);
    }

    public static MemorySegment SHARED_C_STRING(String s) { return SHARED_ARENA.allocateFrom(s); }
    public static MemorySegment SHARED_CLAY_STRING(String s) {
        var cstr = SHARED_ARENA.allocateFrom(s);
        return clayString(cstr, SHARED_ARENA);
    }


    public static MemorySegment clayString(MemorySegment cstr, SegmentAllocator allocator) {
        var ms = Clay_String.allocate(allocator);
        Clay_String.chars(ms, cstr);
        Clay_String.length(ms, (int)cstr.byteSize());
        Clay_String.isStaticallyAllocated(ms, true); // todo: maybe only if alloc==SHARED?
        return ms;
    }

    public static MemorySegment createColor(float r, float g, float b, float a, SegmentAllocator allocator) {
        var color = Clay_Color.allocate(allocator);
        Clay_Color.r(color, r);
        Clay_Color.g(color, g);
        Clay_Color.b(color, b);
        Clay_Color.a(color, a);
        return color;
    }

    public static final int CLAY_LEFT_TO_RIGHT = 0; // ClayFFM.CLAY_LEFT_TO_RIGHT();
    public static final int CLAY_TOP_TO_BOTTOM = 1; // ClayFFM.CLAY_TOP_TO_BOTTOM();
//    public static MethodHandle errorHandlerHandle;

    static {
        System.loadLibrary("clay");
//        try {
//            errorHandlerHandle = MethodHandles.lookup().findStatic(
//            errorHandlerHandle = MethodHandles.lookup().findStatic(
//                    Clay.class, "errorHandler", MethodType.methodType(void.class, MemorySegment.class, MemorySegment.class));
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
    }

//    public static MemorySegment clayErrorCallbackPtr = Linker.nativeLinker().upcallStub(errorHandlerHandle,
//            FunctionDescriptor.ofVoid(
//                    Clay_ErrorData.layout(),
//                    ValueLayout.ADDRESS)
//            , Arena.global());


    public static void clay(Clay element) {
        clay(element, null);
    }

    public static void clay(Clay element, Runnable children) {
        ClayFFM.Clay__OpenElement();
        ClayFFM.Clay__ConfigureOpenElement(element.elmMs);
        if (children != null) {
            children.run();
        }
        ClayFFM.Clay__CloseElement();
        elementQueue.offer(element.elmMs);
        element.elmMs = null;
    }
    public static void CLAY_TEXT(String text, Function<TextElementConfig, TextElementConfig> textConfig) {
        clayText(text, textConfig);
    }
    // todo: need one for global string literals and dynamic strings
    public static void clayText(String text, Function<TextElementConfig, TextElementConfig> textConfig) {
        var ct = makeClayText(text);
        var cfgMs = nextTextDeclaration();
        textConfig.apply(new TextElementConfig(cfgMs));
       var cfg = Clay__StoreTextElementConfig(cfgMs);
       Clay__OpenTextElement(ct, cfg);

    }

    public record TextElementConfig(MemorySegment ms) {
        public TextElementConfig letterSpacing(int spacing) {
            Clay_TextElementConfig.letterSpacing(ms, (short) spacing);
            return this;
        }

        public TextElementConfig fontId(int fontId) {
            Clay_TextElementConfig.fontId(ms, (short) fontId);
            return this;
        }

        public TextElementConfig fontSize(int fontSize) {
            Clay_TextElementConfig.fontSize(ms, (short) fontSize);
            return this;
        }

        public TextElementConfig textAlignment(int textAlignment) {
            Clay_TextElementConfig.textAlignment(ms, textAlignment);
            return this;
        }

        public TextElementConfig lineHeight(int lineHeight) {
            Clay_TextElementConfig.lineHeight(ms, (short) lineHeight);
            return this;
        }

        public TextElementConfig wrapMode(int wrapMode) {
            Clay_TextElementConfig.wrapMode(ms, wrapMode);
            return this;
        }

        public TextElementConfig textColor(Color color) {
            return textColor(color.r, color.g, color.b, color.a);
        }

        public TextElementConfig textColor(float r, float g, float b, float a) {
            var colorMS = Clay_TextElementConfig.textColor(ms);
            Clay_Color.r(colorMS, r);
            Clay_Color.g(colorMS, g);
            Clay_Color.b(colorMS, b);
            Clay_Color.a(colorMS, a);
            return this;
        }
        public TextElementConfig userData(MemorySegment userData) {
            Clay_TextElementConfig.userData(ms, userData);
            return this;
        }
    }

    private static MemorySegment allocateGlobalClayString(String s) {
        return allocateClayString(Arena.global(), s);
    }

    private static MemorySegment allocateClayString(SegmentAllocator allocator, String s) {
        // need to allocate the "chars" and the Clay_String struct to hold them
        var clayStr = Clay_String.allocate(allocator);
        var utf8 = allocator.allocateFrom(s);
        Clay_String.chars(clayStr, utf8);
        Clay_String.isStaticallyAllocated(clayStr, false); //allocator == Arena.global());
        Clay_String.length(clayStr, (int) utf8.byteSize());
        return clayStr;
    }

    private static Map<String, MemorySegment> globalStrings = new HashMap<String, MemorySegment>();

    private static MemorySegment makeClayText(String s) {
        return globalStrings.computeIfAbsent(s, Clay::allocateGlobalClayString);
    }
    private MemorySegment elmMs;

    private Clay(String id) {
        elmMs = nextElementDeclaration();
        var idMs = Clay_ElementDeclaration.id(elmMs);
// todo: handle other id types.. CLAY_IDI, CLAY_SID, CLAY_SIDI
        var clayStringMS = globalStrings.computeIfAbsent(id, Clay::allocateGlobalClayString);
        // todo: this puts the ElementId struct right inside the declaration struct
        ClayFFM.Clay__HashString((b, a) -> {
//            System.out.println("******* HASH  ****************");
//            System.out.println("bytes: " + b + " align: " + a + " MS: " +idMs.byteSize());
            return idMs;
        }, clayStringMS, 0, 0);
        this.id = id;
    }

    MemorySegment idMs;
    private String id;

    public static Clay id(String id) {
        return new Clay(id);
    }

    public static void errorHandler(MemorySegment errorData) {
        var ms = Clay_ErrorData.errorText(errorData);
        var stringSegment = Clay_String.chars(ms);
        var len = Clay_String.length(ms);
        byte[] stringBytes = stringSegment.asSlice(0, len).toArray(ValueLayout.JAVA_BYTE);
        String javaString = new String(stringBytes, StandardCharsets.UTF_8);

        System.out.println("from java from c:  " + javaString );
    }

    // todo: maybe copy :: also maybe use a string pointer and lookup the image in the renderer
    public Clay image(MemorySegment imageData) {
        var ms = Clay_ElementDeclaration.image(elmMs);
        Clay_ImageElementConfig.imageData(ms, imageData);
        return this;
    }

    public Clay aspectRatio(float aspectRatio) {
        var ms = Clay_ElementDeclaration.aspectRatio(elmMs);
        Clay_AspectRatioElementConfig.aspectRatio(ms, aspectRatio);
        return this;
    }

    public Clay border(Function<BorderElementConfig, BorderElementConfig> b) {
        var ms = Clay_ElementDeclaration.border(elmMs);
        b.apply(new BorderElementConfig(ms));
        return this;
    }

    public static int CLAY_CORNER_RADIUS(int radius) {
        return radius;
    }

    public Clay cornerRadius(float radius) {
        return cornerRadius(radius, radius, radius, radius);
    }

    public Clay cornerRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        var ms = Clay_ElementDeclaration.cornerRadius(elmMs);
        Clay_CornerRadius.topLeft(ms, topLeft);
        Clay_CornerRadius.topRight(ms, topRight);
        Clay_CornerRadius.bottomLeft(ms, bottomLeft);
        Clay_CornerRadius.bottomRight(ms, bottomRight);
        return this;
    }
    // todo: not thread safe.. clayj not designed for thread safety, may revisit
    private static final Queue<MemorySegment> elementQueue = new ArrayDeque<>();

    private static final Queue<MemorySegment> textQueue = new ArrayDeque<>();

    private static MemorySegment nextElementDeclaration() {
        if(elementQueue.isEmpty()) {
            // this only happens when nesting
            // so after 10 elements deep in a nest it will allocate 10.
            // hundreds of elements at the same level will reuse the same memory
            var ms = Clay_ElementDeclaration.allocateArray(10, arena);
            for(int i = 0; i < 10; i++) {
                var element = Clay_ElementDeclaration.asSlice(ms, i);
                elementQueue.offer(element); // todo: test?
            }
        }
        var element = elementQueue.remove();
        element.fill((byte) 0);
        return element;
    }

    private static MemorySegment nextTextDeclaration() {
        if(textQueue.isEmpty()) {
            // this only happens when nesting
            // so after 10 elements deep in a nest it will allocate 10.
            // hundreds of elements at the same level will reuse the same memory
            var ms = Clay_TextElementConfig.allocateArray(10, arena);
            for(int i = 0; i < 10; i++) {
                var element = Clay_TextElementConfig.asSlice(ms, i);
                textQueue.offer(element); // todo: test?
            }
        }
        var element = textQueue.remove();
        element.fill((byte) 0);
        return element;
    }

    //    Clay_Context* Clay_Initialize(Clay_Arena arena, Clay_Dimensions layoutDimensions, Clay_ErrorHandler errorHandler)
    public Clay layout(Function<LayoutConfig, LayoutConfig> c) {
        var ms = Clay_ElementDeclaration.layout(elmMs);
        c.apply(new LayoutConfig(ms));
        return this;
    }

    public Clay floating(Function<FloatingElementConfig, FloatingElementConfig> settings) {
        var flMs = Clay_ElementDeclaration.floating(elmMs);
        settings.apply(new FloatingElementConfig(flMs));
        return this;
    }

    public static Function<LayoutConfig.Padding, LayoutConfig.Padding> CLAY_PADDING_ALL(int value) {
        return p -> p.all(value);
    }

    public Clay backgroundColor(Color c) {
        return backgroundColor(c.r, c.g, c.b, c.a);
    }


    public Clay backgroundColor(float r, float g, float b, float a) {
        var ms = Clay_ElementDeclaration.backgroundColor(elmMs);
        Clay_Color.r(ms, r);
        Clay_Color.g(ms, g);
        Clay_Color.b(ms, b);
        Clay_Color.a(ms, a);
        return this;
    }

    public static LayoutConfig.SizingAxis CLAY_SIZING_FIT(float min) {
        return fit(min);
    }

    public static LayoutConfig.SizingAxis CLAY_SIZING_FIT(float min, float max) {
        return fit(min, max);
    }

    public static LayoutConfig.SizingAxis CLAY_SIZING_GROW(float min) {
        return grow(min);
    }

    public static LayoutConfig.SizingAxis CLAY_SIZING_GROW(float min, float max) {
        return grow(min, max);
    }

    public static LayoutConfig.SizingAxis CLAY_SIZING_FIXED(float n) {
        return fixed(n);
    }

    public static LayoutConfig.SizingAxis CLAY_SIZING_PERCENT(float n) {
        return percent(n);
    }


    public record BorderWidth(MemorySegment ms) {
        public BorderWidth left(int left) {
            Clay_BorderWidth.left(ms, (short) left);
            return this;
        }

        public BorderWidth right(int right) {
            Clay_BorderWidth.right(ms, (short) right);
            return this;
        }

        public BorderWidth top(int top) {
            Clay_BorderWidth.top(ms, (short) top);
            return this;
        }

        public BorderWidth bottom(int bottom) {
            Clay_BorderWidth.bottom(ms, (short) bottom);
            return this;
        }

        public BorderWidth betweenChildren(int betweenChildren) {
            Clay_BorderWidth.betweenChildren(ms, (short) betweenChildren);
            return this;
        }
    }

    public static class BorderElementConfig {
        BorderWidth width; // Controls the widths of individual borders. At least one of these should be > 0 for a BORDER render command to be generated.
        MemorySegment ms;

        BorderElementConfig(MemorySegment ms) {
            this.ms = ms;
            var widthMs = Clay_BorderElementConfig.width(ms);
            width = new BorderWidth(widthMs); // Controls the widths of individual borders. At least one of these should be > 0 for a BORDER render command to be generated.
        }
        public BorderElementConfig color(Color c) {
            return color(c.r, c.g, c.b, c.a);
        }
        public BorderElementConfig color(float r, float g, float b, float a) {
            var colorMs = Clay_BorderElementConfig.color(ms);
            Clay_Color.r(colorMs, r);
            Clay_Color.g(colorMs, g);
            Clay_Color.b(colorMs, b);
            Clay_Color.a(colorMs, a);
            return this;
        }

        public BorderElementConfig width(Function<BorderWidth, BorderWidth> f) {
            f.apply(width);
            return this;
        }
    }

    public record ClipElementConfig(MemorySegment ms) {
        public ClipElementConfig horizontal(boolean on) {
            Clay_ClipElementConfig.horizontal(ms, on);
            return this;
        }

        public ClipElementConfig vertical(boolean on) {
            Clay_ClipElementConfig.vertical(ms, on);
            return this;
        }

        public ClipElementConfig childOffset(float x, float y) {
            var offset = Clay_ClipElementConfig.childOffset(ms);
            Clay_Vector2.x(offset, x);
            Clay_Vector2.y(offset, y);
            return this;
        }
    }


    public Clay clip(Function<ClipElementConfig, ClipElementConfig> c) {
        var ms = Clay_ElementDeclaration.clip(elmMs);
        c.apply(new ClipElementConfig(ms));
        return this;
    }


    private static Arena arena;

    public static void initialize(Arena arena) {
        Clay.arena = arena;
    }


    public static int minMemorySize() {
        return ClayFFM.Clay_MinMemorySize();
    }

    public record LayoutConfig(MemorySegment ms) {
        public LayoutConfig layoutDirection(int direction) {
            Clay_LayoutConfig.layoutDirection(ms, direction);
            return this;
        }
        public LayoutConfig padding(int left, int right, int top, int bottom) {
            var padMs = Clay_LayoutConfig.padding(ms);
            new Padding(padMs)
                .left(left)
                .right(right)
                .top(top)
                .bottom(bottom);
            return this;
        }

        public LayoutConfig padding(Function<Padding, Padding> p) {
            var padMs = Clay_LayoutConfig.padding(ms);
            p.apply(new Padding(padMs));
            return this;
        }

        public LayoutConfig sizing(Function<Sizing, Sizing> s) {
            var sizeMs = Clay_LayoutConfig.sizing(ms);
            s.apply(new Sizing(sizeMs));
            return this;
        }

        public LayoutConfig childGap(int gap) {
            Clay_LayoutConfig.childGap(ms, (short)gap);
            return this;
        }

        public record Padding(MemorySegment ms) {
            public Padding left(int left) {
                Clay_Padding.left(ms, (short)left);
                return this;
            }

            public Padding right(int right) {
                Clay_Padding.right(ms, (short)right);
                return this;
            }

            public Padding top(int top) {
                Clay_Padding.top(ms, (short)top);
                return this;
            }

            public Padding bottom(int bottom) {
                Clay_Padding.bottom(ms, (short)bottom);
                return this;
            }

            public Padding all(int all) {
                left(all);
                right(all);
                top(all);
                bottom(all);
                return this;
            }
        }

        public record Sizing(MemorySegment ms) {
            Sizing width(SizingAxis width) {
                var wMs = Clay_Sizing.width(ms);
                Clay_SizingAxis.type(wMs, width.type);
                var mmMs = Clay_SizingAxis.size.minMax(wMs);
                // size is a union
                // todo: should work, might need if/else for percent
                Clay_SizingMinMax.min(mmMs, width.minOrPercent);
                Clay_SizingMinMax.max(mmMs, width.max);
                return this;
            }

            Sizing height(SizingAxis height) {
                var hMs = Clay_Sizing.height(ms);
                Clay_SizingAxis.type(hMs, height.type);
                var mmMs = Clay_SizingAxis.size.minMax(hMs);
                // size is a union
                // todo: should work, might need if/else for percent
                Clay_SizingMinMax.min(mmMs, height.minOrPercent);
                Clay_SizingMinMax.max(mmMs, height.max);
                return this;
            }

            public static SizingAxis percent(float percent) {
                return new SizingAxis(percent, 0, ClayFFM.CLAY__SIZING_TYPE_PERCENT());
            }

            public static SizingAxis grow(float min) {
                return grow(min, Float.MAX_VALUE);
            }

            public static SizingAxis grow(float min, float max) {
                return new SizingAxis(min, max, ClayFFM.CLAY__SIZING_TYPE_GROW());
            }

            public static SizingAxis fit(float min) {
                return fit(min, Float.MAX_VALUE);
            }

            public static SizingAxis fit(float min, float max) {
                return new SizingAxis(min, max, ClayFFM.CLAY__SIZING_TYPE_FIT());
            }

            public static SizingAxis fixed(float value) {
                return new SizingAxis(value, value, ClayFFM.CLAY__SIZING_TYPE_FIXED());
            }
        }

        public record SizingAxis(
                float minOrPercent,
                float max,
                int type) {
        }
    }

    public record FloatingElementConfig(MemorySegment ms) {

        public FloatingElementConfig offset(float x, float y) {
            var v2 = Clay_FloatingElementConfig.offset(ms);
            Clay_Vector2.x(v2, x);
            Clay_Vector2.y(v2, y);
            return this;
        }

        public FloatingElementConfig expand(float width, float height) {
            var dims = Clay_FloatingElementConfig.expand(ms);
            Clay_Dimensions.width(dims, width);
            Clay_Dimensions.height(dims, height);
            return this;
        }

        public FloatingElementConfig parentId(String parentId) {
            // todo:
            //            this.parentId = parentId;
            return this;
        }

        public FloatingElementConfig attachTo(int attachTo) {
            Clay_FloatingElementConfig.attachTo(ms, attachTo);
            return this;
        }

        public FloatingElementConfig clipTo(int clipTo) {
            Clay_FloatingElementConfig.clipTo(ms, clipTo);
            return this;
        }

        public FloatingElementConfig pointerCaptureMode(int pointerCaptureMode) {
            Clay_FloatingElementConfig.pointerCaptureMode(ms, pointerCaptureMode);
            return this;
        }

        public FloatingElementConfig zIndex(int zIndex) {
            Clay_FloatingElementConfig.zIndex(ms, (short)zIndex);
            return this;
        }

        public FloatingElementConfig attachPoints(Function<FloatingAttachPoints, FloatingAttachPoints> f) {
            var apMs = Clay_FloatingElementConfig.attachPoints(ms);
            f.apply(new FloatingAttachPoints(apMs));
            return this;
        }

        public record FloatingAttachPoints(MemorySegment ms) {

            public FloatingAttachPoints element(int attachPoint) {
                Clay_FloatingAttachPoints.element(ms, attachPoint);
                return this;
            }

            public FloatingAttachPoints parent(int attachPoint) {
                Clay_FloatingAttachPoints.parent(ms, attachPoint);
                return this;
            }

            public FloatingAttachPoints parent(AttachPoint attachPoint) {
                return parent(attachPoint.getValue());
            }

            public FloatingAttachPoints element(AttachPoint attachPoint) {
                return element(attachPoint.getValue());
            }
        }

        public enum AttachPoint {
            CLAY_ATTACH_POINT_LEFT_TOP(ClayFFM.CLAY_ATTACH_POINT_LEFT_TOP()),
            CLAY_ATTACH_POINT_LEFT_CENTER(ClayFFM.CLAY_ATTACH_POINT_LEFT_CENTER()),
            CLAY_ATTACH_POINT_LEFT_BOTTOM(ClayFFM.CLAY_ATTACH_POINT_LEFT_BOTTOM()),
            CLAY_ATTACH_POINT_CENTER_TOP(ClayFFM.CLAY_ATTACH_POINT_CENTER_TOP()),
            CLAY_ATTACH_POINT_CENTER_CENTER(ClayFFM.CLAY_ATTACH_POINT_CENTER_CENTER()),
            CLAY_ATTACH_POINT_CENTER_BOTTOM(ClayFFM.CLAY_ATTACH_POINT_CENTER_BOTTOM()),
            CLAY_ATTACH_POINT_RIGHT_TOP(ClayFFM.CLAY_ATTACH_POINT_RIGHT_TOP()),
            CLAY_ATTACH_POINT_RIGHT_CENTER(ClayFFM.CLAY_ATTACH_POINT_RIGHT_CENTER()),
            CLAY_ATTACH_POINT_RIGHT_BOTTOM(ClayFFM.CLAY_ATTACH_POINT_RIGHT_BOTTOM());

            AttachPoint(int n) {
                this.value = n;
            }

            private final int value;

            public int getValue() {
                return value;
            }
        }
    }
    public static void CLAY(Clay element, Runnable childen) {
        clay(element, childen);
    }
    public final static Color COLOR_RED = new Color(255, 0, 0, 255);
    public record Color(float r, float g, float b, float a) {}
}
