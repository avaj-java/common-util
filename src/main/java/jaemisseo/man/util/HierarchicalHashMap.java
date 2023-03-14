package jaemisseo.man.util;

import java.util.HashMap;
import java.util.Map;

public class HierarchicalHashMap<K,V> extends HashMap<K,V> {

    public HierarchicalHashMap(){
    }

    public HierarchicalHashMap(Map<? extends K, ? extends V> m){
        super(m);
    }

}

