package t1;

import A1.C0124p;
import android.content.Context;
import android.util.DisplayMetrics;
import e0.C0405a;

/* renamed from: t1.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0803e {

    /* renamed from: i  reason: collision with root package name */
    public static final C0803e f5788i = new C0803e(320, 50, "320x50_mb");

    /* renamed from: j  reason: collision with root package name */
    public static final C0803e f5789j;

    /* renamed from: k  reason: collision with root package name */
    public static final C0803e f5790k;

    /* renamed from: a  reason: collision with root package name */
    public final int f5791a;

    /* renamed from: b  reason: collision with root package name */
    public final int f5792b;

    /* renamed from: c  reason: collision with root package name */
    public final String f5793c;

    /* renamed from: d  reason: collision with root package name */
    public boolean f5794d;

    /* renamed from: e  reason: collision with root package name */
    public boolean f5795e;
    public int f;

    /* renamed from: g  reason: collision with root package name */
    public boolean f5796g;

    /* renamed from: h  reason: collision with root package name */
    public int f5797h;

    static {
        new C0803e(468, 60, "468x60_as");
        new C0803e(320, 100, "320x100_as");
        new C0803e(728, 90, "728x90_as");
        new C0803e(300, 250, "300x250_as");
        new C0803e(160, 600, "160x600_as");
        new C0803e(-1, -2, "smart_banner");
        f5789j = new C0803e(-3, -4, "fluid");
        f5790k = new C0803e(0, 0, "invalid");
        new C0803e(50, 50, "50x50_mb");
        new C0803e(-3, 0, "search_v2");
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public C0803e(int r4, int r5) {
        /*
            r3 = this;
            r0 = -1
            if (r4 != r0) goto L6
            java.lang.String r0 = "FULL"
            goto La
        L6:
            java.lang.String r0 = java.lang.String.valueOf(r4)
        La:
            r1 = -2
            if (r5 != r1) goto L10
            java.lang.String r1 = "AUTO"
            goto L14
        L10:
            java.lang.String r1 = java.lang.String.valueOf(r5)
        L14:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            java.lang.String r0 = "x"
            r2.append(r0)
            r2.append(r1)
            java.lang.String r0 = "_as"
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r3.<init>(r4, r5, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: t1.C0803e.<init>(int, int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0030  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0033  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static t1.C0803e a(android.content.Context r2, int r3) {
        /*
            com.google.android.gms.internal.ads.WJ r0 = E1.f.f854b
            r0 = -1
            if (r2 != 0) goto L6
            goto L2d
        L6:
            android.content.Context r1 = r2.getApplicationContext()
            if (r1 == 0) goto L10
            android.content.Context r2 = r2.getApplicationContext()
        L10:
            android.content.res.Resources r2 = r2.getResources()
            if (r2 == 0) goto L2d
            android.util.DisplayMetrics r1 = r2.getDisplayMetrics()
            if (r1 == 0) goto L2d
            android.content.res.Configuration r2 = r2.getConfiguration()
            if (r2 == 0) goto L2d
            int r2 = r1.heightPixels
            float r2 = (float) r2
            float r1 = r1.density
            float r2 = r2 / r1
            int r2 = java.lang.Math.round(r2)
            goto L2e
        L2d:
            r2 = -1
        L2e:
            if (r2 != r0) goto L33
            t1.e r2 = t1.C0803e.f5790k
            goto L8f
        L33:
            float r2 = (float) r2
            r0 = 1041865114(0x3e19999a, float:0.15)
            float r2 = r2 * r0
            int r2 = java.lang.Math.round(r2)
            r0 = 90
            int r2 = java.lang.Math.min(r0, r2)
            r0 = 655(0x28f, float:9.18E-43)
            if (r3 <= r0) goto L54
            float r0 = (float) r3
            r1 = 1144389632(0x44360000, float:728.0)
            float r0 = r0 / r1
            r1 = 1119092736(0x42b40000, float:90.0)
            float r0 = r0 * r1
            int r0 = java.lang.Math.round(r0)
            goto L7f
        L54:
            r0 = 632(0x278, float:8.86E-43)
            if (r3 <= r0) goto L5b
            r0 = 81
            goto L7f
        L5b:
            r0 = 526(0x20e, float:7.37E-43)
            if (r3 <= r0) goto L6c
            float r0 = (float) r3
            r1 = 1139408896(0x43ea0000, float:468.0)
            float r0 = r0 / r1
            r1 = 1114636288(0x42700000, float:60.0)
            float r0 = r0 * r1
            int r0 = java.lang.Math.round(r0)
            goto L7f
        L6c:
            r0 = 432(0x1b0, float:6.05E-43)
            if (r3 <= r0) goto L73
            r0 = 68
            goto L7f
        L73:
            float r0 = (float) r3
            r1 = 1134559232(0x43a00000, float:320.0)
            float r0 = r0 / r1
            r1 = 1112014848(0x42480000, float:50.0)
            float r0 = r0 * r1
            int r0 = java.lang.Math.round(r0)
        L7f:
            int r2 = java.lang.Math.min(r0, r2)
            r0 = 50
            int r2 = java.lang.Math.max(r2, r0)
            t1.e r0 = new t1.e
            r0.<init>(r3, r2)
            r2 = r0
        L8f:
            r3 = 1
            r2.f5794d = r3
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: t1.C0803e.a(android.content.Context, int):t1.e");
    }

    public final int b(Context context) {
        int i4;
        int i5 = this.f5792b;
        if (i5 != -4 && i5 != -3) {
            if (i5 != -2) {
                E1.f fVar = C0124p.f.f161a;
                return E1.f.m(context, i5);
            }
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float f = displayMetrics.density;
            int i6 = (int) (displayMetrics.heightPixels / f);
            if (i6 <= 400) {
                i4 = 32;
            } else if (i6 <= 720) {
                i4 = 50;
            } else {
                i4 = 90;
            }
            return (int) (i4 * f);
        }
        return -1;
    }

    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof C0803e)) {
            return false;
        }
        C0803e c0803e = (C0803e) obj;
        if (this.f5791a != c0803e.f5791a || this.f5792b != c0803e.f5792b || !this.f5793c.equals(c0803e.f5793c)) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        return this.f5793c.hashCode();
    }

    public final String toString() {
        return this.f5793c;
    }

    public C0803e(int i4, int i5, String str) {
        if (i4 < 0 && i4 != -1 && i4 != -3) {
            throw new IllegalArgumentException(C0405a.c("Invalid width for AdSize: ", i4));
        }
        if (i5 < 0 && i5 != -2 && i5 != -4) {
            throw new IllegalArgumentException(C0405a.c("Invalid height for AdSize: ", i5));
        }
        this.f5791a = i4;
        this.f5792b = i5;
        this.f5793c = str;
    }
}
