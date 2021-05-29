package jaemisseo.man.util

class SimpleConditionUtil {



    /**************************************************
     *
     * MATCHES
     *
     **************************************************/
    static boolean matches(Object data, Object condition){
        if (data instanceof String){
            return data.matches(condition)
        }else{
            return matchesObject(data, condition)
        }
    }

    static boolean matchesObject(Object data, Object condition){
        return matchesObject(data, condition, null)
    }

    static boolean matchesObject(Object data, Object condition, Closure closure){
        if (condition == null)
            return (data == null)

        if (condition instanceof String && (data instanceof List || data instanceof Map))
            condition = [id:condition]

        if (condition instanceof String || condition instanceof Number || condition instanceof Boolean){
            return matchedValue(data, condition)

        }else if (condition instanceof List){
            if (closure){
                return closure(data, condition)
            }
            if (data instanceof List){
                return condition.every{
                    matchesObject(data, it)
                }
            }else{
                return condition.any{
                    matchesObject(data, it)
                }
            }

        }else if (condition instanceof Map){
            if (closure){
                return closure(data, condition)
            }
            return condition.every{ String key, Object conditionValue ->
                if (data == null)
                    return false
                Object dataValue = data[key]
                matchesObject(dataValue, conditionValue)
            }
        }
    }

    static boolean matchedValue(Object dataValue, Object conditionValue){
        if (conditionValue instanceof String){
            if (conditionValue.contains("*")){
                String dataStringValue = String.valueOf(dataValue)
                String regexp = conditionValue.replace('*',".*")
                boolean matched = dataStringValue.matches(regexp)
                return matched
            }else{
                return conditionValue.equals(dataValue)
            }
        }else{
            if (dataValue instanceof Number || dataValue instanceof Boolean){
                return dataValue.equals(conditionValue)
            }
        }
    }



    /**************************************************
     *
     * FIND
     *
     **************************************************/
    static Object find(Object dataCollection, Object condition){
        return find(dataCollection, condition, null)
    }

    static Object find(Object dataCollection, Object condition, Closure closure){
        Object matchedData = null
        if (dataCollection instanceof Map){
            matchedData = dataCollection.find{ String key, Object data ->
                matchesObject(data, condition, closure)
            }

        }else if (dataCollection instanceof List){
            matchedData = dataCollection.find{ Object data ->
                matchesObject(data, condition, closure)
            }
        }
        return matchedData
    }



    /**************************************************
     *
     * FIND ALL
     *
     **************************************************/
    static List<Object> findAll(Object dataCollection, Object condition){
        return findAll(dataCollection, condition, null)
    }

    static List<Object> findAll(Object dataCollection, Object condition, Closure closure){
        List<?> matchedDataList = []
        if (dataCollection instanceof Map){
            dataCollection.each{ String key, Object data ->
                if (matchesObject(data, condition, closure))
                    matchedDataList << data
            }

        }else if (dataCollection instanceof List){
            dataCollection.each{ Object data ->
                if (matchesObject(data, condition, closure))
                    matchedDataList << data
            }
        }
        return matchedDataList
    }




}
