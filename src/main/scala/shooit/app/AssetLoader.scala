package shooit.app

import scalikejdbc.DBSession
import shooit.database.assets.{MachineNotesTable, MachineTable, UserNotesTable, UserTable}
import shooit.datamodel.assets.{Machine, User}


object AssetLoader {

  def load()(implicit session: DBSession): Unit = {
    UserTable.createTable()
    UserNotesTable.createTable()

    MachineTable.createTable()
    MachineNotesTable.createTable()

    val user1 = User("shewitt", "Sam Hewitt")
    val user2 = User("jstern", "Jeremy Stern")
    val user3 = User("mross", "Mike Ross")

    val users = Seq(user1, user2, user3)
    UserTable.insertUsers(users)

    val machine1 = Machine("mbpro1", "2014 Mac Book Pro", Some(user2), Seq("machine given to jeremy stern"))
    val machine2 = Machine("mbair1", "2014 Mac Book Air", Some(user3), Seq("machine given to mike ross", "machine given to emily seibert"))
    val machine3 = Machine("macbook1", "2015 MacBook")

    val machines = Seq(machine1, machine2, machine3)
    MachineTable.insertMachines(machines)
  }
}
