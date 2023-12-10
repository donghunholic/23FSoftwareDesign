import com.mdeditor.sd.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

public class SingleLineBlockTest {
    SingleLineBlock block;
    BlockManager manager = mock(BlockManager.class);
    KeyListener[] listeners;
    KeyListener l;
    KeyEvent eE, eB, eU, eD, eL, eR;

    @BeforeEach
    void clearBlock() {
        block = new SingleLineBlock(manager);
        listeners = block.getKeyListeners();
        l = listeners[listeners.length - 1];
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
    void testKeyEventHandlerKeyCombined() {
        block.setCaretPosition(0);
        assertDoesNotThrow(() -> {
            l.keyReleased(eB);
            l.keyPressed(eB);
            l.keyReleased(eB);
            verify(manager).update(block, BlockEvent.DELETE_BLOCK, -1);
        });
    }
}
