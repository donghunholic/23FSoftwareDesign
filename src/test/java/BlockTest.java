import com.mdeditor.sd.Block;
import com.mdeditor.sd.BlockEvent;
import com.mdeditor.sd.BlockManager;
import com.mdeditor.sd.Utils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.text.Caret;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BlockTest {
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
    void testIndent() {
        block.setIndent_level(10);
        assertEquals(block.getIndent_level(), 10);
    }

    @Test
    void testMouseEventHandler() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> {
            block.dispatchEvent(new MouseEvent(block, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 0, true, MouseEvent.BUTTON1));
        });
        SwingUtilities.invokeAndWait(() -> {
            block.dispatchEvent(new MouseEvent(block, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 0, 0, 0, true, MouseEvent.BUTTON1));
        });
        SwingUtilities.invokeAndWait(() -> {
            block.dispatchEvent(new MouseEvent(block, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 0, 0, 0, true, MouseEvent.BUTTON1));
        });
        SwingUtilities.invokeAndWait(() -> {
            block.dispatchEvent(new MouseEvent(block, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, 0, 0, 0, true, MouseEvent.BUTTON1));
        });
        SwingUtilities.invokeAndWait(() -> {
            block.dispatchEvent(new MouseEvent(block, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, 0, 0, 0, true, MouseEvent.BUTTON1));
        });
        verify(manager, atMost(1)).update(block, BlockEvent.OUTFOCUS_CLICKED, block.getCaretPosition());
    }
}
