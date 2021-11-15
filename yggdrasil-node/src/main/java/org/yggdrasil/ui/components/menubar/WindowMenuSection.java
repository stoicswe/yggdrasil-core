package org.yggdrasil.ui.components.menubar;

import javax.swing.*;

public class WindowMenuSection extends JMenu {

    public WindowMenuSection(String label) {
        this.setText(label);
        this.add(new JMenuItem("Minimize"));
        this.addSeparator();
        this.add(new JMenuItem("Sending Addresses"));
        this.add(new JMenuItem("Receiving Addresses"));
        this.addSeparator();
        this.add(new JMenuItem("Information"));
        this.add(new JMenuItem("Console"));
        this.add(new JMenuItem("Network Traffic"));
        this.add(new JMenuItem("Peers"));
    }

}
