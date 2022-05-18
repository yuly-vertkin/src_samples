/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.data.repository.sharefile;


import android.support.annotation.NonNull;

import com.ncloudtech.cloudoffice.android.network.api.AuthAPI;
import com.ncloudtech.cloudoffice.data.storage.api.AddFilePermissionsRequest;

import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;

public interface IShareFileRepository {
    @NonNull Observable<AuthAPI.ContactsResponse> getContacts();
    @NonNull Observable<List<AuthAPI.Group>> getGroups();
    @NonNull Observable<ResponseBody> shareFile(@NonNull String fileId, @NonNull AddFilePermissionsRequest request);
}
