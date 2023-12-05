package com.mdeditor.sd;

import javax.swing.text.Element;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MultiLineBlock extends Block {
    /**
     * quote: >
     * checkbox: []
     * list: -
     * code block:
     * When 'enter' key input event, automatically add prefix to the new line
     */
    public String prefix;

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

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(getCaretPosition()==1)
                {
                    if(e.getKeyCode() == KeyEvent.VK_LEFT){
                        CaretPosition=-1;
                    }
                }
                else if(CaretPosition==-1)
                {
                    CaretPosition=-1;
                }
                else
                {
                    CaretPosition=getCaretPosition();
                }

                previousCaretPosition = getCaretPosition();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    requestManager(BlockEvent.UPDATE_BLOCK);

                    String text = getMdText();
                    int caret = getCaretPosition();
                    if(caret < text.length()){
                        String insertStr = getNewLine();
                        getBlock().setText(text.substring(0,caret) + insertStr + text.substring(caret));
                        setCaretPosition(caret + insertStr.length());
                    }
                    else{
                        String curLine = getLastLine(text);
                        String pattern = "^[ ]*" + Pattern.quote(prefix) + "[ \n]*$";
                        Pattern regex = Pattern.compile(pattern);
                        if(regex.matcher(curLine).matches()){
                            requestManager(BlockEvent.NEW_BLOCK);
                        }
                        else{
                            String insertStr = getNewLine();
                            getBlock().setText(text + insertStr);
                            setCaretPosition(caret + insertStr.length());
                        }
                    }

                }

                else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    if(CaretPosition==0)
                    {
                        CaretPosition=-1;
                    }
                    else if(CaretPosition==-1)
                    {
                        requestManager(BlockEvent.DELETE_BLOCK);
                    }
                }

                else if(e.getKeyCode() == KeyEvent.VK_UP){
                    if(isCaretInFirstLine(previousCaretPosition)) {
                        requestManager(BlockEvent.OUTFOCUS_BLOCK_UP);
                    }
                }

                else if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    if(isCaretInLastLine(previousCaretPosition)){
                        requestManager(BlockEvent.OUTFOCUS_BLOCK_DOWN);
                    }
                }
            }
        });
    }

    private static String getLastLine(String input) {
        String[] lines = input.split("\\n");

        return Arrays.stream(lines)
                .reduce((first, second) -> second)
                .orElse("");
    }

    private String getNewLine(){
        return " ".repeat(Math.max(0, getIndent_level() * 2)) +
                prefix + " ";
    }

    private boolean isCaretInFirstLine(int caretPosition){
        Element root = this.getDocument().getDefaultRootElement();
        int line = root.getElementIndex(caretPosition);

        return line == 0;
    }

    public boolean isCaretInLastLine(int caretPosition) {
        Element root = this.getDocument().getDefaultRootElement();
        int line = root.getElementIndex(caretPosition);
        int lastLine = root.getElementCount() - 1;

        return line == lastLine;
    }
}

