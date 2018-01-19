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
	private List<Match> matches;
	private List<Team> teams;
	private Map<String,Team> mapTeam;
	private Map<Integer,Match> mapMatch;
	
	//grafo Orientato e Pesato
	private DefaultDirectedWeightedGraph<Team,DefaultWeightedEdge> graph;
	//grafo NON Orientato e Pesato
	private SimpleWeightedGraph<Team,DefaultWeightedEdge> graph2;
	
	
	//Visite
	private ConnectivityInspector<Team,DefaultWeightedEdge> ci;

	
//DEVO CREARE IL COSTRUTTORE DEL MODEL CON ALL'INTERNO IL DAO
	public Model() {
		super();
		dao=new SerieADAO();	

	}
	
//GET LISTA SEASON	
	public List<Season>getSeasons(){
		if(seasons==null){ 
			seasons=dao.listSeasons();}
		return seasons;
	}
//GET LISTA NUMERO GOAL	
	public List<Integer> getTotGOAL () {
		return dao.getTotGOAL();
	}	
//GET LISTA MATCH	
	public List<Match> getAllMatches(Season stagione){
		if(matches==null)	
			matches= dao.getMatchesForSeason(stagione,mapTeam);
		
		return matches;
	}
//GET LISTA TEAM CON CONTROLLO MAP	
	public List<Team>  getAllTeams() { 
		if(teams==null)	
			teams=dao.listTeams(); //LIST	
		return teams;
	}
//GET LISTA TEAM CON CONTROLLO MAP	
		public List<Team>  getAllTeamsBySeason(Season s) { 
			if(teams==null)	
				teams=dao.listTeamsforSeason(s); //LIST	
			this.mapTeam=new HashMap<>();
			for(Team atemp:teams){
				this.mapTeam.put(atemp.getTeam(), atemp);
			}
			return teams;
		}
	

	
//GRAFI
	public void creaGrafo(Season season) {
		
		String s="";
		if (graph==null)graph=new DefaultDirectedWeightedGraph<Team,DefaultWeightedEdge>(DefaultWeightedEdge.class);
				
		//aggiungi vertici
		Graphs.addAllVertices(graph, this.getAllTeamsBySeason(season));
		Map<String,Team> mapteam =dao.MapTeamsForSeason(season);

		System.out.println("Grafo creato: " + graph.vertexSet().size() + " nodi");	

		long time0 = System.nanoTime() ;
		
		//aggiungi archi
		//for(Match mtemp: this.getAllMatches(season)){
			for(Match mtemp:dao.getMatchesForSeason(season, mapTeam)){	
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
					Integer peso3=  dao.diffRETI(home,away, season, mapTeam);
					
					//IMPOSTO ARCHI
					DefaultWeightedEdge e1 = graph.addEdge(home,away);
					if(peso3>0)
					graph.setEdgeWeight(e1,peso3 );
					
					s+= home+" "+ away+" "+ peso3 +"\n";
					
				}
			}
		long time1 = System.nanoTime() ;
		
		//Per eliminare vertici non connessi
		Set<Team> elimina=new HashSet<Team>();
		for(Team ttemp: graph.vertexSet()){
			if(Graphs.neighborListOf(graph, ttemp).size()==0)
				elimina.add(ttemp);
		}
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
	Map<String,Team> team =dao.MapTeamsForSeason(season);
	
	Graphs.addAllVertices(graph, this.getAllTeamsBySeason(season));
//	System.out.println("\n"+graph.vertexSet().toString()+"\n");
	
	for (Team t : graph.vertexSet()){
		List<Coppie> co = dao.getPartiteConPunteggio(t, team, season);
		 if(co!=null){
			 for(Coppie c : co){
				 double peso=c.getGoalfatti()-c.getGoalsubiti();
				DefaultWeightedEdge de =graph.addEdge(c.getHome(), c.getAway());
				graph.setEdgeWeight(de, peso);
				s+= de +" "+ peso+"\n";
				}			 
		 }
	}
	System.out.println("Grafo creato: " + graph.vertexSet().size() + " nodi, " + graph.edgeSet().size() + " archi");	
	System.out.println(s);
}	

 	// CLASSIFICA	
 public List<TeamPunteggio> getClassifica(Season season){
		creaGrafo(season);
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
			@Override
			public int compare(TeamPunteggio o1, TeamPunteggio o2) {
				return Double.compare(o1.getPunteggio(), o2.getPunteggio());
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
			

//			System.out.println("-------------VISITE----------------------");			
//			
//			System.out.println("\n");
//			List<TeamPunteggio> t= model.getDestinations(s, team);
//		
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
			
