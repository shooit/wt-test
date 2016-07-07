package shooit.database

import scalikejdbc._
import shooit.datamodel.SerializableTaxonomy


object ChildrenTable {

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
               (implicit session: DBSession): Option[String] = {
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
                 (implicit session: DBSession): Set[String] = {
    DB localTx { implicit session: DBSession =>
      sql"""
        SELECT child FROM children WHERE parent = $id
      """.map(rs => rs.string("child")).list.apply().toSet
    }
  }

  /**
    * Gets the children for a parent id
    */
  def getChildren(id: String)
                 (implicit session: DBSession): Set[SerializableTaxonomy] = {
    val ids = getChildrenIds(id)

    sql"""
      SELECT * FROM taxonomies WHERE id IN $ids
    """.map(rs => SerializableTaxonomy(rs)).list.apply().toSet
  }


  /**
    * Adds a child to a parent
    */
  def insertChild(parent: String, child: String, ignoreDuplicates: Boolean = true)
                 (implicit session: DBSession): Int = {
    DB localTx { implicit session: DBSession =>
      if (ignoreDuplicates) {
        sql"""
          INSERT OR IGNORE INTO children ( parent, child ) VALUES ( $parent, $child )
        """.update.apply()
      } else {
        sql"""
          INSERT INTO children ( parent, child ) VALUES ( $parent, $child )
        """.update.apply()
      }
    }
  }

  /**
    * Inserts parent child pairs into the table
    */
  def insertChildren(pairs: Seq[(String, String)], ignoreDuplicates: Boolean = true)
                    (implicit session: DBSession): Seq[Int] = {
    DB autoCommit { implicit session =>
      if (ignoreDuplicates) {
        sql"""
          INSERT OR IGNORE INTO children ( parent, child ) VALUES ( ?, ? )
        """.batch(pairs.map(p => Seq[Any](p._1, p._2)): _*).apply()
      } else {
        sql"""
          INSERT INTO children ( parent, child ) VALUES ( ?, ? )
        """.batch(pairs.map(p => Seq[Any](p._1, p._2)): _*).apply()
      }
    }
  }

  /**
    * Removes a child from a parent
    */
  def removeChild(parent: String, child: String)
                 (implicit session: DBSession): Int = {
    DB localTx { implicit session: DBSession =>
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
    DB localTx { implicit session: DBSession =>
      sql"""
        DELETE FROM children WHERE parent = $parent
      """.update.apply()
    }
  }

}
