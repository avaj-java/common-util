package jaemisseo.man.util

/**
 * Created by sujkim on 2017-03-29.
 */
class PropertiesGenerator {



    /**
     * Parameters from Terminal(Command Line) Option
     * @param args
     * @return
     */
    Map genPropertyValueMap(String[] args){
        Map propertyValueMap = [
            ''      : [],
            '--'    : []
        ]
        //Generate Value Listing Map (Key starts with '-')
        if (args){
            String nowKey = ''

            args.each{
                it = it.replaceAll('\\^\\*', '\\*')         // Command Line(GitBash) Asterik Issue..  no use *, use ^*

                //OPTION: --VALUE
                //RESULT: propertyValueMap['--'] = [VALUE1, VALUE2...]
                if (it.startsWith('--')){
                    if (!propertyValueMap['--'])
                        propertyValueMap['--'] = []
                    String value = it.substring(2, it.length())
                    propertyValueMap['--'] << value

                //OPTION: -PROPERTY.KEY.NAME=VALUE
                //RESULT: propertyValueMap['PROPERTY.KEY.NAME'] = VALUE
                }else if (it.startsWith('-')){
                    int indexEqualMark = it.indexOf('=')
                    if (indexEqualMark != -1){
                        String beforeEqualMark = (it.startsWith('-')) ? it.substring(1, indexEqualMark) : ''
                        def afterEqualMark = it.substring(indexEqualMark + 1)
                        propertyValueMap[beforeEqualMark] = afterEqualMark ?: ''
                        nowKey = ''
                    }else {
                        nowKey = it.substring(1, it.length())
                        if (!propertyValueMap[nowKey])
                            propertyValueMap[nowKey] = []
                    }

                //OPTION: -PROPERTY.KEY.NAME VALUE1 VALUE2 ...
                //RESULT: propertyValueMap['PROPERTY.KEY.NAME'] = [VALUE1, VALUE2, ...]
                }else{
                    if (!propertyValueMap[nowKey])
                        propertyValueMap[nowKey] = []
                    propertyValueMap[nowKey] << it
                }
            }

        }
        return propertyValueMap
    }

}
