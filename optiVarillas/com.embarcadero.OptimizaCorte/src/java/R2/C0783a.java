package r2;

import a0.C0339a;
import a0.C0340b;
import a0.C0341c;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/* renamed from: r2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0783a {

    /* renamed from: a  reason: collision with root package name */
    public static final LinearInterpolator f5709a = new LinearInterpolator();

    /* renamed from: b  reason: collision with root package name */
    public static final C0340b f5710b = new a0.d(C0340b.f2857c);

    /* renamed from: c  reason: collision with root package name */
    public static final C0339a f5711c = new C0339a();

    /* renamed from: d  reason: collision with root package name */
    public static final C0341c f5712d = new a0.d(C0341c.f2858c);

    /* renamed from: e  reason: collision with root package name */
    public static final DecelerateInterpolator f5713e = new DecelerateInterpolator();

    public static float a(float f, float f4, float f5) {
        return ((f4 - f) * f5) + f;
    }

    public static float b(float f, float f4, float f5, float f6, float f7) {
        if (f7 <= f5) {
            return f;
        }
        if (f7 >= f6) {
            return f4;
        }
        return a(f, f4, (f7 - f5) / (f6 - f5));
    }

    public static int c(float f, int i4, int i5) {
        return Math.round(f * (i5 - i4)) + i4;
    }
}
