package com.mdeditor.sd;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
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
                    if(CaretPosition==0)
                    {
                        CaretPosition=-1;
                    }
                    else if(CaretPosition==-1)
                    {
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
