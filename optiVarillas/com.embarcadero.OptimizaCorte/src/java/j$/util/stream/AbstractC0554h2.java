package j$.util.stream;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.h2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public abstract class AbstractC0554h2 extends AbstractC0559i2 {

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ int f4523l;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ AbstractC0554h2(AbstractC0521b abstractC0521b, int i4, int i5) {
        super(abstractC0521b, i4);
        this.f4523l = i5;
    }

    @Override // j$.util.stream.AbstractC0521b
    final boolean M() {
        switch (this.f4523l) {
            case 0:
                return true;
            default:
                return false;
        }
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final InterfaceC0551h unordered() {
        switch (this.f4523l) {
            case 0:
                return !H() ? this : new AbstractC0554h2(this, EnumC0540e3.f4493r, 1);
            default:
                return !H() ? this : new AbstractC0554h2(this, EnumC0540e3.f4493r, 1);
        }
    }
}
