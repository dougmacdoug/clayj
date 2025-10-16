package dmacd.ffm.raylib;

import java.lang.foreign.*;
import java.util.function.Consumer;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

public class Rayliib {
    /**
     * {@snippet lang=c :
     * struct BoundingBox {
     *     Vector3 min;
     *     Vector3 max;
     * }
     * }
     */
    public static class BoundingBox {

        BoundingBox() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            Vector3.layout().withName("min"),
            Vector3.layout().withName("max")
        ).withName("BoundingBox");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final GroupLayout min$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("min"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Vector3 min
         * }
         */
        public static final GroupLayout min$layout() {
            return min$LAYOUT;
        }

        private static final long min$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Vector3 min
         * }
         */
        public static final long min$offset() {
            return min$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Vector3 min
         * }
         */
        public static MemorySegment min(MemorySegment struct) {
            return struct.asSlice(min$OFFSET, min$LAYOUT.byteSize());
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Vector3 min
         * }
         */
        public static void min(MemorySegment struct, MemorySegment fieldValue) {
            MemorySegment.copy(fieldValue, 0L, struct, min$OFFSET, min$LAYOUT.byteSize());
        }

        private static final GroupLayout max$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("max"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Vector3 max
         * }
         */
        public static final GroupLayout max$layout() {
            return max$LAYOUT;
        }

        private static final long max$OFFSET = 12;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Vector3 max
         * }
         */
        public static final long max$offset() {
            return max$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Vector3 max
         * }
         */
        public static MemorySegment max(MemorySegment struct) {
            return struct.asSlice(max$OFFSET, max$LAYOUT.byteSize());
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Vector3 max
         * }
         */
        public static void max(MemorySegment struct, MemorySegment fieldValue) {
            MemorySegment.copy(fieldValue, 0L, struct, max$OFFSET, max$LAYOUT.byteSize());
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * typedef Camera3D Camera
     * }
     */
    public static class Camera extends Camera3D {

        Camera() {
            // Should not be called directly
        }
    }

    /**
     * {@snippet lang=c :
     * struct Camera2D {
     *     Vector2 offset;
     *     Vector2 target;
     *     float rotation;
     *     float zoom;
     * }
     * }
     */
    public static class Camera2D {

        Camera2D() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            Vector2.layout().withName("offset"),
            Vector2.layout().withName("target"),
            RayFFM.C_FLOAT.withName("rotation"),
            RayFFM.C_FLOAT.withName("zoom")
        ).withName("Camera2D");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final GroupLayout offset$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("offset"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Vector2 offset
         * }
         */
        public static final GroupLayout offset$layout() {
            return offset$LAYOUT;
        }

        private static final long offset$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Vector2 offset
         * }
         */
        public static final long offset$offset() {
            return offset$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Vector2 offset
         * }
         */
        public static MemorySegment offset(MemorySegment struct) {
            return struct.asSlice(offset$OFFSET, offset$LAYOUT.byteSize());
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Vector2 offset
         * }
         */
        public static void offset(MemorySegment struct, MemorySegment fieldValue) {
            MemorySegment.copy(fieldValue, 0L, struct, offset$OFFSET, offset$LAYOUT.byteSize());
        }

        private static final GroupLayout target$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("target"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Vector2 target
         * }
         */
        public static final GroupLayout target$layout() {
            return target$LAYOUT;
        }

        private static final long target$OFFSET = 8;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Vector2 target
         * }
         */
        public static final long target$offset() {
            return target$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Vector2 target
         * }
         */
        public static MemorySegment target(MemorySegment struct) {
            return struct.asSlice(target$OFFSET, target$LAYOUT.byteSize());
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Vector2 target
         * }
         */
        public static void target(MemorySegment struct, MemorySegment fieldValue) {
            MemorySegment.copy(fieldValue, 0L, struct, target$OFFSET, target$LAYOUT.byteSize());
        }

        private static final ValueLayout.OfFloat rotation$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("rotation"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float rotation
         * }
         */
        public static final ValueLayout.OfFloat rotation$layout() {
            return rotation$LAYOUT;
        }

        private static final long rotation$OFFSET = 16;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float rotation
         * }
         */
        public static final long rotation$offset() {
            return rotation$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float rotation
         * }
         */
        public static float rotation(MemorySegment struct) {
            return struct.get(rotation$LAYOUT, rotation$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float rotation
         * }
         */
        public static void rotation(MemorySegment struct, float fieldValue) {
            struct.set(rotation$LAYOUT, rotation$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat zoom$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("zoom"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float zoom
         * }
         */
        public static final ValueLayout.OfFloat zoom$layout() {
            return zoom$LAYOUT;
        }

        private static final long zoom$OFFSET = 20;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float zoom
         * }
         */
        public static final long zoom$offset() {
            return zoom$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float zoom
         * }
         */
        public static float zoom(MemorySegment struct) {
            return struct.get(zoom$LAYOUT, zoom$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float zoom
         * }
         */
        public static void zoom(MemorySegment struct, float fieldValue) {
            struct.set(zoom$LAYOUT, zoom$OFFSET, fieldValue);
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * struct Camera3D {
     *     Vector3 position;
     *     Vector3 target;
     *     Vector3 up;
     *     float fovy;
     *     int projection;
     * }
     * }
     */
    public static class Camera3D {

        Camera3D() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            Vector3.layout().withName("position"),
            Vector3.layout().withName("target"),
            Vector3.layout().withName("up"),
            RayFFM.C_FLOAT.withName("fovy"),
            RayFFM.C_INT.withName("projection")
        ).withName("Camera3D");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final GroupLayout position$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("position"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Vector3 position
         * }
         */
        public static final GroupLayout position$layout() {
            return position$LAYOUT;
        }

        private static final long position$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Vector3 position
         * }
         */
        public static final long position$offset() {
            return position$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Vector3 position
         * }
         */
        public static MemorySegment position(MemorySegment struct) {
            return struct.asSlice(position$OFFSET, position$LAYOUT.byteSize());
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Vector3 position
         * }
         */
        public static void position(MemorySegment struct, MemorySegment fieldValue) {
            MemorySegment.copy(fieldValue, 0L, struct, position$OFFSET, position$LAYOUT.byteSize());
        }

        private static final GroupLayout target$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("target"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Vector3 target
         * }
         */
        public static final GroupLayout target$layout() {
            return target$LAYOUT;
        }

        private static final long target$OFFSET = 12;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Vector3 target
         * }
         */
        public static final long target$offset() {
            return target$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Vector3 target
         * }
         */
        public static MemorySegment target(MemorySegment struct) {
            return struct.asSlice(target$OFFSET, target$LAYOUT.byteSize());
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Vector3 target
         * }
         */
        public static void target(MemorySegment struct, MemorySegment fieldValue) {
            MemorySegment.copy(fieldValue, 0L, struct, target$OFFSET, target$LAYOUT.byteSize());
        }

        private static final GroupLayout up$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("up"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Vector3 up
         * }
         */
        public static final GroupLayout up$layout() {
            return up$LAYOUT;
        }

        private static final long up$OFFSET = 24;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Vector3 up
         * }
         */
        public static final long up$offset() {
            return up$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Vector3 up
         * }
         */
        public static MemorySegment up(MemorySegment struct) {
            return struct.asSlice(up$OFFSET, up$LAYOUT.byteSize());
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Vector3 up
         * }
         */
        public static void up(MemorySegment struct, MemorySegment fieldValue) {
            MemorySegment.copy(fieldValue, 0L, struct, up$OFFSET, up$LAYOUT.byteSize());
        }

        private static final ValueLayout.OfFloat fovy$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("fovy"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float fovy
         * }
         */
        public static final ValueLayout.OfFloat fovy$layout() {
            return fovy$LAYOUT;
        }

        private static final long fovy$OFFSET = 36;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float fovy
         * }
         */
        public static final long fovy$offset() {
            return fovy$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float fovy
         * }
         */
        public static float fovy(MemorySegment struct) {
            return struct.get(fovy$LAYOUT, fovy$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float fovy
         * }
         */
        public static void fovy(MemorySegment struct, float fieldValue) {
            struct.set(fovy$LAYOUT, fovy$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt projection$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("projection"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int projection
         * }
         */
        public static final ValueLayout.OfInt projection$layout() {
            return projection$LAYOUT;
        }

        private static final long projection$OFFSET = 40;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int projection
         * }
         */
        public static final long projection$offset() {
            return projection$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int projection
         * }
         */
        public static int projection(MemorySegment struct) {
            return struct.get(projection$LAYOUT, projection$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int projection
         * }
         */
        public static void projection(MemorySegment struct, int fieldValue) {
            struct.set(projection$LAYOUT, projection$OFFSET, fieldValue);
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * struct Color {
     *     unsigned char r;
     *     unsigned char g;
     *     unsigned char b;
     *     unsigned char a;
     * }
     * }
     */
    public static class Color {

        Color() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            RayFFM.C_CHAR.withName("r"),
            RayFFM.C_CHAR.withName("g"),
            RayFFM.C_CHAR.withName("b"),
            RayFFM.C_CHAR.withName("a")
        ).withName("Color");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final ValueLayout.OfByte r$LAYOUT = (ValueLayout.OfByte)$LAYOUT.select(groupElement("r"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * unsigned char r
         * }
         */
        public static final ValueLayout.OfByte r$layout() {
            return r$LAYOUT;
        }

        private static final long r$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * unsigned char r
         * }
         */
        public static final long r$offset() {
            return r$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * unsigned char r
         * }
         */
        public static byte r(MemorySegment struct) {
            return struct.get(r$LAYOUT, r$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * unsigned char r
         * }
         */
        public static void r(MemorySegment struct, byte fieldValue) {
            struct.set(r$LAYOUT, r$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfByte g$LAYOUT = (ValueLayout.OfByte)$LAYOUT.select(groupElement("g"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * unsigned char g
         * }
         */
        public static final ValueLayout.OfByte g$layout() {
            return g$LAYOUT;
        }

        private static final long g$OFFSET = 1;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * unsigned char g
         * }
         */
        public static final long g$offset() {
            return g$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * unsigned char g
         * }
         */
        public static byte g(MemorySegment struct) {
            return struct.get(g$LAYOUT, g$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * unsigned char g
         * }
         */
        public static void g(MemorySegment struct, byte fieldValue) {
            struct.set(g$LAYOUT, g$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfByte b$LAYOUT = (ValueLayout.OfByte)$LAYOUT.select(groupElement("b"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * unsigned char b
         * }
         */
        public static final ValueLayout.OfByte b$layout() {
            return b$LAYOUT;
        }

        private static final long b$OFFSET = 2;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * unsigned char b
         * }
         */
        public static final long b$offset() {
            return b$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * unsigned char b
         * }
         */
        public static byte b(MemorySegment struct) {
            return struct.get(b$LAYOUT, b$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * unsigned char b
         * }
         */
        public static void b(MemorySegment struct, byte fieldValue) {
            struct.set(b$LAYOUT, b$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfByte a$LAYOUT = (ValueLayout.OfByte)$LAYOUT.select(groupElement("a"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * unsigned char a
         * }
         */
        public static final ValueLayout.OfByte a$layout() {
            return a$LAYOUT;
        }

        private static final long a$OFFSET = 3;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * unsigned char a
         * }
         */
        public static final long a$offset() {
            return a$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * unsigned char a
         * }
         */
        public static byte a(MemorySegment struct) {
            return struct.get(a$LAYOUT, a$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * unsigned char a
         * }
         */
        public static void a(MemorySegment struct, byte fieldValue) {
            struct.set(a$LAYOUT, a$OFFSET, fieldValue);
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * struct Font {
     *     int baseSize;
     *     int glyphCount;
     *     int glyphPadding;
     *     Texture2D texture;
     *     Rectangle *recs;
     *     GlyphInfo *glyphs;
     * }
     * }
     */
    public static class Font {

        Font() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            RayFFM.C_INT.withName("baseSize"),
            RayFFM.C_INT.withName("glyphCount"),
            RayFFM.C_INT.withName("glyphPadding"),
            Texture.layout().withName("texture"),
            RayFFM.C_POINTER.withName("recs"),
            RayFFM.C_POINTER.withName("glyphs")
        ).withName("Font");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final ValueLayout.OfInt baseSize$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("baseSize"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int baseSize
         * }
         */
        public static final ValueLayout.OfInt baseSize$layout() {
            return baseSize$LAYOUT;
        }

        private static final long baseSize$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int baseSize
         * }
         */
        public static final long baseSize$offset() {
            return baseSize$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int baseSize
         * }
         */
        public static int baseSize(MemorySegment struct) {
            return struct.get(baseSize$LAYOUT, baseSize$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int baseSize
         * }
         */
        public static void baseSize(MemorySegment struct, int fieldValue) {
            struct.set(baseSize$LAYOUT, baseSize$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt glyphCount$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("glyphCount"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int glyphCount
         * }
         */
        public static final ValueLayout.OfInt glyphCount$layout() {
            return glyphCount$LAYOUT;
        }

        private static final long glyphCount$OFFSET = 4;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int glyphCount
         * }
         */
        public static final long glyphCount$offset() {
            return glyphCount$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int glyphCount
         * }
         */
        public static int glyphCount(MemorySegment struct) {
            return struct.get(glyphCount$LAYOUT, glyphCount$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int glyphCount
         * }
         */
        public static void glyphCount(MemorySegment struct, int fieldValue) {
            struct.set(glyphCount$LAYOUT, glyphCount$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt glyphPadding$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("glyphPadding"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int glyphPadding
         * }
         */
        public static final ValueLayout.OfInt glyphPadding$layout() {
            return glyphPadding$LAYOUT;
        }

        private static final long glyphPadding$OFFSET = 8;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int glyphPadding
         * }
         */
        public static final long glyphPadding$offset() {
            return glyphPadding$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int glyphPadding
         * }
         */
        public static int glyphPadding(MemorySegment struct) {
            return struct.get(glyphPadding$LAYOUT, glyphPadding$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int glyphPadding
         * }
         */
        public static void glyphPadding(MemorySegment struct, int fieldValue) {
            struct.set(glyphPadding$LAYOUT, glyphPadding$OFFSET, fieldValue);
        }

        private static final GroupLayout texture$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("texture"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Texture2D texture
         * }
         */
        public static final GroupLayout texture$layout() {
            return texture$LAYOUT;
        }

        private static final long texture$OFFSET = 12;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Texture2D texture
         * }
         */
        public static final long texture$offset() {
            return texture$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Texture2D texture
         * }
         */
        public static MemorySegment texture(MemorySegment struct) {
            return struct.asSlice(texture$OFFSET, texture$LAYOUT.byteSize());
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Texture2D texture
         * }
         */
        public static void texture(MemorySegment struct, MemorySegment fieldValue) {
            MemorySegment.copy(fieldValue, 0L, struct, texture$OFFSET, texture$LAYOUT.byteSize());
        }

        private static final AddressLayout recs$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("recs"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Rectangle *recs
         * }
         */
        public static final AddressLayout recs$layout() {
            return recs$LAYOUT;
        }

        private static final long recs$OFFSET = 32;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Rectangle *recs
         * }
         */
        public static final long recs$offset() {
            return recs$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Rectangle *recs
         * }
         */
        public static MemorySegment recs(MemorySegment struct) {
            return struct.get(recs$LAYOUT, recs$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Rectangle *recs
         * }
         */
        public static void recs(MemorySegment struct, MemorySegment fieldValue) {
            struct.set(recs$LAYOUT, recs$OFFSET, fieldValue);
        }

        private static final AddressLayout glyphs$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("glyphs"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * GlyphInfo *glyphs
         * }
         */
        public static final AddressLayout glyphs$layout() {
            return glyphs$LAYOUT;
        }

        private static final long glyphs$OFFSET = 40;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * GlyphInfo *glyphs
         * }
         */
        public static final long glyphs$offset() {
            return glyphs$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * GlyphInfo *glyphs
         * }
         */
        public static MemorySegment glyphs(MemorySegment struct) {
            return struct.get(glyphs$LAYOUT, glyphs$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * GlyphInfo *glyphs
         * }
         */
        public static void glyphs(MemorySegment struct, MemorySegment fieldValue) {
            struct.set(glyphs$LAYOUT, glyphs$OFFSET, fieldValue);
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * struct GlyphInfo {
     *     int value;
     *     int offsetX;
     *     int offsetY;
     *     int advanceX;
     *     Image image;
     * }
     * }
     */
    public static class GlyphInfo {

        GlyphInfo() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            RayFFM.C_INT.withName("value"),
            RayFFM.C_INT.withName("offsetX"),
            RayFFM.C_INT.withName("offsetY"),
            RayFFM.C_INT.withName("advanceX"),
            Image.layout().withName("image")
        ).withName("GlyphInfo");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final ValueLayout.OfInt value$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("value"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int value
         * }
         */
        public static final ValueLayout.OfInt value$layout() {
            return value$LAYOUT;
        }

        private static final long value$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int value
         * }
         */
        public static final long value$offset() {
            return value$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int value
         * }
         */
        public static int value(MemorySegment struct) {
            return struct.get(value$LAYOUT, value$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int value
         * }
         */
        public static void value(MemorySegment struct, int fieldValue) {
            struct.set(value$LAYOUT, value$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt offsetX$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("offsetX"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int offsetX
         * }
         */
        public static final ValueLayout.OfInt offsetX$layout() {
            return offsetX$LAYOUT;
        }

        private static final long offsetX$OFFSET = 4;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int offsetX
         * }
         */
        public static final long offsetX$offset() {
            return offsetX$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int offsetX
         * }
         */
        public static int offsetX(MemorySegment struct) {
            return struct.get(offsetX$LAYOUT, offsetX$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int offsetX
         * }
         */
        public static void offsetX(MemorySegment struct, int fieldValue) {
            struct.set(offsetX$LAYOUT, offsetX$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt offsetY$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("offsetY"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int offsetY
         * }
         */
        public static final ValueLayout.OfInt offsetY$layout() {
            return offsetY$LAYOUT;
        }

        private static final long offsetY$OFFSET = 8;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int offsetY
         * }
         */
        public static final long offsetY$offset() {
            return offsetY$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int offsetY
         * }
         */
        public static int offsetY(MemorySegment struct) {
            return struct.get(offsetY$LAYOUT, offsetY$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int offsetY
         * }
         */
        public static void offsetY(MemorySegment struct, int fieldValue) {
            struct.set(offsetY$LAYOUT, offsetY$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt advanceX$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("advanceX"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int advanceX
         * }
         */
        public static final ValueLayout.OfInt advanceX$layout() {
            return advanceX$LAYOUT;
        }

        private static final long advanceX$OFFSET = 12;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int advanceX
         * }
         */
        public static final long advanceX$offset() {
            return advanceX$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int advanceX
         * }
         */
        public static int advanceX(MemorySegment struct) {
            return struct.get(advanceX$LAYOUT, advanceX$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int advanceX
         * }
         */
        public static void advanceX(MemorySegment struct, int fieldValue) {
            struct.set(advanceX$LAYOUT, advanceX$OFFSET, fieldValue);
        }

        private static final GroupLayout image$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("image"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Image image
         * }
         */
        public static final GroupLayout image$layout() {
            return image$LAYOUT;
        }

        private static final long image$OFFSET = 16;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Image image
         * }
         */
        public static final long image$offset() {
            return image$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Image image
         * }
         */
        public static MemorySegment image(MemorySegment struct) {
            return struct.asSlice(image$OFFSET, image$LAYOUT.byteSize());
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Image image
         * }
         */
        public static void image(MemorySegment struct, MemorySegment fieldValue) {
            MemorySegment.copy(fieldValue, 0L, struct, image$OFFSET, image$LAYOUT.byteSize());
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * struct Image {
     *     void *data;
     *     int width;
     *     int height;
     *     int mipmaps;
     *     int format;
     * }
     * }
     */
    public static class Image {

        Image() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            RayFFM.C_POINTER.withName("data"),
            RayFFM.C_INT.withName("width"),
            RayFFM.C_INT.withName("height"),
            RayFFM.C_INT.withName("mipmaps"),
            RayFFM.C_INT.withName("format")
        ).withName("Image");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final AddressLayout data$LAYOUT = (AddressLayout)$LAYOUT.select(groupElement("data"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * void *data
         * }
         */
        public static final AddressLayout data$layout() {
            return data$LAYOUT;
        }

        private static final long data$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * void *data
         * }
         */
        public static final long data$offset() {
            return data$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * void *data
         * }
         */
        public static MemorySegment data(MemorySegment struct) {
            return struct.get(data$LAYOUT, data$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * void *data
         * }
         */
        public static void data(MemorySegment struct, MemorySegment fieldValue) {
            struct.set(data$LAYOUT, data$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt width$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("width"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int width
         * }
         */
        public static final ValueLayout.OfInt width$layout() {
            return width$LAYOUT;
        }

        private static final long width$OFFSET = 8;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int width
         * }
         */
        public static final long width$offset() {
            return width$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int width
         * }
         */
        public static int width(MemorySegment struct) {
            return struct.get(width$LAYOUT, width$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int width
         * }
         */
        public static void width(MemorySegment struct, int fieldValue) {
            struct.set(width$LAYOUT, width$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt height$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("height"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int height
         * }
         */
        public static final ValueLayout.OfInt height$layout() {
            return height$LAYOUT;
        }

        private static final long height$OFFSET = 12;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int height
         * }
         */
        public static final long height$offset() {
            return height$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int height
         * }
         */
        public static int height(MemorySegment struct) {
            return struct.get(height$LAYOUT, height$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int height
         * }
         */
        public static void height(MemorySegment struct, int fieldValue) {
            struct.set(height$LAYOUT, height$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt mipmaps$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("mipmaps"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int mipmaps
         * }
         */
        public static final ValueLayout.OfInt mipmaps$layout() {
            return mipmaps$LAYOUT;
        }

        private static final long mipmaps$OFFSET = 16;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int mipmaps
         * }
         */
        public static final long mipmaps$offset() {
            return mipmaps$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int mipmaps
         * }
         */
        public static int mipmaps(MemorySegment struct) {
            return struct.get(mipmaps$LAYOUT, mipmaps$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int mipmaps
         * }
         */
        public static void mipmaps(MemorySegment struct, int fieldValue) {
            struct.set(mipmaps$LAYOUT, mipmaps$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt format$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("format"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int format
         * }
         */
        public static final ValueLayout.OfInt format$layout() {
            return format$LAYOUT;
        }

        private static final long format$OFFSET = 20;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int format
         * }
         */
        public static final long format$offset() {
            return format$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int format
         * }
         */
        public static int format(MemorySegment struct) {
            return struct.get(format$LAYOUT, format$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int format
         * }
         */
        public static void format(MemorySegment struct, int fieldValue) {
            struct.set(format$LAYOUT, format$OFFSET, fieldValue);
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * struct Matrix {
     *     float m0;
     *     float m4;
     *     float m8;
     *     float m12;
     *     float m1;
     *     float m5;
     *     float m9;
     *     float m13;
     *     float m2;
     *     float m6;
     *     float m10;
     *     float m14;
     *     float m3;
     *     float m7;
     *     float m11;
     *     float m15;
     * }
     * }
     */
    public static class Matrix {

        Matrix() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            RayFFM.C_FLOAT.withName("m0"),
            RayFFM.C_FLOAT.withName("m4"),
            RayFFM.C_FLOAT.withName("m8"),
            RayFFM.C_FLOAT.withName("m12"),
            RayFFM.C_FLOAT.withName("m1"),
            RayFFM.C_FLOAT.withName("m5"),
            RayFFM.C_FLOAT.withName("m9"),
            RayFFM.C_FLOAT.withName("m13"),
            RayFFM.C_FLOAT.withName("m2"),
            RayFFM.C_FLOAT.withName("m6"),
            RayFFM.C_FLOAT.withName("m10"),
            RayFFM.C_FLOAT.withName("m14"),
            RayFFM.C_FLOAT.withName("m3"),
            RayFFM.C_FLOAT.withName("m7"),
            RayFFM.C_FLOAT.withName("m11"),
            RayFFM.C_FLOAT.withName("m15")
        ).withName("Matrix");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final ValueLayout.OfFloat m0$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m0"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m0
         * }
         */
        public static final ValueLayout.OfFloat m0$layout() {
            return m0$LAYOUT;
        }

        private static final long m0$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m0
         * }
         */
        public static final long m0$offset() {
            return m0$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m0
         * }
         */
        public static float m0(MemorySegment struct) {
            return struct.get(m0$LAYOUT, m0$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m0
         * }
         */
        public static void m0(MemorySegment struct, float fieldValue) {
            struct.set(m0$LAYOUT, m0$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m4$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m4"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m4
         * }
         */
        public static final ValueLayout.OfFloat m4$layout() {
            return m4$LAYOUT;
        }

        private static final long m4$OFFSET = 4;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m4
         * }
         */
        public static final long m4$offset() {
            return m4$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m4
         * }
         */
        public static float m4(MemorySegment struct) {
            return struct.get(m4$LAYOUT, m4$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m4
         * }
         */
        public static void m4(MemorySegment struct, float fieldValue) {
            struct.set(m4$LAYOUT, m4$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m8$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m8"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m8
         * }
         */
        public static final ValueLayout.OfFloat m8$layout() {
            return m8$LAYOUT;
        }

        private static final long m8$OFFSET = 8;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m8
         * }
         */
        public static final long m8$offset() {
            return m8$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m8
         * }
         */
        public static float m8(MemorySegment struct) {
            return struct.get(m8$LAYOUT, m8$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m8
         * }
         */
        public static void m8(MemorySegment struct, float fieldValue) {
            struct.set(m8$LAYOUT, m8$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m12$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m12"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m12
         * }
         */
        public static final ValueLayout.OfFloat m12$layout() {
            return m12$LAYOUT;
        }

        private static final long m12$OFFSET = 12;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m12
         * }
         */
        public static final long m12$offset() {
            return m12$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m12
         * }
         */
        public static float m12(MemorySegment struct) {
            return struct.get(m12$LAYOUT, m12$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m12
         * }
         */
        public static void m12(MemorySegment struct, float fieldValue) {
            struct.set(m12$LAYOUT, m12$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m1$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m1"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m1
         * }
         */
        public static final ValueLayout.OfFloat m1$layout() {
            return m1$LAYOUT;
        }

        private static final long m1$OFFSET = 16;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m1
         * }
         */
        public static final long m1$offset() {
            return m1$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m1
         * }
         */
        public static float m1(MemorySegment struct) {
            return struct.get(m1$LAYOUT, m1$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m1
         * }
         */
        public static void m1(MemorySegment struct, float fieldValue) {
            struct.set(m1$LAYOUT, m1$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m5$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m5"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m5
         * }
         */
        public static final ValueLayout.OfFloat m5$layout() {
            return m5$LAYOUT;
        }

        private static final long m5$OFFSET = 20;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m5
         * }
         */
        public static final long m5$offset() {
            return m5$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m5
         * }
         */
        public static float m5(MemorySegment struct) {
            return struct.get(m5$LAYOUT, m5$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m5
         * }
         */
        public static void m5(MemorySegment struct, float fieldValue) {
            struct.set(m5$LAYOUT, m5$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m9$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m9"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m9
         * }
         */
        public static final ValueLayout.OfFloat m9$layout() {
            return m9$LAYOUT;
        }

        private static final long m9$OFFSET = 24;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m9
         * }
         */
        public static final long m9$offset() {
            return m9$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m9
         * }
         */
        public static float m9(MemorySegment struct) {
            return struct.get(m9$LAYOUT, m9$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m9
         * }
         */
        public static void m9(MemorySegment struct, float fieldValue) {
            struct.set(m9$LAYOUT, m9$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m13$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m13"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m13
         * }
         */
        public static final ValueLayout.OfFloat m13$layout() {
            return m13$LAYOUT;
        }

        private static final long m13$OFFSET = 28;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m13
         * }
         */
        public static final long m13$offset() {
            return m13$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m13
         * }
         */
        public static float m13(MemorySegment struct) {
            return struct.get(m13$LAYOUT, m13$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m13
         * }
         */
        public static void m13(MemorySegment struct, float fieldValue) {
            struct.set(m13$LAYOUT, m13$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m2$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m2"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m2
         * }
         */
        public static final ValueLayout.OfFloat m2$layout() {
            return m2$LAYOUT;
        }

        private static final long m2$OFFSET = 32;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m2
         * }
         */
        public static final long m2$offset() {
            return m2$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m2
         * }
         */
        public static float m2(MemorySegment struct) {
            return struct.get(m2$LAYOUT, m2$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m2
         * }
         */
        public static void m2(MemorySegment struct, float fieldValue) {
            struct.set(m2$LAYOUT, m2$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m6$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m6"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m6
         * }
         */
        public static final ValueLayout.OfFloat m6$layout() {
            return m6$LAYOUT;
        }

        private static final long m6$OFFSET = 36;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m6
         * }
         */
        public static final long m6$offset() {
            return m6$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m6
         * }
         */
        public static float m6(MemorySegment struct) {
            return struct.get(m6$LAYOUT, m6$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m6
         * }
         */
        public static void m6(MemorySegment struct, float fieldValue) {
            struct.set(m6$LAYOUT, m6$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m10$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m10"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m10
         * }
         */
        public static final ValueLayout.OfFloat m10$layout() {
            return m10$LAYOUT;
        }

        private static final long m10$OFFSET = 40;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m10
         * }
         */
        public static final long m10$offset() {
            return m10$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m10
         * }
         */
        public static float m10(MemorySegment struct) {
            return struct.get(m10$LAYOUT, m10$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m10
         * }
         */
        public static void m10(MemorySegment struct, float fieldValue) {
            struct.set(m10$LAYOUT, m10$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m14$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m14"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m14
         * }
         */
        public static final ValueLayout.OfFloat m14$layout() {
            return m14$LAYOUT;
        }

        private static final long m14$OFFSET = 44;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m14
         * }
         */
        public static final long m14$offset() {
            return m14$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m14
         * }
         */
        public static float m14(MemorySegment struct) {
            return struct.get(m14$LAYOUT, m14$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m14
         * }
         */
        public static void m14(MemorySegment struct, float fieldValue) {
            struct.set(m14$LAYOUT, m14$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m3$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m3"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m3
         * }
         */
        public static final ValueLayout.OfFloat m3$layout() {
            return m3$LAYOUT;
        }

        private static final long m3$OFFSET = 48;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m3
         * }
         */
        public static final long m3$offset() {
            return m3$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m3
         * }
         */
        public static float m3(MemorySegment struct) {
            return struct.get(m3$LAYOUT, m3$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m3
         * }
         */
        public static void m3(MemorySegment struct, float fieldValue) {
            struct.set(m3$LAYOUT, m3$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m7$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m7"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m7
         * }
         */
        public static final ValueLayout.OfFloat m7$layout() {
            return m7$LAYOUT;
        }

        private static final long m7$OFFSET = 52;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m7
         * }
         */
        public static final long m7$offset() {
            return m7$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m7
         * }
         */
        public static float m7(MemorySegment struct) {
            return struct.get(m7$LAYOUT, m7$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m7
         * }
         */
        public static void m7(MemorySegment struct, float fieldValue) {
            struct.set(m7$LAYOUT, m7$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m11$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m11"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m11
         * }
         */
        public static final ValueLayout.OfFloat m11$layout() {
            return m11$LAYOUT;
        }

        private static final long m11$OFFSET = 56;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m11
         * }
         */
        public static final long m11$offset() {
            return m11$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m11
         * }
         */
        public static float m11(MemorySegment struct) {
            return struct.get(m11$LAYOUT, m11$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m11
         * }
         */
        public static void m11(MemorySegment struct, float fieldValue) {
            struct.set(m11$LAYOUT, m11$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat m15$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("m15"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float m15
         * }
         */
        public static final ValueLayout.OfFloat m15$layout() {
            return m15$LAYOUT;
        }

        private static final long m15$OFFSET = 60;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float m15
         * }
         */
        public static final long m15$offset() {
            return m15$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float m15
         * }
         */
        public static float m15(MemorySegment struct) {
            return struct.get(m15$LAYOUT, m15$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float m15
         * }
         */
        public static void m15(MemorySegment struct, float fieldValue) {
            struct.set(m15$LAYOUT, m15$OFFSET, fieldValue);
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * typedef Vector4 Quaternion
     * }
     */
    public static class Quaternion extends Vector4 {

        Quaternion() {
            // Should not be called directly
        }
    }

    /**
     * {@snippet lang=c :
     * struct Ray {
     *     Vector3 position;
     *     Vector3 direction;
     * }
     * }
     */
    public static class Ray {

        Ray() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            Vector3.layout().withName("position"),
            Vector3.layout().withName("direction")
        ).withName("Ray");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final GroupLayout position$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("position"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Vector3 position
         * }
         */
        public static final GroupLayout position$layout() {
            return position$LAYOUT;
        }

        private static final long position$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Vector3 position
         * }
         */
        public static final long position$offset() {
            return position$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Vector3 position
         * }
         */
        public static MemorySegment position(MemorySegment struct) {
            return struct.asSlice(position$OFFSET, position$LAYOUT.byteSize());
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Vector3 position
         * }
         */
        public static void position(MemorySegment struct, MemorySegment fieldValue) {
            MemorySegment.copy(fieldValue, 0L, struct, position$OFFSET, position$LAYOUT.byteSize());
        }

        private static final GroupLayout direction$LAYOUT = (GroupLayout)$LAYOUT.select(groupElement("direction"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * Vector3 direction
         * }
         */
        public static final GroupLayout direction$layout() {
            return direction$LAYOUT;
        }

        private static final long direction$OFFSET = 12;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * Vector3 direction
         * }
         */
        public static final long direction$offset() {
            return direction$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * Vector3 direction
         * }
         */
        public static MemorySegment direction(MemorySegment struct) {
            return struct.asSlice(direction$OFFSET, direction$LAYOUT.byteSize());
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * Vector3 direction
         * }
         */
        public static void direction(MemorySegment struct, MemorySegment fieldValue) {
            MemorySegment.copy(fieldValue, 0L, struct, direction$OFFSET, direction$LAYOUT.byteSize());
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * struct Rectangle {
     *     float x;
     *     float y;
     *     float width;
     *     float height;
     * }
     * }
     */
    public static class Rectangle {

        Rectangle() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            RayFFM.C_FLOAT.withName("x"),
            RayFFM.C_FLOAT.withName("y"),
            RayFFM.C_FLOAT.withName("width"),
            RayFFM.C_FLOAT.withName("height")
        ).withName("Rectangle");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final ValueLayout.OfFloat x$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("x"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static final ValueLayout.OfFloat x$layout() {
            return x$LAYOUT;
        }

        private static final long x$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static final long x$offset() {
            return x$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static float x(MemorySegment struct) {
            return struct.get(x$LAYOUT, x$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static void x(MemorySegment struct, float fieldValue) {
            struct.set(x$LAYOUT, x$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat y$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("y"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static final ValueLayout.OfFloat y$layout() {
            return y$LAYOUT;
        }

        private static final long y$OFFSET = 4;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static final long y$offset() {
            return y$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static float y(MemorySegment struct) {
            return struct.get(y$LAYOUT, y$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static void y(MemorySegment struct, float fieldValue) {
            struct.set(y$LAYOUT, y$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat width$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("width"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float width
         * }
         */
        public static final ValueLayout.OfFloat width$layout() {
            return width$LAYOUT;
        }

        private static final long width$OFFSET = 8;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float width
         * }
         */
        public static final long width$offset() {
            return width$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float width
         * }
         */
        public static float width(MemorySegment struct) {
            return struct.get(width$LAYOUT, width$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float width
         * }
         */
        public static void width(MemorySegment struct, float fieldValue) {
            struct.set(width$LAYOUT, width$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat height$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("height"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float height
         * }
         */
        public static final ValueLayout.OfFloat height$layout() {
            return height$LAYOUT;
        }

        private static final long height$OFFSET = 12;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float height
         * }
         */
        public static final long height$offset() {
            return height$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float height
         * }
         */
        public static float height(MemorySegment struct) {
            return struct.get(height$LAYOUT, height$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float height
         * }
         */
        public static void height(MemorySegment struct, float fieldValue) {
            struct.set(height$LAYOUT, height$OFFSET, fieldValue);
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * struct Texture {
     *     unsigned int id;
     *     int width;
     *     int height;
     *     int mipmaps;
     *     int format;
     * }
     * }
     */
    public static class Texture {

        Texture() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            RayFFM.C_INT.withName("id"),
            RayFFM.C_INT.withName("width"),
            RayFFM.C_INT.withName("height"),
            RayFFM.C_INT.withName("mipmaps"),
            RayFFM.C_INT.withName("format")
        ).withName("Texture");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final ValueLayout.OfInt id$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("id"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * unsigned int id
         * }
         */
        public static final ValueLayout.OfInt id$layout() {
            return id$LAYOUT;
        }

        private static final long id$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * unsigned int id
         * }
         */
        public static final long id$offset() {
            return id$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * unsigned int id
         * }
         */
        public static int id(MemorySegment struct) {
            return struct.get(id$LAYOUT, id$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * unsigned int id
         * }
         */
        public static void id(MemorySegment struct, int fieldValue) {
            struct.set(id$LAYOUT, id$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt width$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("width"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int width
         * }
         */
        public static final ValueLayout.OfInt width$layout() {
            return width$LAYOUT;
        }

        private static final long width$OFFSET = 4;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int width
         * }
         */
        public static final long width$offset() {
            return width$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int width
         * }
         */
        public static int width(MemorySegment struct) {
            return struct.get(width$LAYOUT, width$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int width
         * }
         */
        public static void width(MemorySegment struct, int fieldValue) {
            struct.set(width$LAYOUT, width$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt height$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("height"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int height
         * }
         */
        public static final ValueLayout.OfInt height$layout() {
            return height$LAYOUT;
        }

        private static final long height$OFFSET = 8;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int height
         * }
         */
        public static final long height$offset() {
            return height$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int height
         * }
         */
        public static int height(MemorySegment struct) {
            return struct.get(height$LAYOUT, height$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int height
         * }
         */
        public static void height(MemorySegment struct, int fieldValue) {
            struct.set(height$LAYOUT, height$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt mipmaps$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("mipmaps"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int mipmaps
         * }
         */
        public static final ValueLayout.OfInt mipmaps$layout() {
            return mipmaps$LAYOUT;
        }

        private static final long mipmaps$OFFSET = 12;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int mipmaps
         * }
         */
        public static final long mipmaps$offset() {
            return mipmaps$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int mipmaps
         * }
         */
        public static int mipmaps(MemorySegment struct) {
            return struct.get(mipmaps$LAYOUT, mipmaps$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int mipmaps
         * }
         */
        public static void mipmaps(MemorySegment struct, int fieldValue) {
            struct.set(mipmaps$LAYOUT, mipmaps$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfInt format$LAYOUT = (ValueLayout.OfInt)$LAYOUT.select(groupElement("format"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * int format
         * }
         */
        public static final ValueLayout.OfInt format$layout() {
            return format$LAYOUT;
        }

        private static final long format$OFFSET = 16;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * int format
         * }
         */
        public static final long format$offset() {
            return format$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * int format
         * }
         */
        public static int format(MemorySegment struct) {
            return struct.get(format$LAYOUT, format$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * int format
         * }
         */
        public static void format(MemorySegment struct, int fieldValue) {
            struct.set(format$LAYOUT, format$OFFSET, fieldValue);
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * typedef Texture Texture2D
     * }
     */
    public static class Texture2D extends Texture {

        Texture2D() {
            // Should not be called directly
        }
    }

    /**
     * {@snippet lang=c :
     * struct Vector2 {
     *     float x;
     *     float y;
     * }
     * }
     */
    public static class Vector2 {

        Vector2() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            RayFFM.C_FLOAT.withName("x"),
            RayFFM.C_FLOAT.withName("y")
        ).withName("Vector2");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final ValueLayout.OfFloat x$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("x"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static final ValueLayout.OfFloat x$layout() {
            return x$LAYOUT;
        }

        private static final long x$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static final long x$offset() {
            return x$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static float x(MemorySegment struct) {
            return struct.get(x$LAYOUT, x$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static void x(MemorySegment struct, float fieldValue) {
            struct.set(x$LAYOUT, x$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat y$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("y"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static final ValueLayout.OfFloat y$layout() {
            return y$LAYOUT;
        }

        private static final long y$OFFSET = 4;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static final long y$offset() {
            return y$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static float y(MemorySegment struct) {
            return struct.get(y$LAYOUT, y$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static void y(MemorySegment struct, float fieldValue) {
            struct.set(y$LAYOUT, y$OFFSET, fieldValue);
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * struct Vector3 {
     *     float x;
     *     float y;
     *     float z;
     * }
     * }
     */
    public static class Vector3 {

        Vector3() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            RayFFM.C_FLOAT.withName("x"),
            RayFFM.C_FLOAT.withName("y"),
            RayFFM.C_FLOAT.withName("z")
        ).withName("Vector3");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final ValueLayout.OfFloat x$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("x"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static final ValueLayout.OfFloat x$layout() {
            return x$LAYOUT;
        }

        private static final long x$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static final long x$offset() {
            return x$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static float x(MemorySegment struct) {
            return struct.get(x$LAYOUT, x$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static void x(MemorySegment struct, float fieldValue) {
            struct.set(x$LAYOUT, x$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat y$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("y"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static final ValueLayout.OfFloat y$layout() {
            return y$LAYOUT;
        }

        private static final long y$OFFSET = 4;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static final long y$offset() {
            return y$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static float y(MemorySegment struct) {
            return struct.get(y$LAYOUT, y$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static void y(MemorySegment struct, float fieldValue) {
            struct.set(y$LAYOUT, y$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat z$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("z"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float z
         * }
         */
        public static final ValueLayout.OfFloat z$layout() {
            return z$LAYOUT;
        }

        private static final long z$OFFSET = 8;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float z
         * }
         */
        public static final long z$offset() {
            return z$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float z
         * }
         */
        public static float z(MemorySegment struct) {
            return struct.get(z$LAYOUT, z$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float z
         * }
         */
        public static void z(MemorySegment struct, float fieldValue) {
            struct.set(z$LAYOUT, z$OFFSET, fieldValue);
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }

    /**
     * {@snippet lang=c :
     * struct Vector4 {
     *     float x;
     *     float y;
     *     float z;
     *     float w;
     * }
     * }
     */
    public static class Vector4 {

        Vector4() {
            // Should not be called directly
        }

        private static final GroupLayout $LAYOUT = MemoryLayout.structLayout(
            RayFFM.C_FLOAT.withName("x"),
            RayFFM.C_FLOAT.withName("y"),
            RayFFM.C_FLOAT.withName("z"),
            RayFFM.C_FLOAT.withName("w")
        ).withName("Vector4");

        /**
         * The layout of this struct
         */
        public static final GroupLayout layout() {
            return $LAYOUT;
        }

        private static final ValueLayout.OfFloat x$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("x"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static final ValueLayout.OfFloat x$layout() {
            return x$LAYOUT;
        }

        private static final long x$OFFSET = 0;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static final long x$offset() {
            return x$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static float x(MemorySegment struct) {
            return struct.get(x$LAYOUT, x$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float x
         * }
         */
        public static void x(MemorySegment struct, float fieldValue) {
            struct.set(x$LAYOUT, x$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat y$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("y"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static final ValueLayout.OfFloat y$layout() {
            return y$LAYOUT;
        }

        private static final long y$OFFSET = 4;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static final long y$offset() {
            return y$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static float y(MemorySegment struct) {
            return struct.get(y$LAYOUT, y$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float y
         * }
         */
        public static void y(MemorySegment struct, float fieldValue) {
            struct.set(y$LAYOUT, y$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat z$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("z"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float z
         * }
         */
        public static final ValueLayout.OfFloat z$layout() {
            return z$LAYOUT;
        }

        private static final long z$OFFSET = 8;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float z
         * }
         */
        public static final long z$offset() {
            return z$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float z
         * }
         */
        public static float z(MemorySegment struct) {
            return struct.get(z$LAYOUT, z$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float z
         * }
         */
        public static void z(MemorySegment struct, float fieldValue) {
            struct.set(z$LAYOUT, z$OFFSET, fieldValue);
        }

        private static final ValueLayout.OfFloat w$LAYOUT = (ValueLayout.OfFloat)$LAYOUT.select(groupElement("w"));

        /**
         * Layout for field:
         * {@snippet lang=c :
         * float w
         * }
         */
        public static final ValueLayout.OfFloat w$layout() {
            return w$LAYOUT;
        }

        private static final long w$OFFSET = 12;

        /**
         * Offset for field:
         * {@snippet lang=c :
         * float w
         * }
         */
        public static final long w$offset() {
            return w$OFFSET;
        }

        /**
         * Getter for field:
         * {@snippet lang=c :
         * float w
         * }
         */
        public static float w(MemorySegment struct) {
            return struct.get(w$LAYOUT, w$OFFSET);
        }

        /**
         * Setter for field:
         * {@snippet lang=c :
         * float w
         * }
         */
        public static void w(MemorySegment struct, float fieldValue) {
            struct.set(w$LAYOUT, w$OFFSET, fieldValue);
        }

        /**
         * Obtains a slice of {@code arrayParam} which selects the array element at {@code index}.
         * The returned segment has address {@code arrayParam.address() + index * layout().byteSize()}
         */
        public static MemorySegment asSlice(MemorySegment array, long index) {
            return array.asSlice(layout().byteSize() * index);
        }

        /**
         * The size (in bytes) of this struct
         */
        public static long sizeof() { return layout().byteSize(); }

        /**
         * Allocate a segment of size {@code layout().byteSize()} using {@code allocator}
         */
        public static MemorySegment allocate(SegmentAllocator allocator) {
            return allocator.allocate(layout());
        }

        /**
         * Allocate an array of size {@code elementCount} using {@code allocator}.
         * The returned segment has size {@code elementCount * layout().byteSize()}.
         */
        public static MemorySegment allocateArray(long elementCount, SegmentAllocator allocator) {
            return allocator.allocate(MemoryLayout.sequenceLayout(elementCount, layout()));
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, Arena arena, Consumer<MemorySegment> cleanup) {
            return reinterpret(addr, 1, arena, cleanup);
        }

        /**
         * Reinterprets {@code addr} using target {@code arena} and {@code cleanupAction} (if any).
         * The returned segment has size {@code elementCount * layout().byteSize()}
         */
        public static MemorySegment reinterpret(MemorySegment addr, long elementCount, Arena arena, Consumer<MemorySegment> cleanup) {
            return addr.reinterpret(layout().byteSize() * elementCount, arena, cleanup);
        }
    }
}
