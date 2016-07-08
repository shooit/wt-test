package shooit.database

import scalikejdbc._
import shooit.datamodel.Product


object ProductTable {

  def createTable()
                 (implicit session: DBSession): Boolean = {
    DB autoCommit { implicit session: DBSession  =>
      sql"""
        CREATE TABLE products (
         id VARCHAR,
         name VARCHAR,
         brand VARCHAR,
         notes VARCHAR,
         category VARCHAR,
         price FLOAT,
         FOREIGN KEY (category) REFERENCES taxonomies(id),
         PRIMARY KEY (id)
       )
      """.execute.apply()
    }
  }

  def insertProduct(p: Product)
                   (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        INSERT INTO products ( id, name, brand, notes, category, price )
        VALUES ( ${p.id}, ${p.name}, ${p.brand}, ${p.description}, ${p.category}, ${p.price} )
      """.update.apply()
    }
  }

  def insertProducts(ps: Seq[Product])
                    (implicit session: DBSession): Seq[Int] = {
    ps.map(insertProduct)
  }

  def updatePrice(id: String, newPrice: Double)
                 (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        UPDATE products SET price = $newPrice WHERE id = $id
      """.update.apply()
    }
  }

  def getProductsInCategory(categoryId: String)
                           (implicit session: DBSession): Seq[Product] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT * FROM products WHERE category = $categoryId
      """.map(rs => Product(rs)).list.apply()
    }
  }

  def getProductsInCategoryAndBelow(categoryId: String)
                                   (implicit session: DBSession): Seq[Product] = {
    DB autoCommit { implicit session: DBSession =>
      val children = ChildrenTable.getChildrenIds(categoryId)


      sql"""

      """

    }
  }
}
