package shooit.datamodel.taxonomies

import org.json4s.JsonAST.JField
import org.json4s.JsonDSL._
import org.json4s.{CustomSerializer, Formats, JObject, JValue}
import scalikejdbc.WrappedResultSet


case class Taxonomy(id: String,
                    name: String,
                    parent: Option[String],
                    children: Set[String]) {
  def isTopLevel = parent.isDefined
  def hasChildren = children.nonEmpty
}

object Taxonomy {
  def apply(rs: WrappedResultSet): Taxonomy = {
    Taxonomy(rs.string("id"), rs.string("name"), Option(rs.string("parent")), Set())
  }
}

case class TaxonomyRelationship(parent: String, child: String)

case class TaxonomyNode(value: Taxonomy, children: Set[TaxonomyNode])

object TaxonomyNode {
  val serializer: Formats => (PartialFunction[JValue, TaxonomyNode], PartialFunction[Any, JValue]) = {
    (format: Formats) =>
      (
        {
          case x: JObject => null
        },
        {
          case t: TaxonomyNode => taxonomy2json(t)
        }
        )
  }

  private def taxonomy2json(t: TaxonomyNode): JValue = {
    if (t.children.isEmpty) {
      JObject(
        JField("id", t.value.id),
        JField("name", t.value.name)
      )
    } else {
      JObject(
        JField("id", t.value.id),
        JField("name", t.value.name),
        JField("children", t.children.map(taxonomy2json))
      )
    }
  }

  object TaxonomyNodeSerializer extends CustomSerializer[TaxonomyNode](serializer)
}