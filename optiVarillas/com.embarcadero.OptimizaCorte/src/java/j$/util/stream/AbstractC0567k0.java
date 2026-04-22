package j$.util.stream;

import j$.util.Spliterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.k0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public abstract class AbstractC0567k0 extends AbstractC0572l0 {

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ int f4533l;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ AbstractC0567k0(AbstractC0521b abstractC0521b, int i4, int i5) {
        super(abstractC0521b, i4);
        this.f4533l = i5;
    }

    @Override // j$.util.stream.AbstractC0521b
    final boolean M() {
        switch (this.f4533l) {
            case 0:
                return true;
            default:
                return false;
        }
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* bridge */ /* synthetic */ InterfaceC0587o0 parallel() {
        switch (this.f4533l) {
            case 0:
                parallel();
                return this;
            default:
                parallel();
                return this;
        }
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* bridge */ /* synthetic */ InterfaceC0587o0 sequential() {
        switch (this.f4533l) {
            case 0:
                sequential();
                return this;
            default:
                sequential();
                return this;
        }
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h
    public final /* bridge */ /* synthetic */ Spliterator spliterator() {
        switch (this.f4533l) {
            case 0:
                return spliterator();
            default:
                return spliterator();
        }
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final InterfaceC0551h unordered() {
        switch (this.f4533l) {
            case 0:
                return !H() ? this : new C0628x(this, EnumC0540e3.f4493r, 4);
            default:
                return !H() ? this : new C0628x(this, EnumC0540e3.f4493r, 4);
        }
    }
}
