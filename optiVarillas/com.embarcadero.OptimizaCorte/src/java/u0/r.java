package u0;

import android.annotation.SuppressLint;
import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class r extends q {

    /* renamed from: h  reason: collision with root package name */
    public static boolean f6011h = true;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a {
        public static void a(View view, int i4, int i5, int i6, int i7) {
            view.setLeftTopRightBottom(i4, i5, i6, i7);
        }
    }

    @Override // u0.p
    @SuppressLint({"NewApi"})
    public void b(View view, int i4, int i5, int i6, int i7) {
        if (f6011h) {
            try {
                a.a(view, i4, i5, i6, i7);
            } catch (NoSuchMethodError unused) {
                f6011h = false;
            }
        }
    }
}
