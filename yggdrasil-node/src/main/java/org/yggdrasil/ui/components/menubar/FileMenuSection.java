package org.yggdrasil.ui.components.menubar;

import org.yggdrasil.ui.components.menubar.file.*;

import javax.swing.*;

public class FileMenuSection extends JMenu {

    JMenuItem createWallet = new CreateWalletMenuItem("Create Wallet");
    JMenu openWallet = new OpenWalletMenu("Open Wallet");
    JMenuItem closeWallet = new CloseWalletMenuItem("Close Wallet");
    JMenuItem closeAllWallets = new CloseAllWalletsMenuItem("Close All Wallets");
    JMenuItem backupWallet = new BackupWalletMenuItem("Backup Wallet");
    JMenuItem signMessage = new SignMessageMenuItem("Sign Message");
    JMenuItem verifyMessage = new VerifyMessageMenuItem("Verify Message");
    JMenuItem exitApp = new ExitAppMenuItem("Exit");

    public FileMenuSection(String label) {
        this.setText(label);
        this.add(createWallet);
        this.add(openWallet);
        this.add(closeWallet);
        this.add(closeAllWallets);
        this.addSeparator();
        this.add(backupWallet);
        this.add(signMessage);
        this.add(verifyMessage);
        this.addSeparator();
        this.add(exitApp);
    }

}
