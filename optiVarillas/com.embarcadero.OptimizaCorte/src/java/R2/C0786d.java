package r2;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.util.ArrayList;
import r.j;

/* renamed from: r2.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0786d {

    /* renamed from: a  reason: collision with root package name */
    public final j<String, C0787e> f5718a = new j<>();

    /* renamed from: b  reason: collision with root package name */
    public final j<String, PropertyValuesHolder[]> f5719b = new j<>();

    public static C0786d a(Context context, int i4) {
        try {
            Animator loadAnimator = AnimatorInflater.loadAnimator(context, i4);
            if (loadAnimator instanceof AnimatorSet) {
                return b(((AnimatorSet) loadAnimator).getChildAnimations());
            }
            if (loadAnimator == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(loadAnimator);
            return b(arrayList);
        } catch (Exception e4) {
            Log.w("MotionSpec", "Can't load animation resource ID #0x" + Integer.toHexString(i4), e4);
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v1, types: [r2.e, java.lang.Object] */
    public static C0786d b(ArrayList arrayList) {
        C0786d c0786d = new C0786d();
        int size = arrayList.size();
        for (int i4 = 0; i4 < size; i4++) {
            Animator animator = (Animator) arrayList.get(i4);
            if (animator instanceof ObjectAnimator) {
                ObjectAnimator objectAnimator = (ObjectAnimator) animator;
                c0786d.f5719b.put(objectAnimator.getPropertyName(), objectAnimator.getValues());
                String propertyName = objectAnimator.getPropertyName();
                long startDelay = objectAnimator.getStartDelay();
                long duration = objectAnimator.getDuration();
                TimeInterpolator interpolator = objectAnimator.getInterpolator();
                if (!(interpolator instanceof AccelerateDecelerateInterpolator) && interpolator != null) {
                    if (interpolator instanceof AccelerateInterpolator) {
                        interpolator = C0783a.f5711c;
                    } else if (interpolator instanceof DecelerateInterpolator) {
                        interpolator = C0783a.f5712d;
                    }
                } else {
                    interpolator = C0783a.f5710b;
                }
                ?? obj = new Object();
                obj.f5723d = 0;
                obj.f5724e = 1;
                obj.f5720a = startDelay;
                obj.f5721b = duration;
                obj.f5722c = interpolator;
                obj.f5723d = objectAnimator.getRepeatCount();
                obj.f5724e = objectAnimator.getRepeatMode();
                c0786d.f5718a.put(propertyName, obj);
            } else {
                throw new IllegalArgumentException("Animator must be an ObjectAnimator: " + animator);
            }
        }
        return c0786d;
    }

    public final C0787e c(String str) {
        j<String, C0787e> jVar = this.f5718a;
        if (jVar.getOrDefault(str, null) != null) {
            return jVar.getOrDefault(str, null);
        }
        throw new IllegalArgumentException();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0786d)) {
            return false;
        }
        return this.f5718a.equals(((C0786d) obj).f5718a);
    }

    public final int hashCode() {
        return this.f5718a.hashCode();
    }

    public final String toString() {
        return "\n" + C0786d.class.getName() + '{' + Integer.toHexString(System.identityHashCode(this)) + " timings: " + this.f5718a + "}\n";
    }
}
