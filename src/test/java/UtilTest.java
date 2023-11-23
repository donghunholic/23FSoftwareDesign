import com.mdeditor.sd.Utils;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {
    @Test
    void testStringToHtml(){
        String Md = "# Head1";
        System.out.println(Utils.stringToHtml(Md));
        assertEquals(Utils.stringToHtml(Md), "<h1>Head1</h1>");
    }
}

