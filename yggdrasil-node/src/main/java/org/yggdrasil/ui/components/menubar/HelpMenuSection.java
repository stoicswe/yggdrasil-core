package org.yggdrasil.ui.components.menubar;

import org.yggdrasil.ui.components.menubar.help.AboutAppMenuItem;
import org.yggdrasil.ui.components.menubar.help.AboutJavaMenuItem;
import org.yggdrasil.ui.components.menubar.help.CmdOptionsMenuItem;

import javax.swing.*;

public class HelpMenuSection extends JMenu {

    JMenuItem cmdOptions = new CmdOptionsMenuItem("Command-Line Options");
    JMenuItem aboutApp = new AboutAppMenuItem("About Yggdrasil Core");
    JMenuItem aboutJava = new AboutJavaMenuItem("About Java");

    public HelpMenuSection(String label) {
        this.setText(label);
        this.add(this.cmdOptions);
        this.addSeparator();
        this.add(this.aboutApp);
        this.add(this.aboutJava);
    }

}
