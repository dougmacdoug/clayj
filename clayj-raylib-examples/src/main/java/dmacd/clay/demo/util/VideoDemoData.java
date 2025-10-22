package dmacd.clay.demo.util;

import java.util.List;

public class VideoDemoData {
    public final List<DemoDocument> documents;
    public VideoDemoData(List<DemoDocument> documents) {
        this.documents = documents;
    }
    public int selectedDocumentIndex = 0;
}
