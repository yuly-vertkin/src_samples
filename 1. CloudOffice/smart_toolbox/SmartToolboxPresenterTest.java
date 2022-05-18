/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.myoffice.toolbox;

import android.content.Context;

import com.ncloudtech.cloudoffice.R;
import com.ncloudtech.cloudoffice.android.myoffice.core.JavaDocumentEditor;
import com.ncloudtech.cloudoffice.android.myoffice.core.model.ExtCharacterProperties;
import com.ncloudtech.cloudoffice.tool.RxJavaTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static com.ncloudtech.cloudoffice.ndk.textformatting.AlignmentType.CENTER;
import static com.ncloudtech.cloudoffice.ndk.textformatting.AlignmentType.LEFT;
import static com.ncloudtech.cloudoffice.ndk.textformatting.AlignmentType.RIGHT;
import static com.ncloudtech.cloudoffice.ndk.textformatting.ListMarkerType.BulletCircleSolid;
import static com.ncloudtech.cloudoffice.ndk.textformatting.ListMarkerType.EnumeratorDecimalBracket;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(RxJavaTestRunner.class)
public class SmartToolboxPresenterTest {
    private ISmartToolboxPresenter presenter;
    @Mock
    private ISmartToolboxView view;
    @Mock
    private Context context;
    private Observable<ExtCharacterProperties> editorStateObservable;
    private Subscriber<? super ExtCharacterProperties> editorStateSubscriber;
    @Mock
    private JavaDocumentEditor editor;
    private TestSubscriber<ToolboxCommand> testSubscriber;

    @Before
    public void setUp() throws Exception {
        presenter = new SmartToolboxPresenter(context, view);

        editorStateObservable = Observable.create(new Observable.OnSubscribe<ExtCharacterProperties>() {
            @Override
            public void call(Subscriber<? super ExtCharacterProperties> subscriber) {
                editorStateSubscriber = subscriber;
            }
        });

        // create TestSubscriber
        testSubscriber = TestSubscriber.create();
        presenter.getToolboxCommandObservable().subscribe(testSubscriber);
    }

    @Test
    public void onClickButtonBold() throws Exception {
        createVerificationEditor(R.id.bold).toggleBold();
    }

    @Test
    public void onClickButtonItalic() throws Exception {
        createVerificationEditor(R.id.italic).toggleItalic();
    }

    @Test
    public void onClickButtonUnderline() throws Exception {
        createVerificationEditor(R.id.underline).toggleUnderline();
    }

    @Test
    public void onClickButtonStrikethrough() throws Exception {
        createVerificationEditor(R.id.strikethrough).toggleStrikethrough();
    }

    @Test
    public void onClickButtonAlignmentLeft() throws Exception {
        createVerificationEditor(R.id.alignment_left).setAlignment(LEFT + 1);
    }

    @Test
    public void onClickButtonAlignmentCenter() throws Exception {
        createVerificationEditor(R.id.alignment_center).setAlignment(CENTER + 1);
    }

    @Test
    public void onClickButtonAlignmentRight() throws Exception {
        createVerificationEditor(R.id.alignment_right).setAlignment(RIGHT + 1);
    }

    @Test
    public void onClickButtonBulletedList() throws Exception {
        createVerificationEditor(R.id.bulleted_list).setListMarkerType(BulletCircleSolid);
    }

    @Test
    public void onClickButtonNumberedList() throws Exception {
        createVerificationEditor(R.id.numbered_list).setListMarkerType(EnumeratorDecimalBracket);
    }

    @Test
    public void onClickButtonDecreaseIndent() throws Exception {
        createVerificationEditor(R.id.decrease_indent).decreaseListLevel();
    }

    @Test
    public void onClickButtonIncreaseIndent() throws Exception {
        createVerificationEditor(R.id.increase_indent).increaseListLevel();
    }

    private JavaDocumentEditor createVerificationEditor(int btnId) {
        presenter.onClickButton(btnId);
        testSubscriber.getOnNextEvents().get(0).execute(editor);
        return verify(editor);
    }

    @Test
    public void setUpdateUIObservable() throws Exception {
        ExtCharacterProperties props = new ExtCharacterProperties();
        presenter.setUpdateUIObservable(editorStateObservable);

        props.bold = true;
        editorStateSubscriber.onNext(props);
        verify(view).setViewSelected(R.id.bold, true);
        verify(view).setViewSelected(R.id.italic, false);
        verify(view).setViewSelected(R.id.underline, false);
        reset(view);
        props.bold = false;

        props.italic = true;
        editorStateSubscriber.onNext(props);
        verify(view).setViewSelected(R.id.bold, false);
        verify(view).setViewSelected(R.id.italic, true);
        verify(view).setViewSelected(R.id.underline, false);
        reset(view);
        props.italic = false;

        props.underline = true;
        editorStateSubscriber.onNext(props);
        verify(view).setViewSelected(R.id.bold, false);
        verify(view).setViewSelected(R.id.italic, false);
        verify(view).setViewSelected(R.id.underline, true);
        reset(view);
        props.underline = false;

        props.strikethrough = true;
        editorStateSubscriber.onNext(props);
        verify(view).setViewSelected(R.id.strikethrough, true);
        reset(view);
        props.strikethrough = false;

        props.alignment = LEFT;
        editorStateSubscriber.onNext(props);
        verify(view).setViewSelected(R.id.alignment_left, true);
        verify(view).setViewSelected(R.id.alignment_center, false);
        verify(view).setViewSelected(R.id.alignment_right, false);
        reset(view);
        props.alignment = 0;

        props.alignment = CENTER;
        editorStateSubscriber.onNext(props);
        verify(view).setViewSelected(R.id.alignment_left, false);
        verify(view).setViewSelected(R.id.alignment_center, true);
        verify(view).setViewSelected(R.id.alignment_right, false);
        reset(view);
        props.alignment = 0;

        props.alignment = RIGHT;
        editorStateSubscriber.onNext(props);
        verify(view).setViewSelected(R.id.alignment_left, false);
        verify(view).setViewSelected(R.id.alignment_center, false);
        verify(view).setViewSelected(R.id.alignment_right, true);
        reset(view);
        props.alignment = 0;

        props.markerType = BulletCircleSolid;
        editorStateSubscriber.onNext(props);
        verify(view).setViewSelected(R.id.bulleted_list, true);
        verify(view).setViewSelected(R.id.numbered_list, false);
        reset(view);
        props.markerType = 0;

        props.markerType = EnumeratorDecimalBracket;
        editorStateSubscriber.onNext(props);
        verify(view).setViewSelected(R.id.bulleted_list, false);
        verify(view).setViewSelected(R.id.numbered_list, true);
        reset(view);
        props.markerType = 0;
    }

    @Test
    public void show() throws Exception {
        presenter.show();
        verify(view).setVisibility(true);
    }

    @Test
    public void hide() throws Exception {
        presenter.hide();
        verify(view).setVisibility(false);
    }

}