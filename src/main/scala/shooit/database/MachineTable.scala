package shooit.database

import scalikejdbc._
import shooit.datamodel.Machine


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
          INSERT INTO machines VALUES ( ${m.id}, ${m.name}, ${m.userId} )
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
        """.batch(ms.map(m => Seq[Any](m.id, m.name, m.userId)): _*).apply()

      val notesResponse: Seq[Int] = ms.flatMap(m => MachineNotesTable.insertNotes(m.id, m.notes))

      machineResponse ++ notesResponse
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
        INSERT INTO machinenotes VALUES ( $machineId, ? , CURRENT_TIMESTAMP )
      """.batch(notes.map(n => Seq[Any](n)): _*).apply()
    }
  }
}
