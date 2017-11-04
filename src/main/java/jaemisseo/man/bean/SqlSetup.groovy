package jaemisseo.man.bean

import jaemisseo.man.util.Option

/**
 * Created by sujkim on 2017-03-17.
 */
class SqlSetup extends Option<SqlSetup> {

    //-DataSource
    String vendor
    String ip
    String port
    String db
    String user
    String password
    String url
    String driver

    //-Replacement
    def replaceAll
    def replaceTable
    def replaceIndex
    def replaceSequence
    def replaceView
    def replaceFunction
    def replaceTablespace
    def replaceUser
    def replaceDatafile
    def replacePassword

    //-Validation
    List<String> commnadListThatObjectMustExist = ['INSERT', 'UPDATE', 'DELETE', 'DROP']
    List<String> commnadListThatObjectMustNotExist = ['CREATE']

    //-Mode
    Boolean modeSqlExecute
    Boolean modeSqlCheckBefore
    Boolean modeSqlFileGenerate
    Boolean modeSqlIgnoreErrorExecute
    Boolean modeSqlIgnoreErrorAlreadyExist
    Boolean modeSqlIgnoreErrorCheckBefore
    Boolean modeSqlProgressBar = true

}
