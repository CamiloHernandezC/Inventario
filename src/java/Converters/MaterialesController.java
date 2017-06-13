package Converters;


import Entities.Materiales;
import Facade.MaterialesFacade;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/*@Named("materialesController")
@SessionScoped*/
public class MaterialesController implements Serializable {

    @EJB
    protected Facade.MaterialesFacade ejbFacade;
    protected List<Materiales> items = null;
    protected Materiales selected;
    
    public MaterialesController() {
    }

    public Materiales getSelected() {
        if(selected==null){
            selected = new Materiales();
        }
        return selected;
    }

    public void setSelected(Materiales selected) {
        this.selected = selected;
    }

    private MaterialesFacade getFacade() {
        return ejbFacade;
    }

    public List<Materiales> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
    
    public Materiales getMateriales(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Materiales.class)
    public static class MaterialesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MaterialesController controller = (MaterialesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "materialesController");
            return controller.getMateriales(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Materiales) {
                Materiales o = (Materiales) object;
                return getStringKey(o.getIdMaterial());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Materiales.class.getName()});
                return null;
            }
        }

    }

}
