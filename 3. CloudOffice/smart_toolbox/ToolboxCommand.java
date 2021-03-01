/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.myoffice.toolbox;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.ncloudtech.cloudoffice.android.myoffice.core.JavaDocumentEditor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.ALIGNMENT_CENTER;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.ALIGNMENT_LEFT;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.ALIGNMENT_RIGHT;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.BOLD;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.BULLETED_LIST;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.INDENT_DECREASE;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.INDENT_INCREASE;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.ITALIC;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.NO_COMMAND;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.NUMBERED_LIST;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.STRIKETHROUGH;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.UNDERLINE;

public interface ToolboxCommand {
    /**
     * Execute command
     * @param editor
     */
    void execute(@NonNull JavaDocumentEditor editor);

    @IntDef(value = {NO_COMMAND, BOLD, ITALIC, UNDERLINE, STRIKETHROUGH, ALIGNMENT_LEFT, ALIGNMENT_CENTER, ALIGNMENT_RIGHT, BULLETED_LIST, NUMBERED_LIST, INDENT_DECREASE, INDENT_INCREASE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Commands {
        int NO_COMMAND = 0;
        int BOLD = 1;
        int ITALIC = 2;
        int UNDERLINE = 3;
        int STRIKETHROUGH = 4;
        int ALIGNMENT_LEFT = 5;
        int ALIGNMENT_CENTER = 6;
        int ALIGNMENT_RIGHT = 7;
        int BULLETED_LIST = 8;
        int NUMBERED_LIST = 9;
        int INDENT_DECREASE = 10;
        int INDENT_INCREASE = 11;
    }

    class Alignment implements ToolboxCommand {
        private int alignment;

        Alignment(int alignment) {
            this.alignment = alignment;
        }

        @Override
        public void execute(@NonNull JavaDocumentEditor editor) {
            // Core start alignment with 1 but android ui with 0
            editor.setAlignment(alignment + 1);
        }
    }

    class MarkedList implements ToolboxCommand {
        private int markerType;

        MarkedList(int markerType) {
            this.markerType = markerType;
        }

        @Override
        public void execute(@NonNull JavaDocumentEditor editor) {
            if (editor.getListMarkerType() != markerType) {
                editor.setListMarkerType(markerType);
            } else {
                editor.resetListMarkerType();
            }
        }
    }

    interface KeyFactory {
        /**
         * Create analytics logging key
         * @param commandId
         * @param editor
         * @return
         */
        @NonNull
        String createKey(@Commands int commandId, @NonNull JavaDocumentEditor editor);
    }

    interface CommandFactory {
        /**
         * Create toolbox command
         * @param commandId
         * @return
         */
        @NonNull
        ToolboxCommand createCommand(@Commands int commandId);
    }
}
