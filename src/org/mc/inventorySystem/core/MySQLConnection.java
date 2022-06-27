/*
 *Author: Sergio Alba Arguello
 */
package org.mc.inventorySystem.core;

import java.sql.*;
import javax.swing.*;

public class MySQLConnection {

    Connection conn = null;

    public static Connection open() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/inventorysystem", "root", "");
            return conn;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }

}
