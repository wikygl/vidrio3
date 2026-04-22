package j$.util.stream;

import j$.util.Collection;
import j$.util.Spliterator;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class P0 implements L0 {

    /* renamed from: a  reason: collision with root package name */
    private final Collection f4354a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public P0(Collection collection) {
        this.f4354a = collection;
    }

    @Override // j$.util.stream.L0
    public final L0 b(int i4) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.L0
    public final long count() {
        return this.f4354a.size();
    }

    @Override // j$.util.stream.L0
    public final void forEach(Consumer consumer) {
        Collection.EL.a(this.f4354a, consumer);
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ L0 h(long j4, long j5, IntFunction intFunction) {
        return AbstractC0637z0.w(this, j4, j5, intFunction);
    }

    @Override // j$.util.stream.L0
    public final void i(Object[] objArr, int i4) {
        for (Object obj : this.f4354a) {
            objArr[i4] = obj;
            i4++;
        }
    }

    @Override // j$.util.stream.L0
    public final Object[] o(IntFunction intFunction) {
        java.util.Collection collection = this.f4354a;
        return collection.toArray((Object[]) intFunction.apply(collection.size()));
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ int q() {
        return 0;
    }

    @Override // j$.util.stream.L0
    public final Spliterator spliterator() {
        return Collection.EL.stream(this.f4354a).spliterator();
    }

    public final String toString() {
        java.util.Collection collection = this.f4354a;
        return String.format("CollectionNode[%d][%s]", Integer.valueOf(collection.size()), collection);
    }
}
