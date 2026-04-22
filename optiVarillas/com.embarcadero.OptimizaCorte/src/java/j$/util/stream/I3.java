package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import java.util.function.Consumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class I3 extends AbstractC0550g3 {
    @Override // j$.util.stream.AbstractC0550g3
    final void d() {
        C0520a3 c0520a3 = new C0520a3();
        this.f4516h = c0520a3;
        Objects.requireNonNull(c0520a3);
        this.f4514e = this.f4511b.S(new H3(c0520a3, 0));
        this.f = new C0516a(this, 5);
    }

    @Override // j$.util.stream.AbstractC0550g3
    final AbstractC0550g3 e(Spliterator spliterator) {
        return new AbstractC0550g3(this.f4511b, spliterator, this.f4510a);
    }

    @Override // j$.util.Spliterator
    public final void forEachRemaining(Consumer consumer) {
        if (this.f4516h != null || this.f4517i) {
            do {
            } while (tryAdvance(consumer));
            return;
        }
        Objects.requireNonNull(consumer);
        c();
        Objects.requireNonNull(consumer);
        H3 h32 = new H3(consumer, 1);
        this.f4511b.R(this.f4513d, h32);
        this.f4517i = true;
    }

    @Override // j$.util.Spliterator
    public final boolean tryAdvance(Consumer consumer) {
        Object obj;
        Objects.requireNonNull(consumer);
        boolean a4 = a();
        if (a4) {
            C0520a3 c0520a3 = (C0520a3) this.f4516h;
            long j4 = this.f4515g;
            if (c0520a3.f4467c != 0) {
                if (j4 < c0520a3.count()) {
                    for (int i4 = 0; i4 <= c0520a3.f4467c; i4++) {
                        long j5 = c0520a3.f4468d[i4];
                        Object[] objArr = c0520a3.f[i4];
                        if (j4 < objArr.length + j5) {
                            obj = objArr[(int) (j4 - j5)];
                        }
                    }
                    throw new IndexOutOfBoundsException(Long.toString(j4));
                }
                throw new IndexOutOfBoundsException(Long.toString(j4));
            } else if (j4 >= c0520a3.f4466b) {
                throw new IndexOutOfBoundsException(Long.toString(j4));
            } else {
                obj = c0520a3.f4447e[(int) j4];
            }
            consumer.accept(obj);
        }
        return a4;
    }
}
