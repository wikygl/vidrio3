package j2;

import W1.C0324l;
import android.os.Bundle;

/* renamed from: j2.A  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0669A extends C {

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ String f4762o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ String f4763p;

    /* renamed from: q  reason: collision with root package name */
    public final /* synthetic */ Bundle f4764q;

    /* renamed from: t  reason: collision with root package name */
    public final /* synthetic */ G f4767t;

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ Long f4761n = null;

    /* renamed from: r  reason: collision with root package name */
    public final /* synthetic */ boolean f4765r = true;

    /* renamed from: s  reason: collision with root package name */
    public final /* synthetic */ boolean f4766s = true;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0669A(G g4, String str, String str2, Bundle bundle) {
        super(g4, true);
        this.f4767t = g4;
        this.f4762o = str;
        this.f4763p = str2;
        this.f4764q = bundle;
    }

    @Override // j2.C
    public final void a() {
        long longValue;
        Long l2 = this.f4761n;
        if (l2 == null) {
            longValue = this.f4773j;
        } else {
            longValue = l2.longValue();
        }
        long j4 = longValue;
        InterfaceC0675e interfaceC0675e = this.f4767t.f4790h;
        C0324l.d(interfaceC0675e);
        interfaceC0675e.i2(this.f4762o, this.f4763p, this.f4764q, this.f4765r, this.f4766s, j4);
    }
}
