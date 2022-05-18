/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Rfc822Tokenizer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.ex.chips2.BaseRecipientAdapter;
import com.android.ex.chips2.Contact;
import com.android.ex.chips2.ContactList;
import com.android.ex.chips2.RecipientEditTextView;
import com.ncloudtech.cloudoffice.R;
import com.ncloudtech.cloudoffice.android.common.analytics.Analytics;
import com.ncloudtech.cloudoffice.android.common.analytics.Keys;
import com.ncloudtech.cloudoffice.android.common.util.AndroidHelper;
import com.ncloudtech.cloudoffice.android.common.widgets.dialog.MaterialDialog;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.sharefile.EmailValidator;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.sharefile.ShareFileInteractor;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.sharefile.ShareFileInteractorCache;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.data.repository.sharefile.ShareFileRepository;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.model.ShareFileDataModel;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.presenter.IShareFilePresenter;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.presenter.ShareFilePresenter;

import java.util.List;

public class ShareFileFragmentDialog extends DialogFragment implements IShareFileView {
    private RecipientEditTextView input;
    private Spinner spinner;
    private CheckBox checkBox;
    private TextView positiveButton;
    private IShareFilePresenter presenter = IShareFilePresenter.STUB;

    public static ShareFileFragmentDialog createDialog(@NonNull Context context, @NonNull final String fileId) {
        return createDialog(context, fileId, "", "");
    }

    public static ShareFileFragmentDialog createDialog(@NonNull Context context, @NonNull final String fileId, @Nullable final String userId, @Nullable final String userName) {
        ShareFileFragmentDialog dialog = new ShareFileFragmentDialog();

        Bundle args = new Bundle();
        args.putString("fileId", fileId);
        args.putString("userId", userId);
        args.putString("userName", userName);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String fileId = getArguments().getString("fileId");
        String userId = getArguments().getString("userId");
        String userName = getArguments().getString("userName");

        presenter = new ShareFilePresenter(getContext(),
            new ShareFileInteractor(getContext(),
                new ShareFileRepository(),
                new ShareFileInteractorCache(getContext()),
                fileId),
            userId, userName);
    }

    @Override
    public void setEnableSendButton(boolean enable) {
        positiveButton.setEnabled(enable);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View customView = LayoutInflater.from(getContext()).inflate(R.layout.share_file_dialog, null);

        input = (RecipientEditTextView) customView.findViewById(R.id.input);
        input.setTokenizer(new Rfc822Tokenizer());

        BaseRecipientAdapter adapter = new BaseRecipientAdapter(getContext(), new ContactList() {
            @Override
            public List<Contact> getContacts(String filterStr) {
                return presenter.getContacts(filterStr).toBlocking().first();
            }
        });
        input.setAdapter(adapter);
        input.setValidator(new EmailValidator());

        spinner = (Spinner) customView.findViewById(R.id.spinner);

        String editingStr = getContext().getString(R.string.rights_editing_full);
        String viewingStr = getContext().getString(R.string.rights_viewing);
        final ArrayAdapter<String> spinnerStringAdapter = new ArrayAdapter<String>(getContext(),
            R.layout.simple_spinner_item, new String[] {editingStr, viewingStr});
        spinnerStringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerStringAdapter);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listenFields();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        checkBox = (CheckBox) customView.findViewById(R.id.checkbox);
        checkBox.setChecked(true);
        checkBox.setOnClickListener(v -> listenFields());

        MaterialDialog dialog = new MaterialDialog.Builder(getContext(), R.string.add_user, R.string.add)
            .setCustomView(customView)
            .negativeText(R.string.cancel)
            .cancelableByTouchOutside(false)
            .setPositiveClickListener(() -> {
                pushInputText();
                presenter.positiveButtonClicked(input.getSelectedRecipients());
                return true;
            })
            .onCancelListener(dlg -> {
                Analytics.log(Keys.FM_SHARE_CANCEL);
                presenter.negativeButtonClicked();
            })
            .limitDialogMaxSizes(false)
            .build();

        dialog.setOnCancelListener(dialogInterface -> Analytics.log(Keys.FM_SHARE_CANCEL));
        presenter.bindView(this);

        dialog.setOnShowListener(dialogInterface -> {
            positiveButton = dialog.getPositiveButton();
            presenter.dialogShown();
        });

        addTextVerifier();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        return dialog;
    }

    private void listenFields() {
        String editingStr = getContext().getString(R.string.rights_editing_full);
        String spinnerValue = (String) spinner.getSelectedItem();
        ShareFileDataModel.UserRights rights = (editingStr.equals(spinnerValue)) ?
            ShareFileDataModel.UserRights.EDITING : ShareFileDataModel.UserRights.VIEWING;
        presenter.listenFields(input.getText().toString(), rights, checkBox.isChecked());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        presenter.unbindView();
    }

    @Override
    public void initInput(@NonNull final String userId, @NonNull final String userName) {
        if (!TextUtils.isEmpty(userId)) {
            input.appendRecipient(userId, userName);
        }
    }

    /**
     * We need to add space to input that create chip from entered text
     */
    private void pushInputText() {
        input.setSelection(input.length());
        input.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE, 0));
        input.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE, 0));
    }

    @Override
    public void hideKeyboard() {
        AndroidHelper.hideSoftKeyboard(input);
    }

    private void addTextVerifier() {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                listenFields();
            }
        });
    }
}
