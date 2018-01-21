package it.polito.tdp.seriea;

public class Esame {
	
	//  ---------------->   MAIN  <---------------
	
    /**
    
  	SerieAController controller = loader.getController() ;
	Model model=new Model();
	controller.setModel(model);
	
	*/
	
	// ------------------>  CONTROLLER  <---------------
	
	/**
	
	Model model;
	
	_______________________________________________
	SET MODEL :
	
	public void setModel(Model model) {
		
		this.model=model;
		
		//devo prendere le stagioni e metterle nella combobox e le inizializzo a 0
		this.boxSeason.getItems().addAll(model.getSeasons());
		this.boxSeason.setValue(this.boxSeason.getItems().get(0));	
		
		//devo prendere le Team e metterle nella combobox e le inizializzo a 0
		this.boxTeam.getItems().addAll(model.getAllTeams());
		this.boxTeam.setValue(this.boxTeam.getItems().get(0));	
	}
	____________________________________________________________________
	
	 */
	
}
