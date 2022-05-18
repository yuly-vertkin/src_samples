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
import com.ncloudtech.cloudoffice.android.network.api.AuthAPI;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.data.repository.sharefile.IShareFileRepository;
import com.ncloudtech.cloudoffice.tool.RxJavaTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RxJavaTestRunner.class)
public class ShareFileInteractorTest {
    private ShareFileInteractorWrap shareFileInteractor;
    @Mock
    private IShareFileRepository shareFileRepository;
    @Mock
    private ShareFileInteractorCache shareFileInteractorCache;
    @Mock
    Context context;
    private List<AuthAPI.Contact> personal, corporate;
    List<AuthAPI.Group> groups;
    private List<Contact> contacts;

    @Before
    public void beforeEachTest() {
        shareFileInteractor = new ShareFileInteractorWrap(context, shareFileRepository, shareFileInteractorCache, "123");

        contacts = new ArrayList<>();
        contacts.add(new Contact("1", "mikem@beta.myoffice.ru", "mike", "", "m"));
        contacts.add(new Contact("2", "ncoandroid@beta.myoffice.ru", "nco", "", "android"));
        contacts.add(new Contact("3", "", "test_group"));

        personal = new ArrayList<>();
        personal.add(new AuthAPI.Contact("mikem@beta.myoffice.ru", "mike", "", "m", ""));
        corporate = new ArrayList<>();
        corporate.add(new AuthAPI.Contact("ncoandroid@beta.myoffice.ru", "nco", "", "android", ""));
        groups = new ArrayList<>();
        groups.add(new AuthAPI.Group("1", "", "test_group", "group"));
    }

    @Test
    public void getContacts_fromNetSuccess() throws Exception {
        when(shareFileRepository.getContacts())
            .thenReturn(Observable.just(new AuthAPI.ContactsResponse(personal, corporate)));
        when(shareFileRepository.getGroups()).thenReturn(Observable.just(groups));
        shareFileInteractor.setContactsNeedUpdate(true);

        // create TestSubscriber
        TestSubscriber<List<Contact>> testSubscriber = TestSubscriber.create();
        shareFileInteractor.getContacts("").subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        // test no errors was not occurred
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        // test of the received contacts
        List<Contact> contacts = testSubscriber.getOnNextEvents().get(0);
        assertEquals(contacts.size(), 3);
        assertEquals(contacts.get(0).getEmail(), "mikem@beta.myoffice.ru");
        assertEquals(contacts.get(1).getEmail(), "ncoandroid@beta.myoffice.ru");
        assertEquals(contacts.get(2).getGroupName(), "test_group");
    }

    @Test
    public void getContacts_fromNetError() throws Exception {
        when(shareFileRepository.getContacts()).thenReturn(Observable.error(new Exception()));
        when(shareFileRepository.getGroups()).thenReturn(Observable.error(new Exception()));

        shareFileInteractor.setContactsNeedUpdate(true);

        // create TestSubscriber
        TestSubscriber<List<Contact>> testSubscriber = TestSubscriber.create();
        shareFileInteractor.getContacts("").subscribe(testSubscriber);

        // verify repository was called
        verify(shareFileRepository).getContacts();

        testSubscriber.awaitTerminalEvent();
        // test no errors was not occurred
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        // test of the received contacts
        List<Contact> contacts = testSubscriber.getOnNextEvents().get(0);
        assertEquals(contacts.size(), 0);
    }

    @Test
    public void getContacts_fromCacheSuccess() throws Exception {
        when(shareFileInteractorCache.getServerContacts()).thenReturn(Observable.just(contacts));

        shareFileInteractor.setContactsNeedUpdate(false);

        // create TestSubscriber
        TestSubscriber<List<Contact>> testSubscriber = TestSubscriber.create();
        shareFileInteractor.getContacts("").subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        // test no errors was not occurred
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        // test of the received contacts
        List<Contact> contacts = testSubscriber.getOnNextEvents().get(0);
        assertEquals(contacts.size(), 3);
        assertEquals(contacts.get(0).getEmail(), "mikem@beta.myoffice.ru");
        assertEquals(contacts.get(1).getEmail(), "ncoandroid@beta.myoffice.ru");
        assertEquals(contacts.get(2).getGroupName(), "test_group");
    }

    @Test
    public void getContacts_fromCacheError() throws Exception {
        when(shareFileInteractorCache.getServerContacts()).thenReturn(Observable.just(Collections.emptyList()));

        shareFileInteractor.setContactsNeedUpdate(false);

        // create TestSubscriber
        TestSubscriber<List<Contact>> testSubscriber = TestSubscriber.create();
        shareFileInteractor.getContacts("").subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        // test no errors was not occurred
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        // test of the received contacts
        List<Contact> contacts = testSubscriber.getOnNextEvents().get(0);
        assertEquals(contacts.size(), 0);
    }

    class ShareFileInteractorWrap extends ShareFileInteractor {
        private boolean isContactsNeedUpdate;

        public ShareFileInteractorWrap(@NonNull Context context, @NonNull IShareFileRepository repository, @NonNull ShareFileInteractorCache cache, @NonNull String fileId) {
            super(context, repository, cache, fileId);
        }

        @Override
        protected boolean isContactsNeedUpdate() {
            return isContactsNeedUpdate;
        }

        public void setContactsNeedUpdate(boolean contactsNeedUpdate) {
            isContactsNeedUpdate = contactsNeedUpdate;
        }

        @Override
        public @NonNull Observable<List<Contact>> getContacts(String filterStr) {
            Observable<List<Contact>> result = !isContactsNeedUpdate() ? cache.getServerContacts() : loadContacts();
            return result
                .flatMap(contacts -> Observable.from(contacts))
//                .filter(c -> !TextUtils.isEmpty(c.getEmail()) && !TextUtils.isEmpty(c.getFirstName()) && c.getFirstName().toLowerCase().contains(filterStr) ||
//                    !TextUtils.isEmpty(c.getEmail()) && !TextUtils.isEmpty(c.getLastName()) && c.getLastName().toLowerCase().contains(filterStr) ||
//                    !TextUtils.isEmpty(c.getEmail()) && c.getEmail().toLowerCase().contains(filterStr) ||
//                    !TextUtils.isEmpty(c.getId()) && !TextUtils.isEmpty(c.getGroupName()) && c.getGroupName().toLowerCase().contains(filterStr))
                .toList();
        }

        @Override
        protected void saveContactsInCache(@NonNull List<Contact> contacts) {
        }
    }


}