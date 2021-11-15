package org.yggdrasil.ui.components.menubar.file;

import javax.swing.*;

public class VerifyMessageMenuItem extends JMenuItem {

    public VerifyMessageMenuItem(String label) {
        this.setText(label);
        this.setEnabled(false);
    }
}
