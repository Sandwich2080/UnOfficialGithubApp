package com.anly.githubapp.ui.base;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import com.anly.githubapp.ui.widget.loading.LoadingView;
import com.anly.mvp.lce.LoadView;

/**
 * Created by mingjun on 16/8/9.
 */
public abstract class BaseLoadingActivity extends BaseActivity implements LoadView {

    private LoadingView mLoadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadingView = new LoadingView(this, getLoadingMessage());
    }

    @Override
    public void showLoading() {
        mLoadingView.show();
    }

    @Override
    public void dismissLoading() {
        mLoadingView.dismiss();
    }

    public abstract String getLoadingMessage();

    @Override
    public void error(Throwable e) {
        Snackbar.make(getWindow().getDecorView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
    }
}
