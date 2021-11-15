package org.yggdrasil.ui.forms;

import org.yggdrasil.ui.components.ApplicationMenuBar;

import javax.swing.*;
import java.awt.*;

public class ApplicationForm extends JFrame {

    // Set the menuBar of the application
    private JMenuBar menuBar = new ApplicationMenuBar();

    public ApplicationForm(String applicationName) {
        try {
            this.setTitle(applicationName);
            this.setSize(new Dimension(654, 524));
            this.setResizable(false);
            this.setJMenuBar(menuBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
