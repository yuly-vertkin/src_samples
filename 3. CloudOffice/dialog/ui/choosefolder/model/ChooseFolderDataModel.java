/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.model;

import android.support.annotation.NonNull;

import com.ncloudtech.cloudoffice.data.storage.api.File;

import java.util.List;

public class ChooseFolderDataModel {
    @NonNull
    private List<File> children;
    @NonNull
    private List<File> parents;
    @NonNull
    private File selectedFile;

    public ChooseFolderDataModel(@NonNull File selectedFile, @NonNull List<File> children, @NonNull List<File> parents) {
        this.children = children;
        this.selectedFile = selectedFile;
        this.parents = parents;
    }

    @NonNull
    public List<File> getChildren() {
        return children;
    }

    @NonNull
    public File getSelectedFile() {
        return selectedFile;
    }

    @NonNull
    public List<File> getParents() {
        return parents;
    }
}
