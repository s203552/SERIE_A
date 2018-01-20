package it.polito.tdp.seriea.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.seriea.db.DBConnect;
import it.polito.tdp.seriea.db.SerieADAO;


public class Model {
	
	private SerieADAO dao;
	
	private List<Season> seasons;
	private List<Integer> anni;
	private Map<Integer,Season> mapSeason;
	
	private List<Match> matches;
	private List<Match> matchesBySeason;
	private Map<Integer,Match> mapMatch;
	private Map<Integer,Match> mapMatchBySeason;
	
	private List<Team> teams;
	private List<Team> teamsBySeason;
	private Map<String,Team> mapTeam;
	private Map<String,Team> mapTeamBySeason;

	
	//grafo Orientato e Pesato
	private DefaultDirectedWeightedGraph<Team,DefaultWeightedEdge> graph;
	//grafo NON Orientato e Pesato
	private SimpleWeightedGraph<Team,DefaultWeightedEdge> graphTEAM;
	private SimpleWeightedGraph<Integer,DefaultWeightedEdge> graphSeason;
	private SimpleWeightedGraph<Integer,DefaultWeightedEdge> graphGOAL;

	
	
	//Visite
	private ConnectivityInspector<Team,DefaultWeightedEdge> ci;

	
//DEVO CREARE IL COSTRUTTORE DEL MODEL CON ALL'INTERNO IL DAO
		public Model() {
			super();
			dao=new SerieADAO();
			this.mapSeason=new HashMap<>();
			this.mapTeam=new HashMap<>();
			this.mapTeamBySeason=new HashMap<>();
			this.mapMatch=new HashMap<>();
			this.mapMatchBySeason=new HashMap<>();

	
		}
//GET LISTA ANNI
		public List<Integer>getYears(){
			if(anni==null){ 		anni=dao.getYears();}
			return anni;
		}
//GET LISTA SEASON	
		public List<Season>getSeasons(){
			if(seasons==null){ 	
				seasons=dao.listSeasons();
				for(Season atemp:seasons){	
				this.mapSeason.put(atemp.getSeason(), atemp);		}
			}
			return seasons;
		}
//GET LISTA NUMERO GOAL	
		public List<Integer> getTotGOAL () {
			return dao.getTotGOAL();
		}	

//GET LISTA TEAM CON CONTROLLO MAP	
		public List<Team>  getAllTeams() { 
			if(teams==null)	{
				teams=dao.listTeams(); //LIST			
			for(Team atemp:teams){			this.mapTeam.put(atemp.getTeam(), atemp);		}
				}
			return teams;
		}
//GET LISTA TEAM BY SEASON CON CONTROLLO MAP	
		public List<Team>  getAllTeamsBySeason(Season s) { 
			if(teamsBySeason==null)	{
				teamsBySeason=dao.listTeamsforSeason(s); //LIST		
			for(Team atemp:teamsBySeason){ this.mapTeamBySeason.put(atemp.getTeam(), atemp);}
			}
			return teamsBySeason;
		}
//GET LISTA MATCH 
		public List<Match> getAllMatches(){
			if(matches==null){
				matches= dao.getAllMatches(mapTeam, mapSeason);
			for(Match atemp:matches){ this.mapMatch.put(atemp.getId(), atemp);}
				}
			return matches;
			}	

//GET LISTA MATCH BY SEASON
		public List<Match> getAllMatchesBySeason(Season stagione){
			if(matchesBySeason==null)	{
				matchesBySeason= dao.getMatchesForSeason(stagione,mapTeam);
			for(Match atemp:matchesBySeason){ this.mapMatchBySeason.put(atemp.getId(), atemp);}
				}
			return matchesBySeason;
			}
				
// ------------------------------------GRAFI----------------------------------------

	
	/**
	 * Permettere all’utente di specificare una stagione di interessa (tramite menu a tendina) e di richiedere il caricamento delle partite.
	 * Creare un grafo che rappresenti i risultati delle partite giocate nella stagione selezionata dall’utente.
	 * Il grafo dovrà essere orientato e pesato, con i vertici che rappresentino l’insieme di squadre che ha giocato
	 *  nella stagione indicata, e gli archi che rappresentino il risultato della partita. 
	 *  Il peso dell’arco tra TeamA e TeamB deve valere +1 se TeamA ha battuto TeamB, 0 se hanno pareggiato, -1 se TeamA ha perso.
	 *  Analizzare il grafo e calcolare la classifica finale del campionato, assegnando 3 punti alle vittorie, 
	 *  1 ai pareggi e 0 alle sconfitte. Stampare la classifica finale, in ordine decrescente di punteggio.
	 * @param season
	 */
	public void creaGrafo(Season season) {
		String s="";
		if (graph==null)graph=new DefaultDirectedWeightedGraph<Team,DefaultWeightedEdge>(DefaultWeightedEdge.class);				
		//aggiungi vertici
		Graphs.addAllVertices(graph, this.getAllTeamsBySeason(season));
		Map<Integer,Season>mapSEASON= dao.MapSeasons();
		Map<String,Team>mapTEAM=dao.MapTeamsForSeason(season);
		System.out.println("Grafo creato: " + graph.vertexSet().size() + " nodi");	
		//Tempo INIZIO
		long time0 = System.nanoTime() ;		
		//aggiungi archi
		//for(Match mtemp: this.getAllMatches(season)){
			for(Match mtemp:dao.getMatchesForSeason(season, mapTeamBySeason)){	
				Team home=mtemp.getHomeTeam();
				Team away=mtemp.getAwayTeam();
				if(home!=null && away!=null && !home.equals(away) ){			
					//CALCOLO PESO: Il peso dell’arco tra TeamA e TeamB deve valere +1 
					//se TeamA ha battuto TeamB, 0 se hanno pareggiato, -1 se TeamA ha perso.
					int peso;
					String ris=mtemp.getFtr();
					if(ris.compareTo("H")==0)  			{   peso=1;} //HOME WIN
					else  { if(ris.compareTo("A")==0) 		peso=-1; //AWAY WIN
						else  								peso=0;  //DRAW
					}				
					//CALCOLO PESO DIFFERENZA RETI 
					double peso2=mtemp.getFthg()-mtemp.getFtag();
					//meglio peso 3
					Integer peso3=  dao.diffRETI(home,away, season, mapTeamBySeason);
					//IMPOSTO ARCHI
					DefaultWeightedEdge e1 = graph.addEdge(home,away);
					graph.setEdgeWeight(e1,peso );				
					s+= home+" "+ away+" "+ peso +"\n";					
				}
			}
		//Tempo FINE
		long time1 = System.nanoTime() ;	
		//Per eliminare vertici non connessi
		Set<Team> elimina=new HashSet<Team>();
		for(Team ttemp: graph.vertexSet()){
			if(Graphs.neighborListOf(graph, ttemp).size()==0)
				elimina.add(ttemp);		}
		graph.removeAllVertices(elimina);
			
		System.out.println("Grafo creato: " + graph.vertexSet().size() + " nodi, " + graph.edgeSet().size() + " archi");	
		System.out.println("Tempo esecuzione grafo: " + (time1-time0)/1000000 + "  MilliSecondi");
		System.out.println(s);
	}

//Creo grafo in cui vertici squadre e archi diretti(homeaway) e pesati col numero di gol fatti dalla hometeam
// Alternativa con peso differenza fra i due 
 
 public void creaGrafo2(Season season){	
	String s="";
	graph= new DefaultDirectedWeightedGraph<Team,DefaultWeightedEdge>(DefaultWeightedEdge.class);
	Graphs.addAllVertices(graph, this.getAllTeamsBySeason(season));
	Map<Integer,Season>mapSEASON= dao.MapSeasons();
	Map<String,Team>mapTEAM=dao.MapTeamsForSeason(season);
	for (Team t : graph.vertexSet()){
		List<CoppieTeam> co = dao.getPartiteConPunteggio(t, mapTeamBySeason, season);
		 if(co!=null){
			 for(CoppieTeam c : co){
				 double peso=c.getGoalfatti()-c.getGoalsubiti();
				DefaultWeightedEdge de =graph.addEdge(c.getHome(), c.getAway());
				graph.setEdgeWeight(de, peso);
				s+= de +" "+ peso+"\n";
				} }
	}
	System.out.println("Grafo creato: " + graph.vertexSet().size() + " nodi, " + graph.edgeSet().size() + " archi");	
	System.out.println(s);
}
 
 //VICINI CONNESSI
	 public List<Team> trovaViciniConnessi(Team s,Season season) {	
			List<Team> connessi = new ArrayList<Team>();
			 connessi.addAll(Graphs.neighborListOf(graph,s));				
			return  connessi;
		}

 // CLASSIFICA	
	 public List<TeamPunteggio> getClassifica(Season season){
		List<TeamPunteggio> sq= new ArrayList<TeamPunteggio>();
		for(Team t : graph.vertexSet()){
			int punteggio=0;
			for(DefaultWeightedEdge de : graph.outgoingEdgesOf(t)){ 		//ARCHI USCENTI graph.outgoingEdgesOf
				int peso = (int)graph.getEdgeWeight(de);
				if(peso== 1)	punteggio+=3;
				if(peso==0)		punteggio++;
			  }
			sq.add(new TeamPunteggio(t,punteggio));			
		}		
		//ORDINO IN BASE AL PUNTEGGIO (crescente)
		sq.sort(new Comparator<TeamPunteggio>() {
			@Override		public int compare(TeamPunteggio o1, TeamPunteggio o2) {
							return -Double.compare(o1.getPunteggio(), o2.getPunteggio());
			}
		});
		return sq;
	}
 

	
 /** --------------------------------test model	---------------------------------------------------	*/		
	
	public static void main(String[] args) {
			
			Model model = new Model();
			Season s=new Season(2016);
			Team team=new Team("Genoa");
			Team team2=new Team("Sampdoria");
			
			model.creaGrafo(s) ;
//			model.creaGrafo2(s) ;
			List<Team> vc= model.trovaViciniConnessi(team,s);		
			System.out.println(vc);
			System.out.println("\n");	
			List<TeamPunteggio> classifica= model.getClassifica(s);		
			System.out.println(classifica);
			
			
			
//			System.out.println("---------------GRAFO TEAM----------------------");

//			model.creaGrafoTEAM() ;
//			List<Team> te= model.getRaggiungibiliInAmpiezza(team);
//			System.out.println(te);
//			List<TeamPunteggio> t= model.getDestinations(team);		
//			System.out.println(t);
			
//			System.out.println("---------------GRAFO Season----------------------");
//			model.creaGrafoSEASON() ;
//			List<Integer> ss= model.getRaggiungibiliInAmpiezza(2016);
//			System.out.println(ss);
//			List<IntegerPair> ips= model.getDestinations(2016);		
//			System.out.println(ips);
			
//			System.out.println("-----------------GRAFO GOAL----------------------");
//			model.creaGrafoGOAL() ;
//			List<Integer> te= model.getRaggiungibiliInAmpiezza(3);
//			System.out.println(te);
//			List<IntegerPair> t= model.getDestinations(3);		
//			System.out.println(t);

//			System.out.println("---------------------VISITE----------------------");			
//			
//			System.out.println("\n");
//			List<TeamPunteggio> t= model.getDestinations(s, team);		
//			System.out.println(t);
				
//			System.out.println(" \n----- successori------ \n");
//			List<Team> ss= model.trovaSucessori(team);
//			System.out.println(ss);
//			
//			System.out.println(" \n-------predeccessori------ \n ");
//			List<Team> pp= model.trovaPredecessori(team);
//			System.out.println(pp);
//
//			System.out.println(" \n------ calcolo percorso e il suo tempo-------  \n");
//			System.out.println(model.calcolaPercorso(team, team2));
//			
//			System.out.println("\n -------Squadre che il eam ha affrontato in quella stagione------- \n");
//			List<Team> te= model.getRaggiungibiliInAmpiezza(team);
//			System.out.println(te);
//			
//			System.out.println("\n------- n raggiungibili------ \n");
//			int i= model.raggiungibili();
//			System.out.println(i);
//			
//			System.out.println("\n-------- percorso BELLMAN---> UGUALE A disktra ma tiene conto peso negativo------ \n");
//			System.out.println(model.BELLMANcalcolaPercorso(team));

			
//			System.out.println("-------------CONNECTIVITY INSPECTOR----------------------");					
//			System.out.println("\n");
//		
//			System.out.println("\n------ è fortemente connesso?------ \n");			
//			System.out.println(model.isConnesso());	
//			
//			System.out.println("\n-------- connessioni grafo--------- \n");
//			int n=model.getNumberConn();
//			System.out.println(n);
//			
//			System.out.println("\n------ lista componenti connesse------ \n");		
//			List<Set<Team>> l =model.getConn(); 
//			System.out.println(l);	
//			
//			System.out.println("\n------- Arco max connesso------  \n");			
//			Set<Team> t=model.MAXconnesso(); 
//			System.out.println(t);
//			
//			System.out.println(" \n ------n archi Arco max connesso------  "\n");			
//			int nc=model.MAXconnessioni();   
//			System.out.println(nc);
//			
//			System.out.println("\n--------- 5 Archi di max connesso------- \n");
//			List<EdgePeso>e= model.get5ArchiMinWeight(); 
//			System.out.println(e);
//			
//					
//			System.out.println("--------------------- BEST & WORST-----------------------");
//			System.out.println("\n");
//			System.out.println("\n------ Best------  \n");		
//			Team winner=model.getBestTeam() ;
//			System.out.println(winner);
//			System.out.println("\n------ Worst------  \n");	
//			Team loser=model.getWorstTeam() ;
//			System.out.println(loser);
//			
//			System.out.println("\n");
//			System.out.println("---------------PIU VICINO & PIU LONTANO Al PUNTEGGIO DEL TEAM DI PARTENZA -----------------------");			
//			System.out.println("\n");
//			System.out.println("\n--------- Team Più Lontano come punti ---------\n");
//			Team l =model.getPiLontano("Genoa");
//			System.out.println(l);
//			
//			System.out.println("\n");
//			System.out.println("-------- Team Più Vicino come punti ---------");
//			System.out.println("\n");
//			
//			Team v =model.getPiuVicino("Genoa");
//			System.out.println(v);
//			
//			System.out.println("\n");
//			System.out.println("------- Team Più Lontano come punti per scontri diretti ----------");
//			System.out.println("\n");
//			
//			Team ld =model.getPiLontanoByVoloDiretto("Genoa");
//			System.out.println(ld);	

	}


}
			
