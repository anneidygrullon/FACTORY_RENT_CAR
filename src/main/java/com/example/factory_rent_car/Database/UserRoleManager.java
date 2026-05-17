package com.example.factory_rent_car.Database;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class UserRoleManager {

    private static final String FILE_NAME = ".factory_rent_car_roles.properties";
    private static final Path FILE_PATH = Paths.get(System.getProperty("user.home"), FILE_NAME);

    public static String getRole(String username) {
        // Primero revisa el roles.properties del classpath
        try (InputStream is = UserRoleManager.class.getResourceAsStream("/com/example/factory_rent_car/roles.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                String role = props.getProperty(username);
                if (role != null) return role;
            }
        } catch (Exception ignored) {}

        // Luego intenta con el archivo externo
        if (Files.exists(FILE_PATH)) {
            try (InputStream is = Files.newInputStream(FILE_PATH)) {
                Properties props = new Properties();
                props.load(is);
                String role = props.getProperty(username);
                if (role != null) return role;
            } catch (Exception ignored) {}
        }

        return "admin";
    }

    public static void setRole(String username, String role) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            Properties props = new Properties();
            if (Files.exists(FILE_PATH)) {
                try (InputStream is = Files.newInputStream(FILE_PATH)) {
                    props.load(is);
                }
            }
            props.setProperty(username, role);
            try (OutputStream os = Files.newOutputStream(FILE_PATH)) {
                props.store(os, "Factory Rent Car - User Roles");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeRole(String username) {
        try {
            if (Files.exists(FILE_PATH)) {
                Properties props = new Properties();
                try (InputStream is = Files.newInputStream(FILE_PATH)) {
                    props.load(is);
                }
                props.remove(username);
                try (OutputStream os = Files.newOutputStream(FILE_PATH)) {
                    props.store(os, "Factory Rent Car - User Roles");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Properties getAllRoles() {
        Properties all = new Properties();
        try (InputStream is = UserRoleManager.class.getResourceAsStream("/com/example/factory_rent_car/roles.properties")) {
            if (is != null) all.load(is);
        } catch (Exception ignored) {}
        if (Files.exists(FILE_PATH)) {
            try (InputStream is = Files.newInputStream(FILE_PATH)) {
                all.load(is);
            } catch (Exception ignored) {}
        }
        return all;
    }
}
