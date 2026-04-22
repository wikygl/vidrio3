package j$.util.concurrent;

import j$.util.D;
import j$.util.Spliterator;
import java.util.Comparator;
import java.util.function.Consumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class j extends p implements Spliterator {

    /* renamed from: i  reason: collision with root package name */
    public final /* synthetic */ int f4159i;

    /* renamed from: j  reason: collision with root package name */
    long f4160j;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ j(l[] lVarArr, int i4, int i5, int i6, long j4, int i7) {
        super(lVarArr, i4, i5, i6);
        this.f4159i = i7;
        this.f4160j = j4;
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        switch (this.f4159i) {
            case 0:
                return 4353;
            default:
                return 4352;
        }
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        switch (this.f4159i) {
            case 0:
                return this.f4160j;
            default:
                return this.f4160j;
        }
    }

    @Override // j$.util.Spliterator
    public final void forEachRemaining(Consumer consumer) {
        switch (this.f4159i) {
            case 0:
                consumer.getClass();
                while (true) {
                    l a4 = a();
                    if (a4 == null) {
                        return;
                    }
                    consumer.accept(a4.f4165b);
                }
            default:
                consumer.getClass();
                while (true) {
                    l a5 = a();
                    if (a5 == null) {
                        return;
                    }
                    consumer.accept(a5.f4166c);
                }
        }
    }

    @Override // j$.util.Spliterator
    public final Comparator getComparator() {
        switch (this.f4159i) {
            case 0:
                throw new IllegalStateException();
            default:
                throw new IllegalStateException();
        }
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        switch (this.f4159i) {
            case 0:
                return D.d(this);
            default:
                return D.d(this);
        }
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        switch (this.f4159i) {
            case 0:
                return D.e(this, i4);
            default:
                return D.e(this, i4);
        }
    }

    @Override // j$.util.Spliterator
    public final boolean tryAdvance(Consumer consumer) {
        switch (this.f4159i) {
            case 0:
                consumer.getClass();
                l a4 = a();
                if (a4 == null) {
                    return false;
                }
                consumer.accept(a4.f4165b);
                return true;
            default:
                consumer.getClass();
                l a5 = a();
                if (a5 == null) {
                    return false;
                }
                consumer.accept(a5.f4166c);
                return true;
        }
    }

    @Override // j$.util.Spliterator
    public final Spliterator trySplit() {
        switch (this.f4159i) {
            case 0:
                int i4 = this.f;
                int i5 = this.f4177g;
                int i6 = (i4 + i5) >>> 1;
                if (i6 <= i4) {
                    return null;
                }
                l[] lVarArr = this.f4172a;
                this.f4177g = i6;
                long j4 = this.f4160j >>> 1;
                this.f4160j = j4;
                return new j(lVarArr, this.f4178h, i6, i5, j4, 0);
            default:
                int i7 = this.f;
                int i8 = this.f4177g;
                int i9 = (i7 + i8) >>> 1;
                if (i9 <= i7) {
                    return null;
                }
                l[] lVarArr2 = this.f4172a;
                this.f4177g = i9;
                long j5 = this.f4160j >>> 1;
                this.f4160j = j5;
                return new j(lVarArr2, this.f4178h, i9, i8, j5, 1);
        }
    }
}
