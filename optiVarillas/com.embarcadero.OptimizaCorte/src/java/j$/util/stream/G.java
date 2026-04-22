package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.Predicate;
import java.util.function.Supplier;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class G implements K3 {

    /* renamed from: a  reason: collision with root package name */
    final int f4280a;

    /* renamed from: b  reason: collision with root package name */
    final Object f4281b;

    /* renamed from: c  reason: collision with root package name */
    final Predicate f4282c;

    /* renamed from: d  reason: collision with root package name */
    final Supplier f4283d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public G(boolean z4, EnumC0545f3 enumC0545f3, Object obj, Predicate predicate, Supplier supplier) {
        this.f4280a = (z4 ? 0 : EnumC0540e3.f4493r) | EnumC0540e3.f4496u;
        this.f4281b = obj;
        this.f4282c = predicate;
        this.f4283d = supplier;
    }

    @Override // j$.util.stream.K3
    public final Object b(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        L3 l32 = (L3) this.f4283d.get();
        abstractC0521b.R(spliterator, l32);
        Object obj = l32.get();
        return obj != null ? obj : this.f4281b;
    }

    @Override // j$.util.stream.K3
    public final Object c(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        return new M(this, EnumC0540e3.ORDERED.r(abstractC0521b.G()), abstractC0521b, spliterator).invoke();
    }

    @Override // j$.util.stream.K3
    public final int d() {
        return this.f4280a;
    }
}
