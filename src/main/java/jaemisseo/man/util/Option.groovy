package jaemisseo.man.util

import jaemisseo.man.annotation.OptionKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.reflect.Field

/**
 * Created by sujkim on 2017-02-19.
 */
class Option<T> {

    static Logger logger = LoggerFactory.getLogger(this.getClass())



    /*************************
     * Clone
     *************************/
    T clone(){
        Option oldOption = this
        T clonedObject = this.class.newInstance().merge(oldOption)
        return clonedObject
    }

    T clone(Map andPutMap){
        return cloneAndPut(andPutMap)
    }

    T clone(Option newOption){
        return cloneAndMerge(newOption)
    }

    T cloneAndPut(Map andPutMap){
        T clonedObject = this.clone()
        (clonedObject as Option).put(andPutMap)
        return clonedObject
    }

    T cloneAndMerge(Option newOption){
        T clonedObject = this.clone()
        (clonedObject as Option).merge(newOption)
        return clonedObject
    }



    /*************************
     * Put
     *************************/
    T put(Map filedValueMap){
        return put(filedValueMap, this)
    }



    /*************************
     * Merge
     *************************/
    T merge(Option newOption){
        Option oldOption = this
        logger.trace "Trying to merge..."
        oldOption.eachFieldName{ String fieldName ->
            try{
                oldOption[fieldName] = (newOption.hasProperty(fieldName) && oldOption.hasProperty(fieldName) && newOption[fieldName] != null && newOption[fieldName] != '') ? newOption[fieldName] : oldOption[fieldName]
            }catch(ReadOnlyPropertyException rope){
            }
        }
        return this
    }



    /*************************
     * List
     *************************/
    List<String> makeFieldNameList(){
        return Option.makeFieldNameList(this)
    }

    List<String> makePropertyNameList(){
        return makePropertyNameList(this)
    }

    List<String> makeFieldNameList(List validKeyList){
        return Option.makeFieldNameList(this)
    }

    List<String> makePropertyNameList(List validKeyList){
        return makePropertyNameList(this, validKeyList)
    }

    List<String> makePropertyNameList(List validKeyList, String prefix){
        return makePropertyNameList(this, validKeyList, prefix)
    }



    /*************************
     * Each
     *************************/
    List<String> eachFieldName(Closure closure){
        return Option.eachFieldName(this, closure)
    }

    List<String> eachProperty(Closure closure){
        return eachProperty(this, closure)
    }

    List<String> eachProperty(List validKeyList, Closure closure){
        return eachProperty(this, validKeyList, closure)
    }

    List<String> eachProperty(List validKeyList, String prefix, Closure closure){
        return eachProperty(this, validKeyList, prefix, closure)
    }



    /*************************
     * Get
     *************************/
    Object get(String propertyName){
        Map<String, Object> optionMap = eachProperty(this)
        return optionMap[propertyName]
    }



    /*************************
     * Has
     *************************/
    boolean hasFieldName(String fieldName){
        return !!properties.hasProperty(fieldName)
    }

    boolean isDefaultType(){
        return false
    }



    /****************************************************************************************************
     *
     *  STATIC
     *
     ****************************************************************************************************/
    /*************************
     * EACH FIELDS
     *************************/
    static List<Object> makeFieldNameList(Object object){
        List list = (object as Option).properties.keySet().toList()
        list -= ['class']
        return list
    }

    static List<Object> eachFieldName(Object object, Closure closure){
        return Option.makeFieldNameList(object).each{ String fieldName ->
            closure(fieldName)
        }
    }

    /*************************
     * EACH PROPERTIES
     *************************/
    static Map<String, Object> eachProperty(Object option){
        return Option.eachProperty(option, null)
    }

    static Map<String, Object> eachProperty(Object option, Closure closure){
        return Option.eachProperty(option, null, closure)
    }

    static Map<String, Object> eachProperty(Object option, List<String> validKeyList, Closure closure){
        return Option.eachProperty(option, validKeyList, null, closure)
    }

    static Map<String, Object> eachProperty(Object option, List<String> validKeyList, String prefix, Closure closure){
        Map<String, Object> optionMap = [:]
        if (option instanceof Map){
            option.each{ String key, Object value ->
                if (value instanceof Map){
                    optionMap.putAll( Option.eachProperty(value, validKeyList, prefix, closure) )
                }else{
                    if (giveValues(key, value, validKeyList, prefix, closure))
                        optionMap.put(key, value)
                }
            }

        }else if (option instanceof Object){
            option.getClass().getDeclaredFields().each{ Field field ->
                OptionKey ant = field.getAnnotation(OptionKey)
                if (ant){
                    field.accessible = true
                    //Value
                    Object value = option[field.name]
                    //Key
                    String key = ant.value() ?: ''
                    List<String> localValidKeyList = (ant.validKeyList()) ? ant.validKeyList()?.toList() : validKeyList
                    if (key){
                        if (giveValues(key, value, localValidKeyList, prefix, closure))
                            optionMap.put(key, value)
                    }else{
                        optionMap.putAll( eachProperty(value, localValidKeyList, prefix, closure) )
                    }
                }
            }
        }
        return optionMap
    }

    static boolean giveValues(String key, Object value, List<String> validKeyList, String prefix, Closure closure){
        //- Check and Remove prefix
        if (prefix){
            if (key.startsWith(prefix))
                key = key.substring(prefix.length())
            else
                return false
        }
        //- inject matching property's value
        if (isMatching(key, validKeyList)){
            if (closure)
                closure(key, value)
            return true
        }
        return false
    }

    /*************************
     * PUT (COPY)
     *************************/
    static Object put(def from, Object to){
        Object completedObject = (to instanceof Class) ? to.newInstance() : to

        if (!from){
            throw new NullPointerException()

        }else if (from instanceof Map){
            completedObject = to
            from.each{ String filedNameToChange, def value ->
                try{
                    completedObject[filedNameToChange] = value
                }catch(e){
                }
            }
            return completedObject

        }else if (from instanceof List){
            List<Object> resultList = []
            from.each{ Map fromObject ->
                def toObject = completedObject.class.newInstance()
                fromObject.each{ String filedNameToChange, def value ->
                    try{
                        toObject[filedNameToChange] = (isDefaultType(value)) ? value : value.class.newInstance()
                    }catch(e){
                    }
                }
                resultList << toObject
            }
            return resultList

        }else if (from instanceof Object){
            completedObject = to
            eachFieldName(from){ String filedNameToChange ->
                try{
                    completedObject[filedNameToChange] = from[filedNameToChange]
                }catch(e){
                }
            }
            return completedObject
        }
    }

    /*************************
     * SIZE
     *************************/
    static int size(Object option){
        return eachProperty(option).size()
    }

    /*************************
     * PropertyNameList
     *************************/
    static List<String> makePropertyNameList(Object option){
        return eachProperty(option){ String key, Object value -> }.collect{ return it.key }
    }

    static List<String> makePropertyNameList(Object option, List<String> validKeyList){
        return eachProperty(option, validKeyList){ String key, Object value -> }.collect{ return it.key }
    }

    static List<String> makePropertyNameList(Object option, List<String> validKeyList, String prefix){
        return eachProperty(option, validKeyList, prefix){ String key, Object value -> }.collect{ return it.key }
    }

    /*************************
     * INJECT
     *************************/
    static Object inject(Object to, Object from){
        return inject(to, from, null)
    }

    static Object inject(Object to, Object from, List<String> validKeyList){
        return inject(to, from, validKeyList, null)
    }

    static Object inject(Object to, Object from, List<String> validKeyList, String prefix){
        eachProperty(from, validKeyList, prefix){ String key, Object value ->
            injectValueTo(to, key, value)
        }
        return to
    }

    static boolean injectValueTo(Object to, String key, Object value){
        if (to instanceof Map){
            to[key] = value
            return true

        }else if (to instanceof Object){
            return to.getClass().getDeclaredFields().any{ Field field ->
                OptionKey ant = field.getAnnotation(OptionKey)
                if (ant){
                    field.accessible = true
                    String targetKey = ant.value() ?: ''
                    List<String> localValidKeyList = (ant.validKeyList()) ? ant.validKeyList()?.toList() : []
                    if (targetKey){
                        if (targetKey == key){
                            to[field.name] = getValueByType(value, field.type)
                            return true
                        }
                    }else{
                        if (!localValidKeyList || isMatching(key, localValidKeyList))
                            return injectValueTo(to[field.name], key, value)
                    }
                }
                return false
            }
        }
        return false
    }

    static Object getValueByType(Object value, Class type){
        if (value.getClass() == type)
            return value

        //Default Type
        switch (type){
            case char: return new Character(value); break;
            case int: return new Integer(value?:0); break;
            case short: return new Short(value?:0); break;
            case double: return new Double(value?:0); break;
            case long: return new Long(value?:0); break;
            case boolean: return new Boolean(value?:false); break;
            case float: return new Float(value?:0); break;
        }

        //Wrap Type
        if ( value == null || (type != String && value == '') )
            return null

        switch (type){
            case Character: return new Character(value); break;
            case String: return new String(value); break;
            case Integer: return new Integer(value); break;
            case Short: return new Short(value); break;
            case Double: return new Double(value); break;
            case Long: return new Long(value); break;
            case Boolean: return new Boolean(value); break;
            case Float: return new Float(value); break;
        }
        return value
    }

    static boolean isMatching(String name, List<String> rangeList){
        return !rangeList || rangeList.any{ String range ->
            isMatching(name, range)
        }
    }

    static boolean isMatching(String name, String range){
        String regexpStr = range.replace('(', '\\(').replace(')', '\\)')
                                .replace('[', '\\[').replace(']', '\\]')
                                .replace('.', '\\.').replace('$', '\\$')
                                .replace('*',"[^\\/\\\\]*")
                                .replace('[^\\/\\\\]*[^\\/\\\\]*',"\\S*")
        return name.matches(regexpStr)
    }

}
