package br.unb.igor.helpers;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import br.unb.igor.model.Sessao;

public class Utils {

    public static SimpleDateFormat DateFormatter_MMddyy = new SimpleDateFormat("dd/MM/yy");

    public static Comparator<Sessao> ComparatorSessionByDateDesc =
        new Comparator<Sessao>() {
            @Override
            public int compare(Sessao l, Sessao r) {
                try {
                    Date left = DateFormatter_MMddyy.parse(l.getData());
                    Date right = DateFormatter_MMddyy.parse(r.getData());
                    return left.before(right) ? 1 : -1;
                } catch (ParseException e) {
                }
                return 0;
            }
        };

    public static Comparator<Sessao> ComparatorSessionByDateAsc =
        new Comparator<Sessao>() {
            @Override
            public int compare(Sessao l, Sessao r) {
                try {
                    Date left = DateFormatter_MMddyy.parse(l.getData());
                    Date right = DateFormatter_MMddyy.parse(r.getData());
                    return left.after(right) || left.equals(right) ? 1 : -1;
                } catch (ParseException e) {
                }
                return 0;
            }
        };

    public static void expandView(final View v, int duration) {
        v.setVisibility(View.VISIBLE);
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        if (duration <= 0) {
            v.getLayoutParams().height = targetHeight;
            v.requestLayout();
            return;
        }
        v.getLayoutParams().height = 1;
        v.requestLayout();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(duration);
        v.startAnimation(a);
    }

    public static void collapseView(final View v, int duration) {
        if (duration <= 0) {
            v.setVisibility(View.GONE);
            return;
        }
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                v.requestLayout();
                if (interpolatedTime >= 1.f) {
                    v.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(duration);
        v.startAnimation(a);
    }

    public static void fadeOutView(final View v, int duration) {
        Animation fadeOut = new AlphaAnimation(1.f, 0.f);
        fadeOut.setDuration(duration);
        fadeOut.setFillAfter(true);
        fadeOut.setFillEnabled(true);
        v.startAnimation(fadeOut);
    }

    public static void fadeInScaleUpView(final View v, int duration) {
        AnimationSet set = new AnimationSet(true);
        Animation scaleUp = new ScaleAnimation(.72f, 1.f, .72f, 1.f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation fadeIn = new AlphaAnimation(0.f, 1.f);
        scaleUp.setDuration(duration);
        scaleUp.setFillAfter(true);
        scaleUp.setFillEnabled(true);
        fadeIn.setDuration(duration);
        fadeIn.setFillAfter(true);
        fadeIn.setFillEnabled(true);
        set.addAnimation(fadeIn);
        set.addAnimation(scaleUp);
        v.startAnimation(set);
    }
}
