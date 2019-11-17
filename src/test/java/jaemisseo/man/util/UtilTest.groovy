package jaemisseo.man.util

import jaemisseo.man.util.test.annotation.AnnotationTestA
import jaemisseo.man.util.test.annotation.AnnotationTestB
import jaemisseo.man.util.test.bean.AnnotationTest1
import jaemisseo.man.util.test.bean.AnnotationTest3
import org.junit.Ignore
import org.junit.Test

/**
 * Created by sujkim on 2017-05-31.
 */
class UtilTest {

//    static void main(String[] args) {
//        new UtilTest().progressBar_menually()
//        new UtilTest().progressBar_automatically()
//        new UtilTest().progressBar_other_thread()
//        new UtilTest().newThread()
//    }


    @Test
    @Ignore
    void macTest(){
        String a = Util.macAddress()
        Util.printInterfaces()
        println a
    }

    @Test
    void vmidLoopTest(){
        Long testCount = 999999

        /** How many times it is unique? **/
        Long cnt = 0
        String now
        String before
        (1..testCount).each{
            ++cnt
            now = Util.makeReplacedVMID()
            if (before && before != now){
                println cnt
                println now
                throw new Exception('not same')
            }
            before = now
        }
        println '< Test Finish!!! >'
    }

    @Test
    void vmidWithThreadTest(){
        int threadSize = 500

        /** Is it unique even with other threads? **/
        Map<String, List<String>> threadAndIdListMap = [:]
        Long cnt = 0
        String now
        String before
        //- Making id
        (1..threadSize).each{ Integer count ->
            Thread thread = new Thread(new Runnable(){void run(){
                Thread.sleep(1)
                String makedID = new Date().getTime() + '-' + Util.makeUUID()
                List idList = (1..100).collect{
                    Thread.sleep(1)
                    return Util.makeReplacedVMID()
                }
                putToMap(threadAndIdListMap, makedID, idList)
                println "[${makedID}] collecting done."
            }}).start()
        }
        //- Waiting
        while (threadAndIdListMap.size() < threadSize){
            sleep(1000)
            println 'wait'
        }
        println "< Collecting Finish!!! > - Size: ${threadAndIdListMap.size()}"

        //- Checking no duplication
        threadAndIdListMap.each{ String threadId, List<String> idList ->
            idList.each{ String id ->
                now = id
                if (before && before != now){
                    println cnt
                    println now
                    throw new Exception('not same')
                }
                before = now
            }
        }
        println '< Test Finish!!! >'
    }

    synchronized void putToMap(Map map, String key, Object value){
        map[key] = value
    }



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
            Thread.sleep(80)
            Util.clearProgressBar(barSize)
            Util.printProgressBar(it, total, barSize)
        }

        //Loop - method1
        println "Start Just "
        (0..total).each{
            //Work
            Thread.sleep(80)
            //Re-print
            Util.withProgressBar(it, total, barSize)
        }

        //Loop - method2
        (0..total).each{
            //Work
            Thread.sleep(80)
            //Re-print
            Util.withProgressBar(it, total, barSize){
                println "Ha Ha Ha ~"
            }
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

        //Just Work
        Util.eachWithProgressBar([0, 1, 2, 5, 10, 50, 100], 20){ it->
            Thread.sleep(80)
        }

        Util.eachWithIndexAndProgressBar([0, 1, 2, 5, 10, 50, 100], 20){ it, i ->
            Thread.sleep(80)
        }
    }

    @Test
    @Ignore
    void while_timeProgressBar_automatically(){
        //Data Setup
        int total = 10
        int barSize = 30

        //Just Work
        Util.whileWithTimeProgressBar(total, barSize){ it->
            if (it.count == 5)
                return true
            Thread.sleep(80)
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
     * progressBar other thread
     *************************/
    @Test
    @Ignore
    void progressBar_other_thread(){
        List list = [1,2,3,4,5,6,7,8,9,10,111, 112, 113, 114, 115, 116, 117]
        Map map = [
            count : 0,
            startTime : new Date().getTime(),
            list : list,
            barSize: 20
        ]

        Util.eachWithTimeProgressBar(list, 30){ data ->
            [1000,2000,3000,4000,5000,6000].each{
                data.stringList.add(data.item + it)
                Thread.sleep(80)
            }
            Thread.sleep(100)
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

        ///////////////////////// Condition( Package + Annotation )
        assert [AnnotationTest1] == Util.findAllClasses("jaemisseo.man", AnnotationTestA)

        ///////////////////////// Condition{ Package + Annotation + Custom }
        assert [AnnotationTest1] == Util.findAllClasses("jaemisseo.man", AnnotationTestA){ return true }

        ///////////////////////// Condition( Package + AnnotationList )
        assert [AnnotationTest1, AnnotationTest3] == Util.findAllClasses("jaemisseo.man", [AnnotationTestA, AnnotationTestB])

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

    /** java.lang.IllegalArgumentException: URI not hierarchical **/
    @Test(expected = java.lang.IllegalArgumentException)
    void uriTestError(){
        new File(new URL('file:jar:/D:/java/projects/asd/af/asdfdf.jar!/Xd/').toURI())
    }

    @Test(expected = java.lang.IllegalArgumentException)
    void uriTestError2(){
        // It works.
        new File(new URL('file:/D:/java/projects/asd/af/asdfdf.jar!/Xd/').toURI())
        // but, It does not works.
        new File(new URL('file:D:/java/projects/asd/af/asdfdf.jar!/Xd/').toURI())
    }

    @Test(expected = java.lang.IllegalArgumentException)
    void uriTestErrror3(){
        new File( new URL('jar:file:/C:/hahaha/project_something/build/libs/exploded/name-v0.1.2.015.war/WEB-INF/lib/xpp3_min-1.1.4c.jar!/').toURI())
    }



    /*************************
     * findObject
     *************************/
    @Test
    void findObject(){
        assert ('true' == false) == false
        assert ('true' == true) == false
        assert ('' == false) == false
        assert (null == false) == false

        assert Util.find([[a:'true', b:false]], [a:'true']) == [[a:'true', b:false]]
        assert Util.find([[a:true, b:false]], [a:'true']) == []
        assert Util.find([[a:true, b:false]], [a:true]) == [[a:true, b:false]]

        assert Util.find([a:'true', b:false], [a:'true']) == [a:'true', b:false]
        assert Util.find([a:true, b:false], [a:'true']) == null
        assert Util.find([a:true, b:false], [a:true]) == [a:true, b:false]

        assert Util.find([a:1, b:2], [a:'1']) == null
        assert Util.find([a:1, b:2], [a:1]) == [a:1, b:2]
        assert Util.find([a:1, b:2], [a:false]) == null

        assert Util.find([a:null, b:2], [a:null]) == [a:null, b:2]
        assert Util.find([a:'null', b:2], [a:'null']) == [a:'null', b:2]
        assert Util.find([a:'null', b:2], [a:null]) != [a:'null', b:2]
        assert Util.find([a:null, b:2], [a:'null']) != [a:null, b:2]

        assert '' != null
        assert null != ''

        assert Util.find([a:null, b:2], [a:'']) != [a:null, b:2]
        assert Util.find([a:'', b:2], [a:'']) != [a:null, b:2]
        assert Util.find([a:'', b:2], [a:null]) != [a:null, b:2]
    }

}
