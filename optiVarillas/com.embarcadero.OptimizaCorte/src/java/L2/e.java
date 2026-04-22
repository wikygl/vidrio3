package L2;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class e extends G3.g {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Context f1506k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ TextPaint f1507l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ G3.g f1508m;

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ d f1509n;

    public e(d dVar, Context context, TextPaint textPaint, G3.g gVar) {
        this.f1509n = dVar;
        this.f1506k = context;
        this.f1507l = textPaint;
        this.f1508m = gVar;
    }

    @Override // G3.g
    public final void B(int i4) {
        this.f1508m.B(i4);
    }

    @Override // G3.g
    public final void C(Typeface typeface, boolean z4) {
        this.f1509n.g(this.f1506k, this.f1507l, typeface);
        this.f1508m.C(typeface, z4);
    }
}
