package com.embarcadero.OptimizaCorte.Activities;

import D1.E;
import M.C;
import Q2.g;
import S0.A;
import S0.D;
import S0.M;
import V0.c;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;
import b1.C0353a;
import com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas;
import com.google.android.material.snackbar.Snackbar;
import d1.C0379b;
import e.AbstractC0392a;
import e.C0397f;
import e1.C0407a;
import i2.c0;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import t1.C0803e;
import t1.C0804f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class ActivityListasGuardadas extends C0397f {

    /* renamed from: T  reason: collision with root package name */
    public static final /* synthetic */ int f3047T = 0;

    /* renamed from: H  reason: collision with root package name */
    public C0804f f3048H;

    /* renamed from: I  reason: collision with root package name */
    public FrameLayout f3049I;

    /* renamed from: J  reason: collision with root package name */
    public ListView f3050J;

    /* renamed from: K  reason: collision with root package name */
    public E f3051K;

    /* renamed from: L  reason: collision with root package name */
    public Cursor f3052L;

    /* renamed from: M  reason: collision with root package name */
    public SimpleCursorAdapter f3053M;

    /* renamed from: N  reason: collision with root package name */
    public C0353a f3054N;

    /* renamed from: O  reason: collision with root package name */
    public C0407a f3055O;

    /* renamed from: P  reason: collision with root package name */
    public Intent f3056P;

    /* renamed from: Q  reason: collision with root package name */
    public Intent f3057Q;

    /* renamed from: R  reason: collision with root package name */
    public Y0.a f3058R;

    /* renamed from: S  reason: collision with root package name */
    public final a f3059S = new a();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class a implements Z0.a {
        public a() {
        }

        @Override // Z0.a
        public final void a() {
            ActivityListasGuardadas.this.runOnUiThread(new C(4, this));
        }

        @Override // Z0.a
        public final void b() {
            ActivityListasGuardadas.this.runOnUiThread(new A(5, this));
        }

        @Override // Z0.a
        public final void c(ArrayList arrayList) {
            ActivityListasGuardadas.this.runOnUiThread(new g(2, this));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void A() {
        b.a aVar = new b.a(this);
        String string = getString(2131820663);
        AlertController.b bVar = aVar.a;
        bVar.d = string;
        bVar.f = getString(2131820661) + "\n\n" + getString(2131820662);
        aVar.c(getString(2131820927), new D(2, this));
        bVar.m = false;
        aVar.a().show();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final synchronized void B() {
        try {
            if (this.f3054N != null) {
                if (this.f3055O == null) {
                    this.f3055O = new C0407a(getApplicationContext());
                }
                synchronized (this) {
                    this.f3055O.b(this.f3054N);
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onBackPressed() {
        B();
        overridePendingTransition(2130771998, 2130771999);
        super/*androidx.activity.ComponentActivity*/.onBackPressed();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onCreate(Bundle bundle) {
        C0407a c0407a = new C0407a(getApplicationContext());
        this.f3055O = c0407a;
        this.f3054N = c0407a.a();
        super.onCreate(bundle);
        setContentView(2131427358);
        this.f3049I = (FrameLayout) findViewById(2131230792);
        C0804f c0804f = new C0804f(this);
        this.f3048H = c0804f;
        c0804f.setAdUnitId(getString(2131820578));
        this.f3049I.addView(this.f3048H);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        this.f3048H.setAdSize(C0803e.a(this, (int) (displayMetrics.widthPixels / displayMetrics.density)));
        this.f3056P = new Intent((Context) this, (Class<?>) ActivityListaCorte.class).setFlags(335544320);
        this.f3057Q = new Intent((Context) this, (Class<?>) ActivityInicio.class).setFlags(335544320);
        AbstractC0392a y4 = y();
        if (y4 != null) {
            y4.c(getString(2131820575));
            y4.b(getString(2131820930));
            y4.a(true);
        }
        this.f3051K = new E((Context) this);
        this.f3050J = (ListView) findViewById(2131231039);
        this.f3052L = this.f3051K.e();
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, 2131427393, this.f3052L, new String[]{"NOMBRE", "FECHA"}, new int[]{2131231315, 2131231310}, 0);
        this.f3053M = simpleCursorAdapter;
        this.f3050J.setAdapter((ListAdapter) simpleCursorAdapter);
        this.f3050J.setLongClickable(true);
        this.f3050J.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: S0.E0
            /* JADX WARN: Type inference failed for: r9v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas, e.f] */
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public final boolean onItemLongClick(AdapterView adapterView, final View view, int i4, long j4) {
                final ?? r9 = ActivityListasGuardadas.this;
                r9.f3052L.moveToPosition(i4);
                Cursor cursor = r9.f3052L;
                final String string = cursor.getString(cursor.getColumnIndexOrThrow("NOMBRE"));
                Cursor cursor2 = r9.f3052L;
                final String string2 = cursor2.getString(cursor2.getColumnIndexOrThrow("LISTA_MEDIDAS"));
                Cursor cursor3 = r9.f3052L;
                final String string3 = cursor3.getString(cursor3.getColumnIndexOrThrow("CONFIG"));
                Cursor cursor4 = r9.f3052L;
                final String string4 = cursor4.getString(cursor4.getColumnIndexOrThrow("RETALES"));
                b.a aVar = new b.a((Context) r9);
                AlertController.b bVar = aVar.a;
                bVar.f = r9.getString(2131820735) + string + "\"?";
                bVar.d = r9.getString(2131820736);
                aVar.c(r9.getString(2131820918), new DialogInterface.OnClickListener() { // from class: S0.G0
                    /* JADX WARN: Type inference failed for: r1v0, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas] */
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i5) {
                        final ?? r12 = ActivityListasGuardadas.this;
                        D1.E e4 = r12.f3051K;
                        final String str = string;
                        ((SQLiteDatabase) e4.f633j).delete("PROYECTOS", "NOMBRE=?", new String[]{str});
                        Cursor e5 = r12.f3051K.e();
                        r12.f3052L = e5;
                        r12.f3053M.changeCursor(e5);
                        r12.f3053M.notifyDataSetChanged();
                        Snackbar h4 = Snackbar.h(view, r12.getString(2131820919) + str + r12.getString(2131820920));
                        String string5 = r12.getString(2131820698);
                        final String str2 = string3;
                        final String str3 = string4;
                        final String str4 = string2;
                        h4.i(string5, new View.OnClickListener() { // from class: S0.H0
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view2) {
                                ActivityListasGuardadas activityListasGuardadas = ActivityListasGuardadas.this;
                                D1.E e6 = activityListasGuardadas.f3051K;
                                e6.getClass();
                                ((SQLiteDatabase) e6.f633j).insert("PROYECTOS", null, D1.E.h(str, str4, DateFormat.getDateTimeInstance(2, 3).format(new Date()), str2, str3));
                                Cursor e7 = activityListasGuardadas.f3051K.e();
                                activityListasGuardadas.f3052L = e7;
                                activityListasGuardadas.f3053M.changeCursor(e7);
                                activityListasGuardadas.f3053M.notifyDataSetChanged();
                            }
                        });
                        h4.j();
                    }
                });
                DialogInterface$OnClickListenerC0257g0 dialogInterface$OnClickListenerC0257g0 = new DialogInterface$OnClickListenerC0257g0(r9, 1);
                bVar.i = bVar.a.getText(2131820699);
                bVar.j = dialogInterface$OnClickListenerC0257g0;
                aVar.a();
                aVar.d();
                return true;
            }
        });
        this.f3050J.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: S0.F0
            /* JADX WARN: Removed duplicated region for block: B:48:0x018b  */
            /* JADX WARN: Removed duplicated region for block: B:51:0x019d  */
            /* JADX WARN: Removed duplicated region for block: B:54:0x01b7  */
            /* JADX WARN: Removed duplicated region for block: B:57:0x01d8  */
            /* JADX WARN: Removed duplicated region for block: B:58:0x01e6  */
            /* JADX WARN: Removed duplicated region for block: B:65:0x0230 A[LOOP:1: B:63:0x022d->B:65:0x0230, LOOP_END] */
            /* JADX WARN: Type inference failed for: r2v0, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas, android.app.Activity] */
            @Override // android.widget.AdapterView.OnItemClickListener
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public final void onItemClick(android.widget.AdapterView r24, android.view.View r25, int r26, long r27) {
                /*
                    Method dump skipped, instructions count: 576
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: S0.F0.onItemClick(android.widget.AdapterView, android.view.View, int, long):void");
            }
        });
        if (this.f3053M.isEmpty()) {
            b.a aVar = new b.a(this);
            String string = getString(2131820712);
            AlertController.b bVar = aVar.a;
            bVar.d = string;
            bVar.f = getString(2131820713);
            aVar.c(getString(2131820918), new M(this, 1));
            bVar.m = false;
            aVar.d();
        }
    }

    public final boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(2131558400, menu);
        boolean z4 = false;
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
        menu.getItem(4).setVisible(false);
        menu.getItem(5).setVisible(false);
        menu.getItem(6).setVisible(false);
        menu.getItem(7).setVisible(false);
        MenuItem item = menu.getItem(8);
        c0 c0Var = c.f;
        if (c0Var != null && c0Var.a() != 1) {
            z4 = true;
        }
        item.setVisible(z4);
        menu.getItem(9).setVisible(true);
        menu.getItem(10).setVisible(true);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            onBackPressed();
        } else if (itemId == 2131231071) {
            new c(this, this.f3048H).f();
        } else if (itemId == 2131231072) {
            B();
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://sites.google.com/view/soldier-developer/cutter-cutting-optimizer/privacy-policy")));
        } else if (itemId == 2131231070) {
            B();
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/account/subscriptions?sku=remove_ads&package=com.embarcadero.OptimizaCorte")));
        } else if (itemId == 2131231073) {
            C0379b.b(this, this.f3054N);
        }
        return super/*android.app.Activity*/.onOptionsItemSelected(menuItem);
    }

    public final void onPause() {
        B();
        super.onPause();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onResume() {
        C0407a c0407a = new C0407a(getApplicationContext());
        this.f3055O = c0407a;
        this.f3054N = c0407a.a();
        a aVar = this.f3059S;
        Y0.a b4 = Y0.a.b(this, aVar);
        this.f3058R = b4;
        b4.f2825a = aVar;
        b4.i();
        if (this.f3058R.d()) {
            this.f3058R.a();
        }
        super.onResume();
    }
}
