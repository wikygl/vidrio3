package j$.util.stream;

import j$.util.C0512o;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class U1 implements V1, InterfaceC0594p2 {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4420a;

    /* renamed from: b  reason: collision with root package name */
    private long f4421b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ LongBinaryOperator f4422c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public U1(LongBinaryOperator longBinaryOperator) {
        this.f4422c = longBinaryOperator;
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
    public final void accept(long j4) {
        if (this.f4420a) {
            this.f4420a = false;
        } else {
            j4 = this.f4422c.applyAsLong(this.f4421b, j4);
        }
        this.f4421b = j4;
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        j((Long) obj);
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
    }

    @Override // j$.util.stream.V1
    public final void g(V1 v12) {
        U1 u12 = (U1) v12;
        if (u12.f4420a) {
            return;
        }
        accept(u12.f4421b);
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        return this.f4420a ? C0512o.a() : C0512o.d(this.f4421b);
    }

    @Override // j$.util.stream.InterfaceC0594p2
    public final /* synthetic */ void j(Long l2) {
        AbstractC0637z0.i(this, l2);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4420a = true;
        this.f4421b = 0L;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }
}
