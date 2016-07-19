package shooit.service

import org.json4s.DefaultFormats
import org.scalatra.ScalatraServlet
import scalikejdbc.DBSession


abstract class BaseServlet extends ScalatraServlet {
  implicit val session: DBSession
  implicit val formats = DefaultFormats
  def notFound404(id: String, assetType: String = "asset") = {
    halt(status = 404, reason = "Asset Not Found", body = s"Could not find $assetType: $id")
  }
}
