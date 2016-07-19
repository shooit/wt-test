package shooit.database.taxonomies

import scalikejdbc._
import shooit.datamodel.taxonomies.Product


object ProductTable {

  /**
    * Creates the product table
    */
  def createTable()
                 (implicit session: DBSession): Boolean = {
    DB autoCommit { implicit session: DBSession  =>
      sql"""
        CREATE TABLE IF NOT EXISTS products (
         id VARCHAR,
         name VARCHAR,
         brand VARCHAR,
         description VARCHAR,
         category VARCHAR,
         price FLOAT,
         FOREIGN KEY (category) REFERENCES taxonomies(id),
         PRIMARY KEY (id)
       )
      """.execute.apply()
    }
  }

  /**
    * Inserts a product
    */
  def insertProduct(p: Product)
                   (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        INSERT INTO products ( id, name, brand, description, category, price )
        VALUES ( ${p.id}, ${p.name}, ${p.brand}, ${p.description.getOrElse("")}, ${p.category}, ${p.price} )
      """.update.apply()
    }
  }

  /**
    * Inserts products
    */
  def insertProducts(ps: Seq[Product])
                    (implicit session: DBSession): Seq[Int] = {
    ps.map(insertProduct)
  }

  /**
    * Updates the price for a product
    */
  def updatePrice(id: String, newPrice: Double)
                 (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        UPDATE products SET price = $newPrice WHERE id = $id
      """.update.apply()
    }
  }

  /**
    * Changes the category for a product
    */
  def changeCategory(id: String, newCategory: String)
                    (implicit session: DBSession): Int = {
    DB autoCommit {implicit session: DBSession =>
      sql"""
        UPDATE products SET category = $newCategory WHERE id = $id
      """.update.apply()
    }
  }

  /**
    * Get a product by id
    */
  def findById(id: String)
              (implicit session: DBSession): Option[Product] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT * FROM products WHERE id = $id
      """.map(rs => Product(rs)).single.apply()
    }
  }

  /**
    * Get all products
    */
  def getAllProducts(implicit session: DBSession): List[Product] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT * FROM products
      """.map(rs => Product(rs)).list.apply()
    }
  }

  /**
    * Gets the products from a specific category
    */
  def getProductsInCategory(categoryId: String)
                           (implicit session: DBSession): Seq[Product] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT * FROM products WHERE category = $categoryId
      """.map(rs => Product(rs)).list.apply()
    }
  }

  /**
    * Gets the products from a specific category and their sub categories
    */
  def getProductsInCategoryAndBelow(categoryId: String)
                                   (implicit session: DBSession): Seq[Product] = {
    DB autoCommit { implicit session: DBSession =>
      val categoryIds = ChildrenTable.getAllChildrenIds(categoryId)

      sql"""
        SELECT * FROM products WHERE category IN ($categoryIds)
      """.map(rs => Product(rs)).list.apply()
    }
  }

  /**
    * Deletes a product
    */
  def deleteProduct(id: String)
                   (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        DELETE FROM products WHERE id = $id
      """.update.apply()
    }
  }
}
