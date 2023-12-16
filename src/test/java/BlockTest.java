import com.mdeditor.sd.block.Block;
import com.mdeditor.sd.manager.BlockEvent;
import com.mdeditor.sd.manager.BlockManager;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.swing.*;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlockTest {
    Block block;
    BlockManager manager = mock(BlockManager.class);

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
        assertEquals("text/html", block.getContentType());
        assertEquals(block.getMdText(), content);
        // check css in head
        assertFalse(Jsoup.parse(block.getText()).head().select("style").isEmpty());
        // check body
        String test = Jsoup.parse(block.getText()).select("body > *").get(0).outerHtml();
        assertEquals("<h1> Head1 </h1>", test);
    }

    @Test
    void testRenderMD() {
        String content = "# Head1";
        block.setMdText(content);
        block.setContentType("text/html");
        block.renderMD();
        assertEquals("text/plain", block.getContentType());
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
    void testRequestManager() {
        block.requestManager(BlockEvent.NEW_BLOCK, 0);
        verify(manager).update(block, BlockEvent.NEW_BLOCK, 0);
        block.requestManager(BlockEvent.UPDATE_BLOCK, 0);
        verify(manager).update(block, BlockEvent.UPDATE_BLOCK, 0);
        block.requestManager(BlockEvent.DELETE_BLOCK, 0);
        verify(manager).update(block, BlockEvent.DELETE_BLOCK, 0);
        block.requestManager(BlockEvent.OUTFOCUS_CLICKED, 0);
        verify(manager).update(block, BlockEvent.OUTFOCUS_CLICKED, 0);
        block.requestManager(BlockEvent.OUTFOCUS_BLOCK_UP, 0);
        verify(manager).update(block, BlockEvent.OUTFOCUS_BLOCK_UP, 0);
        block.requestManager(BlockEvent.OUTFOCUS_BLOCK_DOWN, 0);
        verify(manager).update(block, BlockEvent.OUTFOCUS_BLOCK_DOWN, 0);
        block.requestManager(BlockEvent.TRANSFORM_SINGLE, 0);
        verify(manager).update(block, BlockEvent.TRANSFORM_SINGLE, 0);
        block.requestManager(BlockEvent.TRANSFORM_MULTI, 0);
        verify(manager).update(block, BlockEvent.TRANSFORM_MULTI, 0);
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
    void testMouseEventHandler() {
        assertDoesNotThrow(() -> {
            SwingUtilities.invokeAndWait(() -> block.dispatchEvent(new MouseEvent(block, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 0, true, MouseEvent.BUTTON1)));
            SwingUtilities.invokeAndWait(() -> block.dispatchEvent(new MouseEvent(block, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 0, 0, 0, true, MouseEvent.BUTTON1)));
            SwingUtilities.invokeAndWait(() -> block.dispatchEvent(new MouseEvent(block, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 0, 0, 0, true, MouseEvent.BUTTON1)));
            SwingUtilities.invokeAndWait(() -> block.dispatchEvent(new MouseEvent(block, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, 0, 0, 0, true, MouseEvent.BUTTON1)));
            SwingUtilities.invokeAndWait(() -> block.dispatchEvent(new MouseEvent(block, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, 0, 0, 0, true, MouseEvent.BUTTON1)));
        });
        verify(manager, atMost(1)).update(block, BlockEvent.OUTFOCUS_CLICKED, block.getCaretPosition());
    }

    @Test
    void testGetCaretPosition() {
        block.setMdText("- how\n- how\n  - ab cd A\n  - ab cd B");
        assertEquals(19,block.getCaretPosition(12));
        assertEquals(23,block.getCaretPosition(16));
        assertEquals(31,block.getCaretPosition(20));
        assertEquals(34,block.getCaretPosition(23));
        assertEquals(35,block.getCaretPosition(24));
    }

    @ParameterizedTest(name = "testGerCaretPosition_{index}")
    @CsvSource({
            "## Head 2, 2, 4",
            "> Quote-quote, 8, 9",
            "- UOL - UOL, 3, 4",
            "* UOL * UOL * UOL, 15, 16",
            " + UOL + UOL, 10, 11",
            "1. OL 2. OL, 5, 7",
            "a. alphaOL, 2, 1"
    })
    void testGetCaretPositionParam(String md, int position, int answer) {
        block.setMdText(md);
        block.renderMD();
        assertEquals(block.getCaretPosition(position), answer);
    }

    @ParameterizedTest(name = "testGerCaretPosition_{index}")
    @CsvSource({
            "## Head 2, 2, 2",
            "> Quote-quote, 8, 8",
            "- UOL - UOL, 3, 12",
            "* UOL * UOL * UOL, 15, 22",
            " + UOL + UOL, 10, 19",
            "1. OL 2. OL, 5, 15",
            "a. alphaOL, 2, 1"
    })
    void testGetCaretPositionParamWithSpace(String md, int position, int answer) {
        md = "  ".repeat(md.length() % 7) + md;
        block.setMdText(md);
        block.renderMD();
        assertEquals(block.getCaretPosition(position), answer);
    }

    @Test
    void testGetCaretPositionBoundary() {
        block.setMdText(null);
        assertEquals(0, block.getCaretPosition(1));
        block.setMdText("");
        assertEquals(0, block.getCaretPosition(1));
        block.setMdText("# Head 1");
        assertEquals(0, block.getCaretPosition(-1));
        block.setMdText("# Head 1");
        assertEquals(0, block.getCaretPosition(100));
    }

    @Test
    void testGetIndent() {
        block.setMdText("  ".repeat(3) + "- indented uol");
        assertEquals(6, block.getIndent());
    }

    @Test
    void testGetIndentAtLine() {
        String md = """
       # Head with indent
           * uol with indent
         + something with indent
                """;
        block.setMdText(md);
        assertEquals(0, block.getIndentAtLine(0));
        assertEquals(4, block.getIndentAtLine(1));
        assertEquals(2, block.getIndentAtLine(2));
    }

    @Test
    void testGetWhichLine() {
        String md = """
       # Head with indent
           * uol with indent
         + something with indent
                """;
        block.setMdText(md);
        block.renderMD();
        block.setCaretPosition(20);
        assertEquals(1, block.getWhichLine(md.split("\n")));
    }

    @Test
    void testGetWhichLineExtreme() {
        String md = """
       # Head with indent
           * uol with indent
         + something with indent
                """;
        block.setMdText(md);
        block.renderMD();
        block.setCaretPosition(md.length());
        assertEquals(2, block.getWhichLine(md.split("\n")));
    }
}
