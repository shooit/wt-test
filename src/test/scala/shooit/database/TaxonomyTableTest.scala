package shooit.database

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import shooit.database.Scopes._

@RunWith(classOf[JUnitRunner])
class TaxonomyTableTest extends Specification {

  sequential

  "TaxonomyTableTest" should {

    "deleteTaxonomy" in new FilledTaxonomyTablesScope {
      TaxonomyTable.deleteTaxonomy("11")
      TaxonomyTable.findById("11") mustEqual None
    }

    "updateNotes" in new OneTaxonomyTablesScope {
      val newNotes = "New Notes"
      TaxonomyTable.updateNotes("0", newNotes)
      TaxonomyTable.findById("0").get.notes mustEqual newNotes
    }

    "findByName" in new OneTaxonomyTablesScope {
      TaxonomyTable.findByName(testName).size mustEqual 1
    }

    "findById" in new OneTaxonomyTablesScope {
      TaxonomyTable.findById(testId) mustEqual Option(testTaxonomy)
    }

    "updateParent" in new FilledTaxonomyTablesScope {
      TaxonomyTable.updateParent(homeTheater.id, electronicsOffice.id)
      TaxonomyTable.findById(homeTheater.id).get.parent mustEqual Option(electronicsOffice.id)
      ChildrenTable.getParent(homeTheater.id) mustEqual Option(electronicsOffice.id)
      ChildrenTable.getChildrenIds(electronicsOffice.id).contains(homeTheater.id) mustEqual true
    }

    "insertTaxonomies" in new FilledTaxonomyTablesScope {
      TaxonomyTable.getAllTaxonomies.size mustEqual ts.size
    }

    "insertTaxonomy" in new OneTaxonomyTablesScope {
      TaxonomyTable.getAllTaxonomies.size mustEqual 1
    }
  }
}
