package shooit.database

import com.typesafe.scalalogging.slf4j.LazyLogging
import scalikejdbc._
import shooit.datamodel.SerializableTaxonomy

object TaxonomyTable extends SQLSyntaxSupport[SerializableTaxonomy]
                        with LazyLogging {

  override val tableName = "taxonomies"

  def createTable()
                 (implicit session: DBSession = AutoSession): Boolean = {
    DB localTx { implicit session =>
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
    * Finds the taxonomy by id
    */
  def getAllTaxonomies(implicit session: DBSession = AutoSession): List[SerializableTaxonomy] = {
    DB localTx { implicit session: DBSession =>
      sql"""
        SELECT * FROM taxonomies
      """.map(rs => SerializableTaxonomy(rs)).list.apply()
    }
  }


  def findById(id: String)
              (implicit session: DBSession = AutoSession): Option[SerializableTaxonomy] = {
    DB localTx { implicit session: DBSession =>
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
    * Selects a list of taxonomies by id
    */
  def findByName(name: String)
                (implicit session: DBSession = AutoSession): List[SerializableTaxonomy] = {

    DB localTx { implicit session: DBSession =>
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
  def insertTaxonomy(t: SerializableTaxonomy)
                    (implicit session: DBSession = AutoSession): Seq[Int] = {
    DB localTx {implicit session: DBSession =>
      sql"""
        INSERT INTO taxonomies ( id, name, notes, parent )
        VALUES ( ${t.id}, ${t.name}, ${t.notes}, ${t.parent.orNull} )
      """.update.apply()

      sql"""
        INSERT INTO children ( parent, child ) VALUES ( ?, ? )
      """.batch(t.children.toSeq.map(c => Seq[Any](t.id, c)): _*).apply()
    }
  }



  //Updating functions
  def updateNotes(id: String, notes: String)
                 (implicit session: DBSession = AutoSession): Int = {
    DB localTx { implicit session: DBSession =>
      sql"""
          UPDATE taxonomies SET notes = $notes WHERE id = $id
         """.update.apply()
    }
  }

  def updateParent(id: String, parent: String)
                  (implicit session: DBSession = AutoSession): Boolean = {
    DB localTx { implicit session: DBSession =>
      findById(id) match {
        case Some(t) =>
          //remove it as a child from its old parent
          val removeResponse = t.parent match {
            case Some(p) => ChildrenTable.removeChild(p, t.id)
            case None    => true
          }

          //update its parent to the new one
          val updateResponse = sql"""
              UPDATE taxonomies SET parent = $parent WHERE id = $id
           """.update.apply() match {
            case 0 =>
              logger.warn(s"No rows updated when setting new parent to taxonomy $id")
              false
            case 1 =>
              logger.info(s"Successfully updated parent for taxonomy $id")
              true
            case _ =>
              logger.error(s"Multiple rows updated when setting new parent to taxonomy $id")
              false
          }

          //add it as a child to the new parent
          val addResponse = ChildrenTable.addChild(parent, id)

          removeResponse && updateResponse && addResponse

        case None => false
      }
    }
  }


  //Deleting functions
  def deleteTaxonomy(id: String)
                    (implicit session: DBSession = AutoSession): Int = {
    DB localTx {implicit session: DBSession =>
      sql"""
        DELETE FROM taxonomies WHERE id = $id
      """.update.apply()

      ChildrenTable.removeParentAndChildren(id)
    }
  }

}