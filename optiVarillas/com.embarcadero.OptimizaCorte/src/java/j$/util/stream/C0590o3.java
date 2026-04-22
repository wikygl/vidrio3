package j$.util.stream;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.util.stream.o3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0590o3 implements InterfaceC0584n2 {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4557a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ DoubleConsumer f4558b;

    public /* synthetic */ C0590o3(DoubleConsumer doubleConsumer, int i4) {
        this.f4557a = i4;
        this.f4558b = doubleConsumer;
    }

    private final /* synthetic */ void b(long j4) {
    }

    private final /* synthetic */ void c(long j4) {
    }

    private final /* synthetic */ void d() {
    }

    private final /* synthetic */ void e() {
    }

    @Override // j$.util.stream.InterfaceC0584n2, j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        switch (this.f4557a) {
            case 0:
                ((T2) this.f4558b).accept(d4);
                return;
            default:
                this.f4558b.accept(d4);
                return;
        }
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void accept(int i4) {
        switch (this.f4557a) {
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
        switch (this.f4557a) {
            case 0:
                AbstractC0637z0.l();
                throw null;
            default:
                AbstractC0637z0.l();
                throw null;
        }
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        switch (this.f4557a) {
            case 0:
                p((Double) obj);
                return;
            default:
                p((Double) obj);
                return;
        }
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.f4557a) {
            case 0:
                return j$.com.android.tools.r8.a.d(this, consumer);
            default:
                return j$.com.android.tools.r8.a.d(this, consumer);
        }
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        switch (this.f4557a) {
            case 0:
                return j$.com.android.tools.r8.a.e(this, doubleConsumer);
            default:
                return j$.com.android.tools.r8.a.e(this, doubleConsumer);
        }
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
        int i4 = this.f4557a;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void l(long j4) {
        int i4 = this.f4557a;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        switch (this.f4557a) {
            case 0:
                return false;
            default:
                return false;
        }
    }

    @Override // j$.util.stream.InterfaceC0584n2
    public final /* synthetic */ void p(Double d4) {
        switch (this.f4557a) {
            case 0:
                AbstractC0637z0.e(this, d4);
                return;
            default:
                AbstractC0637z0.e(this, d4);
                return;
        }
    }
}
