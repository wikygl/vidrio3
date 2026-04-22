package j$.util.stream;

import j$.util.C0509l;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class L1 implements V1 {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4321a;

    /* renamed from: b  reason: collision with root package name */
    private Object f4322b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ BinaryOperator f4323c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public L1(BinaryOperator binaryOperator) {
        this.f4323c = binaryOperator;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void accept(double d4) {
        AbstractC0637z0.a();
        throw null;
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
    public final void accept(Object obj) {
        if (this.f4321a) {
            this.f4321a = false;
        } else {
            obj = this.f4323c.apply(this.f4322b, obj);
        }
        this.f4322b = obj;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // j$.util.stream.V1
    public final void g(V1 v12) {
        L1 l12 = (L1) v12;
        if (l12.f4321a) {
            return;
        }
        accept(l12.f4322b);
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        return this.f4321a ? C0509l.a() : C0509l.d(this.f4322b);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4321a = true;
        this.f4322b = null;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }
}
