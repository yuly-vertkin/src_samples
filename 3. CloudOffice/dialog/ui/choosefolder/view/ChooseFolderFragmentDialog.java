/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ncloudtech.cloudoffice.R;
import com.ncloudtech.cloudoffice.android.common.AppConstants;
import com.ncloudtech.cloudoffice.android.common.myfm.widget.breadcrumbs.BreadcrumbsBaseView;
import com.ncloudtech.cloudoffice.android.common.util.AndroidHelper;
import com.ncloudtech.cloudoffice.android.common.widgets.dialog.MaterialDialog;
import com.ncloudtech.cloudoffice.android.network.myfm.adapter.SimpleFoldersListAdapter;
import com.ncloudtech.cloudoffice.android.network.myfm.widget.breadcrumbs.BreadcrumbsView;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.choosefolder.ChooseFolderInteractorImpl;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.data.repository.choosefolder.ChooseFolderRepositoryImpl;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.presenter.ChooseFolderPresenter;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.presenter.ChooseFolderPresenterImpl;
import com.ncloudtech.cloudoffice.android.util.EventBusManagerImpl;
import com.ncloudtech.cloudoffice.data.storage.api.File;

import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ChooseFolderFragmentDialog extends DialogFragment implements ChooseFolderView {
    public static final String CURRENT_FOLDER_STR = "currentFolder";
    public static final String SELECTED_FILE_STR = "selectedFile";
    public static final String IS_COPY_STR = "isCopy";

    public static final float MAX_SIZE_FACTOR_HEIGHT = 0.85F;

    @NonNull
    private BreadcrumbsView breadcrumbsView;
    @NonNull
    private ListView fileList;
    @NonNull
    private SimpleFoldersListAdapter filesAdapter;
    /**
     * Presenter reference
     */
    @NonNull
    private ChooseFolderPresenter presenter = ChooseFolderPresenter.STUB;

    public static ChooseFolderFragmentDialog createDialog(@NonNull Fragment targetFragment, @NonNull File rootFolder, @Nullable File clickedFile, boolean isCopy) {
        ChooseFolderFragmentDialog dialog = new ChooseFolderFragmentDialog();
        dialog.setTargetFragment(targetFragment, AppConstants.CHOOSE_FOLDER_REQUEST_CODE);

        Bundle args = new Bundle();
        args.putParcelable(CURRENT_FOLDER_STR, rootFolder);
        args.putParcelable(SELECTED_FILE_STR, clickedFile);
        args.putBoolean(IS_COPY_STR, isCopy);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File rootFolder = (File) getArguments().getParcelable(CURRENT_FOLDER_STR);
        File clickedFile = (File) getArguments().getParcelable(SELECTED_FILE_STR);
        boolean isCopy = getArguments().getBoolean(IS_COPY_STR);

        presenter = new ChooseFolderPresenterImpl(getContext(),
            new ChooseFolderInteractorImpl(new ChooseFolderRepositoryImpl()),
            new EventBusManagerImpl(),
            rootFolder, clickedFile, isCopy);

        View root = LinearLayout.inflate(getContext(), R.layout.choose_folder_dialog, null);

        breadcrumbsView = (BreadcrumbsView) root.findViewById(R.id.breadcrumb_view);
        breadcrumbsView.setOnValueClickedListener(new BreadcrumbsBaseView.OnBreadcrumbClickedListener<File>() {
            @Override
            public void onBreadcrumbClicked(File file) {
                presenter.onFolderChanged(file);
            }
        });

        // FIXME: Make refactoring to Recycler View.
        fileList = (ListView) root.findViewById(R.id.list_view);
        filesAdapter = new SimpleFoldersListAdapter(getContext(), clickedFile);
        fileList.setAdapter(filesAdapter);
        fileList.setOnItemClickListener((parent, view, position, id) ->
            presenter.onFolderChanged(filesAdapter.getItem(position)));

        final Dialog dialog = new MaterialDialog.Builder(getContext(), R.string.choose_folder,
            (isCopy) ? R.string.copy : R.string.move)
            .setCustomView(root)
            .limitDialogMaxSizes(true)
            .maxSizeFactorHeight(MAX_SIZE_FACTOR_HEIGHT)
            .negativeText(R.string.cancel)
            .setPositiveClickListener(() -> {
                presenter.onPositiveButtonClicked();
                return true;
            })
            .onCancelListener(dlg -> presenter.onNegativeButtonClicked())
            .addAction(R.drawable.ic_move_popover_new_folder, v -> presenter.onFolderCreated())
            .build();

        dialog.setOnKeyListener((dialog1, keyCode, event) -> presenter.onKeyEvent(keyCode, event));

        presenter.bindView(this);
        presenter.onFolderChanged(rootFolder);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        presenter.unbindView();
    }

    @Override
    public void updateViews(@NonNull List<File> files, @NonNull List<File> parents) {
        filesAdapter.addData(files);
        fileList.setSelection(0);
        breadcrumbsView.setFolderList(parents);
        breadcrumbsView.update();
    }

    @Override
    public void setActivityResult(@NonNull Intent intent, boolean isOk) {
        getTargetFragment().onActivityResult(getTargetRequestCode(),
            isOk ? RESULT_OK : RESULT_CANCELED, intent);
    }

    @Override
    public void showErrorMessage(int messageId) {
        AndroidHelper.showToast(messageId);
    }

    @Override
    public void showErrorMessage(@NonNull String message) {
        AndroidHelper.showToast(message);
    }
}
