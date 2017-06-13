package MaterialController;

import Entities.Materiales;
import Querys.Querys;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named("materialesController")
@SessionScoped
public class MaterialesController extends Converters.MaterialesController{

    public MaterialesController() {
    }
    
    public void agregarMaterial(){
        buscarMaterial();
    }
    
    public Materiales buscarMaterial(){
        String sQuery = Querys.MATERIALES_ALL+" Where "+Querys.MATERIALES_ID_MATERIAL+selected.getIdMaterial()+"'";
        selected = (Materiales) ejbFacade.findByQuery(sQuery, true).result;
        if(selected!=null){
            return selected;
            
        }
        return selected;
    }
    
}