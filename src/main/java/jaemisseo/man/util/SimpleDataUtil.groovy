package jaemisseo.man.util

import groovy.json.JsonSlurper

import java.sql.Clob
import java.text.SimpleDateFormat

public class SimpleDataUtil {

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

        result = splitSimpleObject(value).collect{ makeValueAsDepthByDot(it) }
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
        if (value == null)
            return null

        Object result
        value = value.trim()
        if (value.startsWith('[') && value.endsWith(']')){
            if (value.length() == 2)
                return []
            value = value.substring(1, value.length() - 1)
            result = generateValues(value)

        }else if (value.startsWith('{') && value.endsWith('}')){
            if (value.length() == 2)
                return [:]
            value = value.substring(1, value.length() - 1)
            result = generateEntriesAsDepthByDot(value)

        }else{
            result = generateEntriesAsDepthByDot(value)
        }

        return result
    }

    static Object parseSimpleObjectExpressionAsFlattenKey(String value){
        if (value == null)
            return null

        Object result
        value = value.trim()
        if (value.startsWith('[') && value.endsWith(']')){
            if (value.length() == 2)
                return []
            value = value.substring(1, value.length() - 1)
            result = generateValues(value)

        }else if (value.startsWith('{') && value.endsWith('}')){
            if (value.length() == 2)
                return [:]
            value = value.substring(1, value.length() - 1)
            result = generateEntriesAsFlattenKey(value)

        }else{
            result = generateEntriesAsFlattenKey(value)
        }

        return result
    }



    private static List<Object> generateValues(String simpleObjectExpression){
        if (simpleObjectExpression == null)
            return null
        List<String> valueStrings = splitSimpleObject(simpleObjectExpression)
        List<Object> values = valueStrings?.collect{valueString -> makeValueAsDepthByDot(valueString) }
        return values
    }

    private static Map<String, Object> generateEntriesAsFlattenKey(String simpleObjectExpression){
        if (simpleObjectExpression == null)
            return null
        List<String> entryStrings = splitSimpleObject(simpleObjectExpression)
        Map<String, Object> entries = entryStrings?.collectEntries{ entryString ->
            makeEntryAsFlattenKey(entryString)
        }
        return entries
    }

    private static Map<String, Object> generateEntriesAsDepthByDot(String simpleObjectExpression){
        Map<String, Object> entries = [:]
        if (simpleObjectExpression == null)
            return entries
        List<String> entryArray = splitSimpleObject(simpleObjectExpression)
        entryArray?.each{ entryString ->
            AbstractMap.SimpleEntry entry = makeEntryAsDepthByDot(entryString)
            consistEntry(entries, entry.key,entry.value)
        }

        return entries
    }



    static List<String> splitSimpleObject(String simpleObjectExpression){
        List<String> splited = splitSimpleObject(simpleObjectExpression, CHAR_COMMA, false)
        return splited
    }

    static List<String> splitSimpleObject(String simpleObjectExpression, char spliter, boolean modeIgnoreBraceAndBracket){
        List<String> splitedList = []
        StringBuilder sb = new StringBuilder()
        simpleObjectExpression = simpleObjectExpression?.trim()
        char[] chars = simpleObjectExpression.toCharArray()
        List<Integer> commaIndexList = []
        Character c, charBefore, currentQuote
        boolean statusCommenting = false
        boolean statusSeperating = false
        int brace = 0, curlyBrace = 0, bracket = 0
        boolean needToNextCheck = false;

        for (int i=0; i<chars.length; i++){
            c = chars[i];

            if (!charBefore.equals(CHAR_BLACK_SLASH)){
                if (currentQuote){
                    currentQuote = (currentQuote.equals(c)) ? null : currentQuote

                }else if (charBefore.equals(CHAR_COMMENT_A1) && c.equals(CHAR_COMMENT_A2)){
                    statusCommenting = true
                    sb.deleteCharAt(sb.length() -1)

                }else if (statusCommenting){


                }else{

                    needToNextCheck = true
                    if (needToNextCheck && !modeIgnoreBraceAndBracket){
                        needToNextCheck = false
                        switch (c){
                            case CHAR_BRACE_OPEN: ++brace; break;
                            case CHAR_BRACE_CLOSE: --brace; break;
                            case CHAR_CURLY_BRACE_OPEN: ++curlyBrace; break;
                            case CHAR_CURLY_BRACE_CLOSE: --curlyBrace; break;
                            case CHAR_BRACKET_OPEN: ++bracket; break;
                            case CHAR_BRACKET_CLOSE: --bracket; break;
                            default:
                                needToNextCheck = true
                                break;
                        }
                    }
                    if (needToNextCheck){
                        switch (c){
                            case CHAR_SINGLE_QUOTE: currentQuote = CHAR_SINGLE_QUOTE; break;
                            case CHAR_DOUBLE_QUOTE: currentQuote = CHAR_DOUBLE_QUOTE; break;
                            default:
                                //Right separator
                                statusSeperating = c.equals(spliter) && brace == 0 && curlyBrace == 0 && bracket == 0
                                if (statusSeperating)
                                    commaIndexList << i
                                break;
                        }
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



    private static AbstractMap.SimpleEntry makeEntryAsDepthByDot(String entryString){
        Integer firstColonIndex = entryString?.indexOf(':')
        String key
        Object value
        if (firstColonIndex != -1){
            key = entryString?.substring(0, firstColonIndex)?.trim()
            value = entryString?.substring(firstColonIndex +1, entryString.length())?.trim()
            value = makeValueAsDepthByDot(value)
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

    private static AbstractMap.SimpleEntry makeEntryAsFlattenKey(String entryString){
        Integer firstColonIndex = entryString?.indexOf(':')
        String key
        Object value
        if (firstColonIndex != -1){
            key = entryString?.substring(0, firstColonIndex)?.trim()
            value = entryString?.substring(firstColonIndex +1, entryString.length())?.trim()
            value = makeValueAsFlattenKey(value)
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



    private static Object makeValueAsDepthByDot(Object value){
        if (value instanceof String){
            //- trim() line-separators, spaces
            value = powerTrim(value)

            //- remove comment
//            value = (value.startsWith("/*") || value.endsWith("*/")) ? removeComment(value) : value

            //- Make some object
            if (value.startsWith('[') && value.endsWith(']')){
                //- Check []
                if (value.length() == 2)
                    return []
                value = value.substring(1, value.length() - 1)
                value = generateValues(value)

            }else if (value.startsWith('{') && value.endsWith('}')){
                //- Check {}
                if (value.length() == 2)
                    return [:]
                value = value.substring(1, value.length() - 1)
                value = generateEntriesAsDepthByDot(value)

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
                    if (checkStatusQuotedString(value)){
                        value = extractValueFromQuote(value)

                    }else if (checkStatusRangeExpression(value)){
                        try{
                            value = resolveRangeExpression(value)
                        }catch(Exception e){
                        }
                    }
                }
            }
        }
        return value
    }

    private static Object makeValueAsFlattenKey(Object value){
        if (value instanceof String){
            //- trim() line-separators, spaces
            value = powerTrim(value)

            //- remove comment
//            value = (value.startsWith("/*") || value.endsWith("*/")) ? removeComment(value) : value

            //- Make some object
            if (value.startsWith('[') && value.endsWith(']')){
                //- Check []
                if (value.length() == 2)
                    return []
                value = value.substring(1, value.length() - 1)
                value = generateValues(value)

            }else if (value.startsWith('{') && value.endsWith('}')){
                //- Check {}
                if (value.length() == 2)
                    return [:]
                value = value.substring(1, value.length() - 1)
                value = generateEntriesAsFlattenKey(value)

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
                    if (checkStatusQuotedString(value)){
                        value = extractValueFromQuote(value)

                    }else if (checkStatusRangeExpression(value)){
                        try{
                            value = resolveRangeExpression(value)
                        }catch(Exception e){
                        }
                    }
                }
            }
        }
        return value
    }




    private static String powerTrim(String value){
        return value.replaceAll('^\\s*', '').replaceAll('\\s*$', '')
    }

    private static String removeComment(String value){
        return value.replaceAll('^[/][*].*[*][/]', '').replaceAll('[/][*].*[*][/]$', '')
    }

    private static boolean checkStatusQuotedString(String maybeQuoteValue){
        return (
                (
                        maybeQuoteValue.startsWith(CHAR_DOUBLE_QUOTE.toString()) && maybeQuoteValue.endsWith(CHAR_DOUBLE_QUOTE.toString())
                )
                ||
                (
                        maybeQuoteValue.startsWith(CHAR_SINGLE_QUOTE.toString()) && maybeQuoteValue.endsWith(CHAR_SINGLE_QUOTE.toString())
                )
        )
    }

    private static boolean checkStatusRangeExpression(String maybeRangeExpression){
        //..
        int foundIndexForDotDot = maybeRangeExpression.indexOf("..")
        if (foundIndexForDotDot > 0 && maybeRangeExpression.length() > foundIndexForDotDot +2){
            String[] array = maybeRangeExpression.split("[.][.]")
            if (array.length == 2){
                boolean validStatusThatBothAreNumber = (array[0].isNumber() && array[1].isNumber())
                boolean validStatusThatSameEachCharLength = (!array[0].isNumber() && !array[1].isNumber() && array[0].length() == array[1].length())
                return validStatusThatBothAreNumber || validStatusThatSameEachCharLength
            }
        }
        return false

        //-
//        int foundIndexForDash = maybeRangeExpression.indexOf("-")
//        if (foundIndexForDash > 0 && maybeRangeExpression.length() > foundIndexForDotDot +1){
//            String[] array = maybeRangeExpression.split("..")
//            if (array.length == 2){
//                boolean validStatusThatBothAreNumber = (array[0].isNumber() && array[1].isNumber())
//                boolean validStatusThatSameEachCharLength = (!array[0].isNumber() && !array[1].isNumber() && array[0].length() == array[1].length())
//                return validStatusThatBothAreNumber || validStatusThatSameEachCharLength
//            }
//        }
//
//        return false
    }

    private static String extractValueFromQuote(String quoteValue){
        return quoteValue?.substring(1, quoteValue.length() -1)
    }

    private static Object consistEntry(Map<String, Object> entries, String key, Object value){
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
                resultList << it
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



    public static toList(Object some){
        if (some == null)
            return null
        if (some instanceof List)
            return some
        ArrayList list = new ArrayList<>()
        list.add( some )
        return list
    }



    public static toSimple(Object value){
        Class clazz = value.getClass()
        String className = clazz.getName()
        if (className.equals("com.datastreams.mdosa.common.model.ObjectId")){  //toString //TODO: 아쉽지만, 일단 Class이름을 판단해보자! - ObjectId가 쓰임새가 많아서 옴기기 쉽지않고 관점을 정하기 쉽지 않음.
            return value.toString()
        }else if (className.equals("java.sql.Clob")){ //toString
            return ClobUtil.convertToString(value as Clob)
        }else if (className.equals("oracle.sql.CLOB")){ //toString
            return ClobUtil.convertToString(value as Clob)
        }else if (value instanceof java.sql.Date){
            return value.getTime()
        }else if (value instanceof java.util.Date){
            return value.getTime()
        }else if (className.equals("java.sql.Timestamp")){ //toDate
            return new Date( ((java.sql.Timestamp)value).getTime() )
        }else if (className.equals("oracle.sql.TIMESTAMP")){ //toDate
            return value.dateValue()
        }else if (className.equals("oracle.sql.TIMESTAMPTZ")){
            return value.dateValue().getTime()
        }

        return value
    }

}
