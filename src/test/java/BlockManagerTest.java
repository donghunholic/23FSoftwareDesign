import com.mdeditor.sd.Block;
import com.mdeditor.sd.BlockManager;
import com.mdeditor.sd.editor.MarkdownEditor;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BlockManagerTest {
    @Mock
    MarkdownEditor markdownEditor;
    BlockManager blockManager = new BlockManager(markdownEditor);

    @Test
    void testParseStringIntoBlocks() {
        // given
        String markdownString = """
# Heading 1
| column 1 | column 2 |
| -------- | -------- |
| data 1   | data 2   |
and one more line""";

        // when
        List<Block> blocks = blockManager.parseStringIntoBlocks(markdownString);

        // then
        assertNotNull(blocks);
        assertFalse(blocks.isEmpty());
        for (Block block : blocks) {
            assertNotNull(block);
            assertNotNull(block.getMdText());
        }
        assertEquals("# Heading 1", blocks.get(0).getMdText());
        assertEquals("""
| column 1 | column 2 |
| -------- | -------- |
| data 1   | data 2   |""", blocks.get(1).getMdText());
        assertEquals("and one more line", blocks.get(2).getMdText());
    }

    @Test
    void testGetTableIndexFromMarkdownString() {
        // markdown with table block
        String markdownWithTable = """
# Heading 1
| column 1 | column 2 |
| -------- | -------- |
| data 1   | data 2   |
and one more line""";
        Pair<Integer, Integer> result1 = blockManager.getTableIndexFromMarkdownString(markdownWithTable);
        assertNotNull(result1);
        assertEquals(12, result1.getLeft());  // Adjust the expected start index based on your actual markdown
        assertEquals(82, result1.getRight());  // Adjust the expected end index based on your actual markdown

        // markdown without table block
        String markdownWithoutTable = "Some text without a table.";
        Pair<Integer, Integer> result2 = blockManager.getTableIndexFromMarkdownString(markdownWithoutTable);
        assertNotNull(result2);
        assertEquals(-1, result2.getLeft());  // Expecting -1 as no TABLE block is present
        assertEquals(-1, result2.getRight()); // Expecting -1 as no TABLE block is present
    }
}
