import com.mdeditor.sd.Block;
import com.mdeditor.sd.BlockEvent;
import com.mdeditor.sd.BlockManager;
import com.mdeditor.sd.Utils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

public class BlockTest {
    @Mock
    BlockManager manager;
    Block block;

    @BeforeEach
    void clearBlock() {
        block = new Block(manager);
    }

    @Test
    void testBlockMdTextGetterSetter() {
        String content = "# Head1";
        block.setMdText(content);
        assertEquals(block.getMdText(), content);
    }

    @Test
    void testBlockRenderHTML() {
        String content = "# Head1";
        block.setMdText(content);
        block.renderHTML();
        assertEquals(block.getContentType(), "text/html");
        assertEquals(block.getMdText(), content);
        // check css in head
        assertFalse(Jsoup.parse(block.getText()).head().select("style").isEmpty());
        // check body
        String test = Jsoup.parse(block.getText()).select("body > *").get(0).outerHtml();
        assertEquals(test, "<h1> Head1 </h1>");
    }

    @Test
    void testRenderMD() {
        String content = "# Head1";
        block.setMdText(content);
        block.setContentType("text/html");
        block.renderMD();
        assertEquals(block.getContentType(), "text/plain");
        assertEquals(block.getMdText(), content);
        assertEquals(block.getText(), content);
    }

    @Test
    void testGetBlock() {
        assertEquals(block.getBlock(), block);
    }

    @Test
    void testGetManager() {
        assertEquals(block.getManager(), manager);
    }

    @Test
    void testRequestFocusInWindow() {
        assertFalse(block.requestFocusInWindow());
    }

    @Test
    void testDestruct() {
        String content = "# Head1";
        block.setMdText(content);
        block.destruct();
        assertNull(block.getMdText());
        assertNull(block.getManager());
    }

    @Test
    void testIndent() {
        block.setIndent_level(10);
        assertEquals(block.getIndent_level(), 10);
    }
}
