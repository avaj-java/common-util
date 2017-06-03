package jaemisseo.man.util

import java.lang.reflect.Constructor

/**
 * Created by sujkim on 2017-05-29.
 */
class Util {

    /*************************
     * PROGRESS BAR
     *************************/
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
        //Delay
        Thread.sleep(10)
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
     * Find All Classes
     *************************/
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    static List<Class> findAllClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration resources = classLoader.getResources(path);
        //Collect Directories
        List dirList = []
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement()
            dirList.add(new File(resource.getFile()))
        }
        //Collect Classes
        List<File> classList = []
        dirList.each{ dir ->
            classList.addAll(findAllClasses(dir, packageName))
        }
        return classList
    }

    static List<Class> findAllClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classList = []
        if (directory.exists()){
            directory.listFiles().each{ file ->
                String fileName = file.getName()
                String classpath = "${packageName}.${fileName}"
                if (file.isDirectory()){
                    assert !fileName.contains(".");
                    classList.addAll(findAllClasses(file, classpath))
                }else if (fileName.endsWith(".class")){
                    // Class
                    Class clazz = Class.forName(classpath.substring(0, classpath.length() - 6))
                    if (validateForClass(clazz))
                        classList << clazz
                }
            }
        }
        return classList
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

}
