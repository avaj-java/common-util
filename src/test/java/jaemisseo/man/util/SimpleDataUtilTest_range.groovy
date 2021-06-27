package jaemisseo.man.util

import org.junit.Test

class SimpleDataUtilTest_range {

    static String simpleObjectFormatValue = '''
        test: [ aa ,bb,  ee ,dd, ff,667747214], /* array */
        number: 21245,                          /* number */
        string:"21245",                         /* string */
        numberRange: 1..5,                      /* array (by range) */
        stringRange: a..z,                      /* array (by range) */
        noStringRange: "a..z"                   /* string */
    '''
    @Test
    void rangeValue(){
        Map<String, Object> data = SimpleDataUtil.parseSimpleObjectExpression(simpleObjectFormatValue)
        assert (
                data['test'] == ['aa', 'bb', 'ee', 'dd', 'ff', 667747214]
                && data['number'] == 21245
                && data['string'] == "21245"
                && data['numberRange'] == 1..5
                && data['stringRange'] == "a".."z"
                && data['noStringRange'] == "a..z"
        )
    }

}
