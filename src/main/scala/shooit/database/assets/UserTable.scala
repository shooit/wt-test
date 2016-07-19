package shooit.database.assets

import scalikejdbc._
import shooit.datamodel.assets.User

object UserTable {

  def createTable()
                 (implicit session: DBSession): Boolean = {
    DB autoCommit { implicit session =>
      sql"""
        CREATE TABLE IF NOT EXISTS users (
          id VARCHAR,
          name VARCHAR,
          PRIMARY KEY (id)
        )
      """.execute.apply()
    }
  }

  def getAllUsers(implicit session: DBSession): Seq[User] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT * FROM users
      """.map(rs => User(rs, UserNotesTable.findById(rs.string("id")))).list.apply()
    }
  }

  def findById(id: String)
              (implicit session: DBSession): Option[User] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT id, name FROM users WHERE id = $id
      """.map(rs => User(rs, UserNotesTable.findById(rs.string("id")))).single.apply()
    }
  }

  def findByName(name: String)
                (implicit session: DBSession): Seq[User] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT id, name FROM users WHERE name = $name
      """.map(rs => User(rs, UserNotesTable.findById(rs.string("id")))).list.apply()
    }
  }

  def insertUser(u: User)
                (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        INSERT INTO users ( id, name ) VALUES ( ?, ? )
      """.bind(u.id, u.name).update.apply()
    }
  }

  def insertUsers(us: Seq[User])
                 (implicit session: DBSession): Seq[Int] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        INSERT INTO users (id, name ) VALUES ( ?, ? )
      """.batch(us.map(u => Seq[Any](u.id, u.name)): _*).apply()
    }
  }

  def deleteUser(id: String)
                (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        DELETE FROM users WHERE id = $id
      """.update.apply()
    }
  }
}

object UserNotesTable {

  def createTable()
                 (implicit session: DBSession): Boolean = {
    DB autoCommit { implicit session =>
      sql"""
        CREATE TABLE IF NOT EXISTS usernotes (
          userid VARCHAR,
          note VARCHAR,
          timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          FOREIGN KEY (userid) REFERENCES users(id)
        )
      """.execute.apply()
    }
  }

  def insertNote(userId: String, note: String)
                (implicit session: DBSession): Int = {
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO usernotes VALUES ( $userId, $note, CURRENT_TIMESTAMP )
      """.update.apply()
    }
  }

  def insertNotes(userId: String, notes: Seq[String])
                 (implicit session: DBSession): Seq[Int] = {
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO usernotes VALUES ( $userId, ? , CURRENT_TIMESTAMP )
      """.batch(notes.map(n => Seq[Any](n)): _*).apply()
    }
  }

  def findById(userId: String)
              (implicit session: DBSession): Seq[String] = {
    DB autoCommit { implicit session =>
      sql"""
        SELECT note FROM usernotes WHERE userid = $userId ORDER BY timestamp DESC
      """.map(rs => rs.string("notes")).list.apply()
    }
  }
}
