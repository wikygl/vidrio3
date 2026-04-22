package a0;

import android.view.animation.Interpolator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class d implements Interpolator {

    /* renamed from: a  reason: collision with root package name */
    public final float[] f2859a;

    /* renamed from: b  reason: collision with root package name */
    public final float f2860b;

    public d(float[] fArr) {
        this.f2859a = fArr;
        this.f2860b = 1.0f / (fArr.length - 1);
    }

    @Override // android.animation.TimeInterpolator
    public final float getInterpolation(float f) {
        if (f >= 1.0f) {
            return 1.0f;
        }
        if (f <= 0.0f) {
            return 0.0f;
        }
        float[] fArr = this.f2859a;
        int min = Math.min((int) ((fArr.length - 1) * f), fArr.length - 2);
        float f4 = this.f2860b;
        float f5 = fArr[min];
        return ((fArr[min + 1] - f5) * ((f - (min * f4)) / f4)) + f5;
    }
}
