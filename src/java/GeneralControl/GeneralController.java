/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeneralControl;

import Controllers.util.JsfUtil;
import PersonControllers.PersonasSucursalController;
import PersonControllers.manualController;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;

import javax.inject.Named;

/**
 *
 * @author MAURICIO
 */
@Named(value = "generalController")
@SessionScoped
public class GeneralController implements Serializable{

    
    private final manualController manualController = JsfUtil.findBean("manualController");
    private final PersonasSucursalController personasSucursalCliController = JsfUtil.findBean("personasSucursalCliController");
    /**
     * Creates a new instance of MenuController
     */
    public GeneralController() {
        
    }
    
    public String clean(String redirectTo){
        if(manualController != null){
           manualController.clean(); 
        }
        if(personasSucursalCliController!=null){
            personasSucursalCliController.clean();
        }
        return redirectTo;
    }
    
}
