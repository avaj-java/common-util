package jaemisseo.man.util.test

import org.junit.Before
import org.junit.Ignore
import org.junit.experimental.theories.DataPoints
import org.junit.runners.Parameterized

import javax.jws.HandlerChain
import javax.xml.bind.annotation.XmlAnyAttribute
import javax.xml.ws.FaultAction
import javax.xml.ws.ServiceMode
import java.lang.annotation.Documented

/**
 * Created by sujkim on 2017-06-04.
 */
@ServiceMode
class AnnotationTest2 {

    @Parameterized.Parameter
    @XmlAnyAttribute
    String testFiled

    long testFiled2

    int testFiled3


    @Before
    @Ignore
    void testMethod(){
    }

}
