/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewControllers;

import Converters.util.JsfUtil;
import MaterialController.CardexController;
import Utils.Navigation;
import java.io.Serializable;
import java.util.Date;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author MAURICIO
 */
@Named(value = "generalViewControl")
@ViewScoped
public class GeneralViewControl implements Serializable{

    //<editor-fold desc="VARIBLES DECLARATION" defaultstate="collapsed">
    private Date actualDate;
    //</editor-fold>
       
    //<editor-fold desc="GETTER AND SETTER" defaultstate="collapsed">
    public Date getActualDate() {
        actualDate = new Date();
        return actualDate;
    }
    //</editor-fold>
    
    /**
     * Creates a new instance of CompleteEntry
     */
    public GeneralViewControl() {
    }
    
    //<editor-fold desc="NAVEGACION INDEX" defaultstate="collapsed">
    public String irConfiguracion(){
        return Navigation.PAGE_CONFIGURATION;
}
    
    public void irContactanos(){
        //HelpUtils helpUtils = JsfUtil.findBean("helpUtils");
        //helpUtils.limpiar();
        JsfUtil.showModal("diagContact");
    }
    
    public void irVerTodosMovimientos(){
        JsfUtil.showModal("diagMovCompletos");
    }
    
    public String irIndex(){
        return Navigation.PAGE_INDEX;
    }
    
    public String irIngresoManual(){
        return Navigation.PAGE_COMPLETE_ENTRY;
    }
    
    public String irIngresoExpress(){
        return Navigation.PAGE_EXPRESS_ENTRY;
    }
    
      public String irSalidaManual(){
        return Navigation.PAGE_COMPLETE_EXIT;
    }
    
    public String irSalidaExpress(){
        return Navigation.PAGE_EXPRESS_EXIT;
    }
    
    public String irIngresoMaterial(){
        CardexController cardexController = JsfUtil.findBean("cardexController");
        cardexController.limpiar();
        return Navigation.PAGE_MATERIAL_ENTRY;
    }
    
    public String irSalidaMaterial(){
        return Navigation.PAGE_MATERIAL_EXIT;
    }
    //</editor-fold>
    
}
