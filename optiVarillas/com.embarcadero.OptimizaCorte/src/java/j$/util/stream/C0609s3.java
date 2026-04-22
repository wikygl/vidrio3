package j$.util.stream;

import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.util.stream.s3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0609s3 implements InterfaceC0594p2 {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4584a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ LongConsumer f4585b;

    public /* synthetic */ C0609s3(LongConsumer longConsumer, int i4) {
        this.f4584a = i4;
        this.f4585b = longConsumer;
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
        switch (this.f4584a) {
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
        switch (this.f4584a) {
            case 0:
                AbstractC0637z0.k();
                throw null;
            default:
                AbstractC0637z0.k();
                throw null;
        }
    }

    @Override // j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final void accept(long j4) {
        switch (this.f4584a) {
            case 0:
                ((X2) this.f4585b).accept(j4);
                return;
            default:
                this.f4585b.accept(j4);
                return;
        }
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        switch (this.f4584a) {
            case 0:
                j((Long) obj);
                return;
            default:
                j((Long) obj);
                return;
        }
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.f4584a) {
            case 0:
                return j$.com.android.tools.r8.a.d(this, consumer);
            default:
                return j$.com.android.tools.r8.a.d(this, consumer);
        }
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        switch (this.f4584a) {
            case 0:
                return j$.com.android.tools.r8.a.g(this, longConsumer);
            default:
                return j$.com.android.tools.r8.a.g(this, longConsumer);
        }
    }

    @Override // j$.util.stream.InterfaceC0594p2
    public final /* synthetic */ void j(Long l2) {
        switch (this.f4584a) {
            case 0:
                AbstractC0637z0.i(this, l2);
                return;
            default:
                AbstractC0637z0.i(this, l2);
                return;
        }
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
        int i4 = this.f4584a;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void l(long j4) {
        int i4 = this.f4584a;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        switch (this.f4584a) {
            case 0:
                return false;
            default:
                return false;
        }
    }
}
