package ogham.bbm

object Test {
	def main(args: Array[String]) {
		val service = new BBMServer with GetTeamPlayers

		println { service.getTeamPlayers("112th Brute Brigade", 2450) }
	}
}