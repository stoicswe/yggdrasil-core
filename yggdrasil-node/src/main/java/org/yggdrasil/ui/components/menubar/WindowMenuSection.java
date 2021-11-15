package org.yggdrasil.ui.components.menubar;

import org.yggdrasil.ui.components.menubar.window.*;

import javax.swing.*;

public class WindowMenuSection extends JMenu {

    JMenuItem minimize = new MinimizeWindowMenuItem("Minimize");
    JMenuItem sendingAddr = new SendingAddressesMenuItem("Sending Addresses");
    JMenuItem receivingAddr = new ReceivingAddressesMenuItem("Receiving Addresses");
    JMenuItem information = new InformationMenuItem("Information");
    JMenuItem console = new ConsoleMenuItem("Console");
    JMenuItem networkTraffic = new NetworkTrafficMenuItem("Network Traffic");
    JMenuItem peers = new PeersMenuItem("Peers");

    public WindowMenuSection(String label) {
        this.setText(label);
        this.add(this.minimize);
        this.addSeparator();
        this.add(this.sendingAddr);
        this.add(this.receivingAddr);
        this.addSeparator();
        this.add(this.information);
        this.add(this.console);
        this.add(this.networkTraffic);
        this.add(this.peers);
    }

}
