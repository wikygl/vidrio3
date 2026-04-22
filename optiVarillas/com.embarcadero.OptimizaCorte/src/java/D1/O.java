package D1;

import android.content.Context;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class O extends AbstractC0200u {

    /* renamed from: b  reason: collision with root package name */
    public final E1.q f643b;

    /* renamed from: c  reason: collision with root package name */
    public final String f644c;

    public O(Context context, String str, String str2) {
        this.f643b = new E1.q(z1.p.f6575A.f6578c.w(context, str));
        this.f644c = str2;
    }

    @Override // D1.AbstractC0200u
    public final void a() {
        this.f643b.i(this.f644c);
    }
}
