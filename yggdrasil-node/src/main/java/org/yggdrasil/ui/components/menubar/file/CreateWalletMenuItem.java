package org.yggdrasil.ui.components.menubar.file;

import org.yggdrasil.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CreateWalletMenuItem extends JMenuItem {

    public CreateWalletMenuItem(String label) {
        this.setText(label);
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String walletName = JOptionPane.showInputDialog(MainFrame.frame, "Enter a new wallet name:", null);
                try {
                    assert (walletName != null) && (!walletName.isEmpty());
                    MainFrame.blockchainService.createWallet(walletName);
                } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException ex) {
                    JOptionPane.showMessageDialog(MainFrame.frame, ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
    }

}
