/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.view;

import android.support.annotation.NonNull;

public interface IShareFileView {
    void setEnableSendButton(boolean enable);
    void initInput(@NonNull final String userId, @NonNull final String userName);
    void hideKeyboard();
}
