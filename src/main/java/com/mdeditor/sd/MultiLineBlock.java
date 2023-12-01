package com.mdeditor.sd;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.regex.Matcher;
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
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    requestManager(BlockEvent.UPDATE_BLOCK);

                    String text = getMdText();
                    int caret = getCaretPosition();
                    if(caret < text.length() - 1){
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
                    requestManager(BlockEvent.OUTFOCUS_BLOCK_UP);
                }

                else if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    requestManager(BlockEvent.OUTFOCUS_BLOCK_DOWN);
                }
            }
        });
    }

    private static String getLastLine(String input) {
        // Split the string into lines
        String[] lines = input.split("\\n");

        // Use Stream API with lambda to get the last line
        return Arrays.stream(lines)
                .reduce((first, second) -> second)
                .orElse("");
    }

    private String getNewLine(){
        return " ".repeat(Math.max(0, getIndent_level() * 2)) +
                prefix + " ";
    }
}

