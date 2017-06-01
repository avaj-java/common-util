package jaemisseo.man.util

/**
 * Created by sujkim on 2017-05-29.
 */
class Util {

    /*************************
     * PROGRESS BAR
     *************************/
    static void withProgressBar(int currentIndex, int totalIndex, int barSize){
        withProgressBar(currentIndex, totalIndex, barSize, null)
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
            print ((curNum..barSize).collect{' '}.join('') as String)

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



    /**
     * FIND OBJECT
     * @param object
     * @param condition
     * @return
     */
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

}
