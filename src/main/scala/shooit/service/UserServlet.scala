package shooit.service

import org.scalatra.ScalatraServlet
import org.json4s.native.Serialization
import org.json4s.DefaultFormats
import scalikejdbc.ConnectionPool
import shooit.datamodel.User
import scalikejdbc._
import shooit.database.{TaxonomyTable, UserTable}


class UserServlet extends ScalatraServlet {
  implicit val formats = DefaultFormats

  //Connect to the database
  val dbUrl = "jdbc:sqlite:/opt/devel/data/wt-test/wt-test.db"
  Class.forName("org.sqlite.JDBC")
  ConnectionPool.singleton(dbUrl, null, null)

  implicit val session = AutoSession

  //GET methods

  get("/") {
    params.get("name") match {
      case Some(name) => Serialization.write(UserTable.findByName(name))
      case None       => Serialization.write(UserTable.getAllUsers)
    }
  }


  get("/:id") {
    UserTable.findById(params("id")) match {
      case Some(user) => Serialization.write(user)
      case None       => "User not found"
    }
  }



  //POST methods
  post("/") {
    val users = Serialization.read[Seq[User]](request.body)

    val ignore = params.get("ignore") match {
      case None    => false
      case Some(s) =>
        s match {
          case "true" => true
          case _      => false
        }
    }

    val response = UserTable.insertUsers(users, ignore)

    s"Successfully inserted ${response.size} users into the table"
  }



  //PUT methods
  put("/:id") {
    val id = params("id")
    val notes = request.body

    UserTable.addNotes(id, notes) match {
      case 1 => s"Successfully added/changed notes for user $id!"
      case 0 => s"Could not add/change notes for user $id!"
      case _ => s"Unexpected behavior!"
    }
  }

  //DELETE methods
  delete("/:id") {
    val id = params("id")

    UserTable.deleteUser(id) match {
      case 1 => s"Successfully deleted user $id!"
      case 0 => s"Could not delete user $id!"
      case _ => s"Unexpected behavior!"
    }
  }

  delete("/") {
    params.get("id") match {
      case Some(id) =>
        UserTable.deleteUser(id) match {
          case 1 => s"Successfully deleted user $id!"
          case 0 => s"Could not delete user $id!"
          case _ => s"Unexpected behavior!"
        }
      case None => "An id must be provided to delete user"
    }
  }

  delete("/") {
    "DON'T TRY TO DELETE EVERYTHING PLEASE"
  }
}
