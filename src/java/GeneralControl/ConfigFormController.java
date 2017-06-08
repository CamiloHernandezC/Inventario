package GeneralControl;

import Controllers.PorteriasSucursalController;
import Controllers.SucursalesController;
import Entities.ConfigForm;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.PorteriasSucursal;
import Entities.Sucursales;
import Facade.ConfigFormFacade;
import PersonControllers.PersonasSucursalController;
import Utils.Constants;
import Utils.Result;

import java.io.Serializable;
import java.util.ArrayList;
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

@Named("configFormController")
@SessionScoped
public class ConfigFormController implements Serializable {

    @EJB
    private Facade.ConfigFormFacade ejbFacade;
    private List<ConfigForm> items = null;
    private ConfigForm selected;

    //Booleanos de control Vista
    // <editor-fold desc="Booleanos" defaultstate="collapsed">
    private boolean mostrarARL;
    private boolean mostrarCelular;
    private boolean mostrarEmail;
    private boolean mostrarDireccion;
    private boolean mostrarEPS;
    private boolean mostrarnacimiento;
    private boolean mostrarFecha_vigencia_ARL;
    private boolean mostrarFecha_vigencia_EPS;
    private boolean mostrarDepartamento;
    private boolean mostrarIdExterno;
    private boolean mostrarPais;
    private boolean mostrarMunicipio;
    private boolean mostrarSucursal;
    private boolean mostrarSexo;
    private boolean mostrarTelefono;
    private boolean mostrarTipo_sanguineo;
    private boolean mostrarFuncionario;
    private boolean mostrarEmpresa;
    private boolean mostrarPersonaContacto;

    public boolean isMostrarPersonaContacto() {
        return mostrarPersonaContacto;
    }

    public ConfigFormFacade getEjbFacade() {
        return ejbFacade;
    }

    public boolean isMostrarARL() {
        return mostrarARL;
    }

    public boolean isMostrarCelular() {
        return mostrarCelular;
    }

    public boolean isMostrarEmail() {
        return mostrarEmail;
    }

    public boolean isMostrarDireccion() {
        return mostrarDireccion;
    }

    public boolean isMostrarEPS() {
        return mostrarEPS;
    }

    public boolean isMostrarnacimiento() {
        return mostrarnacimiento;
    }

    public boolean isMostrarFecha_vigencia_ARL() {
        return mostrarFecha_vigencia_ARL;
    }

    public boolean isMostrarFecha_vigencia_EPS() {
        return mostrarFecha_vigencia_EPS;
    }

    public boolean isMostrarDepartamento() {
        return mostrarDepartamento;
    }

    public boolean isMostrarIdExterno() {
        return mostrarIdExterno;
    }

    public boolean isMostrarPais() {
        return mostrarPais;
    }

    public boolean isMostrarMunicipio() {
        return mostrarMunicipio;
    }

    public boolean isMostrarSucursal() {
        PorteriasSucursalController porteriaSucursalController = JsfUtil.findBean("porteriasSucursalController");
        PersonasSucursalController personasSucursalController = JsfUtil.findBean("personasSucursalController");
        Result result = porteriaSucursalController.findBranchOfficeByEntry("1");
        if (result.errorCode == Constants.NO_RESULT_EXCEPTION) {
            JsfUtil.addErrorMessage("No se ha cargado ninguna Sucursal, Por favor contacte al servicio tecnico.");
            return mostrarSucursal = true;
        }
        List<PorteriasSucursal> array = (List<PorteriasSucursal>) result.result;
        if (array.size() == 1) {
            personasSucursalController.getSelected().setSucursales(array.get(0).getSucursales());
            return mostrarSucursal = false;
        }
        SucursalesController sucursalesController = JsfUtil.findBean("sucursalesController");
        ArrayList<Sucursales> arraySucursales = new ArrayList<>();
        array.forEach((array1) -> {
            arraySucursales.add(array1.getSucursales());
        });
        sucursalesController.setItems(arraySucursales);
        return mostrarSucursal = true;
    }

    public boolean isMostrarSexo() {
        return mostrarSexo;
    }

    public boolean isMostrarTelefono() {
        return mostrarTelefono;
    }

    public boolean isMostrarTipo_sanguineo() {
        return mostrarTipo_sanguineo;
    }

    public boolean isMostrarFuncionario() {
        return mostrarFuncionario;
    }

    public boolean isMostrarEmpresa() {
        return mostrarEmpresa;
    }

    //</editor-fold>
    public ConfigFormController() {
    }

    public ConfigForm getSelected() {
        return selected;
    }

    public void setSelected(ConfigForm selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ConfigFormFacade getFacade() {
        return ejbFacade;
    }

    public ConfigForm prepareCreate() {
        selected = new ConfigForm();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ConfigFormCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ConfigFormUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ConfigFormDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ConfigForm> getItems() {
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

    public ConfigForm getConfigForm(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<ConfigForm> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ConfigForm> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public void showFieldsPerson() {
        List<ConfigForm> array;
        String squery = "SELECT c FROM ConfigForm c WHERE c.formulario ='" + Constants.CONFIGPERSONSFORM + "'";
        array = (List<ConfigForm>) ejbFacade.findByQueryArray(squery).result;
        for (int i = 0; i < array.size(); i++) {
            ConfigForm modelo = array.get(i);
            String tmp = modelo.getCampo();
            tmp = JsfUtil.quitaEspacios(tmp);
            switch (tmp) {

                case "Arl":
                    mostrarARL = modelo.getMostrar();
                    break;
                case "Celular":
                    mostrarCelular = modelo.getMostrar();
                    break;
                case "Email":
                    mostrarEmail = modelo.getMostrar();
                    break;
                case "Direccion":
                    mostrarDireccion = modelo.getMostrar();
                    break;
                case "Eps":
                    mostrarEPS = modelo.getMostrar();
                    break;
                case "Fechanacimiento":
                    mostrarnacimiento = modelo.getMostrar();
                    break;
                case "Fechaarl":
                    mostrarFecha_vigencia_ARL = modelo.getMostrar();
                    break;
                case "Fechaeps":
                    mostrarFecha_vigencia_EPS = modelo.getMostrar();
                    break;
                case "Departamento":
                    mostrarDepartamento = modelo.getMostrar();
                    break;
                case "Id_externo":
                    mostrarIdExterno = modelo.getMostrar();
                    //limpiarIdExterno = mostrarIdExterno;
                    break;
                case "Municipio":
                    mostrarMunicipio = modelo.getMostrar();
                    break;
                case "Pais":
                    mostrarPais = modelo.getMostrar();
                    break;
                case "Sucursal":
                    mostrarSucursal = modelo.getMostrar();
                    break;
                case "Sexo":
                    mostrarSexo = modelo.getMostrar();
                    break;
                case "Telefono":
                    mostrarTelefono = modelo.getMostrar();
                    break;
                case "Rh":
                    mostrarTipo_sanguineo = modelo.getMostrar();
                    break;
                case "Funcionario":
                    mostrarFuncionario = modelo.getMostrar();
                    break;
                case "Empresa":
                    mostrarEmpresa = modelo.getMostrar();
                    break;
                case "Personacontacto":
                    mostrarPersonaContacto = modelo.getMostrar();
                    break;

            }
        }
    }

    @FacesConverter(forClass = ConfigForm.class)
    public static class ConfigFormControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConfigFormController controller = (ConfigFormController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "configFormController");
            return controller.getConfigForm(getKey(value));
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
            if (object instanceof ConfigForm) {
                ConfigForm o = (ConfigForm) object;
                return getStringKey(o.getIdConfigForm());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ConfigForm.class.getName()});
                return null;
            }
        }
    }
    
}
