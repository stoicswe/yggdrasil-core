package org.yggdrasil.ui.components.menubar;

import org.yggdrasil.ui.components.menubar.settings.ChangePassphraseMenuItem;
import org.yggdrasil.ui.components.menubar.settings.EncryptWalletMenuItem;
import org.yggdrasil.ui.components.menubar.settings.MaskValuesMenuItem;
import org.yggdrasil.ui.components.menubar.settings.OptionsMenuItem;

import javax.swing.*;

public class SettingsMenuSection extends JMenu {

    JMenuItem encryptWallet = new EncryptWalletMenuItem("Encrypt Wallet");
    JMenuItem changePassphrase = new ChangePassphraseMenuItem("Change Passphrase");
    JMenuItem maskValues = new MaskValuesMenuItem("Mask Values");
    JMenuItem options = new OptionsMenuItem("Options...");

    public SettingsMenuSection(String label) {
        this.setText(label);
        this.add(this.encryptWallet);
        this.add(this.changePassphrase);
        this.addSeparator();
        this.add(this.maskValues);
        this.addSeparator();
        this.add(this.options);
    }

}
