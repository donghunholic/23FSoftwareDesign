package com.mdeditor.sd;

import java.util.LinkedList;

public class BlockManager {
    private final LinkedList<Block> blockList;
    private final MarkdownEditor mdEditor;

    public BlockManager(MarkdownEditor mdE) {
        this.blockList = new LinkedList<>();
        this.mdEditor = mdE;
    }

    /**
     * Handle Focus event
     * Block is created or deleted, request update to MarkdownEditor
     */
    public void update(Block block, BlockEvent e) {
        int temp = blockList.indexOf(block);
        switch (e) {
            case NEW_BLOCK :
                blockList.add(blockList.indexOf(block), new Block(this));
                //request update to mdEditor
                break;
            case DELETE_BLOCK :
                blockList.remove(block);
                //request update to mdEditor
                break;
            case OUTFOCUS_BLOCK_UP :
                blockList.get(temp == 0 ? 0 : temp - 1).requestFocus();
                break;
            case OUTFOCUS_BLOCK_DOWN :
                blockList.get(temp == blockList.size() - 1 ? temp : temp + 1).requestFocus();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + e);
        }
    }

}
