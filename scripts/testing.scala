import scalikejdbc._
import shooit.database.{ChildrenTable, ProductTable, TaxonomyTable, UserTable}
import shooit.datamodel.{Product, SerializableTaxonomy, Taxonomy, User}

val dbUrl = "jdbc:sqlite:/opt/devel/data/wt-test/wt-test.db"

ConnectionPool.singleton(dbUrl, "", "")
Class.forName("org.h2.Driver")
ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")
implicit val session = AutoSession

UserTable.createTable()
val users = Seq(
  User("shewitt", "Sam Hewitt", Some("applicant")),
  User("jstern", "Jeremy Stern", Some("current employee")),
  User("mross", "Mike Ross", Some("current employee")),
  User("jdoe", "John Doe")
)

UserTable.insertUsers(users)
UserTable.getAllUsers



TaxonomyTable.createTable()
ChildrenTable.createTable()

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

val tv1 = Product("1", "32\" LCD TV", "LG", "", "9", 299.99)
val tv2 = Product("2", "50\" LED 4K TV", "Sharp", "Best TV EVER!", "9", 299.99)
val tv3 = Product("3", "13\" CRT TV", "RCA", "Old school", "9", 29.99)

val ps = Seq(tv1, tv2, tv3)

TaxonomyTable.insertTaxonomies(ts, false)
ProductTable.insertProducts(ps)