/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.choosefolder;

import android.support.annotation.NonNull;

import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.model.ChooseFolderDataModel;
import com.ncloudtech.cloudoffice.data.storage.api.File;

import rx.Observable;

public interface ChooseFolderInteractor {
    /**
     * Get children and parents file lists for this file.
     * @param file current selected file
     * @return Observable for ChooseFolderDataModel
     */
    @NonNull
    Observable<ChooseFolderDataModel> getData(@NonNull File file);

    /**
     * Get parent for this file.
     * @param currentFile selected file
     * @return Observable for file
     */
    @NonNull
    Observable<File> getParentFile(@NonNull File currentFile);

    /**
     * Validate if folder not try to copy / move into itself
     * @param selectedFile file to be copied / moved
     * @return
     */
    boolean validateDestinationFolder(@NonNull File selectedFile);
}
