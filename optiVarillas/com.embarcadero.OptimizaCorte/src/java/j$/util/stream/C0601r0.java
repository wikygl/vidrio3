package j$.util.stream;

import java.util.function.Predicate;

/* renamed from: j$.util.stream.r0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0601r0 extends AbstractC0621v0 {

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ EnumC0625w0 f4575c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ Predicate f4576d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0601r0(EnumC0625w0 enumC0625w0, Predicate predicate) {
        super(enumC0625w0);
        this.f4575c = enumC0625w0;
        this.f4576d = predicate;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        boolean z4;
        boolean z5;
        if (this.f4598a) {
            return;
        }
        boolean test = this.f4576d.test(obj);
        EnumC0625w0 enumC0625w0 = this.f4575c;
        z4 = enumC0625w0.f4606a;
        if (test == z4) {
            this.f4598a = true;
            z5 = enumC0625w0.f4607b;
            this.f4599b = z5;
        }
    }
}
