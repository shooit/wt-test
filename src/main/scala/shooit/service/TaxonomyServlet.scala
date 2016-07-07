package shooit.service

import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import org.scalatra.ScalatraServlet
import scalikejdbc.{AutoSession, ConnectionPool}
import shooit.database.{TaxonomyTable, UserTable}


class TaxonomyServlet extends ScalatraServlet {
  implicit val formats = DefaultFormats

  //Connect to the database
  val dbUrl = "jdbc:sqlite:/opt/devel/data/wt-test/wt-test.db"
  Class.forName("org.sqlite.JDBC")
  ConnectionPool.singleton(dbUrl, null, null)

  implicit val session = AutoSession

  get("/") {
    params.get("name") match {
      case Some(name) => Serialization.write(TaxonomyTable.findByName(name))
      case None       => Serialization.write(TaxonomyTable.getAllTaxonomies)
    }
  }

  get("/:id") {
    TaxonomyTable.findById(params("id")) match {
      case Some(t) => Serialization.write(t)
      case None    => "Taxonomy not found"
    }
  }

  put("/:id") {
    val id = params("id")
    val notes = request.body

    UserTable.addNotes(id, notes) match {
      case 1 => s"Successfully added/changed notes for taxonomy $id!"
      case 0 => s"Could not add/change notes for taxonomy $id!"
      case _ => s"Unexpected behavior!"
    }
  }

  delete("/:id") {
    val id = params("id")

    TaxonomyTable.deleteTaxonomy(id) match {
      case 1 => s"Successfully deleted taxonomy $id!"
      case 0 => s"Could not delete taxonomy $id!"
      case _ => s"Unexpected behavior!"
    }
  }
}
