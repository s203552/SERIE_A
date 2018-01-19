package it.polito.tdp.seriea.model;

public class Coppie {
Team home;
Team away;
int goalfatti;
int goalsubiti;
public Coppie(Team home, Team away, int goalfatti, int goalsubiti) {
	super();
	this.home = home;
	this.away = away;
	this.goalfatti = goalfatti;
	this.goalsubiti = goalsubiti;
}
public Team getHome() {
	return home;
}
public Team getAway() {
	return away;
}
public int getGoalfatti() {
	return goalfatti;
}
public int getGoalsubiti() {
	return goalsubiti;
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((away == null) ? 0 : away.hashCode());
	result = prime * result + goalfatti;
	result = prime * result + goalsubiti;
	result = prime * result + ((home == null) ? 0 : home.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Coppie other = (Coppie) obj;
	if (away == null) {
		if (other.away != null)
			return false;
	} else if (!away.equals(other.away))
		return false;
	if (goalfatti != other.goalfatti)
		return false;
	if (goalsubiti != other.goalsubiti)
		return false;
	if (home == null) {
		if (other.home != null)
			return false;
	} else if (!home.equals(other.home))
		return false;
	return true;
}


}

