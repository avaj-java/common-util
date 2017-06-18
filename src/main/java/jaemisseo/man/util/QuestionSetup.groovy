package jaemisseo.man.util

/**
 * Created by sujkim on 2017-03-18.
 */
class QuestionSetup extends Option<QuestionSetup>{

    String question
    String answer
    String recommandAnswer
    String validation       //Not Supported Yet
    Boolean modeOnlyInteractive

    Integer repeatLimit = 1

    Map descriptionMap
    Map valueMap

    Boolean modeLoadResponseFile
}
