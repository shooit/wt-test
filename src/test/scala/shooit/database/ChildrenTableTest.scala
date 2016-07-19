package shooit.database

import org.specs2.mutable.SpecificationWithJUnit
import shooit.database.Scopes._
import shooit.database.taxonomies.ChildrenTable

class ChildrenTableTest extends SpecificationWithJUnit {

  sequential

  "ChildrenTableTest" should {
    "removeChild" in new FilledTaxonomyTablesScope {
      ChildrenTable.removeChild("0", "5")
      ChildrenTable.getChildrenIds("0").contains("5") mustEqual false
    }

    "getParent" in new FilledTaxonomyTablesScope {
      ChildrenTable.getParent("2") mustEqual Option("1")
    }

    "getChildrenIds" in new FilledTaxonomyTablesScope {
      ChildrenTable.getChildrenIds("2") mustEqual Set("6", "7", "8")
    }

    "removeParentAndChildren" in new FilledTaxonomyTablesScope {
      ChildrenTable.removeParentAndChildren("2")
      ChildrenTable.getChildrenIds("2") mustEqual Set()
    }

    "getChildren" in new FilledTaxonomyTablesScope {
      ChildrenTable.getChildren("2") mustEqual Set(tvVideo, cellPhones, computers)
    }

  }
}
