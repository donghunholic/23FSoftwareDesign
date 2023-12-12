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
     * Handle blockEvent
     * At each event case,
     * 1) Change blockOnFocus and call ManageBlock
     * 2) blockList Management
     * 3) Handle Caret Position
     *
     * @param block block which called requestManager
     *              (Basically onFocus block, except OUTFOCUS_CLICKED -> clicked block)
     * @param e Type of BlockEvent
     * @param pos Primitive caret position
     */
    public void update(Block block, BlockEvent e, int pos) {
        int idx = blockList.indexOf(block);
        blockOnFocus.setMdText(blockOnFocus.getText().strip());

        int caretPos = pos;

        switch (e) {
            case UPDATE_BLOCK -> { return; }

            case NEW_BLOCK -> {
                SingleLineBlock newBlock = new SingleLineBlock(this);

                String mdText = block.getMdText();
                int caretPosition = Math.min(block.getCaretPosition(), mdText.length());

                block.setMdText(mdText.substring(0, caretPosition));
                newBlock.setMdText(mdText.substring(caretPosition));
                blockList.add(idx+1, newBlock);
                this.blockOnFocus = newBlock;
            }
            case DELETE_BLOCK -> {
                if(idx > 0){
                    Block newFocusBlock = blockList.get(idx-1);
                    caretPos = newFocusBlock.getMdText().length();
                    newFocusBlock.setMdText(newFocusBlock.getMdText() + blockOnFocus.getMdText());
                    this.blockOnFocus = newFocusBlock;

                    blockList.remove(block);
                    block.destruct();
                }
            }
            case OUTFOCUS_BLOCK_UP -> {
                if(idx > 0){
                    this.blockOnFocus = blockList.get(idx-1);
                    manageBlock(idx);
                    caretPos = Math.max(0, blockOnFocus.getMdText().lastIndexOf('\n')) + pos;
                }
                else return;
            }
            case OUTFOCUS_BLOCK_DOWN -> {
                if(idx < blockList.size()-1){
                    this.blockOnFocus = blockList.get(idx+1);
                    manageBlock(idx);
                    int nextLineLength = blockOnFocus.getMdText().indexOf('\n') == -1 ?
                            blockOnFocus.getMdText().length() : blockOnFocus.getMdText().indexOf('\n');
                    caretPos = Math.min(nextLineLength, pos);
                }
                else return;
            }
            case OUTFOCUS_CLICKED ->{
                if(blockOnFocus != blockList.get(idx)){
                    Block tmpFocus = blockList.get(idx);
                    manageBlock(blockList.indexOf(blockOnFocus));
                    blockOnFocus = tmpFocus;
                    blockOnFocus.setContentType("text/html");
                    caretPos = block.getCaretPosition(pos);
                }
            }
            case TRANSFORM_MULTI -> {
                Block newBlock = new MultiLineBlock(this, "");
                String pre = Utils.getPrefix(block, 0);
                newBlock.setMdText(block.getMdText() + "\n" + pre + " ");
                caretPos += pre.length() + 1;

                blockList.add(idx, newBlock);
                blockList.remove(block);
                block.destruct();

                blockOnFocus = newBlock;
                blockOnFocus.renderMD();
            }
            case TRANSFORM_SINGLE -> {
                Block newBlock = new SingleLineBlock(this);
                newBlock.setMdText(block.getMdText());

                blockList.add(idx, newBlock);
                blockList.remove(block);
                block.destruct();
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

        //TODO: Stack Overflow happens when there is indentation inside multilineblock
        if(!is_last_line && (idx < blockList.size() - 1)){
            BlockParse(idx+1);
        }
    }

    /**
     * Initial blockList setup when opening WYSIWYG editor
     * @param markdownString All text from original file
     */
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

    /**
     * For every block in blockList, call renderHTML if block is not focused,
     * blockOnFocus calls (overridden) requestFocusInWindow
     * 'pos' must not be illegal, because this is the final caret position
     * @param caretPos caret position under pretreatment
     */
    public void renderAll(int caretPos){
        for(Block block : blockList){
            if(block != blockOnFocus && !block.getContentType().equals("text/html")){
                block.renderHTML();
            }
        }

        mdEditor.updateUI();

        int pos = (caretPos == -1 || caretPos > blockOnFocus.getMdText().length()) ?
                blockOnFocus.getMdText().length() : Math.max(0, caretPos);
        SwingUtilities.invokeLater(()->{
            blockOnFocus.requestFocusInWindow();
            blockOnFocus.setCaretPosition(pos);
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

    /**
     * Merges block if block has the same type with surrounding blocks after text update
     * @param idx block number which we inspect now
     */
    public void mergeBlock(int idx){
        int cur_idx = idx;
        Block cur, up, down;
        if(idx > 0){
            cur = blockList.get(cur_idx);
            up = blockList.get(cur_idx - 1);
            if(cur instanceof MultiLineBlock && up instanceof MultiLineBlock){
                String curPre = Utils.getPrefix(cur, 0);
                String upPre = Utils.getPrefix(up, 0);
                if(curPre.equals(upPre) || (Utils.isOL(curPre) && Utils.isOL(upPre))){
                    up.setMdText(up.getMdText() + "\n" + cur.getMdText());
                    if(blockOnFocus == cur){
                        blockOnFocus = blockList.get(cur_idx + 1);
                        blockOnFocus.renderMD();
                    }
                    blockList.remove(cur);
                    cur.destruct();
                    cur_idx--;
                    mergeBlock(cur_idx);
                }
            }
        }
        if(idx < blockList.size() - 1){
            cur = blockList.get(cur_idx);
            down = blockList.get(cur_idx + 1);
            if(cur instanceof MultiLineBlock && down instanceof MultiLineBlock){
                String curPre = Utils.getPrefix(cur, 0);
                String downPre = Utils.getPrefix(down, 0);
                if(curPre.equals(downPre) || (Utils.isOL(curPre) && Utils.isOL(downPre))){
                    cur.setMdText(cur.getMdText() + "\n" + down.getMdText());
                    if(blockOnFocus == down){
                        blockOnFocus = cur;
                        blockOnFocus.renderMD();
                    }
                    blockList.remove(down);
                    down.destruct();
                    mergeBlock(cur_idx);
                }
            }
        }
        blockList.get(cur_idx).renderMD();
        blockList.get(cur_idx).renderHTML();
    }

    /**
     * call blockParse and mergeBlock
     * @param idx block index which will be focused out
     */
    public void manageBlock(int idx){
        BlockParse(idx);
        mergeBlock(idx);
    }
}
