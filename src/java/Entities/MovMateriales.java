/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Kmilo
 */
@Entity
@Table(name = "mov_materiales")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MovMateriales.findAll", query = "SELECT m FROM MovMateriales m"),
    @NamedQuery(name = "MovMateriales.findByIdMovimientoMaterial", query = "SELECT m FROM MovMateriales m WHERE m.idMovimientoMaterial = :idMovimientoMaterial"),
    @NamedQuery(name = "MovMateriales.findByFechaMovimiento", query = "SELECT m FROM MovMateriales m WHERE m.fechaMovimiento = :fechaMovimiento"),
    @NamedQuery(name = "MovMateriales.findByCantida", query = "SELECT m FROM MovMateriales m WHERE m.cantida = :cantida"),
    @NamedQuery(name = "MovMateriales.findByObservacion", query = "SELECT m FROM MovMateriales m WHERE m.observacion = :observacion")})
public class MovMateriales implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "Id_Movimiento_Material")
    private Integer idMovimientoMaterial;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Fecha_Movimiento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaMovimiento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Cantida")
    private int cantida;
    @Size(max = 140)
    @Column(name = "Observacion")
    private String observacion;
    @JoinColumn(name = "Sucursal", referencedColumnName = "Id_Sucursal")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Sucursales sucursal;
    @JoinColumn(name = "Id_Material", referencedColumnName = "Id_Material")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Materiales idMaterial;
    @JoinColumn(name = "Remision", referencedColumnName = "Id_Remision")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Remisiones remision;

    public MovMateriales() {
    }

    public MovMateriales(Integer idMovimientoMaterial) {
        this.idMovimientoMaterial = idMovimientoMaterial;
    }

    public MovMateriales(Integer idMovimientoMaterial, Date fechaMovimiento, int cantida) {
        this.idMovimientoMaterial = idMovimientoMaterial;
        this.fechaMovimiento = fechaMovimiento;
        this.cantida = cantida;
    }

    public Integer getIdMovimientoMaterial() {
        return idMovimientoMaterial;
    }

    public void setIdMovimientoMaterial(Integer idMovimientoMaterial) {
        this.idMovimientoMaterial = idMovimientoMaterial;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public int getCantida() {
        return cantida;
    }

    public void setCantida(int cantida) {
        this.cantida = cantida;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Sucursales getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursales sucursal) {
        this.sucursal = sucursal;
    }

    public Materiales getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Materiales idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Remisiones getRemision() {
        return remision;
    }

    public void setRemision(Remisiones remision) {
        this.remision = remision;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMovimientoMaterial != null ? idMovimientoMaterial.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MovMateriales)) {
            return false;
        }
        MovMateriales other = (MovMateriales) object;
        if ((this.idMovimientoMaterial == null && other.idMovimientoMaterial != null) || (this.idMovimientoMaterial != null && !this.idMovimientoMaterial.equals(other.idMovimientoMaterial))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.MovMateriales[ idMovimientoMaterial=" + idMovimientoMaterial + " ]";
    }
    
}
