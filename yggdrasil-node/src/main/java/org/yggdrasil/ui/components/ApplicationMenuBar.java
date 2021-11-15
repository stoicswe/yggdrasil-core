package org.yggdrasil.ui.components;

import org.yggdrasil.ui.components.menubar.FileMenuSection;
import org.yggdrasil.ui.components.menubar.HelpMenuSection;
import org.yggdrasil.ui.components.menubar.SettingsMenuSection;
import org.yggdrasil.ui.components.menubar.WindowMenuSection;

import javax.swing.*;

public class ApplicationMenuBar extends JMenuBar {

    public ApplicationMenuBar() {
        this.add(new FileMenuSection("File"));
        this.add(new SettingsMenuSection("Settings"));
        this.add(new WindowMenuSection("Window"));
        this.add(new HelpMenuSection("Help"));
    }

}
