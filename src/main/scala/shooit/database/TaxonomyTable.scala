package shooit.database

import scalikejdbc._
import shooit.datamodel.SerializableTaxonomy

object TaxonomyTable extends SQLSyntaxSupport[SerializableTaxonomy] {

  override val tableName = "taxonomies"

  //  def apply(rs: WrappedResultSet)

  def createTable(implicit session: DBSession = AutoSession): Boolean = {
    DB localTx { implicit session =>
      sql"""
          CREATE TABLE taxonomies (id VARCHAR, name VARCHAR, notes VARCHAR, PRIMARY KEY(id))
      """.execute.apply()
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
          val parent =
            sql"""
                SELECT parent FROM taxonomy2taxonomy WHERE child = $id
             """.map(rs => rs.string("parent")).single.apply()

          val children =
            sql"""
                SELECT child FROM taxonomy2taxonomy WHERE parent = $id
               """.map(rs => rs.string("child")).list.apply()

          SerializableTaxonomy(id, name, notes, parent, children.toSet)
      }
    }
  }

  def findByName(name: String)
                (implicit session: DBSession = AutoSession): List[SerializableTaxonomy] = {

    DB localTx { implicit session: DBSession =>
      val results =
        sql"""
            SELECT id, notes FROM taxonomies where name = $name
         """.map(rs => (rs.string("id"), rs.string("notes"))).list.apply()

      results.map {
        case (id, notes) =>
          val parent =
            sql"""
              SELECT parent FROM taxonomy2taxonomy WHERE child = $id
           """.map(rs => rs.string("parent")).single.apply()

          val children =
            sql"""
                SELECT child FROM taxonomy2taxonomy WHERE parent = $id
               """.map(rs => rs.string("child")).list.apply()

          SerializableTaxonomy(id, name, notes, parent, children.toSet)
      }

    }
  }

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
      val success =
        sql"""
          UPDATE taxonomy2taxonomy SET parent = $parent WHERE child = $id
        """.update.apply()

      if (success == 1) addChild(parent, id) && ??? else false
    }
  }

  def addChild(parent: String, child: String)
              (implicit session: DBSession = AutoSession): Boolean = {
    DB localTx { implicit session: DBSession =>
      sql"""
           UPDATE taxonomy2taxonomy SET child = $child WHERE parent = $parent
         """.update.apply() == 1
    }
  }

  def removeChild(parent: String, child: String)
                 (implicit session: DBSession = AutoSession): Boolean = {
    DB localTx { implicit session: DBSession =>
      sql"""
        DELETE FROM taxonomy2taxonomy WHERE parent = 
        """
    }
  }
}