package shooit.service.assets

import org.json4s.native.Serialization
import scalikejdbc.DBSession
import shooit.database.assets.{MachineNotesTable, MachineTable}
import shooit.datamodel.assets.Machine
import shooit.service.BaseServlet


class MachineServlet(implicit val session: DBSession) extends BaseServlet {

  get("/") {
    params.get("name") match {
      case Some(name) => Serialization.writePretty(MachineTable.findByName(name))
      case None => Serialization.writePretty(MachineTable.getAllMachines)
    }
  }

  get("/:id") {
    val id = params("id")

    MachineTable.findById(id) match {
      case Some(m) => Serialization.writePretty(m)
      case None    => notFound404(id, "Machine")
    }
  }

  post("/") {
    val machines = Serialization.read[Seq[Machine]](request.body)
    MachineTable.insertMachines(machines)
  }

  put("/:id") {
    val id = params("id")

    params.get("userId") match {
      case Some(u) => MachineTable.updateUser(id, u)
      case None    =>
    }

    params.get("note") match {
      case Some(n) => MachineNotesTable.insertNote(id, n)
      case None    =>
    }
  }

  delete("/:id") {
    val id = params("id")
    MachineTable.deleteMachine(id) match {
      case 1 => s"Successfully deleted machine $id!"
      case 0 => s"Could not delete machine $id!"
      case _ => s"Unexpected behavior!"
    }
  }
}
