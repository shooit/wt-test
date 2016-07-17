package shooit.datamodel

import scalikejdbc.WrappedResultSet

trait Asset {
  def name: String
  def id: String
}

case class User(id: String, name: String, notes: Seq[String] = Seq()) extends Asset

object User {

  /**
    * Build a user from a ResultSet
    */
  def apply(rs: WrappedResultSet): User = {

    //explicitly handle empty strings
    val notes = rs.string("notes") match {
      case "" => None
      case s: String  => Option(s)
    }
    User(rs.string("id"), rs.string("name"))
  }
}

case class Machine(id: String, name: String, notes: Seq[String] = Seq(), user: User) extends Asset

object Machine {
  def assignUser(machine: Machine, user: User): Machine = {
    machine.copy(user = user)
  }

  def addNote(machine: Machine, note: String): Machine = {
    machine.copy(notes = note +: machine.notes)
  }

  def addNotes(machine: Machine, notes: Seq[String]): Machine = {
    machine.copy(notes = notes ++ machine.notes)
  }
}

