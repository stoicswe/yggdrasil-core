package org.yggdrasil.ui.components.menubar;

import javax.swing.*;

public class HelpMenuSection extends JMenu {

    public HelpMenuSection(String label) {
        this.setText(label);
        this.add(new JMenuItem("Command-Line Options"));
        this.addSeparator();
        this.add(new JMenuItem("About Yggdrasil Core"));
        this.add(new JMenuItem("About Java"));
    }

}
