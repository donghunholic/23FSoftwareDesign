package com.mdeditor.sd;

import javax.swing.*;
import static com.mdeditor.sd.Utils.*;

/**
 * Block contains mdText.
 * When the block is out of focus, setText(MD2HTML) // MD2HTML : commonmark util function
 * When the block grabs focus, setText(getMdText)
 */
public class Block extends JTextPane {

    private String mdText;
    private final BlockManager blockManager;

    public Block(BlockManager manager){
        this.mdText = "";
        this.blockManager = manager;
    }

    public String getMdText(){
        return mdText;
    }

    public void setMdText(String newText){
        mdText = newText;
    }

    /**
     * convert mdText to HTML
     * convertHTML() // TODO
     */
    public void renderHTML(){
        this.setText(convertHTML(getMdText()));
    }

    public void renderMD(){
        this.setText(mdText);
    }

    /**
     * Caution: this is from View.
     * @return string from jTextPane
     */
    public String getCurText(){
        return this.getText();
    }

    public Block getBlock(){
        return this;
    }

    public BlockManager getManager(){
        return blockManager;
    }

    public void requestManager(BlockEvent e){
        blockManager.update(this, e);
    }
}
