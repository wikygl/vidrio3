package C3;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class b0 extends d0 {

    /* renamed from: l  reason: collision with root package name */
    public final boolean f454l;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public b0(Y y4) {
        super(true);
        C0159i c0159i;
        C0159i c0159i2;
        boolean z4 = true;
        I(y4);
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = d0.f458k;
        InterfaceC0158h interfaceC0158h = (InterfaceC0158h) atomicReferenceFieldUpdater.get(this);
        if (interfaceC0158h instanceof C0159i) {
            c0159i = (C0159i) interfaceC0158h;
        } else {
            c0159i = null;
        }
        if (c0159i != null) {
            d0 p4 = c0159i.p();
            while (!p4.D()) {
                InterfaceC0158h interfaceC0158h2 = (InterfaceC0158h) atomicReferenceFieldUpdater.get(p4);
                if (interfaceC0158h2 instanceof C0159i) {
                    c0159i2 = (C0159i) interfaceC0158h2;
                } else {
                    c0159i2 = null;
                }
                if (c0159i2 != null) {
                    p4 = c0159i2.p();
                }
            }
            this.f454l = z4;
        }
        z4 = false;
        this.f454l = z4;
    }

    @Override // C3.d0
    public final boolean D() {
        return this.f454l;
    }
}
