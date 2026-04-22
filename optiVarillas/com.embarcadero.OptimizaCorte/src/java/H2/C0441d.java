package h2;

import android.os.Build;

/* renamed from: h2.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0441d {

    /* renamed from: a  reason: collision with root package name */
    public static final int f3586a;

    static {
        int i4;
        if (Build.VERSION.SDK_INT >= 23) {
            i4 = 67108864;
        } else {
            i4 = 0;
        }
        f3586a = i4;
    }
}
