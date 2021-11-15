package org.yggdrasil.ui.components.menubar.file;

import javax.swing.*;

public class BackupWalletMenuItem extends JMenuItem {

    public BackupWalletMenuItem(String label) {
        this.setText(label);
        this.setEnabled(false);
    }

}
