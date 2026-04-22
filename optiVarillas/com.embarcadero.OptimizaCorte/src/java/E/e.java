package E;

import D.f;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Method;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class e {

    /* renamed from: a  reason: collision with root package name */
    public static final l f810a;

    /* renamed from: b  reason: collision with root package name */
    public static final r.h<String, Typeface> f811b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class a extends B2.a {

        /* renamed from: j  reason: collision with root package name */
        public f.e f812j;
    }

    static {
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 29) {
            f810a = new l();
        } else if (i4 >= 28) {
            f810a = new h();
        } else if (i4 >= 26) {
            f810a = new h();
        } else {
            if (i4 >= 24) {
                Method method = g.f820c;
                if (method == null) {
                    Log.w("TypefaceCompatApi24Impl", "Unable to collect necessary private methods.Fallback to legacy implementation.");
                }
                if (method != null) {
                    f810a = new l();
                }
            }
            f810a = new l();
        }
        f811b = new r.h<>(16);
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x002b, code lost:
        if (r6.equals(r9) == false) goto L11;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v1, types: [java.lang.Object, E.e$a] */
    /* JADX WARN: Type inference failed for: r5v4, types: [java.lang.Object, J.n, java.lang.Runnable] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.Typeface a(android.content.Context r14, D.e.b r15, android.content.res.Resources r16, int r17, java.lang.String r18, int r19, int r20, D.f.e r21, boolean r22) {
        /*
            Method dump skipped, instructions count: 411
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: E.e.a(android.content.Context, D.e$b, android.content.res.Resources, int, java.lang.String, int, int, D.f$e, boolean):android.graphics.Typeface");
    }

    public static String b(Resources resources, int i4, String str, int i5, int i6) {
        return resources.getResourcePackageName(i4) + '-' + str + '-' + i5 + '-' + i4 + '-' + i6;
    }
}
