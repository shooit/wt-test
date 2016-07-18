package shooit.database

import scalikejdbc._
import shooit.datamodel.User

object UserTable {

  def createTable()
                 (implicit session: DBSession): Boolean = {
    DB autoCommit { implicit session =>
      sql"""
        CREATE TABLE IF NOT EXISTS users (
          id VARCHAR,
          name VARCHAR,
          uri VARCHAR
          PRIMARY KEY (id)
        )
      """.execute.apply()
    }
  }

  def getAllUsers(implicit session: DBSession): Seq[User] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT * FROM users
      """.map(rs => User(rs)).list.apply()
    }
  }

  def findById(id: String)
              (implicit session: DBSession): Option[User] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT id, name, parent FROM users WHERE id = $id
      """.map(rs => User(rs)).single.apply()
    }
  }

  def findByName(name: String)
                (implicit session: DBSession): Seq[User] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT id, name, parent FROM users WHERE name = $name
      """.map(rs => User(rs)).list.apply()
    }
  }

  def insertUser(u: User)
                (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        INSERT INTO users ( id, name, notes ) VALUES ( ?, ?, ? )
      """.bind(u.id, u.name).update.apply()
    }
  }
}

object UserNotesTable
