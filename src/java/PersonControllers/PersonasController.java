package PersonControllers;

import Controllers.AbstractPersistenceController;
import GeneralControl.ConfigFormController;
import Controllers.util.JsfUtil;
import Entities.Estados;
import Entities.Personas;
import Facade.PersonasFacade;
import PersonControllers.Views.PersonFormEntry;
import Querys.Querys;
import Utils.Constants;
import Utils.Result;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.context.RequestContext;

public class PersonasController extends AbstractPersistenceController<Personas>{

    @EJB
    protected Facade.PersonasFacade ejbFacade;
    protected List<Personas> items = null;
    protected Personas selected;
    protected ConfigFormController configFormController = JsfUtil.findBean("configFormController");
    protected PersonasSucursalController personasSucursalCliController = JsfUtil.findBean("personasSucursalController");
    protected MovPersonasController movPersonasCliController = JsfUtil.findBean("movPersonasController");
    public PersonasController() {
    }

    //<editor-fold desc="INHERITED METHODS" defaultstate="collapsed">
    @Override
    public Personas getSelected() {
        if (selected == null) {
            selected = new Personas();
        }
        return selected;
    }

    @Override
    public void setSelected(Personas selected) {
        this.selected = selected;
    }

    @Override
    protected PersonasFacade getFacade() {
        return ejbFacade;
    }

    @Override
    protected void setItems(List<Personas> items) {
        this.items = items;
    }

    @Override
    protected void setEmbeddableKeys() {
        //Nothing to do here
    }

    @Override
    protected void initializeEmbeddableKey() {
        //Nothing to do here
    }
    
    /**
     * 
     */
    @Override
    public void prepareCreate() {
        calculatePrimaryKey(Querys.PERSONA_CLI_PRIMARY_KEY);
        selected.setEstado(new Estados(Constants.STATUS_ENTRY));
        prepareUpdate();
    }
    
    @Override
    protected void prepareUpdate() {
        assignParametersToUpdate();
    }
    //</editor-fold>

    /**
     * Verifica las fechas de seguridad social ingresadas en el sistema.
     * @return True: Fechas son validas, False: Fechas vencidas
     */
    protected boolean verifyDatesPerson() {
        //return true;
        boolean fechaSS = true;
        //Determina si se esta mostrando el campo seleccionado.
        if (configFormController.isMostrarFecha_vigencia_ARL() == false || configFormController.isMostrarFecha_vigencia_EPS() == false) {
            return true;
        }
        //Determina si viene nulo dicho parametro.
        if (selected.getFechaVigenciaSS() == null) {
            JsfUtil.addErrorMessage("La persona no tiene asignada fecha de vigencia de Seguridad Social");
            return false;
        }
        if (selected.getFechaVigenciaSS().before(new Date())) {
            fechaSS = false;
            selected.setFechaVigenciaSS(null);
            JsfUtil.addErrorMessage("Fecha de vigencia de la seguridad social esta vencida");
        }
        return fechaSS;
    }

    public Result findPersonByDocument() {
        String squery = Querys.PERSONA_CLI_ALL + "WHERE" + Querys.PERSONA_CLI_DOC_TYPE + selected.getTipoDocumento().getTipoDocumento() + "' AND"
                + Querys.PERSONA_CLI_DOC_NUMBER + selected.getNumeroDocumento() + "'";
        return ejbFacade.findByQuery(squery, false);//Only one person should have document type, an document number (It is unique in database)
    }

    protected void disableNoEditableFields(boolean enable) {
        PersonFormEntry personFormEntry = JsfUtil.findBean("personFormEntry");
        personFormEntry.setDisableNoEditableField(enable);
    }

    /**
     * If person is blocked (registered in blocked table) will show a dialog
     *
     * @return true if the person is blocked, false otherwise
     */
    protected boolean verifyBlockedPerson() {

        if (Objects.equals(selected.getEstado().getIdEstado(), Constants.STATUS_BLOCKED)) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('blockedDialog').show();");
            return true;
        }
        return false;
    }

    public void cancel() {
        selected = new Personas();
        //TODO CLEAN BRANCH AND AREA, MOV, EVERITHING
        JsfUtil.cancel();
    }
    
    public void clean(){
        selected = null;
        items = null;
    }
    
    // <editor-fold desc="CONVERTER" defaultstate="collapsed">
    
    public Personas getPersonas(java.lang.Integer id) {
        return getFacade().find(id);
    }
    
    @FacesConverter(forClass = Personas.class)
    public static class PersonasControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonasController controller = (PersonasController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personasController");
            return controller.getPersonas(getKey(value));
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
            if (object instanceof Personas) {
                Personas o = (Personas) object;
                return getStringKey(o.getIdPersona());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Personas.class.getName()});
                return null;
            }
        }

    }
    //</editor-fold>
}
