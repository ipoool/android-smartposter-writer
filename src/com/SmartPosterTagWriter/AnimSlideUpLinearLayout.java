package com.SmartPosterTagWriter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class AnimSlideUpLinearLayout extends LinearLayout implements OnGlobalLayoutListener {
   public AnimSlideUpLinearLayout(Context context) {
     super(context);
     getViewTreeObserver().addOnGlobalLayoutListener(this);
   }

   public AnimSlideUpLinearLayout(Context context, AttributeSet attrs) {
     super(context, attrs);
     getViewTreeObserver().addOnGlobalLayoutListener(this);
   }

    @Override
    public void onGlobalLayout() {
      
    	int childCount = 1;
    	
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.startAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.left_up_animation));

        }
    }

}