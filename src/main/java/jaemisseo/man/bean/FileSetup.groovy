package jaemisseo.man.bean

import jaemisseo.man.util.Option


/**
 * Created by sujkim on 2017-02-19.
 */
class FileSetup extends Option<FileSetup> {

    String encoding = 'utf-8'
    String lineBreak
    String lastLineBreak = ''
    String backupPath

    Boolean modeAutoMkdir = false
    Boolean modeAutoBackup = false
    Boolean modeAutoOverWrite = false

}
