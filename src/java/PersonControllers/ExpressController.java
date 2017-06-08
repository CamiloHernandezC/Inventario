/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PersonControllers;

import Controllers.util.JsfUtil;
import Entities.Personas;
import Entities.PersonasSucursal;
import Entities.TiposDocumento;
import Utils.BundleUtils;
import Utils.Constants;
import Utils.Navigation;
import Utils.Photo;
import Utils.Result;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author MAURICIO
 */
@Named(value = "expressController")
@SessionScoped
public class ExpressController extends PersonasController {

    //private persona menu;
    private String code;//Store code reader value
    private manualController manualController = JsfUtil.findBean("manualController");

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void entryByCodeReader(boolean express) {
        if (code == null) {
            return;
        }
        Result result = findByCodeReader();
        //Verificacion del Formato aceptado
        if (result.errorCode == Constants.UNKNOWN_EXCEPTION) {//unaccepted text format
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("UNACCEPTED_FORMAT"));
            if (express) {
                JsfUtil.redirectTo(Navigation.PAGE_EXPRESS_ENTRY);
            } else {
                JsfUtil.redirectTo(Navigation.PAGE_COMPLETE_ENTRY);
            }
            clean();
        } else {
            configFormController.showFieldsPerson();
            //code = null;
            if (result.errorCode == Constants.NO_RESULT_EXCEPTION) {
                disableNoEditableFields(false);
                if (express) {
                    JsfUtil.showModal("expressDialog");
                } else {
                    JsfUtil.redirectTo(Navigation.PAGE_PERSON_REGISTER);
                }
                clean();
                return;
            }
            if (code.startsWith("C,")) {//Cedula
                selected = (Personas) result.result;
                manualController.setSelected(selected);
            }
            if (code.startsWith("B,")) {//Codigo de Barras
                PersonasSucursal personasSucursalCli = (PersonasSucursal) result.result;
                selected = personasSucursalCli.getPersonas();
                manualController.setSelected(selected);
                personasSucursalCliController.setSelected(personasSucursalCli);
                if (personasSucursalCliController.verifyBlockSpecificPerson()) {
                    clean();
                    return;
                }
            }
            if (verifyBlockedPerson()) {
                clean();
                return;
            }
            boolean fechas = verifyDatesPerson();
            if (express) {
                if (code.startsWith("B")) {
                    if (!fechas) {
                        JsfUtil.showModal("fechasDialog");
                        clean();
                        return;
                    }
                    movPersonasCliController.recordEntryMovement(Constants.UPDATE);
                    JsfUtil.redirectTo(Navigation.PAGE_EXPRESS_ENTRY);
                }
                if (code.startsWith("C")) {
                    JsfUtil.showModal("sucursalDialog");
                }
            } else {
                disableNoEditableFields(true);
                try {
                    manualController.setImagen(Photo.obtenerFotoCarpeta(String.valueOf(selected.getIdPersona()), Constants.OBJECT_PERSON));
                } catch (IOException ex) {
                    System.out.println("Manual Entry: Error al obtener la foto de la personas con id" + selected.getIdPersona());
                    System.out.println("Exception Manual Entry" + ex);
                }
                JsfUtil.redirectTo(Navigation.PAGE_PERSON_REGISTER);
            }
        }
        clean();
    }

    public void exitByCodeReader(boolean express) {
        if (code == null) {
            return;
        }
        Result result = findByCodeReader();
        if (result.errorCode == Constants.UNKNOWN_EXCEPTION) {//unaccepted text format
            if (express) {
                JsfUtil.redirectTo(Navigation.PAGE_EXPRESS_EXIT);
            } else {
                JsfUtil.redirectTo(Navigation.PAGE_PERSON_EXIT);
            }
            clean();
        } else {
            //configFormCliController.showFieldsPerson();
            if (code.startsWith("C,")) {//Cedula
                selected = (Personas) result.result;//Assign for verirfy block
                manualController.setSelected(selected);//Assign to model shown in form
                personasSucursalCliController.loadSpecificPersonByEntry(selected.getIdPersona());
            }
            if (code.startsWith("B,")) {//Codigo de Barras
                PersonasSucursal personasSucursal = (PersonasSucursal) result.result;
                selected = personasSucursal.getPersonas();//Assign for verirfy block
                manualController.setSelected(selected);//Assign to model shown in form
                personasSucursalCliController.setSelected(personasSucursal);
            }
            if (personasSucursalCliController.verifyBlockSpecificPerson()) {
                clean();
                return;
            }
            if (verifyBlockedPerson()) {
                clean();
                return;
            }
            JsfUtil.addSuccessMessage("Salida exitosa");
            if (express) {
                movPersonasCliController.recordExitMovement();
                movPersonasCliController.findLastMovements();
                JsfUtil.redirectTo(Navigation.PAGE_EXPRESS_EXIT);
            } else {
                try {
                    manualController.setImagen(Photo.obtenerFotoCarpeta(String.valueOf(selected.getIdPersona()), Constants.OBJECT_PERSON));
                } catch (IOException ex) {
                    System.out.println("Manual Entry: Error al obtener la foto de la personas con id" + selected.getIdPersona());
                    System.out.println("Exception Manual Entry" + ex);
                }
                JsfUtil.redirectTo(Navigation.PAGE_PERSON_EXIT);
            }
        }
        clean();
    }

    private Result findByCodeReader() {
        if (code.startsWith("C,")) {//ID CARD (CEDULA)
            String[] separatedWords = separateWords();
            if (separatedWords != null) {
                //Se debe asignar el tipo de documento y número de documento para poder buscar
                manualController.getSelected().setTipoDocumento(new TiposDocumento(Constants.DOCUMENT_TYPE_CEDULA));//Se asigna el tipo de documento como cedula
                manualController.getSelected().setNumeroDocumento(separatedWords[0]);//Se le asigna el numero de cedula que fue leido por el lector de cedulas
                //<editor-fold desc="Assign selected to info in id card" defaultstate="collapsed">
                manualController.getSelected().setApellido1(separatedWords[1]);
                manualController.getSelected().setApellido2(separatedWords[2]);
                manualController.getSelected().setNombre1(separatedWords[3]);
                manualController.getSelected().setNombre2(separatedWords[4]);
                manualController.getSelected().setSexo(separatedWords[5].equals("M"));
                DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                try {
                    Date birthDate = formatter.parse(separatedWords[6]);
                    manualController.getSelected().setFechaNacimiento(birthDate);
                } catch (ParseException ex) {
                    System.out.println(Constants.MESSAGE_DATE_FORMAT_EXCEPTION);
                }
                String RH = "¡".equals(separatedWords[7].substring(1)) ? "+" : "-";
                manualController.getSelected().setRh(separatedWords[7].substring(0, 1) + RH);
                //</editor-fold>
                return manualController.findPersonByDocument();
            }
        }
        if (code.startsWith("B,")) {//BAR CODE
            return findPersonByIdExterno(code.substring(2));
        }
        return new Result(null, Constants.UNKNOWN_EXCEPTION);//Cuando el formato no coincide con los soportados
    }

    public String[] separateWords() {
        int commaCounter = 0;
        String[] separatedWords = new String[10];
        int oldi = 1;
        for (int i = 2; i < code.length(); i++) {//Start in 2 to avoid "C,"
            char c = code.charAt(i);
            if (c == ',') {
                if (oldi + 1 != i) {
                    separatedWords[commaCounter] = code.substring(oldi + 1, i);
                } else {
                    separatedWords[commaCounter] = "";
                }
                commaCounter++;
                oldi = i;
            }
        }
        if (commaCounter == 9) {
            separatedWords[0] = String.valueOf(Integer.parseInt(separatedWords[0]));//Las cedulas las completa con 0 a la izquierda, esta linea de codigo quita los 0
            return separatedWords;
        }
        return null;
    }

    public Result findPersonByIdExterno(String code) {
        return personasSucursalCliController.findPersonByIdExterno(code);
    }

    public String redirecToPersonFormEntry() {
        configFormController.showFieldsPerson();
        return Navigation.PAGE_PERSON_REGISTER;
    }

    @Override
    public void clean() {
        code = null;
    }

}
