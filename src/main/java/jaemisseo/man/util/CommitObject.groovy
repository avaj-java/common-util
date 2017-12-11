package jaemisseo.man.util

/**
 * Created by sujung on 2017-04-12.
 */
class CommitObject {

    String id

    CommitObject parent
    CommitObject child
    Map customData = [:]

    Map insertedMap
    Map updatedBeforeMap
    Map updatedAfterMap
    Map deletedMap

    CommitObject(String id){
        this.id = id
    }

    CommitObject gap(Map old, Map now){
        insertedMap = now.findAll{ !old.containsKey(it.key) }
        deletedMap = old.findAll{ !now.containsKey(it.key) }
        updatedBeforeMap = old.findAll{ now.containsKey(it.key) && now[it.key] != old[it.key]  }
        updatedAfterMap = now.findAll{ old.containsKey(it.key) && now[it.key] != old[it.key]  }
        return this
    }

}
