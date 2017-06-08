/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Entities.EmpresaOrigen;
import Entities.Notificaciones;
import Entities.PersonasSucursal;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;


/**
 *
 * @author a.morales
 */
@Named("notificationControl")
@SessionScoped
public class NotificationControl implements Serializable{
    @EJB
    protected Facade.NotificacionesFacade ejbFacade;
    private Notificaciones selected;
     
    
    public void notifyEvent(Object object,String tipoEvento,String rutaArchivo){
        
        String squery = "";
        if(object instanceof PersonasSucursal){
            PersonasSucursal persona = (PersonasSucursal) object;
            squery = createQuery(persona.getPersonas().getEmpresaOrigen(),persona.getEntidad().getIdEntidad(),persona.getPersonas().getEstado().getIdEstado(),persona.getPersonas().getIdPersona(),0,null,Constants.CATEGORY_PERSON,persona.getSucursales().getIdSucursal(),tipoEvento); 
        }
        Result result = ejbFacade.findByQueryArray(squery);
        if(result.errorCode != Constants.OK ){
            return;
        }
        List<Notificaciones> items = (List<Notificaciones>) result.result;
        for (Notificaciones notification : items) {
            Email.crearEmail(notification, rutaArchivo, object, tipoEvento);
        }
    }
    

    private String createQuery(EmpresaOrigen pIdEmOrigen, int pIdEntidad, int pIdEstado, int pIdPersona,int pIdobjeto, String pPlacaVehiculo, int pCategoria, int pIdSucursal, String pTipoEvento) {
        
        Date actualDate = new Date();
        java.sql.Date jpqlDate = new java.sql.Date(actualDate.getTime());
        java.sql.Time jpqlTime = new java.sql.Time(actualDate.getTime());
        
        int pIdPorteria = 1;//TODO - Obtener la porteria Real

        String sIdPorteria = "n.porteria is NULL or n.porteria.idPorteria ='" + pIdPorteria + "'";

        String sIdSucursal = "n.sucursal is NULL or n.sucursal.idSucursal ='" + pIdSucursal + "'";

        String sIdEmOrigen = "n.empresaOrigen is NULL or n.empresaOrigen ='" + pIdEmOrigen + "'";

        String sIdCategoria = "n.categoria is NULL or n.categoria.idCategoria = '" + pCategoria + "'";

        String sIdEntidad = "n.entidad is NULL or n.entidad.idEntidad = '" + pIdEntidad + "'";

        String sIdEstado = "n.estado is NULL or n.estado.idEstado = '" + pIdEstado + "'";
        
        String sTipoEvento = "n.tipoEvento is NULL or n.tipoEvento = '" + pTipoEvento + "'";
        
        String sIdPersona = "n.persona is NULL or n.persona.idPersona = '" + pIdPersona + "'";
        
        String sIdObjeto = "n.objeto is NULL or n.objeto.idObjeto = '"+ pIdobjeto + "'";

        String sPlacaVehiculo = "n.vehiculo is NULL or n.vehiculo.Placa = '"+ pPlacaVehiculo + "'";

        /*Construcción del query*/
        String sQuery = "SELECT n FROM Notificaciones n WHERE (" + sIdPorteria + ") and (" + sIdSucursal
                + ") and (" + sIdCategoria + ") and (" + sIdEntidad + ") and (" + sIdEstado + ") and ("+sTipoEvento
                +") AND n.fechaDesde <= '"+jpqlDate+"' AND n.fechaHasta >= '"+jpqlDate+"' AND n.horaDesde <= '"+jpqlTime+"' AND n.horaHasta >= '"+jpqlTime+"'";

        if(pIdEmOrigen!=null){
            sQuery+=" and (" + sIdEmOrigen + ")";
        }
        if(pIdobjeto != 0){
            sQuery+=" and ("+ sIdObjeto + ")";
        }
        if(pIdPersona !=0){
            sQuery+=" and (" + sIdPersona + ")";
        }
        if(pPlacaVehiculo != null){
            sQuery+=" and ("+ sPlacaVehiculo +")";
        }

        return sQuery;
    }
    
}
