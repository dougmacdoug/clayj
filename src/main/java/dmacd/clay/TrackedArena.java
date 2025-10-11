package dmacd.clay;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

public class TrackedArena implements Arena, AutoCloseable {

    private final Arena arena;
    private long totalAllocatedBytes = 0;

    private TrackedArena(Arena arena) {
        this.arena = arena;
    }

    public static TrackedArena ofConfined() {
        return new TrackedArena(Arena.ofConfined());
    }

    public MemorySegment allocate(long byteSize) {
        MemorySegment segment = arena.allocate(byteSize);
        totalAllocatedBytes += byteSize;
        return segment;
    }

    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {
//        var re = new RuntimeException("asked for " + byteSize);
//        re.printStackTrace(); // todo: testing only
        MemorySegment segment = arena.allocate(byteSize, byteAlignment);
        totalAllocatedBytes += byteSize;
        return segment;
    }

    @Override
    public MemorySegment.Scope scope() {
        return arena.scope();
    }

    public MemorySegment allocateFrom(String str) {
        MemorySegment segment = arena.allocateFrom(str);
        totalAllocatedBytes += segment.byteSize();
        return segment;
    }

    public long getTotalAllocatedBytes() {
        return totalAllocatedBytes;
    }

    @Override
    public void close() {
        arena.close();
    }
}