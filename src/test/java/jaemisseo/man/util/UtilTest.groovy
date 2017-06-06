package jaemisseo.man.util

import org.junit.Test
import org.junit.experimental.theories.DataPoint

import javax.jws.soap.SOAPBinding

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

    /*************************
     * newInstance
     *************************/
    @Test
    void newInstance(){
        assert 'class java.lang.String' == Util.newInstance('java.lang.String').getClass().toString()
        assert 'java.lang.String'       == Util.newInstance('java.lang.String').getClass().getName()
        assert 'java.lang.String'       == Util.newInstance('java.lang.String').getClass().getCanonicalName()
        assert 'String'                 == Util.newInstance('java.lang.String').getClass().getSimpleName()
    }

    /*************************
     * findAllSourcePath
     *************************/
    @Test
    void findAllSourcePath(){
        List<Class<?>> pathList = Util.findAllSourcePath("jaemisseo/man")
        pathList.each{ println it }

        println "/////"
        
        List<Class<?>> pathList2 = Util.findAllSourcePathByPackageName("jaemisseo.man")
        pathList2.each{ println it }

        assert pathList.size() == pathList2.size()
    }

    /*************************
     * findAllClasses
     *************************/
    @Test
    void findAllClass(){

        println '\n\n///////////////////////// No Condition'
        Util.findAllClasses().each {
           println it
        }
        println '\n\n///////////////////////// Condition{ Custom }'
        Util.findAllClasses(){ return true }.each {
            println it
        }

        println '\n\n///////////////////////// Condition( Package )'
        Util.findAllClasses('jaemisseo.man').each {
            println it
        }

        println '\n\n///////////////////////// Condition( Package + Annotation )'
        Util.findAllClasses("jaemisseo.man", SOAPBinding).each{
            println it
        }

        println '\n\n///////////////////////// Condition{ Package + Annotation + Custom }'
        Util.findAllClasses("jaemisseo.man", SOAPBinding){ return true }.each{
            println it
        }

        println '\n\n///////////////////////// Condition( Package + AnnotationList )'
        Util.findAllClasses("jaemisseo.man", [DataPoint, SOAPBinding]).each{
            println it
        }

        println '\n\n///////////////////////// FInd All Infomation'
        Util.findAllClasses().each { Class clazz ->
            println clazz
            println clazz.getAnnotations()
            println clazz.getDeclaredAnnotations()
            println clazz.getDeclaredConstructors()
            println clazz.getDeclaredFields()
            println clazz.getDeclaredMethods()
            println '===== ===== ===== ===== ===== ===== ====='
        }
    }

}
