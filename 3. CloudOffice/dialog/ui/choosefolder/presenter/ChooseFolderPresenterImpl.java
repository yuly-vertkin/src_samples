/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.ncloudtech.cloudoffice.R;
import com.ncloudtech.cloudoffice.android.common.util.InternetConnectionDetector;
import com.ncloudtech.cloudoffice.android.network.myfm.FilesEvents;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.choosefolder.ChooseFolderInteractor;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.view.ChooseFolderView;
import com.ncloudtech.cloudoffice.android.util.EventBusManager;
import com.ncloudtech.cloudoffice.data.storage.api.File;
import com.ncloudtech.cloudoffice.fsconnector.exception.SrvBaseException;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.view.ChooseFolderFragmentDialog.CURRENT_FOLDER_STR;
import static com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.view.ChooseFolderFragmentDialog.IS_COPY_STR;
import static com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.view.ChooseFolderFragmentDialog.SELECTED_FILE_STR;

public class ChooseFolderPresenterImpl implements ChooseFolderPresenter {
    /**
     * Activity context
     */
    @NonNull
    private Context context;
    /**
     * Current folder
     */
    @NonNull
    private File currentFolder;
    /**
     * File selected for copy or move
     */
    @NonNull
    private final File selectedFile;
    /**
     * Copy or move selected file
     */
    private boolean isCopy;
    /**
     * View reference
     */
    @Nullable
    private ChooseFolderView fileView;
    /**
     * Interactor reference
     */
    @NonNull
    private ChooseFolderInteractor interactor;
    @NonNull
    private EventBusManager eventBusManager;
    /**
     * Rx subscription
     */
    @Nullable
    private Subscription subscription;

    public ChooseFolderPresenterImpl(@NonNull Context context, @NonNull ChooseFolderInteractor interactor,
                                     @NonNull EventBusManager eventBusManager,
                                     @NonNull File rootFolder, @NonNull File selectedFile, boolean isCopy) {
        this.context = context;
        this.interactor = interactor;
        this.eventBusManager = eventBusManager;
        this.currentFolder = rootFolder;
        this.selectedFile = selectedFile;
        this.isCopy = isCopy;
    }

    @Override
    public void bindView(@NonNull ChooseFolderView fileView) {
        this.fileView = fileView;
        eventBusManager.register(this);
    }

    @Override
    public void unbindView() {
        fileView = null;
        eventBusManager.unregister(this);

        unsubscribe();
    }

    @Override
    public void onFolderChanged(@Nullable File file) {
        if (file == null) {
            return;
        }

        unsubscribe();

        subscription = interactor.getData(file)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(dataModel -> {
                currentFolder = file;
                if (fileView != null) {
                    fileView.updateViews(dataModel.getChildren(), dataModel.getParents());
                }
            }, throwable -> {
                if (fileView != null) {
                    fileView.showErrorMessage(R.string.error_network_required);
                }
            });
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public boolean onKeyEvent(int keyCode, @NonNull KeyEvent event) {
        // handle back press
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            File parent = interactor.getParentFile(currentFolder).toBlocking().first();
            if (parent != null) {
                onFolderChanged(parent);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFolderCreated() {
        eventBusManager.post(new FilesEvents.CreateFolderEvent(currentFolder));
    }

    /**
     * Handle event: file was created
     * @param event
     */
    public void onEventMainThread(FilesEvents.AddFileToAdapterEvent event) {
        onFolderChanged(currentFolder);
        eventBusManager.post(new FilesEvents.LoadFileListFromServerEvent());
    }

    /**
     * Handle event: file was renamed
     * @param event
     */
    public void onEventMainThread(FilesEvents.UpdatedFileEvent event) {
        boolean errorNotConnected = InternetConnectionDetector.isNotConnected(context);
        boolean errorFileAlreadyExist = TextUtils.equals(
            SrvBaseException.ErrorCode.SRV_ERR_FILE_ALREADY_EXIST.name(),
            event.errorCodeId.toUpperCase());

        if (!errorNotConnected && !errorFileAlreadyExist) {
            onFolderChanged(event.file);
        }

        eventBusManager.post(new FilesEvents.LoadFileListFromServerEvent());
    }

    @Override
    public void onPositiveButtonClicked() {
        if (interactor.validateDestinationFolder(selectedFile)) {
            setActivityResult(true);
        } else if (fileView != null) {
            fileView.showErrorMessage(R.string.error_file_move);
        }
    }

    @Override
    public void onNegativeButtonClicked() {
        setActivityResult(false);
    }

    private void setActivityResult(boolean isOk) {
        if (fileView != null) {
            Intent intent = new Intent();
            intent.putExtra(CURRENT_FOLDER_STR, currentFolder);
            intent.putExtra(SELECTED_FILE_STR, selectedFile);
            intent.putExtra(IS_COPY_STR, isCopy);
            fileView.setActivityResult(intent, isOk);
        }
    }
}
