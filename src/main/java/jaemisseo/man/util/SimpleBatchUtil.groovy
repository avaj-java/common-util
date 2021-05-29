package jaemisseo.man.util

import java.nio.charset.Charset

class SimpleBatchUtil {


    /**
     *
     * @param totalItemSize
     * @param batchSize
     * @param closure
     */
    static List<Error> eachPage(long totalItemSize, long batchSize, Closure closure){
        return eachPage(totalItemSize, batchSize, false, closure)
    }

    static List<Error> eachPage(long totalItemSize, long batchSize, boolean modeIgnoreError, Closure closure){
        List<Error> errors = [];
        if (totalItemSize == 0)
            return errors;
        if (batchSize < 1)
            return errors;
        long totalPageSize = Math.floor(totalItemSize / batchSize) +1;
        long from;
        long to;


        (1..totalPageSize).each{ long currentPage ->
            from = ((currentPage -1) * batchSize) +1;
            to = (currentPage == totalPageSize) ? totalItemSize : (currentPage * batchSize);
            try{
                closure(currentPage, from, to);
            }catch(Exception e){
                if (modeIgnoreError){
                    errors.add(e);
                }else{
                    throw e;
                }
            }
        }
        return errors;
    }


    /**
     *
     * @param contents
     * @param charset - ex)  StandardCharsets.UTF_8
     * @param batchSize
     * @param closure
     */
    static void eachBytes(String contents, Charset charset, int batchSize, Closure closure){
        if (contents == null || contents.isEmpty())
            return;
        if (batchSize < 1)
            return;
        byte[] bytes = contents.getBytes(charset);
        int originByteLength = bytes.length;
        int totalPageSize = (originByteLength / batchSize) +1;
        int fromIndex = -1;
        int toIndex = -1;
        int currentPageSize = -1;
        (1..totalPageSize).each{ int currentPage ->
            //- Calculate page
            fromIndex = ((currentPage -1) * batchSize)
            toIndex = (currentPage == totalPageSize) ? originByteLength : ((currentPage) * batchSize) -1
            currentPageSize = toIndex - fromIndex
            //- Logging Splited Contents
            byte[] subBytes = new byte[currentPageSize]
            System.arraycopy(bytes, fromIndex, subBytes, 0, currentPageSize)
            closure(currentPage, fromIndex, toIndex, subBytes)
        }
    }

}
