/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Controllers.util.JsfUtil;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author a.morales
 */

@Named("helpUtils")
@SessionScoped
public class HelpUtils implements Serializable{
    
    private String Nombre;
    private String correoContacto;
    private String Descripcion;
    private String ciudad;

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getCorreoContacto() {
        return correoContacto;
    }

    public void setCorreoContacto(String correoContacto) {
        this.correoContacto = correoContacto;
    }
    
    public void showModalHelpConfiguration(){
        JsfUtil.showModal("dlgHelpTheme");
    }
    
    public void showModalHelpContact(){
        JsfUtil.showModal("dlgHelpContact");
    }
    
    public void showModalHelpVerMovimientos(){
        JsfUtil.showModal("dlgHelpMov");
    }
    
    public void enviarCorreoContacto(){
        Email.crearEmail(Nombre, correoContacto, Descripcion,ciudad);
        limpiar();
        JsfUtil.addSuccessMessage("Correo electornico enviado al servicio tecnico");
        JsfUtil.exitModal("diagContact");
    }

    public void limpiar() {
        ciudad = null;
        Nombre=null;
        Descripcion=null;
        correoContacto=null;
    }
    
}
