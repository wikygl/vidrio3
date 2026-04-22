package j$.util.stream;

import j$.util.Spliterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class H1 extends AbstractC0637z0 {

    /* renamed from: h  reason: collision with root package name */
    public final /* synthetic */ int f4296h;

    public /* synthetic */ H1(int i4) {
        this.f4296h = i4;
    }

    @Override // j$.util.stream.AbstractC0637z0, j$.util.stream.K3
    public final Object b(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        switch (this.f4296h) {
            case 0:
                return EnumC0540e3.SIZED.r(abstractC0521b.G()) ? Long.valueOf(spliterator.getExactSizeIfKnown()) : (Long) super.b(abstractC0521b, spliterator);
            case 1:
                return EnumC0540e3.SIZED.r(abstractC0521b.G()) ? Long.valueOf(spliterator.getExactSizeIfKnown()) : (Long) super.b(abstractC0521b, spliterator);
            case 2:
                return EnumC0540e3.SIZED.r(abstractC0521b.G()) ? Long.valueOf(spliterator.getExactSizeIfKnown()) : (Long) super.b(abstractC0521b, spliterator);
            default:
                return EnumC0540e3.SIZED.r(abstractC0521b.G()) ? Long.valueOf(spliterator.getExactSizeIfKnown()) : (Long) super.b(abstractC0521b, spliterator);
        }
    }

    @Override // j$.util.stream.AbstractC0637z0, j$.util.stream.K3
    public final Object c(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        switch (this.f4296h) {
            case 0:
                return EnumC0540e3.SIZED.r(abstractC0521b.G()) ? Long.valueOf(spliterator.getExactSizeIfKnown()) : (Long) super.c(abstractC0521b, spliterator);
            case 1:
                return EnumC0540e3.SIZED.r(abstractC0521b.G()) ? Long.valueOf(spliterator.getExactSizeIfKnown()) : (Long) super.c(abstractC0521b, spliterator);
            case 2:
                return EnumC0540e3.SIZED.r(abstractC0521b.G()) ? Long.valueOf(spliterator.getExactSizeIfKnown()) : (Long) super.c(abstractC0521b, spliterator);
            default:
                return EnumC0540e3.SIZED.r(abstractC0521b.G()) ? Long.valueOf(spliterator.getExactSizeIfKnown()) : (Long) super.c(abstractC0521b, spliterator);
        }
    }

    @Override // j$.util.stream.AbstractC0637z0, j$.util.stream.K3
    public final int d() {
        switch (this.f4296h) {
            case 0:
                return EnumC0540e3.f4493r;
            case 1:
                return EnumC0540e3.f4493r;
            case 2:
                return EnumC0540e3.f4493r;
            default:
                return EnumC0540e3.f4493r;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.Object, j$.util.stream.V1] */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.Object, j$.util.stream.V1] */
    /* JADX WARN: Type inference failed for: r0v3, types: [java.lang.Object, j$.util.stream.V1] */
    /* JADX WARN: Type inference failed for: r0v4, types: [java.lang.Object, j$.util.stream.V1] */
    @Override // j$.util.stream.AbstractC0637z0
    public final V1 f0() {
        switch (this.f4296h) {
            case 0:
                return new Object();
            case 1:
                return new Object();
            case 2:
                return new Object();
            default:
                return new Object();
        }
    }
}
