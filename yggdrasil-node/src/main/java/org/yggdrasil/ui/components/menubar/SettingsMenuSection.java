package org.yggdrasil.ui.components.menubar;

import javax.swing.*;

public class SettingsMenuSection extends JMenu {

    public SettingsMenuSection(String label) {
        this.add(new JMenuItem("Encrypt Wallet"));
        this.add(new JMenuItem("Change Passphrase"));
        this.addSeparator();
        this.add(new JMenuItem("Mask Values"));
        this.addSeparator();
        this.add(new JMenuItem("Options..."));
    }

}
