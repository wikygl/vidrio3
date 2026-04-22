package com.embarcadero.OptimizaCorte.Activities;

import M.O;
import M.V;
import S0.C0294z0;
import T0.g;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.activity.ComponentActivity;
import androidx.appcompat.app.b;
import androidx.appcompat.widget.ActionBarContainer;
import androidx.fragment.app.p;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorteContainer;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.d;
import e.AbstractC0392a;
import e.C0397f;
import e.z;
import j$.util.Objects;
import java.util.ArrayList;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class ActivityListaCorteContainer extends C0397f {

    /* renamed from: L  reason: collision with root package name */
    public static final /* synthetic */ int f3041L = 0;

    /* renamed from: H  reason: collision with root package name */
    public TabLayout f3042H;

    /* renamed from: I  reason: collision with root package name */
    public ViewPager2 f3043I;

    /* renamed from: J  reason: collision with root package name */
    public g f3044J;

    /* renamed from: K  reason: collision with root package name */
    public ArrayList f3045K;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a implements TabLayout.d {
        public a() {
        }

        /* JADX WARN: Type inference failed for: r5v4, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorteContainer, android.app.Activity] */
        public final void a(TabLayout.f fVar) {
            if (Objects.equals(fVar.b, "+")) {
                int i4 = ActivityListaCorteContainer.f3041L;
                final ?? r5 = ActivityListaCorteContainer.this;
                View inflate = r5.getLayoutInflater().inflate(2131427448, (ViewGroup) null);
                final EditText editText = (EditText) inflate.findViewById(2131230925);
                b.a aVar = new b.a((Context) r5);
                aVar.a.q = inflate;
                aVar.c("ok", new DialogInterface.OnClickListener() { // from class: S0.C0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i5) {
                        ActivityListaCorteContainer activityListaCorteContainer = ActivityListaCorteContainer.this;
                        activityListaCorteContainer.f3045K.add(editText.getText().toString());
                        T0.g gVar = activityListaCorteContainer.f3044J;
                        gVar.f2337k.add(new X0.b());
                        ((RecyclerView.e) activityListaCorteContainer.f3044J).a.c(activityListaCorteContainer.f3042H.getTabCount() - 1);
                        TabLayout tabLayout = activityListaCorteContainer.f3042H;
                        TabLayout.f i6 = tabLayout.i();
                        i6.a("+");
                        tabLayout.b(i6, activityListaCorteContainer.f3042H.getTabCount(), tabLayout.k.isEmpty());
                        activityListaCorteContainer.f3042H.post(new A(4, activityListaCorteContainer));
                        activityListaCorteContainer.A();
                    }
                });
                aVar.a().show();
            }
        }
    }

    public final void A() {
        LinearLayout linearLayout = (LinearLayout) this.f3042H.getChildAt(0);
        for (final int i4 = 0; i4 < linearLayout.getChildCount(); i4++) {
            final TabLayout.f h4 = this.f3042H.h(i4);
            linearLayout.getChildAt(i4).setOnLongClickListener(new View.OnLongClickListener() { // from class: S0.B0
                /* JADX WARN: Type inference failed for: r0v0, types: [android.content.Context, java.lang.Object, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorteContainer] */
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view) {
                    final ?? r02 = ActivityListaCorteContainer.this;
                    final TabLayout.f fVar = h4;
                    if (fVar != null) {
                        int tabCount = r02.f3042H.getTabCount() - 1;
                        final int i5 = i4;
                        if (i5 != tabCount && r02.f3042H.getTabCount() != 2) {
                            b.a aVar = new b.a((Context) r02);
                            aVar.a.f = "Eliminar tab " + ((Object) fVar.b) + "?";
                            aVar.c("sí", new DialogInterface.OnClickListener() { // from class: S0.D0
                                @Override // android.content.DialogInterface.OnClickListener
                                public final void onClick(DialogInterface dialogInterface, int i6) {
                                    int i7;
                                    ActivityListaCorteContainer activityListaCorteContainer = ActivityListaCorteContainer.this;
                                    ArrayList arrayList = activityListaCorteContainer.f3045K;
                                    TabLayout.f fVar2 = fVar;
                                    arrayList.remove(fVar2.d);
                                    TabLayout tabLayout = activityListaCorteContainer.f3042H;
                                    tabLayout.getClass();
                                    if (fVar2.g == tabLayout) {
                                        int i8 = fVar2.d;
                                        TabLayout.f fVar3 = tabLayout.l;
                                        if (fVar3 != null) {
                                            i7 = fVar3.d;
                                        } else {
                                            i7 = 0;
                                        }
                                        tabLayout.k(i8);
                                        ArrayList arrayList2 = tabLayout.k;
                                        TabLayout.f fVar4 = (TabLayout.f) arrayList2.remove(i8);
                                        int i9 = -1;
                                        TabLayout.f fVar5 = null;
                                        if (fVar4 != null) {
                                            fVar4.g = null;
                                            fVar4.h = null;
                                            fVar4.a = null;
                                            fVar4.i = -1;
                                            fVar4.b = null;
                                            fVar4.c = null;
                                            fVar4.d = -1;
                                            fVar4.e = null;
                                            TabLayout.g0.b(fVar4);
                                        }
                                        int size = arrayList2.size();
                                        for (int i10 = i8; i10 < size; i10++) {
                                            if (((TabLayout.f) arrayList2.get(i10)).d == tabLayout.j) {
                                                i9 = i10;
                                            }
                                            ((TabLayout.f) arrayList2.get(i10)).d = i10;
                                        }
                                        tabLayout.j = i9;
                                        if (i7 == i8) {
                                            if (!arrayList2.isEmpty()) {
                                                fVar5 = (TabLayout.f) arrayList2.get(Math.max(0, i8 - 1));
                                            }
                                            tabLayout.l(fVar5, true);
                                        }
                                        ArrayList<androidx.fragment.app.k> arrayList3 = activityListaCorteContainer.f3044J.f2337k;
                                        int i11 = i5;
                                        arrayList3.remove(i11);
                                        ((RecyclerView.e) activityListaCorteContainer.f3044J).a.d(i11);
                                        TabLayout tabLayout2 = activityListaCorteContainer.f3042H;
                                        TabLayout.f i12 = tabLayout2.i();
                                        i12.a("+");
                                        tabLayout2.b(i12, activityListaCorteContainer.f3042H.getTabCount(), tabLayout2.k.isEmpty());
                                        activityListaCorteContainer.A();
                                        return;
                                    }
                                    throw new IllegalArgumentException("Tab does not belong to this TabLayout.");
                                }
                            });
                            aVar.b("Cancelar", new P(2));
                            aVar.a().show();
                            return true;
                        }
                    } else {
                        int i6 = ActivityListaCorteContainer.f3041L;
                        r02.getClass();
                    }
                    return false;
                }
            });
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v7, types: [android.view.View$OnLongClickListener, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r8v6, types: [T0.g, androidx.viewpager2.adapter.FragmentStateAdapter] */
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(2131427357);
        AbstractC0392a y4 = y();
        if (y4 != null) {
            y4.c(getString(2131820575));
            y4.b(getString(2131820928));
            y4.a(true);
            ActionBarContainer actionBarContainer = ((z) y4).f3325d;
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            O.d.s(actionBarContainer, 0.0f);
        }
        this.f3043I = findViewById(2131231150);
        ?? fragmentStateAdapter = new FragmentStateAdapter(((p) this).B.a.n, ((ComponentActivity) this).m);
        fragmentStateAdapter.f2337k = new ArrayList<>();
        this.f3044J = fragmentStateAdapter;
        TabLayout findViewById = findViewById(2131231243);
        this.f3042H = findViewById;
        findViewById.setTabMode(0);
        g gVar = this.f3044J;
        gVar.f2337k.add(new X0.b());
        this.f3043I.setAdapter(this.f3044J);
        ArrayList arrayList = new ArrayList();
        this.f3045K = arrayList;
        arrayList.add("Default");
        TabLayout tabLayout = this.f3042H;
        ViewPager2 viewPager2 = this.f3043I;
        d dVar = new d(tabLayout, viewPager2, new C0294z0(this));
        if (!dVar.e) {
            RecyclerView.e adapter = viewPager2.getAdapter();
            dVar.d = adapter;
            if (adapter != null) {
                dVar.e = true;
                viewPager2.l.a.add(new d.c(tabLayout));
                tabLayout.a(new d.d(viewPager2));
                dVar.d.a.registerObserver(new d.a(dVar));
                dVar.a();
                tabLayout.m(viewPager2.getCurrentItem(), 0.0f, true, true, true);
                TabLayout tabLayout2 = this.f3042H;
                TabLayout.f i4 = tabLayout2.i();
                i4.a("+");
                tabLayout2.b(i4, this.f3042H.getTabCount(), tabLayout2.k.isEmpty());
                A();
                this.f3042H.setOnLongClickListener(new Object());
                this.f3042H.a(new a());
                return;
            }
            throw new IllegalStateException("TabLayoutMediator attached before ViewPager2 has an adapter");
        }
        throw new IllegalStateException("TabLayoutMediator is already attached");
    }
}
