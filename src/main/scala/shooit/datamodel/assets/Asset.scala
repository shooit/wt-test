package shooit.datamodel.assets

import scalikejdbc.WrappedResultSet

trait Asset {
  def name: String
  def id: String
  def notes: Seq[String]
}

case class User(id: String, name: String, notes: Seq[String] = Seq()) extends Asset

object User {

  /**
    * Build a user from a ResultSet
    */
  def apply(rs: WrappedResultSet): User = {
    User(rs.string("id"), rs.string("name"))
  }

  def apply(rs: WrappedResultSet, notes: Seq[String]): User = {
    User(rs.string("id"), rs.string("name"), notes = notes)
  }


  def addNote(user: User, note: String): User = {
    user.copy(notes = note +: user.notes)
  }

  def addNotes(user: User, note: Seq[String]): User = {
    user.copy(notes = note ++ user.notes)
  }
}

case class Machine(id: String,
                   name: String,
                   user: Option[User] = None,
                   notes: Seq[String] = Seq()) extends Asset

object Machine {

  def apply(rs: WrappedResultSet): Machine = {
    Machine(rs.string("id"), rs.string("name"))
  }

  def apply(rs: WrappedResultSet, notes: Seq[String]): Machine = {
    Machine(rs.string("id"), rs.string("name"), notes = notes)
  }

  def assignUser(machine: Machine, user: User): Machine = {
    machine.copy(user = Some(user))
  }

  def addNote(machine: Machine, note: String): Machine = {
    machine.copy(notes = note +: machine.notes)
  }

  def addNotes(machine: Machine, notes: Seq[String]): Machine = {
    machine.copy(notes = notes ++ machine.notes)
  }
}

