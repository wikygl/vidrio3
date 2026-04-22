package e3;

import b3.C0358c;
import b3.InterfaceC0362g;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class i implements InterfaceC0362g {

    /* renamed from: a  reason: collision with root package name */
    public boolean f3375a = false;

    /* renamed from: b  reason: collision with root package name */
    public boolean f3376b = false;

    /* renamed from: c  reason: collision with root package name */
    public C0358c f3377c;

    /* renamed from: d  reason: collision with root package name */
    public final f f3378d;

    public i(f fVar) {
        this.f3378d = fVar;
    }

    @Override // b3.InterfaceC0362g
    public final InterfaceC0362g b(String str) {
        if (!this.f3375a) {
            this.f3375a = true;
            this.f3378d.c(this.f3377c, str, this.f3376b);
            return this;
        }
        throw new RuntimeException("Cannot encode a second value in the ValueEncoderContext");
    }

    @Override // b3.InterfaceC0362g
    public final InterfaceC0362g c(boolean z4) {
        if (!this.f3375a) {
            this.f3375a = true;
            this.f3378d.b(this.f3377c, z4 ? 1 : 0, this.f3376b);
            return this;
        }
        throw new RuntimeException("Cannot encode a second value in the ValueEncoderContext");
    }
}
