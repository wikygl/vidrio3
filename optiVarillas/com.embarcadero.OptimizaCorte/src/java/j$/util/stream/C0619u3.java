package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.Consumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.u3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0619u3 extends x3 implements j$.util.G {
    /* JADX WARN: Type inference failed for: r10v0, types: [j$.util.stream.z3, j$.util.Spliterator] */
    @Override // j$.util.stream.z3
    protected final Spliterator a(Spliterator spliterator, long j4, long j5, long j6, long j7) {
        return new z3((j$.util.G) spliterator, j4, j5, j6, j7);
    }

    @Override // j$.util.stream.x3
    protected final Object b() {
        return new E0(1);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        j$.util.D.a(this, consumer);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return j$.util.D.f(this, consumer);
    }
}
