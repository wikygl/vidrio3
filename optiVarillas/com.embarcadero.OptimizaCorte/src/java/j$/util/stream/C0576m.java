package j$.util.stream;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.m  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0576m implements Collector {

    /* renamed from: a  reason: collision with root package name */
    private final Supplier f4539a;

    /* renamed from: b  reason: collision with root package name */
    private final BiConsumer f4540b;

    /* renamed from: c  reason: collision with root package name */
    private final BinaryOperator f4541c;

    /* renamed from: d  reason: collision with root package name */
    private final Function f4542d;

    /* renamed from: e  reason: collision with root package name */
    private final Set f4543e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0576m(C0571l c0571l, C0571l c0571l2, C0571l c0571l3) {
        Set set = Collectors.f4262a;
        C0571l c0571l4 = new C0571l(1);
        this.f4539a = c0571l;
        this.f4540b = c0571l2;
        this.f4541c = c0571l3;
        this.f4542d = c0571l4;
        this.f4543e = set;
    }

    @Override // j$.util.stream.Collector
    public final BiConsumer accumulator() {
        return this.f4540b;
    }

    @Override // j$.util.stream.Collector
    public final Set characteristics() {
        return this.f4543e;
    }

    @Override // j$.util.stream.Collector
    public final BinaryOperator combiner() {
        return this.f4541c;
    }

    @Override // j$.util.stream.Collector
    public final Function finisher() {
        return this.f4542d;
    }

    @Override // j$.util.stream.Collector
    public final Supplier supplier() {
        return this.f4539a;
    }
}
