package com.embarcadero.OptimizaCorte.Activities;

import B.RunnableC0145a;
import D1.E;
import S0.A;
import S0.C0284u0;
import S0.DialogInterface$OnClickListenerC0257g0;
import S0.H;
import S0.P;
import S0.RunnableC0286v0;
import S0.RunnableC0287w;
import S0.RunnableC0293z;
import S0.View$OnClickListenerC0270n;
import S0.View$OnClickListenerC0278r0;
import S0.W;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;
import b1.C0353a;
import b1.C0354b;
import c.AbstractC0363a;
import c1.C0373f;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.google.android.material.textfield.TextInputEditText;
import d1.C0379b;
import e.AbstractC0392a;
import e.C0397f;
import e1.C0407a;
import i2.c0;
import j$.util.Objects;
import j.C0652f;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import t1.C0802d;
import t1.C0803e;
import t1.C0804f;
import t1.C0807i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class ActivityListaCorte extends C0397f {
    public static ProgressDialog l0;

    /* renamed from: m0  reason: collision with root package name */
    public static Thread f3001m0;

    /* renamed from: H  reason: collision with root package name */
    public Button f3002H;

    /* renamed from: I  reason: collision with root package name */
    public TextInputEditText f3003I;

    /* renamed from: J  reason: collision with root package name */
    public TextInputEditText f3004J;

    /* renamed from: K  reason: collision with root package name */
    public TextInputEditText f3005K;

    /* renamed from: L  reason: collision with root package name */
    public ListView f3006L;

    /* renamed from: M  reason: collision with root package name */
    public Button f3007M;

    /* renamed from: N  reason: collision with root package name */
    public Button f3008N;

    /* renamed from: O  reason: collision with root package name */
    public ImageButton f3009O;

    /* renamed from: U  reason: collision with root package name */
    public long f3015U;

    /* renamed from: V  reason: collision with root package name */
    public int f3016V;

    /* renamed from: Y  reason: collision with root package name */
    public T0.b f3019Y;

    /* renamed from: Z  reason: collision with root package name */
    public C0804f f3020Z;

    /* renamed from: a0  reason: collision with root package name */
    public F1.a f3021a0;

    /* renamed from: b0  reason: collision with root package name */
    public a f3022b0;

    /* renamed from: c0  reason: collision with root package name */
    public androidx.activity.result.c f3023c0;

    /* renamed from: d0  reason: collision with root package name */
    public boolean f3024d0;

    /* renamed from: e0  reason: collision with root package name */
    public C0353a f3025e0;

    /* renamed from: f0  reason: collision with root package name */
    public C0407a f3026f0;

    /* renamed from: g0  reason: collision with root package name */
    public Y0.a f3027g0;

    /* renamed from: h0  reason: collision with root package name */
    public Timer f3028h0;

    /* renamed from: P  reason: collision with root package name */
    public final HashMap f3010P = new HashMap();

    /* renamed from: Q  reason: collision with root package name */
    public final String[] f3011Q = {"typeRect", "typeLeft", "typeRight", "typeBoth"};

    /* renamed from: R  reason: collision with root package name */
    public int f3012R = 0;

    /* renamed from: S  reason: collision with root package name */
    public ArrayList<b1.e> f3013S = new ArrayList<>();

    /* renamed from: T  reason: collision with root package name */
    public ArrayList<b1.e> f3014T = new ArrayList<>();

    /* renamed from: W  reason: collision with root package name */
    public final ArrayList<C0354b> f3017W = new ArrayList<>();

    /* renamed from: X  reason: collision with root package name */
    public final ArrayList<C0354b> f3018X = new ArrayList<>();

    /* renamed from: i0  reason: collision with root package name */
    public final W f3029i0 = new InputFilter() { // from class: S0.W
        @Override // android.text.InputFilter
        public final CharSequence filter(CharSequence charSequence, int i4, int i5, Spanned spanned, int i6, int i7) {
            ProgressDialog progressDialog = ActivityListaCorte.l0;
            ActivityListaCorte activityListaCorte = ActivityListaCorte.this;
            activityListaCorte.getClass();
            if (charSequence != null) {
                for (int i8 = 0; i8 < charSequence.length(); i8++) {
                    Editable text = activityListaCorte.f3003I.getText();
                    Objects.requireNonNull(text);
                    if (text.toString().contains(".") && String.valueOf(charSequence.charAt(i8)).equals(".")) {
                        return charSequence.toString().substring(0, i8);
                    }
                    if (activityListaCorte.f3003I.getText().toString().contains(" ") && String.valueOf(charSequence.charAt(i8)).equals(" ")) {
                        return charSequence.toString().substring(0, i8);
                    }
                    if (activityListaCorte.f3003I.getText().toString().contains("/") && String.valueOf(charSequence.charAt(i8)).equals("/")) {
                        return charSequence.toString().substring(0, i8);
                    }
                    if (!"0123456789. /".contains(String.valueOf(charSequence.charAt(i8)))) {
                        return charSequence.toString().substring(0, i8);
                    }
                }
                return charSequence;
            }
            return charSequence;
        }
    };

    /* renamed from: j0  reason: collision with root package name */
    public final f f3030j0 = new f();

    /* renamed from: k0  reason: collision with root package name */
    public final g f3031k0 = new g();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends F1.b {
        public a() {
        }

        @Override // G3.g
        public final void v(C0807i c0807i) {
            ActivityListaCorte.this.f3021a0 = null;
        }

        @Override // G3.g
        public final void x(Object obj) {
            ActivityListaCorte.this.f3021a0 = (F1.a) obj;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class b extends G3.g {

        /* renamed from: k  reason: collision with root package name */
        public final /* synthetic */ Intent f3033k;

        public b(Intent intent) {
            this.f3033k = intent;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte, android.app.Activity] */
        @Override // G3.g
        public final void u() {
            ProgressDialog progressDialog = ActivityListaCorte.l0;
            ?? r02 = ActivityListaCorte.this;
            r02.F();
            r02.startActivity(this.f3033k);
            r02.overridePendingTransition(2130771998, 2130771999);
            Log.d("TAG", "The ad was dismissed.");
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte, android.app.Activity] */
        @Override // G3.g
        public final void w() {
            ProgressDialog progressDialog = ActivityListaCorte.l0;
            ?? r02 = ActivityListaCorte.this;
            r02.F();
            r02.startActivity(this.f3033k);
            r02.overridePendingTransition(2130771998, 2130771999);
            Log.d("TAG", "The ad failed to show.");
        }

        @Override // G3.g
        public final void y() {
            ActivityListaCorte.this.f3021a0 = null;
            Log.d("TAG", "The ad was shown.");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class c extends G3.g {

        /* renamed from: k  reason: collision with root package name */
        public final /* synthetic */ Intent f3035k;

        public c(Intent intent) {
            this.f3035k = intent;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte, android.app.Activity] */
        @Override // G3.g
        public final void u() {
            ProgressDialog progressDialog = ActivityListaCorte.l0;
            ?? r02 = ActivityListaCorte.this;
            r02.F();
            r02.startActivity(this.f3035k);
            r02.overridePendingTransition(2130771998, 2130771999);
            r02.finish();
            Log.d("TAG", "The ad was dismissed.");
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte, android.app.Activity] */
        @Override // G3.g
        public final void w() {
            ProgressDialog progressDialog = ActivityListaCorte.l0;
            ?? r02 = ActivityListaCorte.this;
            r02.F();
            r02.startActivity(this.f3035k);
            r02.overridePendingTransition(2130771998, 2130771999);
            r02.finish();
            Log.d("TAG", "The ad failed to show.");
        }

        @Override // G3.g
        public final void y() {
            ActivityListaCorte.this.f3021a0 = null;
            Log.d("TAG", "The ad was shown.");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class d extends G3.g {
        public d() {
        }

        @Override // G3.g
        public final void u() {
            ActivityListaCorte.this.overridePendingTransition(2130771998, 2130771999);
            Log.d("TAG", "The ad was dismissed.");
        }

        @Override // G3.g
        public final void w() {
            ActivityListaCorte.this.overridePendingTransition(2130771998, 2130771999);
            Log.d("TAG", "The ad failed to show.");
        }

        @Override // G3.g
        public final void y() {
            ActivityListaCorte.this.f3021a0 = null;
            Log.d("TAG", "The ad was shown.");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class e extends TimerTask {
        public e() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public final void run() {
            ActivityListaCorte.this.runOnUiThread(new RunnableC0287w(1, this));
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class f implements Z0.a {
        public f() {
        }

        @Override // Z0.a
        public final void a() {
            ActivityListaCorte.this.runOnUiThread(new RunnableC0293z(1, this));
        }

        @Override // Z0.a
        public final void b() {
            ActivityListaCorte.this.runOnUiThread(new RunnableC0286v0(0, this));
        }

        @Override // Z0.a
        public final void c(ArrayList arrayList) {
            ActivityListaCorte.this.runOnUiThread(new RunnableC0145a(1, this));
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class g implements Z0.a {
        public g() {
        }

        @Override // Z0.a
        public final void a() {
            ActivityListaCorte.this.runOnUiThread(new A(3, this));
        }

        @Override // Z0.a
        public final void b() {
            ActivityListaCorte.this.runOnUiThread(new RunnableC0145a(2, this));
        }

        @Override // Z0.a
        public final void c(ArrayList arrayList) {
            ActivityListaCorte.this.runOnUiThread(new RunnableC0286v0(1, this));
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class h implements Comparator<C0354b> {
        @Override // java.util.Comparator
        public final int compare(C0354b c0354b, C0354b c0354b2) {
            return c0354b.f2899b.compareTo(c0354b2.f2899b);
        }
    }

    public static String B(String str) {
        return str.replace("typeLeft", "typeLe").replace("typeRight", "typeRi").replace("typeBoth", "typeBo").replace("typeRect", "typeRe");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:307:0x0d06  */
    /* JADX WARN: Removed duplicated region for block: B:308:0x0d21  */
    /* JADX WARN: Removed duplicated region for block: B:311:0x0d2a  */
    /* JADX WARN: Removed duplicated region for block: B:313:0x0d48  */
    /* JADX WARN: Removed duplicated region for block: B:315:0x0d51  */
    /* JADX WARN: Removed duplicated region for block: B:316:0x0d69  */
    /* JADX WARN: Removed duplicated region for block: B:319:0x0d75  */
    /* JADX WARN: Removed duplicated region for block: B:320:0x0d8b  */
    /* JADX WARN: Removed duplicated region for block: B:323:0x0d9b  */
    /* JADX WARN: Removed duplicated region for block: B:324:0x0d9e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void C(java.util.ArrayList r33, java.util.ArrayList r34, java.util.ArrayList r35, java.util.ArrayList r36, java.util.ArrayList r37, int r38, int r39, java.util.ArrayList r40, java.util.ArrayList r41, java.util.ArrayList r42) {
        /*
            Method dump skipped, instructions count: 3646
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte.C(java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int, int, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList):void");
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void D(a aVar) {
        F1.a.b(this, getString(2131820579), new C0802d(new C0802d.a()), aVar);
    }

    public final void E() {
        this.f3019Y.clear();
        ArrayList<String> arrayList = this.f3025e0.f2883j;
        int i4 = 0;
        if (arrayList.size() > 1 && arrayList.size() % 3 == 0) {
            while (i4 < arrayList.size()) {
                if (i4 % 3 == 0) {
                    this.f3019Y.add(new C0354b(arrayList.get(i4), arrayList.get(i4 + 1), arrayList.get(i4 + 2), this.f3025e0));
                }
                this.f3019Y.notifyDataSetChanged();
                i4++;
            }
        } else if (arrayList.size() > 1 && arrayList.size() % 2 == 0) {
            while (i4 < arrayList.size()) {
                if (i4 % 2 == 0) {
                    this.f3019Y.add(new C0354b(arrayList.get(i4), arrayList.get(i4 + 1), "typeRect", this.f3025e0));
                }
                this.f3019Y.notifyDataSetChanged();
                i4++;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final synchronized void F() {
        try {
            if (this.f3025e0 != null) {
                if (this.f3026f0 == null) {
                    this.f3026f0 = new C0407a(getApplicationContext());
                }
                synchronized (this) {
                    this.f3026f0.b(this.f3025e0);
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void G() {
        SharedPreferences sharedPreferences = getSharedPreferences("cutsettings", 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("opencountmain", (sharedPreferences.getInt("opencountmain", 0) % 5) + 1);
        edit.apply();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onBackPressed() {
        long currentTimeMillis = System.currentTimeMillis();
        if (!this.f3025e0.f2897x) {
            if (currentTimeMillis % 2 != 0) {
                F1.a aVar = this.f3021a0;
                if (aVar != null) {
                    aVar.c(new d());
                }
                F1.a aVar2 = this.f3021a0;
                if (aVar2 != null) {
                    aVar2.e(this);
                } else {
                    overridePendingTransition(2130771998, 2130771999);
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                D(this.f3022b0);
            } else {
                overridePendingTransition(2130771998, 2130771999);
            }
        } else {
            overridePendingTransition(2130771998, 2130771999);
        }
        super/*androidx.activity.ComponentActivity*/.onBackPressed();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v2, types: [c.a, java.lang.Object] */
    public final void onCreate(Bundle bundle) {
        C0407a c0407a = new C0407a(getApplicationContext());
        this.f3026f0 = c0407a;
        this.f3025e0 = c0407a.a();
        super.onCreate(bundle);
        setContentView(2131427359);
        this.f3023c0 = v(new androidx.activity.result.b() { // from class: S0.F
            /* JADX WARN: Type inference failed for: r1v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte, java.lang.Object] */
            public final void c(Object obj) {
                androidx.activity.result.a aVar = (androidx.activity.result.a) obj;
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                ?? r12 = ActivityListaCorte.this;
                r12.getClass();
                if (aVar.j == -1) {
                    try {
                        r12.f3019Y.clear();
                        Intent intent = aVar.k;
                        if (intent != null) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(r12.getContentResolver().openInputStream(intent.getData())));
                            while (true) {
                                String readLine = bufferedReader.readLine();
                                if (readLine != null) {
                                    int length = readLine.split(":").length;
                                    if (length != 2) {
                                        if (length == 3) {
                                            r12.f3019Y.add(new C0354b(readLine.split(":")[0].trim() + "[" + readLine.split(":")[2].trim() + "]", readLine.split(":")[1].trim() + " " + r12.getString(2131820739), "typeRect", r12.f3025e0));
                                        }
                                    } else {
                                        r12.f3019Y.add(new C0354b(readLine.split(":")[0].trim(), readLine.split(":")[1].trim() + " " + r12.getString(2131820739), "typeRect", r12.f3025e0));
                                    }
                                    StringBuilder sb = new StringBuilder();
                                    ArrayList<C0354b> arrayList = r12.f3017W;
                                    if (!arrayList.isEmpty()) {
                                        for (int i4 = 0; i4 < arrayList.size(); i4++) {
                                            sb.append(arrayList.get(i4).f2899b);
                                            sb.append("@");
                                            sb.append(arrayList.get(i4).f2900c);
                                            sb.append("@");
                                            sb.append(arrayList.get(i4).f);
                                            sb.append("@");
                                        }
                                        ArrayList<String> arrayList2 = new ArrayList<>(Arrays.asList(new StringBuilder(sb.substring(0, sb.lastIndexOf("@"))).toString().split("@")));
                                        arrayList2.trimToSize();
                                        r12.f3025e0.f2883j = arrayList2;
                                        r12.F();
                                    }
                                } else {
                                    bufferedReader.close();
                                    return;
                                }
                            }
                        }
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
            }
        }, (AbstractC0363a) new Object());
        C0804f c0804f = new C0804f(this);
        this.f3020Z = c0804f;
        c0804f.setAdUnitId(getString(2131820578));
        ((FrameLayout) findViewById(2131230793)).addView(this.f3020Z);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        this.f3020Z.setAdSize(C0803e.a(this, (int) (displayMetrics.widthPixels / displayMetrics.density)));
        this.f3003I = findViewById(2131230926);
        this.f3004J = findViewById(2131230933);
        this.f3005K = findViewById(2131230929);
        this.f3006L = (ListView) findViewById(2131231034);
        this.f3002H = (Button) findViewById(2131230824);
        this.f3007M = (Button) findViewById(2131230826);
        this.f3008N = (Button) findViewById(2131230831);
        ImageButton imageButton = (ImageButton) findViewById(2131230986);
        this.f3009O = imageButton;
        imageButton.setImageResource(2131165312);
        this.f3022b0 = new a();
        this.f3024d0 = false;
        ProgressDialog progressDialog = new ProgressDialog(this);
        l0 = progressDialog;
        progressDialog.setCancelable(false);
        C0353a c0353a = this.f3025e0;
        this.f3015U = c0353a.f2889p;
        this.f3016V = c0353a.f2881h;
        c0353a.f2875a = this.f3013S;
        final View findViewById = findViewById(2131230789);
        findViewById.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: S0.K
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public final void onGlobalLayout() {
                ProgressDialog progressDialog2 = ActivityListaCorte.l0;
                ActivityListaCorte activityListaCorte = ActivityListaCorte.this;
                activityListaCorte.getClass();
                View view = findViewById;
                int i4 = 8;
                if (view.getRootView().getHeight() - view.getHeight() > TypedValue.applyDimension(1, 300.0f, activityListaCorte.getResources().getDisplayMetrics())) {
                    activityListaCorte.f3020Z.setVisibility(8);
                    return;
                }
                C0804f c0804f2 = activityListaCorte.f3020Z;
                if (!activityListaCorte.f3025e0.f2897x) {
                    i4 = 0;
                }
                c0804f2.setVisibility(i4);
            }
        });
        AbstractC0392a y4 = y();
        if (y4 != null) {
            y4.c(getString(2131820575));
            y4.b(getString(2131820928));
            y4.a(true);
        }
        HashMap hashMap = this.f3010P;
        C0284u0.d(2131165454, hashMap, "typeRect", 2131165374, "typeLeft");
        C0284u0.d(2131165457, hashMap, "typeRight", 2131165312, "typeBoth");
        int i4 = this.f3012R;
        String[] strArr = this.f3011Q;
        Integer num = (Integer) hashMap.get(strArr[i4]);
        if (num != null) {
            this.f3009O.setImageResource(num.intValue());
            this.f3009O.setTag(strArr[this.f3012R]);
        }
        this.f3009O.setOnClickListener(new View$OnClickListenerC0270n(this, 1));
        this.f3005K.setKeyListener(DigitsKeyListener.getInstance("0123456789/QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm<>,;.:_-*"));
        this.f3005K.setRawInputType(4097);
        this.f3005K.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: S0.G
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i5, KeyEvent keyEvent) {
                ActivityListaCorte activityListaCorte = ActivityListaCorte.this;
                if (i5 == 6) {
                    Editable text = activityListaCorte.f3003I.getText();
                    Objects.requireNonNull(text);
                    if (!text.toString().isEmpty()) {
                        Editable text2 = activityListaCorte.f3004J.getText();
                        Objects.requireNonNull(text2);
                        if (!text2.toString().isEmpty()) {
                            activityListaCorte.f3002H.performClick();
                            return false;
                        }
                    }
                    activityListaCorte.f3003I.requestFocus();
                    return false;
                }
                ProgressDialog progressDialog2 = ActivityListaCorte.l0;
                activityListaCorte.getClass();
                return false;
            }
        });
        W w4 = this.f3029i0;
        try {
            if (this.f3025e0.f2888o.equals(getString(2131820882)) && !this.f3025e0.f2892s.split("@")[0].equals("2")) {
                this.f3003I.setFilters(new InputFilter[]{w4});
                this.f3003I.setKeyListener(DigitsKeyListener.getInstance("0123456789.a /"));
                this.f3003I.setInputType(524289);
            } else {
                this.f3003I.setInputType(524433);
                this.f3003I.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
            }
        } catch (Exception unused) {
            this.f3003I.setInputType(524433);
            this.f3003I.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
            onBackPressed();
        }
        try {
            if (this.f3025e0.f2892s.split("@")[0].equals("2")) {
                this.f3003I.setInputType(524433);
                this.f3003I.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
            }
        } catch (Exception unused2) {
            this.f3003I.setInputType(524433);
            this.f3003I.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
            onBackPressed();
        }
        this.f3004J.setInputType(524433);
        this.f3004J.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        T0.b bVar = new T0.b(this, this.f3017W);
        this.f3019Y = bVar;
        this.f3006L.setAdapter((ListAdapter) bVar);
        this.f3006L.setLongClickable(true);
        this.f3006L.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: S0.t0
            /* JADX WARN: Type inference failed for: r1v0, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte, java.lang.Object] */
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public final boolean onItemLongClick(AdapterView adapterView, View view, int i5, long j4) {
                ProgressDialog progressDialog2 = ActivityListaCorte.l0;
                ?? r12 = ActivityListaCorte.this;
                r12.getClass();
                l.Q q4 = new l.Q(r12, view);
                C0652f c0652f = new C0652f(r12);
                androidx.appcompat.view.menu.f fVar = q4.f5053a;
                c0652f.inflate(2131558403, fVar);
                q4.f5056d = new L(r12, fVar.getItem(0).getItemId(), i5, fVar.getItem(1).getItemId(), view);
                androidx.appcompat.view.menu.i iVar = q4.f5055c;
                if (!iVar.b()) {
                    if (iVar.f != null) {
                        iVar.d(0, 0, false, false);
                    } else {
                        throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
                    }
                }
                return true;
            }
        });
        E();
        ArrayList<Double> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        ArrayList<String> arrayList3 = this.f3025e0.f2884k;
        try {
            if (arrayList3.size() > 1) {
                for (int i5 = 0; i5 < arrayList3.size(); i5++) {
                    if (i5 % 2 == 0) {
                        arrayList2.add(new C0354b(arrayList3.get(i5), arrayList3.get(i5 + 1), this.f3025e0));
                    }
                }
                for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                    for (int i7 = 0; i7 < Integer.parseInt(((C0354b) arrayList2.get(i6)).f2900c.split(" ")[0]); i7++) {
                        if (this.f3025e0.f2890q) {
                            arrayList.add(Double.valueOf(C0373f.h(((C0354b) arrayList2.get(i6)).f2899b.trim())));
                        } else {
                            arrayList.add(Double.valueOf(Double.parseDouble(((C0354b) arrayList2.get(i6)).f2899b.trim())));
                        }
                    }
                }
            }
            this.f3025e0.f2885l = arrayList;
        } catch (Exception unused3) {
        }
        this.f3002H.setOnClickListener(new H(this, 0));
        this.f3007M.setOnClickListener(new View.OnClickListener() { // from class: S0.s0
            /* JADX WARN: Type inference failed for: r1v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte, android.view.KeyEvent$Callback, e.f] */
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                int i8 = 0;
                Object obj = this;
                switch (r1) {
                    case 0:
                        ProgressDialog progressDialog2 = ActivityListaCorte.l0;
                        ?? r12 = (ActivityListaCorte) obj;
                        C0373f.l(10L, r12.getApplicationContext());
                        ArrayList<C0354b> arrayList4 = r12.f3018X;
                        arrayList4.clear();
                        ArrayList<C0354b> arrayList5 = r12.f3017W;
                        if (!arrayList5.isEmpty()) {
                            arrayList4.addAll(arrayList5);
                            arrayList4.trimToSize();
                        }
                        b.a aVar = new b.a((Context) r12);
                        aVar.a.f = r12.getString(2131820736) + "?";
                        aVar.c(r12.getString(2131820736), new M(r12, 0));
                        aVar.b(r12.getString(2131820699), new DialogInterface$OnClickListenerC0256g(r12, 1));
                        aVar.a();
                        aVar.d();
                        return;
                    default:
                        com.google.android.material.datepicker.p pVar = (com.google.android.material.datepicker.p) obj;
                        pVar.S0.setEnabled(pVar.P().j());
                        pVar.Q0.toggle();
                        if (pVar.F0 != 1) {
                            i8 = 1;
                        }
                        pVar.F0 = i8;
                        pVar.T(pVar.Q0);
                        pVar.S();
                        return;
                }
            }
        });
        this.f3008N.setOnClickListener(new View$OnClickListenerC0278r0(0, this));
        if (getSharedPreferences("cutsettings", 0).getBoolean("firstopena", true)) {
            Toast.makeText((Context) this, (CharSequence) getString(2131820721), 1).show();
            SharedPreferences.Editor edit = getSharedPreferences("cutsettings", 0).edit();
            edit.putBoolean("firstopena", false);
            edit.apply();
        }
    }

    public final boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(2131558400, menu);
        boolean z4 = false;
        menu.getItem(4).setVisible(false);
        menu.getItem(5).setVisible(false);
        menu.getItem(6).setVisible(false);
        menu.getItem(7).setVisible(false);
        MenuItem item = menu.getItem(8);
        c0 c0Var = V0.c.f;
        if (c0Var != null && c0Var.a() != 1) {
            z4 = true;
        }
        item.setVisible(z4);
        menu.getItem(9).setVisible(true);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v12, types: [android.content.DialogInterface$OnClickListener, java.lang.Object] */
    public final boolean onOptionsItemSelected(MenuItem menuItem) {
        Intent intent = new Intent((Context) this, (Class<?>) ActivityListasGuardadas.class);
        Intent intent2 = new Intent((Context) this, (Class<?>) ActivityRetales.class);
        final E e4 = new E((Context) this);
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            onBackPressed();
        } else if (itemId == 2131231075) {
            long currentTimeMillis = System.currentTimeMillis();
            if (!this.f3025e0.f2897x) {
                if (currentTimeMillis % 2 != 0) {
                    F1.a aVar = this.f3021a0;
                    if (aVar != null) {
                        aVar.c(new b(intent2));
                    }
                    F1.a aVar2 = this.f3021a0;
                    if (aVar2 != null) {
                        aVar2.e(this);
                    } else {
                        F();
                        startActivity(intent2);
                        overridePendingTransition(2130771998, 2130771999);
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                    D(this.f3022b0);
                } else {
                    F();
                    startActivity(intent2);
                    overridePendingTransition(2130771998, 2130771999);
                }
            } else {
                F();
                startActivity(intent2);
                overridePendingTransition(2130771998, 2130771999);
            }
        } else if (itemId == 2131231069) {
            b.a aVar3 = new b.a(this);
            String string = getString(2131820707);
            AlertController.b bVar = aVar3.a;
            bVar.d = string;
            bVar.f = getString(2131820702) + "\n" + getString(2131820703) + "\n" + getString(2131820704) + "\n" + getString(2131820705) + "\n\n" + getString(2131820706) + "\n\n1200:5\n650:3\n1520:24:P2\n2100:14:V7";
            aVar3.c(getString(2131820779), new DialogInterface$OnClickListenerC0257g0(this, 0));
            aVar3.b(getString(2131820699), (DialogInterface.OnClickListener) new Object());
            aVar3.d();
        } else if (itemId == 2131231066) {
            long currentTimeMillis2 = System.currentTimeMillis();
            if (!this.f3025e0.f2897x) {
                if (currentTimeMillis2 % 2 != 0) {
                    F1.a aVar4 = this.f3021a0;
                    if (aVar4 != null) {
                        aVar4.c(new c(intent));
                    }
                    F1.a aVar5 = this.f3021a0;
                    if (aVar5 != null) {
                        aVar5.e(this);
                    } else {
                        F();
                        startActivity(intent);
                        overridePendingTransition(2130771998, 2130771999);
                        finish();
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                    D(this.f3022b0);
                } else {
                    F();
                    startActivity(intent);
                    overridePendingTransition(2130771998, 2130771999);
                    finish();
                }
            } else {
                F();
                startActivity(intent);
                overridePendingTransition(2130771998, 2130771999);
                finish();
            }
        } else if (itemId == 2131231068) {
            ArrayList<C0354b> arrayList = this.f3017W;
            arrayList.trimToSize();
            if (!arrayList.isEmpty()) {
                final View inflate = LayoutInflater.from(this).inflate(2131427387, (ViewGroup) null);
                b.a aVar6 = new b.a(this);
                String string2 = getString(2131820782);
                AlertController.b bVar2 = aVar6.a;
                bVar2.d = string2;
                bVar2.q = inflate;
                aVar6.b(getString(2131820699), new P(1));
                aVar6.c(getString(2131820781), new DialogInterface.OnClickListener() { // from class: S0.q0
                    /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte, java.lang.Object, e.f] */
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i4) {
                        boolean z4;
                        String str;
                        ProgressDialog progressDialog = ActivityListaCorte.l0;
                        final ?? r02 = ActivityListaCorte.this;
                        r02.getClass();
                        View view = inflate;
                        final CheckBox checkBox = (CheckBox) view.findViewById(2131230861);
                        final CheckBox checkBox2 = (CheckBox) view.findViewById(2131230860);
                        final String obj = ((EditText) view.findViewById(2131230941)).getText().toString();
                        final D1.E e5 = e4;
                        Cursor e6 = e5.e();
                        if (!obj.isEmpty()) {
                            if (e6.moveToFirst()) {
                                while (!e6.isAfterLast()) {
                                    if (e6.getString(e6.getColumnIndexOrThrow("NOMBRE")).equals(obj)) {
                                        z4 = true;
                                        break;
                                    }
                                    e6.moveToNext();
                                }
                            }
                            z4 = false;
                            e6.close();
                            if (!z4) {
                                StringBuilder sb = new StringBuilder();
                                ArrayList<C0354b> arrayList2 = r02.f3017W;
                                if (!arrayList2.isEmpty()) {
                                    for (int i5 = 0; i5 < arrayList2.size(); i5++) {
                                        sb.append(arrayList2.get(i5).f2899b);
                                        sb.append("@");
                                        sb.append(arrayList2.get(i5).f2900c);
                                        sb.append("@");
                                        sb.append(arrayList2.get(i5).f);
                                        sb.append("@");
                                    }
                                    String str2 = "";
                                    String str3 = (!checkBox.isChecked() || (str3 = r02.f3025e0.f2893t) == null) ? "" : "";
                                    if (checkBox2.isChecked() && (str = r02.f3025e0.f2892s) != null) {
                                        str2 = str;
                                    }
                                    ((SQLiteDatabase) e5.f633j).insert("PROYECTOS", null, D1.E.h(obj, new StringBuilder(sb.substring(0, sb.lastIndexOf("@"))).toString(), DateFormat.getDateTimeInstance(2, 3).format(new Date()), str2, str3));
                                    Toast.makeText((Context) r02, r02.getString(2131820709) + obj + r02.getString(2131820710), 0).show();
                                    return;
                                }
                                return;
                            }
                            b.a aVar7 = new b.a((Context) r02);
                            aVar7.a.f = r02.getString(2131820711) + obj + "\". " + r02.getString(2131820725);
                            aVar7.c("OK", new DialogInterface.OnClickListener() { // from class: S0.I
                                /* JADX WARN: Type inference failed for: r9v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte] */
                                @Override // android.content.DialogInterface.OnClickListener
                                public final void onClick(DialogInterface dialogInterface2, int i6) {
                                    String str4;
                                    ProgressDialog progressDialog2 = ActivityListaCorte.l0;
                                    StringBuilder sb2 = new StringBuilder();
                                    ?? r9 = ActivityListaCorte.this;
                                    ArrayList<C0354b> arrayList3 = r9.f3017W;
                                    if (!arrayList3.isEmpty()) {
                                        for (int i7 = 0; i7 < arrayList3.size(); i7++) {
                                            sb2.append(arrayList3.get(i7).f2899b);
                                            sb2.append("@");
                                            sb2.append(arrayList3.get(i7).f2900c);
                                            sb2.append("@");
                                            sb2.append(arrayList3.get(i7).f);
                                            sb2.append("@");
                                        }
                                        String str5 = "";
                                        String str6 = (!checkBox.isChecked() || (str6 = r9.f3025e0.f2893t) == null) ? "" : "";
                                        if (checkBox2.isChecked() && (str4 = r9.f3025e0.f2892s) != null) {
                                            str5 = str4;
                                        }
                                        String sb3 = new StringBuilder(sb2.substring(0, sb2.lastIndexOf("@"))).toString();
                                        D1.E e7 = e5;
                                        e7.getClass();
                                        String format = DateFormat.getDateTimeInstance(2, 3).format(new Date());
                                        String str7 = obj;
                                        ((SQLiteDatabase) e7.f633j).update("PROYECTOS", D1.E.h(str7, sb3, format, str5, str6), "NOMBRE=?", new String[]{str7});
                                        Toast.makeText((Context) r9, r9.getString(2131820709) + str7 + r9.getString(2131820710), 0).show();
                                    }
                                }
                            });
                            aVar7.b(r02.getString(2131820699), new J(r02, 0));
                            aVar7.a().show();
                        }
                    }
                });
                aVar6.a().show();
            }
        } else if (itemId == 2131231077) {
            menuItem.setChecked(!menuItem.isChecked());
            this.f3025e0.f2891r = menuItem.isChecked();
            if (this.f3025e0.f2891r) {
                Toast.makeText((Context) this, (CharSequence) getString(2131820790), 1).show();
            } else {
                Toast.makeText((Context) this, (CharSequence) getString(2131820791), 1).show();
            }
        } else if (itemId == 2131231071) {
            new V0.c(this, this.f3020Z, getString(2131820579), this.f3022b0).f();
        } else if (itemId == 2131231072) {
            F();
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://sites.google.com/view/soldier-developer/cutter-cutting-optimizer/privacy-policy")));
        } else if (itemId == 2131231070) {
            F();
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/account/subscriptions?sku=remove_ads&package=com.embarcadero.OptimizaCorte")));
        } else if (itemId == 2131231073) {
            C0379b.b(this, this.f3025e0);
        }
        return super/*android.app.Activity*/.onOptionsItemSelected(menuItem);
    }

    public final void onPause() {
        F();
        this.f3024d0 = true;
        Timer timer = this.f3028h0;
        if (timer != null) {
            timer.cancel();
            Log.d("INTERSTITIAL_TIMER", "onPause: interstitial timer paused");
        }
        super.onPause();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onResume() {
        if (this.f3025e0 == null) {
            this.f3025e0 = new C0353a();
        }
        C0407a c0407a = new C0407a(getApplicationContext());
        this.f3026f0 = c0407a;
        this.f3025e0 = c0407a.a();
        this.f3024d0 = false;
        E();
        ArrayList<Double> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        ArrayList<String> arrayList3 = this.f3025e0.f2884k;
        try {
            if (arrayList3.size() > 1) {
                for (int i4 = 0; i4 < arrayList3.size(); i4++) {
                    if (i4 % 2 == 0) {
                        arrayList2.add(new C0354b(arrayList3.get(i4), arrayList3.get(i4 + 1), this.f3025e0));
                    }
                }
                for (int i5 = 0; i5 < arrayList2.size(); i5++) {
                    for (int i6 = 0; i6 < Integer.parseInt(((C0354b) arrayList2.get(i5)).f2900c.split(" ")[0]); i6++) {
                        if (this.f3025e0.f2890q) {
                            arrayList.add(Double.valueOf(C0373f.h(((C0354b) arrayList2.get(i5)).f2899b.trim())));
                        } else {
                            arrayList.add(Double.valueOf(Double.parseDouble(((C0354b) arrayList2.get(i5)).f2899b.trim())));
                        }
                    }
                }
            }
        } catch (NumberFormatException unused) {
            Log.d("listaCorteOnResume", "NumberFormatException");
        }
        this.f3025e0.f2885l = arrayList;
        G();
        f fVar = this.f3030j0;
        Y0.a b4 = Y0.a.b(this, fVar);
        this.f3027g0 = b4;
        b4.f2825a = fVar;
        b4.i();
        if (this.f3027g0.d()) {
            this.f3027g0.a();
        }
        Timer timer = this.f3028h0;
        if (timer != null) {
            timer.cancel();
            Log.d("INTERSTITIAL_TIMER", "onResume: interstitial timer paused");
        }
        this.f3028h0 = new Timer();
        this.f3028h0.schedule(new e(), 0L, 1200000L);
        super.onResume();
    }
}
