package com.anly.githubapp.ui.module.repo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.anly.githubapp.GithubApplication;
import com.anly.githubapp.R;
import com.anly.githubapp.data.api.RepoApi;
import com.anly.githubapp.data.model.Repo;
import com.anly.githubapp.data.pref.AccountPref;
import com.anly.githubapp.di.HasComponent;
import com.anly.githubapp.di.component.DaggerRepoComponent;
import com.anly.githubapp.di.component.RepoComponent;
import com.anly.githubapp.di.module.ActivityModule;
import com.anly.githubapp.di.module.RepoModule;
import com.anly.githubapp.presenter.repo.RepoListPresenter;
import com.anly.githubapp.ui.base.BaseLoadingActivity;
import com.anly.githubapp.ui.module.repo.adapter.RepoListRecyclerAdapter;
import com.anly.mvp.lce.LceView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mingjun on 16/8/10.
 */
public class RepoListActivity extends BaseLoadingActivity implements LceView<ArrayList<Repo>>, HasComponent<RepoComponent> {


    @BindView(R.id.repo_list)
    RecyclerView mRepoListView;

    @Inject
    RepoListPresenter mPresenter;

    private RepoListRecyclerAdapter mAdapter;

    private static final String EXTRA_USER_NAME = "extra_user_name";
    private static final String ACTION_REPOS = "com.anly.githubapp.ACTION_REPOS";
    private static final String ACTION_STARRED_REPOS = "com.anly.githubapp.ACTION_STARRED_REPOS";

    public static void launchToShowRepos(Context context, String username) {
        Intent intent = new Intent(context, RepoListActivity.class);
        intent.putExtra(EXTRA_USER_NAME, username);
        intent.setAction(ACTION_REPOS);
        context.startActivity(intent);
    }

    public static void launchToShowStars(Context context, String username) {
        Intent intent = new Intent(context, RepoListActivity.class);
        intent.putExtra(EXTRA_USER_NAME, username);
        intent.setAction(ACTION_STARRED_REPOS);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        setContentView(R.layout.activity_repo_list);
        ButterKnife.bind(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        mPresenter.attachView(this);
        loadData();
    }

    private void loadData() {
        String action = getIntent().getAction();

        String username = getIntent().getStringExtra(EXTRA_USER_NAME);
        boolean isSelf = AccountPref.isSelf(this, username);

        if (ACTION_REPOS.equals(action)) {
            setTitle(isSelf ? getString(R.string.my_repositories) : getString(R.string.repositories, username));
            mPresenter.loadRepos(username, isSelf, RepoApi.OWNER_REPOS);
        }
        else if (ACTION_STARRED_REPOS.equals(action)) {
            setTitle(isSelf ? getString(R.string.my_stars) : getString(R.string.your_stars, username));
            mPresenter.loadRepos(username, isSelf, RepoApi.STARRED_REPOS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    private void initViews() {
        mAdapter = new RepoListRecyclerAdapter(null);
        mAdapter.setOnRecyclerViewItemClickListener(mItemtClickListener);

        mRepoListView.setLayoutManager(new LinearLayoutManager(this));
        mRepoListView.addItemDecoration(new HorizontalDividerItemDecoration
                .Builder(this)
                .color(Color.TRANSPARENT)
                .size(getResources().getDimensionPixelSize(R.dimen.divider_height))
                .build());

        mRepoListView.setAdapter(mAdapter);
    }

    private BaseQuickAdapter.OnRecyclerViewItemClickListener mItemtClickListener = new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Repo repo = mAdapter.getItem(position);
            RepoDetailActivity.launch(RepoListActivity.this, repo.getOwner().getLogin(), repo.getName());
        }
    };

    @Override
    public String getLoadingMessage() {
        return getString(R.string.loading);
    }

    @Override
    public void showContent(ArrayList<Repo> data) {
        mAdapter.setNewData(data);
    }

    @Override
    public void showError(Throwable e) {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public RepoComponent getComponent() {
        return DaggerRepoComponent.builder()
                .applicationComponent(GithubApplication.get(this).getComponent())
                .activityModule(new ActivityModule(this))
                .repoModule(new RepoModule())
                .build();
    }
}
