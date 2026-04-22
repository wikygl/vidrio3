package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public abstract class AbstractC0521b implements InterfaceC0551h {

    /* renamed from: a  reason: collision with root package name */
    private final AbstractC0521b f4448a;

    /* renamed from: b  reason: collision with root package name */
    private final AbstractC0521b f4449b;

    /* renamed from: c  reason: collision with root package name */
    protected final int f4450c;

    /* renamed from: d  reason: collision with root package name */
    private AbstractC0521b f4451d;

    /* renamed from: e  reason: collision with root package name */
    private int f4452e;
    private int f;

    /* renamed from: g  reason: collision with root package name */
    private Spliterator f4453g;

    /* renamed from: h  reason: collision with root package name */
    private boolean f4454h;

    /* renamed from: i  reason: collision with root package name */
    private boolean f4455i;

    /* renamed from: j  reason: collision with root package name */
    private Runnable f4456j;

    /* renamed from: k  reason: collision with root package name */
    private boolean f4457k;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractC0521b(Spliterator spliterator, int i4, boolean z4) {
        this.f4449b = null;
        this.f4453g = spliterator;
        this.f4448a = this;
        int i5 = EnumC0540e3.f4482g & i4;
        this.f4450c = i5;
        this.f = (~(i5 << 1)) & EnumC0540e3.f4487l;
        this.f4452e = 0;
        this.f4457k = z4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractC0521b(AbstractC0521b abstractC0521b, int i4) {
        if (abstractC0521b.f4454h) {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
        abstractC0521b.f4454h = true;
        abstractC0521b.f4451d = this;
        this.f4449b = abstractC0521b;
        this.f4450c = EnumC0540e3.f4483h & i4;
        this.f = EnumC0540e3.j(i4, abstractC0521b.f);
        AbstractC0521b abstractC0521b2 = abstractC0521b.f4448a;
        this.f4448a = abstractC0521b2;
        if (M()) {
            abstractC0521b2.f4455i = true;
        }
        this.f4452e = abstractC0521b.f4452e + 1;
    }

    private Spliterator O(int i4) {
        int i5;
        int i6;
        AbstractC0521b abstractC0521b = this.f4448a;
        Spliterator spliterator = abstractC0521b.f4453g;
        if (spliterator != null) {
            abstractC0521b.f4453g = null;
            if (abstractC0521b.f4457k && abstractC0521b.f4455i) {
                AbstractC0521b abstractC0521b2 = abstractC0521b.f4451d;
                int i7 = 1;
                while (abstractC0521b != this) {
                    int i8 = abstractC0521b2.f4450c;
                    if (abstractC0521b2.M()) {
                        if (EnumC0540e3.SHORT_CIRCUIT.r(i8)) {
                            i8 &= ~EnumC0540e3.f4496u;
                        }
                        spliterator = abstractC0521b2.L(abstractC0521b, spliterator);
                        if (spliterator.hasCharacteristics(64)) {
                            i5 = (~EnumC0540e3.f4495t) & i8;
                            i6 = EnumC0540e3.f4494s;
                        } else {
                            i5 = (~EnumC0540e3.f4494s) & i8;
                            i6 = EnumC0540e3.f4495t;
                        }
                        i8 = i5 | i6;
                        i7 = 0;
                    }
                    abstractC0521b2.f4452e = i7;
                    abstractC0521b2.f = EnumC0540e3.j(i8, abstractC0521b.f);
                    i7++;
                    AbstractC0521b abstractC0521b3 = abstractC0521b2;
                    abstractC0521b2 = abstractC0521b2.f4451d;
                    abstractC0521b = abstractC0521b3;
                }
            }
            if (i4 != 0) {
                this.f = EnumC0540e3.j(i4, this.f);
            }
            return spliterator;
        }
        throw new IllegalStateException("source already consumed or closed");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final L0 A(IntFunction intFunction) {
        AbstractC0521b abstractC0521b;
        if (this.f4454h) {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
        this.f4454h = true;
        if (this.f4448a.f4457k && (abstractC0521b = this.f4449b) != null && M()) {
            this.f4452e = 0;
            return K(abstractC0521b, abstractC0521b.O(0), intFunction);
        }
        return y(O(0), true, intFunction);
    }

    abstract L0 B(AbstractC0521b abstractC0521b, Spliterator spliterator, boolean z4, IntFunction intFunction);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final long C(Spliterator spliterator) {
        if (EnumC0540e3.SIZED.r(this.f)) {
            return spliterator.getExactSizeIfKnown();
        }
        return -1L;
    }

    abstract boolean D(Spliterator spliterator, InterfaceC0599q2 interfaceC0599q2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract EnumC0545f3 E();

    /* JADX INFO: Access modifiers changed from: package-private */
    public final EnumC0545f3 F() {
        AbstractC0521b abstractC0521b = this;
        while (abstractC0521b.f4452e > 0) {
            abstractC0521b = abstractC0521b.f4449b;
        }
        return abstractC0521b.E();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int G() {
        return this.f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean H() {
        return EnumC0540e3.ORDERED.r(this.f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final /* synthetic */ Spliterator I() {
        return O(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract D0 J(long j4, IntFunction intFunction);

    L0 K(AbstractC0521b abstractC0521b, Spliterator spliterator, IntFunction intFunction) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    Spliterator L(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        return K(abstractC0521b, spliterator, new C0571l(17)).spliterator();
    }

    abstract boolean M();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Spliterator P() {
        AbstractC0521b abstractC0521b = this.f4448a;
        if (this == abstractC0521b) {
            if (this.f4454h) {
                throw new IllegalStateException("stream has already been operated upon or closed");
            }
            this.f4454h = true;
            Spliterator spliterator = abstractC0521b.f4453g;
            if (spliterator != null) {
                abstractC0521b.f4453g = null;
                return spliterator;
            }
            throw new IllegalStateException("source already consumed or closed");
        }
        throw new IllegalStateException();
    }

    abstract Spliterator Q(AbstractC0521b abstractC0521b, Supplier supplier, boolean z4);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final InterfaceC0599q2 R(Spliterator spliterator, InterfaceC0599q2 interfaceC0599q2) {
        w(spliterator, S((InterfaceC0599q2) Objects.requireNonNull(interfaceC0599q2)));
        return interfaceC0599q2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final InterfaceC0599q2 S(InterfaceC0599q2 interfaceC0599q2) {
        Objects.requireNonNull(interfaceC0599q2);
        AbstractC0521b abstractC0521b = this;
        while (abstractC0521b.f4452e > 0) {
            AbstractC0521b abstractC0521b2 = abstractC0521b.f4449b;
            interfaceC0599q2 = abstractC0521b.N(abstractC0521b2.f, interfaceC0599q2);
            abstractC0521b = abstractC0521b2;
        }
        return interfaceC0599q2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Spliterator T(Spliterator spliterator) {
        return this.f4452e == 0 ? spliterator : Q(this, new C0516a(spliterator, 6), this.f4448a.f4457k);
    }

    @Override // java.lang.AutoCloseable
    public final void close() {
        this.f4454h = true;
        this.f4453g = null;
        AbstractC0521b abstractC0521b = this.f4448a;
        Runnable runnable = abstractC0521b.f4456j;
        if (runnable != null) {
            abstractC0521b.f4456j = null;
            runnable.run();
        }
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final boolean isParallel() {
        return this.f4448a.f4457k;
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final InterfaceC0551h onClose(Runnable runnable) {
        if (this.f4454h) {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
        Objects.requireNonNull(runnable);
        AbstractC0521b abstractC0521b = this.f4448a;
        Runnable runnable2 = abstractC0521b.f4456j;
        if (runnable2 != null) {
            runnable = new J3(runnable2, runnable);
        }
        abstractC0521b.f4456j = runnable;
        return this;
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final InterfaceC0551h parallel() {
        this.f4448a.f4457k = true;
        return this;
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final InterfaceC0551h sequential() {
        this.f4448a.f4457k = false;
        return this;
    }

    @Override // j$.util.stream.InterfaceC0551h
    public Spliterator spliterator() {
        if (this.f4454h) {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
        this.f4454h = true;
        AbstractC0521b abstractC0521b = this.f4448a;
        if (this == abstractC0521b) {
            Spliterator spliterator = abstractC0521b.f4453g;
            if (spliterator != null) {
                abstractC0521b.f4453g = null;
                return spliterator;
            }
            throw new IllegalStateException("source already consumed or closed");
        }
        return Q(this, new C0516a(this, 0), abstractC0521b.f4457k);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void w(Spliterator spliterator, InterfaceC0599q2 interfaceC0599q2) {
        Objects.requireNonNull(interfaceC0599q2);
        if (EnumC0540e3.SHORT_CIRCUIT.r(this.f)) {
            x(spliterator, interfaceC0599q2);
            return;
        }
        interfaceC0599q2.l(spliterator.getExactSizeIfKnown());
        spliterator.forEachRemaining(interfaceC0599q2);
        interfaceC0599q2.k();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean x(Spliterator spliterator, InterfaceC0599q2 interfaceC0599q2) {
        AbstractC0521b abstractC0521b = this;
        while (abstractC0521b.f4452e > 0) {
            abstractC0521b = abstractC0521b.f4449b;
        }
        interfaceC0599q2.l(spliterator.getExactSizeIfKnown());
        boolean D4 = abstractC0521b.D(spliterator, interfaceC0599q2);
        interfaceC0599q2.k();
        return D4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final L0 y(Spliterator spliterator, boolean z4, IntFunction intFunction) {
        if (this.f4448a.f4457k) {
            return B(this, spliterator, z4, intFunction);
        }
        D0 J3 = J(C(spliterator), intFunction);
        R(spliterator, J3);
        return J3.a();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Object z(K3 k32) {
        if (this.f4454h) {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
        this.f4454h = true;
        return this.f4448a.f4457k ? k32.c(this, O(k32.d())) : k32.b(this, O(k32.d()));
    }
}
