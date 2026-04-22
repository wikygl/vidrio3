package l;

import android.view.View;
import k.InterfaceC0683f;
import l.C0715y;

/* renamed from: l.x  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0714x extends J {

    /* renamed from: s  reason: collision with root package name */
    public final /* synthetic */ C0715y.e f5206s;

    /* renamed from: t  reason: collision with root package name */
    public final /* synthetic */ C0715y f5207t;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0714x(C0715y c0715y, View view, C0715y.e eVar) {
        super(view);
        this.f5207t = c0715y;
        this.f5206s = eVar;
    }

    @Override // l.J
    public final InterfaceC0683f b() {
        return this.f5206s;
    }

    @Override // l.J
    public final boolean c() {
        C0715y c0715y = this.f5207t;
        if (!c0715y.getInternalPopup().c()) {
            c0715y.f5214o.l(c0715y.getTextDirection(), c0715y.getTextAlignment());
            return true;
        }
        return true;
    }
}
