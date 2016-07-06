import scalikejdbc._
import shooit.database.{ChildrenTable, TaxonomyTable}
import shooit.datamodel.{SerializableTaxonomy, Taxonomy, User}

val dbUrl = "jdbc:sqlite:/opt/devel/data/wt-test/wt-test.db"

ConnectionPool.singleton(dbUrl, "", "")


//val users = Seq(
//  User("shewitt", "Sam Hewitt", Some("applicant")),
//  User("jstern", "Jeremy Stern", Some("current employee")),
//  User("mross", "Mike Ross", Some("current employee")),
//  User("jdoe", "John Doe")
//)
//
//User.insertUsers(users)(AutoSession)
//
//User.createTable(AutoSession)

implicit val session = AutoSession

//TaxonomyTable.createTable()
//ChildrenTable.createTable()

val allProducts = SerializableTaxonomy("1", "All Products", "top level", None, Set("2", "3", "4"))
val houseAndHome = SerializableTaxonomy("2", "House and Home", "", Option("1"), Set())
val electronics = SerializableTaxonomy("3", "Electronics", "", Option("1"), Set())
val garden = SerializableTaxonomy("4", "Garden", "For the plants", Option("1"), Set())


TaxonomyTable.insertTaxonomy(allProducts)
TaxonomyTable.insertTaxonomy(houseAndHome)
TaxonomyTable.insertTaxonomy(electronics)
TaxonomyTable.insertTaxonomy(garden)
ChildrenTable.getChildren("1")