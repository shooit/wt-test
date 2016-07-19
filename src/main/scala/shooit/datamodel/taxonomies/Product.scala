package shooit.datamodel.taxonomies

import scalikejdbc.WrappedResultSet


case class Product(id: String,
                   name: String,
                   brand: String,
                   description: Option[String],
                   category: String,
                   price: Double)

object Product {

  def apply(rs: WrappedResultSet): Product = {

    //a different way to handle missing strings
    implicit def str2opt(s: String): Option[String] = {
      s match {
        case "" => None
        case s: String => Option(s)
      }
    }

    Product(
      rs.string("id"),
      rs.string("name"),
      rs.string("brand"),
      rs.string("description"),
      rs.string("category"),
      rs.double("price")
    )
  }
}
