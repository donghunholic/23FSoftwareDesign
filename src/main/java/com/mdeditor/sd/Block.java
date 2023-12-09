package com.mdeditor.sd;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Block contains mdText.
 * When the block is out of focus, renderHTML()
 * When the block grabs focus, renderMD()
 */
public class Block extends JTextPane {

    private String mdText;
    private BlockManager blockManager;

    public final String indent = "  ";
    private int indent_level;

    //protected int PreCaretPosition;
    protected int CaretPosition;

    public Block(BlockManager manager){
        this.mdText = "";
        this.setEditable(true);
        this.blockManager = manager;
        this.indent_level = 0;
        //PreCaretPosition=0;
        CaretPosition=0;


        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestManager(BlockEvent.OUTFOCUS_CLICKED, getCaretPosition());

            }

            @Override
            public void mousePressed(MouseEvent e) { }

            @Override
            public void mouseReleased(MouseEvent e) {  }

            @Override
            public void mouseEntered(MouseEvent e) {  }

            @Override
            public void mouseExited(MouseEvent e) {  }
        });

        this.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                if(e.getDot()==0&&CaretPosition==-1)
                {
                    CaretPosition=-1;
                }
                else
                {
                    CaretPosition=e.getDot();
                }
            }
        });
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
        this.setContentType("text/html");
        this.setText(Utils.stringToHtmlWithCss(getMdText()));
    }

    /**
     * Set block's jTextPane to mdText
     */
    public void renderMD(){
        if(!this.getContentType().equals("text/plain") || this.getText().isEmpty()){
            this.setContentType("text/plain");
            this.setText(mdText);
        }
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
    public void requestManager(BlockEvent e, int pos){
        blockManager.update(this, e, pos);
    }

    @Override
    public boolean requestFocusInWindow(){
        renderMD();
        return super.requestFocusInWindow();
    }

    /**
     * Clear mdText and deallocate blockManager
     * for garbage collection works to this block.
     */
    public void destruct(){
        mdText = null;
        blockManager = null;
    }

    public void setIndent_level(int level){
        indent_level = level;
    }

    public int getIndent_level(){
        return this.indent_level;
    }
}
