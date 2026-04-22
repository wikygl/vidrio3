package S0;

import android.view.View;
import b1.C0354b;
import com.embarcadero.OptimizaCorte.Activities.ActivityRetales;
import com.embarcadero.OptimizaCorte.Activities.ActivityTestBillingV5;
import e.C0397f;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class W0 implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2205j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ C0397f f2206k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f2207l;

    public /* synthetic */ W0(C0397f c0397f, Object obj, int i4) {
        this.f2205j = i4;
        this.f2206k = c0397f;
        this.f2207l = obj;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [android.content.Context, java.lang.Object, com.embarcadero.OptimizaCorte.Activities.ActivityTestBillingV5, android.app.Activity] */
    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        Object obj = this.f2207l;
        C0397f c0397f = this.f2206k;
        switch (this.f2205j) {
            case 0:
                ActivityRetales activityRetales = (ActivityRetales) c0397f;
                ArrayList<C0354b> arrayList = activityRetales.f3105Q;
                arrayList.add((C0354b) obj);
                activityRetales.f3103O.notifyDataSetChanged();
                ArrayList<Double> arrayList2 = activityRetales.f3108T;
                arrayList2.clear();
                arrayList2.trimToSize();
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    C0354b c0354b = arrayList.get(i4);
                    activityRetales.f3106R = c0354b;
                    activityRetales.f3107S = Integer.parseInt(c0354b.f2900c.trim().split(" ")[0]);
                    for (int i5 = 1; i5 <= activityRetales.f3107S; i5++) {
                        try {
                            arrayList2.add(Double.valueOf(activityRetales.f3106R.f2899b.trim()));
                        } catch (Exception e4) {
                            e4.printStackTrace();
                        }
                    }
                }
                arrayList2.trimToSize();
                activityRetales.f3112X.f2885l = arrayList2;
                return;
            default:
                int i6 = ActivityTestBillingV5.f3117Q;
                ?? r02 = (ActivityTestBillingV5) c0397f;
                r02.getClass();
                Y0.a b4 = Y0.a.b(r02, (Z0.a) obj);
                r02.f3126P = b4;
                Y0.b bVar = new Y0.b(r02, b4);
                if (b4.d()) {
                    bVar.a();
                    return;
                }
                return;
        }
    }
}
