package com.gastro.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AnimationUtil {

    public static void applyButtonAnimation(FloatingActionButton fab, final Context context, final Runnable action) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator jumpAnimation = ObjectAnimator.ofFloat(fab, "translationY", 0f, 30f, 0f);
                jumpAnimation.setDuration(300);

                jumpAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        action.run();
                    }
                });

                jumpAnimation.start();
            }
        });
    }
}