package C1;

import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class l implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ s f377j;

    public l(s sVar) {
        this.f377j = sVar;
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        s sVar = this.f377j;
        sVar.f391F = 2;
        sVar.f392k.finish();
    }
}
