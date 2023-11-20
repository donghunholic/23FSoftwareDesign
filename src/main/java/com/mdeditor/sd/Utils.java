package com.mdeditor.sd;
import org.commonmark.*;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
//import org.commonmark.ext.gfm.tables.TablesExtension;

import java.util.Arrays;
import java.util.List;

public class Utils {
    public static String wrapWithHtmlTag(String tag, String content){
        return "<" + tag + ">" + content + "</" + tag + ">";
    }

    public static String stringToHtml(String str){
        //List<Extension> extensions = Arrays.asList(TablesExtension.create());
//        Parser parser = Parser.builder()
//                .extensions(extensions)
//                .build();
        Parser parser = Parser.builder()
                .build();
//        HtmlRenderer renderer = HtmlRenderer.builder()
//                .extensions(extensions)
//                .build();
        HtmlRenderer renderer = HtmlRenderer.builder()
                .build();
        Node document = parser.parse(str);
        return renderer.render(document);
    }
}

