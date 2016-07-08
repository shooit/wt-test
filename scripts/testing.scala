import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import scalikejdbc._
import shooit.database.{ChildrenTable, ProductTable, TaxonomyTable, UserTable}
import shooit.datamodel.{Product, Taxonomy, TaxonomyNode, User}

val dbUrl = "jdbc:sqlite:/opt/devel/data/wt-test/wt-test.db"
ConnectionPool.singleton(dbUrl, "", "")
//Class.forName("org.h2.Driver")
//ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")
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

val tree = TaxonomyTable.treeById("1")
implicit val formats = DefaultFormats + TaxonomyNode.TaxonomyNodeSerializer
Serialization.write(tree)


TaxonomyTable.createTable()
ChildrenTable.createTable()
ProductTable.createTable()

val allProducts = Taxonomy("1", "All Departments", None, Set("2", "3", "4"))
val electronicsOffice = Taxonomy("2", "Electronics And Office", Option("1"), Set("6", "7", "8"))
val moviesMusicBooks = Taxonomy("3", "Movies, Music & Books", Option("1"), Set())
val homeFurniturePatio = Taxonomy("4", "Home, Furniture & Patio", Option("1"), Set())
val homeImprovement = Taxonomy("5", "Home Improvement", Option("1"), Set())

val tvVideo = Taxonomy("6", "TV & Video", Option("2"), Set())
val cellPhones = Taxonomy("7", "Cell Phones", Option("2"), Set())
val computers = Taxonomy("8", "Computers", Option("2"), Set())

val tvs = Taxonomy("9", "TVs", Option("6"), Set())
val dvds = Taxonomy("10", "DVD & Blu-ray Players",  Option("6"), Set())
val homeTheater = Taxonomy("11", "Home Audio & Theater", Option("6"), Set())

val ts = Seq(allProducts, electronicsOffice, moviesMusicBooks,
  homeFurniturePatio, homeImprovement, tvVideo, cellPhones,
  computers, tvs, dvds, homeTheater)

val tv1 = Product("1", "32\" LCD TV", "LG", None, "9", 299.99)
val tv2 = Product("2", "50\" LED 4K TV", "Sharp", Option("Best TV EVER!"), "9", 299.99)
val tv3 = Product("3", "13\" CRT TV", "RCA", Option("Old school"), "9", 29.99)

val ps = Seq(tv1, tv2, tv3)
