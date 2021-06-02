package jaemisseo.man.util;

import jaemisseo.man.util.SimpleSplitUtil
import org.junit.Test

class SimpleSplitUtilTest {

    @Test
    void ttt(){
        List<String> groups = SimpleSplitUtil.separateGroupByQuotes("1 ==~ '^h\"\"ello' == \"^h%e^#llo'\" ")
        assert groups instanceof List
        assert groups.size() == 5
        assert (
                groups[0] == '1 ==~ '
                && groups[1] == '\'^h""ello\''
                && groups[2] == ' == '
                && groups[3] == '"^h%e^#llo\'"'
                && groups[4] == ' '
        )
    }

    @Test
    void dd(){
        List<String> groups = SimpleSplitUtil.separateGroupByQuotes('==~ "\\w*"')
        assert groups instanceof List
        assert groups.size() == 2
        assert (
                groups[0] == '==~ '
                && groups[1] == '"\\w*"'
        )
    }

}
