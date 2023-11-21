package com.mdeditor.sd;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SingleLineBlock extends Block {
    public SingleLineBlock(BlockManager manager){
        super(manager);

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    requestManager(BlockEvent.NEW_BLOCK);
                }

                else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && getCaretPosition() == 0){
                    requestManager(BlockEvent.DELETE_BLOCK);
                }

                else if(e.getKeyCode() == KeyEvent.VK_UP){
                    if(getCaretPosition() != 0) setCaretPosition(0);
                    else requestManager(BlockEvent.OUTFOCUS_BLOCK_UP);
                }

                else if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    if(getCaretPosition() != getCurText().length()) setCaretPosition(getCurText().length());
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
