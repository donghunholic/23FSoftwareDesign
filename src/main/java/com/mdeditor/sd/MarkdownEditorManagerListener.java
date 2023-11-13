package com.mdeditor.sd;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;

public class MarkdownEditorManagerListener implements FileEditorManagerListener {

    private final Project project;

    public MarkdownEditorManagerListener(Project project) {
        this.project = project;
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        // Check if the selected file is a markdown file and not the current file
        System.out.println("selectionChanged called");
        FileEditor selectedEditor = event.getNewEditor();
        FileEditor[] editors = FileEditorManager.getInstance(project).getAllEditors();
        for (FileEditor editor : editors) {
            if (editor != selectedEditor) {
                if (editor instanceof MarkdownEditor) {
                    MarkdownEditor markdownEditor = (MarkdownEditor) editor;
                    if (markdownEditor != null) {
                        System.out.println("this editor is markdownEditor");
                        JTextPane Editor=(JTextPane)markdownEditor.getComponent();
                        Editor.setText("good!");
                    }
                }
            }
        }
    }


    // Method to get the MarkdownEditor instance from the project's FileEditorManager
    private MarkdownEditor getMarkdownEditor() {
        FileEditor[] editors = FileEditorManager.getInstance(project).getAllEditors();
        for (FileEditor editor : editors) {
            if (editor instanceof MarkdownEditor) {
                return (MarkdownEditor) editor;
            }
        }
        return null;
    }
}