package jaemisseo.man.bean

import groovy.sql.Sql
import jaemisseo.man.util.ConnectionGenerator
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
    def replaceSchema
    def replaceSchemaForObject
    def replaceForceSchemaForObject
    def replaceUser
    def replaceOwner
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
    Boolean modeProgressBar = true


    SqlSetup setup(){
        ConnectionGenerator conGen = generateConnectionGenerator()
        url = conGen.url
        driver = conGen.driver
        return this
    }

    ConnectionGenerator generateConnectionGenerator(){
        return new ConnectionGenerator(this)
    }

    Sql generateSqlInstance(){
        ConnectionGenerator conGen = generateConnectionGenerator()
        return conGen.generateSqlInstance()
    }

}
