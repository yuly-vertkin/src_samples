/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.sharefile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.ex.chips2.Contact;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.model.ShareFileDataModel;

import java.util.List;

import rx.Observable;

public interface IShareFileInteractor {
    @NonNull Observable<List<Contact>> getContacts(String filterStr);
    void shareFile(@NonNull ShareFileDataModel shareFileData);
    boolean enableSendButton(@Nullable String inputText);
}
