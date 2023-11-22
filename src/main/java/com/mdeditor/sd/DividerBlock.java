package com.mdeditor.sd;

public class DividerBlock extends SingleLineBlock {
    public DividerBlock(MarkdownEditor mdE) {
        super(mdE);
        this.setMdText("---\n");
    }
}
