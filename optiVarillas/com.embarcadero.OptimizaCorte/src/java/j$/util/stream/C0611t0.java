package j$.util.stream;

import java.util.function.LongConsumer;
import java.util.function.LongPredicate;

/* renamed from: j$.util.stream.t0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0611t0 extends AbstractC0621v0 implements InterfaceC0594p2 {
    @Override // j$.util.stream.AbstractC0621v0, j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final void accept(long j4) {
        if (this.f4598a) {
            return;
        }
        LongPredicate longPredicate = null;
        longPredicate.test(j4);
        throw null;
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        j((Long) obj);
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
    }

    @Override // j$.util.stream.InterfaceC0594p2
    public final /* synthetic */ void j(Long l2) {
        AbstractC0637z0.i(this, l2);
    }
}
