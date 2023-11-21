package com.mdeditor.sd;

import com.intellij.util.containers.LinkedListWithSum;
import org.apache.batik.bridge.Mark;

import java.util.LinkedList;
import java.util.List;

public class BlockManager {
    private final List<Block> blockList;
    private final MarkdownEditor mdEditor;

    public BlockManager(MarkdownEditor mdE) {
        this.blockList = new LinkedList<>();
        this.mdEditor = mdE;
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
                blockList.add(blockList.indexOf(block), new Block(this));
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
