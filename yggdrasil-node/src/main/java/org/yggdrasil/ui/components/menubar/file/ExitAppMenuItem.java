package org.yggdrasil.ui.components.menubar.file;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExitAppMenuItem extends JMenuItem {

    public ExitAppMenuItem(String label) {
        this.setText(label);
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform necessary shutdown logic here
                System.exit(0);
            }
        });
    }
}
