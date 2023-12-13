package com.mdeditor.sd.block.multi;

/**
 * Type of multi line contains in this block.
 */
public enum MultiLine {
    UL("ul"),
    OL("ol"),
    BLOCK_QUOTE("blockquote"),
    CODE_BLOCK("pre"),
    TABLE("table"),
    NONE("")

    ;

    private final String tag;

    MultiLine(String tag){
        this.tag = tag;
    }

    /**
     * return tag of
     */
    public String getTag() {
        return tag;
    }

    /**
     *
     * @param input :
     * @return
     */
    public static Boolean isMultiLine(String input){
        return fromString(input) != NONE;
    }

    public static MultiLine fromString(String tag) {
        for (MultiLine multiLine : MultiLine.values()) {
            if (multiLine.tag.equals(tag)) {
                return multiLine;
            }
        }
        return NONE;
    }
}
