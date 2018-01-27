package com.example.mohammad.tennisclub;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

/**
 * Created by mohammad on 16/01/18.
 */

public class FABBehaviour extends CoordinatorLayout.Behavior<FloatingActionMenu> {

    public FABBehaviour(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
            child.setTranslationY(translationY);
        }
        return true;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            float translationY = Math.max(0, dependency.getTranslationY() - dependency.getHeight());
            child.setTranslationY(translationY);
        }

    }
}
