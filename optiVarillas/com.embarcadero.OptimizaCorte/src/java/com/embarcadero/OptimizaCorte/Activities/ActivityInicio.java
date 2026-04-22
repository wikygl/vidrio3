package com.embarcadero.OptimizaCorte.Activities;

import A1.T0;
import M.C;
import S0.A;
import S0.B;
import S0.RunnableC0281t;
import S0.RunnableC0285v;
import S0.RunnableC0293z;
import S0.View$OnClickListenerC0270n;
import S0.View$OnClickListenerC0272o;
import S0.View$OnClickListenerC0274p;
import S0.r;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;
import androidx.appcompat.widget.SwitchCompat;
import b1.C0353a;
import c1.C0371d;
import c1.C0373f;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.google.android.material.textfield.TextInputEditText;
import d1.C0379b;
import e.AbstractC0392a;
import e.C0397f;
import e1.C0407a;
import i2.c0;
import j$.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import t1.C0802d;
import t1.C0803e;
import t1.C0804f;
import t1.C0810l;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class ActivityInicio extends C0397f {

    /* renamed from: A0  reason: collision with root package name */
    public static final /* synthetic */ int f2947A0 = 0;

    /* renamed from: H  reason: collision with root package name */
    public ListView f2948H;

    /* renamed from: I  reason: collision with root package name */
    public Button f2949I;

    /* renamed from: J  reason: collision with root package name */
    public Button f2950J;

    /* renamed from: K  reason: collision with root package name */
    public TextInputEditText f2951K;

    /* renamed from: L  reason: collision with root package name */
    public TextInputEditText f2952L;

    /* renamed from: M  reason: collision with root package name */
    public TextInputEditText f2953M;

    /* renamed from: N  reason: collision with root package name */
    public TextInputEditText f2954N;

    /* renamed from: O  reason: collision with root package name */
    public SwitchCompat f2955O;

    /* renamed from: P  reason: collision with root package name */
    public SwitchCompat f2956P;

    /* renamed from: Q  reason: collision with root package name */
    public Button f2957Q;

    /* renamed from: R  reason: collision with root package name */
    public Button f2958R;

    /* renamed from: S  reason: collision with root package name */
    public TextView f2959S;

    /* renamed from: T  reason: collision with root package name */
    public ArrayAdapter f2960T;

    /* renamed from: U  reason: collision with root package name */
    public ArrayList<String> f2961U;

    /* renamed from: V  reason: collision with root package name */
    public Spinner f2962V;

    /* renamed from: W  reason: collision with root package name */
    public Spinner f2963W;

    /* renamed from: X  reason: collision with root package name */
    public boolean f2964X;

    /* renamed from: Y  reason: collision with root package name */
    public boolean f2965Y;

    /* renamed from: Z  reason: collision with root package name */
    public SeekBar f2966Z;

    /* renamed from: a0  reason: collision with root package name */
    public Button f2967a0;

    /* renamed from: b0  reason: collision with root package name */
    public SwitchCompat f2968b0;

    /* renamed from: c0  reason: collision with root package name */
    public ArrayList f2969c0;

    /* renamed from: d0  reason: collision with root package name */
    public ArrayList f2970d0;

    /* renamed from: e0  reason: collision with root package name */
    public ArrayList f2971e0;

    /* renamed from: f0  reason: collision with root package name */
    public ArrayList<Double> f2972f0;

    /* renamed from: g0  reason: collision with root package name */
    public ArrayList f2973g0;

    /* renamed from: h0  reason: collision with root package name */
    public HashSet f2974h0;

    /* renamed from: i0  reason: collision with root package name */
    public Intent f2975i0;

    /* renamed from: j0  reason: collision with root package name */
    public Intent f2976j0;

    /* renamed from: k0  reason: collision with root package name */
    public Intent f2977k0;
    public C0804f l0;

    /* renamed from: m0  reason: collision with root package name */
    public FrameLayout f2978m0;

    /* renamed from: n0  reason: collision with root package name */
    public F1.a f2979n0;

    /* renamed from: o0  reason: collision with root package name */
    public B f2980o0;

    /* renamed from: p0  reason: collision with root package name */
    public ArrayList f2981p0;

    /* renamed from: q0  reason: collision with root package name */
    public ArrayAdapter<String> f2982q0;

    /* renamed from: r0  reason: collision with root package name */
    public ArrayAdapter<String> f2983r0;

    /* renamed from: s0  reason: collision with root package name */
    public double f2984s0;

    /* renamed from: t0  reason: collision with root package name */
    public boolean f2985t0;

    /* renamed from: u0  reason: collision with root package name */
    public C0353a f2986u0;

    /* renamed from: v0  reason: collision with root package name */
    public C0407a f2987v0;

    /* renamed from: w0  reason: collision with root package name */
    public Y0.a f2988w0;

    /* renamed from: x0  reason: collision with root package name */
    public Timer f2989x0;

    /* renamed from: y0  reason: collision with root package name */
    public final b f2990y0 = new b();

    /* renamed from: z0  reason: collision with root package name */
    public final c f2991z0 = new c();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class a extends TimerTask {
        public a() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public final void run() {
            ActivityInicio.this.runOnUiThread(new RunnableC0293z(0, this));
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class b implements Z0.a {
        public b() {
        }

        @Override // Z0.a
        public final void a() {
            ActivityInicio.this.runOnUiThread(new RunnableC0285v(this, 0));
        }

        @Override // Z0.a
        public final void b() {
            ActivityInicio.this.runOnUiThread(new RunnableC0281t(0, this));
        }

        @Override // Z0.a
        public final void c(ArrayList arrayList) {
            ActivityInicio.this.runOnUiThread(new C(1, this));
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class c implements Z0.a {
        public c() {
        }

        @Override // Z0.a
        public final void a() {
            ActivityInicio.this.runOnUiThread(new C(2, this));
        }

        @Override // Z0.a
        public final void b() {
            ActivityInicio.this.runOnUiThread(new A(0, this));
        }

        @Override // Z0.a
        public final void c(ArrayList arrayList) {
            ActivityInicio.this.runOnUiThread(new Q2.g(1, this));
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class d implements AdapterView.OnItemSelectedListener {
        public d() {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        @SuppressLint({"SetTextI18n"})
        public final void onItemSelected(AdapterView<?> adapterView, View view, int i4, long j4) {
            ActivityInicio activityInicio = ActivityInicio.this;
            if (activityInicio.f2964X) {
                if (i4 == 0) {
                    activityInicio.f2963W.setEnabled(true);
                    activityInicio.f2973g0.clear();
                    activityInicio.f2973g0.addAll(activityInicio.f2970d0);
                    activityInicio.f2982q0.notifyDataSetChanged();
                    activityInicio.f2963W.setSelection(0, true);
                    activityInicio.H();
                    activityInicio.f2961U.clear();
                    activityInicio.f2961U.add("6500");
                    activityInicio.f2960T.notifyDataSetChanged();
                    activityInicio.f2951K.setText("10");
                    activityInicio.f2952L.setText("10");
                    activityInicio.f2953M.setText("4");
                    activityInicio.f2986u0.f2890q = true;
                }
                if (i4 == 2) {
                    activityInicio.f2963W.setEnabled(false);
                    activityInicio.H();
                    activityInicio.f2961U.clear();
                    activityInicio.f2961U.add("256");
                    activityInicio.f2960T.notifyDataSetChanged();
                    activityInicio.f2951K.setText("0.79");
                    activityInicio.f2952L.setText("0.79");
                    activityInicio.f2953M.setText("0.16");
                    activityInicio.f2963W.setEnabled(false);
                    activityInicio.f2986u0.f2890q = false;
                }
                if (i4 == 1) {
                    activityInicio.I();
                    Log.d("INI_BILLING_V5_COPY", "onItemSelected: setTecladoTexto()");
                    activityInicio.f2963W.setEnabled(true);
                    activityInicio.f2973g0.clear();
                    activityInicio.f2973g0.addAll(activityInicio.f2971e0);
                    activityInicio.f2982q0.notifyDataSetChanged();
                    activityInicio.f2963W.setSelection(0, true);
                    activityInicio.f2961U.clear();
                    activityInicio.f2961U.add(C0373f.j(6500.0d, Integer.parseInt(activityInicio.f2982q0.getItem(0).split("/")[1])));
                    activityInicio.f2960T.notifyDataSetChanged();
                    activityInicio.f2951K.setText("");
                    activityInicio.f2952L.setText("");
                    activityInicio.f2953M.setText("");
                    activityInicio.f2951K.setText(C0373f.j(10.0d, Integer.parseInt(activityInicio.f2982q0.getItem(0).split("/")[1])));
                    activityInicio.f2952L.setText(C0373f.j(10.0d, Integer.parseInt(activityInicio.f2982q0.getItem(0).split("/")[1])));
                    activityInicio.f2953M.setText(C0373f.j(4.0d, Integer.parseInt(activityInicio.f2982q0.getItem(0).split("/")[1])));
                    activityInicio.f2986u0.f2890q = true;
                }
                activityInicio.f2964X = false;
            }
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public final void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class e implements AdapterView.OnItemSelectedListener {
        public e() {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public final void onItemSelected(AdapterView<?> adapterView, View view, int i4, long j4) {
            boolean z4;
            ActivityInicio activityInicio = ActivityInicio.this;
            if (activityInicio.f2965Y) {
                int selectedItemPosition = activityInicio.f2963W.getSelectedItemPosition();
                if (selectedItemPosition != 0) {
                    if (selectedItemPosition != 1) {
                        if (selectedItemPosition != 2) {
                            if (selectedItemPosition == 3) {
                                if (activityInicio.f2962V.getSelectedItemPosition() == 0) {
                                    activityInicio.f2961U.clear();
                                    activityInicio.f2961U.add("6500");
                                    activityInicio.f2960T.notifyDataSetChanged();
                                    activityInicio.f2951K.setText("10");
                                    activityInicio.f2952L.setText("10");
                                    activityInicio.f2953M.setText("4");
                                } else {
                                    activityInicio.f2961U.clear();
                                    activityInicio.f2961U.add(C0373f.j(6500.0d, Long.parseLong(activityInicio.f2982q0.getItem(3).split("/")[1])));
                                    activityInicio.f2960T.notifyDataSetChanged();
                                    activityInicio.f2951K.setText("");
                                    activityInicio.f2952L.setText("");
                                    activityInicio.f2953M.setText("");
                                    F3.e.c(activityInicio.f2982q0.getItem(3).split("/")[1], 10.0d, activityInicio.f2951K);
                                    F3.e.c(activityInicio.f2982q0.getItem(3).split("/")[1], 10.0d, activityInicio.f2952L);
                                    F3.e.c(activityInicio.f2982q0.getItem(3).split("/")[1], 4.0d, activityInicio.f2953M);
                                }
                            }
                        } else if (activityInicio.f2962V.getSelectedItemPosition() == 0) {
                            activityInicio.f2961U.clear();
                            activityInicio.f2961U.add(C0373f.g(6.5d));
                            activityInicio.f2960T.notifyDataSetChanged();
                            activityInicio.f2951K.setText("");
                            activityInicio.f2952L.setText("");
                            activityInicio.f2953M.setText("");
                            activityInicio.f2951K.setText(C0373f.g(0.01d));
                            activityInicio.f2952L.setText(C0373f.g(0.01d));
                            activityInicio.f2953M.setText(C0373f.g(0.004d));
                        } else {
                            activityInicio.f2961U.clear();
                            activityInicio.f2961U.add(C0373f.j(6500.0d, Long.parseLong(activityInicio.f2982q0.getItem(2).split("/")[1])));
                            activityInicio.f2960T.notifyDataSetChanged();
                            activityInicio.f2951K.setText("");
                            activityInicio.f2952L.setText("");
                            activityInicio.f2953M.setText("");
                            F3.e.c(activityInicio.f2982q0.getItem(2).split("/")[1], 10.0d, activityInicio.f2951K);
                            F3.e.c(activityInicio.f2982q0.getItem(2).split("/")[1], 10.0d, activityInicio.f2952L);
                            F3.e.c(activityInicio.f2982q0.getItem(2).split("/")[1], 4.0d, activityInicio.f2953M);
                        }
                    } else if (activityInicio.f2962V.getSelectedItemPosition() == 0) {
                        activityInicio.f2961U.clear();
                        activityInicio.f2961U.add(C0373f.g(650.0d));
                        activityInicio.f2960T.notifyDataSetChanged();
                        activityInicio.f2951K.setText("");
                        activityInicio.f2952L.setText("");
                        activityInicio.f2953M.setText("");
                        activityInicio.f2951K.setText(C0373f.g(1.0d));
                        activityInicio.f2952L.setText(C0373f.g(1.0d));
                        activityInicio.f2953M.setText(C0373f.g(0.4d));
                    } else {
                        activityInicio.f2961U.clear();
                        activityInicio.f2961U.add(C0373f.j(6500.0d, Long.parseLong(activityInicio.f2982q0.getItem(1).split("/")[1])));
                        activityInicio.f2960T.notifyDataSetChanged();
                        activityInicio.f2951K.setText("");
                        activityInicio.f2952L.setText("");
                        activityInicio.f2953M.setText("");
                        F3.e.c(activityInicio.f2982q0.getItem(1).split("/")[1], 10.0d, activityInicio.f2951K);
                        F3.e.c(activityInicio.f2982q0.getItem(1).split("/")[1], 10.0d, activityInicio.f2952L);
                        F3.e.c(activityInicio.f2982q0.getItem(1).split("/")[1], 4.0d, activityInicio.f2953M);
                    }
                } else if (activityInicio.f2962V.getSelectedItemPosition() == 0) {
                    activityInicio.f2961U.clear();
                    activityInicio.f2961U.add("6500");
                    activityInicio.f2960T.notifyDataSetChanged();
                    activityInicio.f2951K.setText("10");
                    activityInicio.f2952L.setText("10");
                    activityInicio.f2953M.setText("4");
                } else {
                    activityInicio.f2961U.clear();
                    activityInicio.f2961U.add(C0373f.j(6500.0d, Long.parseLong(activityInicio.f2982q0.getItem(0).split("/")[1])));
                    activityInicio.f2960T.notifyDataSetChanged();
                    activityInicio.f2951K.setText("");
                    activityInicio.f2952L.setText("");
                    activityInicio.f2953M.setText("");
                    z4 = false;
                    F3.e.c(activityInicio.f2982q0.getItem(0).split("/")[1], 10.0d, activityInicio.f2951K);
                    F3.e.c(activityInicio.f2982q0.getItem(0).split("/")[1], 10.0d, activityInicio.f2952L);
                    F3.e.c(activityInicio.f2982q0.getItem(0).split("/")[1], 4.0d, activityInicio.f2953M);
                    activityInicio.f2965Y = z4;
                }
                z4 = false;
                activityInicio.f2965Y = z4;
            }
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public final void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class f extends G3.g {
        public f() {
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, android.app.Activity] */
        @Override // G3.g
        public final void u() {
            int i4 = ActivityInicio.f2947A0;
            ?? r02 = ActivityInicio.this;
            r02.F();
            r02.startActivity(r02.f2975i0);
            r02.overridePendingTransition(2130771998, 2130771999);
            Log.d("TAG", "The ad was dismissed.");
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, android.app.Activity] */
        @Override // G3.g
        public final void w() {
            int i4 = ActivityInicio.f2947A0;
            ?? r02 = ActivityInicio.this;
            r02.F();
            r02.startActivity(r02.f2975i0);
            r02.overridePendingTransition(2130771998, 2130771999);
            Log.d("TAG", "The ad failed to show.");
        }

        @Override // G3.g
        public final void y() {
            ActivityInicio.this.f2979n0 = null;
            Log.d("TAG", "The ad was shown.");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class g extends G3.g {

        /* renamed from: k  reason: collision with root package name */
        public final /* synthetic */ Intent f2998k;

        public g(Intent intent) {
            this.f2998k = intent;
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, android.app.Activity] */
        @Override // G3.g
        public final void u() {
            int i4 = ActivityInicio.f2947A0;
            ?? r02 = ActivityInicio.this;
            r02.F();
            r02.startActivity(this.f2998k);
            r02.overridePendingTransition(2130771998, 2130771999);
            Log.d("TAG", "The ad was dismissed.");
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, android.app.Activity] */
        @Override // G3.g
        public final void w() {
            int i4 = ActivityInicio.f2947A0;
            ?? r02 = ActivityInicio.this;
            r02.F();
            r02.startActivity(this.f2998k);
            r02.overridePendingTransition(2130771998, 2130771999);
            Log.d("TAG", "The ad failed to show.");
        }

        @Override // G3.g
        public final void y() {
            ActivityInicio.this.f2979n0 = null;
            Log.d("TAG", "The ad was shown1.");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class h extends G3.g {
        public h() {
        }

        @Override // G3.g
        public final void u() {
            int i4 = ActivityInicio.f2947A0;
            ActivityInicio.this.J();
            Log.d("TAG", "The ad was dismissed.");
        }

        @Override // G3.g
        public final void w() {
            int i4 = ActivityInicio.f2947A0;
            ActivityInicio.this.J();
            Log.d("TAG", "The ad failed to show.");
        }

        @Override // G3.g
        public final void y() {
            ActivityInicio.this.f2979n0 = null;
            Log.d("TAG", "The ad was shown.");
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void A(ActivityInicio activityInicio) {
        SharedPreferences sharedPreferences = activityInicio.getSharedPreferences("cutsettings", 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("opencount", (sharedPreferences.getInt("opencount", 1) % 5) + 1);
        edit.apply();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void C() {
        Toast.makeText((Context) this, (CharSequence) (getString(2131820726) + getString(2131820701)), 1).show();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void D() {
        new V0.c(this, this.l0, getString(2131820579), this.f2980o0).a();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void E(F1.b bVar) {
        F1.a.b(this, getString(2131820579), new C0802d(new C0802d.a()), bVar);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final synchronized void F() {
        try {
            if (this.f2986u0 != null) {
                if (this.f2987v0 == null) {
                    this.f2987v0 = new C0407a(getApplicationContext());
                }
                synchronized (this) {
                    this.f2987v0.b(this.f2986u0);
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final synchronized void G() {
        SharedPreferences.Editor edit = getSharedPreferences("cutsettings", 0).edit();
        edit.putInt("spinner1", this.f2962V.getSelectedItemPosition());
        edit.putInt("spinner2", this.f2963W.getSelectedItemPosition());
        edit.putStringSet("listview_stock", new HashSet(this.f2961U));
        Editable text = this.f2951K.getText();
        Objects.requireNonNull(text);
        edit.putString("ed_damageleft", text.toString());
        Editable text2 = this.f2952L.getText();
        Objects.requireNonNull(text2);
        edit.putString("ed_damageright", text2.toString());
        Editable text3 = this.f2953M.getText();
        Objects.requireNonNull(text3);
        edit.putString("ed_disco", text3.toString());
        Editable text4 = this.f2954N.getText();
        Objects.requireNonNull(text4);
        edit.putString("ed_tilt", text4.toString());
        edit.putBoolean("sw_disc", this.f2955O.isChecked());
        edit.putInt("seekbar_engine", this.f2966Z.getProgress());
        edit.putBoolean("sw_engine", this.f2956P.isChecked());
        edit.putBoolean("sw_experimental", this.f2968b0.isChecked());
        edit.apply();
    }

    public final void H() {
        this.f2951K.setInputType(524433);
        this.f2952L.setInputType(524433);
        this.f2953M.setInputType(524433);
        this.f2951K.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        this.f2952L.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        this.f2953M.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        Log.d("INI_BILLING_V5_COPY", "setTecladoNumerico: ");
    }

    public final void I() {
        TextInputEditText textInputEditText = this.f2951K;
        textInputEditText.setFilters(new InputFilter[]{new C0371d(textInputEditText)});
        this.f2951K.setKeyListener(DigitsKeyListener.getInstance("0123456789a. /"));
        TextInputEditText textInputEditText2 = this.f2952L;
        textInputEditText2.setFilters(new InputFilter[]{new C0371d(textInputEditText2)});
        this.f2952L.setKeyListener(DigitsKeyListener.getInstance("0123456789a. /"));
        TextInputEditText textInputEditText3 = this.f2953M;
        textInputEditText3.setFilters(new InputFilter[]{new C0371d(textInputEditText3)});
        this.f2953M.setKeyListener(DigitsKeyListener.getInstance("0123456789a. /"));
        this.f2951K.setInputType(524289);
        this.f2952L.setInputType(524289);
        this.f2953M.setInputType(524289);
        Log.d("INI_BILLING_V5_COPY", "setTecladoTexto: ");
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void J() {
        G();
        F();
        C0373f.l(10L, this);
        startActivity(this.f2977k0);
        overridePendingTransition(2130771998, 2130771999);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v93, types: [R0.a$a, java.lang.Object] */
    @SuppressLint({"SetTextI18n"})
    public final void onCreate(Bundle bundle) {
        StringBuilder sb;
        String str;
        boolean z4;
        setTheme(2131886091);
        C0407a c0407a = new C0407a(getApplicationContext());
        this.f2987v0 = c0407a;
        this.f2986u0 = c0407a.a();
        super.onCreate(bundle);
        setContentView(2131427356);
        AbstractC0392a y4 = y();
        if (y4 != null) {
            y4.c(getString(2131820575));
            y4.b(getString(2131820927));
        }
        this.f2985t0 = false;
        this.f2959S = (TextView) findViewById(2131231319);
        this.f2966Z = (SeekBar) findViewById(2131231201);
        this.f2978m0 = (FrameLayout) findViewById(2131230791);
        C0804f c0804f = new C0804f(this);
        this.l0 = c0804f;
        c0804f.setAdUnitId(getString(2131820578));
        this.f2978m0.addView(this.l0);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        this.l0.setAdSize(C0803e.a(this, (int) (displayMetrics.widthPixels / displayMetrics.density)));
        this.f2980o0 = new B(this);
        ArrayList arrayList = new ArrayList();
        this.f2981p0 = arrayList;
        arrayList.add("9E438B9A38FAA47A8A8A16E20E3B0D5E");
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = this.f2981p0;
        arrayList2.clear();
        if (arrayList3 != null) {
            arrayList2.addAll(arrayList3);
        }
        C0810l c0810l = new C0810l(arrayList2);
        T0 c4 = T0.c();
        c4.getClass();
        synchronized (c4.f93e) {
            try {
                C0810l c0810l2 = c4.f94g;
                c4.f94g = c0810l;
                if (c4.f != null) {
                    c0810l2.getClass();
                }
            } finally {
            }
        }
        this.f2975i0 = new Intent((Context) this, (Class<?>) ActivityListaCorte.class);
        this.f2976j0 = new Intent((Context) this, (Class<?>) ActivityRetales.class);
        this.f2977k0 = new Intent((Context) this, (Class<?>) ActivityListasGuardadas.class);
        new Intent((Context) this, (Class<?>) ActivityListaCorteContainer.class);
        ((ScrollView) findViewById(2131231189)).setOnTouchListener(new View.OnTouchListener() { // from class: S0.i
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                int i4 = ActivityInicio.f2947A0;
                ActivityInicio.this.findViewById(2131231040).getParent().requestDisallowInterceptTouchEvent(false);
                return view.performClick();
            }
        });
        ListView listView = (ListView) findViewById(2131231040);
        this.f2948H = listView;
        listView.setOnTouchListener(new View.OnTouchListener() { // from class: S0.j
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                if (ActivityInicio.this.f2961U.size() > 4) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return view.performClick();
            }
        });
        this.f2950J = (Button) findViewById(2131230842);
        this.f2949I = (Button) findViewById(2131230843);
        Button button = (Button) findViewById(2131230846);
        this.f2957Q = button;
        ArrayList<String> arrayList4 = this.f2986u0.f2884k;
        if (arrayList4 != null) {
            if (!arrayList4.isEmpty()) {
                sb = new StringBuilder();
                sb.append(getString(2131820597));
                str = " [1+]";
                sb.append(str);
                button.setText(sb.toString());
                this.f2958R = (Button) findViewById(2131230844);
                this.f2967a0 = (Button) findViewById(2131230845);
                this.f2951K = findViewById(2131230937);
                this.f2952L = findViewById(2131230936);
                this.f2953M = findViewById(2131230938);
                this.f2954N = findViewById(2131230939);
                this.f2962V = (Spinner) findViewById(2131231218);
                this.f2963W = (Spinner) findViewById(2131231219);
                ArrayList arrayList5 = new ArrayList();
                this.f2969c0 = arrayList5;
                arrayList5.add(getString(2131820916));
                this.f2969c0.add(getString(2131820914));
                this.f2969c0.add(getString(2131820915));
                ArrayList arrayList6 = new ArrayList();
                this.f2970d0 = arrayList6;
                arrayList6.add(getString(2131820646));
                this.f2970d0.add(getString(2131820644));
                this.f2970d0.add(getString(2131820645));
                ArrayList arrayList7 = new ArrayList();
                this.f2971e0 = arrayList7;
                arrayList7.add("1/4");
                this.f2971e0.add("1/8");
                this.f2971e0.add("1/16");
                this.f2971e0.add("1/32");
                this.f2972f0 = new ArrayList<>();
                this.f2955O = findViewById(2131231241);
                this.f2956P = findViewById(2131231239);
                SwitchCompat findViewById = findViewById(2131231240);
                this.f2968b0 = findViewById;
                this.f2956P.setEnabled(!findViewById.isChecked());
                SeekBar seekBar = this.f2966Z;
                if (this.f2968b0.isChecked() && !this.f2956P.isChecked()) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                seekBar.setEnabled(z4);
                this.f2968b0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: A2.a
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z5) {
                        boolean z6;
                        switch (r2) {
                            case 0:
                                CompoundButton.OnCheckedChangeListener onCheckedChangeListener = this.r;
                                if (onCheckedChangeListener != null) {
                                    onCheckedChangeListener.onCheckedChanged(compoundButton, z5);
                                    return;
                                }
                                return;
                            default:
                                ActivityInicio activityInicio = (ActivityInicio) this;
                                activityInicio.f2956P.setEnabled(!z5);
                                SeekBar seekBar2 = activityInicio.f2966Z;
                                if (!z5 && !activityInicio.f2956P.isChecked()) {
                                    z6 = true;
                                } else {
                                    z6 = false;
                                }
                                seekBar2.setEnabled(z6);
                                return;
                        }
                    }
                });
                v3.h.e(this, "context");
                ?? obj = new Object();
                obj.f = 1;
                obj.f = 3;
                obj.f2051a = getString(2131820886);
                obj.f2053c = C.a.b(this, 2131034146);
                new R0.a(this, obj).show();
                this.f2959S.setText("Version 5.2.9.2 (build 317)");
                this.f2950J.setOnClickListener(new View.OnClickListener() { // from class: S0.a
                    /* JADX WARN: Type inference failed for: r1v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, java.lang.Object] */
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        int i4 = ActivityInicio.f2947A0;
                        final ?? r12 = ActivityInicio.this;
                        r12.getClass();
                        C0373f.l(10L, r12);
                        View inflate = LayoutInflater.from(r12).inflate(2131427462, (ViewGroup) null);
                        final EditText editText = (EditText) inflate.findViewById(2131230940);
                        InputFilter inputFilter = new InputFilter() { // from class: S0.s
                            @Override // android.text.InputFilter
                            public final CharSequence filter(CharSequence charSequence, int i5, int i6, Spanned spanned, int i7, int i8) {
                                int i9 = ActivityInicio.f2947A0;
                                if (charSequence != null) {
                                    for (int i10 = 0; i10 < charSequence.length(); i10++) {
                                        EditText editText2 = editText;
                                        Editable text = editText2.getText();
                                        Objects.requireNonNull(text);
                                        if (text.toString().contains(".") && String.valueOf(charSequence.charAt(i10)).equals(".")) {
                                            return charSequence.toString().substring(0, i10);
                                        }
                                        if (editText2.getText().toString().contains(" ") && String.valueOf(charSequence.charAt(i10)).equals(" ")) {
                                            return charSequence.toString().substring(0, i10);
                                        }
                                        if (editText2.getText().toString().contains("/") && String.valueOf(charSequence.charAt(i10)).equals("/")) {
                                            return charSequence.toString().substring(0, i10);
                                        }
                                        if (!"0123456789. /".contains(String.valueOf(charSequence.charAt(i10)))) {
                                            return charSequence.toString().substring(0, i10);
                                        }
                                    }
                                    return charSequence;
                                }
                                return charSequence;
                            }
                        };
                        if (r12.f2962V.getSelectedItemPosition() == 1) {
                            editText.setFilters(new InputFilter[]{inputFilter});
                            editText.setRawInputType(524289);
                        } else {
                            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
                            editText.setRawInputType(8194);
                        }
                        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: S0.b
                            @Override // android.view.View.OnFocusChangeListener
                            public final void onFocusChange(View view2, boolean z5) {
                                int i5 = ActivityInicio.f2947A0;
                                ActivityInicio activityInicio = ActivityInicio.this;
                                activityInicio.getClass();
                                EditText editText2 = editText;
                                editText2.post(new D.g(activityInicio, 1, editText2));
                            }
                        });
                        editText.requestFocus();
                        b.a aVar = new b.a((Context) r12);
                        String string = r12.getString(2131820648);
                        AlertController.b bVar = aVar.a;
                        bVar.d = string;
                        bVar.q = inflate;
                        aVar.b(r12.getString(2131820699), new DialogInterface$OnClickListenerC0248c(0));
                        aVar.c(r12.getString(2131820587), new DialogInterface.OnClickListener() { // from class: S0.d
                            /* JADX WARN: Type inference failed for: r3v2, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, java.lang.Object] */
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i5) {
                                int i6 = ActivityInicio.f2947A0;
                                ?? r32 = ActivityInicio.this;
                                r32.getClass();
                                String obj2 = editText.getText().toString();
                                if (!obj2.isEmpty()) {
                                    r32.f2961U.add(obj2);
                                    r32.f2961U.removeAll(Arrays.asList("", null));
                                    r32.f2961U.trimToSize();
                                    r32.f2960T.notifyDataSetChanged();
                                } else {
                                    Toast.makeText((Context) r32, r32.getString(2131820649), 0).show();
                                }
                                r32.G();
                            }
                        });
                        aVar.a().show();
                    }
                });
                this.f2973g0 = new ArrayList(this.f2970d0);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>((Context) this, 17367048, (List<String>) this.f2969c0);
                this.f2983r0 = arrayAdapter;
                arrayAdapter.setDropDownViewResource(17367049);
                this.f2962V.setAdapter((SpinnerAdapter) this.f2983r0);
                this.f2962V.setSelection(0);
                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>((Context) this, 17367048, (List<String>) this.f2973g0);
                this.f2982q0 = arrayAdapter2;
                arrayAdapter2.setDropDownViewResource(17367049);
                this.f2963W.setAdapter((SpinnerAdapter) this.f2982q0);
                this.f2962V.setOnTouchListener(new View.OnTouchListener() { // from class: S0.k
                    @Override // android.view.View.OnTouchListener
                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        ActivityInicio.this.f2964X = true;
                        return view.performClick();
                    }
                });
                this.f2962V.setOnItemSelectedListener(new d());
                this.f2963W.setOnTouchListener(new View.OnTouchListener() { // from class: S0.l
                    @Override // android.view.View.OnTouchListener
                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        ActivityInicio.this.f2965Y = true;
                        return view.performClick();
                    }
                });
                this.f2963W.setOnItemSelectedListener(new e());
                this.f2948H.setTranscriptMode(1);
                this.f2961U = new ArrayList<>();
                ArrayAdapter arrayAdapter3 = new ArrayAdapter((Context) this, 2131427463, (List) this.f2961U);
                this.f2960T = arrayAdapter3;
                this.f2948H.setAdapter((ListAdapter) arrayAdapter3);
                this.f2948H.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: S0.m
                    /* JADX WARN: Type inference failed for: r4v2, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, java.lang.Object] */
                    @Override // android.widget.AdapterView.OnItemLongClickListener
                    public final boolean onItemLongClick(AdapterView adapterView, View view, final int i4, long j4) {
                        int i5 = ActivityInicio.f2947A0;
                        final ?? r4 = ActivityInicio.this;
                        r4.getClass();
                        C0373f.l(10L, r4);
                        b.a aVar = new b.a((Context) r4);
                        String string = r4.getString(2131820695);
                        AlertController.b bVar = aVar.a;
                        bVar.d = string;
                        bVar.f = r4.getString(2131820693) + r4.f2961U.get(i4) + r4.getString(2131820694);
                        bVar.m = true;
                        aVar.c(r4.getString(2131820918), new DialogInterface.OnClickListener() { // from class: S0.e
                            /* JADX WARN: Type inference failed for: r0v0, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio] */
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i6) {
                                int i7 = ActivityInicio.f2947A0;
                                StringBuilder sb2 = new StringBuilder();
                                ?? r02 = ActivityInicio.this;
                                sb2.append(r02.getString(2131820696));
                                ArrayList<String> arrayList8 = r02.f2961U;
                                int i8 = i4;
                                sb2.append(arrayList8.get(i8));
                                sb2.append(r02.getString(2131820697));
                                Toast.makeText((Context) r02, sb2.toString(), 0).show();
                                r02.f2961U.remove(i8);
                                r02.f2960T.notifyDataSetChanged();
                            }
                        });
                        aVar.b(r4.getString(2131820699), new DialogInterface$OnClickListenerC0254f(0));
                        aVar.a().show();
                        return false;
                    }
                });
                this.f2961U.clear();
                this.f2961U.add("6500");
                this.f2974h0 = new HashSet(new ArrayList(this.f2961U));
                this.f2960T.notifyDataSetChanged();
                this.f2949I.setOnClickListener(new View$OnClickListenerC0270n(this, 0));
                this.f2957Q.setOnClickListener(new View$OnClickListenerC0272o(0, this));
                this.f2958R.setOnClickListener(new View$OnClickListenerC0274p(0, this));
                this.f2966Z.setEnabled(!this.f2956P.isChecked());
                this.f2956P.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: S0.q
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z5) {
                        boolean z6;
                        ActivityInicio activityInicio = ActivityInicio.this;
                        SeekBar seekBar2 = activityInicio.f2966Z;
                        if (!activityInicio.f2956P.isChecked() && !activityInicio.f2968b0.isChecked()) {
                            z6 = true;
                        } else {
                            z6 = false;
                        }
                        seekBar2.setEnabled(z6);
                    }
                });
                this.f2967a0.setOnClickListener(new r(0, this));
            }
            sb = new StringBuilder();
        } else {
            sb = new StringBuilder();
        }
        sb.append(getString(2131820597));
        str = " [0]";
        sb.append(str);
        button.setText(sb.toString());
        this.f2958R = (Button) findViewById(2131230844);
        this.f2967a0 = (Button) findViewById(2131230845);
        this.f2951K = findViewById(2131230937);
        this.f2952L = findViewById(2131230936);
        this.f2953M = findViewById(2131230938);
        this.f2954N = findViewById(2131230939);
        this.f2962V = (Spinner) findViewById(2131231218);
        this.f2963W = (Spinner) findViewById(2131231219);
        ArrayList arrayList52 = new ArrayList();
        this.f2969c0 = arrayList52;
        arrayList52.add(getString(2131820916));
        this.f2969c0.add(getString(2131820914));
        this.f2969c0.add(getString(2131820915));
        ArrayList arrayList62 = new ArrayList();
        this.f2970d0 = arrayList62;
        arrayList62.add(getString(2131820646));
        this.f2970d0.add(getString(2131820644));
        this.f2970d0.add(getString(2131820645));
        ArrayList arrayList72 = new ArrayList();
        this.f2971e0 = arrayList72;
        arrayList72.add("1/4");
        this.f2971e0.add("1/8");
        this.f2971e0.add("1/16");
        this.f2971e0.add("1/32");
        this.f2972f0 = new ArrayList<>();
        this.f2955O = findViewById(2131231241);
        this.f2956P = findViewById(2131231239);
        SwitchCompat findViewById2 = findViewById(2131231240);
        this.f2968b0 = findViewById2;
        this.f2956P.setEnabled(!findViewById2.isChecked());
        SeekBar seekBar2 = this.f2966Z;
        if (this.f2968b0.isChecked()) {
        }
        z4 = false;
        seekBar2.setEnabled(z4);
        this.f2968b0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: A2.a
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z5) {
                boolean z6;
                switch (r2) {
                    case 0:
                        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = this.r;
                        if (onCheckedChangeListener != null) {
                            onCheckedChangeListener.onCheckedChanged(compoundButton, z5);
                            return;
                        }
                        return;
                    default:
                        ActivityInicio activityInicio = (ActivityInicio) this;
                        activityInicio.f2956P.setEnabled(!z5);
                        SeekBar seekBar22 = activityInicio.f2966Z;
                        if (!z5 && !activityInicio.f2956P.isChecked()) {
                            z6 = true;
                        } else {
                            z6 = false;
                        }
                        seekBar22.setEnabled(z6);
                        return;
                }
            }
        });
        v3.h.e(this, "context");
        ?? obj2 = new Object();
        obj2.f = 1;
        obj2.f = 3;
        obj2.f2051a = getString(2131820886);
        obj2.f2053c = C.a.b(this, 2131034146);
        new R0.a(this, obj2).show();
        this.f2959S.setText("Version 5.2.9.2 (build 317)");
        this.f2950J.setOnClickListener(new View.OnClickListener() { // from class: S0.a
            /* JADX WARN: Type inference failed for: r1v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, java.lang.Object] */
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                int i4 = ActivityInicio.f2947A0;
                final ActivityInicio r12 = ActivityInicio.this;
                r12.getClass();
                C0373f.l(10L, r12);
                View inflate = LayoutInflater.from(r12).inflate(2131427462, (ViewGroup) null);
                final EditText editText = (EditText) inflate.findViewById(2131230940);
                InputFilter inputFilter = new InputFilter() { // from class: S0.s
                    @Override // android.text.InputFilter
                    public final CharSequence filter(CharSequence charSequence, int i5, int i6, Spanned spanned, int i7, int i8) {
                        int i9 = ActivityInicio.f2947A0;
                        if (charSequence != null) {
                            for (int i10 = 0; i10 < charSequence.length(); i10++) {
                                EditText editText2 = editText;
                                Editable text = editText2.getText();
                                Objects.requireNonNull(text);
                                if (text.toString().contains(".") && String.valueOf(charSequence.charAt(i10)).equals(".")) {
                                    return charSequence.toString().substring(0, i10);
                                }
                                if (editText2.getText().toString().contains(" ") && String.valueOf(charSequence.charAt(i10)).equals(" ")) {
                                    return charSequence.toString().substring(0, i10);
                                }
                                if (editText2.getText().toString().contains("/") && String.valueOf(charSequence.charAt(i10)).equals("/")) {
                                    return charSequence.toString().substring(0, i10);
                                }
                                if (!"0123456789. /".contains(String.valueOf(charSequence.charAt(i10)))) {
                                    return charSequence.toString().substring(0, i10);
                                }
                            }
                            return charSequence;
                        }
                        return charSequence;
                    }
                };
                if (r12.f2962V.getSelectedItemPosition() == 1) {
                    editText.setFilters(new InputFilter[]{inputFilter});
                    editText.setRawInputType(524289);
                } else {
                    editText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
                    editText.setRawInputType(8194);
                }
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: S0.b
                    @Override // android.view.View.OnFocusChangeListener
                    public final void onFocusChange(View view2, boolean z5) {
                        int i5 = ActivityInicio.f2947A0;
                        ActivityInicio activityInicio = ActivityInicio.this;
                        activityInicio.getClass();
                        EditText editText2 = editText;
                        editText2.post(new D.g(activityInicio, 1, editText2));
                    }
                });
                editText.requestFocus();
                b.a aVar = new b.a((Context) r12);
                String string = r12.getString(2131820648);
                AlertController.b bVar = aVar.a;
                bVar.d = string;
                bVar.q = inflate;
                aVar.b(r12.getString(2131820699), new DialogInterface$OnClickListenerC0248c(0));
                aVar.c(r12.getString(2131820587), new DialogInterface.OnClickListener() { // from class: S0.d
                    /* JADX WARN: Type inference failed for: r3v2, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, java.lang.Object] */
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i5) {
                        int i6 = ActivityInicio.f2947A0;
                        ?? r32 = ActivityInicio.this;
                        r32.getClass();
                        String obj22 = editText.getText().toString();
                        if (!obj22.isEmpty()) {
                            r32.f2961U.add(obj22);
                            r32.f2961U.removeAll(Arrays.asList("", null));
                            r32.f2961U.trimToSize();
                            r32.f2960T.notifyDataSetChanged();
                        } else {
                            Toast.makeText((Context) r32, r32.getString(2131820649), 0).show();
                        }
                        r32.G();
                    }
                });
                aVar.a().show();
            }
        });
        this.f2973g0 = new ArrayList(this.f2970d0);
        ArrayAdapter<String> arrayAdapter4 = new ArrayAdapter<>((Context) this, 17367048, (List<String>) this.f2969c0);
        this.f2983r0 = arrayAdapter4;
        arrayAdapter4.setDropDownViewResource(17367049);
        this.f2962V.setAdapter((SpinnerAdapter) this.f2983r0);
        this.f2962V.setSelection(0);
        ArrayAdapter<String> arrayAdapter22 = new ArrayAdapter<>((Context) this, 17367048, (List<String>) this.f2973g0);
        this.f2982q0 = arrayAdapter22;
        arrayAdapter22.setDropDownViewResource(17367049);
        this.f2963W.setAdapter((SpinnerAdapter) this.f2982q0);
        this.f2962V.setOnTouchListener(new View.OnTouchListener() { // from class: S0.k
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                ActivityInicio.this.f2964X = true;
                return view.performClick();
            }
        });
        this.f2962V.setOnItemSelectedListener(new d());
        this.f2963W.setOnTouchListener(new View.OnTouchListener() { // from class: S0.l
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                ActivityInicio.this.f2965Y = true;
                return view.performClick();
            }
        });
        this.f2963W.setOnItemSelectedListener(new e());
        this.f2948H.setTranscriptMode(1);
        this.f2961U = new ArrayList<>();
        ArrayAdapter arrayAdapter32 = new ArrayAdapter((Context) this, 2131427463, (List) this.f2961U);
        this.f2960T = arrayAdapter32;
        this.f2948H.setAdapter((ListAdapter) arrayAdapter32);
        this.f2948H.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: S0.m
            /* JADX WARN: Type inference failed for: r4v2, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, java.lang.Object] */
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public final boolean onItemLongClick(AdapterView adapterView, View view, final int i4, long j4) {
                int i5 = ActivityInicio.f2947A0;
                final ActivityInicio r4 = ActivityInicio.this;
                r4.getClass();
                C0373f.l(10L, r4);
                b.a aVar = new b.a((Context) r4);
                String string = r4.getString(2131820695);
                AlertController.b bVar = aVar.a;
                bVar.d = string;
                bVar.f = r4.getString(2131820693) + r4.f2961U.get(i4) + r4.getString(2131820694);
                bVar.m = true;
                aVar.c(r4.getString(2131820918), new DialogInterface.OnClickListener() { // from class: S0.e
                    /* JADX WARN: Type inference failed for: r0v0, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio] */
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i6) {
                        int i7 = ActivityInicio.f2947A0;
                        StringBuilder sb2 = new StringBuilder();
                        ?? r02 = ActivityInicio.this;
                        sb2.append(r02.getString(2131820696));
                        ArrayList<String> arrayList8 = r02.f2961U;
                        int i8 = i4;
                        sb2.append(arrayList8.get(i8));
                        sb2.append(r02.getString(2131820697));
                        Toast.makeText((Context) r02, sb2.toString(), 0).show();
                        r02.f2961U.remove(i8);
                        r02.f2960T.notifyDataSetChanged();
                    }
                });
                aVar.b(r4.getString(2131820699), new DialogInterface$OnClickListenerC0254f(0));
                aVar.a().show();
                return false;
            }
        });
        this.f2961U.clear();
        this.f2961U.add("6500");
        this.f2974h0 = new HashSet(new ArrayList(this.f2961U));
        this.f2960T.notifyDataSetChanged();
        this.f2949I.setOnClickListener(new View$OnClickListenerC0270n(this, 0));
        this.f2957Q.setOnClickListener(new View$OnClickListenerC0272o(0, this));
        this.f2958R.setOnClickListener(new View$OnClickListenerC0274p(0, this));
        this.f2966Z.setEnabled(!this.f2956P.isChecked());
        this.f2956P.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: S0.q
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z5) {
                boolean z6;
                ActivityInicio activityInicio = ActivityInicio.this;
                SeekBar seekBar22 = activityInicio.f2966Z;
                if (!activityInicio.f2956P.isChecked() && !activityInicio.f2968b0.isChecked()) {
                    z6 = true;
                } else {
                    z6 = false;
                }
                seekBar22.setEnabled(z6);
            }
        });
        this.f2967a0.setOnClickListener(new r(0, this));
    }

    public final boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(2131558400, menu);
        boolean z4 = false;
        menu.getItem(0).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(4).setVisible(false);
        menu.getItem(5).setVisible(false);
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
    public final boolean onOptionsItemSelected(MenuItem menuItem) {
        StringBuilder sb;
        Intent intent = new Intent((Context) this, (Class<?>) ActivityRetales.class);
        int itemId = menuItem.getItemId();
        if (itemId == 2131231075) {
            if (!this.f2986u0.f2897x) {
                F1.a aVar = this.f2979n0;
                if (aVar != null) {
                    aVar.c(new g(intent));
                }
                F1.a aVar2 = this.f2979n0;
                if (aVar2 != null) {
                    aVar2.e(this);
                } else {
                    F();
                    startActivity(intent);
                    overridePendingTransition(2130771998, 2130771999);
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                E(this.f2980o0);
            } else {
                F();
                startActivity(intent);
                overridePendingTransition(2130771998, 2130771999);
            }
        } else if (itemId == 2131231066) {
            long currentTimeMillis = System.currentTimeMillis();
            if (!this.f2986u0.f2897x) {
                if (currentTimeMillis % 2 != 0) {
                    F1.a aVar3 = this.f2979n0;
                    if (aVar3 != null) {
                        aVar3.c(new h());
                    }
                    F1.a aVar4 = this.f2979n0;
                    if (aVar4 != null) {
                        aVar4.e(this);
                    } else {
                        J();
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                    E(this.f2980o0);
                } else {
                    J();
                }
            } else {
                J();
            }
        } else if (itemId == 2131231074) {
            if (this.f2962V.getSelectedItemPosition() != 2) {
                int selectedItemPosition = this.f2963W.getSelectedItemPosition();
                if (selectedItemPosition != 0) {
                    if (selectedItemPosition != 1) {
                        if (selectedItemPosition != 2) {
                            if (selectedItemPosition == 3) {
                                if (this.f2962V.getSelectedItemPosition() == 0) {
                                    H();
                                    this.f2961U.clear();
                                    this.f2961U.add("6500");
                                    this.f2960T.notifyDataSetChanged();
                                    this.f2951K.setText("10");
                                    this.f2952L.setText("10");
                                    this.f2953M.setText("4");
                                } else {
                                    this.f2961U.clear();
                                    I();
                                    Log.d("INI_BILLING_V5_COPY", "resetValues: setTecladoTexto()");
                                    this.f2961U.add(C0373f.j(6500.0d, Long.parseLong(this.f2982q0.getItem(3).split("/")[1])));
                                    this.f2960T.notifyDataSetChanged();
                                    this.f2951K.setText("");
                                    this.f2952L.setText("");
                                    this.f2953M.setText("");
                                    F3.e.c(this.f2982q0.getItem(3).split("/")[1], 10.0d, this.f2951K);
                                    F3.e.c(this.f2982q0.getItem(3).split("/")[1], 10.0d, this.f2952L);
                                    F3.e.c(this.f2982q0.getItem(3).split("/")[1], 4.0d, this.f2953M);
                                }
                            }
                        } else if (this.f2962V.getSelectedItemPosition() == 0) {
                            H();
                            this.f2961U.clear();
                            this.f2961U.add(C0373f.g(6.5d));
                            this.f2960T.notifyDataSetChanged();
                            this.f2951K.setText("");
                            this.f2952L.setText("");
                            this.f2953M.setText("");
                            this.f2951K.setText(C0373f.g(0.01d));
                            this.f2952L.setText(C0373f.g(0.01d));
                            this.f2953M.setText(C0373f.g(0.004d));
                        } else {
                            I();
                            Log.d("INI_BILLING_V5_COPY", "resetValues: setTecladoTexto()");
                            this.f2961U.clear();
                            this.f2961U.add(C0373f.j(6500.0d, Long.parseLong(this.f2982q0.getItem(2).split("/")[1])));
                            this.f2960T.notifyDataSetChanged();
                            this.f2951K.setText("");
                            this.f2952L.setText("");
                            this.f2953M.setText("");
                            F3.e.c(this.f2982q0.getItem(2).split("/")[1], 10.0d, this.f2951K);
                            F3.e.c(this.f2982q0.getItem(2).split("/")[1], 10.0d, this.f2952L);
                            F3.e.c(this.f2982q0.getItem(2).split("/")[1], 4.0d, this.f2953M);
                        }
                    } else if (this.f2962V.getSelectedItemPosition() == 0) {
                        H();
                        this.f2961U.clear();
                        this.f2961U.add(C0373f.g(650.0d));
                        this.f2960T.notifyDataSetChanged();
                        this.f2951K.setText("");
                        this.f2952L.setText("");
                        this.f2953M.setText("");
                        this.f2951K.setText(C0373f.g(1.0d));
                        this.f2952L.setText(C0373f.g(1.0d));
                        this.f2953M.setText(C0373f.g(0.4d));
                    } else {
                        I();
                        Log.d("INI_BILLING_V5_COPY", "resetValues: setTecladoTexto()");
                        this.f2961U.clear();
                        this.f2961U.add(C0373f.j(6500.0d, Long.parseLong(this.f2982q0.getItem(1).split("/")[1])));
                        this.f2960T.notifyDataSetChanged();
                        this.f2951K.setText("");
                        this.f2952L.setText("");
                        this.f2953M.setText("");
                        F3.e.c(this.f2982q0.getItem(1).split("/")[1], 10.0d, this.f2951K);
                        F3.e.c(this.f2982q0.getItem(1).split("/")[1], 10.0d, this.f2952L);
                        F3.e.c(this.f2982q0.getItem(1).split("/")[1], 4.0d, this.f2953M);
                    }
                } else if (this.f2962V.getSelectedItemPosition() == 0) {
                    H();
                    this.f2961U.clear();
                    this.f2961U.add("6500");
                    this.f2960T.notifyDataSetChanged();
                    this.f2951K.setText("10");
                    this.f2952L.setText("10");
                    this.f2953M.setText("4");
                } else {
                    this.f2961U.clear();
                    I();
                    Log.d("INI_BILLING_V5_COPY", "resetValues: setTecladoTexto()");
                    this.f2961U.add(C0373f.j(6500.0d, Long.parseLong(this.f2982q0.getItem(0).split("/")[1])));
                    this.f2960T.notifyDataSetChanged();
                    this.f2951K.setText("");
                    this.f2952L.setText("");
                    this.f2953M.setText("");
                    F3.e.c(this.f2982q0.getItem(0).split("/")[1], 10.0d, this.f2951K);
                    F3.e.c(this.f2982q0.getItem(0).split("/")[1], 10.0d, this.f2952L);
                    F3.e.c(this.f2982q0.getItem(0).split("/")[1], 4.0d, this.f2953M);
                }
            } else {
                this.f2963W.setEnabled(false);
                H();
                this.f2961U.clear();
                this.f2961U.add("256");
                this.f2960T.notifyDataSetChanged();
                this.f2951K.setText("0.79");
                this.f2952L.setText("0.79");
                this.f2953M.setText("0.16");
                this.f2963W.setEnabled(false);
                this.f2986u0.f2890q = false;
            }
            this.f2955O.setChecked(true);
            this.f2954N.setText("90");
            this.f2966Z.setProgress(2);
            C0353a c0353a = new C0353a();
            this.f2986u0 = c0353a;
            Button button = this.f2957Q;
            ArrayList<String> arrayList = c0353a.f2884k;
            String str = " [0]";
            if (arrayList != null) {
                if (!arrayList.isEmpty()) {
                    sb = new StringBuilder();
                    sb.append(getString(2131820597));
                    str = " [1+]";
                    sb.append(str);
                    button.setText(sb.toString());
                    F();
                    G();
                } else {
                    sb = new StringBuilder();
                }
            } else {
                sb = new StringBuilder();
            }
            sb.append(getString(2131820597));
            sb.append(str);
            button.setText(sb.toString());
            F();
            G();
        } else if (itemId == 2131231071) {
            new V0.c(this, this.l0, getString(2131820579), this.f2980o0).f();
        } else if (itemId == 2131231072) {
            F();
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://sites.google.com/view/soldier-developer/cutter-cutting-optimizer/privacy-policy")));
        } else if (itemId == 2131231070) {
            F();
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/account/subscriptions?sku=remove_ads&package=com.embarcadero.OptimizaCorte")));
        } else if (itemId == 2131231073) {
            C0379b.b(this, this.f2986u0);
        }
        return super/*android.app.Activity*/.onOptionsItemSelected(menuItem);
    }

    public final void onPause() {
        Timer timer = this.f2989x0;
        if (timer != null) {
            timer.cancel();
            Log.d("INTERSTITIAL_TIMER", "onPause: interstitial timer paused");
        }
        StringBuilder sb = new StringBuilder();
        this.f2961U.removeAll(Arrays.asList("", null));
        this.f2961U.trimToSize();
        if (!this.f2961U.isEmpty()) {
            for (int i4 = 0; i4 < this.f2961U.size(); i4++) {
                sb.append(this.f2961U.get(i4));
                sb.append("%");
            }
            sb.deleteCharAt(sb.lastIndexOf("%"));
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.f2962V.getSelectedItemPosition());
        sb2.append("@");
        sb2.append(this.f2963W.getSelectedItemPosition());
        sb2.append("@");
        sb2.append((Object) sb);
        sb2.append("@");
        Editable text = this.f2951K.getText();
        Objects.requireNonNull(text);
        sb2.append((Object) text);
        sb2.append("@");
        Editable text2 = this.f2952L.getText();
        Objects.requireNonNull(text2);
        sb2.append((Object) text2);
        sb2.append("@");
        Editable text3 = this.f2953M.getText();
        Objects.requireNonNull(text3);
        sb2.append((Object) text3);
        sb2.append("@");
        sb2.append(this.f2955O.isChecked());
        sb2.append("@");
        Editable text4 = this.f2954N.getText();
        Objects.requireNonNull(text4);
        sb2.append((Object) text4);
        sb2.append("@");
        sb2.append(this.f2966Z.getProgress());
        sb2.append("@");
        sb2.append(this.f2956P.isChecked());
        this.f2986u0.f2892s = sb2.toString();
        G();
        F();
        this.f2985t0 = true;
        super.onPause();
    }

    /* JADX WARN: Can't wrap try/catch for region: R(17:1|(1:(1:(1:5)(1:47))(1:48))(1:49)|6|(2:8|(13:10|11|12|(1:14)|15|(1:19)|20|21|(5:23|(1:(1:(1:27)(1:34))(1:35))(1:36)|28|(1:30)(1:33)|31)|37|(1:39)|40|41)(1:44))(1:46)|45|11|12|(0)|15|(2:17|19)|20|21|(0)|37|(0)|40|41) */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:21:0x019c  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x01bd A[Catch: Exception -> 0x01e0, TryCatch #0 {Exception -> 0x01e0, blocks: (B:27:0x01ae, B:29:0x01bd, B:33:0x01d4, B:38:0x023a, B:40:0x0242, B:42:0x025f, B:41:0x0255, B:35:0x01e3, B:36:0x01ec, B:37:0x0216), top: B:48:0x01ae }] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x02e0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void onResume() {
        /*
            Method dump skipped, instructions count: 772
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.embarcadero.OptimizaCorte.Activities.ActivityInicio.onResume():void");
    }
}
