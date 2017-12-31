package jaemisseo.man.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.reflect.Constructor
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Created by sujkim on 2017-05-29.
 */
class Util {

    final static Logger logger = LoggerFactory.getLogger(Util.getClass());

    /*************************
     * each PROGRESS BAR
     *************************/
    static boolean eachWithProgressBar(def progressList, int barSize, Closure eachClosure){
        return eachWithProgressBar(progressList, barSize, true, eachClosure)
    }

    static boolean eachWithProgressBar(def progressList, int barSize, boolean modePrint, Closure eachClosure){
        // -Normal Each Mode
        if (!modePrint){
            progressList.each{ def object ->
                eachClosure(object)
            }
            return true
        }

        // -ProgressBar Each Mode
        int totalSize = (progressList) ? progressList.size() : 0
        long startTime = new Date().getTime()
        //Print 0%
        withProgressBar(0, totalSize, barSize)
        //Each
        progressList.eachWithIndex{ Object obj, int i ->
            int count = i + 1
            //Work
            eachClosure(obj)
            //Re-print
            withProgressBar(count, totalSize, barSize)
        }
        //End Check Time
        long endTime = new Date().getTime()
        double elapseTime = (endTime - startTime) / 1000
        return true
    }

    /*************************
     * each PROGRESS BAR with Index
     *************************/
    static boolean eachWithIndexAndProgressBar(def progressList, int barSize, Closure eachClosure){
        return eachWithIndexAndProgressBar(progressList, barSize, true, eachClosure)
    }

    static boolean eachWithIndexAndProgressBar(def progressList, int barSize, boolean modePrint, Closure eachClosure){
        // -Normal Each Mode
        if (!modePrint){
            progressList.eachWithIndex{ def object, int i ->
                eachClosure(object, i)
            }
            return true
        }

        // -ProgressBar Each Mode
        int totalSize = (progressList) ? progressList.size() : 0
        long startTime = new Date().getTime()
        //Print 0%
        withProgressBar(0, totalSize, barSize)
        //Each
        progressList.eachWithIndex{ Object obj, int i ->
            int count = i + 1
            //Work
            eachClosure(obj, i)
            //Re-print
            withProgressBar(count, totalSize, barSize)
        }
        //End Check Time
        long endTime = new Date().getTime()
        double elapseTime = (endTime - startTime) / 1000
        return true
    }

    /*************************
     * each PROGRESS BAR with count
     *************************/
    static boolean eachWithCountAndProgressBar(def progressList, int barSize, Closure eachClosure){
        return eachWithCountAndProgressBar(progressList, barSize, true, eachClosure)
    }

    static boolean eachWithCountAndProgressBar(def progressList, int barSize, boolean modePrint, Closure eachClosure){
        // -Normal Each Mode
        if (!modePrint){
            progressList.eachWithIndex{ def object, int i ->
                int count = i + 1
                eachClosure(object, count)
            }
            return true
        }

        // -ProgressBar Each Mode
        int totalSize = (progressList) ? progressList.size() : 0
        long startTime = new Date().getTime()
        //Print 0%
        withProgressBar(0, totalSize, barSize)
        //Each
        progressList.eachWithIndex{ Object obj, int i ->
            int count = i + 1
            //Work
            eachClosure(obj, count)
            //Re-print
            withProgressBar(count, totalSize, barSize)
        }
        //End Check Time
        long endTime = new Date().getTime()
        double elapseTime = (endTime - startTime) / 1000
        return true
    }



    /*************************
     * each TIME PROGRESS BAR
     * 1. You can get parameter made by type of Map, When you use closure.
     *
     * 2. If you wanna print some, then you can add Some String to data.stringList on the closure.
     * - ex)
     *      data.stringList.add("String you wanna say")
     *************************/
    static double eachWithTimeProgressBar(def progressList, int barSize, Closure eachClosure){
        return eachWithTimeProgressBar(progressList, barSize, true, eachClosure)
    }

    static double eachWithTimeProgressBar(def progressList, int barSize, boolean modePrint, Closure eachClosure){
        return startTimeProgressBar(progressList, barSize, modePrint){ Map data ->
            int count = 0
            progressList.eachWithIndex{ Object obj, int i ->
                Thread.sleep(1)
                count = i + 1
                data.item = obj
                data.count = count
                eachClosure(data)
            }
        }
    }

    /*************************
     * while TIME PROGRESS BAR
     * 1. You can get parameter made by type of Map, When you use closure.
     *
     * 2. If you wanna print some, then you can add Some String to data.stringList on the closure.
     * - ex)
     *      data.stringList.add("String you wanna say")
     *************************/
    static double whileWithTimeProgressBar(int second, int barSize, Closure eachClosure){
        return whileWithTimeProgressBar(second, barSize, true, eachClosure)
    }

    static double whileWithTimeProgressBar(int sencond, int barSize, boolean modePrint, Closure eachClosure){
        List progressItemList = (0..(sencond-1))
        return startTimeProgressBar(progressItemList, barSize, modePrint){ Map data ->
            int count = 0
            double elapsedSecond = 0
            while(true){
                Thread.sleep(1)
                elapsedSecond = ((new Date().getTime() - data.startTime) / 1000)
                data.item = elapsedSecond
                data.count = elapsedSecond as int
                if (elapsedSecond > sencond)
                    break
                if (eachClosure(data))
                    break
            }
        }
    }


    static double startTimeProgressBar(def progressList, int barSize, Closure eachClosure){
        return startTimeProgressBar(progressList, barSize, true, eachClosure)
    }

    static double startTimeProgressBar(def progressList, int barSize, boolean modePrint, Closure eachClosure){
        int totalSize = (progressList) ? progressList.size() : 0
        //Printer
        Map data = startPrinter(totalSize, barSize, modePrint)
        //Worker
        double elapseTime = startWorker(data, eachClosure)
        return elapseTime
    }

    static Map startPrinter(int totalSize, int barSize){
        return startPrinter(totalSize, barSize, true)
    }

    static Map startPrinter(int totalSize, int barSize, boolean modePrint){
        Map data = [
                count:0, item:null, stringList:[], printerThread:null, startTime:new Date().getTime(), totalSize:totalSize, barSize:barSize, modePrint:modePrint
        ]
        List stringList = data.stringList
        long startTime = data.startTime

        if (!modePrint)
            return data

        data.printerThread = Util.newThread(''){
//            while ( (data.count as int) < totalSize ){
            while (true){
                //Print String and ProgressBar
                if (stringList) {
                    while (stringList) {
                        Util.withTimeProgressBar(data.count as int, totalSize, barSize, startTime) {
                            logger.info stringList[0]
                            stringList.remove(0)
                        }
                    }
                    //Print ProgressBar
                } else {
                    Util.withTimeProgressBar(data.count as int, totalSize, barSize, startTime)
                }
                //Delay
                Thread.sleep(100)
            }
        }
        return data
    }

    static double startWorker(Map data, Closure eachClosure){
        double elapseTime
        try{
            //Worker
            eachClosure(data)

        }catch(InterruptedException e){
        }finally{
            //Finisher
            elapseTime = endWorker(data)
            print ' DONE \n'
        }
        return elapseTime
    }

    static double endWorker(Map data){
        //Finisher
        Thread printerThread = data.printerThread
        if (printerThread){
            while (data.stringList){}
            withTimeProgressBar(data.count, data.totalSize, data.barSize, data.startTime)
            if (!printerThread.isInterrupted())
                printerThread.interrupt()
            while (printerThread.isAlive()){}
        }
        //End Check Time
        long endTime = new Date().getTime()
        double elapseTime = (endTime - data.startTime) / 1000
        return elapseTime
    }

    /*************************
     * Re-print PROGRESS BAR
     *************************/
    static boolean withProgressBar(int currentCount, int totalSize, int barSize){
        return withProgressBar(currentCount, totalSize, barSize, null)
    }

    static boolean withProgressBar(int currentCount, int totalSize, int barSize, Closure progressClosure){
        return withTimeProgressBar(currentCount, totalSize, barSize, 0, progressClosure)
    }

    static boolean withTimeProgressBar(int currentCount, int totalSize, int barSize, long startTime){
        return withTimeProgressBar(currentCount, totalSize, barSize, startTime, null)
    }

    static boolean withTimeProgressBar(int currentCount, int totalSize, int barSize, long startTime, Closure progressClosure){
        //Clear
        clearProgressBar(barSize)
        //print
        boolean result = (progressClosure) ? progressClosure() : true
        //Print
        printProgressBar(currentCount, totalSize, barSize, startTime)
        //Delay
        Thread.sleep(1)
        return result
    }

    static void printProgressBar(int currentCount, int totalSize, int barSize){
        printProgressBar(currentCount, totalSize, barSize, 0)
    }

    static void printProgressBar(int currentCount, int totalSize, int barSize, long startTime){
        //Calculate
        int curCntInBar = (currentCount / totalSize) * barSize
        int curPercent = (currentCount / totalSize) * 100

        //Print Start
        print '\r['
        //Print Progress
        if (curCntInBar > 0 )
            print ((1..curCntInBar).collect{ '>' }.join('') as String)
        //Print Remain
        if ( (barSize - curCntInBar) > 0 )
            print ((curCntInBar..barSize-1).collect{' '}.join('') as String)

        //Print Last
        // Progressing...
        print "] ${curPercent}%"

        //Print Time
        if (startTime){
            long endTime = new Date().getTime()
            Integer elapseTime = (endTime - startTime) / 1000
            print " ${elapseTime}s"
        }

        //Print Finish
//        if (curCntInBar >= barSize)
//            print ' DONE   \n'
    }

    static void clearProgressBar(int barSize){
        //Delete
        print "\r ${(1..barSize).collect{' '}.join('') as String}"
        //Init
        print "\r"
    }



    /*************************
     * FIND OBJECT
     *************************/
    static def find(def object, def condition){
        return find(object, condition, null)
    }

    static def find(def object, def condition, Closure closure){
        if (object instanceof Map){
            def matchedObj = getMatchedObject(object, condition, closure)
            return matchedObj
        }else if (object instanceof List){
            List results = []
            object.each{
                def matchedObj = getMatchedObject(it, condition, closure)
                if (matchedObj)
                    results << matchedObj
            }
            return results
        }
        return [:]
    }

    static def getMatchedObject(def object, def condition){
        return getMatchedObject(object, condition, null)
    }

    static def getMatchedObject(def object, def condition, Closure closure){
        if (condition instanceof String){
            condition = [id:condition]
        }
        if (condition instanceof List){
            for (int i=0; i<condition.size(); i++){
                if (find(object, condition[i]))
                    return object
            }
            return null //No Matching
        }
        if (condition instanceof Map){
            if (closure){
                return closure(object, condition)
            }else{
                for (String key : (condition as Map).keySet()){
                    def attributeValue = object[key]
                    def conditionValue = condition[key]
                    if (attributeValue) {
                        /**{"attribute":["value1","value2"]} **/
                        if (attributeValue instanceof List) {
                            //{"conditionAttribute":["value1","value2"]}
                            if (conditionValue instanceof List) {
                                if (!attributeValue.findAll { (conditionValue as List).contains(it) })
                                    return //No Matching

                                //{"conditionAttribute": ... }
                            } else {
                                if (!attributeValue.findAll { it == conditionValue })
                                    return //No Matching
                            }

                            /**{"attribute": ... } **/
                        } else {
                            //{"conditionAttribute":["value1","value2"]}
                            if (conditionValue instanceof List) {
                                if (!conditionValue.contains(attributeValue))
                                    return //No Matching

                                //{"conditionAttribute": ... }
                            } else {
                                if (attributeValue != conditionValue)
                                    return //No Matching
                            }
                        }

                    } else {
                        if (attributeValue != conditionValue)
                            return //No Matching
                    }
                }
                return object
            }
        }
        if (condition == null)
            return object
    }

    /*************************
     * Find All SourcePath
     *************************/
    static List<String> findAllSourcePathByPackageName(String packageName){
        return findAllSourcePath(packageName.replace('.', '/'))
    }

    static List<String> findAllSourcePathBySourcePath(String sourcePath){
        return findAllSourcePath(sourcePath)
    }

    static List<String> findAllSourcePath(String sourcePath){
        List<String> resultList = []
        List<URL> urlList = Thread.currentThread().getContextClassLoader().getResources(sourcePath).toList()
        List<URL> rootUrlList = Thread.currentThread().getContextClassLoader().getResources('./').toList()
        urlList.each{ url ->
            if (url.protocol == 'jar'){
                String jarPath = url.getPath().substring(5, url.getPath().indexOf("!")) //strip out only the JAR file
                JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))
                Enumeration<JarEntry> entries = jar.entries()
                while(entries.hasMoreElements()) {
                    String entryRelpath = entries.nextElement().getName()
                    if (entryRelpath.startsWith(sourcePath)){ //filter according to the path
                        String entry = entryRelpath.substring(sourcePath.length())
                        resultList << entryRelpath
                    }
                }
            }else{
                File sourceDirectory = new File(url.toURI())
                URL rootURL = rootUrlList.find{ sourceDirectory.path.startsWith(new File(it.toURI()).path) }
                if (rootURL){
                    File rootDirectory = new File(rootURL.toURI())
                    resultList.addAll( findAllSourcePath(sourceDirectory, rootDirectory) )
                }
            }
        }
        return resultList
    }

    static List<String> findAllSourcePath(File directory, File rootDirectory) throws ClassNotFoundException {
        List<String> resultList = []
        if (directory.exists()){
            directory.listFiles().each{ node ->
                String oneFilePath = node.getPath().substring(rootDirectory.path.length()+1, node.path.length())
                if (node.isDirectory()){
                    resultList.addAll(findAllSourcePath(node, rootDirectory))
                }else{
                    resultList << oneFilePath
                }
            }
        }
        return resultList
    }

    /*************************
     * Find All Classes
     *************************/
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     */
    static List<Class> findAllClasses() throws ClassNotFoundException, IOException {
        return findAllClasses('')
    }

    static List<Class> findAllClasses(Class annotationClass) throws ClassNotFoundException, IOException {
        return findAllClasses('', annotationClass)
    }

    static List<Class> findAllClasses(List<Class> annotationList) throws ClassNotFoundException, IOException {
        return findAllClasses('', annotationList)
    }


    static List<Class> findAllClasses(String packageName) throws ClassNotFoundException, IOException {
        return findAllClasses(packageName){ Class clazz -> validateForClass(clazz) }
    }

    static List<Class> findAllClasses(String packageName, Class annotationClass) throws ClassNotFoundException, IOException {
        return findAllClasses(packageName, annotationClass, null)
    }

    static List<Class> findAllClasses(String packageName, List<Class> annotationList) throws ClassNotFoundException, IOException {
        return findAllClasses(packageName, annotationList, null)
    }


    static List<Class> findAllClasses(Closure closure) throws ClassNotFoundException, IOException {
        List<Class> clazzList = findAllClasses(''){ Class clazz -> validateForClass(clazz) }
        if (closure)
            clazzList = clazzList.findAll{ closure(it) }
        return clazzList
    }

    static List<Class> findAllClasses(Class annotationClass, Closure closure) throws ClassNotFoundException, IOException {
        return findAllClasses([annotationClass], closure)
    }

    static List<Class> findAllClasses(List<Class> annotationList, Closure closure) throws ClassNotFoundException, IOException {
        return findAllClasses('', annotationList, closure)
    }



    static List<Class> findAllClasses(String packageName, Closure closure) throws ClassNotFoundException, IOException {
        List<Class> clazzList = []
        List<String> entryList = findAllSourcePathByPackageName(packageName)
        entryList.each{ entityRelpath ->
            //remove .class
            //replace / => .
            if (entityRelpath.endsWith('.class')){
                String classpath = entityRelpath.substring(0, entityRelpath.length() - 6).replaceAll(/[\/\\]+/, '.')
                Class clazz = Class.forName(classpath)
                clazzList << clazz
            }
        }
        if (closure)
            clazzList = clazzList.findAll{ closure(it) }
        return clazzList
    }

    static List<Class> findAllClasses(String packageName, Class annotationClass, Closure closure) throws ClassNotFoundException, IOException {
        return findAllClasses(packageName, [annotationClass], closure)
    }

    static List<Class> findAllClasses(String packageName, List<Class> annotationList, Closure closure) throws ClassNotFoundException, IOException {
        List<Class> clazzList = findAllClasses(packageName){ Class clazz ->
            return clazz.getAnnotations().findAll{ annotationList.contains(it.annotationType()) }
        }
        if (closure)
            clazzList = clazzList.findAll{ closure(it) }
        return clazzList
    }

    static boolean validateForClass(Class clazz){
        // Validate - Only Instance Makable Class
        try{
            Util.newInstance(clazz.getName())
        }catch(e){
            return false
        }
        return true
    }



    /*************************
     * Thread
     *************************/
    static Thread newThread(Closure threadRunClosure){
        //Create Thread
        Thread thread = new Thread(new Runnable(){void run(){
            threadRunClosure()
        }})
        //Start Thread
        thread.start()
        //Return Thread
        return thread
    }

    static Thread newThread(String interruptMessage, Closure threadRunClosure){
        return newThread{
            try{
                threadRunClosure()
            }catch(InterruptedException e){
                if (interruptMessage)
                    logger.error interruptMessage
            }
        }
    }



    /*************************
     * newInstance
     *************************/
    static Object newInstance(String classpath){
        return newInstance(classpath, [] as Class[], [] as Object[])
    }

    static Object newInstance(Class clazz){
        return newInstance(clazz, [] as Class[], [] as Object[])
    }

    /**
     * ex)
     *   Class[] types = [Double.TYPE]
     *   Object[] parameters = [new Double(0)]
     */
    static Object newInstance(String classpath, Class[] types, Object[] parameters){
        Class clazz = Class.forName(classpath)
        return newInstance(clazz, types, parameters)
    }

    static Object newInstance(Class clazz, Class[] types, Object[] parameters){
        Constructor constructor = clazz.getConstructor(types)
        Object instance = constructor.newInstance(parameters)
        return instance
    }



    /*************************
     * Check JUnit Test
     *************************/
    static boolean isJUnitTest() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace()
        List<StackTraceElement> list = Arrays.asList(stackTrace)
        for (StackTraceElement element : list) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true
            }
        }
        return false
    }

    /*************************
     * Line Up, String!
     *************************/
    static int getLongestLength(List list, String fieldName){
        return getLongestLength( list.collect{it[fieldName]} )
    }

    static int getLongestLength(List<String> list){
        int longestLength = -1
        list.each{ def text ->
            int length = text?.length()
            if (longestLength < length)
                longestLength = length
        }
        return longestLength
    }

    static String getSpacesToLineUp(String stringItem, int bestLongerLength){
        return (stringItem.length() < bestLongerLength) ? (1..(bestLongerLength - stringItem.length())).collect{' '}.join('') : ''
    }

    static String multiTrim(String content){
        //- Remove Shortest Left Indent
        Integer shortestIndentIndex = -1
        List<String> stringList = content.split('\n').toList()
        List<String> resultStringList = stringList.findAll{
            List charList = it.toList()
            if (charList.count(" ") != charList.size()){
                int indentIndex = 0
                for (int i=0; i<charList.size(); i++){
                    if (charList[i] != " "){
                        indentIndex = i
                        break
                    }
                }
                if (indentIndex >= 0){
                    if (shortestIndentIndex == -1 || shortestIndentIndex > indentIndex){
                        shortestIndentIndex =  indentIndex
                    }
                }
            }
            return true
        }
        //- Remove Empty Top and Bottom
        Integer startRowIndex
        Integer endRowIndex
        resultStringList.eachWithIndex{ String row, int index ->
            String line = row.trim()
            if (line && startRowIndex == null)
                startRowIndex = index
            if (line)
                endRowIndex = index
        }
        resultStringList = resultStringList[startRowIndex..endRowIndex]
        String resultString = resultStringList.collect{
            return (it) ? it.substring(shortestIndentIndex) : it
        }.join('\n')
        return resultString
    }


}
