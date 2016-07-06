package shooit.database

import scalikejdbc._
import shooit.datamodel.User

object UserTable extends SQLSyntaxSupport[User] {

  override val tableName = "users"


  /**
    * Build a user from a ResultSet
    */
  def apply(rs: WrappedResultSet): User = {
    val notes = rs.string("notes") match {
      case "" => None
      case s: String  => Option(s)
    }
    new User(rs.string("id"), rs.string("name"), notes)
  }


  //Create function
  /**
    * Creates the users table
    */
  def createTable(implicit session: DBSession = AutoSession): Boolean = {
    sql"""
          CREATE TABLE users (id VARCHAR, name VARCHAR, notes VARCHAR, PRIMARY KEY(id))
       """.execute.apply
  }


  //Inserting functions
  /**
    * Insert a user
    */
  def insertUser(u: User, ignoreDuplicate: Boolean = true)
                (implicit session: DBSession = AutoSession): Int = {
    if (ignoreDuplicate) {
      sql"""
          INSERT OR IGNORE INTO users ( id, name, notes ) VALUES ( ?, ?, ? )
       """.bind(u.id, u.name, u.notes).update.apply()
    } else {
      sql"""
          INSERT INTO users ( id, name, notes ) VALUES ( ?, ?, ? )
       """.bind(u.id, u.name, u.notes).update.apply()
    }
  }

  /**
    * Insert multiple users as a batch update
    */
  def insertUsers(users: Seq[User], ignoreDuplicates: Boolean = true)
                 (implicit session: DBSession = AutoSession): IndexedSeq[Int] = {
    if (ignoreDuplicates) {
      sql"""
          INSERT OR IGNORE INTO users ( id, name, notes ) VALUES ( ?, ?, ? )
       """.batch(users.map(u => Seq[Any](u.id, u.name, u.notes.getOrElse(""))): _*).apply()
    } else {
      sql"""
          INSERT INTO users ( id, name, notes ) VALUES ( ?, ?, ? )
       """.batch(users.map(u => Seq[Any](u.id, u.name, u.notes.getOrElse(""))): _*).apply()
    }
  }


  //Selecting functions
  /**
    * Selects all the users from the table
    */
  def getAllUsers(implicit session: DBSession = AutoSession): List[User] = {
    DB localTx { implicit session: DBSession =>
      sql"""
          SELECT * FROM users
       """.map(rs => UserTable(rs)).list.apply()
    }
  }

  /**
    * Selects a single user by id
    */
  def findById(id: String)
              (implicit session: DBSession = AutoSession): Option[User] = {
    DB localTx { implicit session: DBSession =>
      sql"""
          SELECT * FROM users WHERE id = $id
       """.map(rs => UserTable(rs)).single.apply()
    }
  }


  /**
    * Selects user by name
    */
  def findByName(name: String)
                (implicit session: DBSession = AutoSession): List[User] = {
    DB localTx { implicit session: DBSession =>
      sql"""
          SELECT * FROM users WHERE name = $name
       """.map(rs => UserTable(rs)).list.apply()
    }
  }


  //Updating functions
  /**
    * Adds notes to a user by id
    */
  def addNotes(id: String, notes: String)
              (implicit session: DBSession = AutoSession): Int = {
    DB localTx { implicit session: DBSession =>
      sql"""
          UPDATE users SET notes = $notes WHERE id = $id
       """.update.apply()
    }
  }


  //Deleting functions
  /**
    * Deletes a user by id
    */
  def deleteUser(id: String)
                (implicit session: DBSession = AutoSession): Int = {
    DB localTx { implicit session: DBSession =>
      sql"""
          DELETE FROM users WHERE id = $id
       """.update.apply()
    }
  }
}