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
				if(!home.equals(away)){
			
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
		//ORDINO IN BASE AL PUNTEGGIO
		
	    //Collections.sort(sq); ---> CREI CLASSE COMPARATORE PUNTEGGIO
		/*
		 * package it.polito.tdp.seriea.model;
			import java.util.Comparator;
			public class ComparatorePunteggi implements Comparator<ModelStat> {	
				@Override  
				 public int compare(ModelStat o1, ModelStat o2) {return -(o1.getPunteggio()-o2.getPunteggio());}
			}
		 */
		sq.sort(new Comparator<TeamPunteggio>() {
			@Override
			public int compare(TeamPunteggio o1, TeamPunteggio o2) {
				return Double.compare(o1.getPunteggio(), o2.getPunteggio());
			}
		});
		return sq;
	}
 
 
 /* Escludendo gli aeroporti con zero rotte, determinare se nel grafo ottenuto è possibile da ogni aeroporto
 raggiungere ogni altro aeroporto.
 ----->>> fortemente connesso?  */	
 	
 	public String isConnesso () {
 		ci = new ConnectivityInspector<>(this.graph);
 	    if (ci.isGraphConnected()){ return "Il grafo--> fortemente connesso";	 }
 		else return "il grafo---> non fortemente connesso";
 	}
 			
 /* lista degli aereoporti connessi */
 		
 	 public List<Set<Team>> getConn(){	
 			ci = new ConnectivityInspector<>(this.graph);
 			return ci.connectedSets();
 		}
 		
 /* numero degli aereoporti connessi */
 		public int getNumberConn(){
 			return this.getConn().size();
 		}

 /* trovo (max) degli aereoporti connessi */
 		public Set<Team> MAXconnesso() {
 			Set<Team> MAX=null;
 			Double size=0.0;
 			for(Set<Team> atemp :this.getConn()){
 				 if(atemp.size()>size){
 					 size = (double) atemp.size();
 					 MAX=atemp;
 				 }	 
 			 }	 
 			return MAX;	
 		}
 	
 /* trovo (max size) aereoporti connessi */
 		public int MAXconnessioni() {
 			return this.MAXconnesso().size();		
 		}
 			
 /* trovo 5 achi con peso minimo di (max size) */				 
 		
 		public List<EdgePeso> get5ArchiMinWeight (){

 		  List<EdgePeso>  edgePeso= new  ArrayList<>();
 		  // avevo pensato di sostituire i vertici del grafo con il set trovato Max connesso per poi richiamare il metodo CreaGrafo()
 		 Set<Team> new_vertex =this.MAXconnesso();
 		 for (Team a1: new_vertex){
 		  for (Team a2: new_vertex){
 			  if (! a1.equals(a2)&& a1!=null&&a2!=null){ 
 				  //peso
 					DefaultWeightedEdge e=graph.getEdge(a1, a2);
 						if(e!=null ){
 							graph.getEdgeWeight(e);
 							edgePeso.add(new EdgePeso(e,graph.getEdgeWeight(e)) );
 						}					
 				  }  
 		  }	
 		 } 
 		 //peso minimo se ti chiede max devi cambiare nella classe EDGE PESO
 		 Collections.sort(edgePeso);
 		 return edgePeso.subList(0, 5);
 	    }
 
 
 

 /** --------------------------------test model	---------------------------------------------------	*/		
	
	public static void main(String[] args) {
			
			Model model = new Model();
			Season s=new Season(2016);
			model.creaGrafo(s) ;

			
//			model.creaGrafo2(s) ;
			
//			System.out.println("\n");
//			System.out.println("-------- connessioni grafo ---------");
//			System.out.println("\n");
//			
//			System.out.println(model.isConnesso());			
//			int n=model.getNumberConn();
//			System.out.println(n);
//			
//			System.out.println("\n");		
//			List<Set<Team>> l =model.getConn(); //lista componenti connesse
//			System.out.println(l);	
//			
//			System.out.println("\n");			
//			Set<Team> t=model.MAXconnesso(); //Arco max connesso 
//			System.out.println(t);
//			
//			System.out.println("\n");			
//			int nc=model.MAXconnessioni();  // n archi Arco max connesso 
//			System.out.println(nc);
//			
//			System.out.println("\n");
//			List<EdgePeso>e= model.get5ArchiMinWeight(); //5 Archi di max connesso
//			System.out.println(e);
//			
//					
//			System.out.println("\n");
//			System.out.println("-------- Best & Worst ---------");
//			System.out.println("\n");
//			
//			Team winner=model.getBestAirport() ;
//			System.out.println(winner);
//			Team loser=model.getWorstAirport() ;
//			System.out.println(loser);
			
			
//			System.out.println("\n");
//			System.out.println("-------- Aereoporto Più Lontano ---------");
//			System.out.println("\n");
//			
//			Airport n =model.getPiLontano(1555);
//			System.out.println(n);
//			
//			System.out.println("\n");
//			System.out.println("------- Aereoporto Più Lontano per volo diretto ----------");
//			System.out.println("\n");
//			
//			Airport l =model.getPiLontanoByVoloDiretto(1555);
//			System.out.println(l);	
//			
//			System.out.println("\n");
//			System.out.println("------- Aereoporto Più Lontano non Raggiungibile ----------");
//			System.out.println("\n");
//			
//			Airport  s=model.getLontanissimo(3484);
//			System.out.println(s);
//			
//			System.out.println("\n");
//			System.out.println("-------- connesso? ---------");
//			System.out.println("\n");
//						
//			System.out.println(model.isConnesso());
//			
//			System.out.println("\n");
//			System.out.println("------- Aereoporti di destinazione e distanza ----------");
//			System.out.println("\n");
//			
//			List<AirportDistance>  ad=model.getDestinations(ar, a);
//			System.out.println(ad);
//			
//			System.out.println("\n");
//			System.out.println("------- Aereoporti raggiungibili ----------");
//			System.out.println("\n");
//			
//			List<Airport>  Ra=model.getReachedAirports(ar);
//			System.out.println(Ra);
//			
//			System.out.println("\n");
//			System.out.println("------- Aereoporti raggiungibili QUERY ----------");
//			System.out.println("\n");
//			
//			List<Airport> AR= model.getAeroportiRaggiunti(ar);
//			System.out.println(AR);
//

	}


}
			
