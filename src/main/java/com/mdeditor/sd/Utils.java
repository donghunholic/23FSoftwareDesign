package com.mdeditor.sd;

public class Utils {
    public static String wrapWithHtmlTag(String tag, String content){
        return "<" + tag + ">" + content + "</" + tag + ">";
    }
}
