/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.data.repository.choosefolder;

import android.support.annotation.NonNull;

import com.ncloudtech.cloudoffice.android.network.api.data.rest.FileResource;
import com.ncloudtech.cloudoffice.data.storage.api.File;

import java.util.List;

import rx.Observable;

public interface ChooseFolderRepository {
    /**
     * Get file list from server
     * @param query query string for server
     * @return Observable for file list
     */
    @NonNull
    Observable<List<FileResource>> loadFiles(@NonNull String query);

    /**
     * Get file parents list including file itself
     * @param currentFile selected file
     * @return Observable for file list
     */
    @NonNull
    Observable<List<File>> getFileParents(@NonNull File currentFile);

    /**
     * Get parent for this file.
     * @param currentFile selected file
     * @return Observable for file
     */
    @NonNull
    Observable<File> getParentFile(@NonNull File currentFile);
}
