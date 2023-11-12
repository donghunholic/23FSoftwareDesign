package com.mdeditor.sd;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;


public class MarkdownEditor implements FileEditor, UserDataHolder {
    private final VirtualFile file;
    private final Project project;

    private final String cssPath;

    public MarkdownEditor(Project project, VirtualFile file) {
        this.file = file;
        this.project = project;


        cssPath = PathManager.getPluginsPath() + "/src/main/resources/editor/markdown.css";
    }

    /**
     * Returns a component which represents the editor in UI.
     */
    @Override
    public @NotNull JComponent getComponent() {
        try{
            String content = VfsUtil.loadText(file);

            JTextPane jTextPane = new JTextPane();
            jTextPane.setContentType("text/html");
            jTextPane.setEditable(true);


            VirtualFile cssFile = LocalFileSystem.getInstance().findFileByPath(cssPath);
            System.out.println(cssPath);

            if (cssFile != null && cssFile.exists()) {
                String cssContent = VfsUtil.loadText(cssFile);
                System.out.println(cssContent);
                String style = "<style>" + cssContent + "</style>";

                jTextPane.setText("<html>" + style + "<center><u>Text</u></center>" + content + "</html>");
            } else {
                jTextPane.setText("<html><center><u>Text</u></center>" + content + "</html>");
            }

            return jTextPane;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return "WYSIWYG Markdown Editor";
    }

    /**
     * Applies a given state to the editor.
     *
     * @param state
     */
    @Override
    public void setState(@NotNull FileEditorState state) {

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

    }

    /**
     * Removes specified listener.
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    /**
     * Usually not invoked directly, see class javadoc.
     */
    @Override
    public void dispose() {

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

    }

    @Override
    public @Nullable VirtualFile getFile() {
        return file;
    }
}
