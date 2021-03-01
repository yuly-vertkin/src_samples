/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.data.repository.choosefolder;

import android.support.annotation.NonNull;

import com.ncloudtech.cloudoffice.android.network.api.ServerAPIImpl;
import com.ncloudtech.cloudoffice.android.network.api.data.rest.FileResource;
import com.ncloudtech.cloudoffice.data.storage.api.File;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class ChooseFolderRepositoryImpl implements ChooseFolderRepository {
    @NonNull
    private List<File> parentFolders = new ArrayList<>();

    @NonNull
    @Override
    public Observable<List<FileResource>> loadFiles(@NonNull String query) {
        return ServerAPIImpl.filesExt(query);
    }

    @NonNull
    @Override
    public Observable<List<File>> getFileParents(@NonNull File currentFile) {
        for (File file : parentFolders) {
            if (file.equals(currentFile)) {
                int index = parentFolders.indexOf(currentFile);
                parentFolders = parentFolders.subList(0, index);
                break;
            }
        }
        return Observable.just(parentFolders);
    }

    @NonNull
    @Override
    public Observable<File> getParentFile(@NonNull File currentFile) {
        return  getFileParents(currentFile)
            .flatMap(files -> Observable.just(!files.isEmpty() ? files.get(files.size() - 1) : null));
    }
}
