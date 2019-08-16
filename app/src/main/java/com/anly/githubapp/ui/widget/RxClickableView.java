package com.anly.githubapp.ui.widget;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Created by mingjun on 16/7/21.
 */
public class RxClickableView {

    @CheckResult
    @NonNull
    public static Observable<Void> clicks(@NonNull View view) {
        return RxView.clicks(view)
                .throttleFirst(500, TimeUnit.MILLISECONDS);
    }
}
