package com.mdeditor.sd.block;

import com.mdeditor.sd.manager.BlockManager;
import com.mdeditor.sd.utils.Utils;
import com.mdeditor.sd.manager.BlockEvent;

import javax.swing.*;
import java.awt.*;
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
    protected int caretPosition;

    public Block(BlockManager manager){
        this.mdText = "";
        this.setEditable(true);
        this.blockManager = manager;
        this.caretPosition=0;
        this.setFont(new Font("Jetbrains Mono", Font.PLAIN, 15));

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestManager(BlockEvent.OUTFOCUS_CLICKED, getCaretPosition());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // nothing to do here, so left it empty
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // nothing to do here, so left it empty
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // nothing to do here, so left it empty
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // nothing to do here, so left it empty
            }
        });

        this.addCaretListener(e -> {
            if(e.getDot()==0 && caretPosition==-1) {
                caretPosition=-1;
            }
            else {
                caretPosition=e.getDot();
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

    /**
     * Function to correct caret difference before and after block's content type change
     * @param position - primitive caret position (in HTML format)
     * @return caret position in MD format
     */
    public int getCaretPosition(int position){
        if (mdText == null || mdText.isEmpty() || position < 0 || position > mdText.length()) {
            return 0;
        }


        char prefix = getPrefix();

        switch (prefix) {
            case '#':
                return markdownHeaderPosition(position);
            case '>':
                return markdownQuotePosition(position);
            case '-', '*', '+', '.':
                return markdownListPosition(position,prefix);
            default:
                return position;
        }
    }

    private char getPrefix(){
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

    private int markdownHeaderPosition(int position) {
        int prefixLength = getPrefixLength('#');
        if (prefixLength == -1) {
            return position;
        }
        prefixLength++;

        return (position - 1) + prefixLength;
    }

    private int markdownQuotePosition(int position) {
        int prefixLength = getPrefixLength('>');
        if (prefixLength == -1) {
            return position;
        }

        return (position - 1) + prefixLength;
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

    private int markdownListPosition(int position, char targetChar) {
        int htmlPos=0;
        int startpos=mdText.indexOf(targetChar);
        while(htmlPos<=position) {
            startpos++;
            if(mdText.charAt(startpos)==' ') {
                continue;
            }
            int endpos=mdText.indexOf('\n', startpos);
            if(endpos==-1) {
                endpos=mdText.length();
            }

            htmlPos++;
            if(htmlPos==position) {
                return startpos;
            }
            int curPos=startpos;

            for(int i=0;i<endpos-startpos;i++) {
                htmlPos++;
                curPos++;
                if(htmlPos==position)
                {
                    return curPos;
                }
            }

            startpos=mdText.indexOf(targetChar, startpos + 1);
            if(startpos==-1) {
                break;
            }
        }
        return position;
    }

    /**
     * @return Number of spaces(indent) where the cursor is
     */
    public int getIndent() {
        String[] lines = getMdText().split("\n");
        return countSpace(lines[getWhichLine(lines)]);
    }

    /**
     * Returns the number of space at lineNum
     * @param lineNum - Line which you want to check indent
     * @return number of space
     */
    public int getIndentAtLine(int lineNum){
        String[] lines = getMdText().split("\n");

        return countSpace(lines[lineNum]);
    }

    public int getWhichLine(String[] lines) {
        int caret = getCaretPosition();
        int totalChars = 0;
        for (int i = 0; i < lines.length; i++) {
            totalChars += lines[i].length() + 1;
            if (totalChars > caret) {
                return i;
            }
        }
        return lines.length - 1;
    }

    private int countSpace(String line) {
        int cnt = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                cnt++;
            }
            else break;
        }
        return cnt;
    }
}
