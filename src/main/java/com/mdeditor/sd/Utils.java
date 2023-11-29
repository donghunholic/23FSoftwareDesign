package com.mdeditor.sd;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Utils {
    private static final String style;
    private static final Parser flexmarkParser;
    private static final HtmlRenderer flexHtmlRenderer;

    static{
        style = readCss();
        MutableDataSet flexmarkOptions = new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(
                        StrikethroughExtension.create(),
                        TablesExtension.create()
                ))
                .set(TablesExtension.MIN_SEPARATOR_DASHES, 1)
                .set(StrikethroughExtension.STRIKETHROUGH_STYLE_HTML_OPEN, "<s>")
                .set(StrikethroughExtension.STRIKETHROUGH_STYLE_HTML_CLOSE, "</s>");
        flexmarkParser = Parser.builder(flexmarkOptions).build();
        flexHtmlRenderer = HtmlRenderer.builder(flexmarkOptions).build();
    }

    public static String readCss(){
        InputStream cssStream = Utils.class.getClassLoader().getResourceAsStream("editor/github-markdown-light.css");
        if(cssStream == null) return "";

        try{
            String cssContent = new String(cssStream.readAllBytes());
            return Utils.wrapWithHtmlTag("style", cssContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String wrapWithHtmlTag(String tag, String content){
        return "<" + tag + ">" + content + "</" + tag + ">";
    }

    public static String stringToHtml(String str){
        Node parseResult = flexmarkParser.parse(str);

        // append 'markdown-body' class into html body
        String htmlOutput = flexHtmlRenderer.render(parseResult);
        Document doc = Jsoup.parse(htmlOutput);
        doc.body().addClass("markdown-body");

        return doc.html();
    }

    public static String stringToHtmlWithCss(String string){
        return style + stringToHtml(string);
    }

    public static Node flexmarkParse(String input){
        return flexmarkParser.parse(input);
    }

    public static String flexmarkHtmlRender(Node node){
        return flexHtmlRenderer.render(node);
    }
}

