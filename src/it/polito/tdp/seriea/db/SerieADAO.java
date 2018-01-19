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

import it.polito.tdp.seriea.model.Coppie;
import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {
	
	/**
	 * @return numero goal 
	 */
	public List<Integer> getTotGOAL () {
		String sql = "SELECT DISTINCT fthg FROM matches ORDER BY fthg";
				
		List<Integer> result = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection() ;
	
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			res.next() ;
			result.add(res.getInt("fthg")) ;
			
			conn.close();
			return result ;
			
			} catch (SQLException e) {
	
			e.printStackTrace();
			throw new RuntimeException("Error in database query",e);
	
		}
	}	

	/**
	 * @return lista di tutte le stagioni 
	 */
	public List<Season> listSeasons() {
		String sql = "SELECT season, description FROM seasons" ;
		
		List<Season> seasons = new ArrayList<>() ;
		Connection conn = DBConnect.getConnection() ;
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				seasons.add( new Season(res.getInt("season"), res.getString("description"))) ;
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
	 * @return lista di tutte le squadre
	 */
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
	 * @return mappa di tutte le squadre
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
	
	/** 
	 * @return lista delle squadre per stagione
	 * @param s stagione
	 */
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
	 * @return mappa delle squadre per stagione
	 * @param s stagione
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

	/**
	 * @return lista partite giocate in @param stagione da @param Team
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
	public List<Coppie> getPartiteConPunteggio(Team team, Map<String,Team> teams, Season stagione) {
		
		String sql = "select hometeam , awayteam, fthg, ftag "
				+ "from matches m "
				+ "where season=? "
				+ "and HomeTeam=? "
				+ "group by AwayTeam"  ;
			List<Coppie> result = new ArrayList<>() ;
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			//GLI PASSO LA STAGIONE
			st.setInt(1, stagione.getSeason());
			st.setString(2, team.getTeam());
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				Team away = teams.get(res.getString("awayteam"));
				Coppie c = new Coppie(team, away,res.getInt("fthg"),res.getInt("ftag"));
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
	
	/**	
	 * @return diff reti
	 */
	public Integer diffRETI(Team  a,Team  a1,Season s,Map<String,Team> teams) {
		String sql = "SELECT  m.hometeam , m.awayteam, m.fthg-m.ftag AS dr FROM matches m "
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





	public static void main(String[] args) {
		SerieADAO dao = new SerieADAO() ;		
		List<Season> seasons = dao.listSeasons() ;
		System.out.println(seasons);		
		List<Team> teams = dao.listTeams() ;
		System.out.println(teams);
		
		Season s = seasons.get(seasons.size()-1);
		Team t = teams.get(19);
		Map<String, Team> mapSquadre = dao.MapTeams();
		
		List<Match> listPartite = dao.getPartiteByTeam(s, t, mapSquadre);
		for(Match m : listPartite)
			System.out.println(m +"\n");
		System.out.println(listPartite.size());
		
		System.out.println(dao.listTeamsforSeason(s));
		
		System.out.println(dao.MapTeamsForSeason(s));
		
		//QUESTO PROCEDIMENTO MI RESTITUISCE DATA UNA STAGIONE UNA MAPPA
		//CONTENETNE LE SQUADRE E IL TOTALE DEI GOAL FATTI
		//------>
		List<Team> listaTeamForSeason = dao.listTeamsforSeason(s);
		
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
	}

}

