package u0;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class s extends r {

    /* renamed from: i  reason: collision with root package name */
    public static boolean f6012i = true;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a {
        public static void a(View view, int i4) {
            view.setTransitionVisibility(i4);
        }
    }

    @Override // u0.p
    @SuppressLint({"NewApi"})
    public void d(View view, int i4) {
        if (Build.VERSION.SDK_INT == 28) {
            super.d(view, i4);
        } else if (f6012i) {
            try {
                a.a(view, i4);
            } catch (NoSuchMethodError unused) {
                f6012i = false;
            }
        }
    }
}
