package shooit.database

import scalikejdbc._
import shooit.datamodel.{Taxonomy, TaxonomyNode}

object TaxonomyTable {

  def createTable()
                 (implicit session: DBSession): Boolean = {
    DB autoCommit { implicit session =>
      sql"""
        CREATE TABLE IF NOT EXISTS taxonomies (
          id VARCHAR,
          name VARCHAR,
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
  def getAllTaxonomies(implicit session: DBSession): List[Taxonomy] = {
    DB autoCommit { implicit session: DBSession =>
      sql"""
        SELECT * FROM taxonomies
      """.map(rs => Taxonomy(rs)).list.apply()
    }
  }

  def treeById(root: String)
              (implicit session: DBSession): Option[TaxonomyNode] = {
    def getTaxonomyTree(root: String): TaxonomyNode = {
      val t = findById(root).get
      val children = ChildrenTable.getChildrenIds(t.id)
      if (children.isEmpty) {
        TaxonomyNode(t, Set())
      }
      else {
        TaxonomyNode(t, children.map(getTaxonomyTree))
      }
    }
    DB autoCommit { implicit session: DBSession =>
      findById(root).map(t => getTaxonomyTree(t.id))

    }
  }

  /**
    * Selects a taxonomy by id
    */
  def findById(id: String)
              (implicit session: DBSession): Option[Taxonomy] = {
    DB autoCommit { implicit session: DBSession =>
      val taxonomy =
        sql"""
          SELECT id, name, parent FROM taxonomies where id = $id
        """.map(rs => Taxonomy(rs)).single.apply()

      taxonomy.map(t => t.copy(children = ChildrenTable.getChildrenIds(t.id)))
    }
  }

  /**
    * Selects a list of taxonomies by name
    */
  def findByName(name: String)
                (implicit session: DBSession): List[Taxonomy] = {

    DB autoCommit { implicit session: DBSession =>
      val taxonomies =
        sql"""
          SELECT id, name, parent FROM taxonomies where name = $name
        """.map(rs => Taxonomy(rs)).list.apply()

      taxonomies.map(t => t.copy(children = ChildrenTable.getChildrenIds(t.id)))
    }
  }

  //Inserting functions
  def insertTaxonomy(t: Taxonomy)
                    (implicit session: DBSession): Seq[Int] = {
    DB autoCommit {implicit session: DBSession =>
      val main: Int =
        sql"""
          INSERT INTO taxonomies ( id, name, parent )
          VALUES ( ${t.id}, ${t.name}, ${t.parent.orNull} )
        """.update.apply()

      val children: Seq[Int] =
        sql"""
          INSERT INTO children ( parent, child ) VALUES ( ?, ? )
        """.batch(t.children.toSeq.map(c => Seq[Any](t.id, c)): _*).apply()

      main +: children
    }
  }

  def insertTaxonomies(ts: Seq[Taxonomy])
                      (implicit session: DBSession): Seq[Int] = {
    DB autoCommit { implicit session: DBSession =>
      val main: Seq[Int] =
      sql"""
        INSERT INTO taxonomies ( id, name, parent ) VALUES ( ?, ?, ? )
      """.batch(ts.map(t => Seq[Any](t.id, t.name, t.parent)): _*).apply()

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
          val updateResponse =
            sql"""
              UPDATE taxonomies SET parent = $parent WHERE id = $id
            """.update.apply()

          //add it as a child to the new parent
          val addResponse = ChildrenTable.insertChild(parent, id)

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