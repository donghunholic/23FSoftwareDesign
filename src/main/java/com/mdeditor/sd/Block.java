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
    private boolean isOnFocus;
    private final MarkdownEditor mdEditor;

    public Block(MarkdownEditor mdE){
        mdText = "";
        isOnFocus = true;
        mdEditor = mdE;
    }

    public String getMdText(){
        return mdText;
    }

    public void setMdText(String newText){
        mdText = newText;
    }

    public boolean getFocus(){
        return isOnFocus;
    }

    public void setFocus(boolean newBool){
        isOnFocus = newBool;
    }

    public void changeFocus(){
        isOnFocus = !isOnFocus;
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

    public MarkdownEditor getManager(){
        return mdEditor;
    }

    @Override
    public void grabFocus() {
        super.grabFocus();
        isOnFocus = true;
    }

    public void requestManager(BlockEvent e){
        switch (e) {
            case NEW_BLOCK -> {
            }
            case DELETE_BLOCK -> {
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
