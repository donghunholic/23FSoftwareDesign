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

    public int getCaretPosition(int position){
        if (mdText == null || mdText.isEmpty() || position < 0 || position > mdText.length()) {
            return -1;
        }


        char prefix=GetPrefix();

        switch (prefix) {
            case '#':
                return MarkdownHeaderPosition(position);
            case '>':
                return MarkdownQuotePosition(position);
            case '-':
            case '*':
            case '+':
            case '.':
                return MarkdownListPosition(position,prefix);
            default:
                return position;
        }
    }

    private char GetPrefix(){
        int prefixPos=0;
        while(mdText.charAt(prefixPos)==' ')
        {
            prefixPos++;
        }
        char prefix = mdText.charAt(prefixPos);
        if (Character.isDigit(prefix))
        {
            prefix='.';
        }

        return prefix;
    }

    private int MarkdownHeaderPosition(int position) {
        int prefixLength = getPrefixLength('#');
        if (prefixLength == -1) {
            return -1;
        }
        prefixLength++;

        int adjustedPosition = (position - 1) + prefixLength;
        return adjustedPosition;
    }

    private int MarkdownQuotePosition(int position) {
        int prefixLength = getPrefixLength('>');
        if (prefixLength == -1) {
            return -1;
        }

        int adjustedPosition = (position - 1) + prefixLength;
        return adjustedPosition;
    }

    private int getPrefixLength(char prefix) {
        int length = 0;
        for (int i = 0; i < mdText.length(); i++) {
            if (mdText.charAt(i) == prefix) {
                length++;
            } else if (mdText.charAt(i) != prefix) {
                return length;
            } else {
                return -1;
            }
        }
        return -1;
    }

    private int MarkdownListPosition(int position, char targetChar) {
        int htmlPos=0;
        int startpos=mdText.indexOf(targetChar);
        while(htmlPos<=position)
        {
            startpos++;
            if(mdText.charAt(startpos)==' ')
            {
                continue;
            }
            int endpos=mdText.indexOf('\n', startpos);
            if(endpos==-1)
            {
                endpos=mdText.length();
            }

            htmlPos++;
            if(htmlPos==position)
            {
                return startpos;
            }
            int curPos=startpos;
            for(int i=0;i<endpos-startpos;i++)
            {
                htmlPos++;
                curPos++;
                if(htmlPos==position)
                {
                    return curPos;
                }
            }
            startpos=mdText.indexOf(targetChar, startpos + 1);
            if(startpos==-1)
            {
                break;
            }
        }
        return -1;
    }

    public Block getThis(){
        return this;
    }
}
