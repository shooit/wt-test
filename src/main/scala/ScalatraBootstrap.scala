import javax.servlet.ServletContext

import org.scalatra.LifeCycle
import scalikejdbc.{AutoSession, ConnectionPool}
import shooit.app.{AssetLoader, TaxonomyLoader}
import shooit.service.assets.{MachineServlet, UserServlet}
import shooit.service.taxonomies.{ProductServlet, TaxonomyServlet}

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {
    val props = System.getProperties

    //connect to the db passed by -DdbUrl or default to in memory
    val dbUrl = props.getProperty("dbUrl", "jdbc:h2:mem:wt-test")

    //bring in both the drivers just in case
    Class.forName("org.h2.Driver")
    Class.forName("org.sqlite.JDBC")

    ConnectionPool.singleton(dbUrl, null, null)
    implicit val session = AutoSession

    //load the data if -DloadData is present
    if (props.containsKey("loadData")) {
      AssetLoader.load()
      TaxonomyLoader.load()
    }

    // Mount servlets.
    context.mount(new TaxonomyServlet, "/wt-test/taxonomies/*")
    context.mount(new ProductServlet, "/wt-test/products/*")
    context.mount(new UserServlet, "/wt-test/users/*")
    context.mount(new MachineServlet, "/wt-test/machines/*")
  }
}
