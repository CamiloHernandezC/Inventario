package MaterialController;

import Controllers.util.JsfUtil;
import Entities.Almacen;
import Entities.Cardex;
import Entities.Materiales;
import Entities.MaterialesSucursalPK;
import Entities.Remisiones;
import Facade.CardexFacade;
import Utils.Navigation;
import java.util.Date;
import java.util.Objects;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named("cardexController")
@SessionScoped
public class CardexController extends Controllers.CardexController{
    
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void agregarTablaMaterial(){
        MaterialesController materialesController = JsfUtil.findBean("materialesController");
        if(String.valueOf(materialesController.getSelected().getIdMaterial()).length() != 1){
            return;
        }
        Materiales material = materialesController.buscarMaterial();
        if(material== null){
            JsfUtil.addErrorMessage("No se encontro material");
            limpiarMaterial(materialesController);
            return;
        }
        selected = new Cardex();
        if(items.isEmpty()){
            prepararCardex(material);
            items.add(selected);
            limpiarMaterial(materialesController);
            return;
        }
        prepararCardex(material);
        buscarIdMaterialItems();
        limpiarMaterial(materialesController);
    }

    private void prepararCardex(Materiales material) {
        selected.setCantida(1);
        selected.getMaterialesSucursal().setMateriales(material);
        selected.setAlmacen(new Almacen(1));
        selected.getMaterialesSucursal().setMaterialesSucursalPK(new MaterialesSucursalPK(material.getIdMaterial(), 1));
        selected.setRemision(new Remisiones(1));
    }

    private void buscarIdMaterialItems() {
        for(Cardex cardex: items){
            if(Objects.equals(cardex.getMaterialesSucursal().getMateriales().getIdMaterial(), selected.getMaterialesSucursal().getMateriales().getIdMaterial())){
                cardex.setCantida(cardex.getCantida()+1);
                cardex.setCantidadActual(cardex.getCantida());
                return;
            }
        }
        items.add(selected);
    }

    private void limpiarMaterial(MaterialesController materialesController) {
        materialesController.setSelected(null);
    }
    
    public void limpiar(){
        selected = null;
        items = null;
    }
    
     public String guardarCardex(){
        if(ejbFacade == null){
            ejbFacade = new CardexFacade();
        }
        for(Cardex cardex : items){
            ejbFacade.create(cardex);
        }
        ejbFacade = null;
        return Navigation.PAGE_INDEX;
    }
     
     public String cancelar(){
         limpiar();
         return Navigation.PAGE_INDEX;
     }
}
