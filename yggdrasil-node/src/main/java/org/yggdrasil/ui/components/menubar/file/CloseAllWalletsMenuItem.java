package org.yggdrasil.ui.components.menubar.file;

import javax.swing.*;

public class CloseAllWalletsMenuItem extends JMenuItem {

    public CloseAllWalletsMenuItem(String label) {
        this.setText(label);
        this.setEnabled(false);
    }
}
