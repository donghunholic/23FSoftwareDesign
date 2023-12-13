import com.mdeditor.sd.block.Block;
import com.mdeditor.sd.manager.BlockManager;
import com.mdeditor.sd.utils.Utils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import com.vladsch.flexmark.util.ast.Node;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {
    @Mock
    BlockManager manager;
    Block block;

    @BeforeEach
    void clearBlock() {
        block = new Block(manager);
    }

    @Test
    void testWrapWithHtmlTag() {
        String tag = "p";
        String content = "plain text";
        String expected = "<p>plain text</p>";

        assertEquals(expected, Utils.wrapWithHtmlTag(tag, content));
    }

    @ParameterizedTest(name = "testStringToHtml_{index}")
    @CsvSource({
            "# Head1, <h1>Head1</h1>",
            "## Head2, <h2>Head2</h2>",
            "### Head3, <h3>Head3</h3>",
            "#### Head4, <h4>Head4</h4>",
            "##### Head5, <h5>Head5</h5>",
            "###### Head6, <h6>Head6</h6>",
            "Markdown GitHub CSS, <p>Markdown GitHub CSS</p>",
            "**bold**, <p><strong>bold</strong></p>",
            "_italics_, <p><em>italics</em></p>",
            "~~strikethrough~~, <p><s>strikethrough</s></p>",
            "[link](csed332.postech.ac.kr), <p><a href=\"csed332.postech.ac.kr\">link</a></p>",
            "inline code block `int a = b;`, <p>inline code block <code>int a = b;</code></p>"
    })
    void testStringToHtmlSingleLine(String string, String expected) {
        String html = Utils.stringToHtml(string);
        String result = Jsoup.parse(html).select(" body > *").get(0).toString();
        assertEquals(expected, result);
    }

    @Test
    void testStringToHtmlSingleLineCheckBoxUnChecked() {
        String expected = """
<ul>\s
 <li class="task-list-item"><input type="checkbox" class="task-list-item-checkbox" disabled readonly>&nbsp;checkbox</li>\s
</ul>""";
        String html = Utils.stringToHtml("- [ ] checkbox");
        String result = Jsoup.parse(html).select(" body > *").get(0).toString();
        assertEquals(expected, result);
    }

    @Test
    void testStringToHtmlSingleLineCheckBoxChecked() {
        String expected = """
<ul>\s
 <li class="task-list-item"><input type="checkbox" class="task-list-item-checkbox" checked disabled readonly>&nbsp;checkbox</li>\s
</ul>""";
        String html = Utils.stringToHtml("- [x] checkbox");
        String result = Jsoup.parse(html).select(" body > *").get(0).toString();
        assertEquals(expected, result);
    }

    @Test
    void testStringToHtmlMultiLineList() {
        String expected = """
<ul>\s
 <li>Unordered List 1\s
  <ol>\s
   <li>ordered list</li>\s
   <li>ordered list</li>\s
   <li>ordered list</li>\s
  </ol> </li>\s
 <li>Unordered List 2</li>\s
</ul>""";
        String content = """
* Unordered List 1
    1. ordered list
    2. ordered list
    3. ordered list
* Unordered List 2
                """;

        String html = Utils.stringToHtml(content);
        String result = Jsoup.parse(html).select(" body > *").get(0).toString();
        assertEquals(expected, result);
    }

    @Test
    void testStringToHtmlMultiLineCodeBlock() {
        String expected = """
<pre><code class="language-java">void function(){
    System.out.println("hello world!");
}
</code></pre>""";
        String content = """
```java
void function(){
    System.out.println("hello world!");
}
```
                """;
        String html = Utils.stringToHtml(content);
        String result = Jsoup.parse(html).select(" body > *").get(0).toString();
        assertEquals(expected, result);
    }

    @Test
    void testStringToHtmlMultiLineQuote() {
        String expected = """
<blockquote>\s
 <p>quote 1 quote 2</p>\s
</blockquote>""";
        String content = """
> quote 1
> quote 2
                """;
        String html = Utils.stringToHtml(content);
        String result = Jsoup.parse(html).select(" body > *").get(0).toString();
        assertEquals(expected, result);
    }

    @Test
    void testStringToHtmlWithCss() {
        String mdText = "# head1";
        assertNotEquals(0, Jsoup.parse(Utils.stringToHtmlWithCss(mdText)).head().select("style").size());
    }

    @Test
    void testFlexmarkParse(){
        assertEquals("input", Utils.flexmarkParse("input").getChars().toString());
    }

    @Test
    void testFlexmarkHtmlRender(){
        Node node = Utils.flexmarkParse("input");
        assertEquals("<p>input</p>", Utils.flexmarkHtmlRender(node).trim());
    }

    @ParameterizedTest(name = "testGetPrefix_{index}")
    @CsvSource({
            "> quote, 0, >",
            "- UOL, 0, -",
            "+ UOL, 0, +",
            "* UOL, 0, *",
            "### Head, 0, ",
            "12. IOL, 0, 12.",
            "1. IOL, 0, 1.",
            "321. IOL, 0, 321.",
            "a. AOL, 0,"
    })
    void testGetPrefix(String mdText, int line, String result) {
        block.setMdText(mdText);
        block.renderMD();
        result = (result == null) ? "" : result;
        assertEquals(Utils.getPrefix(block, line), result);
    }

    @ParameterizedTest(name = "testIsOL_{index}")
    @CsvSource({
            ">, false",
            "-, false",
            "###, false",
            "12., true",
            "1., true",
            "321., true"
    })
    void testIsOL(String prefix, boolean result) {
        assertEquals(result, Utils.isOL(prefix));
    }

    @ParameterizedTest(name = "testIsBlockStringMultiline_{index}")
    @CsvSource({
            "> quote, true",
            "- UOL, true",
            "+ UOL, true",
            "* UOL, true",
            "### Head, false",
            "12. IOL, true",
            "1. IOL, true",
            "321. IOL, true",
            "a. AOL, false"
    })
    void testIsBlockStringMultiline(String mdText, boolean result) {
        block.setMdText(mdText);
        block.renderMD();
        assertEquals(Utils.isBlockStringMultiline(block), result);
    }
}