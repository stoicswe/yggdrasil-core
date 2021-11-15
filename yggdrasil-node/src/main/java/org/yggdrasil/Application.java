package org.yggdrasil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yggdrasil.ui.MainFrame;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;


/**
 * Application runner.
 *
 * @since 0.0.1
 * @author nathanielbunch
 *
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // Initiate the general look and feel of the UI elements
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Start the node in a springboot instance
        SpringApplication.run(Application.class, args);
    }

}
