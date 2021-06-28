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
    void range(){
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



    static String simpleObjectInvalidRangeFormatValue = '''
        test: [ aa ,bb,  ee ,dd, ff,667747214], /* array */
        number: 21245,                          /* number */
        string:"21245",                         /* string */
        validFormat001: 5..100,                 /* array (by range) */
        validFormat002: [5..100, a..b, c..f],   /* array (by range) */
        invalidFormat001: !..#,                 /* array (by range) */
        invalidFormat002: !..#.yml,             /* string */
        invalidFormat003: 1d..5,                /* string */
        invalidFormat004: a..z.yml,             /* string */
        invalidFormat005: a-z.yml,              /* string */
        invalidFormat006: application-something.yml, /* string */
        invalidFormat007: [application-local.yml, application-force.yml, sixx-*, idx..*, xax-*..yml],
        noStringRange: "a-z.yml"                /* string */
    '''

    @Test
    void invalidRange(){
        Map<String, Object> data = SimpleDataUtil.parseSimpleObjectExpression(simpleObjectInvalidRangeFormatValue)
        assert (
                data['test'] == ['aa', 'bb', 'ee', 'dd', 'ff', 667747214]
                && data['number'] == 21245
                && data['string'] == "21245"
                && data['validFormat001'] == 5..100
                && data['validFormat002'] == [5..100, 'a'..'b', 'c'..'f']
                && data['invalidFormat001'] == '!'..'#'
                && data['invalidFormat002'] == "!..#.yml"
                && data['invalidFormat003'] == "1d..5"
                && data['invalidFormat004'] == "a..z.yml"
                && data['invalidFormat005'] == "a-z.yml"
                && data['invalidFormat006'] == "application-something.yml"
                && data['invalidFormat007'] == ['application-local.yml', 'application-force.yml', 'sixx-*', 'idx..*', 'xax-*..yml']
                && data['noStringRange'] == "a-z.yml"
        )
    }

}
