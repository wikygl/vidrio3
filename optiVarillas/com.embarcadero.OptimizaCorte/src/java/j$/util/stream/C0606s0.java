package j$.util.stream;

import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

/* renamed from: j$.util.stream.s0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0606s0 extends AbstractC0621v0 implements InterfaceC0589o2 {
    @Override // j$.util.stream.AbstractC0621v0, j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        if (this.f4598a) {
            return;
        }
        IntPredicate intPredicate = null;
        intPredicate.test(i4);
        throw null;
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        m((Integer) obj);
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }

    @Override // j$.util.stream.InterfaceC0589o2
    public final /* synthetic */ void m(Integer num) {
        AbstractC0637z0.g(this, num);
    }
}
