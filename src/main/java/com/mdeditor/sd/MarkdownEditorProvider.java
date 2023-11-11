package com.mdeditor.sd;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MarkdownEditorProvider implements FileEditorProvider {

    /**
     * The method is expected to run fast.
     *
     * @param project
     * @param file    file to be tested for acceptance.
     * @return {@code true} if provider can create valid editor for the specified {@code file}.
     */
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getName().endsWith(".md");
    }

    /**
     * Creates editor for the specified file.
     * <p>
     * This method is called only if the provider has accepted this file (i.e. method {@link #accept(Project, VirtualFile)} returned
     * {@code true}).
     * The provider should return only valid editor.
     *
     * @param project
     * @param file
     * @return created editor for specified file.
     */
    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new MarkdownEditor(project, file);
    }

    /**
     * @return editor type ID for the editors created with this FileEditorProvider. Each FileEditorProvider should have
     * a unique nonnull ID. The ID is used for saving/loading of EditorStates.
     */
    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return "WYSIWYG Markdown Editor";
    }

    /**
     * @return a policy that specifies how an editor created via this provider should be opened.
     * @see FileEditorPolicy#NONE
     * @see FileEditorPolicy#HIDE_DEFAULT_EDITOR
     * @see FileEditorPolicy#HIDE_OTHER_EDITORS
     * @see FileEditorPolicy#PLACE_BEFORE_DEFAULT_EDITOR
     * @see FileEditorPolicy#PLACE_AFTER_DEFAULT_EDITOR
     */
    @Override~
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
