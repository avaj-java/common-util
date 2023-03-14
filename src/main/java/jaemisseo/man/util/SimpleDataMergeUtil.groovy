package jaemisseo.man.util

class SimpleDataMergeUtil {


    /**
     * Merge Not-Null-Value
     * @param batchOptions: The lower the index number, the higher the priority.
     * @return
     */
    public static <T> T merge(T... data){
        if (data == null || data.length == 0)
            return new NullPointerException()

        T mergedData = data[0].getClass().newInstance()

        Option.eachFieldName(mergedData){ fieldName ->
            try{
                Object[] targetValues = (data*."$fieldName").toArray()
                Object newValue = mergeValues( targetValues )
                mergedData[fieldName] = newValue
            }catch(e){
                //Ignore
            }
        }
        return mergedData
    }

    public static <T> T mergeValues(T... data){
        MergeResolver resolver = new SimpleDataMergeResolver()
        return mergeValues(resolver, data)
    }

    public static <T> T mergeValues(MergeResolver resolver, T... data){
        Object mergedValue = null
        for (int i=data.length -1; i>-1; i--){
            Object v = data[i];
            if (v != null)
                mergedValue = resolver.resolve(mergedValue, v)
        }
        return mergedValue
    }

    public interface MergeResolver {
        Object resolve(Object mergedData, Object data);
        Object resolveDefault(Object mergedData, Object data);
        Map resolveMap(Object mergedData, Object data);
        List resolveList(Object mergedData, Object data);
    }

    public static class SimpleDataMergeResolver implements MergeResolver {

        private static Map<String, Object> toMap(Object data){
            if (data == null){
                return new HashMap<String, Object>()
            }else if (data instanceof Map){
                return data
            }else if (data instanceof List){
                int count = 0;
                Map<String, Object> map = data.collectEntries{ ["${++count}".toString(), it] }
                return map
            }
            return ["0": data] //?
        }

        private static List<Object> toList(Object data){
            if (data == null){
                return new ArrayList<Object>()
            }else if (data instanceof Map){
                List list = new ArrayList<Object>(data.values());
                return list
            }else if (data instanceof List){
                return data
            }
            return [data] //?
        }

        @Override
        public Object resolve(Object mergedData, Object data){
            if (data instanceof Map){
                return resolveMap(mergedData, data)
            }else if (data instanceof List){
                return resolveList(mergedData, data)
            }
            return resolveDefault(mergedData, data)
        }

        @Override
        public Object resolveDefault(Object mergedData, Object data) {
            return data
        }

        @Override
        public Map resolveMap(Object mergedData, Object data) {
            HashMap<String, Object> mergedMap = null
            try{
                mergedMap = toMap(mergedData)
                Object mergedValue = null
                data.each { k, v ->
                    mergedValue = resolve(mergedMap[k], v)
                    mergedMap.put(k, mergedValue)
                }
            }catch(e){
                e.printStackTrace()
            }

            return mergedMap
        }

        @Override
        public List resolveList(Object mergedData, Object data) {
            ArrayList<Object> mergeList = null;
            try{
                mergeList = toList(mergedData)
                Object mergedValue = null
                data.eachWithIndex{ v, i ->
                    mergedValue = resolve(mergeList[i], v)
                    if (mergeList.size() > i){
                        mergeList.set(i, mergedValue)
                    }else{
                        int gap = mergeList.size()-1 -i;
                        if (gap > 1){
                            (0..gap).each{
                                mergeList.add(null)
                            }
                        }
                        mergeList.add(mergedValue)
                    }
                }
            }catch(e){
                e.printStackTrace()
            }

            return mergeList
        }
    }


}
