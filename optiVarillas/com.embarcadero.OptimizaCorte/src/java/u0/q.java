package u0;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class q extends p {
    public static boolean f = true;

    /* renamed from: g  reason: collision with root package name */
    public static boolean f6010g = true;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a {
        public static void a(View view, Matrix matrix) {
            view.setAnimationMatrix(matrix);
        }

        public static void b(View view, Matrix matrix) {
            view.transformMatrixToGlobal(matrix);
        }

        public static void c(View view, Matrix matrix) {
            view.transformMatrixToLocal(matrix);
        }
    }

    @SuppressLint({"NewApi"})
    public void e(View view, Matrix matrix) {
        if (f) {
            try {
                a.b(view, matrix);
            } catch (NoSuchMethodError unused) {
                f = false;
            }
        }
    }

    @SuppressLint({"NewApi"})
    public void f(View view, Matrix matrix) {
        if (f6010g) {
            try {
                a.c(view, matrix);
            } catch (NoSuchMethodError unused) {
                f6010g = false;
            }
        }
    }
}
