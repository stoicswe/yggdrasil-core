package org.yggdrasil.ui.components.menubar;

import org.yggdrasil.ui.components.menubar.file.*;

import javax.swing.*;

public class FileMenuSection extends JMenu {

    JMenuItem createWallet = new CreateWalletMenuItem("Create Wallet");
    JMenuItem openWallet = new OpenWalletMenu("Open Wallet");
    JMenuItem closeWallet = new CloseWalletMenuItem("Close Wallet");
    JMenuItem closeAllWallets = new CloseAllWalletsMenuItem("Close All Wallets");
    JMenuItem backupWallet = new BackupWalletMenuItem("Backup Wallet");
    JMenuItem signMessage = new SignMessageMenuItem("Sign Message");
    JMenuItem verifyMessage = new VerifyMessageMenuItem("Verify Message");
    JMenuItem exitApp = new ExitAppMenuItem("Exit");

    public FileMenuSection(String label) {
        this.setText(label);
        this.add(this.createWallet);
        this.add(this.openWallet);
        this.add(this.closeWallet);
        this.add(this.closeAllWallets);
        this.addSeparator();
        this.add(this.backupWallet);
        this.add(this.signMessage);
        this.add(this.verifyMessage);
        this.addSeparator();
        this.add(this.exitApp);
    }

}
