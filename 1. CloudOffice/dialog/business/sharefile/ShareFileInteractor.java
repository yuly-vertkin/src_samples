/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.sharefile;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.android.ex.chips2.Contact;
import com.ncloudtech.cloudoffice.R;
import com.ncloudtech.cloudoffice.android.common.analytics.Analytics;
import com.ncloudtech.cloudoffice.android.common.analytics.Keys;
import com.ncloudtech.cloudoffice.android.common.util.AndroidHelper;
import com.ncloudtech.cloudoffice.android.network.api.AuthAPI;
import com.ncloudtech.cloudoffice.android.network.myfm.FilesEvents;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.data.repository.sharefile.IShareFileRepository;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.model.ShareFileDataModel;
import com.ncloudtech.cloudoffice.data.storage.api.AccessRole;
import com.ncloudtech.cloudoffice.data.storage.api.AddFilePermissionsRequest;
import com.ncloudtech.cloudoffice.data.storage.api.Permission;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShareFileInteractor implements IShareFileInteractor {
    private static final String SERVER_CONTACTS_UPDATE_TIME = "avatarUpdateTime";
    private final int SERVER_CONTACTS_UPDATE_PERIOD = (int) DateUtils.HOUR_IN_MILLIS;

    @NonNull
    protected Context context;
    @NonNull
    protected IShareFileRepository repository;
    @NonNull
    protected ShareFileInteractorCache cache;
    @NonNull
    protected String fileId;

    public ShareFileInteractor(@NonNull Context context, @NonNull IShareFileRepository repository, @NonNull ShareFileInteractorCache cache, @NonNull String fileId) {
        this.context = context;
        this.repository = repository;
        this.cache = cache;
        this.fileId = fileId;
    }

    @Override
    public @NonNull Observable<List<Contact>> getContacts(String filterStr) {
        Observable<List<Contact>> result = !isContactsNeedUpdate() ? cache.getServerContacts() : loadContacts();
        return result
            .flatMap(contacts -> Observable.from(contacts))
            .filter(c -> !TextUtils.isEmpty(c.getEmail()) && !TextUtils.isEmpty(c.getFirstName()) && c.getFirstName().toLowerCase().contains(filterStr) ||
                        !TextUtils.isEmpty(c.getEmail()) && !TextUtils.isEmpty(c.getLastName()) && c.getLastName().toLowerCase().contains(filterStr) ||
                        !TextUtils.isEmpty(c.getEmail()) && c.getEmail().toLowerCase().contains(filterStr) ||
                        !TextUtils.isEmpty(c.getId()) && !TextUtils.isEmpty(c.getGroupName()) && c.getGroupName().toLowerCase().contains(filterStr))
            .toList();
    }

    protected @NonNull Observable<List<Contact>> loadContacts() {
        return Observable.concat(
            repository.getContacts()
                .onErrorReturn(throwable -> new AuthAPI.ContactsResponse(Collections.emptyList(), Collections.emptyList()))
                .flatMap(contactsResponse ->
                    Observable.concat(
                        Observable.from(contactsResponse.getPersonal()),
                        Observable.from(contactsResponse.getCorporate()))
                    .map(c -> new Contact(c.email, c.email, c.first_name, c.middle_name, c.last_name))),
            repository.getGroups()
                .onErrorReturn(throwable -> Collections.emptyList())
                .flatMap(groups ->
                    Observable.from(groups)
                    .map(g -> new Contact(g.id, g.email, g.name))))
            .toList()
            .doOnNext(this::saveContactsInCache);
    }

    protected boolean isContactsNeedUpdate() {
        long lastUpdateTime = AndroidHelper.getDefaultSharedPreferences(context).getLong(SERVER_CONTACTS_UPDATE_TIME, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastUpdateTime);
        calendar.add(Calendar.MILLISECOND, SERVER_CONTACTS_UPDATE_PERIOD);

        return new Date().after(calendar.getTime());
    }

    protected void saveContactsInCache(@NonNull List<Contact> contacts){
        setContactsUpdateTime(context, System.currentTimeMillis());
        cache.saveServerContacts(contacts);
    }

    public static void setContactsUpdateTime(@NonNull Context context, long updateTime) {
        SharedPreferences sp = AndroidHelper.getDefaultSharedPreferences(context);
        sp.edit().putLong(SERVER_CONTACTS_UPDATE_TIME, updateTime).apply();
    }

    @Override
    public void shareFile(@NonNull ShareFileDataModel shareFileData) {
        if (shareFileData.hasContacts()) {
            repository.shareFile(fileId, getFilePermissionsRequest(shareFileData))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    EventBus.getDefault().post(new FilesEvents.FileSharedEvent());
                }, throwable -> {
                    EventBus.getDefault().post(new FilesEvents.FileSharedEvent(new ShareFileException(throwable, shareFileData.getContacts().size())));
                });
        } else {
            Analytics.log(Keys.FM_SHARE_GO_CONFLICT);
            AndroidHelper.showToast(context, R.string.error_no_user);
        }
    }

    @Override
    public boolean enableSendButton(@Nullable String inputText) {
        return !TextUtils.isEmpty(inputText);
    }

    @NonNull
    protected AddFilePermissionsRequest getFilePermissionsRequest(@NonNull ShareFileDataModel shareFileData) {
        List<Permission> permissions = new ArrayList<>();

        for (Contact contact : shareFileData.getContacts()) {
            Permission permission = new Permission();
            permission.setId(contact.getId());
            permission.setEmail(contact.getEmail());
            permission.setIsGroup(contact.isGroup());
            AccessRole role = (shareFileData.getRights() == ShareFileDataModel.UserRights.EDITING) ?
                AccessRole.EDITOR : AccessRole.VIEWER;
            permission.setRole(role);
            permissions.add(permission);
        }

        return new AddFilePermissionsRequest()
            .withPermissions(permissions)
            .withSendEmail(shareFileData.isSendByEmail());
    }

}
