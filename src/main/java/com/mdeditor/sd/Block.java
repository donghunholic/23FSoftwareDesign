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
    private BlockManager blockManager;

    public Block(BlockManager manager){
        this.mdText = "";
        this.setEditable(true);
        this.blockManager = manager;
    }

    /**
     * Get block mdText
     * @return mdText
     */
    public String getMdText(){
        return this.mdText;
    }

    /**
     * Set mdText to newText.
     * Called inside renderHTML()
     */
    public void setMdText(String newText){
        mdText = newText;
    }

    /**
     * convert mdText to HTML
     * using Utils.stringToHtml()
     */
    public void renderHTML(){
        this.setMdText(getCurText());
        this.setText(Utils.stringToHtml(getMdText()));

    }

    /**
     * Set block's jTextPane to mdText
     */
    public void renderMD(){
        this.setText(mdText);
    }

    /**
     * Caution: String is from jTextPane.
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

    /**
     * Requests blockManager to handle BlockEvents by keyListener
     * @param e - See BlockEvent.java
     */
    public void requestManager(BlockEvent e){
        blockManager.update(this, e);
    }

    @Override
    public void grabFocus(){
        super.grabFocus();
        renderMD();
    }

    /**
     * Clear mdText and deallocate blockManager
     * for garbage collection works to this block.
     */
    public void destruct(){
        mdText = null;
        blockManager = null;
    }
}
