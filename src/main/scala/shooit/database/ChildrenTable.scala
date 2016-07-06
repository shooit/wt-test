package shooit.database

import com.typesafe.scalalogging.slf4j.LazyLogging
import scalikejdbc._
import shooit.datamodel.{SerializableTaxonomy, SerializableTaxonomyRelationship}

object ChildrenTable extends SQLSyntaxSupport[SerializableTaxonomyRelationship]
                        with LazyLogging {

  override val tableName = "children"

  def createTable()
                 (implicit session: DBSession): Boolean = {
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
  def getChildrenIds(id: String)
                 (implicit session: DBSession = AutoSession): Set[String] = {
    DB localTx { implicit session: DBSession =>
      sql"""
        SELECT child FROM children WHERE parent = $id
      """.map(rs => rs.string("child")).list.apply().toSet
    }
  }

  def getChildren(id: String)
                 (implicit session: DBSession = AutoSession): Set[SerializableTaxonomy] = {
    val ids = getChildrenIds(id)

    sql"""
      SELECT * FROM taxonomies WHERE id IN $ids
    """.map(rs => SerializableTaxonomy(rs)).list.apply().toSet
  }


  /**
    * Adds a child to a parent
    */
  def addChild(parent: String, child: String)
              (implicit session: DBSession = AutoSession): Boolean = {
    DB localTx { implicit session: DBSession =>
      sql"""
        INSERT INTO children ( parent, child ) VALUES ( $parent, $child )
      """.update.apply() match {
        case 0 =>
          logger.warn(s"No rows inserted for parent child pair ($parent, $child)")
          false
        case 1 =>
          logger.info(s"Successfully inserted a parent child pair ($parent, $child)")
          true
        case _ =>
          logger.error(s"Multiple rows insert for parent child pair ($parent, $child)")
          false
      }
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
      """.update.apply() match {
        case 0 =>
          logger.warn(s"No rows removed for parent child pair ($parent, $child)")
          false
        case 1 =>
          logger.info(s"Successfully removed a parent child pair ($parent, $child)")
          true
        case _ =>
          logger.error(s"Multiple rows removed for parent child pair ($parent, $child)")
          false
      }
    }
  }

  def removeParentAndChildren(parent: String)
                             (implicit session: DBSession = AutoSession): Int = {
    DB localTx { implicit session: DBSession =>
      sql"""
        DELETE FROM children WHERE parent = $parent
      """.update.apply()
    }
  }

}
