package G3;

import F3.w;
import java.util.concurrent.TimeUnit;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class k {

    /* renamed from: a  reason: collision with root package name */
    public static final String f999a;

    /* renamed from: b  reason: collision with root package name */
    public static final long f1000b;

    /* renamed from: c  reason: collision with root package name */
    public static final int f1001c;

    /* renamed from: d  reason: collision with root package name */
    public static final int f1002d;

    /* renamed from: e  reason: collision with root package name */
    public static final long f1003e;
    public static final e f;

    /* renamed from: g  reason: collision with root package name */
    public static final i f1004g;

    /* renamed from: h  reason: collision with root package name */
    public static final i f1005h;

    static {
        String str;
        int i4 = w.f946a;
        try {
            str = System.getProperty("kotlinx.coroutines.scheduler.default.name");
        } catch (SecurityException unused) {
            str = null;
        }
        if (str == null) {
            str = "DefaultDispatcher";
        }
        f999a = str;
        f1000b = A0.c.f("kotlinx.coroutines.scheduler.resolution.ns", 100000L, 1L, Long.MAX_VALUE);
        int i5 = w.f946a;
        if (i5 < 2) {
            i5 = 2;
        }
        f1001c = A0.c.g("kotlinx.coroutines.scheduler.core.pool.size", i5, 1, 0, 8);
        f1002d = A0.c.g("kotlinx.coroutines.scheduler.max.pool.size", 2097150, 0, 2097150, 4);
        f1003e = TimeUnit.SECONDS.toNanos(A0.c.f("kotlinx.coroutines.scheduler.keep.alive.sec", 60L, 1L, Long.MAX_VALUE));
        f = e.f992k;
        f1004g = new i(0);
        f1005h = new i(1);
    }
}
