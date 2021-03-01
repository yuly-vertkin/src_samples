/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2017
 *
 * You can not use the contents of the file in any way without New Cloud Technologies, Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.android.myoffice.toolbox;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ncloudtech.cloudoffice.R;
import com.ncloudtech.cloudoffice.android.myoffice.core.model.ExtCharacterProperties;
import com.ncloudtech.cloudoffice.android.myoffice.ui.EditorView.ISmartToolboxVisibilityPresenter;

import rx.Observable;

import static com.ncloudtech.cloudoffice.android.util.ToolboxUtils.enableButton;

public class SmartToolboxView extends FrameLayout implements ISmartToolboxView {
    @NonNull
    private ISmartToolboxPresenter presenter;

    public SmartToolboxView(Context context) {
        super(context);
        init();
    }

    public SmartToolboxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmartToolboxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        presenter = new SmartToolboxPresenter(getContext(), this);
        initView();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initView();
        presenter.onUpdateProperties();
    }

    private void initView() {
        removeAllViews();
        View.inflate(getContext(), R.layout.smart_toolbox_text, this);

        setViewOnClickListener(this);
    }

    private void setViewOnClickListener(View v) {
        if (v instanceof ViewGroup) {
            final int count = ((ViewGroup) v).getChildCount();
            for (int i = 0; i < count; i++) {
                setViewOnClickListener(((ViewGroup) v).getChildAt(i));
            }
        } else if (v instanceof ImageView) {
            v.setOnClickListener(this::onClick);
        }
    }

    private void onClick(View v) {
        presenter.onClickButton(v.getId());
    }

    @Override
    public void setViewSelected(int id, boolean selected) {
        View v = findViewById(id);
        if (v instanceof ImageView) {
            v.setSelected(selected);
        }
    }

    @Override
    public void setViewEnabled(int id, boolean enabled) {
        View v = findViewById(id);
        if (v instanceof ImageView) {
            enableButton((ImageView) v, enabled);
        }
    }

    @Override
    public void setVisibility(boolean visible) {
        setVisibility(visible ? VISIBLE : GONE);
    }

    /**
     * Get visibility presenter
     * @return
     */
    @NonNull
    public ISmartToolboxVisibilityPresenter getVisibilityPresenter() {
        return presenter;
    }

    @NonNull
    public Observable<ToolboxCommand> getToolboxCommandObservable() {
        return presenter.getToolboxCommandObservable();
    }

    public void setUpdateUIObservable(@Nullable Observable<ExtCharacterProperties> observable) {
        presenter.setUpdateUIObservable(observable);
    }

    public void unbind() {
        presenter.unbind();
    }
}
