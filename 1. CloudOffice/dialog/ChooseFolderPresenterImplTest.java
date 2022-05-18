/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.presenter;

import android.content.Context;

import com.ncloudtech.cloudoffice.android.network.myfm.FilesEvents;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.choosefolder.ChooseFolderInteractor;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.model.ChooseFolderDataModel;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.view.ChooseFolderView;
import com.ncloudtech.cloudoffice.android.util.EventBusManager;
import com.ncloudtech.cloudoffice.data.storage.api.File;
import com.ncloudtech.cloudoffice.tool.RxJavaTestRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RxJavaTestRunner.class)
public class ChooseFolderPresenterImplTest {
    private ChooseFolderPresenterImpl presenter;
    @Mock
    private ChooseFolderInteractor interactor;
    @Mock
    private ChooseFolderView fileView;
    @Mock
    private EventBusManager eventBusManager;
    @Mock
    private Context context;
    private List<File> files;
    private ChooseFolderDataModel dataModel;
    private File currentFolder;

    @Before
    public void setUp() throws Exception {
        presenter = new ChooseFolderPresenterImpl(context, interactor, eventBusManager, new File(), new File(), true);
        presenter.bindView(fileView);

        files = new ArrayList<>();
        files.add(new File().withId("1").withFilename("a").withParentId("0"));
        files.add(new File().withId("2").withFilename("b").withParentId("0"));
        files.add(new File().withId("3").withFilename("c").withParentId("0"));

        currentFolder = new File().withId("0").withFilename("currentFolder");

        dataModel = new ChooseFolderDataModel(currentFolder, files, files);
    }

    @After
    public void tearDown() {
        presenter.unbindView();
    }

    @Test
    public void onFolderChanged_Success() throws Exception {
        when(interactor.getData(anyObject())).thenReturn(Observable.just(dataModel));

        presenter.onFolderChanged(currentFolder);

        // verify repository and view was called
        verify(interactor).getData(anyObject());
        verify(fileView).updateViews(anyList(), anyList());

        assertEquals(files, dataModel.getChildren());
        assertEquals(files, dataModel.getParents());
    }

    @Test
    public void onFolderChanged_Error() throws Exception {
        when(interactor.getData(anyObject())).thenReturn(Observable.error(new Exception()));

        presenter.onFolderChanged(currentFolder);

        // verify repository and view was called
        verify(interactor).getData(anyObject());
        verify(fileView).showErrorMessage(anyInt());
    }

    @Test
    public void bindView() throws Exception {
        verify(eventBusManager).register(anyObject());
    }

    @Test
    public void unbindView() throws Exception {
        presenter.unbindView();
        verify(eventBusManager).unregister(anyObject());
    }

    @Test
    public void onFolderCreated() throws Exception {
        presenter.onFolderCreated();
        verify(eventBusManager).post(anyObject());
    }

    @Test
    public void onEventMainThread_AddFileToAdapterEvent() throws Exception {
        FilesEvents.AddFileToAdapterEvent event = mock(FilesEvents.AddFileToAdapterEvent.class);
        when(interactor.getData(anyObject())).thenReturn(Observable.just(dataModel));

        presenter.onEventMainThread(event);
        verify(interactor).getData(anyObject());
        verify(eventBusManager).post(anyObject());
    }

    @Test
    public void onPositiveButtonClicked_Error() throws Exception {
        when(interactor.validateDestinationFolder(anyObject())).thenReturn(false);

        presenter.onPositiveButtonClicked();
        verify(fileView).showErrorMessage(anyInt());
    }
}