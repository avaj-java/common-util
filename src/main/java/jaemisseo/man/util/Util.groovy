package jaemisseo.man.util

/**
 * Created by sujkim on 2017-05-29.
 */
class Util {

    /**
     * PRINT PROGRESS BAR
     * @param currentIndex
     * @param totalIndex
     * @return
     */
    static printProgressBar(int currentIndex, int totalIndex){
        //Delay
        Thread.sleep(10)

        //Calculate
        int maxNum = 20
        int nowNum = (currentIndex / totalIndex) * maxNum

        //Print Start
        print '\r['

        //Print Progress
        if (nowNum > 0 )
            print ((1..nowNum).collect{ '>' }.join('') as String)

        //Print Remain
        if ( (maxNum - nowNum) > 0 )
            print ((nowNum..maxNum).collect{' '}.join('') as String)

        //Print Last
        //End
        if (nowNum >= maxNum)
            print '] DONE  \n'
        //Start
        else if (nowNum == 0)
            print ']'
        // Progressing...
        else
            print ']'
    }

    static clearProgressBar(int currentIndex, int totalIndex){
        //Calculate
        int maxNum = 20

        //to Delete
        print '\r'
        print ((1..maxNum).collect{' '}.join('') as String)
        //to Init
        print '\r'
    }



    /**
     * FIND OBJECT
     * @param object
     * @param condition
     * @return
     */
    def static find(def object, def condition){
        return find(object, condition, null)
    }

    def static find(def object, def condition, Closure closure){
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

    def static getMatchedObject(def object, def condition, Closure closure){
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
