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
    User(rs.string("id"), rs.string("name"), notes)
  }
}