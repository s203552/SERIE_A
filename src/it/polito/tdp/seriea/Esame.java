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
	
	Se ci sono delle comboBox assicurati di averle inizializzate. 
	Esempi:
	private ChoiceBox<Season> boxSeason;
	private ChoiceBox<Team> boxTeam;
	
	___________________________________Nel metodo collegato al bottone:
	txtResult.claer();
	
	Se l'inserimento delle variabili � mediante ComboBox:
	
	Team team= this.boxTeam.getValue();
    	if (team == null) { 	
    	txtResult.setText("Errore: selezionare la squadra\n");
    	return;
    	}    
    	
    Season season= this.boxSeason.getValue();
    	if (season == null) {     
    	txtResult.setText("Errore: selezionare la stagione\n");
    	return;
    	} 
    	
    Se chiedo delle liste al model, controllo che non siano vuote. Esempio:
    List<Team> teamOnlyWin = model.getTeamsOnlyWin();
           if (teamOnlyWin = null) {
           txtResult.setText("Non ci sono squadre che hanno sempre vinto!");
           }
    txtResult.setText("Le squadre che hanno sempre solo vinto sono: \n");
    for(Team t : teamOnlyWin){
        txtResult.appendText(t.toString() + "\n");
    }
	____________________________________________________________________
	
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
