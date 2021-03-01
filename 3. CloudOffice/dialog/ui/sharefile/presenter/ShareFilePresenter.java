/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.presenter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.ex.chips2.Contact;
import com.android.ex.chips2.RecipientEntry;
import com.ncloudtech.cloudoffice.android.common.analytics.Analytics;
import com.ncloudtech.cloudoffice.android.common.analytics.Keys;
import com.ncloudtech.cloudoffice.android.common.analytics.events.StringParamInfo;
import com.ncloudtech.cloudoffice.android.network.myfm.FilesEvents;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.sharefile.IShareFileInteractor;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.model.ShareFileDataModel;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.model.ShareFileDataModel.UserRights;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.view.IShareFileView;

import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;

public class ShareFilePresenter implements IShareFilePresenter {

    private Context context;
    private IShareFileView shareFileView;
    private IShareFileInteractor shareFileInteractor;
    @NonNull
    private String userId;
    @NonNull
    private String userName;
    @NonNull
    private String inputText;
    @NonNull
    private UserRights spinnerState;
    private boolean checkboxState;
    private boolean dialogCanceled;

    public ShareFilePresenter(@NonNull Context context, @NonNull IShareFileInteractor interactor) {
        this(context, interactor, "", "");
    }

    public ShareFilePresenter(@NonNull Context context, @NonNull IShareFileInteractor interactor, @Nullable String userId, @Nullable String userName) {
        this.context = context;
        shareFileInteractor = interactor;
        this.userId = (userId != null) ? userId : "";
        this.userName = (userName != null) ? userName : "";
        inputText = "";
        spinnerState = UserRights.VIEWING;
        dialogCanceled = true;
    }

    @Override
    public void bindView(@NonNull IShareFileView view) {
        shareFileView = view;
    }

    @Override
    public void unbindView() {
        shareFileView = null;

        EventBus.getDefault().post(new FilesEvents.ShareFileDialogClosed(dialogCanceled));
    }

    @NonNull
    @Override
    public Observable<List<Contact>> getContacts(String filterStr) {
        return shareFileInteractor.getContacts(filterStr);
    }

    @Override
    public void listenFields(@Nullable String inputText, @NonNull UserRights spinnerState, boolean checkboxState) {
        if (!this.inputText.equals(inputText)) {
            shareFileView.setEnableSendButton(shareFileInteractor.enableSendButton(inputText));
        }
        this.inputText = (inputText != null) ? inputText : "";
        this.spinnerState = spinnerState;
        this.checkboxState = checkboxState;
    }

    @Override
    public void dialogShown() {
        shareFileView.initInput(userId, userName);
        shareFileView.setEnableSendButton(shareFileInteractor.enableSendButton(userId));
    }

    @Override
    public void positiveButtonClicked(@NonNull List<RecipientEntry> recepients) {
        shareFileView.hideKeyboard();
        dialogCanceled = false;
        shareFileForUsers(recepients);
    }

    @Override
    public void negativeButtonClicked() {
        shareFileView.hideKeyboard();
        dialogCanceled = true;
    }

    private void shareFileForUsers(@NonNull List<RecipientEntry> recepients) {
        shareFileInteractor.shareFile(getShareFileDataModel(recepients));
    }

    private ShareFileDataModel getShareFileDataModel(@NonNull List<RecipientEntry> recepients) {
        List<Contact> contactList = getRecipients(recepients);
        Analytics.log(Keys.FM_SHARE_GO,
                      new StringParamInfo(Keys.Params.PARAM_ARE_NOTIFED,
                                          String.valueOf(checkboxState)),
                      new StringParamInfo(Keys.Params.PARAM_SHARE_RIHTS,
                                          spinnerState.getAnalyticsName()),
                      new StringParamInfo(Keys.Params.PARAM_SHARE_COUNT,
                                          String.valueOf(contactList.size())));
        return new ShareFileDataModel(contactList, spinnerState, checkboxState);
    }

    @NonNull
    private List<Contact> getRecipients(@NonNull List<RecipientEntry> recepients) {
        List<Contact> result = new LinkedList<>();
        for (RecipientEntry item : recepients) {
            if (!item.isValid()) {
                continue;
            }
            Contact c = new Contact(item.getId(), item.getDestination(), Contact.TYPE_GROUP.equals(item.getType()));
            if (!result.contains(c)) {
                result.add(c);
            }
        }
        return result;
    }

}
