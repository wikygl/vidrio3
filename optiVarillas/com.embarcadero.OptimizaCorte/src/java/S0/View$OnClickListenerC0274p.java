package S0;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import b1.C0353a;
import b1.C0354b;
import c1.C0373f;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityRetales;
import e1.C0407a;
import j$.util.Objects;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: S0.p  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class View$OnClickListenerC0274p implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2268j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2269k;

    public /* synthetic */ View$OnClickListenerC0274p(int i4, Object obj) {
        this.f2268j = i4;
        this.f2269k = obj;
    }

    private final void a(View view) {
        X0.b bVar = (X0.b) this.f2269k;
        bVar.getClass();
        C0373f.l(10L, view.getContext());
        Editable text = bVar.f2807e0.getText();
        Objects.requireNonNull(text);
        String trim = text.toString().trim();
        Editable text2 = bVar.f2808f0.getText();
        Objects.requireNonNull(text2);
        String trim2 = text2.toString().trim();
        Editable text3 = bVar.f2809g0.getText();
        Objects.requireNonNull(text3);
        String trim3 = text3.toString().trim();
        Log.d("TAG", "onCreate: uds = " + trim2);
        if (trim2.isEmpty()) {
            trim2 = "1";
        }
        if (!bVar.f2807e0.getText().toString().trim().isEmpty()) {
            String trim4 = bVar.f2807e0.getText().toString().trim();
            if (!trim.equals("0") && !trim2.equals("0")) {
                try {
                    if (!bVar.f2815n0.f2888o.equals(bVar.l(2131820882)) || bVar.f2815n0.f2892s.split("@")[0].equals("2")) {
                        trim4 = C0373f.g(Double.parseDouble(bVar.f2807e0.getText().toString().trim()));
                    }
                } catch (NumberFormatException unused) {
                    Toast.makeText(bVar.f2805c0, bVar.l(2131820701), 1).show();
                    return;
                }
            } else {
                Toast.makeText(bVar.f2805c0, bVar.l(2131820701), 1).show();
            }
            if (bVar.f2815n0.f2888o.equals(bVar.l(2131820882)) && !bVar.f2815n0.f2892s.split("@")[0].equals("2")) {
                try {
                    trim4 = C0373f.j(C0373f.h(trim4), bVar.f2813k0);
                    if (trim4.equals("0")) {
                        Toast.makeText(bVar.f2805c0, bVar.l(2131820701), 1).show();
                        return;
                    }
                } catch (Exception unused2) {
                    Toast.makeText(bVar.f2805c0, bVar.l(2131820701), 1).show();
                    return;
                }
            }
            try {
                if (bVar.f2809g0.getText().toString().trim().isEmpty()) {
                    bVar.l0.add(0, new C0354b(trim4, trim2 + " " + bVar.l(2131820739), bVar.f2815n0));
                    bVar.f2814m0.notifyDataSetChanged();
                    bVar.f2810h0.smoothScrollToPosition(0);
                } else {
                    bVar.l0.add(0, new C0354b(trim4 + "[" + trim3 + "]", trim2 + " " + bVar.l(2131820739), bVar.f2815n0));
                    bVar.f2814m0.notifyDataSetChanged();
                    bVar.f2810h0.smoothScrollToPosition(0);
                }
                bVar.f2807e0.setText("");
                bVar.f2808f0.setText("");
                bVar.f2809g0.setText("");
                bVar.f2807e0.requestFocus();
            } catch (NumberFormatException unused3) {
                Toast.makeText(bVar.f2805c0, bVar.l(2131820701), 1).show();
                return;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (bVar.l0.size() > 0) {
            for (int i4 = 0; i4 < bVar.l0.size(); i4++) {
                sb.append(bVar.l0.get(i4).f2899b);
                sb.append("@");
                sb.append(bVar.l0.get(i4).f2900c);
                sb.append("@");
            }
            StringBuilder sb2 = new StringBuilder(sb.substring(0, sb.lastIndexOf("@")));
            ArrayList<String> arrayList = new ArrayList<>();
            for (String str : sb2.toString().split("@")) {
                arrayList.add(str);
            }
            arrayList.trimToSize();
            bVar.f2815n0.f2883j = arrayList;
        }
        synchronized (bVar) {
            try {
                if (bVar.f2815n0 != null) {
                    if (bVar.f2816o0 == null) {
                        bVar.f2816o0 = new C0407a(bVar.I().getApplicationContext());
                    }
                    synchronized (bVar) {
                        bVar.f2816o0.b(bVar.f2815n0);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, java.lang.Object, android.app.Activity] */
    /* JADX WARN: Type inference failed for: r5v2, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityRetales] */
    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        boolean z4;
        double c4;
        double c5;
        double c6;
        C0354b c0354b;
        Object obj = this.f2269k;
        switch (this.f2268j) {
            case 0:
                int i4 = ActivityInicio.f2947A0;
                ?? r5 = (ActivityInicio) obj;
                r5.getClass();
                ConnectivityManager connectivityManager = (ConnectivityManager) r5.getSystemService("connectivity");
                if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                if (z4) {
                    if (U0.a.a(r5) && !r5.f2986u0.f2897x) {
                        U0.a.b(r5, view, r5.getString(2131820573), r5.getString(2131820572), r5.getString(2131820640)).show();
                        return;
                    }
                    r5.f2972f0.clear();
                    r5.f2972f0.trimToSize();
                    r5.f2961U.removeAll(Arrays.asList("", null));
                    r5.f2961U.trimToSize();
                    if (!r5.f2961U.isEmpty()) {
                        for (int i5 = 0; i5 < r5.f2961U.size(); i5++) {
                            try {
                                if (r5.f2962V.getSelectedItemPosition() == 1) {
                                    r5.f2972f0.add(Double.valueOf(C0373f.h(r5.f2961U.get(i5))));
                                } else {
                                    r5.f2972f0.add(Double.valueOf(Double.parseDouble(r5.f2961U.get(i5))));
                                }
                            } catch (Exception e4) {
                                Toast.makeText((Context) r5, e4.getMessage(), 0).show();
                            }
                        }
                        r5.f2972f0.trimToSize();
                    }
                    if (!r5.f2972f0.isEmpty()) {
                        Editable text = r5.f2951K.getText();
                        Objects.requireNonNull(text);
                        if (!text.toString().isEmpty()) {
                            Editable text2 = r5.f2952L.getText();
                            Objects.requireNonNull(text2);
                            if (!text2.toString().isEmpty()) {
                                Editable text3 = r5.f2953M.getText();
                                Objects.requireNonNull(text3);
                                if (!text3.toString().isEmpty()) {
                                    Editable text4 = r5.f2954N.getText();
                                    Objects.requireNonNull(text4);
                                    if (!text4.toString().isEmpty()) {
                                        StringBuilder sb = new StringBuilder();
                                        if (!r5.f2961U.isEmpty()) {
                                            for (int i6 = 0; i6 < r5.f2961U.size(); i6++) {
                                                sb.append(r5.f2961U.get(i6));
                                                sb.append("%");
                                            }
                                            sb.deleteCharAt(sb.lastIndexOf("%"));
                                        } else {
                                            sb.append("6500");
                                        }
                                        try {
                                            double parseDouble = Double.parseDouble(r5.f2954N.getText().toString());
                                            r5.f2984s0 = parseDouble;
                                            if (parseDouble < 1.0d || parseDouble > 90.0d) {
                                                r5.f2954N.setText("90");
                                                r5.f2984s0 = 90.0d;
                                            }
                                        } catch (Exception unused) {
                                            r5.f2954N.setText("90");
                                            r5.f2984s0 = 90.0d;
                                        }
                                        C0353a c0353a = r5.f2986u0;
                                        c0353a.f2892s = r5.f2962V.getSelectedItemPosition() + "@" + r5.f2963W.getSelectedItemPosition() + "@" + ((Object) sb) + "@" + r5.f2951K.getText().toString() + "@" + r5.f2952L.getText().toString() + "@" + r5.f2953M.getText().toString() + "@" + r5.f2955O.isChecked() + "@" + r5.f2954N.getText().toString() + "@" + r5.f2966Z.getProgress() + "@" + r5.f2956P.isChecked();
                                        try {
                                            c0353a.f2894u = r5.f2972f0;
                                            c0353a.f2881h = r5.f2966Z.getProgress() + 2;
                                            r5.f2986u0.f2895v = r5.f2956P.isChecked();
                                            if (r5.f2962V.getSelectedItemPosition() == 0) {
                                                r5.f2986u0.f2877c = Double.parseDouble(r5.f2951K.getText().toString()) + Double.parseDouble(r5.f2952L.getText().toString());
                                                C0353a c0353a2 = r5.f2986u0;
                                                if (r5.f2955O.isChecked()) {
                                                    c6 = C0373f.c(Double.parseDouble(r5.f2953M.getText().toString()), r5.f2984s0) * 2.0d;
                                                } else {
                                                    c6 = C0373f.c(Double.parseDouble(r5.f2953M.getText().toString()), r5.f2984s0);
                                                }
                                                c0353a2.f2878d = c6;
                                                r5.f2986u0.f2888o = r5.f2982q0.getItem(r5.f2963W.getSelectedItemPosition());
                                                r5.f2986u0.f2890q = false;
                                            }
                                            if (r5.f2962V.getSelectedItemPosition() == 1) {
                                                r5.f2986u0.f2877c = C0373f.h(r5.f2951K.getText().toString()) + C0373f.h(r5.f2952L.getText().toString());
                                                C0353a c0353a3 = r5.f2986u0;
                                                if (r5.f2955O.isChecked()) {
                                                    c5 = C0373f.c(C0373f.h(r5.f2953M.getText().toString()), r5.f2984s0) * 2.0d;
                                                } else {
                                                    c5 = C0373f.c(C0373f.h(r5.f2953M.getText().toString()), r5.f2984s0);
                                                }
                                                c0353a3.f2878d = c5;
                                                r5.f2986u0.f2888o = r5.getString(2131820882);
                                                r5.f2986u0.f2889p = Long.parseLong(r5.f2982q0.getItem(r5.f2963W.getSelectedItemPosition()).split("/")[1]);
                                                r5.f2986u0.f2890q = true;
                                            }
                                            if (r5.f2962V.getSelectedItemPosition() == 2) {
                                                r5.f2986u0.f2877c = Double.parseDouble(r5.f2951K.getText().toString()) + Double.parseDouble(r5.f2952L.getText().toString());
                                                C0353a c0353a4 = r5.f2986u0;
                                                if (r5.f2955O.isChecked()) {
                                                    c4 = C0373f.c(Double.parseDouble(r5.f2953M.getText().toString()), r5.f2984s0) * 2.0d;
                                                } else {
                                                    c4 = C0373f.c(Double.parseDouble(r5.f2953M.getText().toString()), r5.f2984s0);
                                                }
                                                c0353a4.f2878d = c4;
                                                r5.f2986u0.f2888o = r5.getString(2131820882);
                                                r5.f2986u0.f2890q = false;
                                            }
                                            C0373f.l(10L, r5);
                                            long currentTimeMillis = System.currentTimeMillis();
                                            if (!r5.f2986u0.f2897x) {
                                                if (currentTimeMillis % 2 != 0) {
                                                    F1.a aVar = r5.f2979n0;
                                                    if (aVar != null) {
                                                        aVar.c(new ActivityInicio.f());
                                                    }
                                                    F1.a aVar2 = r5.f2979n0;
                                                    if (aVar2 != 0) {
                                                        aVar2.e(r5);
                                                    } else {
                                                        r5.F();
                                                        r5.startActivity(r5.f2975i0);
                                                        r5.overridePendingTransition(2130771998, 2130771999);
                                                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                                    }
                                                    r5.E(r5.f2980o0);
                                                } else {
                                                    r5.F();
                                                    r5.startActivity(r5.f2975i0);
                                                    r5.overridePendingTransition(2130771998, 2130771999);
                                                }
                                            } else {
                                                r5.F();
                                                r5.startActivity(r5.f2975i0);
                                                r5.overridePendingTransition(2130771998, 2130771999);
                                            }
                                        } catch (Exception unused2) {
                                            r5.C();
                                        }
                                        r5.F();
                                        r5.G();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    Toast.makeText((Context) r5, r5.getString(2131820715), 1).show();
                    return;
                }
                U0.a.b(r5, view, r5.getString(2131820638), r5.getString(2131820639), r5.getString(2131820640)).show();
                return;
            case 1:
                int i7 = ActivityRetales.f3095b0;
                ?? r52 = (ActivityRetales) obj;
                C0373f.l(10L, r52.getApplicationContext());
                ArrayList<Double> arrayList = r52.f3108T;
                arrayList.clear();
                Editable text5 = r52.f3101M.getText();
                Objects.requireNonNull(text5);
                if (text5.toString().isEmpty()) {
                    r52.f3101M.setText("1");
                }
                Editable text6 = r52.f3100L.getText();
                Objects.requireNonNull(text6);
                if (!text6.toString().trim().isEmpty()) {
                    Editable text7 = r52.f3101M.getText();
                    Objects.requireNonNull(text7);
                    if (!text7.toString().trim().isEmpty()) {
                        if (!r52.f3100L.getText().toString().trim().equals("0") && !r52.f3101M.getText().toString().trim().equals("0")) {
                            try {
                                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
                                decimalFormatSymbols.setDecimalSeparator('.');
                                DecimalFormat decimalFormat = new DecimalFormat("#.#####");
                                decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
                                if (r52.f3109U) {
                                    c0354b = new C0354b(C0373f.j(C0373f.h(r52.f3100L.getText().toString().trim()), r52.f3112X.f2889p), r52.f3101M.getText().toString() + " " + r52.getString(2131820739), r52.f3112X);
                                } else {
                                    c0354b = new C0354b(decimalFormat.format(Double.valueOf(r52.f3100L.getText().toString().trim())), r52.f3101M.getText().toString() + " " + r52.getString(2131820739), r52.f3112X);
                                }
                                r52.f3103O.add(c0354b);
                                r52.f3100L.setText("");
                                r52.f3101M.setText("");
                                r52.f3100L.requestFocus();
                            } catch (NumberFormatException e5) {
                                Toast.makeText((Context) r52, r52.getString(2131820701), 0).show();
                                e5.printStackTrace();
                            }
                        } else {
                            Toast.makeText((Context) r52, r52.getString(2131820701), 1).show();
                        }
                    }
                }
                int i8 = 0;
                while (true) {
                    ArrayList<C0354b> arrayList2 = r52.f3105Q;
                    if (i8 < arrayList2.size()) {
                        C0354b c0354b2 = arrayList2.get(i8);
                        r52.f3106R = c0354b2;
                        try {
                            r52.f3107S = Integer.parseInt(c0354b2.f2900c.trim().split(" ")[0]);
                        } catch (Exception e6) {
                            r52.f3107S = 0;
                            e6.printStackTrace();
                        }
                        for (int i9 = 1; i9 <= r52.f3107S; i9++) {
                            try {
                                if (r52.f3109U) {
                                    arrayList.add(Double.valueOf(C0373f.h(r52.f3106R.f2899b.trim())));
                                } else {
                                    arrayList.add(Double.valueOf(r52.f3106R.f2899b.trim()));
                                }
                            } catch (Exception e7) {
                                e7.printStackTrace();
                            }
                        }
                        i8++;
                    } else {
                        arrayList.trimToSize();
                        StringBuilder sb2 = new StringBuilder();
                        if (arrayList2.size() > 0) {
                            for (int i10 = 0; i10 < arrayList2.size(); i10++) {
                                sb2.append(arrayList2.get(i10).f2899b);
                                sb2.append("@");
                                sb2.append(arrayList2.get(i10).f2900c);
                                sb2.append("@");
                            }
                            StringBuilder sb3 = new StringBuilder(sb2.substring(0, sb2.lastIndexOf("@")));
                            r52.f3112X.f2893t = sb3.toString();
                            ArrayList<String> arrayList3 = new ArrayList<>();
                            for (String str : sb3.toString().split("@")) {
                                arrayList3.add(str);
                            }
                            arrayList3.trimToSize();
                            r52.f3112X.f2884k = arrayList3;
                            return;
                        }
                        return;
                    }
                }
                break;
            case 2:
                ((U2.m) obj).u();
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                a(view);
                return;
            default:
                ((androidx.appcompat.app.b) obj).dismiss();
                return;
        }
    }
}
