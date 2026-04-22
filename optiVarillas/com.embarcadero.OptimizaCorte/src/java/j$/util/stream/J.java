package j$.util.stream;

import j$.util.C0512o;
import java.util.function.LongConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class J extends L implements InterfaceC0594p2 {

    /* renamed from: c  reason: collision with root package name */
    static final G f4308c;

    /* renamed from: d  reason: collision with root package name */
    static final G f4309d;

    static {
        EnumC0545f3 enumC0545f3 = EnumC0545f3.LONG_VALUE;
        f4308c = new G(true, enumC0545f3, C0512o.a(), new r(7), new C0571l(8));
        f4309d = new G(false, enumC0545f3, C0512o.a(), new r(7), new C0571l(8));
    }

    @Override // j$.util.stream.L, j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final void accept(long j4) {
        p(Long.valueOf(j4));
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        if (this.f4319a) {
            return C0512o.d(((Long) this.f4320b).longValue());
        }
        return null;
    }
}
