package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class U extends CountedCompleter {

    /* renamed from: a  reason: collision with root package name */
    private Spliterator f4416a;

    /* renamed from: b  reason: collision with root package name */
    private final InterfaceC0599q2 f4417b;

    /* renamed from: c  reason: collision with root package name */
    private final AbstractC0521b f4418c;

    /* renamed from: d  reason: collision with root package name */
    private long f4419d;

    U(U u4, Spliterator spliterator) {
        super(u4);
        this.f4416a = spliterator;
        this.f4417b = u4.f4417b;
        this.f4419d = u4.f4419d;
        this.f4418c = u4.f4418c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public U(AbstractC0521b abstractC0521b, Spliterator spliterator, InterfaceC0599q2 interfaceC0599q2) {
        super(null);
        this.f4417b = interfaceC0599q2;
        this.f4418c = abstractC0521b;
        this.f4416a = spliterator;
        this.f4419d = 0L;
    }

    @Override // java.util.concurrent.CountedCompleter
    public final void compute() {
        Spliterator trySplit;
        Spliterator spliterator = this.f4416a;
        long estimateSize = spliterator.estimateSize();
        long j4 = this.f4419d;
        if (j4 == 0) {
            j4 = AbstractC0536e.g(estimateSize);
            this.f4419d = j4;
        }
        boolean r4 = EnumC0540e3.SHORT_CIRCUIT.r(this.f4418c.G());
        InterfaceC0599q2 interfaceC0599q2 = this.f4417b;
        boolean z4 = false;
        U u4 = this;
        while (true) {
            if (r4 && interfaceC0599q2.n()) {
                break;
            } else if (estimateSize <= j4 || (trySplit = spliterator.trySplit()) == null) {
                break;
            } else {
                U u5 = new U(u4, trySplit);
                u4.addToPendingCount(1);
                if (z4) {
                    spliterator = trySplit;
                } else {
                    U u6 = u4;
                    u4 = u5;
                    u5 = u6;
                }
                z4 = !z4;
                u4.fork();
                u4 = u5;
                estimateSize = spliterator.estimateSize();
            }
        }
        u4.f4418c.w(spliterator, interfaceC0599q2);
        u4.f4416a = null;
        u4.propagateCompletion();
    }
}
