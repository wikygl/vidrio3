package j$.util.stream;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/* renamed from: j$.util.stream.g3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class AbstractC0550g3 implements Spliterator {

    /* renamed from: a  reason: collision with root package name */
    final boolean f4510a;

    /* renamed from: b  reason: collision with root package name */
    final AbstractC0521b f4511b;

    /* renamed from: c  reason: collision with root package name */
    private Supplier f4512c;

    /* renamed from: d  reason: collision with root package name */
    Spliterator f4513d;

    /* renamed from: e  reason: collision with root package name */
    InterfaceC0599q2 f4514e;
    BooleanSupplier f;

    /* renamed from: g  reason: collision with root package name */
    long f4515g;

    /* renamed from: h  reason: collision with root package name */
    AbstractC0531d f4516h;

    /* renamed from: i  reason: collision with root package name */
    boolean f4517i;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractC0550g3(AbstractC0521b abstractC0521b, Spliterator spliterator, boolean z4) {
        this.f4511b = abstractC0521b;
        this.f4512c = null;
        this.f4513d = spliterator;
        this.f4510a = z4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractC0550g3(AbstractC0521b abstractC0521b, Supplier supplier, boolean z4) {
        this.f4511b = abstractC0521b;
        this.f4512c = supplier;
        this.f4513d = null;
        this.f4510a = z4;
    }

    private boolean b() {
        while (this.f4516h.count() == 0) {
            if (this.f4514e.n() || !this.f.getAsBoolean()) {
                if (this.f4517i) {
                    return false;
                }
                this.f4514e.k();
                this.f4517i = true;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean a() {
        AbstractC0531d abstractC0531d = this.f4516h;
        if (abstractC0531d == null) {
            if (this.f4517i) {
                return false;
            }
            c();
            d();
            this.f4515g = 0L;
            this.f4514e.l(this.f4513d.getExactSizeIfKnown());
            return b();
        }
        long j4 = this.f4515g + 1;
        this.f4515g = j4;
        boolean z4 = j4 < abstractC0531d.count();
        if (z4) {
            return z4;
        }
        this.f4515g = 0L;
        this.f4516h.clear();
        return b();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void c() {
        if (this.f4513d == null) {
            this.f4513d = (Spliterator) this.f4512c.get();
            this.f4512c = null;
        }
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        c();
        int z4 = EnumC0540e3.z(this.f4511b.G()) & EnumC0540e3.f;
        return (z4 & 64) != 0 ? (z4 & (-16449)) | (this.f4513d.characteristics() & 16448) : z4;
    }

    abstract void d();

    abstract AbstractC0550g3 e(Spliterator spliterator);

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        c();
        return this.f4513d.estimateSize();
    }

    @Override // j$.util.Spliterator
    public final Comparator getComparator() {
        if (j$.util.D.e(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    @Override // j$.util.Spliterator
    public final long getExactSizeIfKnown() {
        c();
        if (EnumC0540e3.SIZED.r(this.f4511b.G())) {
            return this.f4513d.getExactSizeIfKnown();
        }
        return -1L;
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return j$.util.D.e(this, i4);
    }

    public final String toString() {
        return String.format("%s[%s]", getClass().getName(), this.f4513d);
    }

    @Override // j$.util.Spliterator
    public Spliterator trySplit() {
        if (this.f4510a && this.f4516h == null && !this.f4517i) {
            c();
            Spliterator trySplit = this.f4513d.trySplit();
            if (trySplit == null) {
                return null;
            }
            return e(trySplit);
        }
        return null;
    }
}
