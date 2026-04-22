package j$.util.stream;

import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;

/* renamed from: j$.util.stream.u0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0616u0 extends AbstractC0621v0 implements InterfaceC0584n2 {
    @Override // j$.util.stream.AbstractC0621v0, j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        if (this.f4598a) {
            return;
        }
        DoublePredicate doublePredicate = null;
        doublePredicate.test(d4);
        throw null;
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        p((Double) obj);
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return j$.com.android.tools.r8.a.e(this, doubleConsumer);
    }

    @Override // j$.util.stream.InterfaceC0584n2
    public final /* synthetic */ void p(Double d4) {
        AbstractC0637z0.e(this, d4);
    }
}
