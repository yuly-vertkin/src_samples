/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.sharefile;

import android.text.TextUtils;
import android.widget.AutoCompleteTextView;

/**
 * Created by georgeci on 10.04.2015.
 */
public class EmailValidator implements AutoCompleteTextView.Validator {
    @Override
    public boolean isValid(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String email = text.toString();
        String[] separated = email.split("\\\\"); // in case of exchange account name like co\email@domain.ru
        if (separated.length > 1)
            email = separated[1];
        else
            email = separated[0];

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public CharSequence fixText(CharSequence invalidText) {
        return "";
    }
}
