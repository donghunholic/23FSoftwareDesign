package com.mdeditor.sd;

import com.intellij.util.containers.LinkedListWithSum;
import org.apache.batik.bridge.Mark;

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
        switch (e) {
            case NEW_BLOCK -> {
                blockList.add(blockList.indexOf(block), new Block(this));

            }
            case DELETE_BLOCK -> {
                blockList.remove(block);
            }
            case OUTFOCUS_BLOCK_UP -> {
                int temp = blockList.indexOf(block);
                blockList.get(temp == 0 ? 0 : temp - 1).requestFocus();
            }
            case OUTFOCUS_BLOCK_DOWN -> {
                int temp = blockList.indexOf(block);
                blockList.get(temp == blockList.size() - 1 ? temp : temp + 1).requestFocus();
            }
            default -> {
            }
        }
    }

}
