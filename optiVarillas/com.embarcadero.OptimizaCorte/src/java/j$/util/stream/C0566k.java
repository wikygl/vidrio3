package j$.util.stream;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/* renamed from: j$.util.stream.k  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class C0566k implements java.util.stream.Collector {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Collector f4532a;

    private /* synthetic */ C0566k(Collector collector) {
        this.f4532a = collector;
    }

    public static /* synthetic */ java.util.stream.Collector a(Collector collector) {
        if (collector == null) {
            return null;
        }
        return collector instanceof C0561j ? ((C0561j) collector).f4529a : new C0566k(collector);
    }

    @Override // java.util.stream.Collector
    public final /* synthetic */ BiConsumer accumulator() {
        return this.f4532a.accumulator();
    }

    @Override // java.util.stream.Collector
    public final /* synthetic */ Set characteristics() {
        return AbstractC0637z0.R(this.f4532a.characteristics());
    }

    @Override // java.util.stream.Collector
    public final /* synthetic */ BinaryOperator combiner() {
        return this.f4532a.combiner();
    }

    public final /* synthetic */ boolean equals(Object obj) {
        Collector collector = this.f4532a;
        if (obj instanceof C0566k) {
            obj = ((C0566k) obj).f4532a;
        }
        return collector.equals(obj);
    }

    @Override // java.util.stream.Collector
    public final /* synthetic */ Function finisher() {
        return this.f4532a.finisher();
    }

    public final /* synthetic */ int hashCode() {
        return this.f4532a.hashCode();
    }

    @Override // java.util.stream.Collector
    public final /* synthetic */ Supplier supplier() {
        return this.f4532a.supplier();
    }
}
