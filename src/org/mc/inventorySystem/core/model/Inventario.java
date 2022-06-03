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
public class Inventario {
    private int idInventario;
    private Producto producto;    
    private int cantidad;

    public Inventario(int idInventario, Producto producto, int cantidad) {
        this.idInventario = idInventario;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Inventario() {
    }

    
    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    

}