package S0;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;
import c1.C0373f;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityRetales;
import e.C0397f;

/* renamed from: S0.n  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class View$OnClickListenerC0270n implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2260j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ C0397f f2261k;

    public /* synthetic */ View$OnClickListenerC0270n(C0397f c0397f, int i4) {
        this.f2260j = i4;
        this.f2261k = c0397f;
    }

    /* JADX WARN: Type inference failed for: r1v8, types: [android.content.DialogInterface$OnClickListener, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r3v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, java.lang.Object, e.f] */
    /* JADX WARN: Type inference failed for: r3v3, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityRetales, java.lang.Object] */
    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        androidx.fragment.app.p pVar = this.f2261k;
        switch (this.f2260j) {
            case 0:
                int i4 = ActivityInicio.f2947A0;
                ?? r32 = (ActivityInicio) pVar;
                r32.getClass();
                C0373f.l(10L, r32);
                b.a aVar = new b.a((Context) r32);
                AlertController.b bVar = aVar.a;
                bVar.f = r32.getString(2131820736) + "?";
                bVar.m = true;
                aVar.c(r32.getString(2131820918), new DialogInterface$OnClickListenerC0256g(r32, 0));
                aVar.b(r32.getString(2131820699), new DialogInterface$OnClickListenerC0258h(0));
                aVar.a().show();
                return;
            case 1:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                androidx.fragment.app.p pVar2 = (ActivityListaCorte) pVar;
                pVar2.getClass();
                b.a aVar2 = new b.a(pVar2);
                AlertController.b bVar2 = aVar2.a;
                bVar2.d = "Premium";
                bVar2.f = pVar2.getString(2131820879);
                bVar2.m = true;
                aVar2.c(pVar2.getString(2131820598), new D(0, pVar2));
                aVar2.b(pVar2.getString(2131820593), (DialogInterface.OnClickListener) new Object());
                aVar2.a().show();
                return;
            default:
                int i5 = ActivityRetales.f3095b0;
                ?? r33 = (ActivityRetales) pVar;
                r33.getClass();
                C0373f.l(10L, r33);
                r33.onBackPressed();
                return;
        }
    }
}
