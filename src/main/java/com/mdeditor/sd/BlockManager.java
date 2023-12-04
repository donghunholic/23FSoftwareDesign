package com.mdeditor.sd;

import com.intellij.openapi.editor.Caret;
import com.mdeditor.sd.editor.MarkdownEditor;
import com.vladsch.flexmark.util.ast.Node;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
    }

    /**
     * Handle Focus event
     * Block is created or deleted, request update to MarkdownEditor
     */
    public void update(Block block, BlockEvent e) {
        int idx = blockList.indexOf(block);
        blockOnFocus.setMdText(blockOnFocus.getText().strip());

        switch (e) {
            case UPDATE_BLOCK -> { }

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
                    BlockParse(idx);
                    blockList.get(idx).renderHTML();
                    //RenderAll();
                    mdEditor.updateUI();
                    SwingUtilities.invokeLater(()->{
                        blockList.get(idx-1).requestFocusInWindow();
                    });
                    this.blockOnFocus = blockList.get(idx-1);

                }
            }
            case OUTFOCUS_BLOCK_DOWN -> {
                if(idx < blockList.size()-1){
                    BlockParse(idx);
                    blockList.get(idx).renderHTML();
                    //RenderAll();
                    mdEditor.updateUI();
                    SwingUtilities.invokeLater(()->{
                        blockList.get(idx+1).requestFocusInWindow();
                    });
                    this.blockOnFocus = blockList.get(idx+1);
                }
            }
            case OUTFOCUS_CLICKED ->{
                BlockParse(idx);
                blockOnFocus.renderHTML();
                //block.renderMD();
                mdEditor.updateUI();
                SwingUtilities.invokeLater(()->{
                    blockList.get(idx).requestFocusInWindow();
                });
                blockOnFocus = blockList.get(idx);
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
            if(block == blockOnFocus){
                fullMd.append(block.getText());
            }
            else{
                fullMd.append(block.getMdText());
            }

            fullMd.append("\n\n");
        }
        return fullMd.toString();
    }

    /**
     * parse the block which is at BlockList[idx]
     * @param idx - the integer of Block's index. Must have value between 0 ~ BlockList.length()
     */
    public void BlockParse(int idx){
        int cur = idx;
        int nl_idx = 0;
        Block temp = this.blockList.get(cur);
        String str = temp.getMdText();
        String prefix = "";
        String newSingleStr = "";
        String newMultiStr = "";
        int prefix_len = 0;
        boolean is_last_line = false;
        if(Utils.prefix_check(temp) != 0){
            prefix_len = Utils.prefix_check(temp);
            prefix = str.substring(temp.getIndent_level() * 2, temp.getIndent_level() * 2 + prefix_len);
            blockList.remove(temp);
            temp = new MultiLineBlock(this, prefix);
            temp.setMdText(str);
            blockList.add(cur, temp);

            while(str.indexOf("\n", nl_idx) != -1 || is_last_line){
                if(is_last_line){
                    break;
                }
                if(!str.substring(nl_idx, nl_idx + prefix_len).equals(prefix)){
                    newSingleStr = str.substring(nl_idx);
                    newMultiStr = str.substring(0,nl_idx);
                    MultiLineBlock curBlock = new MultiLineBlock(this, prefix);
                    SingleLineBlock newBlock = new SingleLineBlock(this);
                    newBlock.setMdText(newSingleStr);
                    curBlock.setMdText(newMultiStr);
                    blockList.remove(temp);
                    blockList.add(cur, curBlock);
                    blockList.add(cur + 1,newBlock);
                    break;
                }
                nl_idx = str.indexOf("\n", nl_idx) + 1;
                if(str.indexOf("\n", nl_idx + 1) == -1){
                    is_last_line = true;
                }
            }
        }
        else{
            if(str.indexOf("\n", nl_idx) == -1){
                is_last_line = true;
            }
            if(!is_last_line){
                nl_idx = str.indexOf("\n", nl_idx);
                newSingleStr = str.substring(0, nl_idx + 1);
                SingleLineBlock newBlock = new SingleLineBlock(this);
                newBlock.setMdText(newSingleStr);
                blockList.add(cur,newBlock);
                temp.setMdText(str.substring(nl_idx+1));
            }
        }
        if(!is_last_line && idx + 1 < blockList.size()){
            BlockParse(idx+1);
        }
    }

    public void setBlocks(String markdownString){
        blockList.clear();
        for(Node child : Utils.flexmarkParse(markdownString).getChildren()){
            Document doc = Jsoup.parse(Utils.flexmarkHtmlRender(child));
            String tagName = doc.select("body > *").get(0).tagName();

            Block block;
            if(MultiLine.isMultiLine(tagName)){
                block = new MultiLineBlock(this, "");
            }
            else{
                block = new SingleLineBlock(this);
            }

            String markdownText = child.getChars().toString();
            block.setMdText(markdownText);

            block.renderHTML();

            blockList.add(block);
        }

        if(blockList.isEmpty()){
            blockList.add(new SingleLineBlock(this));
        }

        blockOnFocus = blockList.get(0);
        blockOnFocus.renderMD();
        blockOnFocus.grabFocus();
        blockOnFocus.setCaretPosition(0); // FIXME : initial focus must be in first block

        mdEditor.updateUI();
    }

    public void RenderAll(){
        for(int i = 0; i < blockList.size(); i++){
            blockList.get(i).renderHTML();
        }
    }
}
