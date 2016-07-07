package shooit.database

import scalikejdbc._
import shooit.datamodel.SerializableTaxonomy

object TaxonomyTable {

  def createTable()
                 (implicit session: DBSession): Boolean = {
    DB autoCommit { implicit session =>
      sql"""
        CREATE TABLE taxonomies (
          id VARCHAR,
          name VARCHAR,
          notes VARCHAR,
          parent VARCHAR,
          FOREIGN KEY (parent) REFERENCES taxonomies(id),
          PRIMARY KEY(id)
        )
      """.execute.apply()
    }
  }


  //Selecting functions
  /**
    * Selects all taxonomies
    */
  def getAllTaxonomies(implicit session: DBSession): List[SerializableTaxonomy] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT * FROM taxonomies
      """.map(rs => SerializableTaxonomy(rs)).list.apply()
    }
  }

  /**
    * Selects a taxonomy by id
    */
  def findById(id: String)
              (implicit session: DBSession): Option[SerializableTaxonomy] = {
    DB autoCommit { implicit session: DBSession =>
      val results =
        sql"""
            SELECT name, notes FROM taxonomies where id = $id
         """.map(rs => (rs.string("name"), rs.string("notes"))).single.apply()

      results.map {
        case (name, notes) =>
          val parent = ChildrenTable.getParent(id)
          val children = ChildrenTable.getChildrenIds(id)

          SerializableTaxonomy(id, name, notes, parent, children)
      }
    }
  }

  /**
    * Selects a list of taxonomies by name
    */
  def findByName(name: String)
                (implicit session: DBSession): List[SerializableTaxonomy] = {

    DB autoCommit { implicit session: DBSession =>
      val results =
        sql"""
            SELECT id, notes FROM taxonomies where name = $name
         """.map(rs => (rs.string("id"), rs.string("notes"))).list.apply()

      results.map {
        case (id, notes) =>
          val parent = ChildrenTable.getParent(id)
          val children = ChildrenTable.getChildrenIds(id)
          SerializableTaxonomy(id, name, notes, parent, children)
      }

    }
  }

  //Inserting functions
  def insertTaxonomy(t: SerializableTaxonomy, ignoreDuplicates: Boolean = true)
                    (implicit session: DBSession): Seq[Int] = {
    DB autoCommit {implicit session: DBSession =>
      val main: Int =
        if (ignoreDuplicates) {
          sql"""
            INSERT OR IGNORE INTO taxonomies ( id, name, notes, parent )
            VALUES ( ${t.id}, ${t.name}, ${t.notes}, ${t.parent.orNull} )
          """.update.apply()
        } else {
          sql"""
            INSERT INTO taxonomies ( id, name, notes, parent )
            VALUES ( ${t.id}, ${t.name}, ${t.notes}, ${t.parent.orNull} )
          """.update.apply()
        }

      val children: Seq[Int] =
        if (ignoreDuplicates) {
          sql"""
            INSERT INTO children ( parent, child ) VALUES ( ?, ? )
          """.batch(t.children.toSeq.map(c => Seq[Any](t.id, c)): _*).apply()
        } else {
          sql"""
            INSERT INTO children ( parent, child ) VALUES ( ?, ? )
          """.batch(t.children.toSeq.map(c => Seq[Any](t.id, c)): _*).apply()
        }

      main +: children
    }
  }

  def insertTaxonomies(ts: Seq[SerializableTaxonomy], ignoreDuplicates: Boolean = true)
                      (implicit session: DBSession): Seq[Int] = {
    DB autoCommit { implicit session: DBSession =>
      val main: Seq[Int] = sql"""
        INSERT INTO taxonomies ( id, name, notes, parent ) VALUES ( ?, ?, ?, ? )
      """.batch(ts.map(t => Seq[Any](t.id, t.name, t.notes, t.parent)): _*).apply()

      val children: Seq[Int] = ts.flatMap{
        t =>
          sql"""
            INSERT INTO children ( parent, child ) VALUES ( ?, ? )
          """.batch(t.children.toSeq.map(c => Seq[Any](t.id, c)): _*).apply()
      }

      main ++ children
    }
  }



  //Updating functions
  def updateNotes(id: String, notes: String)
                 (implicit session: DBSession): Int = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
          UPDATE taxonomies SET notes = $notes WHERE id = $id
         """.update.apply()
    }
  }

  /**
    * Updates the parent for a given taxonomy
    */
  def updateParent(id: String, parent: String)
                  (implicit session: DBSession): Seq[Int] = {
    DB autoCommit { implicit session: DBSession =>
      findById(id) match {
        case Some(t) =>
          //remove it as a child from its old parent
          val removeResponse = t.parent match {
            case Some(p) => ChildrenTable.removeChild(p, t.id)
            case None    => 0
          }

          //update its parent to the new one
          val updateResponse = sql"""
              UPDATE taxonomies SET parent = $parent WHERE id = $id
           """.update.apply()

          //add it as a child to the new parent
          val addResponse = ChildrenTable.insertChild(parent, id, ignoreDuplicates = false)

          Seq(removeResponse, updateResponse, addResponse)

        case None => Seq()
      }
    }
  }


  //Deleting functions
  /**
    * Deletes a taxonomy and its values in the children table
    */
  def deleteTaxonomy(id: String)
                    (implicit session: DBSession): Int = {
    DB autoCommit {implicit session: DBSession =>
      sql"""
        DELETE FROM taxonomies WHERE id = $id
      """.update.apply()

      ChildrenTable.removeParentAndChildren(id)
    }
  }
}