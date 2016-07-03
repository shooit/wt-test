import scalikejdbc._
import shooit.datamodel.User

val dbUrl = "jdbc:sqlite:/opt/devel/data/wt-test/wt-test.db"

ConnectionPool.singleton(dbUrl, "", "")


val users = Seq(
  User("shewitt", "Sam Hewitt", Some("applicant")),
  User("jstern", "Jeremy Stern", Some("current employee")),
  User("mross", "Mike Ross", Some("current employee")),
  User("jdoe", "John Doe")
)

User.insertUsers(users)(AutoSession)

User.createTable(AutoSession)