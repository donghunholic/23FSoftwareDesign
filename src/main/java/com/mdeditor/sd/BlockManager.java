package com.mdeditor.sd;

import org.apache.batik.bridge.Mark;

import java.util.List;

public class BlockManager {
    private List<Block> blockList;
    private final MarkdownEditor mdEditor;

    public BlockManager(MarkdownEditor mdE) {
        mdEditor = mdE;
    }
    /* add block to blockList */
    public boolean attach(Block block) {
        return blockList.add(block);
    }

    /* remove block to blockList */
    public boolean detach(Block block) {
        return blockList.remove(block);
    }

    public void update(Block block, BlockEvent e) {
        switch (e) {
            case NEW_BLOCK -> {
                blockList.add(blockList.indexOf(block), new Block(mdEditor));
            }
            case DELETE_BLOCK -> {
                blockList.remove(block);
            }
            case OUTFOCUS_BLOCK_UP -> {
            }
            case OUTFOCUS_BLOCK_DOWN -> {
            }
            default -> {
            }
        }
    }

}
