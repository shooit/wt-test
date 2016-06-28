package shooit.datamodel

import java.net.URI

import scalikejdbc._


trait Asset {
  def name: String
  def uri: URI
}

case class User(name: String, notes: String, uri: URI) extends Asset

object User extends SQLSyntaxSupport[User] {
  override val tableName = "users"

  def apply(rs: WrappedResultSet) = new User(rs.string("name"), rs.string("notes"), new URI(rs.string("uri")))
}

