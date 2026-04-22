package com.embarcadero.OptimizaCorte.Activities;

import S0.RunnableC0285v;
import S0.RunnableC0286v0;
import S0.RunnableC0287w;
import S0.View$OnClickListenerC0270n;
import S0.View$OnClickListenerC0274p;
import S0.View$OnClickListenerC0290x0;
import T0.b;
import V0.c;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;
import b1.C0353a;
import b1.C0354b;
import c1.C0371d;
import com.embarcadero.OptimizaCorte.Activities.ActivityRetales;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import d1.C0379b;
import e.AbstractC0392a;
import e.C0397f;
import e1.C0407a;
import i2.c0;
import java.util.ArrayList;
import java.util.Arrays;
import t1.C0803e;
import t1.C0804f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class ActivityRetales extends C0397f {

    /* renamed from: b0  reason: collision with root package name */
    public static final /* synthetic */ int f3095b0 = 0;

    /* renamed from: H  reason: collision with root package name */
    public C0804f f3096H;

    /* renamed from: I  reason: collision with root package name */
    public FrameLayout f3097I;

    /* renamed from: J  reason: collision with root package name */
    public Button f3098J;

    /* renamed from: K  reason: collision with root package name */
    public Button f3099K;

    /* renamed from: L  reason: collision with root package name */
    public TextInputEditText f3100L;

    /* renamed from: M  reason: collision with root package name */
    public TextInputEditText f3101M;

    /* renamed from: N  reason: collision with root package name */
    public ListView f3102N;

    /* renamed from: O  reason: collision with root package name */
    public b f3103O;

    /* renamed from: P  reason: collision with root package name */
    public Button f3104P;

    /* renamed from: R  reason: collision with root package name */
    public C0354b f3106R;

    /* renamed from: S  reason: collision with root package name */
    public int f3107S;

    /* renamed from: U  reason: collision with root package name */
    public boolean f3109U;

    /* renamed from: V  reason: collision with root package name */
    public int f3110V;

    /* renamed from: W  reason: collision with root package name */
    public ArrayList<String> f3111W;

    /* renamed from: X  reason: collision with root package name */
    public C0353a f3112X;

    /* renamed from: Y  reason: collision with root package name */
    public C0407a f3113Y;

    /* renamed from: Z  reason: collision with root package name */
    public Y0.a f3114Z;

    /* renamed from: Q  reason: collision with root package name */
    public final ArrayList<C0354b> f3105Q = new ArrayList<>();

    /* renamed from: T  reason: collision with root package name */
    public final ArrayList<Double> f3108T = new ArrayList<>();

    /* renamed from: a0  reason: collision with root package name */
    public final a f3115a0 = new a();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class a implements Z0.a {
        public a() {
        }

        @Override // Z0.a
        public final void a() {
            ActivityRetales.this.runOnUiThread(new RunnableC0286v0(3, this));
        }

        @Override // Z0.a
        public final void b() {
            ActivityRetales.this.runOnUiThread(new RunnableC0287w(3, this));
        }

        @Override // Z0.a
        public final void c(ArrayList arrayList) {
            ActivityRetales.this.runOnUiThread(new RunnableC0285v(this, 2));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final synchronized void A() {
        try {
            if (this.f3112X != null) {
                if (this.f3113Y == null) {
                    this.f3113Y = new C0407a(getApplicationContext());
                }
                synchronized (this) {
                    this.f3113Y.b(this.f3112X);
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onBackPressed() {
        A();
        overridePendingTransition(2130771998, 2130771999);
        super/*androidx.activity.ComponentActivity*/.onBackPressed();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onCreate(Bundle bundle) {
        ArrayList<Double> arrayList;
        C0407a c0407a = new C0407a(getApplicationContext());
        this.f3113Y = c0407a;
        this.f3112X = c0407a.a();
        super.onCreate(bundle);
        setContentView(2131427360);
        AbstractC0392a y4 = y();
        if (y4 != null) {
            y4.c(getString(2131820575));
            y4.b(getString(2131820931));
            y4.a(true);
        }
        this.f3097I = (FrameLayout) findViewById(2131230794);
        C0804f c0804f = new C0804f(this);
        this.f3096H = c0804f;
        c0804f.setAdUnitId(getString(2131820578));
        this.f3097I.addView(this.f3096H);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        this.f3096H.setAdSize(C0803e.a(this, (int) (displayMetrics.widthPixels / displayMetrics.density)));
        final View findViewById = findViewById(2131230790);
        findViewById.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: S0.S0
            /* JADX WARN: Type inference failed for: r0v1, types: [com.embarcadero.OptimizaCorte.Activities.ActivityRetales, java.lang.Object, e.f, android.app.Activity] */
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public final void onGlobalLayout() {
                int i4 = ActivityRetales.f3095b0;
                ?? r02 = ActivityRetales.this;
                r02.getClass();
                View view = findViewById;
                int i5 = 0;
                if (view.getRootView().getHeight() - view.getHeight() > TypedValue.applyDimension(1, 200.0f, r02.getResources().getDisplayMetrics())) {
                    DisplayMetrics displayMetrics2 = new DisplayMetrics();
                    r02.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics2);
                    int i6 = displayMetrics2.widthPixels;
                    int i7 = displayMetrics2.heightPixels;
                    float f = i6 / displayMetrics2.xdpi;
                    float f4 = i7 / displayMetrics2.ydpi;
                    if (Math.round(Math.sqrt((f4 * f4) + (f * f))) >= 5.0d) {
                        C0804f c0804f2 = r02.f3096H;
                        if (r02.f3112X.f2897x) {
                            i5 = 8;
                        }
                        c0804f2.setVisibility(i5);
                        return;
                    }
                    r02.f3096H.setVisibility(8);
                    return;
                }
                C0804f c0804f3 = r02.f3096H;
                if (r02.f3112X.f2897x) {
                    i5 = 8;
                }
                c0804f3.setVisibility(i5);
            }
        });
        this.f3098J = (Button) findViewById(2131230825);
        this.f3104P = (Button) findViewById(2131230827);
        this.f3099K = (Button) findViewById(2131230829);
        this.f3100L = findViewById(2131230927);
        this.f3101M = findViewById(2131230934);
        this.f3099K.setOnClickListener(new View$OnClickListenerC0270n(this, 2));
        boolean z4 = this.f3112X.f2890q;
        this.f3109U = z4;
        if (z4) {
            this.f3100L.setKeyListener(DigitsKeyListener.getInstance("0123456789a. /"));
            TextInputEditText textInputEditText = this.f3100L;
            textInputEditText.setFilters(new InputFilter[]{new C0371d(textInputEditText)});
            this.f3100L.setRawInputType(524289);
        } else {
            this.f3100L.setRawInputType(8194);
        }
        ArrayList<C0354b> arrayList2 = this.f3105Q;
        this.f3103O = new b(this, arrayList2);
        ListView listView = (ListView) findViewById(2131231035);
        this.f3102N = listView;
        listView.setAdapter((ListAdapter) this.f3103O);
        this.f3102N.setTranscriptMode(1);
        this.f3102N.setLongClickable(true);
        this.f3102N.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: S0.T0
            /* JADX WARN: Type inference failed for: r8v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityRetales] */
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public final boolean onItemLongClick(AdapterView adapterView, final View view, final int i4, long j4) {
                final ?? r8 = ActivityRetales.this;
                final String str = r8.f3103O.getItem(i4).f2899b;
                final C0354b item = r8.f3103O.getItem(i4);
                b.a aVar = new b.a((Context) r8);
                AlertController.b bVar = aVar.a;
                bVar.f = r8.getString(2131820693) + str + r8.getString(2131820694);
                bVar.d = r8.getString(2131820695);
                aVar.c(r8.getString(2131820918), new DialogInterface.OnClickListener() { // from class: S0.U0
                    /* JADX WARN: Type inference failed for: r7v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityRetales, e.f] */
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i5) {
                        ?? r7 = ActivityRetales.this;
                        ArrayList<C0354b> arrayList3 = r7.f3105Q;
                        arrayList3.remove(i4);
                        arrayList3.trimToSize();
                        r7.f3103O.notifyDataSetChanged();
                        Snackbar h4 = Snackbar.h(view, r7.getString(2131820696) + str + r7.getString(2131820697));
                        h4.i(r7.getString(2131820698), new W0(r7, item, 0));
                        h4.j();
                        StringBuilder sb = new StringBuilder();
                        ArrayList<String> arrayList4 = new ArrayList<>();
                        if (arrayList3.size() > 0) {
                            for (int i6 = 0; i6 < arrayList3.size(); i6++) {
                                sb.append(arrayList3.get(i6).f2899b);
                                sb.append("@");
                                sb.append(arrayList3.get(i6).f2900c);
                                sb.append("@");
                            }
                            for (String str2 : new StringBuilder(sb.substring(0, sb.lastIndexOf("@"))).toString().split("@")) {
                                arrayList4.add(str2);
                            }
                        }
                        arrayList4.removeAll(Arrays.asList("", null));
                        arrayList4.trimToSize();
                        r7.f3112X.f2884k = arrayList4;
                        ArrayList<Double> arrayList5 = r7.f3108T;
                        arrayList5.clear();
                        arrayList5.trimToSize();
                        for (int i7 = 0; i7 < arrayList3.size(); i7++) {
                            C0354b c0354b = arrayList3.get(i7);
                            r7.f3106R = c0354b;
                            r7.f3107S = Integer.parseInt(c0354b.f2900c.trim().split(" ")[0]);
                            for (int i8 = 1; i8 <= r7.f3107S; i8++) {
                                try {
                                    arrayList5.add(Double.valueOf(r7.f3106R.f2899b.trim()));
                                } catch (Exception e4) {
                                    e4.printStackTrace();
                                }
                            }
                        }
                        arrayList5.trimToSize();
                        r7.f3112X.f2885l = arrayList5;
                    }
                });
                aVar.b(r8.getString(2131820699), new DialogInterface.OnClickListener() { // from class: S0.V0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i5) {
                        int i6 = ActivityRetales.f3095b0;
                        androidx.fragment.app.p pVar = ActivityRetales.this;
                        Toast.makeText((Context) pVar, (CharSequence) pVar.getString(2131820700), 0).show();
                        dialogInterface.cancel();
                    }
                });
                aVar.a();
                aVar.d();
                return true;
            }
        });
        this.f3103O.clear();
        ArrayList<String> arrayList3 = this.f3112X.f2884k;
        this.f3111W = arrayList3;
        try {
        } catch (NumberFormatException e4) {
            e4.printStackTrace();
        }
        if (arrayList3.size() > 1) {
            this.f3110V = 0;
            while (this.f3110V < this.f3111W.size()) {
                int i4 = this.f3110V;
                if (i4 % 2 == 0) {
                    this.f3103O.add(new C0354b(this.f3111W.get(i4), this.f3111W.get(this.f3110V + 1), this.f3112X));
                }
                this.f3103O.notifyDataSetChanged();
                this.f3110V++;
            }
            this.f3110V = 0;
            while (true) {
                int i5 = this.f3110V;
                int size = arrayList2.size();
                arrayList = this.f3108T;
                if (i5 >= size) {
                    break;
                }
                C0354b c0354b = arrayList2.get(this.f3110V);
                this.f3106R = c0354b;
                this.f3107S = Integer.parseInt(c0354b.f2900c.trim().split(" ")[0]);
                for (int i6 = 1; i6 <= this.f3107S; i6++) {
                    try {
                        arrayList.add(Double.valueOf(this.f3106R.f2899b.trim()));
                    } catch (Exception e5) {
                        e5.printStackTrace();
                    }
                }
                this.f3110V++;
                e4.printStackTrace();
            }
            arrayList.trimToSize();
            this.f3112X.f2885l = arrayList;
        }
        this.f3098J.setOnClickListener(new View$OnClickListenerC0274p(1, this));
        this.f3104P.setOnClickListener(new View$OnClickListenerC0290x0(1, this));
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
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            onBackPressed();
        } else if (itemId == 2131231071) {
            new c(this, this.f3096H).f();
        } else if (itemId == 2131231072) {
            A();
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://sites.google.com/view/soldier-developer/cutter-cutting-optimizer/privacy-policy")));
        } else if (itemId == 2131231070) {
            A();
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/account/subscriptions?sku=remove_ads&package=com.embarcadero.OptimizaCorte")));
        } else if (itemId == 2131231073) {
            C0379b.b(this, this.f3112X);
        }
        return super/*android.app.Activity*/.onOptionsItemSelected(menuItem);
    }

    public final void onPause() {
        A();
        super.onPause();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onResume() {
        ArrayList<Double> arrayList;
        C0407a c0407a = new C0407a(getApplicationContext());
        this.f3113Y = c0407a;
        this.f3112X = c0407a.a();
        this.f3103O.clear();
        ArrayList<String> arrayList2 = this.f3112X.f2884k;
        this.f3111W = arrayList2;
        arrayList2.removeAll(Arrays.asList("", null));
        this.f3111W.trimToSize();
        try {
        } catch (NumberFormatException e4) {
            e4.printStackTrace();
        }
        if (this.f3111W.size() > 1) {
            this.f3110V = 0;
            while (this.f3110V < this.f3111W.size()) {
                int i4 = this.f3110V;
                if (i4 % 2 == 0) {
                    this.f3103O.add(new C0354b(this.f3111W.get(i4), this.f3111W.get(this.f3110V + 1), this.f3112X));
                }
                this.f3103O.notifyDataSetChanged();
                this.f3110V++;
            }
            this.f3110V = 0;
            while (true) {
                int i5 = this.f3110V;
                ArrayList<C0354b> arrayList3 = this.f3105Q;
                int size = arrayList3.size();
                arrayList = this.f3108T;
                if (i5 >= size) {
                    break;
                }
                C0354b c0354b = arrayList3.get(this.f3110V);
                this.f3106R = c0354b;
                this.f3107S = Integer.parseInt(c0354b.f2900c.trim().split(" ")[0]);
                for (int i6 = 1; i6 <= this.f3107S; i6++) {
                    try {
                        arrayList.add(Double.valueOf(this.f3106R.f2899b.trim()));
                    } catch (Exception e5) {
                        e5.printStackTrace();
                    }
                }
                this.f3110V++;
                e4.printStackTrace();
            }
            arrayList.trimToSize();
            this.f3112X.f2885l = arrayList;
        }
        a aVar = this.f3115a0;
        Y0.a b4 = Y0.a.b(this, aVar);
        this.f3114Z = b4;
        b4.f2825a = aVar;
        b4.i();
        if (this.f3114Z.d()) {
            this.f3114Z.a();
        }
        super.onResume();
    }
}
