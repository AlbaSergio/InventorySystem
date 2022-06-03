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
public class Categoria {

    private int idCategoria;
    private String nombreCategoria;
    private String descripcion;
    private int estatus;

    public Categoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Categoria(int idCategoria, String nombre, int estatus) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombre;
        this.estatus = estatus;
    }

    public Categoria() {
    }

    public Categoria(String string) {
        
    }

   

    /**
     * @return the idCategoria
     */
    public int getIdCategoria() {
        return idCategoria;
    }

    /**
     * @param idCategoria the idCategoria to set
     */
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    /**
     * @return the nombreCategoria
     */
    public String getNombreCategoria() {
        return nombreCategoria;
    }

    /**
     * @param nombreCategoria the nombreCategoria to set
     */
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
        return idCategoria + ".-  "+ nombreCategoria;
    }
    
    
}
