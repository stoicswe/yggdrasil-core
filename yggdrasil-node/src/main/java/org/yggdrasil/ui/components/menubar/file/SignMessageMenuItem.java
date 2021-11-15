package org.yggdrasil.ui.components.menubar.file;

import javax.swing.*;

public class SignMessageMenuItem extends JMenuItem {

    public SignMessageMenuItem(String label) {
        this.setText(label);
        this.setEnabled(false);
    }
}
