import com.ibm.etcd.client.EtcdClient
import com.ibm.etcd.client.KeyUtils.bs
import com.ibm.etcd.client.KvStoreClient
import com.ibm.etcd.client.kv.WatchUpdate
import io.grpc.stub.StreamObserver

fun main() {
    val client: KvStoreClient = EtcdClient.forEndpoint("localhost", 2379).withPlainText().build()

    val kvClient = client.kvClient

    kvClient.put(bs("key"), bs("value")).sync()

    val getValue  = kvClient.get(bs("key")).sync()

    println(getValue.kvsList[0].value.toStringUtf8())


    //For example, let’s imagine that you have a stateful service and want to update the log level from debug to info
    //during runtime without having to restart the application.

    val observer = object: StreamObserver<WatchUpdate> {

        override fun onNext(value:WatchUpdate) {
            println("watch event: $value")
        }
        override fun onError(t:Throwable) {
            println("watch error: $t")
        }
        override fun onCompleted() {
            println("watch completed")
        }
    }

    kvClient.put(bs("loglevel"), bs("debug")).sync()


    //var watch = kvClient.watch(bs("loglevel")).start(observer)
    var watch = kvClient.watch(bs("Books")).asPrefix().start(observer);

        // simulating some work
    Thread.sleep(1000000)

    watch.close()

}
