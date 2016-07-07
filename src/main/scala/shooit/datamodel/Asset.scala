package shooit.datamodel

import scalikejdbc.WrappedResultSet

trait Asset {
  def name: String
  def id: String
}

case class User(id: String, name: String, notes: Option[String] = None) extends Asset


