package j$.util.stream;

import j$.util.C0511n;
import java.util.function.IntConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class I extends L implements InterfaceC0589o2 {

    /* renamed from: c  reason: collision with root package name */
    static final G f4300c;

    /* renamed from: d  reason: collision with root package name */
    static final G f4301d;

    static {
        EnumC0545f3 enumC0545f3 = EnumC0545f3.INT_VALUE;
        f4300c = new G(true, enumC0545f3, C0511n.a(), new r(6), new C0571l(7));
        f4301d = new G(false, enumC0545f3, C0511n.a(), new r(6), new C0571l(7));
    }

    @Override // j$.util.stream.L, j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        p(Integer.valueOf(i4));
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        if (this.f4319a) {
            return C0511n.d(((Integer) this.f4320b).intValue());
        }
        return null;
    }
}
