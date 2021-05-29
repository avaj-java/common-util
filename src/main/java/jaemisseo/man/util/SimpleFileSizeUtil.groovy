package jaemisseo.man.util

class SimpleFileSizeUtil {


    static final String EXP_B = "B"
    static final String EXP_K = "K"
    static final String EXP_KB = "KB"
    static final String EXP_M = "M"
    static final String EXP_MB = "MB"
    static final String EXP_G = "G"
    static final String EXP_GB = "GB"
    static final String EXP_T = "T"
    static final String EXP_TB = "TB"


    public static Long parseLongFromFileSizeExpression(Object fileSize){
        Long valueLong = null;
        if (fileSize == null){
            valueLong = null;

        }else if (fileSize instanceof Number){
            //None
            valueLong = fileSize;

        }else if (fileSize instanceof String){
            if (((String)fileSize).isNumber()){
                valueLong = Long.parseLong(fileSize);
            }else{
                String number = fileSize.replaceAll(/[^0-9\\,]]/, "");
                String unit = fileSize.replaceAll(/[^A-Za-z]/, "");
                Long unitSize = parseLongFromUnit(unit);

                if (number == null)
                    number = 1;
                if (unitSize == null)
                    unitSize = 1;
                if (number.isNumber()){
                    valueLong = number * unitSize;
                }else{
                    valueLong = unitSize;
                }
            }
        }
        return valueLong;
    }

    public static Long parseLongFromUnit(String unit){
        Long size = 1
        if (unit == null){
            //None
        }else if (unit.equalsIgnoreCase(EXP_B)){
            //None
        }else if (unit.equalsIgnoreCase(EXP_K) || unit.equalsIgnoreCase(EXP_KB)){
            size = 1_000
        }else if (unit.equalsIgnoreCase(EXP_M) || unit.equalsIgnoreCase(EXP_MB)){
            size = 1_000_000
        }else if (unit.equalsIgnoreCase(EXP_G) || unit.equalsIgnoreCase(EXP_GB)){
            size = 1_000_000_000
        }else if (unit.equalsIgnoreCase(EXP_T) || unit.equalsIgnoreCase(EXP_TB)){
            size = 1_000_000_000_000
        }
        return size
    }

}
