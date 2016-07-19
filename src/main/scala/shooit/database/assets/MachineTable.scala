package shooit.database.assets

import scalikejdbc._
import shooit.datamodel.assets.Machine


object MachineTable {

  def createTable()
            (implicit session: DBSession): Boolean = {
    DB autoCommit { implicit session =>
      sql"""
        CREATE TABLE IF NOT EXISTS machines (
          id VARCHAR,
          name VARCHAR,
          user VARCHAR,
          FOREIGN KEY (user) REFERENCES users(id),
          PRIMARY KEY (id)
        )
      """.execute.apply()
    }
  }

  def insertMachine(m: Machine)
                   (implicit session: DBSession): Seq[Int] = {
    DB autoCommit { implicit session =>
      val machineResponse: Int = sql"""
          INSERT INTO machines VALUES ( ${m.id}, ${m.name}, ${m.user.map(_.id)} )
        """.update.apply()

      val notesResponse: Seq[Int] = MachineNotesTable.insertNotes(m.id, m.notes)

      machineResponse +: notesResponse
    }
  }

  def insertMachines(ms: Seq[Machine])
                    (implicit session: DBSession): Seq[Int] = {
    DB autoCommit { implicit session =>
      val machineResponse: Seq[Int] =
        sql"""
          INSERT INTO machines VALUES ( ?, ?, ? )
        """.batch(ms.map(m => Seq[Any](m.id, m.name, m.user.map(_.id))): _*).apply()

      val notesResponse: Seq[Int] = ms.flatMap(m => MachineNotesTable.insertNotes(m.id, m.notes))

      machineResponse ++ notesResponse
    }
  }

  def updateUser(machineId: String, userId: String)
                (implicit session: DBSession): Int = {
    DB autoCommit { implicit session =>
      sql"""
        UPDATE machines SET user = $userId WHERE id = $machineId
      """.update.apply()
    }
  }

  def getAllMachines(implicit session: DBSession): Seq[Machine] = {
    DB autoCommit { implicit session =>
      sql"""
        SELECT * FROM users
      """.map(rs => Machine(rs, MachineNotesTable.findById(rs.string("id")))).list.apply()
    }
  }

  def findById(machineId: String)
              (implicit session: DBSession): Option[Machine] = {
    DB autoCommit { implicit session =>
      sql"""
        SELECT * FROM machines WHERE id = $machineId
      """.map(rs => Machine(rs, MachineNotesTable.findById(rs.string("id")))).single.apply()
    }
  }

  def findByName(name: String)
                (implicit session: DBSession): Seq[Machine] = {
    DB autoCommit { implicit session =>
     sql"""
        SELECT * FROM machines WHERE name = $name
      """.map(rs => Machine(rs, MachineNotesTable.findById(rs.string("id")))).list.apply()
    }
  }

  def deleteMachine(id: String)
                   (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        DELETE FROM machines WHERE id = $id
      """.update.apply()
    }
  }

}


object MachineNotesTable {

  def createTable()
                 (implicit session: DBSession): Boolean = {
    DB autoCommit { implicit session =>
      sql"""
        CREATE TABLE IF NOT EXISTS machinenotes (
          machineid VARCHAR,
          note VARCHAR,
          timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          FOREIGN KEY (machineid) REFERENCES machines(id)
        )
      """.execute.apply()
    }
  }

  def insertNote(machineId: String, note: String)
                (implicit session: DBSession): Int = {
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO machinenotes VALUES ( $machineId, $note, CURRENT_TIMESTAMP )
      """.update.apply()
    }
  }

  def insertNotes(machineId: String, notes: Seq[String])
                 (implicit session: DBSession): Seq[Int] = {
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO machinenotes VALUES ( ?, ? , CURRENT_TIMESTAMP )
      """.batch(notes.map(n => Seq[Any](machineId, n)): _*).apply()
    }
  }

  def findById(machineId: String)
              (implicit session: DBSession): Seq[String] = {
    DB autoCommit { implicit session =>
      sql"""
        SELECT note FROM machinenotes WHERE machineid = $machineId ORDER BY timestamp DESC
      """.map(rs => rs.string("notes")).list.apply()
    }
  }
}
