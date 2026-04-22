package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.Consumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class S implements K3, L3 {

    /* renamed from: a  reason: collision with root package name */
    private final boolean f4385a;

    /* JADX INFO: Access modifiers changed from: protected */
    public S(boolean z4) {
        this.f4385a = z4;
    }

    public /* synthetic */ void accept(double d4) {
        AbstractC0637z0.a();
        throw null;
    }

    public /* synthetic */ void accept(int i4) {
        AbstractC0637z0.k();
        throw null;
    }

    public /* synthetic */ void accept(long j4) {
        AbstractC0637z0.l();
        throw null;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // j$.util.stream.K3
    public final int d() {
        if (this.f4385a) {
            return 0;
        }
        return EnumC0540e3.f4493r;
    }

    public final void e(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        (this.f4385a ? new T(abstractC0521b, spliterator, this) : new U(abstractC0521b, spliterator, abstractC0521b.S(this))).invoke();
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void l(long j4) {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }
}
