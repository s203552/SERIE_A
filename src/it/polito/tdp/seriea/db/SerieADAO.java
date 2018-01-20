package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.seriea.model.CoppieTeam;
import it.polito.tdp.seriea.model.IntegerPair;
import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {
	
	/**
	 * @return numero goal & stagioni
	 */
	public List<Integer> getTotGOAL () {
		String sql = "SELECT DISTINCT fthg FROM matches ORDER BY fthg";
				
		List<Integer> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection() ;
	
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				result.add(new Integer (res.getInt("fthg")));
			}
			
			conn.close();
			return result ;
			
			} catch (SQLException e) {
	
			e.printStackTrace();
			throw new RuntimeException("Error in database query",e);
	
		}
	}	

	public List<Integer> getYears () {
		String sql = "SELECT Distinct season FROM matches";
				
		List<Integer> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection() ;
	
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				result.add(new Integer (res.getInt("season")));
			}	
			
			conn.close();
			return result ;
			
			} catch (SQLException e) {
	
			e.printStackTrace();
			throw new RuntimeException("Error in database query",e);
	
		}
	}	

	/**
	 *  * @return MAPPA e lista di tutte le SEASON 
	 */
	public Map<Integer,Season> MapSeasons() {
		String sql = "SELECT season, description FROM seasons" ;
		
		Map<Integer,Season>  seasons = new HashMap<>() ;
		Connection conn = DBConnect.getConnection() ;
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				seasons.put( res.getInt("season"), new Season(res.getInt("season") ,res.getString("description"))) ;
			}	
			conn.close();
			return seasons ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Season> listSeasons() {
				String sql = "SELECT Distinct season FROM seasons" ;
				
				List<Season> seasons = new ArrayList<>() ;
				Connection conn = DBConnect.getConnection() ;
				try {
					PreparedStatement st = conn.prepareStatement(sql) ;
					ResultSet res = st.executeQuery() ;
					while(res.next()) {
						seasons.add( new Season(res.getInt("season"))) ;
					}	
					conn.close();
					return seasons ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null ;
				}
			}

	/** 
	 * * @return mappa e lista di tutti i TEAM
	 */
	public Map<String,Team> MapTeams() {
		String sql = "SELECT distinct HomeTeam FROM matches" ;
		Map<String,Team> mapTeam = new HashMap<String,Team>() ;
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			
			while(res.next()) {
				mapTeam.put( res.getString("HomeTeam"),new Team(res.getString("HomeTeam"))) ;
			}
			
			conn.close();
			return mapTeam ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams" ;
		List<Team> teams = new ArrayList<>() ;
		Connection conn = DBConnect.getConnection() ;	
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				teams.add( new Team(res.getString("team"))) ;
			}
			conn.close();
			return teams ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	/** 
	 * @return mappa e LisT  delle TEAM FOR SEASON
	 */
	public Map<String,Team> MapTeamsForSeason(Season s) {
		String sql = "SELECT distinct HomeTeam FROM matches WHERE Season=?" ;
		Map<String,Team> mapTeamForSeason = new HashMap<String,Team>() ;
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, s.getSeason());
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				mapTeamForSeason.put( res.getString("HomeTeam"),new Team(res.getString("HomeTeam"))) ;
			}
			
			conn.close();
			return mapTeamForSeason ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Team> listTeamsforSeason(Season s) {
		String sql = "SELECT DISTINCT HomeTeam FROM matches Where season=?" ;	
		List<Team> teamForSeason = new ArrayList<>() ;
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, s.getSeason());
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				teamForSeason.add( new Team(res.getString("HomeTeam"))) ;
			}	
			conn.close();
			return teamForSeason ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	/**	
	 * @return tutti i MATCH
	 */
	public List<Match> getAllMatches( Map<String, Team> mapSquadre,Map<Integer, Season> mapSeason) {
		
		String sql = "SELECT DISTINCT * FROM matches ";
		List<Match> matches = new ArrayList<>() ;
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				//Costruttore completo
				matches.add(new Match(res.getInt("match_id"),mapSeason.get(res.getString("season")), res.getString("div"), res.getDate("date").toLocalDate(), mapSquadre.get(res.getString("HomeTeam")), 
						                mapSquadre.get(res.getString("AwayTeam")), res.getInt("fthg"),  res.getInt("ftag"), res.getString("ftr"),  res.getInt("hTHG"),  
						                res.getInt("hTAG"), res.getString("hTR"), res.getInt("hS"),  res.getInt("aS"),  res.getInt("hST"),  res.getInt("aST"), res.getInt("hF"),  res.getInt("aF"),  
						                res.getInt("hC"),  res.getInt("aC"),  res.getInt("hY"),  res.getInt("aY"), res.getInt("hR"),  res.getInt("aR")));
				//Costruttore ridotto
				/*matches.add( new Match(res.getInt("match_id"), stagione, mapSquadre.get(res.getString("HomeTeam")), 
									  mapSquadre.get(res.getString("AwayTeam")), res.getString("Ftr")));*/
			}
			conn.close();
			return matches ;
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	/**
	 * @return lista partite giocate in una stagione da @param Team,Season
	 * @param mapSquadre mappa delle squadre  */
	public List<Match> getPartiteByTeam(Season stagione,Team Team, Map<String, Team> mapSquadre) {
		
		String sql = "SELECT * FROM matches "+
	                 "WHERE season=? AND (HomeTeam=? OR AwayTeam =?) "  ;
		List<Match> matches = new ArrayList<>() ;
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, stagione.getSeason());
			st.setString(2, Team.getTeam());
			st.setString(3, Team.getTeam());
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				//Costruttore completo
				matches.add(new Match(res.getInt("match_id"), stagione, res.getString("div"), res.getDate("date").toLocalDate(), mapSquadre.get(res.getString("HomeTeam")), 
						                mapSquadre.get(res.getString("AwayTeam")), res.getInt("fthg"),  res.getInt("ftag"), res.getString("ftr"),  res.getInt("hTHG"),  
						                res.getInt("hTAG"), res.getString("hTR"), res.getInt("hS"),  res.getInt("aS"),  res.getInt("hST"),  res.getInt("aST"), res.getInt("hF"),  res.getInt("aF"),  
						                res.getInt("hC"),  res.getInt("aC"),  res.getInt("hY"),  res.getInt("aY"), res.getInt("hR"),  res.getInt("aR")));
				//Costruttore ridotto
				/*matches.add( new Match(res.getInt("match_id"), stagione, mapSquadre.get(res.getString("HomeTeam")), 
									  mapSquadre.get(res.getString("AwayTeam")), res.getString("Ftr")));*/
			}
			conn.close();
			return matches ;
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	/**
	 * @return lista partite giocate in @param stagione
	 * @param mapSquadre mappa delle squadre  */
	public List<Match> getMatchesForSeason(Season stagione, Map<String, Team> mapSquadre) {
		
		String sql = "SELECT * FROM matches "+
	                 "WHERE season=? "  ;
		List<Match> matches = new ArrayList<>() ;
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, stagione.getSeason());
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				//Costruttore completo
				matches.add(new Match(res.getInt("match_id"), stagione, res.getString("div"), res.getDate("date").toLocalDate(), mapSquadre.get(res.getString("HomeTeam")), 
						                mapSquadre.get(res.getString("AwayTeam")), res.getInt("fthg"),  res.getInt("ftag"), res.getString("ftr"),  res.getInt("hTHG"),  
						                res.getInt("hTAG"), res.getString("hTR"), res.getInt("hS"),  res.getInt("aS"),  res.getInt("hST"),  res.getInt("aST"), res.getInt("hF"),  res.getInt("aF"),  
						                res.getInt("hC"),  res.getInt("aC"),  res.getInt("hY"),  res.getInt("aY"), res.getInt("hR"),  res.getInt("aR")));
				//Costruttore ridotto
				/*matches.add( new Match(res.getInt("match_id"), stagione, mapSquadre.get(res.getString("HomeTeam")), 
									  mapSquadre.get(res.getString("AwayTeam")), res.getString("Ftr")));*/
			}
			conn.close();
			return matches ;
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	/**
	 * @return list di coppie che data la stagione e un team restituisce tutti i match e i goal in casa e fuori casa
	 * @param team  @param teams @param stagione
	 */
	public List<CoppieTeam> getPartiteConPunteggio(Team team, Map<String,Team> teams, Season stagione) {
		
		String sql = "select hometeam , awayteam, fthg, ftag "
				+ "from matches m "
				+ "where season=? "
				+ "and HomeTeam=? "
				+ "group by AwayTeam"  ;
			List<CoppieTeam> result = new ArrayList<>() ;
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			//GLI PASSO LA STAGIONE
			st.setInt(1, stagione.getSeason());
			st.setString(2, team.getTeam());
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				Team away = teams.get(res.getString("awayteam"));
				CoppieTeam c = new CoppieTeam(team, away,res.getInt("fthg"),res.getInt("ftag"));
				result.add(c);
			}
			
			conn.close();
			return result ;
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
	}
		//VITTORIA CASA CON ALMENO 2 GOAL DI SCARTO______> QUERY:   M.FTHR-MFTAR >2
	
	/** @return numero di squadre che hanno giocato nelle due stagioni
	 */
	public List<Match> nSquadreNelleSTAGIONI(Integer s1,Integer s2,Map<String,Team> mapSquadre,Map<Integer, Season> mapSeason) {
		String sql = "(SELECT DISTINCT * FROM matches m WHERE m.`Season`= ?  GROUP BY  m.`HomeTeam`)"
				+ " UNION (SELECT DISTINCT * FROM matches m WHERE m.`Season`= ?  GROUP BY m.`HomeTeam`) ";
				
		List<Match>result= new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection() ;
	
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, s1);
			st.setInt(2, s2);
			
			ResultSet res = st.executeQuery() ;
			res.next() ;
			result.add(new Match(res.getInt("match_id"),mapSeason.get(res.getString("season")), res.getString("div"), res.getDate("date").toLocalDate(), mapSquadre.get(res.getString("HomeTeam")), 
	                mapSquadre.get(res.getString("AwayTeam")), res.getInt("fthg"),  res.getInt("ftag"), res.getString("ftr"),  res.getInt("hTHG"),  
	                res.getInt("hTAG"), res.getString("hTR"), res.getInt("hS"),  res.getInt("aS"),  res.getInt("hST"),  res.getInt("aST"), res.getInt("hF"),  res.getInt("aF"),  
	                res.getInt("hC"),  res.getInt("aC"),  res.getInt("hY"),  res.getInt("aY"), res.getInt("hR"),  res.getInt("aR")));

			conn.close();
			return result ;
			
			} catch (SQLException e) {
	
			e.printStackTrace();
			throw new RuntimeException("Error in database query",e);
	
		}
	}	

	
/**-------------------------------------------PESI GRAFICI ESAMI---------------------------------------*/
/**----------------------------------------------------------------------------------*/

	
	/**------------------------------------PESO GRAFOGOAL----------------------------------------------*/
	
	/**	
	 * @return partite giocate finite con quel risultato
	 */
	public Integer nPartiteFiniteXaY(Integer i,Integer i2) {
		
		String sql = "SELECT  m.FTHG, m.FTAG, count(*) AS dr FROM matches m WHERE  m.`FTHG`=? AND m.`FTAG`=? group by m.`FTHG`, m.`FTAG` ";	
		try {
			Connection conn = DBConnect.getConnection() ;
	
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, i);
			st.setInt(2, i2);
			ResultSet res = st.executeQuery() ;
			res.next() ;
			Integer result = res.getInt("dr") ;
			
			conn.close();
			return result ;
			
			} catch (SQLException e) {
	
			e.printStackTrace();
			throw new RuntimeException("Error in database query",e);
	
		}
	}	
	
	public Integer nPartiteFiniteXaY_InSTAGIONE(Integer i,Integer i2,Season s) {
		
		String sql = "SELECT  season,m.FTHG, m.FTAG, count(*) AS dr FROM matches m WHERE  m.`FTHG`=3 AND m.`FTAG`=1 AND season= 2017 GROUP BY m.`FTHG`, m.`FTAG`,season";	
		try {
			Connection conn = DBConnect.getConnection() ;
	
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, i);
			st.setInt(2, i2);
			st.setInt(3, s.getSeason());
			ResultSet res = st.executeQuery() ;
			res.next() ;
			Integer result = res.getInt("dr") ;
			
			conn.close();
			return result ;
			
			} catch (SQLException e) {
	
			e.printStackTrace();
			throw new RuntimeException("Error in database query",e);
	
		}
	}	

	
	
	/**-------------------------------------PESO GRAFOTEAM---------------------------------------------*/
		
	/*	 @return numero partite o stagioni che hanno giocato contro	 */
	public Integer nPartiteGiocateContro(Team  a,Team  a1,Map<String,Team> teams) {
		
		//count per 2 perchè è andata e ritorno
		
		//nPartiteGiocateContro
		String sql = "SELECT  m.hometeam , m.awayteam, count(*)*2 AS dr "    
				+ "FROM matches m WHERE  m.hometeam=? AND m.awayteam=? GROUP BY m.hometeam, m.`AwayTeam`";
		//nStagioniGiocateContro		
		String sql1 = "SELECT m.hometeam , m.awayteam, count(season) AS dr FROM matches m WHERE m.hometeam=? AND m.awayteam=? GROUP BY m.hometeam , m.awayteam";
		
		try {
			Connection conn = DBConnect.getConnection() ;
	
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setString(1, a.getTeam());
			st.setString(2, a1.getTeam());
			
			
			ResultSet res = st.executeQuery() ;
			res.next() ;
			Team away = teams.get(res.getString("m.awayteam"));
			Integer result = res.getInt("dr") ;
			
			conn.close();
			return result ;
			
			} catch (SQLException e) {
	
			e.printStackTrace();
			throw new RuntimeException("Error in database query",e);
	
		}
	}	
	/*	 @return TOTgoalScontriDiretti------SUM diff reti----->TOTgoal team in casa----->TOTgoal team in trasferta	 */
	public Integer TOTgoalScontriDiretti(Team  a,Team  a1,Map<String,Team> teams) {
		String sql = "SELECT m.hometeam , m.awayteam, sum(m.fthg+m.FTAG) AS dr "
				+ "FROM matches m WHERE m.hometeam=? AND m.awayteam=? GROUP BY m.hometeam , m.awayteam";
		//SOMMA DIFFERENZA RETI SCONTRI DIRETTI
		String sql1 = "SELECT m.hometeam , m.awayteam, sum(m.fthg-m.FTAG) AS dr "
				+ "FROM matches m WHERE m.hometeam=? AND m.awayteam=? GROUP BY m.hometeam , m.awayteam";
		//SOMMA GOAL SQUADRA IN CASA SCONTRI DIRETTI
		String sql2 = "SELECT m.hometeam , m.awayteam, sum(m.fthg) AS dr "
				+ "FROM matches m WHERE m.hometeam=? AND m.awayteam=? GROUP BY m.hometeam , m.awayteam";
		//SOMMA GOAL SQUADRA IN TRASFERTA  SCONTRI DIRETTI
		String sql3 = "SELECT m.hometeam , m.awayteam, sum(m.FTAG) AS dr "
				+ "FROM matches m WHERE m.hometeam=? AND m.awayteam=? GROUP BY m.hometeam , m.awayteam";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setString(1, a.getTeam());
			st.setString(2, a1.getTeam());
			
			
			ResultSet res = st.executeQuery() ;
			res.next() ;
			Team away = teams.get(res.getString("m.awayteam"));
			Integer result = res.getInt("dr") ;
			
			conn.close();
			return result ;
			
			} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException("Error in database query",e);

		}
	}	

	/**	
	 *  DATA LA STAGIONE
	 * 
	 * 
	 * 
	 * @return diff reti----->Goal in casa----->Goal in trasferta---->tot goal
	 */
	public Integer diffRETI(Team  a,Team  a1,Season s,Map<String,Team> teams) {
		//diff_REti
		String sql = "SELECT  m.hometeam , m.awayteam, m.fthg-m.ftag AS dr FROM matches m "
				+ "WHERE m.season=? AND m.hometeam=? AND m.awayteam=?";
		//TOT
		String sql1 = "SELECT  m.hometeam , m.awayteam, m.fthg+m.ftag AS dr FROM matches m "
				+ "WHERE m.season=? AND m.hometeam=? AND m.awayteam=?";		
		//CASA
		String sql2 = "SELECT  m.hometeam , m.awayteam, m.fthg AS dr FROM matches m "
				+ "WHERE m.season=? AND m.hometeam=? AND m.awayteam=?";
		//Trasferta
		String sql3 = "SELECT  m.hometeam , m.awayteam, m.ftag AS dr FROM matches m "
				+ "WHERE m.season=? AND m.hometeam=? AND m.awayteam=?";
		try {
			Connection conn = DBConnect.getConnection() ;
	
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, s.getSeason());
			st.setString(2, a.getTeam());
			st.setString(3, a1.getTeam());
			
			
			ResultSet res = st.executeQuery() ;
			res.next() ;
			Team away = teams.get(res.getString("m.awayteam"));
			Integer result = res.getInt("dr") ;
			
			conn.close();
			return result ;
			
			} catch (SQLException e) {
	
			e.printStackTrace();
			throw new RuntimeException("Error in database query",e);
	
		}
	}	
	
	
	
	/**---------------------------------------PESO GRAFOSEASON-------------------------------------------*/
	
	/* @return numero squadre  nelle due stagioni	 */
	public List<String> Squadre2Stagioni(Integer s,Integer s1) {
		
		String sql = "SELECT m.hometeam FROM matches m "
				+ "WHERE  m.hometeam IN (SELECT DISTINCT hometeam FROM matches m WHERE m.`Season`= ?  GROUP BY  m.`HomeTeam`)"
				+ " AND m.hometeam IN (SELECT DISTINCT hometeam FROM matches m WHERE m.`Season`= ? GROUP BY m.`HomeTeam`) GROUP BY m.hometeam";
		
				
		List<String> result = new ArrayList<>() ;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, s);
			st.setInt(2, s1);
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				result.add(new String (res.getString("m.hometeam")));
			}	
			 
			
			conn.close();
			return result ;
			
			} catch (SQLException e) {
	
			e.printStackTrace();
			throw new RuntimeException("Error in database query",e);
	
		}
	
	}

	/* @return  GOAL  tot, diff, casa, trasferta nelle due stagioni	 */
	public Integer GoalTotali2Stagioni(Integer s,Integer s1) {
		//TOTALI in 2 stagioni
		String sql = "SELECT sum(fthg+ftag) as dr FROM matches m WHERE  m.hometeam IN (SELECT DISTINCT hometeam FROM matches m GROUP BY m.`HomeTeam`) AND season=? OR season=?";
		//diff_reti in 2 stagioni
		String sql1 = "SELECT sum(fthg-ftag) as dr FROM matches m WHERE  m.hometeam IN (SELECT DISTINCT hometeam FROM matches m GROUP BY m.`HomeTeam`) AND season=? OR season=?";
		//in casa in 2 stagioni
		String sql2 = "SELECT sum(fthg) as dr FROM matches m WHERE  m.hometeam IN (SELECT DISTINCT hometeam FROM matches m GROUP BY m.`HomeTeam`) AND season=? OR season=?";
		//in trasferta in 2 stagioni
		String sql3 = "SELECT sum(ftag) as dr FROM matches m WHERE  m.hometeam IN (SELECT DISTINCT hometeam FROM matches m GROUP BY m.`HomeTeam`) AND season=? OR season=?";
		
					
		
		try {
			Connection conn = DBConnect.getConnection() ;
	
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, s);
			st.setInt(2, s1);
			
			ResultSet res = st.executeQuery() ;
			res.next() ;
			Integer result = res.getInt("dr") ;
			
			conn.close();
			return result ;
			
			} catch (SQLException e) {
	
			e.printStackTrace();
			throw new RuntimeException("Error in database query",e);
	
		}
	}	






	//SELECT season,sum(fthg+ftag) FROM matches m WHERE Season=?  GROUP BY season







//-------------------------------------main----------------------------------------------
		
	

	public static void main(String[] args) {
		
		SerieADAO dao = new SerieADAO() ;		
		Season s = new Season (2015);
		Season s2 = new Season (2015);
		Team team=new Team("Genoa");
		Team team2=new Team("Sampdoria");
		
		List<Season> seasons = dao.listSeasons() ;		
		List<Team> teams = dao.listTeams() ;
		List<Team> listaTeamForSeason = dao.listTeamsforSeason(s);
		Map<String, Team> teamMAP =dao.MapTeams();

//		
//		List<Match> listPartite = dao.getPartiteByTeam(s, t, mapSquadre);
//		for(Match m : listPartite)
//			System.out.println(m +"\n");
//		System.out.println(listPartite.size());
//		
//		System.out.println(dao.listTeamsforSeason(s));
//		
//		System.out.println(dao.MapTeamsForSeason(s));
		
		//QUESTO PROCEDIMENTO MI RESTITUISCE DATA UNA STAGIONE UNA MAPPA
		//CONTENETNE LE SQUADRE E IL TOTALE DEI GOAL FATTI
		//------>
		
//		 Map<Team, Integer> mappaGoalH = dao.ListaGoalHomeInSeasonDESC(s, mapSquadre);
//		 Map<Team, Integer> mappaGoalA = dao.ListaGoalAwayInSeasonDESC(s, mapSquadre);
//		 
//		 Map<Team, Integer> mappaGoal = new HashMap<Team, Integer>();
//		
//		 for(Team t1 : mappaGoalH.keySet())
//			 for(Team t2 : mappaGoalA.keySet()){
//				 if(t1.equals(t2))
//					 mappaGoal.put(t1, mappaGoalH.get(t1)+mappaGoalA.get(t2));
//			 }
//		 		 
//		 System.out.println(mappaGoal);
		 //------->


	Integer ip= dao.nPartiteFiniteXaY(3, 1);
	System.out.println(ip);
	
	List<String> sq = dao.Squadre2Stagioni(  2015, 2016);
	System.out.println(sq);
	}
}

