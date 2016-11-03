package de.axxepta.argon_crex.workspace;

import ro.sync.exml.plugin.option.OptionPagePluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

import javax.swing.*;
import java.awt.*;

/**
 * @author wittenberg@axxepta.de
 */
public class CRexConnectionOptionPage extends OptionPagePluginExtension {

    public static final String KEY_CREX_SERVER = "KEY_CREX_SERVER";
    public static final String KEY_CREX_USER = "KEY_CREX_USER";
    public static final String KEY_CREX_PWD = "KEY_CREX_PWD";

    private static final String DEF_CREX_SERVER = "https://c-rex.net";
    private static final String DEF_CREX_USER = "demo@c-rex.net";
    private static final String DEF_CREX_PWD = "demo$crex";

    private JTextField serverTextField;
    private JTextField userTextField;
    private JTextField pwdTextField;

    @Override
    public void apply(PluginWorkspace pluginWorkspace) {
        pluginWorkspace.getOptionsStorage().setOption(KEY_CREX_SERVER,
                !"".equals(serverTextField.getText()) ? serverTextField.getText() : DEF_CREX_SERVER);
        pluginWorkspace.getOptionsStorage().setOption(KEY_CREX_USER,
                !"".equals(userTextField.getText()) ? userTextField.getText() : DEF_CREX_USER);
        pluginWorkspace.getOptionsStorage().setOption(KEY_CREX_PWD,
                !"".equals(pwdTextField.getText()) ? pwdTextField.getText() : DEF_CREX_PWD);
    }

    @Override
    public void restoreDefaults() {
        serverTextField.setText(DEF_CREX_SERVER);
        userTextField.setText(DEF_CREX_USER);
        pwdTextField.setText(DEF_CREX_PWD);
    }

    @Override
    public String getTitle() {
        return "C-Rex Connection Settings";
    }

    @Override
    public JComponent init(PluginWorkspace pluginWorkspace) {
        //initial values
        String templatesDB = getOption(KEY_CREX_SERVER, false);
        String templatesSource = getOption(KEY_CREX_USER, false);
        String sharedDB = getOption(KEY_CREX_PWD, false);

        GridBagConstraints c = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel titleLbl = new JLabel("C-Rex connection settings");
        titleLbl.setFont(titleLbl.getFont().deriveFont(Font.BOLD));
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.WEST;
        panel.add(titleLbl, c);
        //
        c.gridx = 0;
        c.gridy++;
        JLabel serverLbl = new JLabel("Server:");
        panel.add(serverLbl, c);

        serverTextField = new JTextField();
        c.gridx++;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 5, 0, 5);
        panel.add(serverTextField, c);
        //
        c.gridx = 0;
        c.gridy++;
        JLabel userLbl = new JLabel("User name:");
        panel.add(userLbl, c);

        userTextField = new JTextField();
        c.gridx++;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 5, 0, 5);
        panel.add(userTextField, c);
        //
        c.gridx = 0;
        c.gridy++;
        JLabel pwdTextFieldLbl = new JLabel("Password:");
        panel.add(pwdTextFieldLbl, c);

        pwdTextField = new JTextField();
        c.gridx++;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 5, 0, 5);
        panel.add(pwdTextField, c);
 

        // initialize fields
        serverTextField.setText(templatesDB != null ? templatesDB : "");
        userTextField.setText(templatesSource != null ? templatesSource : "");
        pwdTextField.setText(sharedDB != null ? sharedDB : "");

        // spacer
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), c);

        return panel;
    }


    public static String getOption(String key, boolean defaults) {
        String defaultValue;
        switch (key) {
            case KEY_CREX_SERVER: defaultValue = DEF_CREX_SERVER; break;
            case KEY_CREX_USER: defaultValue = DEF_CREX_USER; break;
            case KEY_CREX_PWD: defaultValue = DEF_CREX_PWD; break;
            default: defaultValue = "empty option";
        }
        if (defaults) {
            return defaultValue;
        } else {
            PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
            if (pluginWorkspace != null) {
                WSOptionsStorage store = pluginWorkspace.getOptionsStorage();
                if (store != null)
                    return store.getOption(key, defaultValue);
                else {
                    return defaultValue;
                }
            } else {
                return defaultValue;
            }
        }
    }

}
