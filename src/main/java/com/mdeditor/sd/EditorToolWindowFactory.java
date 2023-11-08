package com.mdeditor.sd;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;

import javax.swing.*;

public class EditorToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();

        // Create content for the first tab
        JComponent contentComponent1 = new JLabel("Content of Tab 1");
        Content content1 = ContentFactory.getInstance().createContent(contentComponent1, "Tab 1", false);
        contentManager.addContent(content1);

        // Create content for the second tab
        JComponent contentComponent2 = new JLabel("Content of Tab 2");
        Content content2 = ContentFactory.getInstance().createContent(contentComponent2, "Tab 2", false);
        contentManager.addContent(content2);
    }

    // Other methods of ToolWindowFactory...
}


