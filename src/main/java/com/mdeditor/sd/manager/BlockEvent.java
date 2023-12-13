package com.mdeditor.sd.manager;

/**
 * Type of block event occured in our editor.
 */
public enum BlockEvent {
    NEW_BLOCK, DELETE_BLOCK, OUTFOCUS_BLOCK_UP, OUTFOCUS_BLOCK_DOWN, OUTFOCUS_CLICKED,
    TRANSFORM_MULTI, TRANSFORM_SINGLE, UPDATE_BLOCK;
}
