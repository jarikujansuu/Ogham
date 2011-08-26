package ogham.bbm.parser

import scala.xml.Elem
import bbmanager._

object PlayerEntityParser {
	def parse(players: bbmanager.ArrayOfPlayerEntity): Elem = {
		<players>{ for (player <- players.PlayerEntity) yield { parse(player.get) }}</players>
	}

	def parse(player: bbmanager.PlayerEntity): Elem = {
			def skills(input: Option[ArrayOfString]): Seq[Elem] = {
				for (x <- input.get.string) yield { <skill>{ x.get }</skill> }
			}

			def attributesElement(player: PlayerEntity): Elem = {
				val mv = player.BaseMovement + player.ModifMovement
				val st = player.BaseStrength + player.ModifStrength
				val ag = player.BaseAgility + player.ModifAgility
				val av = player.BaseArmour + player.ModifArmour

				val mvModif = player.ModifMovement.compare(0) match { case 0 => "original" case -1 => "decreased" case 1 => "increased" }
				val stModif = player.ModifStrength.compare(0) match { case 0 => "original" case -1 => "decreased" case 1 => "increased" }
				val agModif = player.ModifAgility.compare(0) match { case 0 => "original" case -1 => "decreased" case 1 => "increased" }
				val avModif = player.ModifArmour.compare(0) match { case 0 => "original" case -1 => "decreased" case 1 => "increased" }

				<attributes>
					<mv modified={ mvModif }>{ mv }</mv>
					<str modified={ stModif }>{ st }</str>
					<ag modified={ agModif }>{ ag }</ag>
					<av modified={ avModif }>{ av }</av>
				</attributes>
			}

			def statsElement(player: PlayerEntity): Elem = {
				val stats = player.Stats.get
				<stats>
					<xp>{ stats.XP }</xp>
					<matches>{ stats.iMatchPlayed.get }</matches>
					<mvp>{ stats.iMVP.get }</mvp>
					<inflicted>
						<touchdowns>{ stats.Inflicted_iTouchdowns.get }</touchdowns>
						<passes>{ stats.Inflicted_iPasses.get }</passes>
						<passing>{ stats.Inflicted_iMetersPassing.get }</passing>
						<running>{ stats.Inflicted_iMetersRunning.get }</running>
						<catches>{ stats.Inflicted_iCatches.get }</catches>
						<interceptions>{ stats.Inflicted_iInterceptions.get }</interceptions>
						<tackles>{ stats.Inflicted_iTackles.get }</tackles>
						<stuns>{ stats.Inflicted_iStuns.get }</stuns>
						<ko>{ stats.Inflicted_iKO.get }</ko>
						<casualties>{ stats.Inflicted_iCasualties.get }</casualties>
						<injuries>{ stats.Inflicted_iInjuries.get }</injuries>
						<dead>{ stats.Inflicted_iDead.get }</dead>
					</inflicted>
					<sustained>
						<casualties>{ stats.Sustained_iCasualties.get }</casualties>
						<interceptions>{ stats.Sustained_iInterceptions.get }</interceptions>
						<tackles>{ stats.Sustained_iTackles.get }</tackles>
						<stuns>{ stats.Sustained_iStuns.get }</stuns>
						<ko>{ stats.Sustained_iKO.get }</ko>
						<injuries>{ stats.Sustained_iInjuries.get }</injuries>
						<dead>{ stats.Sustained_iDead.get }</dead>
					</sustained>
				</stats>
			}

		<player>
			<num>{ player.Stats.get.ID }</num>
			<name>{ player.strName.get }</name>
			<level>{ 1 + player.iNbLevelsUp.get }</level>
			<position>{ player.Position.get }</position>
			<value>{ player.iSalary.get / 1000 }</value>
			{ player.CasualtyType.get match { case "" => {} case x => { <casualty>{ x }</casualty> } } }
			{ player.bDead.get match { case 1 => <dead>true</dead> case _ => {} } }
			{ attributesElement(player) }
			<baseSkills>{ skills(player.BaseSkills) }</baseSkills>
			<skills>{ skills(player.Skills) }</skills>
			{ statsElement(player) }
		</player>
	}
}