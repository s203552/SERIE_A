package it.polito.tdp.seriea.model;

import java.time.LocalDate;

public class Match {
	
	private int id ;
	private Season season ;
	private String div ;
	private LocalDate date ;
	private Team homeTeam ;
	private Team awayTeam ;
	private int fthg ; // full time home goals
	private int ftag ; // full time away goals
	private String ftr ; // full time result (H, A, D)
	
	/*
	 *  � possibile aggiungere altri campi, se risulteranno necessari
	 *  
		HTHG = Half Time(*) Home Team Goals					(*) tranne quanto la partita è assegnata a tavolino
		HTAG = Half Time(*) Away Team Goals
		HTR = Half Time(*) Result (H=Home Win, D=Draw, A=Away Win)
		HS = Home Team Shots
		 AS = Away Team Shots
		 HST = Home Team Shots on Target
		AST = Away Team Shots on Target
		HHW = Home Team Hit Woodwork
		AHW = Away Team Hit Woodwork
		HC = Home Team Corners
		AC = Away Team Corners
		HF = Home Team Fouls Committed
		AF = Away Team Fouls Committed
		HO = Home Team Offsides
		AO = Away Team Offsides
		AR = Away Team Red Cards
		HY = Home Team Yellow Cards
		AY = Away Team Yellow Cards
		HR = Home Team Red Cards
	 */
	private int HTHG;	//Half Time(*) Home Team Goals					(*) tranne quanto la partita è assegnata a tavolino
	private int HTAG;	// Half Time(*) Away Team Goals
	private String HTR;	// Half Time(*) Result (H=Home Win, D=Draw, A=Away Win)
	private int HS ;	// Home Team Shots
	private int AS ; 	//Away Team Shots
	private int HST ; 	//Home Team Shots on Target
	private int AST ; 	//Away Team Shots on Target
	private int HHW ; 	//Home Team Hit Woodwork
	private int AHW ; 	//Away Team Hit Woodwork
	private int HC ; 	// Home Team Corners
	private int AC ; 	//Away Team Corners
	private int HF ;	// Home Team Fouls Committed
	private int AF; 	// Away Team Fouls Committed
	private int HO; 	// Home Team Offsides
	private int AO; 	//Away Team Offsides
	private int HY ; 	//Home Team Yellow Cards
	private int AY; 	// Away Team Yellow Cards
	private int HR; 	// Home Team Red Cards
	private int AR; 	// Away Team Red Cards
	
	
	//COSTRUTTORE CHE CI SERVE PER PUNTO ! CON SOLO DATI NECCESSARI---> DAO
	public Match(int id, Season season, Team homeTeam, Team awayTeam,String ftr) {
		super();
		this.id = id;
		this.season = season;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.ftr = ftr;
	}

	//COSTRUTTORE COMPLETO
	//Differenze con il DB: int season, String homeTeam, String awayTeam
	public Match(int id, Season season, String div, LocalDate date, 
			Team homeTeam, Team awayTeam, int fthg, int ftag, String ftr, 
			int hTHG, int hTAG, String hTR, int hS, int aS, int hST, int aST, 
			int hF, int aF, int hC, int aC, int hY, int aY, int hR, int aR) {
		super();
		this.id = id;
		this.season = season;
		this.div = div;
		this.date = date;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.fthg = fthg;
		this.ftag = ftag;
		this.ftr = ftr;
		HTHG = hTHG;
		HTAG = hTAG;
		HTR = hTR;
		HS = hS;
		AS = aS;
		HST = hST;
		AST = aST;
		HC = hC;
		AC = aC;
		HF = hF;
		AF = aF;
		HY = hY;
		AY = aY;
		HR = hR;
		AR = aR;
	}



	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Season getSeason() {
		return season;
	}


	public void setSeason(Season season) {
		this.season = season;
	}


	public String getDiv() {
		return div;
	}


	public void setDiv(String div) {
		this.div = div;
	}


	public LocalDate getDate() {
		return date;
	}


	public void setDate(LocalDate date) {
		this.date = date;
	}


	public Team getHomeTeam() {
		return homeTeam;
	}


	public void setHomeTeam(Team homeTeam) {
		this.homeTeam = homeTeam;
	}


	public Team getAwayTeam() {
		return awayTeam;
	}


	public void setAwayTeam(Team awayTeam) {
		this.awayTeam = awayTeam;
	}


	public int getFthg() {
		return fthg;
	}


	public void setFthg(int fthg) {
		this.fthg = fthg;
	}


	public int getFtag() {
		return ftag;
	}


	public void setFtag(int ftag) {
		this.ftag = ftag;
	}


	public String getFtr() {
		return ftr;
	}


	public void setFtr(String ftr) {
		this.ftr = ftr;
	}


	public int getHTHG() {
		return HTHG;
	}


	public void setHTHG(int hTHG) {
		HTHG = hTHG;
	}


	public int getHTAG() {
		return HTAG;
	}


	public void setHTAG(int hTAG) {
		HTAG = hTAG;
	}


	public String getHTR() {
		return HTR;
	}


	public void setHTR(String hTR) {
		HTR = hTR;
	}


	public int getHS() {
		return HS;
	}


	public void setHS(int hS) {
		HS = hS;
	}


	public int getAS() {
		return AS;
	}


	public void setAS(int aS) {
		AS = aS;
	}


	public int getHST() {
		return HST;
	}


	public void setHST(int hST) {
		HST = hST;
	}


	public int getAST() {
		return AST;
	}


	public void setAST(int aST) {
		AST = aST;
	}


	public int getHHW() {
		return HHW;
	}


	public void setHHW(int hHW) {
		HHW = hHW;
	}


	public int getAHW() {
		return AHW;
	}


	public void setAHW(int aHW) {
		AHW = aHW;
	}


	public int getHC() {
		return HC;
	}


	public void setHC(int hC) {
		HC = hC;
	}


	public int getAC() {
		return AC;
	}


	public void setAC(int aC) {
		AC = aC;
	}


	public int getHF() {
		return HF;
	}


	public void setHF(int hF) {
		HF = hF;
	}


	public int getAF() {
		return AF;
	}


	public void setAF(int aF) {
		AF = aF;
	}


	public int getHO() {
		return HO;
	}


	public void setHO(int hO) {
		HO = hO;
	}


	public int getAO() {
		return AO;
	}


	public void setAO(int aO) {
		AO = aO;
	}


	public int getHY() {
		return HY;
	}


	public void setHY(int hY) {
		HY = hY;
	}


	public int getAY() {
		return AY;
	}


	public void setAY(int aY) {
		AY = aY;
	}


	public int getHR() {
		return HR;
	}


	public void setHR(int hR) {
		HR = hR;
	}


	public int getAR() {
		return AR;
	}


	public void setAR(int aR) {
		AR = aR;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Match other = (Match) obj;
		if (id != other.id)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Match [id=" + id + ", season=" + season + ", homeTeam=" + homeTeam + ", awayTeam=" + awayTeam+ ", fthg=" + fthg + ", ftag=" + ftag + "]";
	}
	
	
	

}
