/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.ex.chips2.Contact;
import com.android.ex.chips2.RecipientEntry;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.model.ShareFileDataModel.UserRights;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.view.IShareFileView;

import java.util.List;

import rx.Observable;

public interface IShareFilePresenter {
    void bindView(@NonNull IShareFileView iShareFileView);
    void unbindView();
    @NonNull Observable<List<Contact>> getContacts(String filterStr);
    void listenFields(@Nullable String inputText, @NonNull UserRights spinnerState, boolean checkboxState);
    void dialogShown();
    void positiveButtonClicked(@NonNull List<RecipientEntry> recepients);
    void negativeButtonClicked();

    IShareFilePresenter STUB = new IShareFilePresenter() {
        @Override
        public void bindView(@NonNull IShareFileView view) {
        }

        @Override
        public void unbindView() {
        }

        @NonNull
        @Override
        public Observable<List<Contact>> getContacts(String filterStr) {
            return Observable.empty();
        }

        @Override
        public void listenFields(@Nullable String inputText, @NonNull UserRights spinnerState, boolean checkboxState) {
        }

        @Override
        public void dialogShown() {
        }

        @Override
        public void positiveButtonClicked(@Nullable List<RecipientEntry> recepients) {
        }

        @Override
        public void negativeButtonClicked() {
        }
    };
}
