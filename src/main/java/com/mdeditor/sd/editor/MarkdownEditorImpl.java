package com.mdeditor.sd.editor;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.*;
import com.intellij.ui.components.JBScrollPane;
import com.mdeditor.sd.Block;
import com.mdeditor.sd.Utils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.editor.Document;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


public class MarkdownEditorImpl implements MarkdownEditor {
    // files
    private final VirtualFile file;
    private final Project project;
    private final String style;
    private String content = null;

    // for UI
    private List<Block> blocks;
    private Box interiorPanel; // for vertical align : blocks are in here
    private JScrollPane scrollPane; // for scroll

    public MarkdownEditorImpl(Project project, VirtualFile file) {
        this.file = file;
        this.project = project;
        this.style = readCss();

        setContentFromFile();

        // Add a listener to detect file editor changes
        project.getMessageBus().connect()
                .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, getFileEditorManagerListener());
        // Add a listener to detect file editor modified
        project.getMessageBus().connect()
                .subscribe(VirtualFileManager.VFS_CHANGES, getFileChangeEventListener());

        initBlocks();
        initUI();
        setInitialUI();
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
    private void setContentFromFile()
    {
        try {
            content = VfsUtilCore.loadText(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BulkFileListener getFileChangeEventListener(){
        return new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
                for(VFileEvent event : events){
                    if(Objects.equals(event.getFile(), file)){
                        setContentFromFile();
                    }
                }
            }
        };
    }

    private FileEditorManagerListener getFileEditorManagerListener(){
        return new FileEditorManagerListener() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                // Check if the selected file is a markdown file and not the current file
                FileEditor selectedEditor = event.getNewEditor();
                if (MarkdownEditorImpl.this.equals(selectedEditor)) {
                    saveVirtualFile(project,file);
                    setContentFromFile();
                }
                else{
                    FileEditor[] editors = FileEditorManager.getInstance(project).getAllEditors();
                    for(FileEditor editor : editors){
                        if (MarkdownEditorImpl.this.equals(editor)) {

                        }
                    }
                }
            }
        };
    }

    private String readCss(){
        InputStream cssStream = getClass().getClassLoader().getResourceAsStream("editor/github-markdown-light.css");
        if(cssStream == null) return "";

        try{
            String cssContent = new String(cssStream.readAllBytes());
            return Utils.wrapWithHtmlTag("style", cssContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initBlocks(){
        blocks = new LinkedList<>();

        // FIXME : Below are just temporal code for test
        for(int i = 0; i<5; i++){
            Block block = new Block();
            block.setContentType("text/html");
            block.setText(makeHtmlWithCss("<u>Text</u>" + content));
            block.setEditable(true);
            block.setBackground(Color.WHITE);
            block.grabFocus();

            // listen
            block.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        updateUI();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });

            blocks.add(block);
        }

        InputStream testHtmlStream = getClass().getClassLoader().getResourceAsStream("editor/markdown.html");
        if(testHtmlStream != null){
            try {
                String testHtmlContent = new String(testHtmlStream.readAllBytes());

                Block block = new Block();
                block.setContentType("text/html");
                block.setText(makeHtmlWithCss(testHtmlContent));
                block.setEditable(true);
                block.setBackground(Color.WHITE);
                blocks.add(block);
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initUI(){
        interiorPanel = Box.createVerticalBox();
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(interiorPanel, BorderLayout.PAGE_START);
        scrollPane = new JBScrollPane(wrapper);
    }

    // set initial UI
    private void setInitialUI(){
        for(Block elem : blocks){
            interiorPanel.add(elem);
        }
    }

    public void updateUI() {
        SwingUtilities.invokeLater(this::update);
    }

    private void update(){
        // FIXME : Below are just temporal code for test
        Block block = new Block();
        block.setText(content);
        block.setEditable(true);
        block.setBackground(Color.GREEN);
        blocks.add(block);

        // FIXME : remove upper part. main logic is here
        interiorPanel.removeAll();
        for(JTextPane elem : blocks){
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
