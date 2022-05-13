package org.yggdrasil.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.yggdrasil.core.api.service.BlockchainService;
import org.yggdrasil.ui.forms.ApplicationForm;
import org.springframework.core.env.Environment;

import javax.swing.*;
import java.awt.*;

/**
 * The main JFrame container for the graphical portion of the
 * Yggdrasil node application.
 *
 * @author nathanielbunch
 */
public class MainFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

    private Environment environment;
    public static JFrame frame;
    public static BlockchainService blockchainService;

    public MainFrame(Environment environment, String applicationName, BlockchainService blokService) {
        // If the application is not running with the headless profile,
        // then initialize the UI.
        this.environment = environment;
        if (!isHeadless()) {
            logger.debug("Initializing the application UI");
            blockchainService = blokService;
            frame = new ApplicationForm(applicationName);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frameSize = frame.getSize();
            if (frameSize.height > screenSize.height) {
                frameSize.height = screenSize.height;
            }
            if (frameSize.width > screenSize.width) {
                frameSize.width = screenSize.width;
            }
            frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            logger.debug("UI initialized.");
            frame.setVisible(true);
        }
    }

    private boolean isHeadless() {
        for(String profile : this.environment.getActiveProfiles()) {
            if(profile.contains("headless")){
                return true;
            }
        }
        return false;
    }

}
