package shooit.datamodel

import scalikejdbc.WrappedResultSet

trait Asset {
  def name: String
  def id: String
  def notes: Seq[String]
}

trait AssetManager[T <: Asset] {

  def apply(resultSet: WrappedResultSet): T

  def addNote(t: T, note: String): T

  def addNotes(t: T, notes: Seq[String]): T
}

case class User(id: String, name: String, notes: Seq[String] = Seq()) extends Asset

object User extends AssetManager[User] {

  /**
    * Build a user from a ResultSet
    */
  def apply(rs: WrappedResultSet): User = {
    User(rs.string("id"), rs.string("name"))
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
                   userId: Option[String] = None,
                   notes: Seq[String] = Seq()) extends Asset

object Machine extends AssetManager[Machine] {

  def apply(rs: WrappedResultSet): Machine = {
    Machine(rs.string())
  }

  def assignUser(machine: Machine, user: String): Machine = {
    machine.copy(userId = Some(user))
  }

  def addNote(machine: Machine, note: String): Machine = {
    machine.copy(notes = note +: machine.notes)
  }

  def addNotes(machine: Machine, notes: Seq[String]): Machine = {
    machine.copy(notes = notes ++ machine.notes)
  }
}

