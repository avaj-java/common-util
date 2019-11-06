package jaemisseo.man.util

import jaemisseo.man.util.test.bean.OptionTestBean
import org.junit.Test

import javax.lang.model.type.NullType

class OptionTest {

    @Test
    void typeNullTest(){
        int a1
        try{
            a1 = Option.getValueByType(null, Integer)
        }catch(e){ println 'error' }
        int a2 = Option.getValueByType(null, int)

        Integer b1 = Option.getValueByType(null, Integer)
        Integer b2 = Option.getValueByType(null, int)

        println a1
        println a2
        println b1
        println b2
    }

    @Test
    void toMap(){
        /** Option.put **/
        Map mapCopy = Option.put(new CommitObject('asdf'), [:])
        assert mapCopy instanceof Map

        /** .toMap() **/
        Map mapConverted = new OptionTestBean(
                id:'hello001',
                name:'myNameIsSomething',
                description:'this is for test',
                count:50,
                totalCount:12310,
                categoryList:['hobby','rice','music']
        ).toMap()
        assert mapConverted instanceof Map && mapConverted.size() == 8
        assert mapConverted.id instanceof String && mapConverted.id == 'hello001'
        assert mapConverted.name instanceof String && mapConverted.name == 'myNameIsSomething'
        assert mapConverted.description instanceof String && mapConverted.description == 'this is for test'
        assert mapConverted.mustNull == null
        assert mapConverted.count instanceof Integer && mapConverted.count == 50
        assert mapConverted.totalCount instanceof Long && mapConverted.totalCount == 12310
    }

}
