package ogham.bbm

trait GetTeamPlayers { this : BBMServer =>
	import ogham.bbm.parser.PlayerEntityParser._
	
	def getTeamPlayers(team: String, league: Int) = {
		remote.service getTeamPlayers (league, new Some(team), new Some("en-US")) fold (
			fault => {},
			result => {
				<team name={ result.groupTeams.get.TeamEntity.first.get.Name.get }>
					{ parse(result.groupPlayers.get) }
				</team>
			}
		)
	}

}