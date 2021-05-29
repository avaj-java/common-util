package jaemisseo.man.util

import groovy.json.JsonSlurper
import java.text.SimpleDateFormat

class SimpleDataUtil {

    static final String TYPE_JSON = "JSON";
    static final String TYPE_SIMPLEJSON = "SIMPLEJSON"; //Created by souljungkim.
    static final String TYPE_OBJECT = "OBJECT";
    static final String TYPE_ARRAY = "ARRAY";
    static final String TYPE_BOOLEAN = "BOOLEAN";
    static final String TYPE_STRING= "STRING";
    static final String TYPE_NUMBER = "NUMBER";
    static final String TYPE_NONE = "NONE";

    static Object parseValue(Object value){
        return parseValue(value, TYPE_NONE)
    }

    static Object parseValue(Object value, String valueType){
        switch (valueType){
            case TYPE_JSON:         return SimpleDataUtil.parseJsonObjectExpression(value); break;
            case TYPE_SIMPLEJSON:   return SimpleDataUtil.parseSimpleObjectExpression(value); break;
            case TYPE_OBJECT:       return SimpleDataUtil.parseSimpleObjectExpression(value); break;
            case TYPE_ARRAY:        return SimpleDataUtil.parseSimpleArrayExpression(value); break;
            case TYPE_BOOLEAN:      return SimpleDataUtil.toBooleanWith(value); break;
            case TYPE_STRING:       return SimpleDataUtil.parseString(value); break;
            case TYPE_NUMBER:       return SimpleDataUtil.parseLong(value); break;
            default:                return value; break;
        }
        return value
    }



    /**************************************************
     *
     * JsonObject
     *
     **************************************************/
    static Map<String, Object> parseJsonObjectExpression(String text){
        return new JsonSlurper().parseText(text)
    }


    /**************************************************
     *
     * SimpleArray
     *
     **************************************************/
    static List<Object> parseSimpleArrayExpression(String value, Object nvl){
        return parseSimpleArrayExpression(value) ?: nvl
    }

    static List<Object> parseSimpleArrayExpression(String value){
        Object result
        if (!value)
            return null
        if (value.startsWith('[') && value.endsWith(']')){
            if (value.length() == 2)
                return []
            value = value.substring(1, value.length() - 1)
        }

        result = splitSimpleObject(value).collect{ makeValue(it) }
        return result
    }



    /**************************************************
     *
     * SimpleObject
     *
     **************************************************/
    static final char CHAR_BLACK_SLASH = (char)"\\"
    static final char CHAR_COMMA = (char)","
    static final char CHAR_BRACE_OPEN = (char)"("
    static final char CHAR_CURLY_BRACE_OPEN = (char)"{"
    static final char CHAR_BRACKET_OPEN = (char)"["
    static final char CHAR_BRACE_CLOSE = (char)")"
    static final char CHAR_CURLY_BRACE_CLOSE = (char)"}"
    static final char CHAR_BRACKET_CLOSE = (char)"]"
    static final char CHAR_SINGLE_QUOTE = (char)"'"
    static final char CHAR_DOUBLE_QUOTE = (char)"\""
    static final char CHAR_COMMENT_A1 = (char)"/"
    static final char CHAR_COMMENT_A2 = (char)"*"

    static Object parseSimpleObjectExpression(String value, Object nvl){
        return parseSimpleObjectExpression(value) ?: nvl
    }

    static Object parseSimpleObjectExpression(String value){
        Object result
        if (value){
            if (value.startsWith('[') && value.endsWith(']')){
                if (value.length() == 2)
                    return []
                value = value.substring(1, value.length() - 1)
                result = splitSimpleObject(value).collect{ makeValue(it) }
            }else if (value.startsWith('{') && value.endsWith('}')){
                if (value.length() == 2)
                    return [:]
                value = value.substring(1, value.length() - 1)
                result = splitSimpleObject(value).collectEntries{makeEntry(it) }
            }else{
                result = generateEntries(value)
            }
        }
        return result
    }

    static Map<String, Object> generateEntries(String simpleObjectExpression){
        Map<String, Object> entries = [:]
        if (simpleObjectExpression){
            def entryArray = splitSimpleObject(simpleObjectExpression)
            entryArray?.each{ entryString ->
                AbstractMap.SimpleEntry entry = makeEntry(entryString)
                consistEntry(
                        entries,
                        entry.key,
                        makeValue(entry.value)
                )
            }
        }
        return entries
    }

    static List<String> splitSimpleObject(String simpleObjectExpression){
        List<String> splitedList = []
        StringBuilder sb = new StringBuilder()
        simpleObjectExpression = simpleObjectExpression?.trim()
        char[] chars = simpleObjectExpression.toCharArray()
        List<Integer> commaIndexList = []
        char charBefore, currentQuote
        boolean statusCommenting = false
        boolean statusSeperating = false
        int brace = 0, curlyBrace = 0, bracket = 0
        chars.eachWithIndex{ c, i ->
            if (!charBefore.equals(CHAR_BLACK_SLASH)){
                if (currentQuote){
                    currentQuote = (currentQuote.equals(c)) ? null : currentQuote

                }else if (charBefore.equals(CHAR_COMMENT_A1) && c.equals(CHAR_COMMENT_A2)){
                    statusCommenting = true
                    sb.deleteCharAt(sb.length() -1)

                }else if (statusCommenting){


                }else{
                    switch (c){
                        case CHAR_BRACE_OPEN: ++brace; break;
                        case CHAR_BRACE_CLOSE: --brace; break;
                        case CHAR_CURLY_BRACE_OPEN: ++curlyBrace; break;
                        case CHAR_CURLY_BRACE_CLOSE: --curlyBrace; break;
                        case CHAR_BRACKET_OPEN: ++bracket; break;
                        case CHAR_BRACKET_CLOSE: --bracket; break;
                        case CHAR_SINGLE_QUOTE: currentQuote = CHAR_SINGLE_QUOTE; break;
                        case CHAR_DOUBLE_QUOTE: currentQuote = CHAR_DOUBLE_QUOTE; break;
                        default:
                            //Right separator
                            statusSeperating = c.equals(CHAR_COMMA) && !brace && !curlyBrace && !bracket
                            if (statusSeperating)
                                commaIndexList << i
                            break;
                    }
                }
            }

            if (statusSeperating){
                String item = sb.toString().trim()
                if (item)
                    splitedList << item
                sb = new StringBuilder()
                statusSeperating = false

            }else if (statusCommenting){
                if (charBefore.equals(CHAR_COMMENT_A2) && c.equals(CHAR_COMMENT_A1))
                    statusCommenting = false

            }else{
                sb.append(c)
            }
            charBefore = c
        }

        //Last Item
        String item = sb.toString().trim()
        if (item)
            splitedList << item

        //Split
        splitedList = splitedList.findAll{ !it.isEmpty() }
        return splitedList
    }

    static AbstractMap.SimpleEntry makeEntry(String entryString){
        Integer firstColonIndex = entryString?.indexOf(':')
        String key
        Object value
        if (firstColonIndex != -1){
            key = entryString?.substring(0, firstColonIndex)?.trim()
            value = entryString?.substring(firstColonIndex +1, entryString.length())?.trim()
            value = makeValue(value)
        }else{
            key = entryString
            value = true
        }

        boolean statusQuotedString = checkStatusQuotedString(key)
        if (statusQuotedString){
            key = extractValueFromQuote(key)
        }

        return new AbstractMap.SimpleEntry<String, Object>(key, value)
    }

    static Object makeValue(Object value){
        if (value instanceof String){
            //- trim() line-separators, spaces
            value = value.replaceAll('^\\s*', '').replaceAll('\\s*$', '')
            //- remove comment
//            if (value.startsWith("/*") || value.endsWith("*/"))
//                value.replaceAll('^[/][*].*[*][/]', '').replaceAll('[/][*].*[*][/]$', '')
            //- Make some object
            if (value.startsWith('[') && value.endsWith(']')){
                //- Check []
                if (value.length() == 2)
                    return []
                value = value.substring(1, value.length() - 1)
                value = splitSimpleObject(value).collect{ makeValue(it) }
            }else if (value.startsWith('{') && value.endsWith('}')){
                //- Check {}
                if (value.length() == 2)
                    return [:]
                value = value.substring(1, value.length() - 1)
                value = splitSimpleObject(value).collectEntries{makeEntry(it)  }
            }else{
                if (value.isNumber()) {
                    value = new BigDecimal(value.toString().trim())
                }else if ("null".equals(value)){
                    value = null
                }else if ("true".equals(value)){
                    value = true
                }else if ("false".equals(value)){
                    value = false
                }else{
                    //TODO: Range 처리 적용
                    boolean statusQuotedString = checkStatusQuotedString(value)
                    if (statusQuotedString){
                        value = extractValueFromQuote(value)
                    }else{
//                        value = resolveRangeExpression(value)
                    }
                }
            }
        }
        return value
    }

    static boolean checkStatusQuotedString(String quoteValue){
        return (
                (
                        quoteValue.startsWith(CHAR_DOUBLE_QUOTE.toString()) && quoteValue.endsWith(CHAR_DOUBLE_QUOTE.toString())
                )
                ||
                (
                        quoteValue.startsWith(CHAR_SINGLE_QUOTE.toString()) && quoteValue.endsWith(CHAR_SINGLE_QUOTE.toString())
                )
        )
    }

    static String extractValueFromQuote(String quoteValue){
        return quoteValue?.substring(1, quoteValue.length() -1)
    }

    static Object consistEntry(Map<String, Object> entries, String key, Object value){
        int firstDotIndex = key.indexOf('.')
        if (firstDotIndex > 0){
            String parentFieldName = key.substring(0, firstDotIndex)
            String subFieldName = key.substring(firstDotIndex +1, key.length())
            if (!entries[parentFieldName])
                entries[parentFieldName] = [:]
            consistEntry(entries[parentFieldName], subFieldName, value)
        }else{
            entries[key] = value
        }
    }



    //params is like LinkedMultiValueMap
    static Map<String, Object> convertToMap(Map<String, List<Object>> params){
        return (params as Map).collectEntries{ String key, List value ->
            [
                    key,
                    (value.size() > 1) ? value : value.size() == 1 ? value[0] : null
            ]
        }
    }

    static Map<String, Object> parsePropertiesKeyMap(Map<String, Object> map){
        Map<String, Object> newMap = [:]
        map.each{ key, val ->
            consistEntry(newMap, key, val)
        }
        return newMap
    }


    static String toUpperFirst(String text){
        int length = text.length()
        return (0 < length) ? text.substring(0, 1).toUpperCase() << text.substring(1, length) : text
    }



    /**************************************************
     *
     * Range
     *
     **************************************************/
    static List<Object> resolveStartAndEndListFromRangeExpression(String rangeExpression){
        if (rangeExpression.contains('-')) {
            return rangeExpression.split('[-]')
        }else if (rangeExpression.contains('..')){
            return rangeExpression.split('[.][.]')
        }
        return null
    }

    static List<Object> resolveRangeExpression(String rangeExpression){
        List<String> resultList
        if (rangeExpression.contains('-')) {
            resultList = resolveDashRangeExpression(rangeExpression as String)
        }else if (rangeExpression.contains('..')){
            resultList = resolveDotDotRangeExpression(rangeExpression as String)
        }
        return resultList
    }

    static List<String> resolveDashRangeExpression(String dashRnage){
        List<String> resultList = []
        List<String> split = dashRnage.split('[-]')
        String rangeStart = split[0]
        String rangeEnd = split[1]
        if (rangeStart.isNumber() && rangeEnd.isNumber())
            (Integer.parseInt(rangeStart)..Integer.parseInt(rangeEnd)).each{
                resultList << it.toString()
            }
        else{
            (rangeStart..rangeEnd).each{
                resultList << it.toString()
            }
        }
        return resultList
    }

    static List<String> resolveDotDotRangeExpression(String dashRnage){
        List<String> resultList = []
        List<String> split = dashRnage.split('[.][.]')
        String rangeStart = split[0]
        String rangeEnd = split[1]
        if (rangeStart.isNumber() && rangeEnd.isNumber())
            (Integer.parseInt(rangeStart)..Integer.parseInt(rangeEnd)).each{
                resultList << it.toString()
            }
        else{
            (rangeStart..rangeEnd).each{
                resultList << it.toString()
            }
        }
        return resultList
    }




    /**************************************************
     *
     * Parsing
     *
     **************************************************/
    static String parseString(Object value){
        String stringValue = null;
        if (value == null){
            //None
        }else{
            stringValue = value.toString();
        }
        return stringValue;
    }

    static Integer parseInteger(Object value){
        Integer valueInteger = null;
        if (value == null){
            //None
        }else if (value instanceof Number){
            valueInteger = value;
        }else if (value instanceof String){
            if (((String)value).isNumber()){
                valueInteger = Integer.parseInt(value);
            }
        }
        return valueInteger;
    }

    static Long parseLong(Object value){
        Long valueLong = null;
        if (value == null){
            //None
        }else if (value instanceof Number){
            valueLong = value;
        }else if (value instanceof String){
            if (((String)value).isNumber()){
                valueLong = Long.parseLong(value);
            }
        }
        return valueLong;
    }

    /******************************
     * Several optional values to Standard value
     * @param value
     * @return
     ******************************/
    static Boolean toBooleanWith(Object value){
        if (value == null)
            return null
        return (value instanceof Boolean) ? value : (value instanceof Number) ? value == 1 : (value instanceof String) ? toBooleanWith(value, ["1", "Y", "TRUE", "T", "OK", "YES"], ["0", "N", "FALSE", "F", "NO"]) : value
    }

    static Boolean toBooleanWith(String value, List<String> trueMeaningPackage, List<String> falseMeaningPackage){
        switch(value?.toUpperCase()?.trim()){
            case {trueMeaningPackage.contains(it)}:
                return true
                break
            case {falseMeaningPackage.contains(it)}:
                return false
                break
        }
        return null
    }



    static Date toDateWith(Object value){
        if (value == null)
            return null

        if (value instanceof Date) {
            return value

        }else if (value instanceof Long){
            return new Date(value)

        }else if (value instanceof String){ //통상적으로 사용하는거 그냥 일반화시켜버린다.
            int size = value.length()
            if (size == 8)
                return new SimpleDateFormat("yyyyMMdd").parse(value)
            if (size == 10)
                return new SimpleDateFormat("yyyy-MM-dd").parse(value)
            if (size >= 19)
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value)

        }else if (value instanceof Integer){
            String s = String.valueOf(value)
            return new SimpleDateFormat("yyyyMMdd").parse(s)
        }
        return null
    }


    static Object toStandardMeaningWith(Object checkValue, Map<Object, Object> meaningByStandardValue){
        toStandardMeaningWith(checkValue, meaningByStandardValue, true)
    }

    static Object toStandardMeaningWith(Object checkValue, Map<Object, Object> meaningByStandardValue, boolean ignoreCase){
        Object result
        meaningByStandardValue.any{ Object standardValue, Object meaningPacakge ->
            if (meaningPacakge instanceof List){
                if (ignoreCase){
                    if (meaningPacakge.find{ it.equalsIgnoreCase(checkValue) })
                        result = standardValue
                }else{
                    if (meaningPacakge.find{ it.equals(checkValue) })
                        result = standardValue
                }

            }else{
                if (ignoreCase){
                    if (meaningPacakge.equalsIgnoreCase(checkValue))
                        result = standardValue
                }else{
                    if (meaningPacakge.equals(checkValue))
                        result = standardValue
                }

            }
            return result
        }
        return result
    }



    public static String paddingRightToMax(String c, int max){
        return paddingRightToMax(c, max, " ")
    }

    public static String paddingRightToMax(String c, int max, String pad){
        String result
        int length = c.length()
        int restLength = max - length
        if (restLength > 0){
            StringBuilder sb = new StringBuilder();
            sb.append(c)
            for (int i=0; i<restLength; i++){
                sb.append(pad)
            }
            result = sb.toString()
        }else{
            result = c
        }
        return result
    }

}
