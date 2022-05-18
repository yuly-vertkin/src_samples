/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.view;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.ncloudtech.cloudoffice.data.storage.api.File;

import java.util.List;

public interface ChooseFolderView {
    /**
     * Update file list and breadcrumbs with new data
     * @param files
     * @param parents
     */
    void updateViews(@NonNull List<File> files, @NonNull List<File> parents);

    void setActivityResult(@NonNull Intent intent, boolean isOk);

    /**
     * show erroe message
     * @param messageId
     */
    void showErrorMessage(int messageId);
    void showErrorMessage(@NonNull String message);
}
