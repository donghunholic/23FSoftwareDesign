package com.mdeditor.sd.block.multi;

import com.mdeditor.sd.block.Block;
import com.mdeditor.sd.manager.BlockManager;
import com.mdeditor.sd.utils.Utils;
import com.mdeditor.sd.manager.BlockEvent;

import javax.swing.text.Element;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @see MultiLine
 */
public class MultiLineBlock extends Block {
    /**
     * quote: >
     * checkbox: []
     * list: -
     * code block:
     * When 'enter' key input event, automatically add prefix to the new line
     */
    MultiLine type;

    public String prefix;

    /**
     * Must append specific keyboard or mouse listener in this constructor.
     * @param manager : manager to call from key or mouse listener
     */
    public MultiLineBlock(BlockManager manager, String pre){
        super(manager);
        prefix = pre;
        this.addKeyListener(new KeyListener() {
            /*
            All of our key listen logic is contained within keyReleased().
            When obtaining the cursor position with getCaretPosition(),
            the cursor position is retrieved after release.
            Therefore, when the up arrow key is pressed while the cursor is on the second line and released,
            the cursor is on the first line. Therefore, the OUTFOCUS_BLOCK_UP event is called.

            To prevent this, use previousCaretPosition to perform logic based on the caret position when pressing keyPressed().
             */
            private int previousCaretPosition = 0;

            @Override
            public void keyTyped(KeyEvent e) {
                // nothing to do here, so left it empty
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(getCaretPosition()==1)
                {
                    if(e.getKeyCode() == KeyEvent.VK_LEFT){
                        caretPosition=-1;
                    }
                }
                else if(caretPosition==-1)
                {
                    caretPosition=-1;
                }
                else
                {
                    caretPosition=getCaretPosition();
                }

                if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB){
                    e.consume();
                }
                previousCaretPosition = getCaretPosition();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    requestManager(BlockEvent.UPDATE_BLOCK, getCaretPosition());
                    if(!Utils.isBlockStringMultiline(getBlock()) && !getMdText().contains("\n")){
                        requestManager(BlockEvent.NEW_BLOCK, getCaretPosition());
                        requestManager(BlockEvent.TRANSFORM_SINGLE, getCaretPosition());
                        return;
                    }


                    String text = getMdText();
                    int caret = getCaretPosition();
                    if(caret > text.length()){
                        caret = text.length();
                    }

                    if(caret < text.length()){
                        String insertStr = getNewLine();
                        getBlock().setText(text.substring(0,caret) + insertStr + text.substring(caret));
                        setCaretPosition(caret + insertStr.length());
                    }
                    else{
                        String[] lines = getMdText().split("\n");
                        String curLine = lines[getWhichLine(lines)];
                        String pref = Utils.getPrefix(getBlock(), getWhichLine(lines));
                        Pattern regex = Pattern.compile("^[ ]*" + Pattern.quote(pref) + "?[ ]*$");
                        if(regex.matcher(curLine).matches()){
                            getBlock().setText(String.join("\n", Arrays.copyOfRange(lines, 0, lines.length - 1)));
                            requestManager(BlockEvent.NEW_BLOCK, 0);
                        }
                        else{
                            String insertStr = getNewLine();
                            getBlock().setText(text + insertStr);
                            setCaretPosition(caret + insertStr.length());
                        }
                    }

                }

                else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    if(caretPosition==0)
                    {
                        caretPosition=-1;
                    }
                    else if(caretPosition==-1)
                    {
                        requestManager(BlockEvent.DELETE_BLOCK, -1);
                    }
                }

                else if(e.getKeyCode() == KeyEvent.VK_UP){
                    if(isCaretInFirstLine(previousCaretPosition)) {
                        requestManager(BlockEvent.OUTFOCUS_BLOCK_UP, getCaretPosition());
                    }
                }

                else if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    if(isCaretInLastLine(previousCaretPosition)){
                        requestManager(BlockEvent.OUTFOCUS_BLOCK_DOWN,
                                getCaretPosition() - Math.max(0, getMdText().lastIndexOf('\n')));
                    }
                }

                else if (e.getKeyCode() == KeyEvent.VK_TAB){
                    String[] lines = getText().split("\n");
                    int caret = getCaretPosition();
                    int lineNum = getWhichLine(lines);
                    if(lineNum == 0) return;
                    StringBuilder newText = new StringBuilder();
                    for(int i = 0; i < lines.length; i++){
                        if(i == lineNum){
                            newText.append("  ");
                        }
                        newText.append(lines[i]).append("\n");
                    }
                    getBlock().setMdText(newText.toString());
                    getBlock().setText(getMdText());
                    setCaretPosition(caret + 2);
                }
            }
        });
    }

    /**
     * Generates a new formatted line for Markdown text.
     * @return the prefix of the line where the cursor is located.
     */
    private String getNewLine(){
        String[] lines = getMdText().split("\n");
        int indent = getIndent();
        int cur = getWhichLine(lines);
        String pref = Utils.getPrefix(this, cur);
        if(pref.endsWith(".")){
            pref = String.valueOf(Integer.parseInt(pref.substring(0, pref.length() - 1)) + 1) + ".";
        }
        String ret = "\n" + " ".repeat(indent) + pref;
        if(!pref.isEmpty()){
            ret += " ";
        }
        return ret;
    }

    /**
     * Set type of this MultiLineBlock.
     * @see MultiLine
     */
    public void setType(MultiLine type) {
        this.type = type;
    }

    /**
     * Get type of this MultiLineBlock
     * @see MultiLine
     */
    public MultiLine getType() {
        return type;
    }

    /**
     * Determines whether the cursor is positioned on the first line.
     * @param caretPosition : cursor position in JTextPane
     * @return true if cursor is in first line, otherwise false.
     */
    private boolean isCaretInFirstLine(int caretPosition){
        Element root = this.getDocument().getDefaultRootElement();
        int line = root.getElementIndex(caretPosition);

        return line == 0;
    }

    /**
     * Determines whether the cursor is positioned on the last line.
     * @param caretPosition : cursor position in JTextPane
     * @return true if cursor is in last line, otherwise false.
     */
    public boolean isCaretInLastLine(int caretPosition) {
        Element root = this.getDocument().getDefaultRootElement();
        int line = root.getElementIndex(caretPosition);
        int lastLine = root.getElementCount() - 1;

        return line == lastLine;
    }
}