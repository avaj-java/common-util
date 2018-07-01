package jaemisseo.man.util

import org.junit.Test

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

}
