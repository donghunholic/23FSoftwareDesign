import com.mdeditor.sd.block.multi.MultiLine;
import com.mdeditor.sd.block.multi.MultiLineBlock;
import com.mdeditor.sd.manager.BlockEvent;
import com.mdeditor.sd.manager.BlockManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MultiLineBlockTest {
    MultiLineBlock block;
    BlockManager manager = mock(BlockManager.class);
    KeyListener[] listeners;
    KeyListener l;
    KeyEvent eT, eE, eB, eU, eD, eL, eR;

    @BeforeEach
    void clearBlock() {
        block = new MultiLineBlock(manager, "");
        listeners = block.getKeyListeners();
        l = listeners[listeners.length - 1];
        eT = new KeyEvent(block, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_TAB, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
        eE = new KeyEvent(block, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
        eB = new KeyEvent(block, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_BACK_SPACE, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
        eU = new KeyEvent(block, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
        eD = new KeyEvent(block, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
        eL = new KeyEvent(block, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
        eR = new KeyEvent(block, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
    }

    @Test
    void testKeyEventHandlerKeyTyped() {
        KeyListener[] listeners = block.getKeyListeners();
        KeyListener l = listeners[listeners.length - 1];
        assertDoesNotThrow(() -> l.keyTyped(new KeyEvent(block, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, 'a', KeyEvent.KEY_LOCATION_UNKNOWN)));
    }

    @Test
    void testKeyEventHandlerKeyPressedValidCaret() {
        block.setMdText("##");
        block.renderMD();
        block.setCaretPosition(1);
        assertDoesNotThrow(() -> {
            l.keyPressed(eL);
            l.keyPressed(eR);
            l.keyPressed(eR);
            l.keyPressed(eR);
            l.keyPressed(eL);
            l.keyPressed(eL);
            l.keyPressed(eL);
            l.keyPressed(eL);
        });
    }

    @Test
    void testKeyEventHandlerKeyPressedInvalidCaret() {
        block.setMdText("##");
        assertDoesNotThrow(() -> {
            l.keyPressed(eL);
            l.keyPressed(eR);
            l.keyPressed(eR);
            l.keyPressed(eR);
            l.keyPressed(eL);
            l.keyPressed(eL);
            l.keyPressed(eL);
            l.keyPressed(eL);
        });
    }

    @Test
    void testKeyEventHandlerKeyReleasedEnter() {
        assertDoesNotThrow(() -> {
            l.keyReleased(eE);
            verify(manager).update(block, BlockEvent.NEW_BLOCK, 0);
        });
    }

    @Test
    void testKeyEventHandlerKeyReleasedBackSpace() {
        block.setCaretPosition(0);
        assertDoesNotThrow(() -> {
            l.keyReleased(eB);
            l.keyReleased(eB);
            verify(manager).update(block, BlockEvent.DELETE_BLOCK, -1);
        });
    }

    @Test
    void testKeyEventHandlerKeyReleasedUp() {
        assertDoesNotThrow(() -> {
            l.keyReleased(eU);
            verify(manager).update(block, BlockEvent.OUTFOCUS_BLOCK_UP, block.getCaretPosition());
        });
    }

    @Test
    void testKeyEventHandlerKeyReleasedDown() {
        assertDoesNotThrow(() -> {
            l.keyReleased(eD);
            verify(manager).update(block, BlockEvent.OUTFOCUS_BLOCK_DOWN, block.getCaretPosition());
        });
    }

    @Test
    void testKeyEventHandlerKeyReleasedValidCaret() {
        block.setMdText("""
                > quote 1
                > quote 2
                > quote 3
                """);
        block.renderMD();
        block.setCaretPosition(3);
        assertDoesNotThrow(() -> {
            l.keyReleased(eE);
        });
    }

    @Test
    void testKeyEventHandlerKeyCombined() {
        block.setCaretPosition(0);
        assertDoesNotThrow(() -> {
            l.keyReleased(eB);
            l.keyPressed(eB);
            l.keyReleased(eB);
            verify(manager).update(block, BlockEvent.DELETE_BLOCK, -1);
        });
    }

    @Test
    void testKeyEventHandlerTab() {
        block.setMdText("""
                > quote 1
                > quote 2
                > quote 3
                """);
        block.renderMD();
        block.setCaretPosition(12);
        assertDoesNotThrow(() -> {
            l.keyReleased(eT);
        });
    }

    @Test
    void testKeyEventHandlerExtreme() {
        block.setMdText("""
                > quote 1
                > quote 2
                > quote 3
                """);
        block.renderMD();
        block.setCaretPosition(block.getMdText().length());
        assertDoesNotThrow(() -> {
            l.keyReleased(eE);
        });
    }

    @Test
    void testMultiLineBlockType() {
        block.setType(MultiLine.OL);
        assertEquals(MultiLine.OL, block.getType());
    }
}
