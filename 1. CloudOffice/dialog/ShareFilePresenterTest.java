/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.ex.chips2.Contact;
import com.android.ex.chips2.RecipientEntry;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.sharefile.IShareFileInteractor;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.model.ShareFileDataModel;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.sharefile.view.IShareFileView;
import com.ncloudtech.cloudoffice.tool.RxJavaTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
public class ShareFilePresenterTest {

    private IShareFilePresenter shareFilePresenter;
    @Mock
    private IShareFileInteractor shareFileInteractor;
    @Mock
    private IShareFileView shareFileView;
    @Captor
    ArgumentCaptor<ShareFileDataModel> argumentCaptor;
    @Mock
    Context context;
    private List<Contact> contacts;

    @Before
    public void beforeEachTest() {

        shareFilePresenter = new ShareFilePresenter(context, shareFileInteractor);
        shareFilePresenter.bindView(shareFileView);

        contacts = new ArrayList<>();
        contacts.add(new Contact("1", "mikem@beta.myoffice.ru", "mike", "", "m"));
        contacts.add(new Contact("2", "ncoandroid@beta.myoffice.ru", "nco", "", "android"));
        contacts.add(new Contact("3", "", "test_group"));
    }


    @Test
    public void getContacts_success() throws Exception {
        when(shareFileInteractor.getContacts("")).thenReturn(Observable.just(contacts));

        // create TestSubscriber
        TestSubscriber<List<Contact>> testSubscriber = TestSubscriber.create();
        shareFilePresenter.getContacts("").subscribe(testSubscriber);

        // verify interactor was called
        verify(shareFileInteractor).getContacts("");

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
    public void getContacts_error() throws Exception {
        when(shareFileInteractor.getContacts("")).thenReturn(Observable.just(Collections.emptyList()));

        // create TestSubscriber
        TestSubscriber<List<Contact>> testSubscriber = TestSubscriber.create();
        shareFilePresenter.getContacts("").subscribe(testSubscriber);

        // verify interactor was called
        verify(shareFileInteractor).getContacts("");

        testSubscriber.awaitTerminalEvent();
        // test no errors was not occurred
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        // test of the received contacts
        List<Contact> contacts = testSubscriber.getOnNextEvents().get(0);
        assertEquals(contacts.size(), 0);
    }

    @Test
    public void positiveButtonClicked() throws Exception {
        shareFilePresenter.positiveButtonClicked(new ArrayList<RecipientEntry>());

        // verify interactor was called
        verify(shareFileInteractor).shareFile(argumentCaptor.capture());
    }
}