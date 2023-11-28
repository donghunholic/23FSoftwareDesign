package com.mdeditor.sd;

import java.awt.event.*;

public class SingleLineBlock extends Block {
    public SingleLineBlock(BlockManager manager){
        super(manager);

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    e.consume();
                    requestManager(BlockEvent.NEW_BLOCK);
                }

                else if(getCaretPosition() == 0 && e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    requestManager(BlockEvent.DELETE_BLOCK);
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

}
