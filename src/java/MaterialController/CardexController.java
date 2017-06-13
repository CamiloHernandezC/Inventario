package MaterialController;

import Converters.util.JsfUtil;
import Entities.Almacen;
import Entities.Cardex;
import Entities.Materiales;
import Entities.MaterialesSucursal;
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
public class CardexController extends Converters.CardexController{
    
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void agregarTablaMaterial(){
        MaterialesController materialesController = JsfUtil.findBean("materialesController");
        if(materialesController.getSelected().getIdMaterial()==null){
            return;
        }        
        Materiales material = materialesController.buscarMaterial();
        if(material== null){
            JsfUtil.addErrorMessage("No se encontro material");
            limpiarMaterial(materialesController);
            JsfUtil.redirectTo(Navigation.PAGE_MATERIAL_ENTRY);
            return;
        }
        selected = new Cardex();
        if(items.isEmpty()){
            prepararCardex(material);
            items.add(selected);
            JsfUtil.redirectTo(Navigation.PAGE_MATERIAL_ENTRY);
            return;
        }
        prepararCardex(material);
        buscarIdMaterialItems();
        limpiarMaterial(materialesController);
        JsfUtil.redirectTo(Navigation.PAGE_MATERIAL_ENTRY);
    }

    private void prepararCardex(Materiales material) {
        selected.setCantida(1);
        selected.setMaterialesSucursal(new MaterialesSucursal(material.getIdMaterial(), 1));//TODO assign real branch office here
        selected.setAlmacen(new Almacen(1));//TODO assign real value here
        selected.setRemision(new Remisiones(1));//TODO assign real value here
    }

    private void buscarIdMaterialItems() {
        for(Cardex cardex: items){
            if(Objects.equals(cardex.getMaterialesSucursal().getMateriales().getIdMaterial(), selected.getMaterialesSucursal().getMaterialesSucursalPK().getIdMaterial())){
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
