/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mc.inventorySystem.core.model;

/**
 *
 * @author practicante02
 */
public class Proveedor {
    private int idProveedor;
    private String empresa;
    private String contacto;
    private String direccion;
    private int estatus;

    public Proveedor(int idProveedor, String nombreEmpresa, String contacto, String direccion, int estatus) {
        this.idProveedor = idProveedor;
        this.empresa = nombreEmpresa;
        this.contacto = contacto;
        this.direccion = direccion;
        this.estatus = estatus;
    }

    public Proveedor() {
    }
    
    

    /**
     * @return the idProveedor
     */
    public int getIdProveedor() {
        return idProveedor;
    }

    /**
     * @param idProveedor the idProveedor to set
     */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * @return the empresa
     */
    public String getEmpresa() {
        return empresa;
    }

    /**
     * @param empresa the empresa to set
     */
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    /**
     * @return the contacto
     */
    public String getContacto() {
        return contacto;
    }

    /**
     * @param contacto the contacto to set
     */
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the estatus
     */
    public int getEstatus() {
        return estatus;
    }

    /**
     * @param estatus the estatus to set
     */
    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    
    
    @Override
    public String toString() {
        return idProveedor + ".- "+ empresa;
    }
    
    
    
    
}
