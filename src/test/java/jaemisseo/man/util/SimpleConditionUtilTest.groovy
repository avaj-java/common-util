package jaemisseo.man.util

import org.junit.Test

class SimpleConditionUtilTest {

    /*************************
     * findObject
     *
     *      - from:             jaemisseo.man.util.Util.find
     *      - Re implements:    com.datastreams.mdosa.util.SimpleConditionUtil
     *************************/
    @Test
    void findObject(){
        assert ('true' == false) == false
        assert ('true' == true) == false
        assert ('' == false) == false
        assert (null == false) == false

        assert SimpleConditionUtil.findAll([[a:'true', b:false]], [a:'true']) == [[a:'true', b:false]]
        assert SimpleConditionUtil.findAll([[a:true, b:false]], [a:'true']) == []
        assert SimpleConditionUtil.findAll([[a:true, b:false]], [a:true]) == [[a:true, b:false]]

        assert SimpleConditionUtil.matches([a:'true', b:false], [a:'true']) == true
        assert SimpleConditionUtil.matches([a:true, b:false], [a:'true']) == false
        assert SimpleConditionUtil.matches([a:true, b:false], [a:true]) == true

        assert SimpleConditionUtil.matches([a:1, b:2], [a:'1']) == false
        assert SimpleConditionUtil.matches([a:1, b:2], [a:1]) == true
        assert SimpleConditionUtil.matches([a:1, b:2], [a:false]) == false

        assert SimpleConditionUtil.matches([a:null, b:2], [a:null]) == true
        assert SimpleConditionUtil.matches([a:'null', b:2], [a:'null']) == true
        assert SimpleConditionUtil.matches([a:'null', b:2], [a:null]) == false
        assert SimpleConditionUtil.matches([a:null, b:2], [a:'null']) == false
        assert SimpleConditionUtil.matches([a:null, b:2], [a:'']) == false
        assert SimpleConditionUtil.matches([a:'', b:2], [a:'']) == true
        assert SimpleConditionUtil.matches([a:'', b:2], [a:null]) == false

        assert '' != null
        assert null != ''
    }


    Object data = [
            id:"sj_001",
            a2:123,
            a3:true,
            a4:"가나다라마바사",
            a5:"!@Y^%그어어 Banana tatata tomato"
    ]

    @Test
    void matchedObject(){
        assert SimpleConditionUtil.matchesObject(data, [id:'sj*'])
        assert SimpleConditionUtil.matchesObject(data, [id:'sj_*'])
        assert SimpleConditionUtil.matchesObject(data, [id:'sj_0*'])
        assert SimpleConditionUtil.matchesObject(data, [id:'sj_001*'])
        assert SimpleConditionUtil.matchesObject(data, [id:'*j_001*'])
        assert SimpleConditionUtil.matchesObject(data, [id:'*_*'])
        assert !SimpleConditionUtil.matchesObject(data, [id:'j_001*'])
        assert !SimpleConditionUtil.matchesObject(data, [id:'*sj'])
        assert !SimpleConditionUtil.matchesObject(data, [id:'s*j'])
    }

    @Test
    void matchesObjectCheckOr(){
        assert SimpleConditionUtil.matchesObject(data, [[id:'sj*'], [id:'sj_110']])
        assert !SimpleConditionUtil.matchesObject(data, [[id:'s*j*111'], [id:'1sj*']])
        assert SimpleConditionUtil.matchesObject(data, [[id:'s*j*111'], [id:'1sj*'], [a2:123]])
    }


    @Test
    void find(){
        List<?> dataCollection = [
                [
                        id:"sj_001",
                        a2:123,
                        a3:true,
                        a4:"가나다라마바사",
                        a5:"!@Y^%그어어 Banana tatata tomato",
                        b1:[ nm:null, id:null ],
                        c1:[ nm:"ANY", dept:[ nm:"sss", id:"sss001", seq:1 ] ],
                ],
                [
                        id:"sj_002",
                        a2:123,
                        a4:123,
                        a5:"부르부르두두ㅜ구구그어어",
                        b1:[ nm:"SUJU", id:"hi", seq:1 ],
                ],
                [
                        id:"sj_003",
                        a2:null,
                        a4:123,
                        a5:null,
                        c1:[ nm:"SOME", dept:[ nm:"sj", id:"sj001", seq: 2 ] ],
                ]
        ]
        assert SimpleConditionUtil.findAll(dataCollection, [id:'sj*']).size() == 3
        assert SimpleConditionUtil.findAll(dataCollection, [id:'sj_*']).size() == 3
        assert SimpleConditionUtil.findAll(dataCollection, [id:'*3']).size() == 1

        assert SimpleConditionUtil.findAll(dataCollection, [id:'sj_002', b1:[nm:"SUJU"]]).size() == 1
        assert SimpleConditionUtil.findAll(dataCollection, [id:'sj_002', b1:[nm:"SU*", id:"*"]]).size() ==1

        assert SimpleConditionUtil.findAll(dataCollection, [id:'*j_002*', b1:[seq:1]]).size() == 1
        assert SimpleConditionUtil.findAll(dataCollection, [id:'*j_002*', b1:[seq:0]]).size() == 0
        assert SimpleConditionUtil.findAll(dataCollection, [b1:[seq:1]]).size() == 1
        assert SimpleConditionUtil.findAll(dataCollection, [b1:[id:"h*"]]).size() == 1

        assert SimpleConditionUtil.findAll(dataCollection, [id:'*_*', c1:[nm:"*", dept:"sss001"]]).size() == 1
        assert SimpleConditionUtil.findAll(dataCollection, [id:'*_*', c1:[nm:"*", dept:[seq:"*"]]]).size() == 2
        assert SimpleConditionUtil.findAll(dataCollection, [id:'*_*', c1:[nm:"*", dept:[seq:[1, 2, 3]]]]).size() == 2
        assert SimpleConditionUtil.findAll(dataCollection, [id:'*_*', c1:[nm:"*", dept:[seq:[5, 6, 7]]]]).size() == 0

        assert SimpleConditionUtil.findAll(dataCollection, [id:'j_001*', a5:"*부르*"]).size() == 0
        assert SimpleConditionUtil.findAll(dataCollection, [id:'*j*0*', a5:"*ㅜ*"]).size() == 1
        assert SimpleConditionUtil.findAll(dataCollection, [id:'*j*0*', a5:"*그어*"]).size() == 2

        assert SimpleConditionUtil.findAll(dataCollection, null).size() == 0
        assert SimpleConditionUtil.findAll([null], null).size() == 1

        assert !SimpleConditionUtil.findAll(dataCollection, [id:'*sj'])
        assert !SimpleConditionUtil.findAll(dataCollection, [id:'s*j'])
    }





}
