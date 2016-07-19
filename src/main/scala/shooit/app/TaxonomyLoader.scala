package shooit.app

import scalikejdbc.DBSession
import shooit.database.taxonomies.{ChildrenTable, ProductTable, TaxonomyTable}
import shooit.datamodel.taxonomies.{Product, Taxonomy}

object TaxonomyLoader {
  def load()(implicit session: DBSession): Unit = {
    TaxonomyTable.createTable()
    ChildrenTable.createTable()
    ProductTable.createTable()

    val allProducts = Taxonomy("1", "All Departments", None, Set("2", "3", "4", "5"))
    val electronicsOffice = Taxonomy("2", "Electronics And Office", Option("1"), Set("6", "7", "8"))
    val moviesMusicBooks = Taxonomy("3", "Movies, Music & Books", Option("1"), Set())
    val homeFurniturePatio = Taxonomy("4", "Home, Furniture & Patio", Option("1"), Set())
    val homeImprovement = Taxonomy("5", "Home Improvement", Option("1"), Set())

    val tvVideo = Taxonomy("6", "TV & Video", Option("2"), Set("9", "10", "11"))
    val cellPhones = Taxonomy("7", "Cell Phones", Option("2"), Set())
    val computers = Taxonomy("8", "Computers", Option("2"), Set())

    val tvs = Taxonomy("9", "TVs", Option("6"), Set())
    val dvds = Taxonomy("10", "DVD & Blu-ray Players",  Option("6"), Set())
    val homeTheater = Taxonomy("11", "Home Audio & Theater", Option("6"), Set())

    val ts = Seq(allProducts, electronicsOffice, moviesMusicBooks,
      homeFurniturePatio, homeImprovement, tvVideo, cellPhones,
      computers, tvs, dvds, homeTheater)

    TaxonomyTable.insertTaxonomies(ts)

    val tv1 = Product("1", "32\" LCD TV", "LG", None, "9", 299.99)
    val tv2 = Product("2", "50\" LED 4K TV", "Sharp", Option("Best TV EVER!"), "9", 1299.99)
    val tv3 = Product("3", "13\" CRT TV", "RCA", Option("Old school"), "9", 29.99)

    val movie1 = Product("4", "Star Wars: The Force Awakens", "Star Wars", None, "3", 14.99)
    val movie2 = Product("5", "Game of Thrones", "HBO", Some("Winter is coming"), "3", 29.99)
    val book1  = Product("6", "Harry Potter and the Sorcerer's Stone", "Harry Potter", Some("Yer a wizard 'arry"), "3", 9.99)

    val electronic = Product("7", "MacBook Pro", "Apple", Some("Why haven't I been updated in a year"), "9", 1999.99)

    val ps = Seq(tv1, tv2, tv3, movie1, movie2, book1, electronic)

    ProductTable.insertProducts(ps)
  }
}
