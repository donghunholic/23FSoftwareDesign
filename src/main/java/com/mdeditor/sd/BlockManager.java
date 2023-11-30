package com.mdeditor.sd;

import com.intellij.openapi.editor.Caret;
import com.mdeditor.sd.editor.MarkdownEditor;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;


public class BlockManager {
    private final List<Block> blockList;
    private final MarkdownEditor mdEditor;
    private Block blockOnFocus;

    public BlockManager(MarkdownEditor mdE) {
        this.blockList = new LinkedList<>();
        this.mdEditor = mdE;

        blockList.add(new SingleLineBlock(this));
        blockList.get(0).grabFocus();
        this.blockOnFocus = blockList.get(0);
    }

    /**
     * Handle Focus event
     * Block is created or deleted, request update to MarkdownEditor
     */
    public void update(Block block, BlockEvent e) {
        int idx = blockList.indexOf(block);
        blockOnFocus.setMdText(blockOnFocus.getText().strip());

        switch (e) {
            case NEW_BLOCK -> {
                String str = block.getMdText().substring(block.getCaretPosition()-1);
                block.setMdText(block.getMdText().substring(0,block.getCaretPosition()-1));
                block.renderHTML();
                SingleLineBlock newBlock = new SingleLineBlock(this);
                newBlock.setMdText(str);
                blockList.add(idx+1, newBlock);

                mdEditor.updateUI();
                SwingUtilities.invokeLater(() -> {
                    newBlock.requestFocusInWindow();
                });
                this.blockOnFocus = newBlock;
            }
            case DELETE_BLOCK -> {
                if(idx > 0){
                    Block newFocusBlock = blockList.get(idx-1);
                    newFocusBlock.setMdText(newFocusBlock.getMdText() + block.getMdText());
                    blockList.remove(block);
                    block.destruct();
                    mdEditor.updateUI();
                    SwingUtilities.invokeLater(() -> {
                        newFocusBlock.requestFocusInWindow();
                    });

                    this.blockOnFocus = newFocusBlock;
                }
            }
            case OUTFOCUS_BLOCK_UP -> {
                if(idx > 0){
                    block.renderHTML();
                    blockList.get(idx-1).requestFocusInWindow();
                    this.blockOnFocus = blockList.get(idx-1);
                }
            }
            case OUTFOCUS_BLOCK_DOWN -> {
                if(idx < blockList.size()-1){
                    block.renderHTML();
                    blockList.get(idx+1).requestFocusInWindow();
                    this.blockOnFocus = blockList.get(idx+1);
                }
            }
            case OUTFOCUS_CLICKED ->{
                blockOnFocus.renderHTML();
                block.renderMD();
                //block.requestFocusInWindow();
                blockOnFocus = block;
            }
            case TRANSFORM_MULTI -> {
                String temp = block.getMdText();

                /* Caution! : prefix parameter must be implemented */
                blockList.add(idx, new MultiLineBlock(this, new String()));
                blockList.remove(block);
                block.destruct();
                blockList.get(idx).setMdText(temp);
                blockList.get(idx).requestFocusInWindow();
            }
            case TRANSFORM_SINGLE -> {
                //implement multi block to single block
            }
            default -> { throw new IllegalStateException("Unexpected value: " + e); }
        }
    }

    public List<Block> getBlockList(){
        return this.blockList;
    }

    /**
     * Extract mdText sequentially from every block.
     * @return Full Markdown text which will be saved into the (virtual) file.
     */
    public String extractFullMd(){
        StringBuilder fullMd = new StringBuilder();
        for(Block block : blockList){
            fullMd.append(block.getMdText());
            fullMd.append("\n");
        }
        return fullMd.toString();
    }
}
