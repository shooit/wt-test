package shooit.database

import scalikejdbc._
import shooit.datamodel.Taxonomy


object ChildrenTable {

  def createTable()
                 (implicit session: DBSession): Boolean = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        CREATE TABLE IF NOT EXISTS children (
          parent VARCHAR,
          child VARCHAR,
          FOREIGN KEY (parent) REFERENCES taxonomies(id),
          FOREIGN KEY (child) REFERENCES taxonomies(id),
          UNIQUE(child),
          PRIMARY KEY (parent, child)
        )
      """.execute.apply()
    }
  }

  /**
    * Gets the parent for a given taxonomy
    */
  def getParent(id: String)
               (implicit session: DBSession): Option[String] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT parent FROM children WHERE child = $id
      """.map(rs => rs.string("parent")).single.apply()
    }
  }

  /**
    * Gets the children of a taxonomy
    */
  def getChildrenIds(id: String)
                    (implicit session: DBSession): Set[String] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT child FROM children WHERE parent = $id
      """.map(rs => rs.string("child")).list.apply().toSet
    }
  }

  /**
    * Get the children and their children ... of a taxonomy
    */
  def getAllChildrenIds(id: String)
                       (implicit session: DBSession): Set[String] = {
    val children = getChildrenIds(id)
    children + id ++ children.flatMap(getAllChildrenIds)
  }

  /**
    * Gets the children for a parent id
    */
  def getChildren(id: String)
                 (implicit session: DBSession): Set[Taxonomy] = {
    val ids = getChildrenIds(id)

    sql"""
      SELECT * FROM taxonomies WHERE id IN ($ids)
    """.map(rs => Taxonomy(rs)).list.apply().toSet
  }


  /**
    * Adds a child to a parent
    */
  def insertChild(parent: String, child: String)
                 (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        INSERT INTO children ( parent, child ) VALUES ( $parent, $child )
      """.update.apply()
    }
  }

  /**
    * Inserts parent child pairs into the table
    */
  def insertChildren(pairs: Seq[(String, String)])
                    (implicit session: DBSession): Seq[Int] = {
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO children ( parent, child ) VALUES ( ?, ? )
      """.batch(pairs.map(p => Seq[Any](p._1, p._2)): _*).apply()
    }
  }

  /**
    * Removes a child from a parent
    */
  def removeChild(parent: String, child: String)
                 (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        DELETE FROM children WHERE parent = $parent AND child = $child
      """.update.apply()
    }
  }

  /**
    * Removes the parent and all its children from the table
    */
  def removeParentAndChildren(parent: String)
                             (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        DELETE FROM children WHERE parent = $parent
      """.update.apply()
    }
  }

}
