package com.example.factory_rent_car.Util;

import javax.swing.*;

public class MensajeFactory {

    public static void informacion(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void advertencia(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(null, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void errorSQL(Exception e, String contexto) {
        error(contexto + ": " + e.getMessage());
        e.printStackTrace();
    }
}
