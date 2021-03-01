/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 */

package com.ncloudtech.cloudoffice.android.myoffice.toolbox;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;

import com.ncloudtech.cloudoffice.android.common.analytics.AnalyticsInteractor;
import com.ncloudtech.cloudoffice.android.myoffice.core.JavaDocumentEditor;

import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.ALIGNMENT_CENTER;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.ALIGNMENT_LEFT;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.ALIGNMENT_RIGHT;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.BOLD;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.BULLETED_LIST;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.INDENT_DECREASE;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.INDENT_INCREASE;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.ITALIC;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.NUMBERED_LIST;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.STRIKETHROUGH;
import static com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands.UNDERLINE;
import static com.ncloudtech.cloudoffice.ndk.textformatting.AlignmentType.CENTER;
import static com.ncloudtech.cloudoffice.ndk.textformatting.AlignmentType.LEFT;
import static com.ncloudtech.cloudoffice.ndk.textformatting.AlignmentType.RIGHT;
import static com.ncloudtech.cloudoffice.ndk.textformatting.ListMarkerType.BulletCircleSolid;
import static com.ncloudtech.cloudoffice.ndk.textformatting.ListMarkerType.EnumeratorDecimalBracket;

public class CommandFactory implements ToolboxCommand.CommandFactory {
    @NonNull
    private final ToolboxCommand.KeyFactory keyFactory;
    @NonNull
    private final AnalyticsInteractor interactor;
    @NonNull
    private static SparseArrayCompat<ToolboxCommand> commands = new SparseArrayCompat<>();

    static {
        commands.put(BOLD, JavaDocumentEditor::toggleBold);
        commands.put(ITALIC, JavaDocumentEditor::toggleItalic);
        commands.put(UNDERLINE, JavaDocumentEditor::toggleUnderline);
        commands.put(STRIKETHROUGH, JavaDocumentEditor::toggleStrikethrough);
        commands.put(ALIGNMENT_LEFT, new ToolboxCommand.Alignment(LEFT));
        commands.put(ALIGNMENT_CENTER, new ToolboxCommand.Alignment(CENTER));
        commands.put(ALIGNMENT_RIGHT, new ToolboxCommand.Alignment(RIGHT));
        commands.put(BULLETED_LIST, new ToolboxCommand.MarkedList(BulletCircleSolid));
        commands.put(NUMBERED_LIST, new ToolboxCommand.MarkedList(EnumeratorDecimalBracket));
        commands.put(INDENT_DECREASE, JavaDocumentEditor::decreaseListLevel);
        commands.put(INDENT_INCREASE, JavaDocumentEditor::increaseListLevel);
    }

    public CommandFactory(@NonNull ToolboxCommand.KeyFactory keyFactory, @NonNull AnalyticsInteractor interactor) {
        this.keyFactory = keyFactory;
        this.interactor = interactor;
    }

    @Override
    @NonNull
    public ToolboxCommand createCommand(@Commands int commandId) {
        return editor -> {
            String key = keyFactory.createKey(commandId, editor);
            commands.get(commandId).execute(editor);
            interactor.logEvent(key);
        };
    }
}
