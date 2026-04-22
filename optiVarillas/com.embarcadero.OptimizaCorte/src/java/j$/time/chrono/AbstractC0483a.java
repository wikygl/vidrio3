package j$.time.chrono;

import j$.time.C0482c;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.Locale;

/* renamed from: j$.time.chrono.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class AbstractC0483a implements n {

    /* renamed from: a  reason: collision with root package name */
    private static final ConcurrentHashMap f3871a = new ConcurrentHashMap();

    /* renamed from: b  reason: collision with root package name */
    private static final ConcurrentHashMap f3872b = new ConcurrentHashMap();

    /* renamed from: c  reason: collision with root package name */
    public static final /* synthetic */ int f3873c = 0;

    static {
        new Locale("ja", "JP", "JP");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:20:0x008b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static j$.time.chrono.n j(java.lang.String r4) {
        /*
            java.lang.String r0 = "id"
            j$.util.Objects.requireNonNull(r4, r0)
        L5:
            j$.util.concurrent.ConcurrentHashMap r0 = j$.time.chrono.AbstractC0483a.f3871a
            java.lang.Object r1 = r0.get(r4)
            j$.time.chrono.n r1 = (j$.time.chrono.n) r1
            if (r1 != 0) goto L17
            j$.util.concurrent.ConcurrentHashMap r1 = j$.time.chrono.AbstractC0483a.f3872b
            java.lang.Object r1 = r1.get(r4)
            j$.time.chrono.n r1 = (j$.time.chrono.n) r1
        L17:
            if (r1 == 0) goto L1a
            return r1
        L1a:
            java.lang.String r1 = "ISO"
            java.lang.Object r0 = r0.get(r1)
            if (r0 != 0) goto L7b
            j$.time.chrono.q r0 = j$.time.chrono.q.f3889o
            java.lang.String r2 = r0.i()
            l(r0, r2)
            j$.time.chrono.x r0 = j$.time.chrono.x.f3909d
            r0.getClass()
            java.lang.String r2 = "Japanese"
            l(r0, r2)
            j$.time.chrono.C r0 = j$.time.chrono.C.f3860d
            r0.getClass()
            java.lang.String r2 = "Minguo"
            l(r0, r2)
            j$.time.chrono.I r0 = j$.time.chrono.I.f3867d
            r0.getClass()
            java.lang.String r2 = "ThaiBuddhist"
            l(r0, r2)
            java.lang.Class<j$.time.chrono.a> r0 = j$.time.chrono.AbstractC0483a.class
            r2 = 0
            java.util.ServiceLoader r0 = java.util.ServiceLoader.load(r0, r2)
            java.util.Iterator r0 = r0.iterator()
        L54:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L72
            java.lang.Object r2 = r0.next()
            j$.time.chrono.a r2 = (j$.time.chrono.AbstractC0483a) r2
            java.lang.String r3 = r2.i()
            boolean r3 = r3.equals(r1)
            if (r3 != 0) goto L54
            java.lang.String r3 = r2.i()
            l(r2, r3)
            goto L54
        L72:
            j$.time.chrono.u r0 = j$.time.chrono.u.f3906d
            r0.getClass()
            l(r0, r1)
            goto L5
        L7b:
            java.lang.Class<j$.time.chrono.n> r0 = j$.time.chrono.n.class
            java.util.ServiceLoader r0 = java.util.ServiceLoader.load(r0)
            java.util.Iterator r0 = r0.iterator()
        L85:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto La6
            java.lang.Object r1 = r0.next()
            j$.time.chrono.n r1 = (j$.time.chrono.n) r1
            java.lang.String r2 = r1.i()
            boolean r2 = r4.equals(r2)
            if (r2 != 0) goto La5
            java.lang.String r2 = r1.q()
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L85
        La5:
            return r1
        La6:
            j$.time.c r0 = new j$.time.c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r2 = "Unknown chronology: "
            r1.<init>(r2)
            r1.append(r4)
            java.lang.String r4 = r1.toString()
            r0.<init>(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.chrono.AbstractC0483a.j(java.lang.String):j$.time.chrono.n");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static n l(n nVar, String str) {
        String q4;
        n nVar2 = (n) f3871a.putIfAbsent(str, nVar);
        if (nVar2 == null && (q4 = nVar.q()) != null) {
            f3872b.putIfAbsent(q4, nVar);
        }
        return nVar2;
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        return i().compareTo(((n) obj).i());
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof AbstractC0483a) && i().compareTo(((AbstractC0483a) obj).i()) == 0;
    }

    public final int hashCode() {
        return getClass().hashCode() ^ i().hashCode();
    }

    @Override // j$.time.chrono.n
    public InterfaceC0487e n(j$.time.k kVar) {
        try {
            return k(kVar).t(j$.time.m.F(kVar));
        } catch (C0482c e4) {
            throw new RuntimeException("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + j$.time.k.class, e4);
        }
    }

    public final String toString() {
        return i();
    }
}
