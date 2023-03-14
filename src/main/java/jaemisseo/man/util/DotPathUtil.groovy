package jaemisseo.man.util

import javax.management.modelmbean.InvalidTargetObjectTypeException
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.security.spec.InvalidParameterSpecException
import java.util.stream.Collectors

public class DotPathUtil {

    public static List<String> findFlattenKeys(Object object, String pattern){
        List<String> result = [];
        Object value = object;

        if (value == null) {
            //None
        }else if (value instanceof Map){
            Set<String> keys = value.keySet()
            for (String key : keys){
                if (isMatchingProperty(key, pattern)){
                    result.add(key);
                }
            }
        }else if (value instanceof List){
            //..
        }else{
//            value = value[fieldPart];
        }

        return result;
    }

    //TODO: Implements..
    public static List<String> findKeys(Object object, String pattern){
        List<String> result = [];
        Object value = object;

        if (value == null) {
            //None
        }else if (value instanceof Map){
            Set<String> keys = value.keySet()
            for (String key : keys){
                if (isMatchingProperty(key, pattern)){
                    result.add(key);
                }
            }
        }else if (value instanceof List){
            //..
        }else{
//            value = value[fieldPart];
        }

        return result;
    }



    public static FlattenDotPathKeyHashMap<String, Object> convertHierarchicalMapToFlattenDotPathKeyMap(Map<String, Object> hierarchicalMap){
        FlattenDotPathKeyHashMap result = new FlattenDotPathKeyHashMap<String, Object>()
        return convertHierarchicalMapToFlattenDotPathKeyMap(hierarchicalMap, '', result);
    }

    public static FlattenDotPathKeyHashMap<String, Object> convertHierarchicalMapToFlattenDotPathKeyMap(Object object, String currentDotPath, FlattenDotPathKeyHashMap<String, Object> result){
        String nextDotPath = null;
        Object v = null;

        if (object instanceof Map){
            Set<String> keys = object.keySet()
            for (String key : keys) {
                nextDotPath = (currentDotPath) ? new StringBuilder().append(currentDotPath).append(".").append(key) : key
                v = object.get(key)
                convertHierarchicalMapToFlattenDotPathKeyMap(v, nextDotPath, result)
            }

        }else if (object instanceof List){
            for (int i=0; i<object.size(); i++){
                nextDotPath = new StringBuilder().append(currentDotPath).append("[").append(i).append("]").toString()
                v = object.get(i);
                convertHierarchicalMapToFlattenDotPathKeyMap(v, nextDotPath, result)
            }

        }else{
            result[currentDotPath] = object  //Groovy Swag ~
        }
        return result;
    }

    public static HierarchicalHashMap<String, Object> convertFlattenDotPathKeyMapToHierarchicalMap(FlattenDotPathKeyHashMap<String, Object> flattenDotPathMap){
        if (flattenDotPathMap == null)
            return null

        HierarchicalHashMap<String, Object> map = new HierarchicalHashMap<>();
        Set<String> keys = flattenDotPathMap.keySet()
        for (String k : keys){
            Object v = flattenDotPathMap.get(k);
            try{
                if (isInjectablePath(map, k, true)) {
                    injectExtendably(map, k, v)
                }

            }catch(Exception e){
                throw new InvalidParameterSpecException(e);
            }
        }

        return map;
    }

    public static HierarchicalHashMap<String, Object> convertFlattenDotPathKeyMapToHierarchicalMap(FlattenDotPathKeyHashMap<String, Object> flattenDotPathMap, String trimKey){
        HierarchicalHashMap<String, Object> map = convertFlattenDotPathKeyMapToHierarchicalMap(flattenDotPathMap)
        Map<String, Object> trimedMap = (Map<String, Object>) DotPathUtil.traverse(map, trimKey)
        if (trimedMap == null)
            return null

        HierarchicalHashMap<String, Object> trimedHierarchicalMap = new HierarchicalHashMap<>(trimedMap)
        return trimedHierarchicalMap;
    }





    /**************************************************
     * Extract a value by dot-path expression
     * @param object
     * @param dotPath
     * @return
     **************************************************/
    public static Object traverse(Object object, String dotPath){
        Object value = object;

        if (value == null)
            return value;

        int foundDotIndex = dotPath.indexOf(".");
        if (foundDotIndex != -1){
            String[] arr = dotPath.split("[.]");
            String fieldPart = null;
            for (int i=0; i<arr.length; i++){
                fieldPart = arr[i];
                if (value == null) {
                    break;
                }else if (value instanceof Map){
                    value = ((Map) value).get(fieldPart);
                }else if (value instanceof List){
                    //..
                }else{
                    value = value[fieldPart];
                }
            }

        }else{
//            value = ((Map) value).get(field);
            value = value[dotPath];
        }

        return value;
    }



    public static Object each(HierarchicalHashMap hMap, Closure closure){
        if (hMap == null)
            return null
        return eachInternal(null, hMap, "", closure)
    }

    private static Object eachInternal(Object parent, Object object, String currentDotPath, Closure closure){
        String nextDotPath = null;
        Object v = null;

        if (object instanceof Map){
            Set<String> keys = object.keySet()
            for (String key : keys) {
                nextDotPath = (currentDotPath) ? new StringBuilder().append(currentDotPath).append(".").append(key) : key
                v = object.get(key)
                each(object, v, nextDotPath, closure)
            }

        }else if (object instanceof List){
            for (int i=0; i<object.size(); i++){
                nextDotPath = new StringBuilder().append(currentDotPath).append("[").append(i).append("]").toString()
                v = object.get(i);
                each(object, v, nextDotPath, closure)
            }

        }else{
            closure(currentDotPath, object)
        }
        return object;
    }

    public static Object reduce(HierarchicalHashMap hMap, Closure closure){
        if (hMap == null)
            return null
        return reduceInternal(hMap, "", null, "", closure)
    }

    private static Object reduceInternal(Object object, String currentDotPath, Object parent, Object partKey, Closure closure){
        String nextDotPath = null;
        Object v = null;

        if (object instanceof Map){
            Set<String> keys = object.keySet()
            for (String key : keys) {
                nextDotPath = (currentDotPath) ? new StringBuilder().append(currentDotPath).append(".").append(key) : key
                v = object.get(key)
                reduceInternal(v, nextDotPath, object, key, closure)
            }

        }else if (object instanceof List){
            for (int i=0; i<object.size(); i++){
                nextDotPath = new StringBuilder().append(currentDotPath).append("[").append(i).append("]").toString()
                v = object.get(i);
                reduceInternal(v, nextDotPath, object, i, closure)
            }

        }else{
            Object reduced = closure(currentDotPath, object)
            parent[partKey] = reduced
        }
        return object;
    }




    /**************************************************
     * Extract a value by dot-path expression
     * @param object
     * @param dotPathPattern
     * @return
     **************************************************/
    public static FlattenDotPathKeyHashMap<String, Object> findAllFlattenEntries(Object object, String dotPathPattern){
        FlattenDotPathKeyHashMap<String, Object> flattenMap = (object instanceof FlattenDotPathKeyHashMap) ? object : convertHierarchicalMapToFlattenDotPathKeyMap(object);
        FlattenDotPathKeyHashMap<String, Object> founds = new FlattenDotPathKeyHashMap<>();

        Set<String> keys = flattenMap.keySet()
        for (String key : keys){
            if (!isMatchingProperty(key, dotPathPattern))
                continue;

            Object value = flattenMap.get(key)

            founds.put(key, value)
        }

        return founds;
    }

    /**************************************************
     *
     * Remove key
     *
     **************************************************/
    public static Object remove(Object object, String dotPath){
        Object value = object;
        Object beforeValue = value

        if (value == null)
            return value;

        String fieldPart = dotPath;
        int foundDotIndex = dotPath.indexOf(".");
        if (foundDotIndex != -1){
            String[] arr = dotPath.split("[.]");
            for (int i=0; i<arr.length; i++){
                beforeValue = value
                fieldPart = arr[i];
                if (value == null) {
                    break;
                }else if (value instanceof Map){
                    value = ((Map) value).get(fieldPart);
                }else if (value instanceof List){
                    //..
                }else{
                    value = value[fieldPart];
                }
            }

        }else{
//            value = ((Map) value).get(field);
            value = value[dotPath];
        }

        if (beforeValue){
            if (beforeValue instanceof Map){
                beforeValue.remove(fieldPart)
            }
        }

        return value;
    }

    public static FlattenDotPathKeyHashMap<String, Object> removeByDotPathPattern(Object object, String dotPathPattern){
        FlattenDotPathKeyHashMap<String, Object> entriesToRemove = findAllFlattenEntries(object, dotPathPattern);

        if (entriesToRemove == null || entriesToRemove.size() == 0)
            return entriesToRemove;

        //2. Remove key
        Set<String> keys = entriesToRemove.keySet();
        for (String key : keys){
            Object value = entriesToRemove.get(key);
            //- Do
            remove(object, key);
        }

        return entriesToRemove;
    }


    /**
     * Check a injectable dotPath on object
     * @param object
     * @param dotPath
     * @param extendable
     * @return
     */
    public static boolean isInjectablePath(Object object, String dotPath, boolean extendable){
        try{
            if (object == null)
                throw new InvalidTargetObjectTypeException()
            if (PrimitiveTypeUtil.isPrimitiveTypeOrStringOrBigDecimal(object))
                throw new InvalidTargetObjectTypeException()

            Object upperValue = null;
            Class upperClass = null;
            Object value = object;
            Object beforeValue = value;
            String fieldPartName = null;

            //1. Separate path by dot(.)
            String[] dotPathParts = dotPath.split("[.]");
            int lastIndex = dotPathParts.length -1;

            //2. Loop by separated part-names array
            for (int i=0; i<dotPathParts.length; i++){
                fieldPartName = dotPathParts[i];
                upperValue = value
                upperClass = upperValue.getClass()

                int indexOfOpenBrace = fieldPartName.indexOf("[")
                if (indexOfOpenBrace != -1){
                    //2-1. Check Child - List
                    String listFieldPartName = fieldPartName.substring(0, indexOfOpenBrace)
                    beforeValue = value = extractChildObject(upperValue, upperClass, listFieldPartName)
//                    value = checkExtendableAndCreateChildObject(dotPath, fieldPartName, i, lastIndex, value, upperClass, extendable)
                    if (value == null || !(value instanceof List)){
                        value = new ArrayList();
                    }

                    //2-2. Check Child - List<Item>
                    upperValue = value
                    upperClass = upperValue.getClass()
                    String indexFieldPartName = fieldPartName.substring(indexOfOpenBrace +1, fieldPartName.length() -1)
                    beforeValue = value = extractChildObject(upperValue, upperClass, indexFieldPartName)
                    value = checkExtendableAndCreateChildObject(dotPath, indexFieldPartName, i, lastIndex, value, upperClass, extendable)

                    fieldPartName = indexFieldPartName
                }else{
                    //2-1. Check Child
                    beforeValue = value = extractChildObject(upperValue, upperClass, fieldPartName)
                    value = checkExtendableAndCreateChildObject(dotPath, fieldPartName, i, lastIndex, value, upperClass, extendable)
                }
            }

            //3. Check injactable
            try {
                if (checkInjactable(upperClass, fieldPartName)) {
                    //..
                }
            }catch(NoSuchFieldException | MissingPropertyException | InvalidTargetObjectTypeException e){
                throw new NotInjectableException( "Target dotPath(" +dotPath+ ") is not injactable path.  ==> Check point " +lastIndex+ "(partName: " +fieldPartName+ ")", e )
            }

        }catch(NotExtendableException | NotInjectableException e){
            return false
        }

        return true
    }

    private static Object checkExtendableAndCreateChildObject(String dotPath, String fieldPartName, int i, int lastIndex, Object value, Class upperClass, boolean extendable){
        Object nextObject = value;
        if (value == null && lastIndex != i){
            if (!extendable)
                throw new NotExtendableException( "Target dotPath(" +dotPath+ ") is not extendable.  ==> Check point " +i+ "(partName: " +fieldPartName+ ")" )
            try {
                nextObject = createChildObject(value, upperClass, fieldPartName)
            }catch(NoSuchFieldException | MissingPropertyException | InvalidTargetObjectTypeException e){
                throw new NotExtendableException( "Target dotPath(" +dotPath+ ") is not extendable.  ==> Check point " +i+ "(partName: " +fieldPartName+ ")", e )
            }
        }

        return nextObject;
    }


    /**
     * Injecting a value by dot-path expression
     * @param object
     * @param dotPath
     * @param valueToInject
     * @throws InvalidTargetObjectTypeException
     */
    public static void inject(Object object, String dotPath, Object valueToInject) throws NotInjectableException, NotExtendableException, InvalidTargetObjectTypeException{
        inject(object, dotPath, valueToInject, false)
    }

    public static void injectExtendably(Object object, String dotPath, Object valueToInject) throws NotInjectableException, NotExtendableException, InvalidTargetObjectTypeException{
        inject(object, dotPath, valueToInject, true)
    }

    public static void inject(Object object, String dotPath, Object valueToInject, boolean extendable) throws NotInjectableException, NotExtendableException, InvalidTargetObjectTypeException{
        if (object == null)
            throw new InvalidTargetObjectTypeException()
        if (PrimitiveTypeUtil.isPrimitiveTypeOrStringOrBigDecimal(object))
            throw new InvalidTargetObjectTypeException()

        Object upperValue = null;
        Class upperClass = null;
        Object value = object;
        Object beforeValue = value;
        String fieldPartName = null;

        //1. Separate path by dot(.)
        String[] dotPathParts = dotPath.split("[.]");
        int lastIndex = dotPathParts.length -1;

        //2. Loop by separated part-names array
        for (int i=0; i<dotPathParts.length; i++){
            fieldPartName = dotPathParts[i];
            upperValue = value
            upperClass = upperValue.getClass()

            int indexOfOpenBrace = fieldPartName.indexOf("[")
            if (indexOfOpenBrace != -1){
                //2-1. Check Child - List
                String listFieldPartName = fieldPartName.substring(0, indexOfOpenBrace)
                beforeValue = value = extractChildObject(upperValue, upperClass, listFieldPartName)
//                value = checkExtendableAndCreateChildObject(dotPath, fieldPartName, i, lastIndex, value, upperClass, extendable)
                if (value == null || !(value instanceof List)){
                    value = new ArrayList();
                }
                injectToUpperObject(upperValue, listFieldPartName, value)

                //2-2. Check Child - List<Item>
                upperValue = value
                upperClass = upperValue.getClass()
                String indexFieldPartName = fieldPartName.substring(indexOfOpenBrace +1, fieldPartName.length() -1)
                beforeValue = value = extractChildObject(upperValue, upperClass, indexFieldPartName)
                value = checkExtendableAndCreateChildObject(dotPath, indexFieldPartName, i, lastIndex, value, upperClass, extendable)

                fieldPartName = indexFieldPartName
            }else{
                //2-1. Check Child
                beforeValue = value = extractChildObject(upperValue, upperClass, fieldPartName)
                value = checkExtendableAndCreateChildObject(dotPath, fieldPartName, i, lastIndex, value, upperClass, extendable)
            }

            //3. Inject extended object
            if (beforeValue == null && lastIndex != i)
                injectToUpperObject(upperValue, fieldPartName, value)
        }

        //3. Inject value
        try{
            if (checkInjactable(upperClass, fieldPartName)){
                injectToUpperObject(upperValue, fieldPartName, valueToInject)
            }
        }catch(NoSuchFieldException | MissingPropertyException | InvalidTargetObjectTypeException e){
            String message = "Target dotPath(" +dotPath+ ") is not injactable path.  ==> Check point " +lastIndex+ "(partName: " +fieldPartName+ ")"
            throw new NotInjectableException(message, e)
        }
    }

    private static void injectToUpperObject(Object upperValue, String fieldPartName, Object value){
        if (upperValue instanceof List){
            int listIndex = Integer.parseInt(fieldPartName);
            upperValue[listIndex] = value
        }else{
            upperValue[fieldPartName] = value
        }
    }

    private static Object extractChildObject(Object upperValue, Class upperClass, String fieldPartName){
        Object value = null
        if (upperValue instanceof Map){
            value = ((Map) upperValue).get(fieldPartName);
        }else if (upperValue instanceof List){
            //TODO: field[n] Expression ..?
            int index = Integer.parseInt(fieldPartName);
            if (upperValue.size() -1 < index){
                for (int i=upperValue.size(); i<index+1; i++){
                    upperValue.add(i, null)
                }
            }
            value = ((List)upperValue).get(index);
        }else if (PrimitiveTypeUtil.isPrimitiveTypeOrStringOrBigDecimal(upperValue)){
            //Maybe last dotpath part
        }else{
            try{
                value = upperValue[fieldPartName] //- Groovy Swag
            }catch(MissingPropertyException mpe){
                value = null
            }catch(Exception e){
                value = null
            }
        }
        return value
    }

    private static Object createChildObject(Object value, Class upperClass, String fieldPartName) throws NoSuchFieldException, MissingPropertyException, InvalidTargetObjectTypeException {
        if (Map.class.isAssignableFrom(upperClass)){
            Map newObject = new HashMap();
            value = newObject

        }else if (List.class.isAssignableFrom(upperClass)){
            Map newObject = new HashMap(); //TODO: List 안에 List 인 구조에서는 어떻게 핸들링해서 넣어줄지 고민필요.
            value = newObject

        }else if (PrimitiveTypeUtil.isPrimitiveTypeOrStringOrBigDecimal(upperClass)) {
            throw new InvalidTargetObjectTypeException()

        }else{
            Field field = findField(upperClass, fieldPartName) //No field ==> Throw NoSuchFieldException
//            Field field = upperClass.getDeclaredField(fieldPartName) //No field ==> Throw NoSuchFieldException
            Class type = field.getType()
            if (PrimitiveTypeUtil.isPrimitiveTypeOrStringOrBigDecimal(type)){
                throw new InvalidTargetObjectTypeException()
            }else{
                Object newObject = type.newInstance()
                value = newObject
            }
        }
        return value
    }

    public static Field findField(Class clazz, String fieldName) throws NoSuchFieldException {
        if (clazz == null) {
            throw new NoSuchFieldException()
        }

        Field f = null;

        try{
            f = clazz.getDeclaredField(fieldName)
//            if (Modifier.isPublic(f.getModifiers()) || Modifier.isProtected(f.getModifiers())){
//            }else{
//                f = null
//            }
        }catch(NoSuchFieldException nsfe){
            //Ignore
        }

        if (f == null){
            f = findField(clazz.getSuperclass(), fieldName);
        }

        return f;
    }

    List<Field> getAllFields(Class clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields())
                .filter({ f ->
                    Modifier.isPublic(f.getModifiers()) || Modifier.isProtected(f.getModifiers())
                })
                .collect(Collectors.toList());
        result.addAll(filteredFields);
        return result;
    }

    private static boolean checkInjactable(Class upperClass, String fieldPartName) throws InvalidTargetObjectTypeException, NoSuchFieldException{
        if (Map.class.isAssignableFrom(upperClass)){
            //Injactable
        }else if (List.class.isAssignableFrom(upperClass) && fieldPartName.isNumber()){
            //Injactable

        }else if (PrimitiveTypeUtil.isPrimitiveTypeOrStringOrBigDecimal(upperClass)){
            //No Injactable
            throw new InvalidTargetObjectTypeException()

        }else{
            Field field = upperClass.getDeclaredField(fieldPartName) //No field ==> Throw NoSuchFieldException
            //Injactable
        }
        return true
    }

    private static boolean isMatchingProperty(String propertyName, String propertyRange){
        String regexpStr = toSlash(propertyRange)
                .replace('(', '\\(').replace(')', '\\)')
                .replace('[', '\\[').replace(']', '\\]')
                .replace('.', '\\.').replace('$', '\\$')
                .replace('*',"[^.]*")
                .replace('[^.]*[^.]*',"\\S*")
        return propertyName.replace('\\', '/').matches(regexpStr)
    }

    private static toSlash(String path){
        return path?.replaceAll(/[\/\\]+/, '/')
    }



    /**************************************************
     *
     * extract last object
     *
     **************************************************/
    public static Object getLastSubObject(Object document, String fullFieldName){
        Object lastSubObject = document;
        int foundDotIndex = fullFieldName.indexOf(".")
        if (foundDotIndex != -1){
            String[] arr = fullFieldName.split("[.]")
            int size = arr.length;
            int lastSubFieldIndex = size -1;
            int lastSubObjectIndex = lastSubFieldIndex -1;
            (0..lastSubObjectIndex).every{ i ->
                String fieldName = arr[i]
                if (lastSubObject instanceof Map){
                    lastSubObject = lastSubObject.get(fieldName)
                }else if (lastSubObject instanceof List){
                    lastSubObject.each{
                        //None
                    }
                    return null //TODO: List 있으면 일단  강조는 하지 말자! 나중에 하자 Recursive하게 처리해야할듯 자료구조들이 경우가 많네~ 으~~
                }
                (lastSubObject != null)
            }
        }

        if (lastSubObject instanceof List)
            return null;

        return lastSubObject;
    }

    public static String getLastSubObject(String fieldName){

    }

    /**************************************************
     *
     * extact last field Name
     *
     **************************************************/
    public static String getLastSubFieldName(String fieldName){
        String lastSubFieldName = null;
        int foundDotIndex = fieldName.indexOf(".")
        if (foundDotIndex != -1){
            String[] arr = fieldName.split("[.]")
            int size = arr.length;
            lastSubFieldName = arr[size-1].join(".")
        }else{
            lastSubFieldName = fieldName;
        }
        return lastSubFieldName;
    }



    public static class NotExtendableException extends Exception {
        public NotExtendableException(){ super(); }
        public NotExtendableException(Throwable cause){ super(cause); }
        public NotExtendableException(String message) { super(message); }
        public NotExtendableException(String message, Throwable cause) { super(message, cause); }
    }

    public static class NotInjectableException extends Exception {
        public NotInjectableException(){ super(); }
        public NotInjectableException(Throwable cause){ super(cause); }
        public NotInjectableException(String message) { super(message); }
        public NotInjectableException(String message, Throwable cause) { super(message, cause); }
    }

}
