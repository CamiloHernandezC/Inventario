/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Facade;

import Entities.MovVehiculos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author a.morales
 */
@Stateless
public class MovVehiculosFacade extends AbstractFacade<MovVehiculos> {

    @PersistenceContext(unitName = "PorteriaV3PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MovVehiculosFacade() {
        super(MovVehiculos.class);
    }
    
}
