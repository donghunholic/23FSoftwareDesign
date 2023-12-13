package com.mdeditor.sd.block.single;

import com.mdeditor.sd.block.Block;
import com.mdeditor.sd.manager.BlockManager;
import com.mdeditor.sd.utils.Utils;
import com.mdeditor.sd.manager.BlockEvent;

import java.awt.event.*;

public class SingleLineBlock extends Block {
    public SingleLineBlock(BlockManager manager){
        super(manager);

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // nothing to do here, so left it empty
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(getCaretPosition()==1) {
                    if(e.getKeyCode() == KeyEvent.VK_LEFT){
                        caretPosition=-1;
                    }
                }
                else if(caretPosition==-1) {
                    caretPosition=-1;
                }
                else {
                    caretPosition=getCaretPosition();
                }
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if(Utils.isBlockStringMultiline(getBlock())){
                        requestManager(BlockEvent.TRANSFORM_MULTI, getCaretPosition());
                    }
                    else{
                        requestManager(BlockEvent.NEW_BLOCK, 0);
                    }
                }

                else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    if(caretPosition==0) {
                        caretPosition=-1;
                    }
                    else if(caretPosition==-1) {
                        requestManager(BlockEvent.DELETE_BLOCK, -1);
                    }
                }

                else if(e.getKeyCode() == KeyEvent.VK_UP){
                    requestManager(BlockEvent.OUTFOCUS_BLOCK_UP, getCaretPosition());
                }

                else if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    requestManager(BlockEvent.OUTFOCUS_BLOCK_DOWN, getCaretPosition());
                }
            }

        });
    }

}
