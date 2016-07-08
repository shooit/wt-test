import javax.servlet.ServletContext

import org.scalatra.LifeCycle
import shooit.service.{ProductServlet, TaxonomyServlet, UserServlet}

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    // Mount servlets.
    context.mount(new UserServlet, "/wt-test/users/*")
    context.mount(new TaxonomyServlet, "/wt-test/taxonomies/*")
    context.mount(new ProductServlet, "/wt-test/products/*")
  }
}
