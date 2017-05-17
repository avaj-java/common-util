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

}