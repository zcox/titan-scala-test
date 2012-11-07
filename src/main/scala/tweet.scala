import com.tinkerpop.blueprints._
import com.tinkerpop.blueprints.Direction._
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion._
import com.tinkerpop.blueprints.Query.Compare._
import com.thinkaurelius.titan.core._
import java.lang.{Integer => JInteger, Long => JLong}
import scala.collection.JavaConversions._

object Tweet {
  def run() {
    val conf = new org.apache.commons.configuration.BaseConfiguration()
    conf.setProperty("storage.backend","cassandra")
    conf.setProperty("storage.hostname","127.0.0.1")
    val g = TitanFactory.open(conf)

    g.createKeyIndex("userId", classOf[Vertex])
    val timeType = g.makeType.name("time").simple.functional(false).dataType(classOf[JInteger]).makePropertyKey()
    g.makeType.name("tweets").primaryKey(timeType).makeEdgeLabel()
    g.stopTransaction(SUCCESS)

    val u = g.addVertex(null)
    u.setProperty("userId", "u1")
    g.stopTransaction(SUCCESS)

    def createTweet(user: Vertex, text: String, time: Int): (Edge, Vertex) = {
      val tweet = g.addVertex(null)
      tweet.setProperty("text", text)
      val edge = g.addEdge(null, user, tweet, "tweets")
      edge.setProperty("time", time)
      (edge, tweet)
    }

    def fillWithTweets(userId: String, n: Int = 1000) {
      val user = g.getVertices("userId", userId).head
      for (i <- (1 to n)) createTweet(user, "This is tweet " + i, i)
    }

    fillWithTweets("u1")
    g.stopTransaction(SUCCESS)

    val user = g.getVertices("userId", "u1").head
    val edges = user.query.labels("tweets").limit(10).edges.map(_.getProperty("time"))
    println("1) edges = " + edges)

    val count = user.query.labels("tweets").count
    println("2) count = " + count)
    val edges2 = user.query.labels("tweets").limit(10).edges.map(_.getProperty("time"))
    println("3) edges = " + edges2)
  }
}