/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.model;

import android.support.annotation.NonNull;

import com.android.ex.chips2.Contact;

import java.util.List;

public class ShareFileDataModel {
    public enum UserRights {
        VIEWING ("read"),
        EDITING ("write");

        private final String analyticsName;

        UserRights(String name) {
            analyticsName = name;
        }

        public String getAnalyticsName() {
            return analyticsName;
        }
    }


    @NonNull
    private List<Contact> contacts;
    @NonNull
    private UserRights rights;
    private boolean sendByEmail;

    public ShareFileDataModel() {
    }

    public ShareFileDataModel(@NonNull List<Contact> contacts, @NonNull UserRights rights, boolean sendByEmail) {
        this.contacts = contacts;
        this.rights = rights;
        this.sendByEmail = sendByEmail;
    }

    public @NonNull UserRights getRights() {
        return rights;
    }

    public boolean isSendByEmail() {
        return sendByEmail;
    }

    public @NonNull List<Contact> getContacts() {
        return contacts;
    }

    public boolean hasContacts() {
        return !contacts.isEmpty();
    }
}
