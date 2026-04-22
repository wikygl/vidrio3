package j$.time.format;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.Comparator;
import java.util.Locale;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class c {

    /* renamed from: b  reason: collision with root package name */
    private static final Comparator f3924b;

    /* renamed from: c  reason: collision with root package name */
    public static final /* synthetic */ int f3925c = 0;

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ s f3926a;

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.Object, java.util.Comparator] */
    static {
        new ConcurrentHashMap(16, 0.75f, 2);
        f3924b = new Object();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public c(s sVar) {
        this.f3926a = sVar;
    }

    public final String b(j$.time.chrono.n nVar, j$.time.temporal.r rVar, long j4, w wVar, Locale locale) {
        return this.f3926a.a(j4, wVar);
    }

    public final String c(j$.time.temporal.r rVar, long j4, w wVar, Locale locale) {
        return this.f3926a.a(j4, wVar);
    }
}
