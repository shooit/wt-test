package shooit.datamodel

import scalikejdbc.WrappedResultSet

trait Asset {
  def name: String
  def id: String
}

case class User(id: String, name: String, notes: Option[String] = None) extends Asset

case class Taxonomy(id: String,
                    name: String,
                    notes: String,
                    parent: Option[Taxonomy],
                    children: Set[Taxonomy]) extends Asset {
  assert(children.forall(_.parent.get == this))

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
                                children: Set[String]) extends Asset {

  def isTopLevel = parent.isDefined

  def hasChildren = children.nonEmpty
}

object SerializableTaxonomy {
  def apply(t: Taxonomy): SerializableTaxonomy = {
    SerializableTaxonomy(t.id, t.name, t.notes, t.parent.map(_.id), t.children.map(_.id))
  }

  def apply(rs: WrappedResultSet): SerializableTaxonomy = {
    SerializableTaxonomy(rs.string("id"), rs.string("name"), rs.string("notes"), Option(rs.string("parent")), Set())
  }
}

case class SerializableTaxonomyRelationship(parent: String, child: String)

object SerializableTaxonomyRelationship {
  def apply(tr: TaxonomyRelationship): SerializableTaxonomyRelationship = {
    new SerializableTaxonomyRelationship(tr.parent.id, tr.child.id)
  }
}


case class TaxonomyRelationship(parent: Taxonomy, child: Taxonomy)
