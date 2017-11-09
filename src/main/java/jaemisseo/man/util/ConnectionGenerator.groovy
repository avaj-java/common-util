package jaemisseo.man.util

import groovy.sql.Sql
import jaemisseo.man.bean.SqlSetup

import java.sql.Connection

/**
 * Created by sujkim on 2017-02-19.
 */
class ConnectionGenerator {

    ConnectionGenerator(){}

    ConnectionGenerator(Map<String, String> map){
        setDatasource(map)
    }

    ConnectionGenerator(SqlSetup sqlSetup){
        setDatasource(sqlSetup)
    }

        static final String ORACLE = "ORACLE"
        static final String TIBERO = "TIBERO"
        String vendor, user, password, ip, port, db, url, driver



    ConnectionGenerator init() {
        vendor = null; user = null; password = null; ip = null; port = null; db = null; url = null; driver = null;
        return this
    }

    ConnectionGenerator setDatasource(Map map) {
        vendor  = map['vendor']
        user    = map['user']
        password = map['password']
        ip      = map['ip']
        port    = map['port']
        db      = map['db']
        url     = map['url']
        driver  = map['driver']
        return this
    }

    ConnectionGenerator setDatasource(SqlSetup sqlOpt) {
        vendor  = sqlOpt.vendor
        user    = sqlOpt.user
        password = sqlOpt.password
        ip      = sqlOpt.ip
        port    = sqlOpt.port
        db      = sqlOpt.db
        url     = sqlOpt.url
        driver  = sqlOpt.driver
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
        return o
    }

    ConnectionGenerator setup(){
        url = url ?: "${getURLProtocol(vendor)}@${ip}:${port}:${db}"
        driver = driver ?: getDriverName(vendor)
        return this
    }

    Connection generate(Map map){
        setDatasource(map)
        return generate()
    }

    Connection generate(){
        Sql sql = generateSqlInstance()
        return sql.getConnection()
    }

    Sql generateSqlInstance(){
        Map<String, String> map = generateDataBaseInfoMap()
        return Sql.newInstance(map.url, map.user, map.password, map.driver)
    }

    String getDriverName(String vendor){
        String driver
        switch (vendor?.toUpperCase()) {
            case ORACLE:
                driver = "oracle.jdbc.driver.OracleDriver"
                break
            case TIBERO:
                driver = "com.tmax.tibero.jdbc.TbDriver"
                break
            default:
                driver = "oracle.jdbc.driver.OracleDriver"
                break
        }
        return driver
    }

    String getURLProtocol(String vendor, String ip, String port, String db){
        String url
        switch (vendor?.toUpperCase()) {
            case ORACLE:
                url = "jdbc:oracle:thin:@${ip}:${port}:${db}"
                break
            case TIBERO:
                url = "jdbc:tibero:thin:@${ip}:${port}:${db}"
                break
            default:
                url = "jdbc:oracle:thin:@${ip}:${port}:${db}"
                break
        }
        return url
    }

}
