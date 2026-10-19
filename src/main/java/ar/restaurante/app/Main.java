package ar.restaurante.app;

import com.formdev.flatlaf.FlatLightLaf;
import ar.restaurante.vista.VentanaLogin;
import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup(); 
            
            // Personalizaciones esteticas globales
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8); 
            UIManager.put("TextComponent.showClearButton", true);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", true);
            UIManager.put("Table.gridColor", new Color(220, 224, 230)); 
            
        } catch (Exception ex) {
            System.err.println("No se pudo inicializar el tema estético moderno.");
        }
        
        SwingUtilities.invokeLater(() -> {
            new VentanaLogin().setVisible(true);
        });
    }
}
