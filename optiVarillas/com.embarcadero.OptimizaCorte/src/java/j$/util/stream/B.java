package j$.util.stream;

import j$.util.Spliterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public abstract class B extends C {

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ int f4254l;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ B(AbstractC0521b abstractC0521b, int i4, int i5) {
        super(abstractC0521b, i4);
        this.f4254l = i5;
    }

    @Override // j$.util.stream.AbstractC0521b
    final boolean M() {
        switch (this.f4254l) {
            case 0:
                return true;
            default:
                return false;
        }
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* bridge */ /* synthetic */ F parallel() {
        switch (this.f4254l) {
            case 0:
                parallel();
                return this;
            default:
                parallel();
                return this;
        }
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* bridge */ /* synthetic */ F sequential() {
        switch (this.f4254l) {
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
        switch (this.f4254l) {
            case 0:
                return spliterator();
            default:
                return spliterator();
        }
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final InterfaceC0551h unordered() {
        switch (this.f4254l) {
            case 0:
                return !H() ? this : new C0620v(this, EnumC0540e3.f4493r, 1);
            default:
                return !H() ? this : new C0620v(this, EnumC0540e3.f4493r, 1);
        }
    }
}
