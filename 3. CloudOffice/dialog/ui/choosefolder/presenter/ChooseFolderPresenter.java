/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.view.ChooseFolderView;
import com.ncloudtech.cloudoffice.data.storage.api.File;

public interface ChooseFolderPresenter {
    /**
     * Bind view with presenter
     * @param fileView view
     */
    void bindView(@NonNull ChooseFolderView fileView);
    /**
     * Unbind view from presenter
     */
    void unbindView();

    /**
     * Handle current folder changing
     * @param file current folder
     */
    void onFolderChanged(@Nullable File file);
    /**
     * Handle current folder creating
     */
    void onFolderCreated();
    /**
     * Handle key event
     * @param keyCode
     * @param event
     * @return if event was handled
     */
    boolean onKeyEvent(int keyCode, @NonNull KeyEvent event);
    /**
     * Handle positive button click
     */
    void onPositiveButtonClicked();
    /**
     * Handle negative button click
     */
    void onNegativeButtonClicked();

    ChooseFolderPresenter STUB = new ChooseFolderPresenter() {

        @Override
        public void bindView(@NonNull ChooseFolderView fileView) {
        }

        @Override
        public void unbindView() {
        }

        @Override
        public void onFolderChanged(@Nullable File file) {
        }

        @Override
        public void onFolderCreated() {
        }

        @Override
        public boolean onKeyEvent(int keyCode, @NonNull KeyEvent event) {
            return false;
        }

        @Override
        public void onPositiveButtonClicked() {
        }

        @Override
        public void onNegativeButtonClicked() {
        }
    };
}
