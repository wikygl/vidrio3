package S0;

import android.content.Context;
import android.view.View;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;
import c1.C0373f;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityRetales;

/* renamed from: S0.x0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class View$OnClickListenerC0290x0 implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2295j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2296k;

    public /* synthetic */ View$OnClickListenerC0290x0(int i4, Object obj) {
        this.f2295j = i4;
        this.f2296k = obj;
    }

    /* JADX WARN: Type inference failed for: r6v6, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityRetales, e.f] */
    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        Object obj = this.f2296k;
        switch (this.f2295j) {
            case 0:
                ActivityListaCorte.g gVar = (ActivityListaCorte.g) obj;
                androidx.fragment.app.p pVar = ActivityListaCorte.this;
                b.a aVar = new b.a(pVar);
                AlertController.b bVar = aVar.a;
                bVar.d = "Premium";
                bVar.f = pVar.getString(2131820879);
                bVar.m = true;
                aVar.c(pVar.getString(2131820598), new DialogInterface$OnClickListenerC0288w0(gVar, 1));
                aVar.b(pVar.getString(2131820593), new DialogInterface$OnClickListenerC0267l0(1));
                aVar.a().show();
                return;
            default:
                int i4 = ActivityRetales.f3095b0;
                ?? r6 = (ActivityRetales) obj;
                C0373f.l(10L, r6.getApplicationContext());
                b.a aVar2 = new b.a((Context) r6);
                aVar2.a.f = r6.getString(2131820736) + "?";
                aVar2.c(r6.getString(2131820736), new J(r6, 1));
                aVar2.b(r6.getString(2131820699), new DialogInterface$OnClickListenerC0248c(2));
                aVar2.a();
                aVar2.d();
                return;
        }
    }
}
