/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.network.myoffice.dialog.business.choosefolder;

import android.support.annotation.NonNull;

import com.ncloudtech.cloudoffice.android.network.api.data.rest.FileResource;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.data.repository.choosefolder.ChooseFolderRepository;
import com.ncloudtech.cloudoffice.android.network.myoffice.dialog.ui.choosefolder.model.ChooseFolderDataModel;
import com.ncloudtech.cloudoffice.data.storage.api.File;
import com.ncloudtech.cloudoffice.tool.RxJavaTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RxJavaTestRunner.class)
public class ChooseFolderInteractorImplTest {
    private ChooseFolderInteractorImplWrap interactor;
    @Mock
    private ChooseFolderRepository repository;
    @Captor
    private ArgumentCaptor<File> argumentCaptor;
    private List<File> files;
    private List<FileResource> fileResources;
    private File selectedFile;

    @Before
    public void setUp() throws Exception {
        interactor = new ChooseFolderInteractorImplWrap(repository);

        files = new ArrayList<>();
        files.add(new File().withId("1").withFilename("a").withParentId("0"));
        files.add(new File().withId("2").withFilename("b").withParentId("0"));
        files.add(new File().withId("3").withFilename("c").withParentId("0"));

        fileResources = new ArrayList<>();
        for(File file : files) {
            fileResources.add(new FileResource(file));
        }

        selectedFile = new File().withId("4").withFilename("selFile");
    }

    @Test
    public void getData_Success() throws Exception {
        when(repository.loadFiles("")).thenReturn(Observable.just(fileResources));
        when(repository.getFileParents(argumentCaptor.capture())).thenReturn(Observable.just(files));

        // create TestSubscriber
        TestSubscriber<ChooseFolderDataModel> testSubscriber = TestSubscriber.create();
        interactor.getData(selectedFile).subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        // test no errors was not occurred
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        // test of the received data
        ChooseFolderDataModel dataModel = testSubscriber.getOnNextEvents().get(0);
        List<File> files = dataModel.getChildren();
        assertEquals(files.size(), 0);

        dataModel = testSubscriber.getOnNextEvents().get(1);
        files = dataModel.getChildren();
        assertEquals(files.size(), 3);
        assertEquals(files.get(0).getFilename(), "a");
        assertEquals(files.get(1).getFilename(), "b");
        assertEquals(files.get(2).getFilename(), "c");

        files = dataModel.getParents();
        assertEquals(files.size(), 4);
        assertEquals(files.get(0).getFilename(), "a");
        assertEquals(files.get(1).getFilename(), "b");
        assertEquals(files.get(2).getFilename(), "c");
        assertEquals(files.get(3).getFilename(), "selFile");
    }

    @Test
    public void getParentFile() throws Exception {
        when(repository.getParentFile(anyObject())).thenReturn(Observable.just(new File().withId("1").withFilename("a").withParentId("0")));

        // create TestSubscriber
        TestSubscriber<File> testSubscriber = TestSubscriber.create();
        interactor.getParentFile(anyObject()).subscribe(testSubscriber);
        verify(repository).getParentFile(anyObject());

        testSubscriber.awaitTerminalEvent();
        // test no errors was not occurred
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();

        // test of the received data
        File file = testSubscriber.getOnNextEvents().get(0);
        assertEquals(file.getFilename(), "a");
    }

    @Test
    public void validateDestinationFolder() throws Exception {
        when(repository.getFileParents(argumentCaptor.capture())).thenReturn(Observable.just(files));

        assertTrue(interactor.validateDestinationFolder(selectedFile));
        assertFalse(interactor.validateDestinationFolder(files.get(0)));
    }

    class ChooseFolderInteractorImplWrap extends ChooseFolderInteractorImpl {

        public ChooseFolderInteractorImplWrap(@NonNull ChooseFolderRepository repository) {
            super(repository);
        }

        @NonNull
        @Override
        protected List<File> getFilesFromDB(@NonNull File file) {
            return new ArrayList<File>();
        }

        @Override
        protected void saveFilesInDb(@NonNull File file, @NonNull List<File> files) {
        }

        @NonNull
        @Override
        protected String prepareFilesIDQuery(@NonNull File file) {
            return "";
        }
    }
}