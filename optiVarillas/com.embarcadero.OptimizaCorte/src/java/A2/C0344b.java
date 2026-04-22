package a2;

import W1.C0324l;
import android.content.Context;
import android.util.Log;
import com.google.errorprone.annotations.ResultIgnorabilityUnspecified;

/* renamed from: a2.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0344b {
    @ResultIgnorabilityUnspecified
    public static void a(Context context, Throwable th) {
        try {
            C0324l.d(context);
        } catch (Exception e4) {
            Log.e("CrashUtils", "Error adding exception to DropBox!", e4);
        }
    }
}
