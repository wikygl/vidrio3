package j$.util.stream;

import java.util.concurrent.CountedCompleter;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
class A1 extends CountedCompleter {

    /* renamed from: a  reason: collision with root package name */
    protected final L0 f4244a;

    /* renamed from: b  reason: collision with root package name */
    protected final int f4245b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ int f4246c;

    /* renamed from: d  reason: collision with root package name */
    private final Object f4247d;

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public A1(A1 a12, K0 k02, int i4) {
        this(a12, k02, i4, (byte) 0);
        this.f4246c = 0;
        this.f4247d = a12.f4247d;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public A1(A1 a12, L0 l0, int i4) {
        this(a12, l0, i4, (byte) 0);
        this.f4246c = 1;
        this.f4247d = (Object[]) a12.f4247d;
    }

    A1(A1 a12, L0 l0, int i4, byte b4) {
        super(a12);
        this.f4244a = l0;
        this.f4245b = i4;
    }

    public A1(L0 l0, Object obj, int i4) {
        this.f4246c = i4;
        this.f4244a = l0;
        this.f4245b = 0;
        this.f4247d = obj;
    }

    final void a() {
        switch (this.f4246c) {
            case 0:
                ((K0) this.f4244a).d(this.f4247d, this.f4245b);
                return;
            default:
                this.f4244a.i((Object[]) this.f4247d, this.f4245b);
                return;
        }
    }

    final A1 b(int i4, int i5) {
        switch (this.f4246c) {
            case 0:
                return new A1(this, ((K0) this.f4244a).b(i4), i5);
            default:
                return new A1(this, this.f4244a.b(i4), i5);
        }
    }

    @Override // java.util.concurrent.CountedCompleter
    public final void compute() {
        A1 a12 = this;
        while (a12.f4244a.q() != 0) {
            a12.setPendingCount(a12.f4244a.q() - 1);
            int i4 = 0;
            int i5 = 0;
            while (i4 < a12.f4244a.q() - 1) {
                A1 b4 = a12.b(i4, a12.f4245b + i5);
                i5 = (int) (i5 + b4.f4244a.count());
                b4.fork();
                i4++;
            }
            a12 = a12.b(i4, a12.f4245b + i5);
        }
        a12.a();
        a12.propagateCompletion();
    }
}
