import com.mdeditor.sd.block.Block;
import com.mdeditor.sd.block.multi.MultiLineBlock;
import com.mdeditor.sd.block.single.SingleLineBlock;
import com.mdeditor.sd.manager.BlockEvent;
import com.mdeditor.sd.manager.BlockManager;
import com.mdeditor.sd.editor.MarkdownEditor;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BlockManagerTest {
    MarkdownEditor markdownEditor;
    BlockManager blockManager;

    @BeforeEach
    void setupBlockManagerTest() {
        markdownEditor = mock(MarkdownEditor.class);
        blockManager = new BlockManager(markdownEditor);
    }

    void setupUpdateTest() {
        String md = """
# Head 1
## Head 2
### Head 3

- UOL
- UOL

> Quote quot
                """;
        blockManager.setBlocks(md);
    }

    @Test
    void testUpdateUPDATE_BLOCK() {
        setupUpdateTest();
        assertDoesNotThrow(() -> blockManager.update(blockManager.getBlockList().get(2), BlockEvent.UPDATE_BLOCK, 10));
    }

    @Test
    void testUpdateNEW_BLOCK() {
        setupUpdateTest();
        int prev = blockManager.getBlockList().size();
        assertDoesNotThrow(() -> blockManager.update(blockManager.getBlockList().get(2), BlockEvent.NEW_BLOCK, 10));
        assertEquals(blockManager.getBlockList().size(), prev + 1);
    }

    @Test
    void testUpdateDELTE_BLOCK() {
        setupUpdateTest();
        int prev = blockManager.getBlockList().size();
        assertDoesNotThrow(() -> blockManager.update(blockManager.getBlockList().get(2), BlockEvent.DELETE_BLOCK, 10));
        assertEquals(blockManager.getBlockList().size(), prev - 1);
    }

    @Test
    void testUpdateOUTFOCUS_BLOCK_UP() {
        setupUpdateTest();
        assertDoesNotThrow(() -> blockManager.update(blockManager.getBlockList().get(2), BlockEvent.OUTFOCUS_BLOCK_UP, 10));
    }

    @Test
    void testUpdateOUTFOCUS_BLOCK_DOWN() {
        setupUpdateTest();
        assertDoesNotThrow(() -> blockManager.update(blockManager.getBlockList().get(2), BlockEvent.OUTFOCUS_BLOCK_DOWN, 10));
    }

    @Test
    void testUpdateOUTFOCUS_CLICKED() {
        setupUpdateTest();
        assertDoesNotThrow(() -> blockManager.update(blockManager.getBlockList().get(2), BlockEvent.OUTFOCUS_CLICKED, 10));
    }

    @Test
    void testUpdateTRANSFORM_MULTI() {
        setupUpdateTest();
        assertDoesNotThrow(() -> blockManager.update(blockManager.getBlockList().get(2), BlockEvent.TRANSFORM_MULTI, 10));
        assertTrue(blockManager.getBlockList().get(2) instanceof MultiLineBlock);
    }

    @Test
    void testUpdateTRANSFORM_SINGLE() {
        setupUpdateTest();
        assertDoesNotThrow(() -> blockManager.update(blockManager.getBlockList().get(2), BlockEvent.TRANSFORM_SINGLE, 10));
        assertTrue(blockManager.getBlockList().get(2) instanceof SingleLineBlock);
    }

    @Test
    void testBlockParse() {
        String md = """
# Head 1
## Head 2
### Head 3

- UOL
- UOL

> Quote quot
> Cuote cuot
                """;
        blockManager.setBlocks(md);
        assertDoesNotThrow(() -> blockManager.blockParse(blockManager.getBlockList().size() - 1));
        assertEquals(5, blockManager.getBlockList().size());
    }

    @Test
    void testBlockParseExtreme() {
        String md = """
# Head 1
## Head 2
### Head 3

- UOL
- UOL

> Quote quot
> Cuote cuot
                """;
        blockManager.setBlocks(md);
        Block b = blockManager.getBlockList().get(blockManager.getBlockList().size() - 1);
        b.setMdText(b.getMdText() + "\n# Head 1\n1. spy\n> maybe");
        assertDoesNotThrow(() -> blockManager.blockParse(blockManager.getBlockList().size() - 1));
        assertEquals(7, blockManager.getBlockList().size());
    }

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

    @Test
    void testSetBlocks() {
        String md = """
# Head 1
## Head 2
### Head 3

- UOL
- UOL

> Quote quot
                """;
        assertDoesNotThrow(() -> blockManager.setBlocks(md));
    }

    @Test
    void testSetBlocksEmptyMD() {
        blockManager.setBlocks("");
        assertTrue(blockManager.getBlockList().get(0).getMdText().isEmpty());
    }

    @Test
    void testGetBlockList() {
        String md = """
# Head 1
## Head 2
### Head 3

- UOL
- UOL

> Quote quot
                """;
        blockManager.setBlocks(md);
        assertEquals(5, blockManager.getBlockList().size());
    }

    @Test
    void testExtractFullMd() {
        String md = """
# Head 1
## Head 2
### Head 3

- UOL
- UOL

> Quote quot""";
        String result = """
# Head 1

## Head 2

### Head 3

- UOL
- UOL

> Quote quot

""";
        blockManager.setBlocks(md);
        assertEquals(blockManager.extractFullMd(), result);
    }

    @ParameterizedTest(name = "testRenderAll_{index}")
    @CsvSource({"-1", "10", "150"})
    void testRenderAll(int pos) {
        String md = """
# Head 1
## Head 2
### Head 3

- UOL
- UOL

> Quote quot
                """;
        blockManager.setBlocks(md);
        assertDoesNotThrow(() -> blockManager.renderAll(pos));
    }

    @Test
    void testRenderAllBoundary() {
        String md = """
# Head 1
## Head 2
### Head 3

- UOL
- UOL

> Quote quot
                """;
        blockManager.setBlocks(md);
        assertDoesNotThrow(() -> blockManager.renderAll(-1));
    }

    @Test
    void testMergeBlock() {
        String md = """
> Quote
- whut
> cuote
                """;
        blockManager.setBlocks(md);
        blockManager.getBlockList().get(1).setMdText("> whut");
        assertDoesNotThrow(() -> blockManager.mergeBlock(1));
        assertEquals(1, blockManager.getBlockList().size());
    }

    @Test
    void testManageBlock() {
        String md = """
> Quote
- whut
> cuote
                """;
        blockManager.setBlocks(md);
        blockManager.getBlockList().get(1).setMdText("> whut");
        assertDoesNotThrow(() -> blockManager.manageBlock(1));
        assertEquals(1, blockManager.getBlockList().size());
    }
}
