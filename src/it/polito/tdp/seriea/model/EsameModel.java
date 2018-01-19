package it.polito.tdp.seriea.model;

import java.util.List;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class EsameModel {
	
// ------------> MODEL <-----------
	
	/**
	 private SerieADAO dao;
	private List<Season> stagioni;
	private List<Match> partite;
	private List<Team> squadre; 
	private DefaultDirectedWeightedGraph<Team,DefaultWeightedEdge> graph;
	public Model() {
		super();
		dao =new SerieADAO();
	}
	
	public List<Season>getSeasons(){
		if(stagioni==null){ 
			stagioni=dao.listSeasons();}
		return stagioni;
	}

	public List<Match> getAllPartite(Season stagione){
		if(partite==null)	
			partite= dao.getPartiteForSeason(stagione,mapSquadre);	
		return partite;
	}

	public List<Team>  getAllTeams() {
		if(squadre==null){	
			squadre=dao.listTeams();       
			for(Team ttemp: squadre){					   
				mapSquadre.put(ttemp.getTeam(),ttemp);}			
		}		
		return squadre;
	}
	 */

}
