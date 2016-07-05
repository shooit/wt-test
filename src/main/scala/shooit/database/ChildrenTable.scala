package shooit.database

import scalikejdbc._
import shooit.datamodel.SerializableTaxonomyRelationship

object ChildrenTable extends SQLSyntaxSupport[SerializableTaxonomyRelationship] {

  override val tableName = "children"

  def createTable(implicit session: DBSession): Boolean = {
    DB localTx { implicit session: DBSession =>
      sql"""
        CREATE TABLE children (
          parent VARCHAR,
          child VARCHAR,
          FOREIGN KEY (parent) REFERENCES taxonomies(id),
          FOREIGN KEY (child) REFERENCES taxonomies(id),
          PRIMARY KEY (parent, child)
        )
      """.execute.apply()
    }
  }

  /**
    * Gets the parent for a given taxonomy
    */
  def getParent(id: String)
               (implicit session: DBSession = AutoSession): Option[String] = {
    DB localTx { implicit session: DBSession =>
      sql"""
        SELECT parent FROM children WHERE child = $id
      """.map(rs => rs.string("parent")).single.apply()
    }
  }

  /**
    * Gets the children of a taxonomy
    */
  def getChildren(id: String)
                 (implicit session: DBSession = AutoSession): Set[String] = {
    DB localTx { implicit session: DBSession =>
      sql"""
        SELECT child FROM children WHERE parent = $id
      """.map(rs => rs.string("child")).list.apply().toSet
    }
  }


  /**
    * Adds a child to a parent
    */
  def addChild(parent: String, child: String)
              (implicit session: DBSession = AutoSession): Boolean = {
    DB localTx { implicit session: DBSession =>
      sql"""
        UPDATE children SET child = $child WHERE parent = $parent
      """.update.apply() == 1
    }
  }

  /**
    * Removes a child from a parent
    */
  def removeChild(parent: String, child: String)
                 (implicit session: DBSession = AutoSession): Boolean = {
    DB localTx { implicit session: DBSession =>
      sql"""
        DELETE FROM children WHERE parent = $parent AND child = $child
      """.update.apply() == 1
    }
  }

}
