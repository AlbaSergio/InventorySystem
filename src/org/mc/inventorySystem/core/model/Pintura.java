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
public class Pintura {
    private int idPintura;
    private String nombre;
    private String marca; 
    private String descripcion; 
    private String categoria;
    private String capacidad;
    private double precio;
    private int estatus;

    public Pintura(int idPintura, String nombre, String marca, String descripcion, String categoria, String capacidad, double precio, int estatus) {
        this.idPintura = idPintura;
        this.nombre = nombre;
        this.marca = marca;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.capacidad = capacidad;
        this.precio = precio;
        this.estatus = estatus;
    }

    public Pintura() {
    }
    
    

    /**
     * @return the idPintura
     */
    public int getIdPintura() {
        return idPintura;
    }

    /**
     * @param idPintura the idPintura to set
     */
    public void setIdPintura(int idPintura) {
        this.idPintura = idPintura;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the marca
     */
    public String getMarca() {
        return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the categoria
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * @param categoria the categoria to set
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * @return the capacidad
     */
    public String getCapacidad() {
        return capacidad;
    }

    /**
     * @param capacidad the capacidad to set
     */
    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    /**
     * @return the precio
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * @param precio the precio to set
     */
    public void setPrecio(double precio) {
        this.precio = precio;
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
    
}
