package ogham.server.resource

import ogham.bbm._
import javax.ws.rs._
import javax.ws.rs.core._
import scala.xml.Elem

@Path("/players")
class TeamPlayers extends ResourceUtil {
	val service = new BBMServer with GetTeamPlayers

	@GET
	@Path("/{leagueId}/{teamName}")
	@Produces(Array("text/html"))
	def getPlayers(@PathParam("leagueId") leagueId: Int, @PathParam("teamName") teamName: String): String = {

		<html> { service.getTeamPlayers(teamName, leagueId) } </html>.toString()
	}
}