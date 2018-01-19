package it.polito.tdp.seriea.model;

public class TeamPunteggio implements Comparable<TeamPunteggio>{
	
	private Team team;
	private int punteggio;

	public TeamPunteggio(Team team, int punteggio) {
		super();
		this.team = team;
		this.punteggio = punteggio;
	}
	
	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public int getPunteggio() {
		return punteggio;
	}
	public void setPunteggio(int punteggio) {
		this.punteggio = punteggio;
	}

	@Override
	public int compareTo(TeamPunteggio other) {
		// classifica dalla squadra con punteggio maggiore a quella minore (DECRESCENTE)
			return -(this.getPunteggio()-other.getPunteggio());
	}

}
