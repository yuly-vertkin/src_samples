/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.myoffice.toolbox;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ncloudtech.cloudoffice.R;
import com.ncloudtech.cloudoffice.android.common.analytics.AnalyticsInteractorImpl;
import com.ncloudtech.cloudoffice.android.common.util.AndroidHelper;
import com.ncloudtech.cloudoffice.android.myoffice.core.model.ExtCharacterProperties;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static com.ncloudtech.cloudoffice.android.common.analytics.AnalyticsInteractor.AnalyticsFlags.FROM_SMART_TOOLBOX;
import static com.ncloudtech.cloudoffice.ndk.textformatting.AlignmentType.CENTER;
import static com.ncloudtech.cloudoffice.ndk.textformatting.AlignmentType.LEFT;
import static com.ncloudtech.cloudoffice.ndk.textformatting.AlignmentType.RIGHT;
import static com.ncloudtech.cloudoffice.ndk.textformatting.ListMarkerType.BulletCircleSolid;
import static com.ncloudtech.cloudoffice.ndk.textformatting.ListMarkerType.EnumeratorDecimalBracket;

public class SmartToolboxPresenter implements ISmartToolboxPresenter {
    @NonNull
    private Context context;
    @NonNull
    private ISmartToolboxView toolboxView;
    @NonNull
    protected CompositeSubscription updateUISubscriptions = new CompositeSubscription();
    @Nullable
    private ExtCharacterProperties lastProperties;
    @NonNull
    private PublishSubject<ToolboxCommand> toolboxCommandPublishSubject = PublishSubject.create();
    @NonNull
    private final ToolboxCommand.CommandFactory commandFactory;

    public SmartToolboxPresenter(@NonNull Context context, @NonNull ISmartToolboxView toolboxView) {
        this.context = context;
        this.toolboxView = toolboxView;

        commandFactory = new CommandFactory(new TextKeyFactory(), new AnalyticsInteractorImpl(FROM_SMART_TOOLBOX));
    }

    @Override
    @NonNull
    public Observable<ToolboxCommand> getToolboxCommandObservable() {
        return toolboxCommandPublishSubject.asObservable();
    }

    @Override
    public void onClickButton(int id) {
        toolboxCommandPublishSubject.onNext(commandFactory.createCommand(CommandEnum.getCommandId(id)));
    }

    @Override
    public void setUpdateUIObservable(@Nullable Observable<ExtCharacterProperties> observable) {
        if (observable != null) {
            final Subscription subscription = observable.subscribe(properties -> {
                lastProperties = properties;
                setProperties(properties);
            });
            updateUISubscriptions.add(subscription);
        }
    }

    @Override
    public void unbind() {
        updateUISubscriptions.clear();
    }

    @Override
    public void onUpdateProperties() {
        setProperties(lastProperties);
    }

    private void setProperties(@Nullable ExtCharacterProperties properties) {
        if (properties != null) {
            toolboxView.setViewSelected(R.id.bold, properties.bold);
            toolboxView.setViewSelected(R.id.italic, properties.italic);
            toolboxView.setViewSelected(R.id.underline, properties.underline);
            toolboxView.setViewSelected(R.id.strikethrough, properties.strikethrough);
            toolboxView.setViewSelected(R.id.alignment_left, properties.alignment == LEFT);
            toolboxView.setViewSelected(R.id.alignment_center, properties.alignment == CENTER);
            toolboxView.setViewSelected(R.id.alignment_right, properties.alignment == RIGHT);
            toolboxView.setViewSelected(R.id.bulleted_list, properties.markerType == BulletCircleSolid);
            toolboxView.setViewSelected(R.id.numbered_list, properties.markerType == EnumeratorDecimalBracket);
            toolboxView.setViewEnabled(R.id.decrease_indent, properties.canDecreaseListLevel);
            toolboxView.setViewEnabled(R.id.increase_indent, properties.canIncreaseListLevel);
        }
    }

    @Override
    public void show() {
        toolboxView.setVisibility(AndroidHelper.isSmartphone(context));
    }
    @Override
    public void hide() {
        toolboxView.setVisibility(false);
    }
}
