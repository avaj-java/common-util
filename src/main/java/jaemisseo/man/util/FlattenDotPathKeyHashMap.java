package jaemisseo.man.util;

import java.util.HashMap;
import java.util.Map;

public class FlattenDotPathKeyHashMap<K,V> extends HashMap<K,V> {

    public FlattenDotPathKeyHashMap(){
    }

    public FlattenDotPathKeyHashMap(Map<? extends K, ? extends V> m){
        super(m);
    }


}

