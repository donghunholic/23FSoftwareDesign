package com.mdeditor.sd;

import com.mdeditor.sd.editor.MarkdownEditor;
import com.vladsch.flexmark.util.ast.Node;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
            case NEW_BLOCK -> {
                String str = block.getMdText().substring(block.getCaretPosition()-1);
                block.setMdText(block.getMdText().substring(0,block.getCaretPosition()-1));
                block.renderHTML();
                SingleLineBlock newBlock = new SingleLineBlock(this);
                newBlock.setMdText(str);
                blockList.add(idx+1, newBlock);

                mdEditor.updateUI();
                newBlock.requestFocusInWindow();
                //newBlock.renderMD();
                this.blockOnFocus = newBlock;
            }
            case DELETE_BLOCK -> {
                if(idx > 0){
                    Block newFocusBlock = blockList.get(idx-1);
                    newFocusBlock.setMdText(newFocusBlock.getMdText() + block.getMdText());
                    blockList.remove(block);
                    block.destruct();
                    mdEditor.updateUI();
                    newFocusBlock.requestFocusInWindow();
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
}
