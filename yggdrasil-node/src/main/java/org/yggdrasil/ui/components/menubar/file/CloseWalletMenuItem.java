package org.yggdrasil.ui.components.menubar.file;

import javax.swing.*;

public class CloseWalletMenuItem extends JMenuItem {

    public CloseWalletMenuItem(String label) {
        this.setText(label);
        this.setEnabled(false);
    }
}
