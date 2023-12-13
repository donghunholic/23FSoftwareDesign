package com.mdeditor.sd.editor;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.*;
import com.intellij.ui.components.JBScrollPane;
import com.mdeditor.sd.block.Block;
import com.mdeditor.sd.manager.BlockManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;

import java.io.IOException;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class MarkdownEditorImpl implements MarkdownEditor {
    // files
    private final VirtualFile file;
    private final Project project;

    // blocks
    private final BlockManager blockManager;

    // for UI
    private Box interiorPanel; // for vertical align : blocks are in here
    private JScrollPane scrollPane; // for scroll

    public MarkdownEditorImpl(Project project, VirtualFile file) {
        this.file = file;
        this.project = project;
        this.blockManager = new BlockManager(this);

        // set ui
        updateEditor();
        initUI();
        setInitialUI();

        // Add a listener to detect file editor changes
        project.getMessageBus().connect()
                .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, getFileEditorManagerListener());
        project.getMessageBus().connect()
                .subscribe(FileEditorManagerListener.Before.FILE_EDITOR_MANAGER, getFileEditorManagerListenerBefore());
        project.getMessageBus().connect()
                .subscribe(ProjectManager.TOPIC, getProjectManagerListener());
    }

    //VirtualFile Save Function
    public void saveVirtualFile(final Project project, final VirtualFile virtualFile) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document= FileDocumentManager.getInstance().getDocument(virtualFile);
            if(document!=null) {
                FileDocumentManager.getInstance().saveDocument(document);
            }
        });
    }

    //Markdown to Editor
    private void updateEditor()
    {
        try {
            String mdFileContent = VfsUtilCore.loadText(file);
            blockManager.setBlocks(mdFileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Editor to Markdown
    private void updateMarkdownFile() {
        ApplicationManager.getApplication().runWriteAction(() ->{
            try {
                if (file != null) {
                    VfsUtil.saveText(file, blockManager.extractFullMd());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private FileEditorManagerListener getFileEditorManagerListener(){
        return new FileEditorManagerListener() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                // Check if the selected file is a MarkdownEditor
                FileEditor selectedEditor = event.getNewEditor();
                if (MarkdownEditorImpl.this.equals(selectedEditor)) {
                    saveVirtualFile(project,file);
                    updateEditor();
                }
                //Check if the selected file is not a markdown file
                else{
                    FileEditor[] editors = FileEditorManager.getInstance(project).getAllEditors();
                    for(FileEditor editor : editors){
                        if (MarkdownEditorImpl.this.equals(editor)) {
                            updateMarkdownFile();
                            break;
                        }
                    }
                }
            }
        };
    }

    private FileEditorManagerListener.Before getFileEditorManagerListenerBefore(){
        return new FileEditorManagerListener.Before() {
            @Override
            public void beforeFileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                updateMarkdownFile();
            }
        };
    }

    private ProjectManagerListener getProjectManagerListener(){
        return new ProjectManagerListener() {
            @Override
            public void projectClosing(@NotNull Project project) {
                updateMarkdownFile();
            }
        };
    }

    private void initUI(){
        interiorPanel = Box.createVerticalBox();
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(interiorPanel, BorderLayout.PAGE_START);
        scrollPane = new JBScrollPane(wrapper);
    }

    // set initial UI
    private void setInitialUI(){
        for(Block elem : blockManager.getBlockList()){
            interiorPanel.add(elem);
        }
    }

    public void updateUI() {
        SwingUtilities.invokeLater(this::update);
    }

    private void update(){
        interiorPanel.removeAll();
        for(JTextPane elem : blockManager.getBlockList()){
            interiorPanel.add(elem);
        }

        interiorPanel.revalidate();
        interiorPanel.repaint();
    }

    /**
     * Returns a component which represents the editor in UI.
     */
    @Override
    public @NotNull JComponent getComponent() {
        return scrollPane;
    }

    /**
     * Returns a component to be focused when the editor is opened.
     */
    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return null;
    }

    /**
     * Returns editor's name - a string that identifies the editor among others
     * (e.g.: "GUI Designer" for graphical editing and "Text" for textual representation of a GUI form editor).
     */
    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return "EASY Markdown Editor";
    }

    /**
     * Applies a given state to the editor.
     *
     * @param state
     */
    @Override
    public void setState(@NotNull FileEditorState state) {
        // nothing to do here, so left it empty
    }

    /**
     * Returns {@code true} when editor's content differs from its source (e.g. a file).
     */
    @Override
    public boolean isModified() {
        return false;
    }

    /**
     * An editor is valid if its contents still exist.
     * For example, an editor displaying the contents of some file stops being valid if the file is deleted.
     * An editor can also become invalid after being disposed of.
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Adds specified listener.
     *
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
        // nothing to do here, so left it empty
    }

    /**
     * Removes specified listener.
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
        // nothing to do here, so left it empty
    }

    /**
     * Usually not invoked directly, see class javadoc.
     */
    @Override
    public void dispose() {
        // nothing to do here, so left it empty
    }

    /**
     * @param key
     * @return a user data value associated with this object. Doesn't require read action.
     */
    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    /**
     * Add a new user data value to this object. Doesn't require write action.
     *
     * @param key
     * @param value
     */
    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
        // nothing to do here, so left it empty
    }

    @Override
    public @Nullable VirtualFile getFile() {
        return file;
    }
}
