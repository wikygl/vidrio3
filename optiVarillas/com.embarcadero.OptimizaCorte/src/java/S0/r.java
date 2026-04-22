package S0;

import android.view.View;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;
import c1.C0373f;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class r implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2274j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2275k;

    public /* synthetic */ r(int i4, Object obj) {
        this.f2274j = i4;
        this.f2275k = obj;
    }

    /* JADX WARN: Type inference failed for: r6v10, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte] */
    /* JADX WARN: Type inference failed for: r6v3, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, android.app.Activity] */
    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        switch (this.f2274j) {
            case 0:
                ?? r6 = (ActivityInicio) this.f2275k;
                ActivityInicio.c cVar = r6.f2991z0;
                Y0.a b4 = Y0.a.b(r6, cVar);
                r6.f2988w0 = b4;
                b4.f2825a = cVar;
                Y0.b bVar = new Y0.b(r6, b4);
                if (!r6.f2985t0 && b4.d()) {
                    bVar.a();
                    return;
                }
                return;
            case 1:
                ActivityListaCorte.f fVar = (ActivityListaCorte.f) this.f2275k;
                androidx.fragment.app.p pVar = ActivityListaCorte.this;
                b.a aVar = new b.a(pVar);
                AlertController.b bVar2 = aVar.a;
                bVar2.d = "Premium";
                bVar2.f = pVar.getString(2131820879);
                bVar2.m = true;
                aVar.c(pVar.getString(2131820598), new DialogInterface$OnClickListenerC0288w0(fVar, 0));
                aVar.b(pVar.getString(2131820593), new DialogInterface$OnClickListenerC0267l0(1));
                aVar.a().show();
                return;
            default:
                ?? r62 = ActivityListaCorte.this;
                C0373f.l(10L, r62.getApplicationContext());
                String[] strArr = r62.f3011Q;
                int length = (r62.f3012R + 1) % strArr.length;
                r62.f3012R = length;
                Integer num = (Integer) r62.f3010P.get(strArr[length]);
                if (num != null) {
                    r62.f3009O.setImageResource(num.intValue());
                    r62.f3009O.setTag(strArr[r62.f3012R]);
                    return;
                }
                return;
        }
    }
}
