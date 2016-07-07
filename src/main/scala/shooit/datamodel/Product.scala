package shooit.datamodel

import scalikejdbc.WrappedResultSet


case class Product(id: String,
                   name: String,
                   brand: String,
                   notes: String,
                   category: String,
                   price: Double) extends Asset

object Product {
  def apply(rs: WrappedResultSet): Product = {
    Product(
      rs.string("id"),
      rs.string("name"),
      rs.string("brand"),
      rs.string("notes"),
      rs.string("category"),
      rs.double("price")
    )
  }
}
