package org.example;

/**
 * This is the bridge class that allows us to run JavaFX
 * without complex module-path errors in IntelliJ.
 */
public class NebulaLauncher {
    public static void main(String[] args) {
        // This manually triggers the NebulaApp window
        NebulaApp.main(args);
    }
}