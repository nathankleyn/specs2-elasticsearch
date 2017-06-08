# specs2-elasticsearch [![Build Status](https://travis-ci.org/nathankleyn/specs2-elasticsearch.svg?branch=master)](https://travis-ci.org/nathankleyn/specs2-elasticsearch)
Small library with a specs2 helper for testing against a local Elasticsearch cluster using elastic4s.

> **Note:** This library is currently pinned against Elasticsearch `1.5.x`, and thus may not be suitable for your use-case. It will be upgraded for later versions of Elasticsearch shortly.

## Usage

Add the dependency to your SBT project:

```scala
resolvers += Resolver.bintrayRepo("nathankleyn", "maven")
libraryDependencies ++= Seq(
  "com.nathankleyn" %% "specs2-elasticsearch" % "0.1.0"
)
```

> **Note:** Builds are currently only available for Scala `2.11` because Elasticsearch `1.5.x` is not available for Scala `2.12`. This will be fixed once this library is updated to use Scala `2.12`.

You can then use it by mixing in the `ElasticsearchSpec` trait into any `Specification` you have (mutable or immutable, it's a `BeforeAfterAll` hook):

```scala
import com.sksamuel.elastic4s.ElasticDsl._
import org.specs2.Specification
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.FutureMatchers

class ElasticsearchSpecSpec(implicit ee: ExecutionEnv) extends Specification with FutureMatchers with ElasticsearchSpec {
  import ElasticsearchSpecSpec._

  def is = sequential ^ s2"""
    Given I have extended the ElasticsearchSpec
    When I run the tests
    Then I should be able to add a document $testAddDocument
    """

  private def testAddDocument =
    e4sClient.execute {
      index into "test" / "testMapping" fields (
        "field1" -> "foo",
        "field2" -> 123
      ) id 123
    }.map(_.isCreated) must beTrue.await
}
```

## License

This repository is [licensed with the MIT license](/LICENSE).
