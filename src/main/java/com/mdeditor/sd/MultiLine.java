package com.mdeditor.sd;

public enum MultiLine {
    UL("ul"),
    OL("ol"),
    BLOCK_QUOTE("blockquote"),
    CODE_BLOCK("pre"),
    TABLE("table"),

    ;

    private final String tag;

    MultiLine(String tag){
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public static Boolean isMultiLine(String input){
        for(MultiLine multiLine : values()){
            if(multiLine.getTag().equalsIgnoreCase(input)){
                return true;
            }
        }
        return false;
    }
}
