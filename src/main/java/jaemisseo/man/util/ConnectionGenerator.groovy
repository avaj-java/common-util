package jaemisseo.man.util

import groovy.sql.Sql

import java.sql.Connection

/**
 * Created by sujkim on 2017-02-19.
 */
class ConnectionGenerator {

    ConnectionGenerator(){}

    ConnectionGenerator(Map<String, String> map){
        setDatasource(map)
    }

        static final String ORACLE = "ORACLE"
        static final String TIBERO = "TIBERO"
        String vendor, id, pw, ip, port, db, url, driver



    ConnectionGenerator init() {
        vendor = null; id = null; pw = null; ip = null; port = null; db = null; url = null; driver = null;
        return this
    }

    ConnectionGenerator setDatasource(Map map) {
        vendor  = map['vendor']
        id      = map['id']
        pw      = map['pw']
        ip      = map['ip']
        port    = map['port']
        db      = map['db']
        url     = map['url']
        driver  = map['driver']
        return this
    }

    Map<String, String> generateDataBaseInfoMap(){
        Map o = [
            vendor  : vendor ?: ORACLE,
            id      : id,
            pw      : pw,
            ip      : ip ?: "127.0.0.1",
            port    : port ?: "1521",
            db      : db ?: "orcl",
        ]
        o['url']    = url ?: "${getURLProtocol(o.vendor)}@${o.ip}:${o.port}:${o.db}"
        o['driver'] = driver ?: getDriverName(o.vendor)
        return o
    }

    Connection generate(Map map){
        setDatasource(map)
        return generate()
    }

    Connection generate(){
        Sql sql
        Map<String, String> m = generateDataBaseInfoMap()
        sql = Sql.newInstance(m.url, m.id, m.pw, m.driver)
        return sql.getConnection()
    }

    String getDriverName(String vendor){
        vendor = (vendor) ?: ORACLE
        vendor = vendor?.toUpperCase()
        String driver = ''
        //Get By Vendor
        if (vendor.equals(ORACLE)) driver = 'oracle.jdbc.driver.OracleDriver'
        else if (vendor.equals(TIBERO)) driver = 'com.tmax.tibero.jdbc.TbDriver'
        return driver
    }

    String getURLProtocol(String vendor){
        vendor = (vendor) ?: ORACLE
        vendor = vendor?.toUpperCase()
        String URLProtocol = ''
        //Get By Vendor
        if (vendor.equals(ORACLE)) URLProtocol = 'jdbc:oracle:thin:'
        else if (vendor.equals(TIBERO)) URLProtocol = 'jdbc:tibero:thin:'
        return URLProtocol
    }

}
