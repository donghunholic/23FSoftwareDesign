package com.mdeditor.sd;

import com.mdeditor.sd.editor.MarkdownEditor;
import com.vladsch.flexmark.util.ast.Node;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class BlockManager {
    private List<Block> blockList;
    private final MarkdownEditor mdEditor;
    private Block blockOnFocus;

    public BlockManager(MarkdownEditor mdE) {
        this.blockList = new LinkedList<>();
        this.mdEditor = mdE;
    }

    /**
     * Handle Focus event
     * Block is created or deleted, request update to MarkdownEditor
     */
    public void update(Block block, BlockEvent e, int pos) {
        int idx = blockList.indexOf(block);
        blockOnFocus.setMdText(blockOnFocus.getText().strip());

        int caretPos = pos;

        switch (e) {
            case UPDATE_BLOCK -> { return; }

            case NEW_BLOCK -> {
                String str = block.getMdText().substring(block.getCaretPosition()-1);
                block.setMdText(block.getMdText().substring(0,block.getCaretPosition()-1));
                block.renderHTML();
                SingleLineBlock newBlock = new SingleLineBlock(this);
                newBlock.setMdText(str);
                blockList.add(idx+1, newBlock);
                this.blockOnFocus = newBlock;
            }
            case DELETE_BLOCK -> {
                if(idx > 0){
                    Block newFocusBlock = blockList.get(idx-1);
                    caretPos = newFocusBlock.getMdText().length();
                    newFocusBlock.setMdText(newFocusBlock.getMdText() + block.getMdText());
                    blockList.remove(block);
                    block.destruct();
                    this.blockOnFocus = newFocusBlock;
                }
            }
            case OUTFOCUS_BLOCK_UP -> {
                if(idx > 0){
                    this.blockOnFocus = blockList.get(idx-1);
                    manageBlock(idx);
                    blockList.get(idx).renderHTML();
                    caretPos = Math.max(0, blockOnFocus.getMdText().lastIndexOf('\n')) + pos;
                }
                else return;
            }
            case OUTFOCUS_BLOCK_DOWN -> {
                if(idx < blockList.size()-1){
                    this.blockOnFocus = blockList.get(idx+1);
                    manageBlock(idx);
                    blockList.get(idx).renderHTML();
                }
                else return;
            }
            case OUTFOCUS_CLICKED ->{
                if(blockOnFocus != blockList.get(idx)){
                    //blockOnFocus = blockList.get(idx);
                    manageBlock(idx);
                    blockOnFocus.renderHTML();
                    blockOnFocus = blockList.get(idx);
                    blockOnFocus.setContentType("text/html");
                }
            }
            case TRANSFORM_MULTI -> {
                String temp = block.getMdText();

                /* Caution! : prefix parameter must be implemented */
                blockList.add(idx, new MultiLineBlock(this, new String()));
                blockList.remove(block);
                block.destruct();
                blockList.get(idx).setMdText(temp);
                blockList.get(idx).requestFocusInWindow();
            }
            case TRANSFORM_SINGLE -> {
                //implement multi block to single block
            }
            default -> { throw new IllegalStateException("Unexpected value: " + e); }
        }
        renderAll(caretPos);
    }

    public List<Block> getBlockList(){
        return this.blockList;
    }

    /**
     * Extract mdText sequentially from every block.
     * @return Full Markdown text which will be saved into the (virtual) file.
     */
    public String extractFullMd(){
        StringBuilder fullMd = new StringBuilder();
        for(Block block : blockList){
            if(block == blockOnFocus){
                fullMd.append(block.getText());
            }
            else{
                fullMd.append(block.getMdText());
            }

            fullMd.append("\n\n");
        }
        return fullMd.toString();
    }

    /**
     * parse the block which is at BlockList[idx]
     * @param idx - the integer of Block's index. Must have value between 0 ~ BlockList.length()
     */
    public void BlockParse(int idx){
        int nl_idx = 0;
        Block temp = this.blockList.get(idx);
        String str = temp.getMdText();
        String prefix = "";
        String newSingleStr = "";
        String newMultiStr = "";
        String sliced = "";
        int prefix_len = 0;
        boolean is_last_line = false;
        if(Utils.getPrefixAtLine(temp, 0) != 0){
            prefix_len = Utils.getPrefixAtLine(temp, 0);
            prefix = str.substring(temp.getIndentAtLine(0), temp.getIndentAtLine(0) + prefix_len);
            boolean isPrefixOl = Utils.isOL(prefix);
            if(temp instanceof SingleLineBlock){
                blockList.remove(temp);
                temp = new MultiLineBlock(this, prefix);
                temp.setMdText(str);
                blockList.add(idx, temp);
            }

            while(str.indexOf("\n", nl_idx) != -1 || is_last_line){
                if(is_last_line){
                    break;
                }
                sliced = str.substring(nl_idx, nl_idx + prefix_len);
                if((!sliced.equals(prefix) && !isPrefixOl) || (isPrefixOl && !Utils.isOL(sliced))){
                    newSingleStr = str.substring(nl_idx);
                    if(newSingleStr.endsWith("\n")){
                        newSingleStr = newSingleStr.substring(0, newSingleStr.length() - 1);
                    }
                    newMultiStr = str.substring(0,nl_idx);
                    if(newMultiStr.endsWith("\n")){
                        newMultiStr = newMultiStr.substring(0, newMultiStr.length() - 1);
                    }
                    MultiLineBlock curBlock = new MultiLineBlock(this, prefix);
                    SingleLineBlock newBlock = new SingleLineBlock(this);
                    newBlock.setMdText(newSingleStr);
                    curBlock.setMdText(newMultiStr);
                    blockList.remove(temp);
                    blockList.add(idx, curBlock);
                    blockList.add(idx + 1,newBlock);
                    break;
                }
                nl_idx = str.indexOf("\n", nl_idx) + 1;
                if(str.indexOf("\n", nl_idx + 1) == -1){
                    is_last_line = true;
                }
            }
        }
        else{
            if(str.indexOf("\n", nl_idx) == -1){
                is_last_line = true;
            }
            if(!is_last_line){
                nl_idx = str.indexOf("\n", nl_idx);
                newSingleStr = str.substring(0, nl_idx + 1);
                if(newSingleStr.endsWith("\n")){
                    newSingleStr = newSingleStr.substring(0, newSingleStr.length() - 1);
                }
                SingleLineBlock newBlock = new SingleLineBlock(this);
                newBlock.setMdText(newSingleStr);
                blockList.add(idx,newBlock);
                temp.setMdText(str.substring(nl_idx+1));
            }
        }
        if(!is_last_line && idx + 1 < blockList.size()){
            BlockParse(idx+1);
        }
    }

    public void setBlocks(String markdownString){
        blockList.clear();
        blockList = parseStringIntoBlocks(markdownString);

        if(blockList.isEmpty()){
            blockList.add(new SingleLineBlock(this));
        }

        for(Block block : blockList){
            block.renderHTML();
        }

        blockOnFocus = blockList.get(0);
        blockOnFocus.renderMD();

        mdEditor.updateUI();
        SwingUtilities.invokeLater(()->{
            blockOnFocus.requestFocusInWindow();
            blockOnFocus.setCaretPosition(0);
        });

    }

    public void renderAll(int caretPos){
        String ContentType=blockOnFocus.getContentType();
        for(Block block : blockList){
            if(block != blockOnFocus && !block.getContentType().equals("text/html")){
                block.renderHTML();
            }
        }

        mdEditor.updateUI();

        int pos = (caretPos == -1 || caretPos > blockOnFocus.getMdText().length()) ?
                blockOnFocus.getMdText().length() : Math.max(0, caretPos);
        System.out.println("caretPos");
        System.out.println(caretPos);
        System.out.println("pos");
        System.out.println(pos);
        SwingUtilities.invokeLater(()->{
            blockOnFocus.requestFocusInWindow();
            if(ContentType.equals("text/html"))
            {
                blockOnFocus.setCaretPosition(blockOnFocus.getCaretPosition(pos));
            }
            else
            {
                blockOnFocus.setCaretPosition(pos);
            }
        });
    }

    /**/
    public Pair<Integer, Integer> getTableIndexFromMarkdownString(String markdownString){
        List<Block> blocks = parseStringIntoBlocks(markdownString);
        for(Block block : blocks){
            if(block instanceof MultiLineBlock && ((MultiLineBlock) block).getType() == MultiLine.TABLE){
                Integer startIndex = markdownString.indexOf(block.getMdText());
                Integer endIndex = startIndex + block.getMdText().length() - 1;
                return Pair.of(startIndex, endIndex);
            }
        }
        return Pair.of(-1, -1);
    }


    /**
     * @param markdownString : markdown string to parse into blocks.
     * @return list of Block, which contains only mdText.
     */
    public List<Block> parseStringIntoBlocks(String markdownString){
        List<Block> blocks = new LinkedList<>();
        for(Node child : Utils.flexmarkParse(markdownString).getChildren()){
            Document doc = Jsoup.parse(Utils.flexmarkHtmlRender(child));
            String tagName = doc.select("body > *").get(0).tagName();

            Block block;
            if(MultiLine.isMultiLine(tagName)){
                block = new MultiLineBlock(this, "");
                ((MultiLineBlock) block).setType(MultiLine.fromString(tagName));
            }
            else{
                block = new SingleLineBlock(this);
            }

            String markdownText = child.getChars().toString().trim();
            block.setMdText(markdownText);

            blocks.add(block);
        }

        return blocks;
    }

    public void mergeBlock(int idx){
        int cur_idx = idx;
        Block cur, up, down;
        if(idx > 0){
            cur = blockList.get(cur_idx);
            up = blockList.get(cur_idx - 1);
            if(cur instanceof MultiLineBlock && up instanceof MultiLineBlock){
                if(Objects.equals(((MultiLineBlock) cur).prefix, ((MultiLineBlock) up).prefix) || (Utils.isOL(((MultiLineBlock) cur).prefix)) && Utils.isOL(((MultiLineBlock) up).prefix)){
                    up.setMdText(up.getMdText() + "\n" + cur.getMdText());
                    if(blockOnFocus == cur){
                        blockOnFocus = blockList.get(cur_idx + 1);
                        SwingUtilities.invokeLater(()->{
                            blockOnFocus.requestFocusInWindow();
                        });
                    }
                    blockList.remove(cur);
                    cur_idx--;
                    mergeBlock(cur_idx);
                }
            }
        }
        if(idx < blockList.size() - 1){
            cur = blockList.get(cur_idx);
            down = blockList.get(cur_idx + 1);
            if(cur instanceof MultiLineBlock && down instanceof MultiLineBlock){
                if(Objects.equals(((MultiLineBlock) cur).prefix, ((MultiLineBlock) down).prefix) || (Utils.isOL(((MultiLineBlock) cur).prefix)) && Utils.isOL(((MultiLineBlock) down).prefix)){
                    cur.setMdText(cur.getMdText() + "\n" + down.getMdText());
                    if(blockOnFocus == down){
                        blockOnFocus = cur;
                        SwingUtilities.invokeLater(()->{
                            blockOnFocus.requestFocusInWindow();
                        });
                    }
                    blockList.remove(down);
                    mergeBlock(cur_idx);
                }
            }
        }
        blockList.get(cur_idx).renderMD();
        blockList.get(cur_idx).renderHTML();
    }

    public void manageBlock(int idx){
        BlockParse(idx);
        mergeBlock(idx);
    }
}
