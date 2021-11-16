package org.yggdrasil.ui.components.menubar.file;

import org.apache.commons.lang3.tuple.Pair;
import org.yggdrasil.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Comparator;
import java.util.List;

public class OpenWalletMenu extends JMenuItem {

    public OpenWalletMenu(String label) {
        this.setText(label);
        try {
            this.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<Pair<String, byte[]>> walletNamesPairs = MainFrame.blockchainService.getWalletNames();
                    if(walletNamesPairs.size() > 0) {
                        walletNamesPairs.sort(Comparator.comparing(Pair::getLeft));
                        String[] walletNames = walletNamesPairs.stream().map(Pair::getLeft).toArray(String[]::new);
                        String selectedWallet = (String) JOptionPane.showInputDialog(
                                MainFrame.frame,
                                "Wallet name:",
                                "Select a Wallet",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                walletNames,
                                walletNames[0]
                        );
                        try {
                            if (selectedWallet != null && !selectedWallet.isEmpty()) {
                                MainFrame.blockchainService.selectWallet(walletNamesPairs.stream().filter(p -> p.getLeft().contentEquals(selectedWallet)).findFirst().get().getRight());
                            }
                        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException ex) {
                            JOptionPane.showMessageDialog(MainFrame.frame, ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.frame, "There are no wallets created yet.", "No wallets", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame.frame, ex.getMessage());
            ex.printStackTrace();
        }
    }
}
