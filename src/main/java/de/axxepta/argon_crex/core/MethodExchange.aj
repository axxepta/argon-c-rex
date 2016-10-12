package de.axxepta.argon_for_man.core;

import de.axxepta.oxygen.utils.WorkspaceUtils;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.editor.EditorPageConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public aspect MethodExchange {
		
	pointcut execOpenURLString(String urlString) :
		execution(public void de.axxepta.oxygen.utils.WorkspaceUtils.openURLString(String)) && args(urlString);
	
	void around(String urlString) : execOpenURLString(urlString) {
		try {
            URL argonURL = new URL(urlString);
            WorkspaceUtils.setCursor(WorkspaceUtils.WAIT_CURSOR);
            if (urlString.toLowerCase().startsWith("argon")  &&
                    (urlString.toLowerCase().endsWith("docx") || urlString.toLowerCase().endsWith("xlsx")))
                PluginWorkspaceProvider.getPluginWorkspace().open(argonURL, EditorPageConstants.PAGE_AUTHOR, "text/xml");
            else
                PluginWorkspaceProvider.getPluginWorkspace().open(argonURL);
            WorkspaceUtils.setCursor(WorkspaceUtils.DEFAULT_CURSOR);
        } catch (MalformedURLException e1) {
            WorkspaceUtils.logger.error(e1);
        }
	}
	
}
