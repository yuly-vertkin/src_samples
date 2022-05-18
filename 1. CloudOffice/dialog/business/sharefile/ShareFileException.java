/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.sharefile;

public class ShareFileException extends Exception {
    private int arg1;

    public ShareFileException(String detailMessage) {
        super(detailMessage);
    }

    public ShareFileException(Throwable throwable, int arg1) {
        super(throwable);
        this.arg1 = arg1;
    }

    public int getArg1() {
        return arg1;
    }
}
