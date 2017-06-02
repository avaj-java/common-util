package jaemisseo.man.util

import org.junit.Test

/**
 * Created by sujkim on 2017-05-31.
 */
class UtilTest {

//    static void main(String[] args) {
//        new UtilTest().progressBar_menually()
//        new UtilTest().progressBar_automatically()
//        new UtilTest().newThread()
//    }




    
    /*************************
     * < README >
     * it can't show progressBar on IntelliJ (DevelopmentTool)
     * try to test on Command Line
     *
     * < USE >
     * Util.clearProgressBar()
     * Util.printProgressBar()
     *************************/
    @Test
    void progressBar_menually(){
        //Data Setup
        int total = 20
        int barSize = 30

        //Loop
        println 'Start'
        (0..total).each{
            Util.clearProgressBar(barSize)
            Thread.sleep(80)
            Util.printProgressBar(it, total, barSize)
        }
    }



    /*************************
     * < README >
     * it can't show progressBar on IntelliJ (DevelopmentTool)
     * try to test on Command Line
     *
     * < USE >
     * Util.withProgressBar()
     *************************/
    @Test
    void progressBar_automatically(){
        //Data Setup
        int total = 20
        int barSize = 30

        //Loop - method1
        println "Start Just "
        (0..total).each{
            Thread.sleep(80)
            Util.withProgressBar(it, total, barSize)
        }

        //Loop - method2
        (0..total).each{
            Thread.sleep(80)
            Util.withProgressBar(it, total, barSize){
                println "Ha Ha Ha ~ "
            }
        }
    }

    /*************************
     * newThread
     *************************/
    @Test
    void newThread(){
        //New Thread Start
        Thread thread = Util.newThread('New thread was stoped'){
            progressBar_automatically()
        }

        //Main Thread
        (0..30).each{
            Thread.sleep(20)
            println "Good"
            //Stop New Thread
            if (it == 25)
                thread.interrupt()
        }
    }

}
