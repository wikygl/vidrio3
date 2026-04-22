package S0;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;
import b1.C0353a;
import b1.C0354b;
import c1.C0370c;
import c1.C0373f;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/* renamed from: S0.r0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class View$OnClickListenerC0278r0 implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2276j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2277k;

    public /* synthetic */ View$OnClickListenerC0278r0(int i4, Object obj) {
        this.f2276j = i4;
        this.f2277k = obj;
    }

    /* JADX WARN: Type inference failed for: r0v21, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte] */
    /* JADX WARN: Type inference failed for: r0v6, types: [java.lang.Object, java.util.Comparator] */
    /* JADX WARN: Type inference failed for: r5v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r5v4, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion] */
    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        ArrayList<C0354b> arrayList;
        boolean z4;
        HashSet hashSet;
        ArrayList arrayList2;
        ArrayList arrayList3;
        ArrayList arrayList4;
        Object obj = this.f2277k;
        switch (this.f2276j) {
            case 0:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                final ?? r5 = (ActivityListaCorte) obj;
                r5.getClass();
                r5.f3013S = new ArrayList<>();
                int i4 = 0;
                while (true) {
                    arrayList = r5.f3017W;
                    if (i4 < arrayList.size()) {
                        if (!r5.f3025e0.f2897x) {
                            C0354b c0354b = arrayList.get(i4);
                            c0354b.f = "typeRect";
                            arrayList.set(i4, c0354b);
                        }
                        if (!arrayList.get(i4).f.equals("typeLeft") && !arrayList.get(i4).f.equals("typeRight") && !arrayList.get(i4).f.equals("typeBoth")) {
                            i4++;
                        }
                    }
                }
                C0353a c0353a = r5.f3025e0;
                c0353a.f2878d = C0373f.c(c0353a.f2878d, 45.0d);
                ConnectivityManager connectivityManager = (ConnectivityManager) r5.getSystemService("connectivity");
                if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                if (z4) {
                    if (U0.a.a(r5) && !r5.f3025e0.f2897x) {
                        U0.a.b(r5, view, r5.getString(2131820573), r5.getString(2131820572), r5.getString(2131820640)).show();
                        return;
                    }
                    C0373f.l(10L, r5);
                    try {
                        Collections.sort(arrayList, new Object());
                        final ArrayList<b1.c> i5 = C0373f.i(arrayList, r5.f3025e0);
                        ArrayList arrayList5 = new ArrayList();
                        for (int i6 = 0; i6 < i5.size(); i6++) {
                            arrayList5.add(Double.valueOf(i5.get(i6).f2905c));
                        }
                        final ArrayList arrayList6 = new ArrayList(r5.f3025e0.f2885l);
                        Collections.sort(arrayList6, Collections.reverseOrder());
                        for (int i7 = 0; i7 < arrayList6.size(); i7++) {
                            C0353a c0353a2 = r5.f3025e0;
                            if (c0353a2.f2877c >= c0353a2.f2878d) {
                                double doubleValue = ((Double) arrayList6.get(i7)).doubleValue();
                                C0353a c0353a3 = r5.f3025e0;
                                arrayList6.set(i7, Double.valueOf((doubleValue - c0353a3.f2877c) + c0353a3.f2878d));
                            } else {
                                arrayList6.set(i7, Double.valueOf(((Double) arrayList6.get(i7)).doubleValue() - r5.f3025e0.f2877c));
                            }
                        }
                        ArrayList arrayList7 = new ArrayList(r5.f3025e0.f2894u);
                        Collections.sort(arrayList7, Collections.reverseOrder());
                        for (int i8 = 0; i8 < arrayList7.size(); i8++) {
                            C0353a c0353a4 = r5.f3025e0;
                            if (c0353a4.f2877c >= c0353a4.f2878d) {
                                double doubleValue2 = ((Double) arrayList7.get(i8)).doubleValue();
                                C0353a c0353a5 = r5.f3025e0;
                                arrayList7.set(i8, Double.valueOf((doubleValue2 - c0353a5.f2877c) + c0353a5.f2878d));
                            } else {
                                arrayList7.set(i8, Double.valueOf(((Double) arrayList7.get(i8)).doubleValue() - r5.f3025e0.f2877c));
                            }
                        }
                        for (int i9 = 0; i9 < arrayList7.size(); i9++) {
                            int i10 = 0;
                            while (i10 < arrayList6.size()) {
                                if (((Double) arrayList6.get(i10)).equals(arrayList7.get(i9))) {
                                    arrayList6.remove(i10);
                                    i10--;
                                }
                                i10++;
                            }
                        }
                        final D1.l0 f = C0373f.f(((Double) arrayList7.get(0)).doubleValue(), arrayList5);
                        ArrayList arrayList8 = new ArrayList((ArrayList) f.f736k);
                        final ArrayList arrayList9 = new ArrayList((ArrayList) f.f736k);
                        ArrayList arrayList10 = new ArrayList((ArrayList) f.f736k);
                        r5.f3025e0.f = ((ArrayList) f.f736k).size();
                        r5.f3025e0.f2880g = ((ArrayList) f.f737l).size();
                        final int size = ((ArrayList) f.f736k).size();
                        final int size2 = ((ArrayList) f.f737l).size();
                        HashSet hashSet2 = new HashSet(arrayList7);
                        arrayList7.clear();
                        arrayList7.addAll(hashSet2);
                        ArrayList arrayList11 = new ArrayList(new HashSet((ArrayList) f.f736k));
                        Collections.sort(arrayList11, Collections.reverseOrder());
                        ArrayList arrayList12 = new ArrayList();
                        final ArrayList arrayList13 = new ArrayList();
                        int i11 = 0;
                        while (i11 < arrayList11.size()) {
                            ArrayList<C0354b> arrayList14 = arrayList;
                            int i12 = 0;
                            while (true) {
                                if (i12 < i5.size()) {
                                    arrayList4 = arrayList7;
                                    if (((Double) arrayList11.get(i11)).doubleValue() == i5.get(i12).f2905c) {
                                        arrayList12.add(Double.valueOf(i5.get(i12).f2904b));
                                    } else {
                                        i12++;
                                        arrayList7 = arrayList4;
                                    }
                                } else {
                                    arrayList4 = arrayList7;
                                }
                            }
                            i11++;
                            arrayList7 = arrayList4;
                            arrayList = arrayList14;
                        }
                        final ArrayList arrayList15 = arrayList7;
                        ArrayList<C0354b> arrayList16 = arrayList;
                        int i13 = 0;
                        while (i13 < arrayList12.size()) {
                            if (r5.f3025e0.f2890q) {
                                hashSet = hashSet2;
                                arrayList2 = arrayList10;
                                arrayList3 = arrayList8;
                                arrayList13.add(new C0354b(C0373f.j(((Double) arrayList12.get(i13)).doubleValue(), r5.f3025e0.f2889p), Collections.frequency((ArrayList) f.f736k, arrayList11.get(i13))));
                            } else {
                                hashSet = hashSet2;
                                arrayList2 = arrayList10;
                                arrayList3 = arrayList8;
                                arrayList13.add(new C0354b(C0373f.g(((Double) arrayList12.get(i13)).doubleValue()), Collections.frequency((ArrayList) f.f736k, arrayList11.get(i13))));
                            }
                            i13++;
                            arrayList8 = arrayList3;
                            arrayList10 = arrayList2;
                            hashSet2 = hashSet;
                        }
                        HashSet hashSet3 = hashSet2;
                        final ArrayList arrayList17 = arrayList10;
                        final ArrayList arrayList18 = arrayList8;
                        ArrayList arrayList19 = new ArrayList(new HashSet((ArrayList) f.f737l));
                        Collections.sort(arrayList19, Collections.reverseOrder());
                        ArrayList arrayList20 = new ArrayList();
                        for (int i14 = 0; i14 < arrayList19.size(); i14++) {
                            int i15 = 0;
                            while (true) {
                                if (i15 < i5.size()) {
                                    if (((Double) arrayList19.get(i14)).doubleValue() == i5.get(i15).f2905c) {
                                        arrayList20.add(Double.valueOf(i5.get(i15).f2904b));
                                    } else {
                                        i15++;
                                    }
                                }
                            }
                        }
                        final ArrayList arrayList21 = new ArrayList();
                        for (int i16 = 0; i16 < arrayList20.size(); i16++) {
                            if (r5.f3025e0.f2890q) {
                                arrayList21.add(new C0354b(C0373f.j(((Double) arrayList20.get(i16)).doubleValue(), r5.f3025e0.f2889p), Collections.frequency((ArrayList) f.f737l, arrayList19.get(i16))));
                            } else {
                                arrayList21.add(new C0354b(C0373f.g(((Double) arrayList20.get(i16)).doubleValue()), Collections.frequency((ArrayList) f.f737l, arrayList19.get(i16))));
                            }
                        }
                        C0370c.f2938a = false;
                        ActivityListaCorte.f3001m0 = new Thread(new Runnable() { // from class: S0.N
                            @Override // java.lang.Runnable
                            public final void run() {
                                ProgressDialog progressDialog2 = ActivityListaCorte.l0;
                                ActivityListaCorte.this.C(arrayList9, arrayList6, arrayList17, arrayList18, arrayList15, size, size2, arrayList13, arrayList21, i5);
                            }
                        });
                        ArrayList arrayList22 = new ArrayList((ArrayList) f.f736k);
                        ArrayList arrayList23 = new ArrayList(hashSet3);
                        Collections.sort(arrayList22, Collections.reverseOrder());
                        for (int i17 = 0; i17 < arrayList23.size(); i17++) {
                            if (((Double) arrayList22.get(0)).doubleValue() > ((Double) arrayList23.get(i17)).doubleValue()) {
                                b.a aVar = new b.a((Context) r5);
                                AlertController.b bVar = aVar.a;
                                bVar.d = "Warning";
                                bVar.f = "Some measurements exceed the length of some stock bar, check optimization with caution.\n\nDo you wish to continue?";
                                aVar.c("Continue", new DialogInterface.OnClickListener() { // from class: S0.O
                                    /* JADX WARN: Type inference failed for: r2v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte] */
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public final void onClick(DialogInterface dialogInterface, int i18) {
                                        ?? r22 = ActivityListaCorte.this;
                                        if (!r22.f3017W.isEmpty() && !((ArrayList) f.f736k).isEmpty()) {
                                            ActivityListaCorte.f3001m0.start();
                                        } else {
                                            Toast.makeText((Context) r22, r22.getString(2131820701), 0).show();
                                        }
                                    }
                                });
                                bVar.m = false;
                                aVar.b("Cancel", new P(0));
                                aVar.a().show();
                                return;
                            }
                        }
                        if (!arrayList16.isEmpty() && !((ArrayList) f.f736k).isEmpty()) {
                            ActivityListaCorte.f3001m0.start();
                            return;
                        } else {
                            Toast.makeText((Context) r5, r5.getString(2131820701), 0).show();
                            return;
                        }
                    } catch (Exception e4) {
                        Toast.makeText((Context) r5, "Error: " + e4.getMessage(), 1).show();
                        return;
                    }
                }
                U0.a.b(r5, view, r5.getString(2131820638), r5.getString(2131820639), r5.getString(2131820640)).show();
                return;
            case 1:
                ?? r02 = ActivityListaCorte.this;
                C0373f.l(10L, r02.getApplicationContext());
                String[] strArr = r02.f3011Q;
                int length = (r02.f3012R + 1) % strArr.length;
                r02.f3012R = length;
                Integer num = (Integer) r02.f3010P.get(strArr[length]);
                if (num != null) {
                    r02.f3009O.setImageResource(num.intValue());
                    r02.f3009O.setTag(strArr[r02.f3012R]);
                    return;
                }
                return;
            case 2:
                final ActivityListaCorte.g gVar = (ActivityListaCorte.g) obj;
                androidx.fragment.app.p pVar = ActivityListaCorte.this;
                b.a aVar2 = new b.a(pVar);
                AlertController.b bVar2 = aVar2.a;
                bVar2.d = "Premium";
                bVar2.f = pVar.getString(2131820879);
                bVar2.m = true;
                aVar2.c(pVar.getString(2131820598), new DialogInterface.OnClickListener() { // from class: S0.y0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i18) {
                        Toast.makeText((Context) ActivityListaCorte.this, (CharSequence) "User not logged in Google Play!", 0).show();
                    }
                });
                aVar2.b(pVar.getString(2131820593), new DialogInterface$OnClickListenerC0267l0(1));
                aVar2.a().show();
                return;
            default:
                ?? r52 = (ActivityOptimizacion) obj;
                if (r52.f3076V.f2897x) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction("android.intent.action.GET_CONTENT");
                    r52.f3078X.F(intent);
                    return;
                }
                r52.f3082b0 = true;
                ActivityOptimizacion.a aVar3 = r52.f3085e0;
                Y0.a b4 = Y0.a.b(r52, aVar3);
                r52.f3083c0 = b4;
                b4.f2825a = aVar3;
                b4.i();
                if (r52.f3083c0.d()) {
                    r52.f3083c0.a();
                    return;
                }
                return;
        }
    }
}
