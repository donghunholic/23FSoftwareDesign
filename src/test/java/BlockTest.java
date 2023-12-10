import com.mdeditor.sd.Block;
import com.mdeditor.sd.BlockEvent;
import com.mdeditor.sd.BlockManager;
import com.mdeditor.sd.Utils;
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

//    @Test
//    void testBlockRenderHTML() {
//        String content = "# Head1";
//        block.setMdText(content);
//        block.renderHTML();
//        // assertEquals(block.getContentType(), "text/html");
//        assertEquals(block.getMdText(), content);
//        assertEquals(block.getText(), Utils.stringToHtml(content));
//    }

    @Test
    void testRenderMD() {
        String content = "# Head1";
        block.setMdText(content);
        block.renderMD();
        // assertEquals(block.getContentType(), "text/plain");
        assertEquals(block.getMdText(), content);
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
    void testgetCaretPosition() {
        block.setMdText("- how\n- how\n  - ab cd A\n  - ab cd B");
        assertEquals(19,block.getCaretPosition(12));
        assertEquals(23,block.getCaretPosition(16));
        assertEquals(31,block.getCaretPosition(20));
        assertEquals(34,block.getCaretPosition(23));
        assertEquals(35,block.getCaretPosition(24));
    }
}
