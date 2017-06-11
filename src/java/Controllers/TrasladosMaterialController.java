package Controllers;

import Entities.TrasladosMaterial;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Facade.TrasladosMaterialFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("trasladosMaterialController")
@SessionScoped
public class TrasladosMaterialController implements Serializable {

    @EJB
    private Facade.TrasladosMaterialFacade ejbFacade;
    private List<TrasladosMaterial> items = null;
    private TrasladosMaterial selected;

    public TrasladosMaterialController() {
    }

    public TrasladosMaterial getSelected() {
        return selected;
    }

    public void setSelected(TrasladosMaterial selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getTrasladosMaterialPK().setRemisionSalida(selected.getRemisiones1().getIdRemision());
        selected.getTrasladosMaterialPK().setRemisionEntrada(selected.getRemisiones().getIdRemision());
    }

    protected void initializeEmbeddableKey() {
        selected.setTrasladosMaterialPK(new Entities.TrasladosMaterialPK());
    }

    private TrasladosMaterialFacade getFacade() {
        return ejbFacade;
    }

    public TrasladosMaterial prepareCreate() {
        selected = new TrasladosMaterial();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TrasladosMaterialCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TrasladosMaterialUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TrasladosMaterialDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TrasladosMaterial> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public TrasladosMaterial getTrasladosMaterial(Entities.TrasladosMaterialPK id) {
        return getFacade().find(id);
    }

    public List<TrasladosMaterial> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TrasladosMaterial> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TrasladosMaterial.class)
    public static class TrasladosMaterialControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TrasladosMaterialController controller = (TrasladosMaterialController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "trasladosMaterialController");
            return controller.getTrasladosMaterial(getKey(value));
        }

        Entities.TrasladosMaterialPK getKey(String value) {
            Entities.TrasladosMaterialPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new Entities.TrasladosMaterialPK();
            key.setRemisionEntrada(Integer.parseInt(values[0]));
            key.setRemisionSalida(Integer.parseInt(values[1]));
            return key;
        }

        String getStringKey(Entities.TrasladosMaterialPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getRemisionEntrada());
            sb.append(SEPARATOR);
            sb.append(value.getRemisionSalida());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof TrasladosMaterial) {
                TrasladosMaterial o = (TrasladosMaterial) object;
                return getStringKey(o.getTrasladosMaterialPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TrasladosMaterial.class.getName()});
                return null;
            }
        }

    }

}
