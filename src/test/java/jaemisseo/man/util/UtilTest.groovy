package jaemisseo.man.util

import org.junit.Test

/**
 * Created by sujkim on 2017-05-31.
 */
class UtilTest {

    static void main(String[] args) {
        new UtilTest().simpleTest()
    }

    @Test
    void simpleTest(){
        //Start
        println 'Start'
        //Start Print
        int total = 23
        (0..total).each{
            Util.clearProgressBar(it, total)
            Thread.sleep(30)
            Util.printProgressBar(it, total)
        }
    }
}
