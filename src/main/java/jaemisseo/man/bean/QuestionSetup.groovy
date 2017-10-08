package jaemisseo.man.bean

import jaemisseo.man.util.Option

/**
 * Created by sujkim on 2017-03-18.
 */
class QuestionSetup extends Option<QuestionSetup> {

    String question
    String answer
    String recommandAnswer
    String validation       //Not Supported Yet
    Map descriptionMap
    Map valueMap
    Integer repeatLimit = 1

    Boolean modeOnlyInteractive
    Boolean modeLoadResponseFile
}
