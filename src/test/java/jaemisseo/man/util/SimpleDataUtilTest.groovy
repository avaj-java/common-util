package jaemisseo.man.util

import org.junit.Test

class SimpleDataUtilTest {

    @Test
    void simpleObject() {
        List<Map> data = SimpleDataUtil.parseSimpleObjectExpression('[{prop1:1,prop2:"1"},{prop1:2, "prop2":"2"},{prop3:true}]')
        assert data instanceof List
        assert data[0]['prop1'] == 1
        assert data[0]['prop2'] == "1"
        assert data[1]['prop1'] == 2
        assert data[1]['prop2'] == "2"
        assert data[2]['prop3'] == true
    }

    @Test
    void simpleObject_space() {
        List<Map> data = SimpleDataUtil.parseSimpleObjectExpression(' [ {    prop1: 1, prop2:   "1" } , {prop1 : 2, "prop2"    : "2"} ,{prop3 :true}  ]   ')
        assert data instanceof List
        assert data[0]['prop1'] == 1
        assert data[0]['prop2'] == "1"
        assert data[1]['prop1'] == 2
        assert data[1]['prop2'] == "2"
        assert data[2]['prop3'] == true
    }

    @Test
    void simpleObject_simple() {
        Map<String, Object> data = SimpleDataUtil.parseSimpleObjectExpression('search,  index,  _name:olapItmDscrText,  _definition:olapItmPathText,  value._name:<span>${_name}</span><a class="icon link" href="https://google.com/" target="_blank"></a>')
        assert (
                data['search']
                && data['index']
                && data['_name'] == 'olapItmDscrText'
                && data['_definition'] == 'olapItmPathText'
                && data['value']['_name'] == '<span>${_name}</span><a class="icon link" href="https://google.com/" target="_blank"></a>'
        )
    }

    @Test
    void ignoreWithinBrace(){
        Map<String, Object> data = SimpleDataUtil.parseSimpleObjectExpression('search,  index, test:[ aa ,bb,  ee ,dd, ff,667747214],number:21245')
        assert (
                data['search']
                && data['index']
                && data['test'] == ['aa', 'bb', 'ee', 'dd', 'ff', 667747214]
                && data['number'] == 21245
        )
    }

    @Test
    void multipleLineText(){
        Map<String, Object> data = SimpleDataUtil.parseSimpleObjectExpression('''
        search,  
        index, 
        test:[ aa ,bb,  ee ,dd, ff,667747214],
        number:21245
        ''')
        assert (
                data['search']
                && data['index']
                && data['test'] == ['aa', 'bb', 'ee', 'dd', 'ff', 667747214]
                && data['number'] == 21245
        )
    }


    static String simpleObjectFormatValue = '''
        search,                                 /* boolean */
        index.abc,                              /* boolean */ 
        index.abcdef: Hello Depth~,             /* string */
        index.lvl1.one: Hello lvl1 One,         /* string */
        index.lvl1.two: Hello lvl1 Two,         /* string */
        test:[ aa ,bb,  ee ,dd, ff,667747214],  /* array */
        number:21245,                           /* number */
        string:"21245"                          /* string */
    '''
    @Test
    void depthByDotMap(){
        Map<String, Object> data = SimpleDataUtil.parseSimpleObjectExpression(simpleObjectFormatValue)
        assert (
                data['search']
                && data['index']['abc']
                && data['index']['abcdef'] == "Hello Depth~"
                && data['index']['lvl1']['one'] == "Hello lvl1 One"
                && data['index']['lvl1']['two'] == "Hello lvl1 Two"
                && data['test'] == ['aa', 'bb', 'ee', 'dd', 'ff', 667747214]
                && data['number'] == 21245
                && data['string'] == "21245"
        )
    }

    @Test
    void flattenKeyMap(){
        Map<String, Object> flattenData = SimpleDataUtil.parseSimpleObjectExpressionAsFlattenKey(simpleObjectFormatValue)
        assert (
                flattenData['search']
                && flattenData['index.abc']
                && flattenData['index.abcdef'] == "Hello Depth~"
                && flattenData['index.lvl1.one'] == "Hello lvl1 One"
                && flattenData['index.lvl1.two'] == "Hello lvl1 Two"
                && flattenData['test'] == ['aa', 'bb', 'ee', 'dd', 'ff', 667747214]
                && flattenData['number'] == 21245
                && flattenData['string'] == "21245"
        )
    }





    @Test
    void spliter(){
        List<String> splitedList = SimpleDataUtil.splitSimpleObject('''
        "mode\\"Use": true,
        "include":[{usrNm:수중,usrId:sujung},{usrNm:뚜둥}],
        object:{
            123,
            asdf,
            "555":"12,3",
            {usrNm:수중*,usrId:sujung*}, 
            {usrNm:뚜둥}
        }
        ''')
        assert splitedList.size() == 3
        assert splitedList[0].equals('"mode\\"Use": true')
        assert splitedList[1].equals('"include":[{usrNm:수중,usrId:sujung},{usrNm:뚜둥}]')
        assert splitedList[2].equals('''object:{
            123,
            asdf,
            "555":"12,3",
            {usrNm:수중*,usrId:sujung*}, 
            {usrNm:뚜둥}
        }''')
    }

    @Test
    void multipleLineAndObjectExpText(){
        Map<String, Object> data = SimpleDataUtil.parseSimpleObjectExpression('''
        "modeUse": true,
        "include":[{usrNm:수중,usrId:sujung},{usrNm:뚜둥}],
        object:{
            123,
            asdf,
            "555":"12,3",
            some1: {usrNm:수중*,usrId:sujung*}, 
            some2: {usrNm:뚜둥}
        }
        ''')
        assert data['modeUse']
        assert data['include'] == [[usrNm:'수중', usrId:'sujung'], [usrNm:'뚜둥']]
        assert data['object']['123'] == true
        assert data['object']['asdf'] == true
        assert data['object']['555'] == "12,3"
        assert data['object']['some1'] == [usrNm:'수중*', usrId:'sujung*']
        assert data['object']['some2'] == [usrNm:'뚜둥']
    }

    @Test
    void ignoreComment(){
        Map<String, Object> data = SimpleDataUtil.parseSimpleObjectExpression('''
        ui: {
            grid: [
                    ["globalName", "dataType", "dataStructure"],
                    ["system", "business", 123, 1.11, 0.12305, '123'],
                    ["standardUrl", "brAnalysis", "columnProfiling", "vulnerabilityLevel", "relatedInfo"/*list*/, "datas"/*list */] /* cocant extras */,
                    ["_definition", true, null, false, 0],
                    ["_cusr", "_createDt", "_musr", "_modifyDt"],
            ] ,
            excludes: [
                    'details',
                    'metaStream',
                    'qtrack',
                    'qualityStream',
                    "detail",
                    "name",
                    "_name",
                    "objectId",
                    "protectionLevel",
                    "uuid",
                    "isMaster",
                    "supersql",
                    "standardIndex",
                    "qualityIndex",
                    "query",
                    "_type"
            ]
        }
        ''')
        assert data['ui']
        assert data['ui']['grid'] instanceof List
        assert data['ui']['grid'].size() == 5
        assert data['ui']['grid'][0].size() == 3
        assert data['ui']['grid'][0] == ["globalName", "dataType", "dataStructure"]
        assert data['ui']['grid'][1].size() == 6
        assert data['ui']['grid'][1] == ["system", "business", 123, 1.11, 0.12305, '123']
        assert data['ui']['grid'][2].size() == 6
        assert data['ui']['grid'][2] == ["standardUrl", "brAnalysis", "columnProfiling", "vulnerabilityLevel", "relatedInfo", "datas"]
        assert data['ui']['grid'][3].size() == 5
        assert data['ui']['grid'][3] == ["_definition", true, null, false, 0]
        assert data['ui']['grid'][4].size() == 4
        assert data['ui']['grid'][4] == ["_cusr", "_createDt", "_musr", "_modifyDt"]
        assert data['ui']['excludes'] instanceof List
        assert data['ui']['excludes'].size() > 10
        assert data['ui']['excludes'][0].equals("details")

    }



    @Test
    void quoteText(){
        Map<String, Object> data = SimpleDataUtil.parseSimpleObjectExpression('''
        search,
        index,
        test:[ aa ,bb,  ee ,dd, ff,667747214],
        quote:[ "aa ,bb",  ee ,dd, ff,667747214],
        quote2: "asdfasdf, ferf ,f g,,,",
        number:21245
        ''')
        assert (
                data['search']
                        && data['index']
                        && data['test'] == ['aa', 'bb', 'ee', 'dd', 'ff', 667747214]
                        && data['quote'] == ['aa ,bb', 'ee', 'dd', 'ff', 667747214]
                        && data['quote2'] == "asdfasdf, ferf ,f g,,,"
                        && data['number'] == 21245
        )
    }



    @Test
    void standardMeaning(){
        Map<String, Object> meaningMap = ["male":["m", "M", "men"], "fmle":["f", "F", "woman", ""]]

        assert ["M", "m", "men"].every{
            String v = SimpleDataUtil.toStandardMeaningWith(it, meaningMap, true)
            return v
        }
        assert !["MEn", "Men", "mEn"].any{
            String v = SimpleDataUtil.toStandardMeaningWith(it, meaningMap, false)
            return v
        }
    }

}
