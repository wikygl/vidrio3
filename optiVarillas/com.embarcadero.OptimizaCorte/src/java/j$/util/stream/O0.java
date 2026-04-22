package j$.util.stream;

import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public class O0 implements L0 {

    /* renamed from: a  reason: collision with root package name */
    final Object[] f4345a;

    /* renamed from: b  reason: collision with root package name */
    int f4346b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public O0(long j4, IntFunction intFunction) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4345a = (Object[]) intFunction.apply((int) j4);
        this.f4346b = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public O0(Object[] objArr) {
        this.f4345a = objArr;
        this.f4346b = objArr.length;
    }

    @Override // j$.util.stream.L0
    public final L0 b(int i4) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.L0
    public final long count() {
        return this.f4346b;
    }

    @Override // j$.util.stream.L0
    public final void forEach(Consumer consumer) {
        for (int i4 = 0; i4 < this.f4346b; i4++) {
            consumer.accept(this.f4345a[i4]);
        }
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ L0 h(long j4, long j5, IntFunction intFunction) {
        return AbstractC0637z0.w(this, j4, j5, intFunction);
    }

    @Override // j$.util.stream.L0
    public final void i(Object[] objArr, int i4) {
        System.arraycopy(this.f4345a, 0, objArr, i4, this.f4346b);
    }

    @Override // j$.util.stream.L0
    public final Object[] o(IntFunction intFunction) {
        Object[] objArr = this.f4345a;
        if (objArr.length == this.f4346b) {
            return objArr;
        }
        throw new IllegalStateException();
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ int q() {
        return 0;
    }

    @Override // j$.util.stream.L0
    public final Spliterator spliterator() {
        return Spliterators.m(this.f4345a, 0, this.f4346b);
    }

    public String toString() {
        Object[] objArr = this.f4345a;
        return String.format("ArrayNode[%d][%s]", Integer.valueOf(objArr.length - this.f4346b), Arrays.toString(objArr));
    }
}
