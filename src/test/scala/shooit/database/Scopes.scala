package shooit.database

import java.util.concurrent.atomic.AtomicInteger

import org.specs2.specification.Scope
import scalikejdbc._
import shooit.datamodel.{Product, SerializableTaxonomy}


object Scopes {
  val counter = new AtomicInteger()
  def dbId = s"db${counter.addAndGet(1)}"

  trait SessionScope extends Scope {
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton(s"jdbc:h2:mem:$dbId", "user", "pass")
    implicit val session = AutoSession
  }

  trait TaxonomyTablesScope extends SessionScope {
    TaxonomyTable.createTable()
    ChildrenTable.createTable()
  }

  trait OneTaxonomyTablesScope extends TaxonomyTablesScope {
    val testId = "0"
    val testName = "Test Taxonomy"
    val testNotes = "Test notes"
    val testTaxonomy = SerializableTaxonomy(testId, testName, testNotes, None, Set())
    TaxonomyTable.insertTaxonomy(testTaxonomy, ignoreDuplicates = false)
  }

  trait FilledTaxonomyTablesScope extends TaxonomyTablesScope {
    val allProducts = SerializableTaxonomy("1", "All Departments", "top level", None, Set("2", "3", "4"))
    val electronicsOffice = SerializableTaxonomy("2", "Electronics And Office", "", Option("1"), Set("6", "7", "8"))
    val moviesMusicBooks = SerializableTaxonomy("3", "Movies, Music & Books", "", Option("1"), Set())
    val homeFurniturePatio = SerializableTaxonomy("4", "Home, Furniture & Patio", "", Option("1"), Set())
    val homeImprovement = SerializableTaxonomy("5", "Home Improvement", "For the plants", Option("1"), Set())

    val tvVideo = SerializableTaxonomy("6", "TV & Video", "", Option("2"), Set())
    val cellPhones = SerializableTaxonomy("7", "Cell Phones", "", Option("2"), Set())
    val computers = SerializableTaxonomy("8", "Computers", "", Option("2"), Set())

    val tvs = SerializableTaxonomy("9", "TVs", "", Option("6"), Set())
    val dvds = SerializableTaxonomy("10", "DVD & Blu-ray Players", "", Option("6"), Set())
    val homeTheater = SerializableTaxonomy("11", "Home Audio & Theater", "", Option("6"), Set())

    val ts = Seq(allProducts, electronicsOffice, moviesMusicBooks,
      homeFurniturePatio, homeImprovement, tvVideo, cellPhones,
      computers, tvs, dvds, homeTheater)

    TaxonomyTable.insertTaxonomies(ts, ignoreDuplicates = false)
  }

  trait ProductTablesScope extends TaxonomyTablesScope {
    ProductTable.createTable()
  }

  trait FilledProductTablesScope extends ProductTablesScope {
    val tv1 = Product("1", "32\" LCD TV", "LG", "", "9", 299.99)
    val tv2 = Product("2", "50\" LED 4K TV", "Sharp", "Best TV EVER!", "9", 299.99)
    val tv3 = Product("3", "13\" CRT TV", "RCA", "Old school", "9", 29.99)

    val ps = Seq(tv1, tv2, tv3)
    ProductTable.insertProducts(ps)
  }
}
