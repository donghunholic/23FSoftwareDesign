package com.mdeditor.sd;

import java.util.List;

public class BlockManager {
    static List<Block> blockList;

    /* add block to blockList */
    public boolean attach(Block block) {
        return blockList.add(block);
    }

    /* remove block to blockList */
    public boolean detach(Block block) {
        return blockList.remove(block);
    }



}
