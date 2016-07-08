import java.util.Properties
import javax.servlet.ServletContext

import org.scalatra.LifeCycle
import scalikejdbc.{AutoSession, ConnectionPool}
import shooit.app.DataLoader
import shooit.service.{ProductServlet, TaxonomyServlet}

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {
    val props = new Properties()
    val dbUrl = props.getProperty("dbUrl", "jdbc:h2:mem:wt-test")

    //bring in both the drivers just in case
    Class.forName("org.h2.Driver")
    Class.forName("org.sqlite.JDBC")

    ConnectionPool.singleton(dbUrl, null, null)

    implicit val session = AutoSession

    DataLoader.load()

    // Mount servlets.
    context.mount(new TaxonomyServlet, "/wt-test/taxonomies/*")
    context.mount(new ProductServlet, "/wt-test/products/*")
  }
}
