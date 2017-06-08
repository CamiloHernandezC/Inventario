package PersonControllers;

import Controllers.AbstractPersistenceController;
import GeneralControl.ConfigFormController;
import Controllers.PorteriasSucursalController;
import Controllers.util.JsfUtil;
import Entities.Entidades;
import Entities.Estados;
import Entities.Personas;
import Entities.PersonasSucursal;
import Entities.PorteriasSucursal;
import Entities.Sucursales;
import Facade.PersonasSucursalFacade;
import Querys.Querys;
import Utils.Constants;
import Utils.Result;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("personasSucursalController")
@SessionScoped
public class PersonasSucursalController extends AbstractPersistenceController<PersonasSucursal> {

    @EJB
    private Facade.PersonasSucursalFacade ejbFacade;
    private List<PersonasSucursal> items = null;
    private PersonasSucursal selected;
    private MovPersonasController movPersonasController = JsfUtil.findBean("movPersonasController");

    public PersonasSucursalController() {
    }

    @Override
    public PersonasSucursal getSelected() {
        if(selected==null){
            selected = new PersonasSucursal();
        }
        return selected;
    }

    @Override
    public void setSelected(PersonasSucursal selected) {
        this.selected = selected;
    }

    @Override
    protected void setEmbeddableKeys() {
        selected.getPersonasSucursalPK().setSucursal(selected.getSucursales().getIdSucursal());
        selected.getPersonasSucursalPK().setIdPersona(selected.getPersonas().getIdPersona());
    }

    @Override
    protected void initializeEmbeddableKey() {
        selected.setPersonasSucursalPK(new Entities.PersonasSucursalPK());
    }

    @Override
    protected PersonasSucursalFacade getFacade() {
        return ejbFacade;
    }

    @Override
    public void prepareCreate() {
        initializeEmbeddableKey();
        assignPrimaryKey();
        selected.setEntidad(new Entidades(Constants.ENTITY_VISITANT));
        selected.setEstado(new Estados(Constants.STATUS_ENTRY));
        prepareUpdate();
    }
    
    @Override
    protected void prepareUpdate() {
        assignParametersToUpdate();
    }
    
    @Override
    protected void setItems(List<PersonasSucursal> items) {
        this.items = items;
    }

    public List<PersonasSucursal> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public PersonasSucursal getPersonasSucursal(Entities.PersonasSucursalPK id) {
        return getFacade().find(id);
    }

    public List<PersonasSucursal> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<PersonasSucursal> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
    
    Result findSpecificPerson(int idPersona) {
        return findSpecificPerson(idPersona, selected.getSucursales().getIdSucursal());
    }
    
    Result findSpecificPerson(int idPersona, int idSucursal) {
        String squery = Querys.PERSONAS_SUCURSAL_CLI_ALL + "WHERE" + Querys.PERSONAS_SUCURSAL_CLI_PERSONA+idPersona+
                "' AND"+Querys.PERSONAS_SUCURSAL_CLI_SUCURSAL+idSucursal+
                "' AND" + Querys.PERSONAS_SUCURSAL_CLI_NO_ESTADO + "4'";//TODO ASSIGN REAL STATUS HERE
        return ejbFacade.findByQuery(squery, false);//False because only one person should appear*/
    }

    private void assignPrimaryKey() {
        manualController manualController = JsfUtil.findBean("manualController");
        selected.setPersonas(manualController.getSelected());
        //branch office is already assigned
    }

    public Result findPersonByIdExterno(String code) {
        String squery = Querys.PERSONAS_SUCURSAL_CLI_ALL+"WHERE"+Querys.PERSONAS_SUCURSAL_ID_EXTERNO+code+"'";
        return ejbFacade.findByQuery(squery, false);//ID EXTERNO MUST BE UNIQUE FOR NOW
    }

    //Aun no sirve para Express por que la sucursal viene nula.
    public boolean verifyBlockSpecificPerson() {
       
        if(Objects.equals(selected.getEstado().getIdEstado(), Constants.STATUS_BLOCKED)){
            JsfUtil.showModal("blockedDialog");
            return true;
        }
        return false;
    }
    
    public void loadSpecificPersonByEntry(int idPersona) {
        movPersonasController.lastEntry(idPersona);
        int branchOffice = movPersonasController.getSelected().getPersonasSucursal().getPersonasSucursalPK().getSucursal();
        selected = (PersonasSucursal) findSpecificPerson(idPersona, branchOffice).result;
    }

    @FacesConverter(forClass = PersonasSucursal.class)
    public static class PersonasSucursalControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonasSucursalController controller = (PersonasSucursalController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personasSucursalController");
            return controller.getPersonasSucursal(getKey(value));
        }

        Entities.PersonasSucursalPK getKey(String value) {
            Entities.PersonasSucursalPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new Entities.PersonasSucursalPK();
            key.setIdPersona(Integer.parseInt(values[0]));
            key.setSucursal(Integer.parseInt(values[1]));
            return key;
        }

        String getStringKey(Entities.PersonasSucursalPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getIdPersona());
            sb.append(SEPARATOR);
            sb.append(value.getSucursal());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof PersonasSucursal) {
                PersonasSucursal o = (PersonasSucursal) object;
                return getStringKey(o.getPersonasSucursalPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), PersonasSucursal.class.getName()});
                return null;
            }
        }

    }
    
    public void clean(){
        selected = null;
        items = null;
    }

}
