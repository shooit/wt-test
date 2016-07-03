import javax.servlet.ServletContext

import org.scalatra.LifeCycle
import shooit.service.WTServlet

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    // Mount servlets.
    context.mount(new WTServlet, "/wt-test/*")
  }
}
