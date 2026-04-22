package j$.util.stream;

import java.util.function.Consumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class H3 implements InterfaceC0599q2 {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4298a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Consumer f4299b;

    public /* synthetic */ H3(Consumer consumer, int i4) {
        this.f4298a = i4;
        this.f4299b = consumer;
    }

    private final /* synthetic */ void b(long j4) {
    }

    private final /* synthetic */ void c(long j4) {
    }

    private final /* synthetic */ void d() {
    }

    private final /* synthetic */ void e() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void accept(double d4) {
        switch (this.f4298a) {
            case 0:
                AbstractC0637z0.a();
                throw null;
            default:
                AbstractC0637z0.a();
                throw null;
        }
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void accept(int i4) {
        switch (this.f4298a) {
            case 0:
                AbstractC0637z0.k();
                throw null;
            default:
                AbstractC0637z0.k();
                throw null;
        }
    }

    @Override // j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final /* synthetic */ void accept(long j4) {
        switch (this.f4298a) {
            case 0:
                AbstractC0637z0.l();
                throw null;
            default:
                AbstractC0637z0.l();
                throw null;
        }
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        switch (this.f4298a) {
            case 0:
                ((C0520a3) this.f4299b).accept(obj);
                return;
            default:
                this.f4299b.accept(obj);
                return;
        }
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.f4298a) {
            case 0:
                return j$.com.android.tools.r8.a.d(this, consumer);
            default:
                return j$.com.android.tools.r8.a.d(this, consumer);
        }
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
        int i4 = this.f4298a;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void l(long j4) {
        int i4 = this.f4298a;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        switch (this.f4298a) {
            case 0:
                return false;
            default:
                return false;
        }
    }
}
