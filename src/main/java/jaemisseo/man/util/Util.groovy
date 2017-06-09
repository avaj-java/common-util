package jaemisseo.man.util

import java.lang.annotation.Annotation
import java.lang.reflect.Constructor
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Created by sujkim on 2017-05-29.
 */
class Util {

    /*************************
     * PROGRESS BAR
     *************************/
    static boolean eachWithProgressBar(def progressList, int barSize, Closure eachClosure){
        int totalIndex = (progressList) ? progressList.size() -1 : 0
        progressList.eachWithIndex{ Object obj, int i ->
            eachClosure(obj, i)
            withProgressBar(i, totalIndex, barSize){}
        }
    }

    static boolean withProgressBar(int currentIndex, int totalIndex, int barSize){
        return withProgressBar(currentIndex, totalIndex, barSize, null)
    }

    static boolean withProgressBar(int currentIndex, int totalIndex, int barSize, Closure progressClosure){
        boolean result
        //Clear
        clearProgressBar(barSize)
        //Set Param
        result = (progressClosure) ? progressClosure() : true
        //Print
        printProgressBar(currentIndex, totalIndex, barSize)
        return result
    }

    static void printProgressBar(int currentIndex, int totalIndex, int barSize){
        //Calculate
        int curNum = (currentIndex / totalIndex) * barSize
        int curPer = (currentIndex / totalIndex) * 100

        //Print Start
        print '\r['
        //Print Progress
        if (curNum > 0 )
            print ((1..curNum).collect{ '>' }.join('') as String)
        //Print Remain
        if ( (barSize - curNum) > 0 )
            print ((curNum..barSize-1).collect{' '}.join('') as String)

        //Print Last
        //End
        if (curNum >= barSize)
            print '] DONE  \n'
        // Progressing...
        else
            print "] ${curPer}%"

        //Delay
        Thread.sleep(1)
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
                    String attributeValue = object[key]
                    def conditionValue = condition[key]
                    if (attributeValue){
                        if (conditionValue instanceof String && attributeValue == conditionValue){
                        }else if (conditionValue instanceof List && conditionValue.contains(attributeValue)){
                        }else{
                            return //No Matching
                        }
                    }else{
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

    static List<Class> findAllClasses(Class annotation) throws ClassNotFoundException, IOException {
        return findAllClasses('', annotation)
    }

    static List<Class> findAllClasses(List<Annotation> annotationList) throws ClassNotFoundException, IOException {
        return findAllClasses('', annotationList)
    }


    static List<Class> findAllClasses(String packageName) throws ClassNotFoundException, IOException {
        return findAllClasses(packageName){ Class clazz -> validateForClass(clazz) }
    }

    static List<Class> findAllClasses(String packageName, Class annotation) throws ClassNotFoundException, IOException {
        return findAllClasses(packageName, annotation, null)
    }

    static List<Class> findAllClasses(String packageName, List<Annotation> annotationList) throws ClassNotFoundException, IOException {
        return findAllClasses(packageName, annotationList, null)
    }


    static List<Class> findAllClasses(Closure closure) throws ClassNotFoundException, IOException {
        List<Class> clazzList = findAllClasses(''){ Class clazz -> validateForClass(clazz) }
        if (closure)
            clazzList = clazzList.findAll{ closure(it) }
        return clazzList
    }

    static List<Class> findAllClasses(Class annotation, Closure closure) throws ClassNotFoundException, IOException {
        return findAllClasses([annotation], closure)
    }

    static List<Class> findAllClasses(List<Annotation> annotationList, Closure closure) throws ClassNotFoundException, IOException {
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

    static List<Class> findAllClasses(String packageName, Class annotation, Closure closure) throws ClassNotFoundException, IOException {
        return findAllClasses(packageName, [annotation], closure)
    }

    static List<Class> findAllClasses(String packageName, List<Annotation> annotationList, Closure closure) throws ClassNotFoundException, IOException {
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
                println interruptMessage
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

}
