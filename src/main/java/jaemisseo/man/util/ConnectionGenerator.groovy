package jaemisseo.man.util

import groovy.sql.Sql
import jaemisseo.man.bean.SqlSetup
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection

/**
 * Created by sujkim on 2017-02-19.
 */
class ConnectionGenerator{

    //Logger
    private Logger logger = LoggerFactory.getLogger(getClass());

    ConnectionGenerator(){}

    ConnectionGenerator(Map<String, String> map){
        setDatasource(map)
    }

    ConnectionGenerator(SqlSetup sqlSetup){
        setDatasource(sqlSetup)
    }

    static final String ORACLE = "ORACLE"           // 'jdbc:oracle:thin:' 'oracle.jdbc.driver.OracleDriver'
    static final String TIBERO = "TIBERO"           // 'jdbc:tibero:thin:' 'com.tmax.tibero.jdbc.TbDriver'
    static final String MYSQL = "MYSQL"             // 'jdbc:mysql:' 'com.mysql.jdbc.Driver'
    static final String MSSQL = "MSSQL"             // 'jdbc:sqlserver:' 'com.microsoft.sqlserver.jdbc.SQLServerDriver'
    static final String INFORMIX = "INFORMIX"       // 'jdbc:informix-sqli:' 'com.informix.jdbc.IfxDriver'
    static final String ALTIBASE = "ALTIBASE"       // 'jdbc:Altibase:' 'Altibase.jdbc.driver.AltibaseDriver'
    static final String SYBASE = "SYBASE"           // 'jdbc:sybase:Tds:' 'com.sybase.jdbc3.jdbc.SybDriver'
    static final String IBM_DB = "IBM_DB"           // 'jdbc:db2:' 'com.ibm.db2.jcc.DB2Driver'

    String vendor
    String user
    String password
    String ip
    String port
    String db
    String url
    String driver


    ConnectionGenerator setDatasource(SqlSetup sqlOpt) {
        vendor  = sqlOpt.vendor
        user    = sqlOpt.user
        password= sqlOpt.password
        ip      = sqlOpt.ip
        port    = sqlOpt.port
        db      = sqlOpt.db
        url     = sqlOpt.url
        driver  = sqlOpt.driver
        return this
    }

    ConnectionGenerator setup(){
        url = url ?: "${getURLProtocol(vendor)}@${ip}:${port}:${db}"
        driver = driver ?: getDriverName(vendor)
        return this
    }

    ConnectionGenerator init(){
        vendor=null; user=null; password=null; ip=null; port=null; db=null; url=null; driver=null;
        return this
    }

    ConnectionGenerator setDatasource(Map map){
        vendor  = map['vendor'] ?: map['VENDOR']
        user    = map['user'] ?: map['USER'] ?: map['id'] ?: map['ID']
        password= map['password'] ?: map['PASSWORD']
        ip      = map['ip'] ?: map['IP']
        port    = map['port'] ?: map['PORT']
        db      = map['db'] ?: map['DB']
        url     = map['url'] ?: map['URL']
        driver  = map['driver'] ?: map['DRIVER']
        return this
    }

    Map<String, String> generateDataBaseInfoMap(){
        Map o = [
            vendor  : vendor ?: ORACLE,
            user    : user,
            password: password,
            ip      : ip ?: "127.0.0.1",
            port    : port ?: "1521",
            db      : db ?: "orcl",
        ]
        o['url']    = url ?: getURLProtocol(o.vendor, o.ip, o.port, o.db)
        o['driver'] = driver ?: getDriverName(o.vendor)
        logger.debug(o.toMapString())
        return o
    }

    Connection generate(Map map){
        setDatasource(map)
        return generate()
    }

    static Connection generateByMap(Map map){
        ConnectionGenerator conGenerator = new ConnectionGenerator().setDatasource(map)
        return conGenerator.generate()
    }

    Connection generate(){
        Sql sql = generateSqlInstance()
        //- Log Debug
//        generateDataBaseInfoMap()
        //- Generate Connection
        return sql.getConnection()
    }

    Sql generateSqlInstance(){
        Map<String, String> map = generateDataBaseInfoMap()
        return Sql.newInstance(map.url, map.user, map.password, map.driver)
    }

    String getDriverName(String vendor){
        String driver
        switch (vendor?.toUpperCase()){
            case ORACLE:
                driver = "oracle.jdbc.driver.OracleDriver"
                break
            case TIBERO:
                driver = "com.tmax.tibero.jdbc.TbDriver"
                break
            case MYSQL:
                driver = 'com.mysql.jdbc.Driver'
                break
            case MSSQL:
                driver = 'com.microsoft.sqlserver.jdbc.SQLServerDriver'
                break
            case INFORMIX:
                driver = 'com.informix.jdbc.IfxDriver'
                break
            case ALTIBASE:
                driver = 'Altibase.jdbc.driver.AltibaseDriver'
                break
            case SYBASE:
                driver = 'com.sybase.jdbc3.jdbc.SybDriver'
                break
            case IBM_DB:
                driver = 'com.ibm.db2.jcc.DB2Driver'
                break
            default:
                driver = "oracle.jdbc.driver.OracleDriver"
                break
        }
        return driver
    }

    String getURLProtocol(String vendor, String ip, String port, String db){
        String URLProtocol
        switch (vendor?.toUpperCase()){
            case ORACLE:
                URLProtocol = "jdbc:oracle:thin:@${ip}:${port}:${db}"
                break
            case TIBERO:
                URLProtocol = "jdbc:tibero:thin:@${ip}:${port}:${db}"
                break
            case MYSQL:
                URLProtocol = "jdbc:mysql://${ip}:${port}/${db}"
                break
            case MSSQL:
                URLProtocol = "jdbc:sqlserver:${ip}:${port}:${db}"
                break
            case INFORMIX:
                URLProtocol = "jdbc:informix-sqli:${ip}:${port}:${db}"
                break
            case ALTIBASE:
                URLProtocol = "jdbc:Altibase:${ip}:${port}:${db}"
                break
            case SYBASE:
                URLProtocol = "jdbc:sybase:Tds:${ip}:${port}:${db}"
                break
            case IBM_DB:
                URLProtocol = "jdbc:db2:${ip}:${port}:${db}"
                break
            default:
                URLProtocol = "jdbc:oracle:thin:@${ip}:${port}:${db}"
                break
        }
        return URLProtocol
    }

}