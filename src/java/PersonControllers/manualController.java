package PersonControllers;

import Controllers.util.JsfUtil;
import Entities.EmpresaOrigen;
import Entities.Personas;
import Entities.PersonasSucursal;
import PersonControllers.Views.PersonFormEntry;
import Utils.BundleUtils;
import Utils.Constants;
import Utils.Navigation;
import Utils.NotificationControl;
import Utils.Photo;
import Utils.Result;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.event.CaptureEvent;

@Named("manualController")
@SessionScoped
public class manualController extends PersonasController {

    private String otherOriginEnterpriseName;
    private byte[] imagen;

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getOtherOriginEnterpriseName() {
        return otherOriginEnterpriseName;
    }

    public void setOtherOriginEnterpriseName(String otherOriginEnterpriseName) {
        this.otherOriginEnterpriseName = otherOriginEnterpriseName;
    }

    public void valueChangeHandlerOriginEnterprise(ValueChangeEvent changeEvent) {
        PersonFormEntry personFormEntry = JsfUtil.findBean("personFormEntry");
        EmpresaOrigen selectedOriginEnterprise = (EmpresaOrigen) changeEvent.getNewValue();
        if (selectedOriginEnterprise.getIdEmpresaOrigen() != null && selectedOriginEnterprise.getIdEmpresaOrigen() == (Constants.ORIGIN_ENTERPRISE_OTHER)) {
            personFormEntry.setDisableOtherEnterprise(false);
            return;
        }
        personFormEntry.setDisableOtherEnterprise(true);
    }

    /**
     * Method used to search person and redirect to register form, verifying if
     * person is blocked
     *
     * @return page to redirect
     */
    public String manualEntry() {
        //Carga los campos del formulario.
        configFormController.showFieldsPerson();
        Result result = findPersonByDocument();
        if (result.errorCode == Constants.NO_RESULT_EXCEPTION) {
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("Please_Register"));
            selected = new Personas();
            disableNoEditableFields(false);
        } else {
            selected = (Personas) result.result;
            if (verifyBlockedPerson()) {
                return null;
            }
            verifyDatesPerson();
            disableNoEditableFields(true);
            try {
                imagen = Photo.obtenerFotoCarpeta(String.valueOf(selected.getIdPersona()), Constants.OBJECT_PERSON);
            } catch (IOException ex) {
                System.out.println("Manual Entry: Error al obtener la foto de la personas con id" + selected.getIdPersona());
                System.out.println("Exception Manual Entry" + ex);
            }
        }
        return Navigation.PAGE_PERSON_REGISTER;
    }

    public String manualExit() {

        Result result = findPersonByDocument();
        if (result.errorCode == Constants.NO_RESULT_EXCEPTION) {
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("No_Entry_Register"));
            return Navigation.PAGE_COMPLETE_EXIT;
        }
        selected = (Personas) result.result;
        if (verifyBlockedPerson()) {
            return null;
        }
        personasSucursalCliController.loadSpecificPersonByEntry(selected.getIdPersona());
        if (personasSucursalCliController.verifyBlockSpecificPerson()) {
            return null;
        }
        try {
                imagen = Photo.obtenerFotoCarpeta(String.valueOf(selected.getIdPersona()), Constants.OBJECT_PERSON);
            } catch (IOException ex) {
                System.out.println("Manual Exit: Error al obtener la foto de la personas con id" + selected.getIdPersona());
                System.out.println("Exception Manual Entry" + ex);
            }
        return Navigation.PAGE_PERSON_EXIT;
    }

    public String save(boolean express) {
        boolean existPerson = selected.getIdPersona() != null;
        if (existPerson) {
            //Verifica existencia de registro en la sucursal determinada por el formulario.
            Result result = personasSucursalCliController.findSpecificPerson(selected.getIdPersona());
            if (result.errorCode == Constants.NO_RESULT_EXCEPTION) {
                personasSucursalCliController.create();
                movPersonasCliController.recordEntryMovement(Constants.CREATE);
            } else {
                PersonasSucursal specificPerson = (PersonasSucursal) result.result;
                specificPerson.setArea(personasSucursalCliController.getSelected().getArea());
                personasSucursalCliController.setSelected(specificPerson);
                if (personasSucursalCliController.verifyBlockSpecificPerson()) {
                    return null;
                }
                personasSucursalCliController.update();
                movPersonasCliController.recordEntryMovement(Constants.UPDATE);
            }
            //Actualiza la personaCli
            update();
        } else {
            if (findPersonByDocument().errorCode != Constants.NO_RESULT_EXCEPTION) {//This person exist, so maybe was an error in identification number
                //We doesn't assign encoutered person. This way data is not lost
                JsfUtil.addErrorMessage("ESTA PERSONA YA ESTA REGISTRADA");//TODO CREATE BUNDLE PROPERTY
                return Navigation.PAGE_PERSON_REGISTER;
            }
            create();
            personasSucursalCliController.create();
            movPersonasCliController.recordEntryMovement(Constants.CREATE);
            //TODO METODO COMUN PARA LOS DOS
        }
        //NOTIFICATION//////////////////////////////////////////////////////////
        
        NotificationControl notificationControl = JsfUtil.findBean("notificationControl");
        notificationControl.notifyEvent(personasSucursalCliController.getSelected(), Constants.STRING_ENTRY, Photo.cargarFoto("DireccionPersona", String.valueOf(selected.getIdPersona())));
        JsfUtil.addSuccessMessage("Ingreso exitoso");
        movPersonasCliController.findLastMovements();
        ////////////////////////////////////////////////////////////////////////
        if (express) {
            personasSucursalCliController.setSelected(null);
            return Navigation.PAGE_EXPRESS_ENTRY;
        }
        //Almacenar Foto////////////////////////////////////////////////////////
        Photo.almacenarFoto(Constants.OBJECT_PERSON, imagen, selected.getIdPersona());
        return Navigation.PAGE_SELECT_ENTRY;
    }

    public void oncapture(CaptureEvent captureEvent) {
        imagen = captureEvent.getData();
    }

    /**
     * Muestra la imagen del modelo ya sea tomada por la webcam o la que tiene
     * en la base de datos
     *
     * @return
     */

    public String exit() {
        movPersonasCliController.recordExitMovement();
        JsfUtil.addSuccessMessage("Salida exitosa");
        movPersonasCliController.findLastMovements();
        return Navigation.PAGE_INDEX;
    }

}
