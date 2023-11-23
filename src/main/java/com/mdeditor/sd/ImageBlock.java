package com.mdeditor.sd;

public class ImageBlock extends SingleLineBlock {
    public String href;

    public ImageBlock(BlockManager manager, String href) {
        super(manager);
        this.href = href;
    }
}
