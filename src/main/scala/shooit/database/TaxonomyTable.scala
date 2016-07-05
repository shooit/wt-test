package shooit.database

import scalikejdbc._
import shooit.datamodel.SerializableTaxonomy

object TaxonomyTable extends SQLSyntaxSupport[SerializableTaxonomy] {

  override val tableName = "taxonomies"

  def createTable(implicit session: DBSession = AutoSession): Boolean = {
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
          val children = ChildrenTable.getChildren(id)

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
          val children = ChildrenTable.getChildren(id)
          SerializableTaxonomy(id, name, notes, parent, children)
      }

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

          println("")
          //remove it as a child from its old parent
          val removeResponse = t.parent match {
            case Some(p) => ChildrenTable.removeChild(p, t.id)
            case None    => false
          }

          //update its parent to the new one
          val updateResponse = sql"""
              UPDATE taxonomies SET parent = $parent WHERE id = $id
           """.update.apply()

          //add it as a child to the new parent
          val addResponse = ChildrenTable.addChild(parent, id)






        case None => false
      }

      //change in taxonomies
      val response1 = sql"""
            UPDATE taxonomies SET parent = $parent WHERE id = $id
         """.update.apply()

      //add child to parent in children
      val response2 = ChildrenTable.addChild(parent, id)

      //remove the child from its old parent
      val oldParent =

      val success =
        sql"""
          UPDATE taxonomy2taxonomy SET parent = $parent WHERE child = $id
        """.update.apply()

      if (success == 1) ChildrenTable.addChild(parent, id) && ChildrenTable.removeChild() else false
    }
  }


}