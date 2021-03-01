/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.choosefolder;

import android.support.annotation.NonNull;

import com.ncloudtech.cloudoffice.android.network.api.APIHelper;
import com.ncloudtech.cloudoffice.android.network.data.DBHelper;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.data.repository.choosefolder.ChooseFolderRepository;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.model.ChooseFolderDataModel;
import com.ncloudtech.cloudoffice.data.storage.api.File;

import java.util.List;

import rx.Observable;

public class ChooseFolderInteractorImpl implements ChooseFolderInteractor {
    /**
     * Repository reference
     */
    @NonNull
    private ChooseFolderRepository repository;

    public ChooseFolderInteractorImpl(@NonNull ChooseFolderRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public Observable<ChooseFolderDataModel> getData(@NonNull File file) {
        return Observable.combineLatest(
            repository.getFileParents(file)
            .doOnNext(files -> files.add(file)),
            loadFilesInternal(file),
            (parents, files) -> new ChooseFolderDataModel(file, files, parents)
        );
    }

    @NonNull
    private Observable<List<File>> loadFilesInternal(@NonNull File file){
        return Observable.concat(
            Observable.just(getFilesFromDB(file)),
            repository.loadFiles(prepareFilesIDQuery(file))
                .flatMap(fileResList -> Observable.just(APIHelper.getFiles(fileResList)))
                .doOnNext(files -> saveFilesInDb(file, files))
        );
    }

    @NonNull
    protected List<File> getFilesFromDB(@NonNull File file) {
        return DBHelper.getChildFilesFromDB(file.getId());
    }

    protected void saveFilesInDb(@NonNull File file, @NonNull List<File> files){
        DBHelper.syncDB(file, files);
    }

    @NonNull
    protected String prepareFilesIDQuery(@NonNull File file) {
        return APIHelper.prepareFilesIDQuery(file.getId());
    }

    @Override
    @NonNull
    public Observable<File> getParentFile(@NonNull File file) {
        return repository.getParentFile(file);
    }

    @Override
    public boolean validateDestinationFolder(@NonNull File selectedFile) {
        List<File> path = repository.getFileParents(selectedFile).toBlocking().first();
        return !path.contains(selectedFile);
    }
}
