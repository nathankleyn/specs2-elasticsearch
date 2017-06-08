package com.nathankleyn.specs2.elasticsearch

import scala.collection.JavaConverters._

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.mappings.MappingDefinition
import org.specs2.Specification
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.FutureMatchers

class ElasticsearchSpecSpec(implicit ee: ExecutionEnv) extends Specification with FutureMatchers with ElasticsearchSpec {
  import ElasticsearchSpecSpec._

  def is = sequential ^ s2"""
    Given I have extended the ElasticsearchSpec
    When I run the tests
    Then I should be able to create an index testCreateIndex
    And I should be able to add a document to that index $testAddDocument
    And I should be able to read that document back testReadDocument
    """

  private def testCreateIndex =
    e4sClient.execute {
      create index indexName mappings mapping
    }.map(_.isAcknowledged) must beTrue.await

  private def testAddDocument =
    e4sClient.execute {
      index into indexName / mappingName fields (
        "field1" -> "foo",
        "field2" -> 123
      ) id 123
    }.map(_.isCreated) must beTrue.await

  private def testReadDocument =
    e4sClient.execute {
      get id 123 from indexName / mappingName
    }.filter(_.isExists).map(_.getSourceAsMap.asScala).await must_== Map(
      "field1" -> "foo",
      "field2" -> 123
    )
}

object ElasticsearchSpecSpec {
  val indexName: String = "test"
  val mappingName: String = "testMapping"
  val mapping: MappingDefinition =
    mappingName as (
      "field1" typed StringType index NotAnalyzed,
      "field2" typed LongType
    )
}
