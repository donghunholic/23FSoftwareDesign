package com.mdeditor.sd;

public class ImageBlock extends SingleLineBlock {
    public String href;

    public ImageBlock(MarkdownEditor mdE, String href) {
        super(mdE);
        this.href = href;
    }
}
