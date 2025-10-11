package dmacd.ffm;

import java.lang.foreign.MemorySegment;

public abstract class Struct {
    protected MemorySegment ms;
    public Struct(MemorySegment ms) {
        this.ms = ms;
    }
    public MemorySegment ms() { return ms; }

}

