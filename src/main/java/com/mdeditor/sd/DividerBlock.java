package com.mdeditor.sd;

public class DividerBlock extends SingleLineBlock {
    public DividerBlock(BlockManager manager) {
        super(manager);
        this.setMdText("---\n");
    }
}
