package shooit.service.taxonomies

import org.json4s.native.Serialization
import scalikejdbc.DBSession
import shooit.database.taxonomies.TaxonomyTable
import shooit.datamodel.taxonomies.Taxonomy
import shooit.service.BaseServlet


class TaxonomyServlet(implicit val session: DBSession) extends BaseServlet {

  get("/") {
    params.get("name") match {
      case Some(name) => Serialization.writePretty(TaxonomyTable.findByName(name))
      case None       => Serialization.writePretty(TaxonomyTable.getAllTaxonomies)
    }
  }

  get("/:id") {
    val id = params("id")

    params.get("tree") match {
      case Some(p) => 
        TaxonomyTable.treeById(id) match {
          case Some(tree) => Serialization.writePretty(tree)
          case None       => notFound404(id, "taxonomy")
        }
      case None =>
        TaxonomyTable.findById(id) match {
          case Some(t) => Serialization.writePretty(t)
          case None    => notFound404(id, "taxonomy")
        }
    }
  }

  post("/") {
    val taxonomies = Serialization.read[Seq[Taxonomy]](request.body)
    val response = TaxonomyTable.insertTaxonomies(taxonomies)

    response
  }

  put("/:id") {
    params.get("parent") match {
      case Some(parent) => TaxonomyTable.updateParent(params("id"), parent)
      case None => "No update passed. Try ?parent=###"
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
