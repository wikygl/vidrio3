package l;

import android.view.View;
import android.view.Window;
import k.C0678a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class d0 implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final C0678a f5116j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ androidx.appcompat.widget.d f5117k;

    public d0(androidx.appcompat.widget.d dVar) {
        this.f5117k = dVar;
        this.f5116j = new C0678a(dVar.a.getContext(), dVar.i);
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        androidx.appcompat.widget.d dVar = this.f5117k;
        Window.Callback callback = dVar.l;
        if (callback != null && dVar.m) {
            callback.onMenuItemSelected(0, this.f5116j);
        }
    }
}
