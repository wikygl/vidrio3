package m0;

import java.util.concurrent.atomic.AtomicBoolean;
import r0.C0779a;

/* renamed from: m0.k  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class AbstractC0735k {

    /* renamed from: a  reason: collision with root package name */
    public final AtomicBoolean f5339a = new AtomicBoolean(false);

    /* renamed from: b  reason: collision with root package name */
    public final AbstractC0731g f5340b;

    /* renamed from: c  reason: collision with root package name */
    public volatile r0.e f5341c;

    public AbstractC0735k(AbstractC0731g abstractC0731g) {
        this.f5340b = abstractC0731g;
    }

    public final r0.e a() {
        this.f5340b.a();
        if (this.f5339a.compareAndSet(false, true)) {
            if (this.f5341c == null) {
                String b4 = b();
                AbstractC0731g abstractC0731g = this.f5340b;
                abstractC0731g.a();
                abstractC0731g.b();
                this.f5341c = new r0.e(((C0779a) abstractC0731g.f5305c.D()).f5691j.compileStatement(b4));
            }
            return this.f5341c;
        }
        String b5 = b();
        AbstractC0731g abstractC0731g2 = this.f5340b;
        abstractC0731g2.a();
        abstractC0731g2.b();
        return new r0.e(((C0779a) abstractC0731g2.f5305c.D()).f5691j.compileStatement(b5));
    }

    public abstract String b();

    public final void c(r0.e eVar) {
        if (eVar == this.f5341c) {
            this.f5339a.set(false);
        }
    }
}
