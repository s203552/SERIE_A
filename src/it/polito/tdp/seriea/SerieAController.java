/**
 * Sample Skeleton for 'SerieA.fxml' Controller Class
 */

package it.polito.tdp.seriea;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.seriea.model.Model;
import it.polito.tdp.seriea.model.TeamPunteggio;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class SerieAController {
	
	//RICHIAMO IL MODELLO
	Model model;

	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxSeason"
    private ChoiceBox<Season> boxSeason; // Value injected by FXMLLoader

    @FXML // fx:id="boxTeam"
    private ChoiceBox<Team> boxTeam; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    
    
    
    @FXML
    void handleCarica(ActionEvent event) {

    	
    //pulisco il testo
    	txtResult.clear();  
    	
    	
   	//riempio combobox con Team
    	Team team= this.boxTeam.getValue();
    	if (team == null)     { 
    		txtResult.appendText("Errore: selezionare la squadra\n");    
    	}
    //riempio combobox con stagioni
    	Season stagione= this.boxSeason.getValue();
    	if (stagione == null ) {   
    		txtResult.appendText("Errore: selezionare la stagione\n");
    	}
    	if(stagione.getSeason()<2003 && stagione.getSeason()>2017){
    		txtResult.appendText("selezionare stagione tra 2003 e 2017 \n");
    	}
    //richiamo lista che mi da la classifica ordinata in ordine decrescente
    	List<TeamPunteggio> ltemp= new ArrayList(  model.getClassifica(stagione));  	
    //per ogni stagione stampo Squadra e il punteggio---> IN txtResult
    	for(TeamPunteggio mstemp: ltemp){
    			txtResult.appendText(mstemp.getTeam().toString()+ " " +Integer.toString(mstemp.getPunteggio())+"\n");
    		} 
        
    	/**
    	 * PER ABILITARE il BOTTONE SUCCESSIVO
    	 */
        //	btnCerca.setDisable(false) ;

    } 
		
    @FXML
    void handleDomino(ActionEvent event) {
    //pulisco il testo
    	txtResult.clear();    	
    //RICHIAMO RICORSIONE
    	
//    	for(DefaultWeightedEdge e : model.trovaSequenza()){
//    		txtResult.appendText(e.toString()+" ");
//    	}
    }
 
    
//--------------------------SE HO LISTA FUORI CON PIU VALORI A TENDINA MA ME NE SERVE SOLO UNA DEI 2-------------------
//    CoppiaInt anni;
//    @FXML
//    void doCreaGrafo(ActionEvent event) {
//    	
//		
//    	anni  = boxAnni.getValue();	 
//		if(boxAnni.getValue()==  null)
//		  { txtResult.setText("Nessuna shape selezionata");
//		  		return;}
//		
//	    model.creaGrafo(anni.getAnno());
//	   
//		Sighting s  = boxSightingUs.getValue();	 
//		if(boxSightingUs.getValue()==  null)
//		  { txtResult.setText("Nessuna shape selezionata");
//		  		return;}
//
//    }
//
// //nel set model
//public void setModel(Model model) {
    
// this.model=model;	
// this.boxAnni.getItems().addAll(model.getAllCoppiaIntAnni());
// anni  = boxAnni.getValue();	 
// this.boxSightingUs.getItems().addAll(model.getAllStati(anni.getAnno()));		
//}


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxSeason != null : "fx:id=\"boxSeason\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert boxTeam != null : "fx:id=\"boxTeam\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'SerieA.fxml'.";
    }

    //------------------------>devo settare il modello!!  
  
	public void setModel(Model model) {
		
		this.model=model;
		//devo prendere le stagioni e metterle nella combobox e le inizializzo a 0
		this.boxSeason.getItems().addAll(model.getSeasons());
		this.boxSeason.setValue(this.boxSeason.getItems().get(0));	
		//devo prendere le Team e metterle nella combobox e le inizializzo a 0
		this.boxTeam.getItems().addAll(model.getAllTeams());
		this.boxTeam.setValue(this.boxTeam.getItems().get(0));	
	}
}
