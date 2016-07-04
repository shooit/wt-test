package shooit.datamodel

import scalikejdbc._


case class Taxonomy(id: String,
                    name: String,
                    notes: String,
                    parent: Option[Taxonomy],
                    children: Set[Taxonomy]) extends Asset {

  def isTopLevel = parent.isDefined

  def hasChildren = children.nonEmpty

  def relationships: Set[TaxonomyRelationship] = {
    children.foldLeft(Set[TaxonomyRelationship]()) {
      case (rs: Set[TaxonomyRelationship], t: Taxonomy) =>
        rs + TaxonomyRelationship(this, t) ++ t.relationships
    }
  }
}


case class SerializableTaxonomy(id: String,
                                name: String,
                                notes: String,
                                parent: Option[String],
                                children: Set[String]) extends Asset

object SerializableTaxonomy {
  def apply(t: Taxonomy): SerializableTaxonomy = {
    new SerializableTaxonomy(t.id, t.name, t.notes, t.parent.map(_.id), t.children.map(_.id))
  }
}

case class SerializableTaxonomyRelationship(parent: String, child: String)

object SerializableTaxonomyRelationship {
  def apply(tr: TaxonomyRelationship): SerializableTaxonomyRelationship = {
    new SerializableTaxonomyRelationship(tr.parent.id, tr.child.id)
  }
}




case class TaxonomyRelationship(parent: Taxonomy, child: Taxonomy)

object TaxonomyRelationship extends SQLSyntaxSupport[TaxonomyRelationship] {

  override val tableName = "taxonomy2taxonomy"

  def createTable(implicit session: DBSession = AutoSession): Boolean = {
    DB localTx { implicit session: DBSession =>
      sql"""
            CREATE TABLE taxonomy2taxonomy (parent VARCHAR, child VARCHAR, PRIMARY KEY (parent, child))
         """.execute.apply()
    }
  }
}