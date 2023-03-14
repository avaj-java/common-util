package jaemisseo.man.util

import java.util.function.Consumer

public class SimpleFailoverUtil {

    /*************************
     * FailoverResult
     *      - Result of failover process
     *************************/
    public static class FailoverResult {
        public FailoverResult(int numberOfTry, int numberOfFailoverAttempts){
            this.numberOfTry = numberOfTry;
            this.numberOfFailoverAttempts = numberOfFailoverAttempts;
            this.currentAttempts = 0;
            start()
        }
        int currentAttempts = 0;
        int numberOfTry;
        int numberOfFailoverAttempts;
        Boolean stateDone;
        Boolean stateNoMoreTry;
        Date startDate
        Date endDate
        List<FailoverTurn> turns = new ArrayList<FailoverTurn>();

        FailoverTurn newTurn(){ //- It would be called by each turn.
            this.currentAttempts++;
            FailoverTurn turn = new FailoverTurn(currentAttempts, this);
            turn.start();
            this.turns.add( turn );
            return turn;
        }
        void start(){
            this.startDate = new Date();
        }
        void done(){ //- It would be called when process complete.
            this.endDate = new Date();
            this.stateDone = true;
        }
        void failed(){ //- It would be called when process failed.
            this.endDate = new Date();
            this.stateDone = false;
            this.stateNoMoreTry = (!this.stateDone && this.currentAttempts >= this.numberOfTry);
        }

        public boolean checkDone(){
            return this.stateDone;
        }
        public List<Exception> getExceptions(){
            List<Exception> exceptions = this.turns.findAll{ it.getException() }.collect{ it }
            return exceptions
        }
        public Exception getLastException(){
            Exception exception = null;
            for (int i=this.turns.size() -1; i>-1; i--){
                exception = this.turns.get(i).getException();
                if (exception != null)
                    break;
            }
            return exception
        }
        public FailoverTurn getLastTurn(){
            int lastIndex = this.turns.size() -1;
            return this.turns.get(lastIndex);
        }
        public String toString(){
            StringBuilder sb = new StringBuilder()
            String mesageForTurns = this.turns.collect{ it.toString() }.join(System.lineSeparator())
            String messageForResult = "[FailoverResult - currentAttempts:$currentAttempts, numberOfTry:$numberOfTry, numberOfFailoverAttempts:$numberOfFailoverAttempts, date:[start:$startDate: end:$endDate]]"
            return sb.append(messageForResult).append(System.lineSeparator()).append(mesageForTurns).toString()
        }
    }

    /*************************
     * FailoverTurn
     *      - Every Turn to execute your code (Original try + Failover try)
     *************************/
    public static class FailoverTurn {
        public FailoverTurn(int attempts, FailoverResult result){
            this.attempts = attempts;
            this.result = result;
        }
        int attempts = 0;
        FailoverResult result;
        Exception exception;
        Boolean failoverable;
        Boolean stateDone;
        Date startDate
        Date endDate
        void start(){ //- It would be called when process complete.
            this.startDate = new Date();
        }
        void done(){ //- It would be called when process complete.
            this.endDate = new Date();
            this.stateDone = true;
        }
        void failed(Exception exception, boolean failoverable){ //- It would be called when process failed.
            this.endDate = new Date();
            this.stateDone = false;
            this.exception = exception;
            this.failoverable = failoverable;
        }
        public boolean checkDone(){
            return this.stateDone;
        }
        public Exception getException(){
            return this.exception;
        }
        String toString(){
            return "[FailoverTurn - stateDone:$stateDone, attempts:$attempts, exception:$exception, date:[start:$startDate: end:$endDate]]"
        }
    }


    /*************************
     * Exception - FailoverLimitExceedException
     *************************/
    public static class FailoverLimitExceedException extends Exception {
        public FailoverLimitExceedException(){
            super();
        }
        public FailoverLimitExceedException(String message){
            super(message);
        }
        public FailoverLimitExceedException(Throwable cause){
            super(cause);
        }
        public FailoverLimitExceedException(String message, Throwable cause){
            super(message, cause);
        }
    }

    /*************************
     * Exception - NotFailoverableException
     *************************/
    public static class NotFailoverableException extends Exception {
        public NotFailoverableException(){
            super();
        }
        public NotFailoverableException(String message){
            super(message);
        }
        public NotFailoverableException(Throwable cause){
            super(cause);
        }
        public NotFailoverableException(String message, Throwable cause){
            super(message, cause);
        }
    }




    public static FailoverResult tryTimes(int numberOfFailoverAttempts, Closure closure){
        return tryTimes(numberOfFailoverAttempts, null, closure)
    }

    public static FailoverResult tryTimes(int numberOfFailoverAttempts, Consumer<FailoverTurn> consumer){
        return tryTimes(numberOfFailoverAttempts, null, consumer);
    }

    public static FailoverResult tryTimes(int numberOfFailoverAttempts, List<Class> retryableExceptions, Consumer<FailoverTurn> consumer){
        FailoverResult result = tryTimes(numberOfFailoverAttempts, retryableExceptions){ FailoverTurn turn ->
            consumer.accept(turn);
        }
        return result;
    }

    /**
     * Try with failover (Non-Throws)
     *      - It never throws Exception - Use FailoverResult.checkDone() to know if the operation was successful.
     *      - You can use the returned "Failover Request" object for analysis.
     * @param numberOfFailoverAttempts: number of failover retries
     * @param retryableExceptions: Exception that requires failover (Null means every exceptions are failoverable exception)
     * @param closure: Closure code that requires failover
     * @return (FailoverResult)
     */
    public static FailoverResult tryTimes(int numberOfFailoverAttempts, List<Class> retryableExceptions, Closure closure){
        int numberOfTry = 1 + numberOfFailoverAttempts; //a Origin Process Turn + Failover Process Turns
        FailoverResult result = new FailoverResult(numberOfTry, numberOfFailoverAttempts);
        FailoverTurn turn = null;

        for (int i=0; i<numberOfTry; i++){
            turn = result.newTurn();

            try{
                closure(turn);
                turn.done();
                break;

            }catch(Exception e){
                boolean failoverable = checkFailoverableException(e, retryableExceptions);
                turn.failed(e, failoverable);
                if (!failoverable){
                    break;
                }
            }
        }

        if (turn.checkDone()){
            //- Success
            result.done();
        }else{
            //- Fail
            result.failed();
        }

        return result;
    }



    public static FailoverResult tryTimesAsThrowable(int numberOfFailoverAttempts, Closure closure) throws FailoverLimitExceedException, NotFailoverableException {
        return tryTimesAsThrowable(numberOfFailoverAttempts, null, closure);
    }

    public static FailoverResult tryTimesAsThrowable(int numberOfFailoverAttempts, Consumer<FailoverTurn> consumer) throws FailoverLimitExceedException, NotFailoverableException {
        return tryTimesAsThrowable(numberOfFailoverAttempts, null, consumer);
    }

    public static FailoverResult tryTimesAsThrowable(int numberOfFailoverAttempts, List<Class> retryableExceptions, Consumer<FailoverTurn> consumer) throws FailoverLimitExceedException, NotFailoverableException {
        FailoverResult result = tryTimesAsThrowable(numberOfFailoverAttempts, retryableExceptions){ FailoverTurn turn ->
            consumer.accept(turn);
        }
        return result;
    }

    /**
     * Try with failover (Throable)
     * @param numberOfFailoverAttempts: number of failover retries
     * @param retryableExceptions: Exception that requires failover (Null means every exceptions are failoverable exception)
     * @param closure: Closure code that requires failover
     * @return (FailoverResult)
     * @throws FailoverLimitExceedException
     */
    public static FailoverResult tryTimesAsThrowable(int numberOfFailoverAttempts, List<Class> retryableExceptions, Closure closure) throws FailoverLimitExceedException, NotFailoverableException {
        //Try and Failover
        FailoverResult result = tryTimes(numberOfFailoverAttempts, retryableExceptions, closure);
        //Check result
        if (!result.checkDone()){
            FailoverTurn lastTurn = result.getLastTurn()
            if (lastTurn.failoverable){
                String message = makeFailMessage("The number of trying reaches failover count. [try:${result.currentAttempts}] [failover:${result.numberOfFailoverAttempts}]", result)
                throw new FailoverLimitExceedException(message);
            }else{
                Exception exception = lastTurn.getException()
                String message = makeFailMessage("Not failoverable exception was thrown [${exception.toString()}] [try:${result.currentAttempts}] [failover:${result.numberOfFailoverAttempts}]", result)
                throw new NotFailoverableException(message, exception);
            }
        }
        return result;
    }



    public static FailoverResult tryTimesAsThrowableLastException(int numberOfFailoverAttempts, Closure closure) throws Exception {
        return tryTimesAsThrowableLastException(numberOfFailoverAttempts, null, closure);
    }

    public static FailoverResult tryTimesAsThrowableLastException(int numberOfFailoverAttempts, Consumer<FailoverTurn> consumer) throws Exception {
        return tryTimesAsThrowableLastException(numberOfFailoverAttempts, null, consumer);
    }

    public static FailoverResult tryTimesAsThrowableLastException(int numberOfFailoverAttempts, List<Class> retryableExceptions, Consumer<FailoverTurn> consumer) throws Exception {
        FailoverResult result = tryTimesAsThrowableLastException(numberOfFailoverAttempts, retryableExceptions){ FailoverTurn turn ->
            consumer.accept(turn);
        }
        return result;
    }

    /**
     * Try with failover (Throable)
     * @param numberOfFailoverAttempts: number of failover retries
     * @param retryableExceptions: Exception that requires failover (Null means every exceptions are failoverable exception)
     * @param closure: Closure code that requires failover
     * @return (FailoverResult)
     * @throws Exception
     */
    public static FailoverResult tryTimesAsThrowableLastException(int numberOfFailoverAttempts, List<Class> retryableExceptions, Closure closure) throws Exception {
        //Try and Failover
        FailoverResult result = tryTimes(numberOfFailoverAttempts, retryableExceptions, closure);
        //Check result
        if (!result.checkDone()){
            Exception lastException = result.getLastException();
            throw lastException;
        }
        return result;
    }



    private static String makeFailMessage(String mainMessage, FailoverResult result){
        StringBuilder sb = new StringBuilder()
        //1. Make a main Message
        sb.append(mainMessage).append(System.lineSeparator())
        //2. Make messages of all turn
        for (FailoverTurn turn : result.getTurns()){
            sb.append(turn.toString()).append(System.lineSeparator())
        }
        //3. Concat
        return sb.toString()
    }

    private static boolean checkFailoverableException(Exception e, List<Class> retryableExceptions){
        boolean statusRetryableException = true
        if (retryableExceptions != null){
            statusRetryableException = retryableExceptions.any{ it.isAssignableFrom(e.getClass())  }
        }
        return statusRetryableException
    }

}
