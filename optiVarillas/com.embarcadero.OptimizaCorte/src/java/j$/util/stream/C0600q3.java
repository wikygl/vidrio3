package j$.util.stream;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.util.stream.q3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0600q3 implements InterfaceC0589o2 {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4572a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ IntConsumer f4573b;

    public /* synthetic */ C0600q3(IntConsumer intConsumer, int i4) {
        this.f4572a = i4;
        this.f4573b = intConsumer;
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
        switch (this.f4572a) {
            case 0:
                AbstractC0637z0.a();
                throw null;
            default:
                AbstractC0637z0.a();
                throw null;
        }
    }

    @Override // j$.util.stream.InterfaceC0589o2, j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        switch (this.f4572a) {
            case 0:
                ((V2) this.f4573b).accept(i4);
                return;
            default:
                this.f4573b.accept(i4);
                return;
        }
    }

    @Override // j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final /* synthetic */ void accept(long j4) {
        switch (this.f4572a) {
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
        switch (this.f4572a) {
            case 0:
                m((Integer) obj);
                return;
            default:
                m((Integer) obj);
                return;
        }
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.f4572a) {
            case 0:
                return j$.com.android.tools.r8.a.d(this, consumer);
            default:
                return j$.com.android.tools.r8.a.d(this, consumer);
        }
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        switch (this.f4572a) {
            case 0:
                return j$.com.android.tools.r8.a.f(this, intConsumer);
            default:
                return j$.com.android.tools.r8.a.f(this, intConsumer);
        }
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
        int i4 = this.f4572a;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void l(long j4) {
        int i4 = this.f4572a;
    }

    @Override // j$.util.stream.InterfaceC0589o2
    public final /* synthetic */ void m(Integer num) {
        switch (this.f4572a) {
            case 0:
                AbstractC0637z0.g(this, num);
                return;
            default:
                AbstractC0637z0.g(this, num);
                return;
        }
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        switch (this.f4572a) {
            case 0:
                return false;
            default:
                return false;
        }
    }
}
