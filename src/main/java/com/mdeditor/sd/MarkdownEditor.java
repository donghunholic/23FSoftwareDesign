package com.mdeditor.sd;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;


public class MarkdownEditor implements FileEditor, UserDataHolder {
    private final VirtualFile file;
    private final Project project;

    private final String style;

    private final JTextPane jTextPane = new JTextPane();

    public MarkdownEditor(Project project, VirtualFile file) {
        this.file = file;
        this.project = project;
        this.style = readCss();

        updateEditor();

        jTextPane.setContentType("text/html");
        jTextPane.setEditable(true);

        // Add a listener to detect file editor changes
        project.getMessageBus().connect()
                .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, getFileEditorManagerListener());
        project.getMessageBus().connect()
                .subscribe(FileEditorManagerListener.Before.FILE_EDITOR_MANAGER, getFileEditorManagerListener_Before());
        project.getMessageBus().connect()
                .subscribe(ProjectManager.TOPIC, getProjectManagerListener());
    }

    //VirtualFile Save Function
    public static void saveVirtualFile(final Project project, final VirtualFile virtualFile) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document= FileDocumentManager.getInstance().getDocument(virtualFile);
            if(document!=null)
            {
                FileDocumentManager.getInstance().saveDocument(document);
            }
        });
    }
    //Markdown to Editor
    private void updateEditor()
    {
        try {
            jTextPane.setText(VfsUtil.loadText(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Editor to Markdown
    private void updateMarkdownFile() {
        ApplicationManager.getApplication().runWriteAction(() ->{
        try {
            if (file != null) {
                VfsUtil.saveText(file, HtmlParser(jTextPane.getText()));
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
                // Check if the selected file is a markdown file and not the current file
                FileEditor selectedEditor = event.getNewEditor();
                if (MarkdownEditor.this.equals(selectedEditor)) {
                    saveVirtualFile(project,file);
                    updateEditor();
                }
                //Check if the selected file is not a markdown file
                else{
                    FileEditor[] editors = FileEditorManager.getInstance(project).getAllEditors();
                    for(FileEditor editor : editors){
                        if (MarkdownEditor.this.equals(editor)) {
                            updateMarkdownFile();
                            break;
                        }
                    }
                }
            }
        };
    }

    private FileEditorManagerListener.Before getFileEditorManagerListener_Before(){
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

    private String HtmlParser(String html){
        Element body = Jsoup.parse(html).body();
        String text = body.text();
        Elements centerTags = Jsoup.parse(jTextPane.getText()).select("center");
        for (Element centerTag : centerTags) {
            text = text.replace(centerTag.text(), "");
        }
        return text;
    }



    private String readCss(){
        InputStream cssStream = getClass().getClassLoader().getResourceAsStream("editor/markdown.css");
        if(cssStream == null) return "";

        try{
            String cssContent = new String(cssStream.readAllBytes());
            return Utils.wrapWithHtmlTag("style", cssContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a component which represents the editor in UI.
     */
    @Override
    public @NotNull JComponent getComponent() {
        return jTextPane;
    }

    private String makeHtmlWithCss(String html){
        return Utils.wrapWithHtmlTag("html", style + html);
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
        System.out.println(jTextPane.getText());
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
