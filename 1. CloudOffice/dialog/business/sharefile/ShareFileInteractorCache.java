/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.sharefile;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.ex.chips2.Contact;
import com.ncloudtech.cloudoffice.android.common.cache.CacheManager;
import com.ncloudtech.cloudoffice.android.common.util.Logr;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import rx.Observable;

public class ShareFileInteractorCache {
    private static final String SERVER_CONTACTS_CACHE_FILE_NAME = "ServerContactsCache";
    @NonNull
    private Context context;

    public ShareFileInteractorCache(@NonNull Context context) {
        this.context = context;
    }

    public @NonNull Observable<List<Contact>> getServerContacts() {
        try {
            return Observable.just((List<Contact>) CacheManager.readObject(context, SERVER_CONTACTS_CACHE_FILE_NAME));
        } catch (IOException | ClassNotFoundException e ) {
            return Observable.just(Collections.emptyList());
        }
    }

    public void saveServerContacts(@NonNull List<Contact> contacts){
        try {
            CacheManager.writeObject(context, SERVER_CONTACTS_CACHE_FILE_NAME, contacts);
        } catch (IOException e) {
            Logr.e("Error caching server contacts");
        }
    }
}
