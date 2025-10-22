package dmacd.clay.demo.util;

import dmacd.clay.Clay;

public class DemoDocument {
    public final Clay.ClayString title;
    public final Clay.ClayString text;

    /**
     * Creates a Document for the Demo
     * @param title must be string literal
     * @param text must be string literal
     */
    public DemoDocument(String title, String text) {
        this.title = Clay.ClayString.literal(title);
        this.text = Clay.ClayString.literal(text);
    }

}
