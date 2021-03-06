/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Facade;

import Facade.AbstractFacade;
import Entities.PersonasSucursal;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Kmilo
 */
@Stateless
public class PersonasSucursalFacade extends AbstractFacade<PersonasSucursal> {

    @PersistenceContext(unitName = "AlmacenV3PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PersonasSucursalFacade() {
        super(PersonasSucursal.class);
    }
    
}
