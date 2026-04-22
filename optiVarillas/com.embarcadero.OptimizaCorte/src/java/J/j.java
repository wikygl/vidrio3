package J;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class j {

    /* renamed from: a  reason: collision with root package name */
    public static final r.h<String, Typeface> f1173a = new r.h<>(16);

    /* renamed from: b  reason: collision with root package name */
    public static final ThreadPoolExecutor f1174b;

    /* renamed from: c  reason: collision with root package name */
    public static final Object f1175c;

    /* renamed from: d  reason: collision with root package name */
    public static final r.j<String, ArrayList<L.a<a>>> f1176d;

    /* JADX WARN: Type inference failed for: r9v0, types: [J.m, java.lang.Object, java.util.concurrent.ThreadFactory] */
    static {
        ?? obj = new Object();
        obj.f1186a = "fonts-androidx";
        obj.f1187b = 10;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 1, 10000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque(), (ThreadFactory) obj);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        f1174b = threadPoolExecutor;
        f1175c = new Object();
        f1176d = new r.j<>();
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x003d  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0043  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static J.j.a a(java.lang.String r6, android.content.Context r7, J.e r8, int r9) {
        /*
            r.h<java.lang.String, android.graphics.Typeface> r0 = J.j.f1173a
            java.lang.Object r1 = r0.a(r6)
            android.graphics.Typeface r1 = (android.graphics.Typeface) r1
            if (r1 == 0) goto L10
            J.j$a r6 = new J.j$a
            r6.<init>(r1)
            return r6
        L10:
            J.k r8 = J.d.a(r7, r8)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L5a
            r1 = 1
            r2 = -3
            J.l[] r3 = r8.f1180b
            int r8 = r8.f1179a
            if (r8 == 0) goto L22
            if (r8 == r1) goto L20
        L1e:
            r1 = -3
            goto L3b
        L20:
            r1 = -2
            goto L3b
        L22:
            if (r3 == 0) goto L3b
            int r8 = r3.length
            if (r8 != 0) goto L28
            goto L3b
        L28:
            int r8 = r3.length
            r1 = 0
            r4 = 0
        L2b:
            if (r4 >= r8) goto L3b
            r5 = r3[r4]
            int r5 = r5.f1185e
            if (r5 == 0) goto L38
            if (r5 >= 0) goto L36
            goto L1e
        L36:
            r1 = r5
            goto L3b
        L38:
            int r4 = r4 + 1
            goto L2b
        L3b:
            if (r1 == 0) goto L43
            J.j$a r6 = new J.j$a
            r6.<init>(r1)
            return r6
        L43:
            E.l r8 = E.e.f810a
            android.graphics.Typeface r7 = r8.b(r7, r3, r9)
            if (r7 == 0) goto L54
            r0.b(r6, r7)
            J.j$a r6 = new J.j$a
            r6.<init>(r7)
            return r6
        L54:
            J.j$a r6 = new J.j$a
            r6.<init>(r2)
            return r6
        L5a:
            J.j$a r6 = new J.j$a
            r7 = -1
            r6.<init>(r7)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: J.j.a(java.lang.String, android.content.Context, J.e, int):J.j$a");
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public final Typeface f1177a;

        /* renamed from: b  reason: collision with root package name */
        public final int f1178b;

        public a(int i4) {
            this.f1177a = null;
            this.f1178b = i4;
        }

        @SuppressLint({"WrongConstant"})
        public a(Typeface typeface) {
            this.f1177a = typeface;
            this.f1178b = 0;
        }
    }
}
