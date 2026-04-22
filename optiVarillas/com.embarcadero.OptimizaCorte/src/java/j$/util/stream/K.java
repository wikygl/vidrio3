package j$.util.stream;

import j$.util.C0509l;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class K extends L {

    /* renamed from: c  reason: collision with root package name */
    static final G f4314c;

    /* renamed from: d  reason: collision with root package name */
    static final G f4315d;

    static {
        EnumC0545f3 enumC0545f3 = EnumC0545f3.REFERENCE;
        f4314c = new G(true, enumC0545f3, C0509l.a(), new r(8), new C0571l(9));
        f4315d = new G(false, enumC0545f3, C0509l.a(), new r(8), new C0571l(9));
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        if (this.f4319a) {
            return C0509l.d(this.f4320b);
        }
        return null;
    }
}
