package shooit.service

import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import org.scalatra.ScalatraServlet
import shooit.database.ProductTable
import shooit.datamodel.Product


class ProductServlet extends ScalatraServlet {
  implicit val formats = DefaultFormats

  get("/") {
    params.get("category") match {
      case Some(cat) =>
        params.get("sub") match {
          case Some(_) => Serialization.writePretty(ProductTable.getProductsInCategoryAndBelow(cat))
          case None    => Serialization.writePretty(ProductTable.getProductsInCategory(cat))
        }
      case None => Serialization.writePretty(ProductTable.getProducts)
    }
  }

  get("/:id") {
    Serialization.writePretty(ProductTable.getProduct(params("id")))
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
    ProductTable.deleteProduct(params("id"))
  }
}
