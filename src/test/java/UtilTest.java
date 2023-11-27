import com.mdeditor.sd.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {
    @Test
    void testWrapWithHtmlTag() {
        String tag = "p";
        String content = "plain text";
        String expected = "<p>plain text</p>";

        assertEquals(Utils.wrapWithHtmlTag(tag, content), expected);
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
            "[link](csed332.postech.ac.kr), <p><a href=\"csed332.postech.ac.kr\">link</a></p>",
            "inline code block `int a = b;`, <p>inline code block <code>int a = b;</code></p>"
    })
    void testStringToHtmlSingleLine(String string, String expected) {
        assertEquals(Utils.stringToHtml(string).trim(), expected);
    }

    @Test
    void testStringToHtmlMultiLineList() {
        String expected = """
<ul>
<li>Unordered List 1
<ol>
<li>ordered list</li>
<li>ordered list</li>
<li>ordered list</li>
</ol>
</li>
<li>Unordered List 2</li>
</ul>
              """;
        String content = """
* Unordered List 1
    1. ordered list
    2. ordered list
    3. ordered list
* Unordered List 2
                """;
        assertEquals(Utils.stringToHtml(content), expected);
    }

    @Test
    void testStringToHtmlMultiLineCodeBlock() {
        String expected = """
<pre><code class="language-java">void function(){
    System.out.println(&quot;hello world!&quot;);
}
</code></pre>
""";
        String content = """
```java
void function(){
    System.out.println("hello world!");
}
```
                """;
        assertEquals(Utils.stringToHtml(content), expected);
    }

    @Test
    void testStringToHtmlMultiLineQuote() {
        String expected = """
<blockquote>
<p>quote 1
quote 2</p>
</blockquote>
""";
        String content = """
> quote 1
> quote 2
                """;
        assertEquals(Utils.stringToHtml(content), expected);
    }
}