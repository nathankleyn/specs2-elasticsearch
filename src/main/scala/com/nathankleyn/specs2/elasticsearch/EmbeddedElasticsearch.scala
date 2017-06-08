package com.nathankleyn.specs2.elasticsearch

import java.nio.file.{Files, Path}

import scala.util.Try

import org.apache.commons.io.FileUtils
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.node.{Node, NodeBuilder}

case class EmbeddedElasticsearch(clusterName: String, dataPath: Path, node: Node) {
  def start(): Unit = node.start()
  def client: Client = node.client()
  def stop(): Unit = {
    node.close()
    Try(FileUtils.forceDelete(dataPath.toFile))
  }
}

object EmbeddedElasticsearch {
  def apply(port: Int, javaPort: Int): EmbeddedElasticsearch = {
    val clusterName = s"EmbeddedElasticsearch-$port"
    val dataPath = Files.createTempDirectory("embedded-elasticsearch-data")
    val node = NodeBuilder.nodeBuilder().settings(clusterSettings(clusterName, dataPath, port, javaPort)).build
    EmbeddedElasticsearch(clusterName, dataPath, node)
  }

  private def clusterSettings(clusterName: String, dataPath: Path, port: Int, javaPort: Int) =
    ImmutableSettings.settingsBuilder.
      put("path.data", dataPath.toString).
      put("cluster.name", clusterName).
      put("http.port", port).
      put("transport.tcp.port", javaPort).
      build
}
