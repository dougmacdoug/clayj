package dmacd.clay;

import dmacd.ffm.clay.*;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static dmacd.ffm.clay.ClayFFM.*;

/**
 * Main library class works as a util for all access to Clay library
 * In its instance form it uses builder pattern to store Clay_ElementDeclaration
 */
public class Clay {

    // todo: CLAY_AUTO_ID() macro
    // todo: proper element id handling (maybe ok, make sure its very ok) .. used in lots of calls
    // todo: clay_id_local (testing etc.. also parentId)

    static {
        System.loadLibrary("clay");
    }

//region Memory Management
//--------------------------------------------------------------------------------------------------
    // todo: make this configurable
    public static final Arena GLOBAL_ARENA = TrackedArena.global();

    /**
     * Memory Management Structures
     * these are used to reduce allocations
     * todo: explain more or point to readme
     */
    // global strings include the Clau_String struct and the string
    private static final Map<String, MemorySegment> globalStrings = new HashMap<>();
    // element queue holds element declarations in a stack
    private static final Queue<MemorySegment> elementQueue = new ArrayDeque<>();
    // string blocks are for dynamic strings keys are length: 4,8..128,136,144..256,512,1024
    // Clay_String structs belonging to dynamic strings are also stored here
    private static final Map<Long, List<MemorySegment>> stringBlocks = new HashMap<>();
    // dynamic string allocations (these get reclaimed at initializeStrings()
    private static final List<MemorySegment> allocatedStrings = new ArrayList<>();

//endregion

    private static void initializeStrings() {
        for (var ms : allocatedStrings) {
            var list = stringBlocks.computeIfAbsent(ms.byteSize(), (n) -> new ArrayList<>());
            list.add(ms);
        }
        allocatedStrings.clear();
    }

    private static final long CLAY_STRING_STRUCT_SIZE = Clay_String.layout().byteSize();

    private static MemorySegment nextDynamicClayString() {
        var list = stringBlocks.computeIfAbsent(CLAY_STRING_STRUCT_SIZE, n -> new ArrayList<>());
        MemorySegment ms;
        if (list.isEmpty()) {
            ms = arena.allocate(CLAY_STRING_STRUCT_SIZE);
        } else {
            ms = list.removeLast();
        }
        ms.fill((byte) 0);
        return ms;
    }

    /**
     * Clayj Scoped Helper Structs
     * todo: move
     */

    public static class Dimensions {
        public static MemorySegment of(float width, float height) {
            var ms = allocateScoped(Clay_Dimensions.layout());
            Clay_Dimensions.width(ms, width);
            Clay_Dimensions.height(ms, height);
            return ms;
        }
    }
    // todo: annoyingly bad
    public static class Color {
        private final MemorySegment ms;
        private Color(MemorySegment ms) {
            this.ms = ms;
        }
        public static MemorySegment scoped(float r, float g, float b, float a) {
            var ms = allocateScoped(Clay_Color.layout());
            Clay_Color.r(ms, r);
            Clay_Color.g(ms, g);
            Clay_Color.b(ms, b);
            Clay_Color.a(ms, a);
            return ms;
        }
        public static Color from(MemorySegment ms) {
            return new Color(ms);
        }
        public Color set(float r, float g, float b, float a) {
            Clay_Color.r(ms, r);
            Clay_Color.g(ms, g);
            Clay_Color.b(ms, b);
            Clay_Color.a(ms, a);
            return this;
        }
    }
// todo: these need to be better
    public record Vector2(MemorySegment ms) {

        public float x() { return Clay_Vector2.x(ms); }
        public float y() { return Clay_Vector2.y(ms); }

        public static Vector2 scoped(float x, float y) {
            var ms = allocateScoped(Clay_Vector2.layout());
            Clay_Vector2.x(ms, x);
            Clay_Vector2.y(ms, y);
            return new Vector2(ms);
        }
    }

    public record BoundingBox(MemorySegment ms) {
        public float x() { return Clay_BoundingBox.x(ms); }
        public float y() { return Clay_BoundingBox.y(ms); }
        public float width() { return Clay_BoundingBox.width(ms); }
        public float height() { return Clay_BoundingBox.height(ms); }

        public BoundingBox x(float x) {
            Clay_BoundingBox.x(ms);
            return this;
        }
        public BoundingBox y(float y) {
            Clay_BoundingBox.y(ms, y);
            return this;
        }
        public BoundingBox width(float w) {
            Clay_BoundingBox.width(ms, w);
            return this;
        }
        public BoundingBox height(float h) {
            Clay_BoundingBox.height(ms, h);
            return this;
        }

        public static BoundingBox scoped(float x, float y, float width, float height) {
            var ms = allocateScoped(Clay_BoundingBox.layout());
            Clay_BoundingBox.x(ms, x);
            Clay_BoundingBox.y(ms, y);
            Clay_BoundingBox.width(ms, width);
            Clay_BoundingBox.height(ms, height);
            return new BoundingBox(ms);
        }
    }

    /***
     *    Clayj Scoped Arena
     * <p>
     *    NOTE: considered using a SlicingAllocator but handling
     *    overflow and reclaiming each loop turned out to be
     *    even more complicated than the stack + map
     *      ** may revisit later
     */
    static Map<Integer, Deque<MemorySegment>> availableAllocations = new HashMap<>();
    static Deque<List<MemorySegment>> allocatedStack = new ArrayDeque<>();

    public static MemorySegment allocateScoped(MemoryLayout layout) {
        return allocateScoped(layout.byteSize());
    }

    public static MemorySegment allocateScoped(long byteSize) {
        var currentSegments = allocatedStack.peek();
        assert currentSegments != null; // run initialize first
        var segments = availableAllocations.computeIfAbsent((int)byteSize, n -> new ArrayDeque<>());
        var ms = segments.poll();
        if (ms == null) {
            ms = arena.allocate(byteSize);
        }
        ms.fill((byte) 0);
        currentSegments.add(ms);
        return ms;
    }


    /**
     * reclaims scoped memory.
     * <p>
     * <b>** Run at the beginning of render loop to avoid OutOfMemoryException</b>
     *
     */
    public static void beginRenderLoop() {
        initializeStrings();
        var allocations = allocatedStack.peek();
        assert allocations != null;
        for (var ms : allocations) {
            var segments = availableAllocations.computeIfAbsent((int) ms.byteSize(), ArrayDeque::new);
            segments.add(ms);
        }
        allocations.clear();
    }

    public static void clay(Clay element) {
        clay(element, null);
    }

    public void runInScope(Consumer<Arena> runner) {
        allocatedStack.push(new ArrayList<>());
        runner.accept(arena);
        var alloc = allocatedStack.pop();
        for (var ms : alloc) {
            var segments = availableAllocations.computeIfAbsent((int) ms.byteSize(), ArrayDeque::new);
            segments.add(ms);
        }

    }

    public static void clay(Clay element, Runnable children) {
        // todo: put a blurb in README about compatibility etc.
        //  we need to use Clay internal functions, but this means
        // clayj is tied to the version it is built against
        // if internals change, clayj will need to be updated
//        ClayFFM.Clay__OpenElement(); // open element happens in id() to support Scroll childOffset()
        ClayFFM.Clay__ConfigureOpenElement(element.elmMs);

        if (children != null) {
            allocatedStack.push(new ArrayList<>());
            children.run();
            var alloc = allocatedStack.pop();
            for (var ms : alloc) {
                var segments = availableAllocations.computeIfAbsent((int) ms.byteSize(), ArrayDeque::new);
                segments.add(ms);
            }
        }
        ClayFFM.Clay__CloseElement();
        elementQueue.offer(element.elmMs);
        element.elmMs = null;
    }

    public static MemorySegment getElementId(String s) {
        var clayStr = ClayString.literal(s); // for now
        return Clay_GetElementId(scopedAllocator, clayStr.ms);
    }

    private static final SegmentAllocator scopedAllocator = (b, a) -> allocateScoped(b);

    /**
     * Use this Arena to allocate structs and other small temporary data
     * <p>
     * This is not needed in clay() declarations where it is
     * automatically in use. Use this for other code blocks that
     * run frequently.
     * <p>
     * See {@link dmacd.clay.renderer.RaylibRenderer#Clay_Raylib_Render(MemorySegment, MemorySegment)} for example.
     * @return Arena - makes allocations reusable after closing
     */
    public static Arena scopedArena() {
        allocatedStack.push(new ArrayList<>());
        return new Arena() {
            // todo: handle alignment
            @Override
            public MemorySegment allocate(long byteSize, long byteAlignment) {
                return allocateScoped(byteSize);
            }

            @Override
            public MemorySegment.Scope scope() {
                return arena.scope();
            }

            @Override
            public void close() {
                var alloc = allocatedStack.pop();
                for (var ms : alloc) {
                    var segments = availableAllocations.computeIfAbsent((int) ms.byteSize(), ArrayDeque::new);
                    segments.add(ms);
                }
            }
        };
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
            Clay_TextElementConfig.textColor(ms, color.ms);
            return this;
        }

        public TextElementConfig textColor(float r, float g, float b, float a) {
            var colorMS = Clay_TextElementConfig.textColor(ms);
            Color.from(colorMS).set(r, g, b, a);
            return this;
        }

        public TextElementConfig userData(MemorySegment userData) {
            Clay_TextElementConfig.userData(ms, userData);
            return this;
        }
    }


    private static class ElementDeclaration {

    }

    private MemorySegment elmMs;

    private Clay() {
        elmMs = nextElementDeclaration();
    }

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
    }

    public static Clay id() {
        return id("");
    }
    public static Clay id(String id) {
        var element = id.isEmpty() ? new Clay() : new Clay(id);
        ClayFFM.Clay__OpenElement(); // supports scroll in childOffset
        return element;
    }

    public static void errorHandler(MemorySegment errorData) {
        var ms = Clay_ErrorData.errorText(errorData);
        var stringSegment = Clay_String.chars(ms);
        var len = Clay_String.length(ms);
        byte[] stringBytes = stringSegment.asSlice(0, len).toArray(ValueLayout.JAVA_BYTE);
        String javaString = new String(stringBytes, StandardCharsets.UTF_8);

        System.out.println("from java from c:  " + javaString);
    }

    /// //////////////////////////////////////////
    /// Begin Clar_ElementDeclaration Builders
    /// //////////////////////////////////////////

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

    private static MemorySegment nextElementDeclaration() {
        if (elementQueue.isEmpty()) {
            // this only happens when nesting
            // so after 10 elements deep in a nest it will allocate 10.
            // hundreds of elements at the same level will reuse the same memory
            var ms = Clay_ElementDeclaration.allocateArray(10, arena);
            for (int i = 0; i < 10; i++) {
                var element = Clay_ElementDeclaration.asSlice(ms, i);
                elementQueue.add(element); // todo: test?
            }
        }
        var element = elementQueue.remove();
        element.fill((byte) 0);
        return element;
    }

    /************************************
     *  Clay_ElementDeclaration handling
     ***********************************/

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
        Clay_ElementDeclaration.backgroundColor(elmMs, c.ms);
        return this;
    }


    public Clay backgroundColor(float r, float g, float b, float a) {
        var colorMS = Clay_ElementDeclaration.backgroundColor(elmMs);
        Color.from(colorMS).set(r, g, b, a);
        return this;
    }

    // Move these with all the other CLAY_stuff somewhere optional
    public static LayoutConfig.SizingAxis CLAY_SIZING_FIT(float min) {
        return LayoutConfig.Sizing.fit(min);
    }

    public static LayoutConfig.SizingAxis CLAY_SIZING_FIT(float min, float max) {
        return LayoutConfig.Sizing.fit(min, max);
    }

    public static LayoutConfig.SizingAxis CLAY_SIZING_GROW(float min) {
        return LayoutConfig.Sizing.grow(min);
    }

    public static LayoutConfig.SizingAxis CLAY_SIZING_GROW(float min, float max) {
        return LayoutConfig.Sizing.grow(min, max);
    }

    public static LayoutConfig.SizingAxis CLAY_SIZING_FIXED(float n) {
        return LayoutConfig.Sizing.fixed(n);
    }

    public static LayoutConfig.SizingAxis CLAY_SIZING_PERCENT(float n) {
        return LayoutConfig.Sizing.percent(n);
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

    public record BorderElementConfig(MemorySegment ms) {
        public BorderElementConfig color(Color c) {
            Clay_BorderElementConfig.color(ms, c.ms);
            return this;
        }

        public BorderElementConfig color(float r, float g, float b, float a) {
            var colorMs = Clay_BorderElementConfig.color(ms);
            Color.from(colorMs).set(r, g, b, a);
            return this;
        }

        public BorderElementConfig width(Function<BorderWidth, BorderWidth> f) {
            var widthMs = Clay_BorderElementConfig.width(ms);
            f.apply(new BorderWidth(widthMs));
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

        public ClipElementConfig childOffset(Vector2 v2) {
            Clay_ClipElementConfig.childOffset(ms, v2.ms);
            return this;
        }

// todo: dont need this one
        public ClipElementConfig childOffset(MemorySegment v2) {
            Clay_ClipElementConfig.childOffset(ms, v2);
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

    public record LayoutConfig(MemorySegment ms) {
        public LayoutConfig layoutDirection(LayoutDirection direction) {
            Clay_LayoutConfig.layoutDirection(ms, direction.ordinal());
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

        public LayoutConfig sizing(SizingAxis width) {
            var sizeMs = Clay_LayoutConfig.sizing(ms);
            new Sizing(sizeMs).width(width);
            return this;
        }

        public LayoutConfig childGap(int gap) {
            Clay_LayoutConfig.childGap(ms, (short) gap);
            return this;
        }

        public LayoutConfig childAlignment(LayoutAlignmentX x, LayoutAlignmentY y) {
            var childMs = Clay_LayoutConfig.childAlignment(ms);
            Clay_ChildAlignment.x(childMs, x.ordinal());
            Clay_ChildAlignment.y(childMs, y.ordinal());
            return this;
        }

        public LayoutConfig childAlignment(Function<ChildAlignment, ChildAlignment> a) {
            var childMs = Clay_LayoutConfig.childAlignment(ms);
            a.apply(new ChildAlignment(childMs));
            return this;
        }

        public record Padding(MemorySegment ms) {
            public Padding left(int left) {
                Clay_Padding.left(ms, (short) left);
                return this;
            }

            public Padding right(int right) {
                Clay_Padding.right(ms, (short) right);
                return this;
            }

            public Padding top(int top) {
                Clay_Padding.top(ms, (short) top);
                return this;
            }

            public Padding bottom(int bottom) {
                Clay_Padding.bottom(ms, (short) bottom);
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

        public record ChildAlignment(MemorySegment ms) {
            public ChildAlignment x(LayoutAlignmentX x) {
                Clay_ChildAlignment.x(ms, x.ordinal());
                return this;
            }

            public ChildAlignment y(LayoutAlignmentY y) {
                Clay_ChildAlignment.y(ms, y.ordinal());
                return this;
            }
        }

        public record Sizing(MemorySegment ms) {
            public Sizing width(SizingAxis width) {
                var wMs = Clay_Sizing.width(ms);
                Clay_SizingAxis.type(wMs, width.type);
                var mmMs = Clay_SizingAxis.size.minMax(wMs);
                // size is a union
                // todo: should work, might need if/else for percent
                Clay_SizingMinMax.min(mmMs, width.minOrPercent);
                Clay_SizingMinMax.max(mmMs, width.max);
                return this;
            }

            public Sizing height(SizingAxis height) {
                var hMs = Clay_Sizing.height(ms);
                Clay_SizingAxis.type(hMs, height.type);
                var mmMs = Clay_SizingAxis.size.minMax(hMs);
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

        public FloatingElementConfig attachTo(FloatingAttachToElement attachTo) {
            Clay_FloatingElementConfig.attachTo(ms, attachTo.ordinal());
            return this;
        }

        public FloatingElementConfig clipTo(FloatingClipToElement clipTo) {
            Clay_FloatingElementConfig.clipTo(ms, clipTo.ordinal());
            return this;
        }

        public FloatingElementConfig pointerCaptureMode(int pointerCaptureMode) {
            Clay_FloatingElementConfig.pointerCaptureMode(ms, pointerCaptureMode);
            return this;
        }

        public FloatingElementConfig zIndex(int zIndex) {
            Clay_FloatingElementConfig.zIndex(ms, (short) zIndex);
            return this;
        }

        public FloatingElementConfig attachPoints(Function<FloatingAttachPoints, FloatingAttachPoints> f) {
            var apMs = Clay_FloatingElementConfig.attachPoints(ms);
            f.apply(new FloatingAttachPoints(apMs));
            return this;
        }

        public record FloatingAttachPoints(MemorySegment ms) {

            public FloatingAttachPoints element(FloatingAttachPointType attachPoint) {
                Clay_FloatingAttachPoints.element(ms, attachPoint.ordinal());
                return this;
            }

            public FloatingAttachPoints parent(FloatingAttachPointType attachPoint) {
                Clay_FloatingAttachPoints.parent(ms, attachPoint.ordinal());
                return this;
            }
        }
    }

    /// ////////////////////////////////////////////////
    /// END Clay_ElementDeclaration Handling
    /// ////////////////////////////////////////////////

    private static Arena arena;

    public static void initialize(Arena arena) {
        Clay.arena = new TrackedArena(arena);
        allocatedStack.push(new ArrayList<>());
    }


    public static int minMemorySize() {
        return ClayFFM.Clay_MinMemorySize();
    }

    public static MemorySegment GLOBAL_CLAY_COLOR(float r, float g, float b, float a) {
        var ms = Clay_Color.allocate(GLOBAL_ARENA);
        Clay_Color.r(ms, r);
        Clay_Color.g(ms, g);
        Clay_Color.b(ms, b);
        Clay_Color.a(ms, a);
        return ms;
    }

    public static void clayText(ClayString text, Function<TextElementConfig, TextElementConfig> textConfig) {
        var ms = allocateScoped(Clay_TextElementConfig.layout());
        textConfig.apply(new TextElementConfig(ms));
        var cfg = Clay__StoreTextElementConfig(ms);
        Clay__OpenTextElement(text.ms, cfg);
    }

    // todo: these names suck
    public static void lText(String s, Function<TextElementConfig, TextElementConfig> textConfig) {
        clayText(ClayString.literal(s), textConfig);
    }

    public static void dText(String s, Function<TextElementConfig, TextElementConfig> textConfig) {
        clayText(ClayString.dynamic(s), textConfig);
    }

    public static class ClayString {
        final private MemorySegment ms;

        private ClayString(MemorySegment ms) {
            this.ms = ms;
        }

        public static MemorySegment buffered(MemorySegment ms, String s) {
            var utf8 = s.getBytes(StandardCharsets.UTF_8);
            if (ms.byteSize() < CLAY_STRING_STRUCT_SIZE + utf8.length) {
                throw new IndexOutOfBoundsException("Buffer size too small(%d). Clay_String requires (%d)"
                        .formatted(ms.byteSize(), CLAY_STRING_STRUCT_SIZE + utf8.length));
            }
            MemorySegment.copy(utf8, 0, ms, ValueLayout.JAVA_BYTE, CLAY_STRING_STRUCT_SIZE, utf8.length);
            Clay_String.chars(ms, ms.asSlice(CLAY_STRING_STRUCT_SIZE, utf8.length));
            Clay_String.isStaticallyAllocated(ms, false);
            Clay_String.length(ms, utf8.length);
            return ms;
        }

        public static ClayString literal(String s) {
            // todo: decide if the claystring struct should be part of the allocation [currently it is only for globals]
            return new ClayString(globalStrings.computeIfAbsent(s, Clay::allocateGlobalClayString));
        }

        public static ClayString dynamic(String s) {
            var utf8 = s.getBytes(StandardCharsets.UTF_8);
            long allocSize = utf8.length;

            if (allocSize <= 128) {
                allocSize += allocSize % 4;
            } else if (allocSize <= 256) {
                allocSize += allocSize % 8;
            } else if (allocSize <= 512) {
                allocSize = 512;
            } else if (allocSize <= 1024) {
                allocSize = 1024;
            } else {
                throw new IndexOutOfBoundsException("UTF8 string length > 1024: " + utf8.length);
            }
            var list = stringBlocks.computeIfAbsent(allocSize, n -> new ArrayList<>());
            var stringBuffer = list.isEmpty() ? arena.allocate(allocSize) : list.removeLast();
            allocatedStrings.add(stringBuffer);

            MemorySegment.copy(utf8, 0, stringBuffer, ValueLayout.JAVA_BYTE, 0, utf8.length);
            var ms = nextDynamicClayString();
            allocatedStrings.add(ms);

            Clay_String.chars(ms, stringBuffer);
            Clay_String.isStaticallyAllocated(ms, false);
            Clay_String.length(ms, utf8.length);
            return new ClayString(ms);
        }
    }


    private static MemorySegment allocateGlobalClayString(String s) {
        var utf8 = s.getBytes(StandardCharsets.UTF_8);
        var ms = GLOBAL_ARENA.allocate(utf8.length + CLAY_STRING_STRUCT_SIZE, 8);
        MemorySegment.copy(utf8, 0, ms, ValueLayout.JAVA_BYTE, CLAY_STRING_STRUCT_SIZE, utf8.length);
        Clay_String.chars(ms, ms.asSlice(CLAY_STRING_STRUCT_SIZE, utf8.length));
        Clay_String.isStaticallyAllocated(ms, true);
        Clay_String.length(ms, utf8.length);
        return ms;
    }

// region Clay Enums
// ------------------------------------------------------------------------------------

    /// Controls the direction in which child elements will be automatically laid out.
    public enum LayoutDirection {
        /// (Default) Lays out child elements from left to right with increasing x.
        LEFT_TO_RIGHT,
        /// Lays out child elements from top to bottom with increasing y.
        TOP_TO_BOTTOM
    }

    /// Controls the alignment along the x-axis (horizontal) of child elements.
    public enum LayoutAlignmentX {
        /// (Default) Aligns child elements to the left hand side of this element, offset by padding.width.left
        ALIGN_X_LEFT,
        /// Aligns child elements to the right hand side of this element, offset by padding.width.right
        ALIGN_X_RIGHT,
        /// Aligns child elements horizontally to the center of this element
        ALIGN_X_CENTER,
    }

    /// Controls the alignment along the y-axis (vertical) of child elements.
    public enum LayoutAlignmentY {
        /// (Default) Aligns child elements to the top of this element, offset by padding.width.top
        ALIGN_Y_TOP,
        /// Aligns child elements to the bottom of this element, offset by padding.width.bottom
        ALIGN_Y_BOTTOM,
        /// Aligns child elements vertically to the center of this element
        ALIGN_Y_CENTER,
    }

    /// Controls how the element takes up space inside its parent container.
    public enum SizingType {
        /// (default) Wraps tightly to the size of the element's contents.
        SIZING_TYPE_FIT,
        /// Expands along this axis to fill available space in the parent element, sharing it with other GROW elements.
        SIZING_TYPE_GROW,
        /// Expects 0-1 range. Clamps the axis size to a percent of the parent container's axis size minus padding and child gaps.
        SIZING_TYPE_PERCENT,
        /// Clamps the axis size to an exact size in pixels.
        SIZING_TYPE_FIXED,
    }

    /// Controls how text "wraps", that is how it is broken into multiple lines when there is insufficient horizontal space.
    public enum TextElementConfigWrapMode {
        /// (default) breaks on whitespace characters.
        TEXT_WRAP_WORDS,
        /// Don't break on space characters, only on newlines.
        TEXT_WRAP_NEWLINES,
        /// Disable text wrapping entirely.
        TEXT_WRAP_NONE,
    }

    /// Controls how wrapped lines of text are horizontally aligned within the outer text bounding box.
    public enum TextAlignment {
        /// (default) Horizontally aligns wrapped lines of text to the left hand side of their bounding box.
        TEXT_ALIGN_LEFT,
        /// Horizontally aligns wrapped lines of text to the center of their bounding box.
        TEXT_ALIGN_CENTER,
        /// Horizontally aligns wrapped lines of text to the right hand side of their bounding box.
        TEXT_ALIGN_RIGHT,
    }

    /// Controls where a floating element is offset relative to its parent element.
    ///
    /// Note: see https://github.com/user-attachments/assets/b8c6dfaa-c1b1-41a4-be55-013473e4a6ce for a visual explanation.
    public enum FloatingAttachPointType {
        ATTACH_POINT_LEFT_TOP,
        ATTACH_POINT_LEFT_CENTER,
        ATTACH_POINT_LEFT_BOTTOM,
        ATTACH_POINT_CENTER_TOP,
        ATTACH_POINT_CENTER_CENTER,
        ATTACH_POINT_CENTER_BOTTOM,
        ATTACH_POINT_RIGHT_TOP,
        ATTACH_POINT_RIGHT_CENTER,
        ATTACH_POINT_RIGHT_BOTTOM,
    }

    /// Controls how mouse pointer events like hover and click are captured or passed through to elements underneath a floating element.
    public enum PointerCaptureMode {
        /// (default) "Capture" the pointer event and don't allow events like hover and click to pass through to elements underneath.
        POINTER_CAPTURE_MODE_CAPTURE,
        //    POINTER_CAPTURE_MODE_PARENT, (CLAY_TODO pass pointer through to attached parent)
        /// Transparently pass through pointer events like hover and click to elements underneath the floating element.
        POINTER_CAPTURE_MODE_PASSTHROUGH,
    }

    /// Controls which element a floating element is "attached" to (i.e. relative offset from).
    public enum FloatingAttachToElement {
        /// (default) Disables floating for this element.
        ATTACH_TO_NONE,
        /// Attaches this floating element to its parent, positioned based on the .attachPoints and .offset fields.
        ATTACH_TO_PARENT,
        /// Attaches this floating element to an element with a specific ID, specified with the .parentId field. positioned based on the .attachPoints and .offset fields.
        ATTACH_TO_ELEMENT_WITH_ID,
        /// Attaches this floating element to the root of the layout, which combined with the .offset field provides functionality similar to "absolute positioning".
        ATTACH_TO_ROOT,
    }

    /// Controls whether or not a floating element is clipped to the same clipping rectangle as the element it's attached to.
    public enum FloatingClipToElement{
        /// (default) - The floating element does not inherit clipping.
        CLIP_TO_NONE,
        /// The floating element is clipped to the same clipping rectangle as the element it's attached to.
        CLIP_TO_ATTACHED_PARENT,
    }

    /// Used by renderers to determine specific handling for each render command.
    public enum RenderCommandType {
        /// This command type should be skipped.
        RENDER_COMMAND_TYPE_NONE,
        /// The renderer should draw a solid color rectangle.
        RENDER_COMMAND_TYPE_RECTANGLE,
        /// The renderer should draw a colored border inset into the bounding box.
        RENDER_COMMAND_TYPE_BORDER,
        /// The renderer should draw text.
        RENDER_COMMAND_TYPE_TEXT,
        /// The renderer should draw an image.
        RENDER_COMMAND_TYPE_IMAGE,
        /// The renderer should begin clipping all future draw commands, only rendering content that falls within the provided boundingBox.
        RENDER_COMMAND_TYPE_SCISSOR_START,
        /// The renderer should finish any previously active clipping, and begin rendering elements in full again.
        RENDER_COMMAND_TYPE_SCISSOR_END,
        /// The renderer should provide a custom implementation for handling this render command based on its .customData
        RENDER_COMMAND_TYPE_CUSTOM,
    }
    /// Represents the current state of interaction with clay this frame.
    public enum PointerDataInteractionState {
        /// A left mouse click, or touch occurred this frame.
        POINTER_DATA_PRESSED_THIS_FRAME,
        /// The left mouse button click or touch happened at some point in the past, and is still currently held down this frame.
        POINTER_DATA_PRESSED,
        /// The left mouse button click or touch was released this frame.
        POINTER_DATA_RELEASED_THIS_FRAME,
        /// The left mouse button click or touch is not currently down / was released at some point in the past.
        POINTER_DATA_RELEASED,
    }
    /// Represents the type of error clay encountered while computing layout.
    public enum ErrorType {
        /// A text measurement function wasn't provided using SetMeasureTextFunction(), or the provided function was null.
        ERROR_TYPE_TEXT_MEASUREMENT_FUNCTION_NOT_PROVIDED,
        /// Clay attempted to allocate its internal data structures but ran out of space.
        /// The arena passed to Clay_Initialize was created with a capacity smaller than that required by Clay_MinMemorySize().
        ERROR_TYPE_ARENA_CAPACITY_EXCEEDED,
        /// Clay ran out of capacity in its internal array for storing elements. This limit can be increased with Clay_SetMaxElementCount().
        ERROR_TYPE_ELEMENTS_CAPACITY_EXCEEDED,
        /// Clay ran out of capacity in its internal array for storing elements. This limit can be increased with Clay_SetMaxMeasureTextCacheWordCount().
        ERROR_TYPE_TEXT_MEASUREMENT_CAPACITY_EXCEEDED,
        /// Two elements were declared with exactly the same ID within one layout.
        ERROR_TYPE_DUPLICATE_ID,
        /// A floating element was declared using CLAY_ATTACH_TO_ELEMENT_ID and either an invalid .parentId was provided or no element with the provided .parentId was found.
        ERROR_TYPE_FLOATING_CONTAINER_PARENT_NOT_FOUND,
        /// An element was declared that using CLAY_SIZING_PERCENT but the percentage value was over 1. Percentage values are expected to be in the 0-1 range.
        ERROR_TYPE_PERCENTAGE_OVER_1,
        /// Clay encountered an internal error. It would be wonderful if you could report this so we can fix it!
        ERROR_TYPE_INTERNAL_ERROR,
    }


// endregion
    @FunctionalInterface
    public interface ClayHoverFunction {
        void onHover(Clay.ElementData e, PointerData p);
    }

    public record ElementData(MemorySegment ms){}
    public record PointerData(MemorySegment ms) {
        Vector2 position() {
            var p = Clay_PointerData.position(ms);
            return new Vector2(p);
        }
        public PointerDataInteractionState state() {
            var state = Clay_PointerData.state(ms);
            // todo: could use a case statement, hoping compiler is smart L)
            return PointerDataInteractionState.values()[state];
        }
        public boolean pressedThisFrame() {
            var state = Clay_PointerData.state(ms);
            return state == PointerDataInteractionState.POINTER_DATA_PRESSED_THIS_FRAME.ordinal();
        }
        public boolean pressed() {
            var state = Clay_PointerData.state(ms);
            return state == PointerDataInteractionState.POINTER_DATA_PRESSED.ordinal();
        }
        public boolean released() {
            var state = Clay_PointerData.state(ms);
            return state == PointerDataInteractionState.POINTER_DATA_RELEASED.ordinal();
        }
        public boolean releasedThisFrame() {
            var state = Clay_PointerData.state(ms);
            return state == PointerDataInteractionState.POINTER_DATA_RELEASED_THIS_FRAME.ordinal();
        }

    }

    public static void onHover(ClayHoverFunction hoverFunc) {
        // todo: this should be new temparena
        var func = Clay_OnHover$onHoverFunction.allocate((elementData, pointerData, userData) ->{
            var pd = new PointerData(pointerData);
            var elm = new ElementData(elementData);
            hoverFunc.onHover(elm, pd);
        }, arena);
        ClayFFM.Clay_OnHover(func, 0);
    }
    public record RenderCommand(MemorySegment ms) {

    }
    public static List<RenderCommand> endLayout() {
        List<RenderCommand> list = new ArrayList<>();
        var renderCommands = ClayFFM.Clay_EndLayout(scopedAllocator);
        for (int i = 0; i < Clay_RenderCommandArray.length(renderCommands); i++) {
            var cmd = ClayFFM.Clay_RenderCommandArray_Get(renderCommands, i);
            cmd = Clay_RenderCommand.reinterpret(cmd, arena, null);
            list.add(new RenderCommand(cmd));
        }
        return list;
    }


    public static Vector2 getScrollOffset() {
        return new Vector2(ClayFFM.Clay_GetScrollOffset(scopedAllocator));
    }
    public static class CLAY_ALIAS {
        public static void CLAY(Clay element, Runnable children) {
            clay(element, children);
        }

        public static ClayString CLAY_STRING(String s) {
            return ClayString.literal(s);
        }

        // todo: change this
        public static void CLAY_TEXT(String str, Function<TextElementConfig, TextElementConfig> textConfig) {
            clayText(ClayString.literal(str), textConfig);
        }

        public static void CLAY_TEXT(MemorySegment ms, Function<TextElementConfig, TextElementConfig> textConfig) {
            clayText(new ClayString(ms), textConfig);
        }

        public static void CLAY_TEXT(ClayString cs, Function<TextElementConfig, TextElementConfig> textConfig) {
            clayText(cs, textConfig);
        }
    }
}