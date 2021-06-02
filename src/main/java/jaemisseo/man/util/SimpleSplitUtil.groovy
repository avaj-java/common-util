package jaemisseo.man.util

class SimpleSplitUtil {

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

    static List<String> separateGroupByQuotes(String value){
        List<String> splitedList = []
        StringBuilder sb = new StringBuilder()
//        value = value?.trim()
        char[] chars = value.toCharArray()
        List<Integer> commaIndexList = []
        Character charBefore, currentQuote
        boolean statusCommenting = false
        boolean statusSeperating = false
        boolean statusOnQuote = false
        int startQuoteIndex = -1, lastEndQuoteIndex = -1
        int brace = 0, curlyBrace = 0, bracket = 0

        int length = chars.length
        Character c = null
        for (int i=0; i<length; i++){
            c = chars[i]

            if (!charBefore.equals(CHAR_BLACK_SLASH)){
                if (currentQuote != null){
                    currentQuote = (currentQuote.equals(c)) ? null : currentQuote
                    statusOnQuote = (currentQuote != null)
                    if (!statusOnQuote){
                        lastEndQuoteIndex = i
                        statusSeperating = true
                    }

                }else if (charBefore.equals(CHAR_COMMENT_A1) && c.equals(CHAR_COMMENT_A2)){
                    statusCommenting = true
                    sb.deleteCharAt(sb.length() -1)

                }else if (statusCommenting){
                    //None

                }else{
                    switch (c){
                        case CHAR_BRACE_OPEN: ++brace; break;
                        case CHAR_BRACE_CLOSE: --brace; break;
                        case CHAR_CURLY_BRACE_OPEN: ++curlyBrace; break;
                        case CHAR_CURLY_BRACE_CLOSE: --curlyBrace; break;
                        case CHAR_BRACKET_OPEN: ++bracket; break;
                        case CHAR_BRACKET_CLOSE: --bracket; break;
                        case CHAR_SINGLE_QUOTE:
                            currentQuote = CHAR_SINGLE_QUOTE;
                            statusOnQuote = true;
                            startQuoteIndex = i;
                            statusSeperating = true;
                            break;
                        case CHAR_DOUBLE_QUOTE:
                            currentQuote = CHAR_DOUBLE_QUOTE;
                            statusOnQuote = true;
                            startQuoteIndex = i;
                            statusSeperating = true;
                            break;
                        default:
                            break;
                    }
                }
            }

            if (statusSeperating){
                //...
                if (statusOnQuote){
                    int gap = startQuoteIndex - lastEndQuoteIndex
                    if (gap > 1){
                        addToNewItem(splitedList, sb.toString())
                        sb = new StringBuilder()
                    }
                    sb.append(c)
                }else{
                    sb.append(c)
                    addToNewItem(splitedList, sb.toString())
                    sb = new StringBuilder()
                }
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
        addToNewItem(splitedList, sb.toString())

        //Split
//        splitedList = splitedList.findAll{ !it.isEmpty() }
        return splitedList
    }

    static void addToNewItem(List<String> splitedList, String item){
        if (!item.isEmpty()){
            splitedList.add(item);
        }
    }

}
