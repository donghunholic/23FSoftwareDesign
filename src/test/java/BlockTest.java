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

    @Test
    void testBlockRenderHTML() {
        String content = "# Head1";
        block.setText(content);
        block.renderHTML();
        // assertEquals(block.getContentType(), "text/html");
        assertEquals(block.getMdText(), content);
        assertEquals(block.getText(), Utils.stringToHtml(content));
    }

    @Test
    void testRenderMD() {
        String content = "# Head1";
        block.setMdText(content);
        block.renderMD();
        // assertEquals(block.getContentType(), "text/plain");
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
}
