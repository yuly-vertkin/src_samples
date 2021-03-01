/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.myoffice.toolbox;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ncloudtech.cloudoffice.R;
import com.ncloudtech.cloudoffice.android.myoffice.core.model.ExtCharacterProperties;
import com.ncloudtech.cloudoffice.android.myoffice.toolbox.ToolboxCommand.Commands;
import com.ncloudtech.cloudoffice.android.myoffice.ui.EditorView.ISmartToolboxVisibilityPresenter;

import rx.Observable;

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

public interface ISmartToolboxPresenter extends ISmartToolboxVisibilityPresenter {
    /**
     * Get observable of toolbox commands
     * @return
     */
    @NonNull
    Observable<ToolboxCommand> getToolboxCommandObservable();

    /**
     * Set UI updates observable
     * @param observable
     */
    void setUpdateUIObservable(@Nullable Observable<ExtCharacterProperties> observable);

    /**
     * Unbind
     */
    void unbind();

    /**
     * On update properties
     */
    void onUpdateProperties();

    /**
     * Click button handler
     * @param id
     */
    void onClickButton(int id);

    enum CommandEnum {
        BOLD_CMD(R.id.bold, BOLD),
        ITALIC_CMD(R.id.italic, ITALIC),
        UNDERLINE_CMD(R.id.underline, UNDERLINE),
        STRIKETHROUGH_CMD(R.id.strikethrough, STRIKETHROUGH),
        ALIGNMENT_LEFT_CMD(R.id.alignment_left, ALIGNMENT_LEFT),
        ALIGNMENT_CENTER_CMD(R.id.alignment_center, ALIGNMENT_CENTER),
        ALIGNMENT_RIGHT_CMD(R.id.alignment_right, ALIGNMENT_RIGHT),
        BULLETED_LIST_CMD(R.id.bulleted_list, BULLETED_LIST),
        NUMBERED_LIST_CMD(R.id.numbered_list, NUMBERED_LIST),
        INDENT_DECREASE_CMD(R.id.decrease_indent, INDENT_DECREASE),
        INDENT_INCREASE_CMD(R.id.increase_indent, INDENT_INCREASE);

        private int resId;
        @Commands
        private int cmdId;

        CommandEnum(int resId, @Commands int cmdId) {
            this.resId = resId;
            this.cmdId = cmdId;
        }

        @Commands
        public static int getCommandId(int resId) {
            CommandEnum[] values = CommandEnum.values();
            for (CommandEnum commandEnum : values) {
                if (commandEnum.resId == resId) {
                    return commandEnum.cmdId;
                }
            }
            return NO_COMMAND;
        }
    }
}
