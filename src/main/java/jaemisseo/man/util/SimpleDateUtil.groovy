package jaemisseo.man.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class SimpleDateUtil {

    /**************************************************
     *
     * Converting
     *      - Ref: https://www.baeldung.com/java-date-to-localdate-and-localdatetime
     *
     **************************************************/
    public Date toDate(String exp, String format) {
        return toDate(exp, new SimpleDateFormat(format))
    }

    public Date toDate(String exp, SimpleDateFormat format) {
        return format.parse(exp)
    }

    public Date toDate(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
//        return java.sql.Date.valueOf(dateToConvert);
    }

    public static Date toDate(LocalDateTime dateToConvert) {
        return java.sql.Timestamp.valueOf(dateToConvert);
//        return java.util.Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }



    public static LocalDate toLocalDate(String exp, String format) {
        return toLocalDate(exp, new SimpleDateFormat(format))
    }

    public static LocalDate toLocalDate(String exp, SimpleDateFormat format) {
        return toLocalDate(format.parse(exp))
    }

    public static LocalDate toLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
//        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

    public LocalDateTime toLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//        return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
//        return new java.sql.Timestamp(dateToConvert.getTime()).toLocalDateTime();
    }



    public static boolean checkValidDateFormat(String date, String format) {
        SimpleDateFormat dateFormatParser = new SimpleDateFormat(format, Locale.KOREA);
        dateFormatParser.setLenient(false);
        try {
            dateFormatParser.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    /**************************************************
     *
     * Checking ElapsedTime
     *
     **************************************************/
    static boolean checkElapsedTime(Date lastDate, long validIntervalMilliSecondTime){
        return checkElapsedTime(lastDate.getTime(), validIntervalMilliSecondTime)
    }

    static boolean checkElapsedTime(Date lastDate, long validIntervalMilliSecondTime, Date checkingDate){
        return checkElapsedTime(lastDate.getTime(), validIntervalMilliSecondTime, checkingDate)
    }

    static boolean checkElapsedTime(Date lastDate, long validIntervalMilliSecondTime, long checkingMilliSecondTime){
        return checkElapsedTime(lastDate.getTime(), validIntervalMilliSecondTime, checkingMilliSecondTime)
    }

    static boolean checkElapsedTime(long lastMilliSecondTime, long validIntervalMilliSecondTime){
        long currentTime = new Date().getTime()
        return checkElapsedTime(lastMilliSecondTime, validIntervalMilliSecondTime, currentTime)
    }

    static boolean checkElapsedTime(long lastMilliSecondTime, long validIntervalMilliSecondTime, Date checkingDate){
        return checkElapsedTime(lastMilliSecondTime, validIntervalMilliSecondTime, checkingDate.getTime())
    }

    static boolean checkElapsedTime(long lastMilliSecondTime, long validIntervalMilliSecondTime, long checkingMilliSecondTime){
        Long elapsedTime = getElapsedTimeFrom(lastMilliSecondTime, checkingMilliSecondTime)
        boolean result = validIntervalMilliSecondTime < Math.abs(elapsedTime)
        return result
    }



    static Long getElapsedTimeFrom(Date fromDate){
        return getElapsedTimeFrom(fromDate.getTime())
    }

    static Long getElapsedTimeFrom(Date fromDate, long checkingMilliSecondTime){
        return getElapsedTimeFrom(fromDate.getTime(), checkingMilliSecondTime)
    }

    static Long getElapsedTimeFrom(Date fromDate, Date checkingDate){
        return getElapsedTimeFrom(fromDate.getTime(), checkingDate.getTime())
    }

    static Long getElapsedTimeFrom(long fromTime){
        long currentTime = new Date().getTime()
        return getElapsedTimeFrom(currentTime, fromTime)
    }

    static Long getElapsedTimeFrom(long fromTime, long checkingMilliSecondTime){
        Long elapsedTime = checkingMilliSecondTime - fromTime
        return elapsedTime
    }




    static Long toMilliSecondTime(Object value) {
        Long result = null;
        if (value instanceof String){
            result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value).getTime()
        }else if (value instanceof Date){
            result = value.getTime()
        }else{
            String className = value.getClass().getName()
            if (className.equals('java.sql.Timestamp')){ //toDate
                java.sql.Timestamp t = ((java.sql.Timestamp)value)
                Date date = new Date( t.getTime() )
                result = date.getTime()
            }else if (className.equals('oracle.sql.TIMESTAMP')){ //toDate
                Date date = value.dateValue()
                result = date.getTime()
            }else if (className.equals('oracle.sql.TIMESTAMPTZ')){
                java.sql.Timestamp t = value.timestampValue()
                Date date = new Date( t.getTime() )
                result = date.getTime()
            }else{
                result = value
            }
        }
        return result
    }

}
