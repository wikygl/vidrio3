package j$.util.stream;

import j$.util.C0510m;
import java.util.function.DoubleConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class H extends L implements InterfaceC0584n2 {

    /* renamed from: c  reason: collision with root package name */
    static final G f4294c;

    /* renamed from: d  reason: collision with root package name */
    static final G f4295d;

    static {
        EnumC0545f3 enumC0545f3 = EnumC0545f3.DOUBLE_VALUE;
        f4294c = new G(true, enumC0545f3, C0510m.a(), new r(5), new C0571l(6));
        f4295d = new G(false, enumC0545f3, C0510m.a(), new r(5), new C0571l(6));
    }

    @Override // j$.util.stream.L, j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        p(Double.valueOf(d4));
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return j$.com.android.tools.r8.a.e(this, doubleConsumer);
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        if (this.f4319a) {
            return C0510m.d(((Double) this.f4320b).doubleValue());
        }
        return null;
    }
}
