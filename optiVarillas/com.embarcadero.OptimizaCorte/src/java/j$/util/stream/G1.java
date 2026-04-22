package j$.util.stream;

import j$.util.C0510m;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class G1 implements V1, InterfaceC0584n2 {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4285a;

    /* renamed from: b  reason: collision with root package name */
    private double f4286b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ DoubleBinaryOperator f4287c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public G1(DoubleBinaryOperator doubleBinaryOperator) {
        this.f4287c = doubleBinaryOperator;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        if (this.f4285a) {
            this.f4285a = false;
        } else {
            d4 = this.f4287c.applyAsDouble(this.f4286b, d4);
        }
        this.f4286b = d4;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void accept(int i4) {
        AbstractC0637z0.k();
        throw null;
    }

    @Override // j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final /* synthetic */ void accept(long j4) {
        AbstractC0637z0.l();
        throw null;
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        p((Double) obj);
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return j$.com.android.tools.r8.a.e(this, doubleConsumer);
    }

    @Override // j$.util.stream.V1
    public final void g(V1 v12) {
        G1 g12 = (G1) v12;
        if (g12.f4285a) {
            return;
        }
        accept(g12.f4286b);
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        return this.f4285a ? C0510m.a() : C0510m.d(this.f4286b);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4285a = true;
        this.f4286b = 0.0d;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }

    @Override // j$.util.stream.InterfaceC0584n2
    public final /* synthetic */ void p(Double d4) {
        AbstractC0637z0.e(this, d4);
    }
}
