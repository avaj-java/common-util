package jaemisseo.man.code

/**
 * Created by sujkim on 2017-05-06.
 */
enum ChangeStatusCode {

    NONE(0),
    NEW(1),
    MODIFIED(2),
    REMOVED(3)

    int value

    ChangeStatusCode(int value){
        this.value = value
    }


    /*************************
     * value로 Code 찾기
     *************************/
    private static final Map<String, ChangeStatusCode> valueMap = new LinkedHashMap<String, ChangeStatusCode>()


    /*****
     * 최초 ValueMap 생성
     *****/
    static {
        for (ChangeStatusCode code : values()) {
            valueMap.put(code.value, code)
        }
    }

    /*****
     * value로 코드 구하기
     *****/
    static ChangeStatusCode findByValue(String value) {
        return findByValue(Integer.parseInt(value))
    }

    static ChangeStatusCode findByValue(int value) {
        return valueMap[value]
    }


}