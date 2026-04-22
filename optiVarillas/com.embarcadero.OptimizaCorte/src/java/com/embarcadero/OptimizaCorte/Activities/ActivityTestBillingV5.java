package com.embarcadero.OptimizaCorte.Activities;

import S0.N0;
import S0.W0;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.embarcadero.OptimizaCorte.Activities.ActivityTestBillingV5;
import e.C0397f;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class ActivityTestBillingV5 extends C0397f {

    /* renamed from: Q  reason: collision with root package name */
    public static final /* synthetic */ int f3117Q = 0;

    /* renamed from: H  reason: collision with root package name */
    public Button f3118H;

    /* renamed from: I  reason: collision with root package name */
    public Button f3119I;

    /* renamed from: J  reason: collision with root package name */
    public Button f3120J;

    /* renamed from: K  reason: collision with root package name */
    public Button f3121K;

    /* renamed from: L  reason: collision with root package name */
    public Button f3122L;

    /* renamed from: M  reason: collision with root package name */
    public Button f3123M;

    /* renamed from: N  reason: collision with root package name */
    public Button f3124N;

    /* renamed from: O  reason: collision with root package name */
    public TextView f3125O;

    /* renamed from: P  reason: collision with root package name */
    public Y0.a f3126P;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class a implements Z0.a {
        public a() {
        }

        @Override // Z0.a
        public final void a() {
            Log.d("ACTV_BILLING_V5_COPY", "onNotLogin: No logueado!");
            ActivityTestBillingV5 activityTestBillingV5 = ActivityTestBillingV5.this;
            activityTestBillingV5.f3124N.setVisibility(8);
            activityTestBillingV5.f3125O.setText("Premium Status: NOT LOGGED");
        }

        @Override // Z0.a
        public final void b() {
            Log.d("ACTV_BILLING_V5_COPY", "onNotPurchase: list es 0000000000000");
            ActivityTestBillingV5 activityTestBillingV5 = ActivityTestBillingV5.this;
            activityTestBillingV5.f3124N.setVisibility(0);
            activityTestBillingV5.f3125O.setText("Premium Status: NOT premium");
        }

        @Override // Z0.a
        public final void c(ArrayList arrayList) {
            Log.d("ACTV_BILLING_V5_COPY", "onPurchase: hay compras realizadas");
            Log.d("ACTV_BILLING_V5_COPY", "onPurchase: " + arrayList);
            TextView textView = ActivityTestBillingV5.this.f3125O;
            textView.setText("Premium Status: PREMIUM (" + arrayList.size() + " productos comprados)");
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(2131427361);
        this.f3118H = (Button) findViewById(2131230834);
        this.f3119I = (Button) findViewById(2131230835);
        this.f3120J = (Button) findViewById(2131230836);
        this.f3121K = (Button) findViewById(2131230837);
        this.f3122L = (Button) findViewById(2131230838);
        this.f3123M = (Button) findViewById(2131230839);
        this.f3124N = (Button) findViewById(2131230840);
        this.f3125O = (TextView) findViewById(2131231320);
        final a aVar = new a();
        this.f3118H.setOnClickListener(new View.OnClickListener() { // from class: S0.X0
            /* JADX WARN: Type inference failed for: r2v2, types: [android.content.Context, java.lang.Object, com.embarcadero.OptimizaCorte.Activities.ActivityTestBillingV5] */
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                int i4 = ActivityTestBillingV5.f3117Q;
                ?? r22 = ActivityTestBillingV5.this;
                r22.getClass();
                Y0.a b4 = Y0.a.b(r22, aVar);
                r22.f3126P = b4;
                b4.i();
            }
        });
        this.f3119I.setOnClickListener(new View.OnClickListener() { // from class: S0.Y0
            /* JADX WARN: Type inference failed for: r3v2, types: [android.content.Context, java.lang.Object, com.embarcadero.OptimizaCorte.Activities.ActivityTestBillingV5] */
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                int i4 = ActivityTestBillingV5.f3117Q;
                ?? r32 = ActivityTestBillingV5.this;
                r32.getClass();
                Y0.a b4 = Y0.a.b(r32, aVar);
                r32.f3126P = b4;
                if (b4.d()) {
                    Log.d("ACTV_BILLING_V5_COPY", "onClick B: is ready, quering purchases..");
                    r32.f3126P.a();
                    return;
                }
                Log.d("ACTV_BILLING_V5_COPY", "onClick B: NOT ready");
            }
        });
        this.f3120J.setOnClickListener(new View.OnClickListener() { // from class: S0.Z0
            /* JADX WARN: Type inference failed for: r2v2, types: [android.content.Context, java.lang.Object, com.embarcadero.OptimizaCorte.Activities.ActivityTestBillingV5, android.app.Activity] */
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                int i4 = ActivityTestBillingV5.f3117Q;
                ?? r22 = ActivityTestBillingV5.this;
                r22.getClass();
                Y0.a b4 = Y0.a.b(r22, aVar);
                r22.f3126P = b4;
                Y0.a.g(r22, b4);
            }
        });
        this.f3121K.setOnClickListener(new N0(this, aVar, 1));
        this.f3122L.setOnClickListener(new View.OnClickListener() { // from class: S0.a1
            /* JADX WARN: Type inference failed for: r3v2, types: [android.content.Context, java.lang.Object, com.embarcadero.OptimizaCorte.Activities.ActivityTestBillingV5] */
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                int i4 = ActivityTestBillingV5.f3117Q;
                ?? r32 = ActivityTestBillingV5.this;
                r32.getClass();
                r32.f3126P = Y0.a.b(r32, aVar);
                StringBuilder sb = new StringBuilder("onClick: price ads: ");
                Y0.a aVar2 = r32.f3126P;
                Q0.d dVar = Y0.a.f2823d;
                aVar2.getClass();
                sb.append(Y0.a.c(dVar));
                Log.d("ACTV_BILLING_V5_COPY", sb.toString());
            }
        });
        this.f3123M.setOnClickListener(new View.OnClickListener() { // from class: S0.b1
            /* JADX WARN: Type inference failed for: r3v2, types: [android.content.Context, java.lang.Object, com.embarcadero.OptimizaCorte.Activities.ActivityTestBillingV5] */
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                int i4 = ActivityTestBillingV5.f3117Q;
                ?? r32 = ActivityTestBillingV5.this;
                r32.getClass();
                r32.f3126P = Y0.a.b(r32, aVar);
                StringBuilder sb = new StringBuilder("onClick: price ads: ");
                Y0.a aVar2 = r32.f3126P;
                Q0.d dVar = Y0.a.f2824e;
                aVar2.getClass();
                sb.append(Y0.a.c(dVar));
                Log.d("ACTV_BILLING_V5_COPY", sb.toString());
            }
        });
        this.f3124N.setOnClickListener(new W0(this, aVar, 1));
    }
}
