package com.mdeditor.sd;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if(!prefix.isEmpty() && getMdText().substring(getMdText().length()-prefix.length()).equals(prefix)){
                        requestManager(BlockEvent.NEW_BLOCK);
                    }
                    else {
                        getBlock().setText(getMdText() + "\n" + prefix);
                    }
                }

                else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && getCaretPosition() == 0){
                    requestManager(BlockEvent.DELETE_BLOCK);
                }

                else if(e.getKeyCode() == KeyEvent.VK_UP){
                    if(getCaretPosition() != 0) setCaretPosition(0);
                    else requestManager(BlockEvent.OUTFOCUS_BLOCK_UP);
                }

                else if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    if(getCaretPosition() != getMdText().length()) setCaretPosition(getMdText().length());
                    else requestManager(BlockEvent.OUTFOCUS_BLOCK_DOWN);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) { }

            @Override
            public void keyReleased(KeyEvent e) { }
        });
    }
}

