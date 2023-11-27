package com.mdeditor.sd;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import org.commonmark.*;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;

import java.util.Arrays;
import java.util.List;

public class Utils {
    public static String wrapWithHtmlTag(String tag, String content){
        return "<" + tag + ">" + content + "</" + tag + ">";
    }

    public static String stringToHtml(String str){
        List<Extension> extensions = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build();
        Node document = parser.parse(str);
        return renderer.render(document);
    }

    public int prefix_check(Block block){
        String temp = block.getMdText();
        int start = block.getIndent_level() * 2;
        int end = temp.indexOf(" ", start);
        if (end == -1) return 0;
        String prefix = temp.substring(start, end);
        if(prefix.equals(">") || prefix.equals("-")) return 1;
        else if (prefix.endsWith(".")){
            try {
                 Integer.parseInt(prefix.substring(0, prefix.length() - 1));
                 return prefix.length() - 1;
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        else return 0;
    }

    public boolean table_check(Block block){
        String temp = block.getMdText();
        if (temp.startsWith("|") && temp.endsWith("|")) {}

    }
}




