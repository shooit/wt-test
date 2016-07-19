package shooit.service.assets


import org.json4s.native.Serialization
import scalikejdbc.DBSession
import shooit.database.assets.{UserNotesTable, UserTable}
import shooit.datamodel.assets.User
import shooit.service.BaseServlet


class UserServlet(implicit val session: DBSession) extends BaseServlet {

  get("/") {
    params.get("name") match {
      case Some(name) => Serialization.writePretty(UserTable.findByName(name))
      case None => Serialization.writePretty(UserTable.getAllUsers)
    }
  }

  get("/:id") {
    val id = params("id")

    UserTable.findById(id) match {
      case Some(u) => Serialization.writePretty(u)
      case None    => notFound404(id, "user")
    }
  }

  post("/") {
    val users = Serialization.read[Seq[User]](request.body)
    UserTable.insertUsers(users)
  }

  put("/:id") {
    val id = params("id")

    params.get("note") match {
      case Some(n) => UserNotesTable.insertNote(id, n)
      case None    =>
    }
  }

  delete("/:id") {
    val id = params("id")
    UserTable.deleteUser(id) match {
      case 1 => s"Successfully deleted user $id!"
      case 0 => s"Could not delete user $id!"
      case _ => s"Unexpected behavior!"
    }
  }

}


