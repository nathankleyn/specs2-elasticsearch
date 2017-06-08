package com.nathankleyn.specs2.elasticsearch

import java.net.ServerSocket

import com.sksamuel.elastic4s.ElasticClient
import org.elasticsearch.client.Client
import org.specs2.specification.BeforeAfterAll

trait ElasticsearchSpec extends BeforeAfterAll {
  val esPort: Int = findFreePort
  val esJavaPort: Int = findFreePort

  private lazy val esNode = EmbeddedElasticsearch(esPort, esJavaPort)
  lazy val esClusterName = esNode.clusterName
  lazy val esClient: Client = esNode.client
  lazy val e4sClient: ElasticClient = ElasticClient.fromClient(esClient)

  // We have seperate methods here from beforeAll/afterAll because if you want to mixin multiple
  // Before/AfterAll traits you are going to need to call these directly.
  def esBefore(): Unit = esNode.start()
  def esAfter(): Unit = esNode.stop()

  def beforeAll(): Unit = esBefore()
  def afterAll(): Unit = esAfter()

  /**
    * Finds a free port on the system.
    *
    * It does this using `ServerSocket` with a port of 0, which will look for an available port (normally in the
    * ephemeral range). We open a socket, grab the port, close the socket and return the port. This means there is a
    * small chance of a race condition here, so be aware.
    */
  private def findFreePort: Int = {
    val ss = new ServerSocket(0)
    val port = ss.getLocalPort
    ss.close()
    port
  }
}
