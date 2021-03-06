package shooit.service.taxonomies

import org.json4s.native.Serialization
import scalikejdbc.DBSession
import shooit.database.taxonomies.ProductTable
import shooit.datamodel.taxonomies.Product
import shooit.service.BaseServlet


class ProductServlet(implicit val session: DBSession) extends BaseServlet {

  get("/") {
    params.get("category") match {
      case Some(cat) =>
        params.get("sub") match {
          case Some(_) => Serialization.writePretty(ProductTable.getProductsInCategoryAndBelow(cat))
          case None    => Serialization.writePretty(ProductTable.getProductsInCategory(cat))
        }
      case None => Serialization.writePretty(ProductTable.getAllProducts)
    }
  }

  get("/:id") {
    val id = params("id")

    ProductTable.findById(id) match {
      case Some(p) => Serialization.writePretty(p)
      case None    => notFound404(id, "product")
    }
  }

  post("/") {
    val products = Serialization.read[Seq[Product]](request.body)
    ProductTable.insertProducts(products)
  }

  put("/:id") {
    params.get("category") match {
      case Some(cat) => ProductTable.changeCategory(params("id"), cat)
      case None => "No update passed. Try: ?category=###"
    }
  }

  delete("/:id") {
    val id = params("id")

    ProductTable.deleteProduct(id) match {
      case 1 => s"Successfully deleted product $id!"
      case 0 => s"Could not delete product $id!"
      case _ => s"Unexpected behavior!"
    }
  }
}
