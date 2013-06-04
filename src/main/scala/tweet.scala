import com.tinkerpop.blueprints._
import com.tinkerpop.blueprints.Direction._
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion._
import com.tinkerpop.blueprints.Query.Compare._
import com.thinkaurelius.titan.core._
import java.lang.{Integer => JInteger, Long => JLong}
import scala.collection.JavaConversions._

trait TitanProvider {
  val conf = new org.apache.commons.configuration.BaseConfiguration()
  conf.setProperty("storage.backend","cassandra")
  conf.setProperty("storage.hostname","127.0.0.1")
  val g = TitanFactory.open(conf)
  println("Opened graph")
}

/*
User[userId] -- tweets[time] --> Tweet[text]

Graph index on userId property for fast user vertex lookups
Vertex index on time property of tweets edges for fast "n most-recent tweets" queries
*/
object CreateTweets extends App with TitanProvider {
  g.makeType().name("userId").unique(OUT).dataType(classOf[String]).indexed(classOf[Vertex]).makePropertyKey()
  val timeType = g.makeType().name("time").unique(OUT).dataType(classOf[JLong]).makePropertyKey()
  g.makeType.name("tweets").primaryKey(timeType).makeEdgeLabel()
  g.commit()
  println("Set up schema")

  val u = g.addVertex(null)
  u.setProperty("userId", "u1")
  g.commit()
  println("Added userId=u1")

  def createTweet(user: Vertex, text: String, time: Int): (Edge, Vertex) = {
    val tweet = g.addVertex(null)
    tweet.setProperty("text", text)
    val edge = g.addEdge(null, user, tweet, "tweets")
    edge.setProperty("time", -time) //Titan sorts ascending so negate timestamp to get descending order https://groups.google.com/d/msg/aureliusgraphs/LkBwqi0VCNQ/uyyEq7acUBwJ
    (edge, tweet)
  }

  def fillWithTweets(userId: String, n: Int = 1000) {
    val user = g.getVertices("userId", userId).head
    for (i <- (1 to n)) createTweet(user, "This is tweet " + i, i)
  }

  fillWithTweets("u1")
  g.commit()
  println("Filled graph with tweets")
}

/*
Print text of the n most-recent tweets for a specific user
*/
object QueryTweets extends App with TitanProvider {
  val user = g.getVertices("userId", "u1").head
  user.query.labels("tweets").limit(10).vertices.map(_.getProperty("text").asInstanceOf[String]).foreach(println)

  val count = user.query.labels("tweets").count
  println("count = " + count)
}
