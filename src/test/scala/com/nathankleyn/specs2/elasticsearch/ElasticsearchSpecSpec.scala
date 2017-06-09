package com.nathankleyn.specs2.elasticsearch

import scala.collection.JavaConverters._
import scala.concurrent.duration._

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
    Then I should be able to create an index $testCreateIndex
    And I should be able to add a document to that index $testAddDocument
    And I should be able to read that document back $testReadDocument
    And I should be able to delete an index $testDeleteIndex
    """

  private def testCreateIndex =
    e4sClient.execute {
      create index indexName mappings mapping
    }.map(_.isAcknowledged) must beTrue.awaitFor(5.seconds)

  private def testAddDocument =
    e4sClient.execute {
      index into indexName / mappingName fields (
        "field1" -> "foo",
        "field2" -> 123
      ) id 123
    }.map(_.isCreated) must beTrue.awaitFor(5.seconds)

  private def testReadDocument =
    e4sClient.execute {
      get id 123 from indexName / mappingName
    }.filter(_.isExists).map(_.getSourceAsMap.asScala.toMap) must be_==[Map[String, Any]](Map(
      "field1" -> "foo",
      "field2" -> 123
    )).awaitFor(5.seconds)

  private def testDeleteIndex =
    e4sClient.execute {
      delete index indexName
    }.map(_.isAcknowledged) must beTrue.awaitFor(5.seconds)
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
