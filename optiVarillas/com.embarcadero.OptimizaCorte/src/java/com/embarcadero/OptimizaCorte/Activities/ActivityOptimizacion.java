package com.embarcadero.OptimizaCorte.Activities;

import B.RunnableC0145a;
import S0.DialogInterface$OnClickListenerC0248c;
import S0.J0;
import S0.L0;
import S0.M0;
import S0.N0;
import S0.P0;
import S0.RunnableC0283u;
import S0.RunnableC0285v;
import S0.RunnableC0286v0;
import S0.RunnableC0287w;
import S0.RunnableC0293z;
import S0.View$OnClickListenerC0272o;
import S0.View$OnClickListenerC0278r0;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b1.C0353a;
import b1.C0354b;
import c.AbstractC0363a;
import c1.C0373f;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import d1.C0379b;
import e.AbstractC0392a;
import e.C0397f;
import e1.C0407a;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import t1.C0802d;
import t1.C0803e;
import t1.C0804f;
import t1.C0807i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class ActivityOptimizacion extends C0397f {

    /* renamed from: g0 */
    public static final /* synthetic */ int f3061g0 = 0;

    /* renamed from: H */
    public F1.a f3062H;

    /* renamed from: I */
    public c f3063I;

    /* renamed from: J */
    public C0804f f3064J;

    /* renamed from: K */
    public FrameLayout f3065K;

    /* renamed from: L */
    public Button f3066L;

    /* renamed from: M */
    public TextView f3067M;

    /* renamed from: N */
    public TextView f3068N;

    /* renamed from: O */
    public CheckBox f3069O;

    /* renamed from: P */
    public RecyclerView f3070P;

    /* renamed from: Q */
    public T0.f f3071Q;

    /* renamed from: R */
    public T0.f f3072R;

    /* renamed from: S */
    public boolean f3073S;

    /* renamed from: V */
    public C0353a f3076V;

    /* renamed from: W */
    public C0407a f3077W;

    /* renamed from: X */
    public androidx.activity.result.c f3078X;

    /* renamed from: Y */
    public View f3079Y;

    /* renamed from: c0 */
    public Y0.a f3083c0;

    /* renamed from: d0 */
    public Timer f3084d0;

    /* renamed from: T */
    public ArrayList<b1.e> f3074T = null;

    /* renamed from: U */
    public ArrayList<b1.e> f3075U = null;

    /* renamed from: Z */
    public String f3080Z = "";

    /* renamed from: a0 */
    public String f3081a0 = "";

    /* renamed from: b0 */
    public boolean f3082b0 = false;

    /* renamed from: e0 */
    public final a f3085e0 = new a();

    /* renamed from: f0 */
    public final b f3086f0 = new b();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class a implements Z0.a {
        public a() {
            ActivityOptimizacion.this = r1;
        }

        @Override // Z0.a
        public final void a() {
            ActivityOptimizacion.this.runOnUiThread(new RunnableC0286v0(2, this));
        }

        @Override // Z0.a
        public final void b() {
            ActivityOptimizacion.this.runOnUiThread(new RunnableC0285v(this, 1));
        }

        @Override // Z0.a
        public final void c(ArrayList arrayList) {
            ActivityOptimizacion.this.runOnUiThread(new RunnableC0287w(2, this));
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class b implements Z0.a {
        public b() {
            ActivityOptimizacion.this = r1;
        }

        @Override // Z0.a
        public final void a() {
            ActivityOptimizacion.this.runOnUiThread(new Q2.g(3, this));
        }

        @Override // Z0.a
        public final void b() {
            ActivityOptimizacion.this.runOnUiThread(new RunnableC0145a(3, this));
        }

        @Override // Z0.a
        public final void c(ArrayList arrayList) {
            ActivityOptimizacion.this.runOnUiThread(new RunnableC0293z(2, this));
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class c extends F1.b {
        public c() {
            ActivityOptimizacion.this = r1;
        }

        @Override // G3.g
        public final void v(C0807i c0807i) {
            ActivityOptimizacion.this.f3062H = null;
        }

        @Override // G3.g
        public final void x(Object obj) {
            ActivityOptimizacion.this.f3062H = (F1.a) obj;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class d extends WebViewClient {
        public d() {
            ActivityOptimizacion.this = r1;
        }

        /* JADX WARN: Type inference failed for: r7v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion, android.app.Activity] */
        @Override // android.webkit.WebViewClient
        public final void onPageFinished(final WebView webView, String str) {
            ?? r7 = ActivityOptimizacion.this;
            if (!r7.isFinishing() || !r7.isDestroyed()) {
                try {
                    ActivityOptimizacion.A(r7, webView);
                } catch (Exception e4) {
                    b.a aVar = new b.a(webView.getContext());
                    AlertController.b bVar = aVar.a;
                    bVar.d = bVar.a.getText(2131820899);
                    bVar.f = X1.b.e(r7.getString(2131820896), "\n\n", r7.getString(2131820897));
                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: S0.R0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i4) {
                            ActivityOptimizacion.d dVar = ActivityOptimizacion.d.this;
                            dVar.getClass();
                            Context context = webView.getContext();
                            C0353a c0353a = ActivityOptimizacion.this.f3076V;
                            C0379b.a(context, c0353a, "Print error: " + e4.getMessage());
                        }
                    };
                    bVar.g = bVar.a.getText(2131820898);
                    bVar.h = onClickListener;
                    bVar.m = false;
                    aVar.b("No", new DialogInterface$OnClickListenerC0248c(1));
                    aVar.a().show();
                }
            }
        }

        @Override // android.webkit.WebViewClient
        public final boolean shouldOverrideUrlLoading(WebView webView, String str) {
            return false;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class e extends G3.g {
        public e() {
            ActivityOptimizacion.this = r1;
        }

        @Override // G3.g
        public final void u() {
            ActivityOptimizacion activityOptimizacion = ActivityOptimizacion.this;
            activityOptimizacion.F(activityOptimizacion.f3076V.f2887n);
            Log.d("TAG", "The ad was dismissed.");
        }

        @Override // G3.g
        public final void w() {
            ActivityOptimizacion activityOptimizacion = ActivityOptimizacion.this;
            activityOptimizacion.F(activityOptimizacion.f3076V.f2887n);
            Log.d("TAG", "The ad failed to show.");
        }

        @Override // G3.g
        public final void y() {
            ActivityOptimizacion.this.f3062H = null;
            Log.d("TAG", "The ad was shown.");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class f extends G3.g {
        public f() {
            ActivityOptimizacion.this = r1;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion] */
        @Override // G3.g
        public final void u() {
            int i4 = ActivityOptimizacion.f3061g0;
            ?? r02 = ActivityOptimizacion.this;
            r02.I();
            r02.startActivity(Intent.createChooser(r02.K(r02.E() + "\n" + r02.f3076V.f2886m), r02.getString(2131820871)));
            Log.d("TAG", "The ad was dismissed.");
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion] */
        @Override // G3.g
        public final void w() {
            int i4 = ActivityOptimizacion.f3061g0;
            ?? r02 = ActivityOptimizacion.this;
            r02.I();
            r02.startActivity(Intent.createChooser(r02.K(r02.E() + "\n" + r02.f3076V.f2886m), r02.getString(2131820871)));
            Log.d("TAG", "The ad failed to show.");
        }

        @Override // G3.g
        public final void y() {
            ActivityOptimizacion.this.f3062H = null;
            Log.d("TAG", "The ad was shown.");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class g extends G3.g {
        public g() {
            ActivityOptimizacion.this = r1;
        }

        @Override // G3.g
        public final void u() {
            ActivityOptimizacion.this.overridePendingTransition(2130771998, 2130771999);
            Log.d("TAG", "The ad was dismissed.");
        }

        @Override // G3.g
        public final void w() {
            ActivityOptimizacion.this.overridePendingTransition(2130771998, 2130771999);
            Log.d("TAG", "The ad failed to show.");
        }

        @Override // G3.g
        public final void y() {
            ActivityOptimizacion.this.f3062H = null;
            Log.d("TAG", "The ad was shown.");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class h extends TimerTask {
        public h() {
            ActivityOptimizacion.this = r1;
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public final void run() {
            ActivityOptimizacion.this.runOnUiThread(new RunnableC0283u(1, this));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void A(ActivityOptimizacion activityOptimizacion, WebView webView) {
        PrintManager printManager = (PrintManager) activityOptimizacion.getSystemService("print");
        PrintDocumentAdapter createPrintDocumentAdapter = webView.createPrintDocumentAdapter("CutterOPT");
        PrintAttributes.Builder minMargins = new PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).setMinMargins(new PrintAttributes.Margins(20, 20, 20, 20));
        if (!activityOptimizacion.isFinishing() || !activityOptimizacion.isDestroyed()) {
            printManager.print("CutterOPT", createPrintDocumentAdapter, minMargins.build());
        }
    }

    public static ArrayList H(ArrayList arrayList) {
        ArrayList arrayList2 = new ArrayList(arrayList);
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            ((b1.e) it.next()).getClass();
        }
        arrayList2.trimToSize();
        return arrayList2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void C(String str) {
        WebView webView = new WebView(this);
        webView.setWebViewClient(new d());
        webView.getSettings().setAllowFileAccess(true);
        webView.loadDataWithBaseURL(null, "<html><head><style>  @page {  margin: 2cm;}.card {  background-color: #fff;  -moz-border-radius: 4px;  -webkit-border-radius: 4px;  border-radius: 4px;  -moz-box-shadow: 0 3px 1px -2px rgba(0,0,0,.2), 0 2px 2px 0 rgba(0,0,0,.14), 0 1px 5px 0 rgba(0,0,0,.12);  -webkit-box-shadow: 0 3px 1px -2px rgba(0,0,0,.2), 0 2px 2px 0 rgba(0,0,0,.14), 0 1px 5px 0 rgba(0,0,0,.12);  box-shadow: 0 3px 1px -2px rgba(0,0,0,.2), 0 2px 2px 0 rgba(0,0,0,.14), 0 1px 5px 0 rgba(0,0,0,.12);  color: rgba(0,0,0,.87);  margin: 0px;  margin-bottom: 16px;  padding: 20px;  width: 90%;  overflow: hidden;  position: relative;  page-break-inside: avoid; /* Evitar que el contenido se corte en páginas */}.card::after {  clear: both;}.card::after, .card::before {  content: \"\";  display: block;}p {  line-height: 1.5;   /* within paragraph */  margin-bottom: 0px; /* between paragraphs */}progress {  height: 7px;  border-radius: 7px;   width: 100%;  box-shadow: 1px 1px 4px rgba( 0, 0, 0, 0.2 );}progress::-webkit-progress-bar {  height: 7px;  background-color: #eeeeee;  border-radius: 7px;}progress::-webkit-progress-value {  height: 7px;  background-color: #26a69a;  border-radius: 7px;}</style></head> <body>" + str + "</body></html>", "text/html", "UTF-8", null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String D() {
        String str;
        b1.d dVar = this.f3076V.f2882i;
        if (dVar == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        File file = new File(getApplicationContext().getCacheDir(), "logo.png");
        if (file.exists() && this.f3076V.f2897x) {
            str = Uri.fromFile(file).toString();
            PrintStream printStream = System.out;
            printStream.println("premium, ruta: " + str);
        } else {
            str = "file:///android_res/drawable/ic_launcher.png";
        }
        sb.append("<div style=\"float: left; width: 120px;\"><img src=\"");
        sb.append(str);
        sb.append("\" width=\"100px\" height=\"100px\" /></div>");
        if (this.f3076V.f2897x) {
            sb.append("<p style=\"font-size:12px;padding-top:25px\">");
            sb.append(this.f3080Z);
            sb.append("</p><p style=\"font-size:12px;\">");
            sb.append(this.f3081a0);
            sb.append("</p>\n\n\n");
        } else {
            sb.append("<p style=\"font-size:12px;padding-top:25px\">");
            sb.append(getString(2131820655));
            sb.append("</p><p style=\"font-size:12px;\">");
            sb.append(getString(2131820724));
            sb.append(" https://goo.gl/OBNeJw</p>\n\n\n");
        }
        sb.append("<p style=\"font-size:20px;font-weight: bold;color: #26a69a;\"> ");
        sb.append(getString(2131820657));
        sb.append(" (");
        sb.append(this.f3076V.f2888o);
        sb.append("):</p>\n\n<b>");
        sb.append(getString(2131820658));
        sb.append(": ");
        sb.append(dVar.f2907a);
        sb.append("</b>\n");
        int i4 = 0;
        int i5 = 0;
        while (true) {
            ArrayList<C0354b> arrayList = dVar.f2910d;
            if (i5 >= arrayList.size()) {
                break;
            }
            sb.append(arrayList.get(i5).f2899b);
            sb.append(" × ");
            sb.append(arrayList.get(i5).f2901d);
            sb.append(" ");
            sb.append(getString(2131820739));
            sb.append("\n");
            i5++;
        }
        sb.append("\n<b>");
        sb.append(getString(2131820652));
        sb.append(": ");
        sb.append(dVar.f2908b);
        sb.append("</b>\n");
        int i6 = 0;
        while (true) {
            ArrayList<C0354b> arrayList2 = dVar.f2911e;
            if (i6 >= arrayList2.size()) {
                break;
            }
            sb.append(arrayList2.get(i6).f2899b);
            sb.append(" × ");
            sb.append(arrayList2.get(i6).f2901d);
            sb.append(" ");
            sb.append(getString(2131820739));
            sb.append("\n");
            i6++;
        }
        sb.append("\n<b>");
        sb.append(getString(2131820653));
        sb.append(": ");
        sb.append(dVar.f2909c);
        sb.append("</b>\n");
        while (true) {
            ArrayList<C0354b> arrayList3 = dVar.f;
            if (i4 < arrayList3.size()) {
                sb.append(arrayList3.get(i4).f2899b);
                sb.append(" × ");
                sb.append(arrayList3.get(i4).f2901d);
                sb.append(" ");
                sb.append(getString(2131820739));
                sb.append("\n");
                i4++;
            } else {
                return sb.toString();
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String E() {
        b1.d dVar = this.f3076V.f2882i;
        if (dVar == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getString(2131820655));
        sb.append("\n");
        sb.append(getString(2131820724));
        sb.append(" https://goo.gl/OBNeJw\n\n");
        sb.append(getString(2131820657));
        sb.append(" (");
        sb.append(this.f3076V.f2888o);
        sb.append(")\n\n");
        sb.append(getString(2131820658));
        sb.append(": ");
        sb.append(dVar.f2907a);
        sb.append("\n");
        int i4 = 0;
        for (int i5 = 0; i5 < this.f3076V.f2882i.f2910d.size(); i5++) {
            ArrayList<C0354b> arrayList = dVar.f2910d;
            sb.append(arrayList.get(i5).f2899b);
            sb.append(" × ");
            sb.append(arrayList.get(i5).f2901d);
            sb.append(" ");
            sb.append(getString(2131820739));
            sb.append("\n");
        }
        sb.append("\n");
        sb.append(getString(2131820652));
        sb.append(": ");
        sb.append(dVar.f2908b);
        sb.append("\n");
        for (int i6 = 0; i6 < this.f3076V.f2882i.f2911e.size(); i6++) {
            ArrayList<C0354b> arrayList2 = dVar.f2911e;
            sb.append(arrayList2.get(i6).f2899b);
            sb.append(" × ");
            sb.append(arrayList2.get(i6).f2901d);
            sb.append(" ");
            sb.append(getString(2131820739));
            sb.append("\n");
        }
        sb.append("\n");
        sb.append(getString(2131820653));
        sb.append(": ");
        sb.append(dVar.f2909c);
        sb.append("\n");
        while (true) {
            ArrayList<C0354b> arrayList3 = dVar.f;
            if (i4 < arrayList3.size()) {
                sb.append(arrayList3.get(i4).f2899b);
                sb.append(" × ");
                sb.append(arrayList3.get(i4).f2901d);
                sb.append(" ");
                sb.append(getString(2131820739));
                sb.append("\n");
                i4++;
            } else {
                return sb.toString();
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void F(String str) {
        if (D() != null) {
            try {
                C((D() + "\n" + str).replaceAll("\n", "<br />"));
            } catch (Exception e4) {
                Toast.makeText((Context) this, (CharSequence) getString(2131820718), 1).show();
                e4.printStackTrace();
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void G(F1.b bVar) {
        F1.a.b(this, getString(2131820579), new C0802d(new C0802d.a()), bVar);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final synchronized void I() {
        try {
            if (this.f3076V != null) {
                if (this.f3077W == null) {
                    this.f3077W = new C0407a(getApplicationContext());
                }
                synchronized (this) {
                    this.f3077W.b(this.f3076V);
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void J() {
        SharedPreferences sharedPreferences = getSharedPreferences("cutsettings", 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("opencount", (sharedPreferences.getInt("opencount", 0) % 5) + 1);
        edit.apply();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final Intent K(String str) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", getString(2131820654));
        intent.putExtra("android.intent.extra.TEXT", str);
        return intent;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onBackPressed() {
        I();
        if (!this.f3076V.f2897x) {
            F1.a aVar = this.f3062H;
            if (aVar != null) {
                aVar.c(new g());
            }
            F1.a aVar2 = this.f3062H;
            if (aVar2 != null) {
                aVar2.e(this);
            } else {
                overridePendingTransition(2130771998, 2130771999);
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
            G(this.f3063I);
        } else {
            overridePendingTransition(2130771998, 2130771999);
        }
        super/*androidx.activity.ComponentActivity*/.onBackPressed();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v10, types: [c.a, java.lang.Object] */
    @SuppressLint({"SetTextI18n"})
    public final void onCreate(Bundle bundle) {
        boolean z4;
        C0407a c0407a = new C0407a(getApplicationContext());
        this.f3077W = c0407a;
        this.f3076V = c0407a.a();
        super.onCreate(bundle);
        setContentView(2131427457);
        this.f3080Z = getString(2131820655);
        this.f3081a0 = getString(2131820724) + " https://goo.gl/OBNeJw";
        c cVar = new c();
        this.f3063I = cVar;
        G(cVar);
        C0373f.l(100L, getApplicationContext());
        AbstractC0392a y4 = y();
        if (y4 != null) {
            y4.c(getString(2131820575));
            y4.b(getString(2131820929));
            y4.a(true);
        }
        this.f3073S = false;
        this.f3067M = (TextView) findViewById(2131231291);
        TextView textView = (TextView) findViewById(2131231292);
        this.f3068N = textView;
        textView.setTextColor(-16777216);
        if (this.f3076V.f2880g > 0) {
            this.f3068N.setTextColor(-65536);
        }
        TextView textView2 = this.f3068N;
        textView2.setText(getString(2131820733) + " " + this.f3076V.f + " (" + this.f3076V.f2880g + " " + getString(2131820734));
        TextView textView3 = this.f3067M;
        StringBuilder sb = new StringBuilder();
        sb.append(getString(2131820728));
        sb.append(" ");
        sb.append(this.f3076V.f2879e);
        textView3.setText(sb.toString());
        RecyclerView findViewById = findViewById(2131231170);
        this.f3070P = findViewById;
        findViewById.setHasFixedSize(true);
        this.f3074T = new ArrayList<>(H(this.f3076V.f2875a));
        this.f3075U = new ArrayList<>(H(this.f3076V.f2876b));
        this.f3070P.setLayoutManager(new LinearLayoutManager(1));
        this.f3071Q = new T0.f(this.f3074T, this, this.f3076V, true);
        T0.f fVar = new T0.f(this.f3075U, this, this.f3076V, false);
        this.f3072R = fVar;
        T0.f fVar2 = this.f3071Q;
        fVar2.f2326g = new R.d(this);
        fVar.f2326g = new J0(this);
        this.f3070P.setAdapter(fVar2);
        Button button = (Button) findViewById(2131230828);
        this.f3066L = button;
        if (this.f3076V.f2882i != null) {
            z4 = true;
        } else {
            z4 = false;
        }
        button.setEnabled(z4);
        this.f3066L.setOnClickListener(new View$OnClickListenerC0272o(1, this));
        CheckBox checkBox = (CheckBox) findViewById(2131230852);
        this.f3069O = checkBox;
        checkBox.setChecked(true);
        this.f3069O.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: S0.K0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z5) {
                T0.f fVar3;
                ActivityOptimizacion activityOptimizacion = ActivityOptimizacion.this;
                RecyclerView recyclerView = activityOptimizacion.f3070P;
                if (z5) {
                    fVar3 = activityOptimizacion.f3071Q;
                } else {
                    fVar3 = activityOptimizacion.f3072R;
                }
                recyclerView.setAdapter(fVar3);
                activityOptimizacion.f3070P.scheduleLayoutAnimation();
            }
        });
        this.f3065K = (FrameLayout) findViewById(2131230794);
        C0804f c0804f = new C0804f(this);
        this.f3064J = c0804f;
        c0804f.setAdUnitId(getString(2131820578));
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        this.f3064J.setAdSize(C0803e.a(this, (int) (displayMetrics.widthPixels / displayMetrics.density)));
        this.f3065K.removeAllViews();
        this.f3065K.addView(this.f3064J);
        Bundle bundle2 = new Bundle();
        bundle2.putString("collapsible", "bottom");
        bundle2.putString("collapsible_request_id", UUID.randomUUID().toString());
        this.f3064J.a(new C0802d(new C0802d.a().a(bundle2)));
        I();
        if (getSharedPreferences("cutsettings", 0).getBoolean("firstopenb", true)) {
            Toast.makeText((Context) this, (CharSequence) getString(2131820720), 1).show();
            SharedPreferences.Editor edit = getSharedPreferences("cutsettings", 0).edit();
            edit.putBoolean("firstopenb", false);
            edit.apply();
        }
        this.f3078X = v(new P0(this), (AbstractC0363a) new Object());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean onCreateOptionsMenu(Menu menu) {
        boolean z4;
        getMenuInflater().inflate(2131558400, menu);
        boolean z5 = false;
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
        menu.getItem(5).setVisible(false);
        menu.getItem(6).setVisible(false);
        menu.getItem(8).setVisible(false);
        menu.getItem(9).setVisible(false);
        menu.getItem(10).setVisible(true);
        MenuItem item = menu.getItem(7);
        if (D() != null && getPackageManager().hasSystemFeature("android.software.webview")) {
            z4 = true;
        } else {
            z4 = false;
        }
        item.setVisible(z4);
        MenuItem item2 = menu.getItem(4);
        if (E() != null) {
            z5 = true;
        }
        item2.setVisible(z5);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            onBackPressed();
        } else if (itemId == 2131231067) {
            if (D() != null) {
                if (!this.f3076V.f2897x) {
                    F1.a aVar = this.f3062H;
                    if (aVar != null) {
                        aVar.c(new e());
                    }
                    F1.a aVar2 = this.f3062H;
                    if (aVar2 != null) {
                        aVar2.e(this);
                    } else {
                        F(this.f3076V.f2887n);
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                    G(this.f3063I);
                } else {
                    this.f3079Y = LayoutInflater.from(this).inflate(2131427384, (ViewGroup) null);
                    b.a aVar3 = new b.a(this);
                    aVar3.a.q = this.f3079Y;
                    final androidx.appcompat.app.b a4 = aVar3.a();
                    final ImageView imageView = (ImageView) this.f3079Y.findViewById(2131231004);
                    Button button = (Button) this.f3079Y.findViewById(2131230830);
                    Button button2 = (Button) this.f3079Y.findViewById(2131230833);
                    Button button3 = (Button) this.f3079Y.findViewById(2131230832);
                    final EditText editText = (EditText) this.f3079Y.findViewById(2131230931);
                    final EditText editText2 = (EditText) this.f3079Y.findViewById(2131230932);
                    SharedPreferences sharedPreferences = getSharedPreferences("cutsettings", 0);
                    this.f3080Z = sharedPreferences.getString("textline1", getString(2131820655));
                    this.f3081a0 = sharedPreferences.getString("textline2", getString(2131820724) + " https://goo.gl/OBNeJw");
                    editText.setText(this.f3080Z);
                    editText2.setText(this.f3081a0);
                    if (new File(getApplicationContext().getCacheDir(), "logo.png").exists()) {
                        imageView.setImageURI(Uri.fromFile(new File(getApplicationContext().getCacheDir(), "logo.png")));
                    }
                    button2.setOnClickListener(new N0(this, imageView, 0));
                    button3.setOnClickListener(new View$OnClickListenerC0278r0(3, this));
                    button.setOnClickListener(new View.OnClickListener() { // from class: S0.O0
                        /* JADX WARN: Multi-variable type inference failed */
                        /* JADX WARN: Type inference failed for: r2v11 */
                        /* JADX WARN: Type inference failed for: r2v14 */
                        /* JADX WARN: Type inference failed for: r2v15 */
                        /* JADX WARN: Type inference failed for: r6v2, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion, java.lang.Object] */
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            FileOutputStream fileOutputStream;
                            int i4 = ActivityOptimizacion.f3061g0;
                            ?? r6 = ActivityOptimizacion.this;
                            r6.getClass();
                            r6.f3080Z = editText.getText().toString();
                            String obj = editText2.getText().toString();
                            r6.f3081a0 = obj;
                            String str = r6.f3080Z;
                            SharedPreferences.Editor edit = r6.getSharedPreferences("cutsettings", 0).edit();
                            edit.putString("textline1", str);
                            edit.putString("textline2", obj);
                            edit.apply();
                            File file = new File(r6.getApplicationContext().getCacheDir(), "logo.png");
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            PrintStream printStream = System.out;
                            printStream.println("ruta logo: " + file.getAbsolutePath());
                            FileOutputStream fileOutputStream2 = null;
                            try {
                                try {
                                    try {
                                        fileOutputStream = new FileOutputStream(file);
                                    } catch (Throwable th) {
                                        th = th;
                                    }
                                } catch (Exception e4) {
                                    e = e4;
                                }
                            } catch (IOException e5) {
                                e5.printStackTrace();
                            }
                            try {
                                fileOutputStream2 = 100;
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                fileOutputStream.close();
                            } catch (Exception e6) {
                                e = e6;
                                fileOutputStream2 = fileOutputStream;
                                e.printStackTrace();
                                if (fileOutputStream2 != null) {
                                    fileOutputStream2.close();
                                    fileOutputStream2 = fileOutputStream2;
                                }
                                r6.F(r6.f3076V.f2887n);
                                a4.dismiss();
                            } catch (Throwable th2) {
                                th = th2;
                                fileOutputStream2 = fileOutputStream;
                                if (fileOutputStream2 != null) {
                                    try {
                                        fileOutputStream2.close();
                                    } catch (IOException e7) {
                                        e7.printStackTrace();
                                    }
                                }
                                throw th;
                            }
                            r6.F(r6.f3076V.f2887n);
                            a4.dismiss();
                        }
                    });
                    a4.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    a4.show();
                }
            }
        } else if (itemId == 2131231076) {
            if (E() != null) {
                if (!this.f3076V.f2897x) {
                    F1.a aVar4 = this.f3062H;
                    if (aVar4 != null) {
                        aVar4.c(new f());
                    }
                    F1.a aVar5 = this.f3062H;
                    if (aVar5 != null) {
                        aVar5.e(this);
                    } else {
                        I();
                        startActivity(Intent.createChooser(K(E() + "\n" + this.f3076V.f2886m), getString(2131820871)));
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                    G(this.f3063I);
                } else {
                    I();
                    startActivity(Intent.createChooser(K(E() + "\n" + this.f3076V.f2886m), getString(2131820871)));
                }
            }
        } else if (itemId == 2131231070) {
            I();
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/account/subscriptions?sku=remove_ads&package=com.embarcadero.OptimizaCorte")));
        } else if (itemId == 2131231073) {
            C0379b.b(this, this.f3076V);
        }
        return super/*android.app.Activity*/.onOptionsItemSelected(menuItem);
    }

    public final void onPause() {
        I();
        this.f3073S = true;
        Timer timer = this.f3084d0;
        if (timer != null) {
            timer.cancel();
        }
        super.onPause();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v8, types: [c.a, java.lang.Object] */
    public final void onResume() {
        C0407a c0407a = new C0407a(getApplicationContext());
        this.f3077W = c0407a;
        this.f3076V = c0407a.a();
        this.f3073S = false;
        J();
        a aVar = this.f3085e0;
        Y0.a b4 = Y0.a.b(this, aVar);
        this.f3083c0 = b4;
        b4.f2825a = aVar;
        b4.i();
        if (this.f3083c0.d()) {
            this.f3083c0.a();
        }
        this.f3074T = H(this.f3076V.f2875a);
        this.f3075U = H(this.f3076V.f2876b);
        this.f3071Q = new T0.f(this.f3074T, this, this.f3076V, true);
        T0.f fVar = new T0.f(this.f3075U, this, this.f3076V, false);
        this.f3072R = fVar;
        this.f3071Q.f2326g = new L0(this);
        fVar.f2326g = new M0(this);
        if (this.f3078X == null) {
            this.f3078X = v(new P0(this), (AbstractC0363a) new Object());
        }
        Timer timer = this.f3084d0;
        if (timer != null) {
            timer.cancel();
        }
        this.f3084d0 = new Timer();
        this.f3084d0.schedule(new h(), 0L, 1200000L);
        super.onResume();
    }
}
