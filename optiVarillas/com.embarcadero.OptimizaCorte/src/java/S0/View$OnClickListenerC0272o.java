package S0;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.b;
import b1.C0353a;
import c1.C0373f;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import j$.util.Objects;
import java.util.Arrays;

/* renamed from: S0.o  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class View$OnClickListenerC0272o implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2264j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2265k;

    public /* synthetic */ View$OnClickListenerC0272o(int i4, Object obj) {
        this.f2264j = i4;
        this.f2265k = obj;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, android.app.Activity] */
    /* JADX WARN: Type inference failed for: r1v2, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion] */
    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        double c4;
        double c5;
        double c6;
        Object obj = this.f2265k;
        switch (this.f2264j) {
            case 0:
                int i4 = ActivityInicio.f2947A0;
                ?? r12 = (ActivityInicio) obj;
                r12.F();
                r12.G();
                C0373f.l(10L, r12);
                r12.f2972f0.clear();
                r12.f2972f0.trimToSize();
                r12.f2961U.removeAll(Arrays.asList("", null));
                r12.f2961U.trimToSize();
                if (!r12.f2961U.isEmpty()) {
                    for (int i5 = 0; i5 < r12.f2961U.size(); i5++) {
                        try {
                            if (r12.f2962V.getSelectedItemPosition() == 1) {
                                r12.f2972f0.add(Double.valueOf(C0373f.h(r12.f2961U.get(i5))));
                            } else {
                                r12.f2972f0.add(Double.valueOf(Double.parseDouble(r12.f2961U.get(i5))));
                            }
                        } catch (Exception e4) {
                            Toast.makeText((Context) r12, e4.getMessage(), 0).show();
                        }
                    }
                    r12.f2972f0.trimToSize();
                }
                if (!r12.f2972f0.isEmpty()) {
                    Editable text = r12.f2951K.getText();
                    Objects.requireNonNull(text);
                    if (!text.toString().isEmpty()) {
                        Editable text2 = r12.f2952L.getText();
                        Objects.requireNonNull(text2);
                        if (!text2.toString().isEmpty()) {
                            Editable text3 = r12.f2953M.getText();
                            Objects.requireNonNull(text3);
                            if (!text3.toString().isEmpty()) {
                                Editable text4 = r12.f2954N.getText();
                                Objects.requireNonNull(text4);
                                if (!text4.toString().isEmpty()) {
                                    StringBuilder sb = new StringBuilder();
                                    if (!r12.f2961U.isEmpty()) {
                                        for (int i6 = 0; i6 < r12.f2961U.size(); i6++) {
                                            sb.append(r12.f2961U.get(i6));
                                            sb.append("%");
                                        }
                                        sb.deleteCharAt(sb.lastIndexOf("%"));
                                    } else {
                                        sb.append("6500");
                                    }
                                    try {
                                        double parseDouble = Double.parseDouble(r12.f2954N.getText().toString());
                                        r12.f2984s0 = parseDouble;
                                        if (parseDouble < 1.0d || parseDouble > 90.0d) {
                                            r12.f2954N.setText("90");
                                            r12.f2984s0 = 90.0d;
                                        }
                                    } catch (Exception unused) {
                                        r12.f2954N.setText("90");
                                        r12.f2984s0 = 90.0d;
                                    }
                                    r12.f2986u0.f2892s = r12.f2962V.getSelectedItemPosition() + "@" + r12.f2963W.getSelectedItemPosition() + "@" + ((Object) sb) + "@" + r12.f2951K.getText().toString() + "@" + r12.f2952L.getText().toString() + "@" + r12.f2953M.getText().toString() + "@" + r12.f2955O.isChecked() + "@" + r12.f2954N.getText().toString() + "@" + r12.f2966Z.getProgress() + "@" + r12.f2956P.isChecked();
                                    r12.f2972f0.clear();
                                    r12.f2972f0.trimToSize();
                                    if (!r12.f2961U.isEmpty()) {
                                        for (int i7 = 0; i7 < r12.f2961U.size(); i7++) {
                                            try {
                                                if (r12.f2962V.getSelectedItemPosition() == 1) {
                                                    r12.f2972f0.add(Double.valueOf(C0373f.h(r12.f2961U.get(i7))));
                                                } else {
                                                    r12.f2972f0.add(Double.valueOf(Double.parseDouble(r12.f2961U.get(i7))));
                                                }
                                            } catch (Exception e5) {
                                                Toast.makeText((Context) r12, e5.getMessage(), 0).show();
                                            }
                                        }
                                        r12.f2972f0.trimToSize();
                                    }
                                    try {
                                        C0353a c0353a = r12.f2986u0;
                                        c0353a.f2894u = r12.f2972f0;
                                        c0353a.f2881h = r12.f2966Z.getProgress() + 2;
                                        r12.f2986u0.f2895v = r12.f2956P.isChecked();
                                        if (r12.f2962V.getSelectedItemPosition() == 0) {
                                            r12.f2986u0.f2877c = Double.parseDouble(r12.f2951K.getText().toString()) + Double.parseDouble(r12.f2952L.getText().toString());
                                            C0353a c0353a2 = r12.f2986u0;
                                            if (r12.f2955O.isChecked()) {
                                                c6 = C0373f.c(Double.parseDouble(r12.f2953M.getText().toString()), r12.f2984s0) * 2.0d;
                                            } else {
                                                c6 = C0373f.c(Double.parseDouble(r12.f2953M.getText().toString()), r12.f2984s0);
                                            }
                                            c0353a2.f2878d = c6;
                                            r12.f2986u0.f2888o = r12.f2982q0.getItem(r12.f2963W.getSelectedItemPosition());
                                            r12.f2986u0.f2890q = false;
                                        }
                                        if (r12.f2962V.getSelectedItemPosition() == 1) {
                                            r12.f2986u0.f2877c = C0373f.h(r12.f2951K.getText().toString()) + C0373f.h(r12.f2952L.getText().toString());
                                            C0353a c0353a3 = r12.f2986u0;
                                            if (r12.f2955O.isChecked()) {
                                                c5 = C0373f.c(C0373f.h(r12.f2953M.getText().toString()), r12.f2984s0) * 2.0d;
                                            } else {
                                                c5 = C0373f.c(C0373f.h(r12.f2953M.getText().toString()), r12.f2984s0);
                                            }
                                            c0353a3.f2878d = c5;
                                            r12.f2986u0.f2888o = r12.getString(2131820882);
                                            r12.f2986u0.f2889p = Long.parseLong(r12.f2982q0.getItem(r12.f2963W.getSelectedItemPosition()).split("/")[1]);
                                            r12.f2986u0.f2890q = true;
                                        }
                                        if (r12.f2962V.getSelectedItemPosition() == 2) {
                                            r12.f2986u0.f2877c = Double.parseDouble(r12.f2951K.getText().toString()) + Double.parseDouble(r12.f2952L.getText().toString());
                                            C0353a c0353a4 = r12.f2986u0;
                                            if (r12.f2955O.isChecked()) {
                                                c4 = C0373f.c(Double.parseDouble(r12.f2953M.getText().toString()), r12.f2984s0) * 2.0d;
                                            } else {
                                                c4 = C0373f.c(Double.parseDouble(r12.f2953M.getText().toString()), r12.f2984s0);
                                            }
                                            c0353a4.f2878d = c4;
                                            r12.f2986u0.f2888o = r12.getString(2131820882);
                                            r12.f2986u0.f2890q = false;
                                        }
                                        long currentTimeMillis = System.currentTimeMillis();
                                        if (!r12.f2986u0.f2897x) {
                                            if (currentTimeMillis % 2 != 0) {
                                                F1.a aVar = r12.f2979n0;
                                                if (aVar != null) {
                                                    aVar.c(new C(r12));
                                                }
                                                F1.a aVar2 = r12.f2979n0;
                                                if (aVar2 != 0) {
                                                    aVar2.e(r12);
                                                } else {
                                                    r12.F();
                                                    r12.startActivity(r12.f2976j0);
                                                    r12.overridePendingTransition(2130771998, 2130771999);
                                                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                                }
                                                r12.E(r12.f2980o0);
                                                return;
                                            }
                                            r12.F();
                                            r12.startActivity(r12.f2976j0);
                                            r12.overridePendingTransition(2130771998, 2130771999);
                                            return;
                                        }
                                        r12.F();
                                        r12.startActivity(r12.f2976j0);
                                        r12.overridePendingTransition(2130771998, 2130771999);
                                        return;
                                    } catch (Exception unused2) {
                                        r12.C();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                Toast.makeText((Context) r12, r12.getString(2131820715), 1).show();
                return;
            case 1:
                final ?? r13 = (ActivityOptimizacion) obj;
                b1.d dVar = r13.f3076V.f2882i;
                if (dVar != null) {
                    View inflate = LayoutInflater.from(r13).inflate(2131427382, (ViewGroup) null);
                    TextView textView = (TextView) inflate.findViewById(2131231309);
                    final TextView textView2 = (TextView) inflate.findViewById(2131231306);
                    final TextView textView3 = (TextView) inflate.findViewById(2131231307);
                    final TextView textView4 = (TextView) inflate.findViewById(2131231308);
                    ListView listView = (ListView) inflate.findViewById(2131231036);
                    ListView listView2 = (ListView) inflate.findViewById(2131231037);
                    ListView listView3 = (ListView) inflate.findViewById(2131231038);
                    try {
                        textView.setText(r13.getString(2131820657) + " (" + r13.f3076V.f2888o + ")");
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(r13.getString(2131820658));
                        sb2.append(": ");
                        sb2.append(dVar.f2907a);
                        textView2.setText(sb2.toString());
                        textView3.setText(r13.getString(2131820652) + ": " + dVar.f2908b);
                        textView4.setText(r13.getString(2131820653) + ": " + dVar.f2909c);
                        T0.a aVar3 = new T0.a(r13, dVar.f2910d);
                        T0.a aVar4 = new T0.a(r13, dVar.f2911e);
                        T0.a aVar5 = new T0.a(r13, dVar.f);
                        aVar3.notifyDataSetChanged();
                        aVar4.notifyDataSetChanged();
                        aVar5.notifyDataSetChanged();
                        listView.setAdapter((ListAdapter) aVar3);
                        listView2.setAdapter((ListAdapter) aVar4);
                        listView3.setAdapter((ListAdapter) aVar5);
                        b.a aVar6 = new b.a((Context) r13);
                        aVar6.a.q = inflate;
                        aVar6.b(r13.getString(2131820650), new DialogInterface$OnClickListenerC0267l0(2));
                        aVar6.c(r13.getString(2131820651), new DialogInterface.OnClickListener() { // from class: S0.I0
                            /* JADX WARN: Multi-variable type inference failed */
                            /* JADX WARN: Type inference failed for: r9v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion, android.app.Activity] */
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i8) {
                                int i9 = ActivityOptimizacion.f3061g0;
                                StringBuilder sb3 = new StringBuilder();
                                ?? r9 = ActivityOptimizacion.this;
                                sb3.append(r9.getString(2131820655));
                                sb3.append("\n");
                                sb3.append(r9.getString(2131820724));
                                sb3.append(" https://goo.gl/OBNeJw\n\n");
                                sb3.append(r9.getString(2131820657));
                                sb3.append(" (");
                                sb3.append(r9.f3076V.f2888o);
                                sb3.append(")\n\n");
                                sb3.append(textView2.getText());
                                sb3.append("\n");
                                for (int i10 = 0; i10 < r9.f3076V.f2882i.f2910d.size(); i10++) {
                                    sb3.append(r9.f3076V.f2882i.f2910d.get(i10).f2899b);
                                    sb3.append(" × ");
                                    sb3.append(r9.f3076V.f2882i.f2910d.get(i10).f2901d);
                                    sb3.append(" ");
                                    sb3.append(r9.getString(2131820739));
                                    sb3.append("\n");
                                }
                                sb3.append("\n");
                                sb3.append(textView3.getText());
                                sb3.append("\n");
                                for (int i11 = 0; i11 < r9.f3076V.f2882i.f2911e.size(); i11++) {
                                    sb3.append(r9.f3076V.f2882i.f2911e.get(i11).f2899b);
                                    sb3.append(" × ");
                                    sb3.append(r9.f3076V.f2882i.f2911e.get(i11).f2901d);
                                    sb3.append(" ");
                                    sb3.append(r9.getString(2131820739));
                                    sb3.append("\n");
                                }
                                sb3.append("\n");
                                sb3.append(textView4.getText());
                                sb3.append("\n");
                                for (int i12 = 0; i12 < r9.f3076V.f2882i.f.size(); i12++) {
                                    sb3.append(r9.f3076V.f2882i.f.get(i12).f2899b);
                                    sb3.append(" × ");
                                    sb3.append(r9.f3076V.f2882i.f.get(i12).f2901d);
                                    sb3.append(" ");
                                    sb3.append(r9.getString(2131820739));
                                    sb3.append("\n");
                                }
                                if (!r9.f3076V.f2897x) {
                                    F1.a aVar7 = r9.f3062H;
                                    if (aVar7 != null) {
                                        aVar7.c(new Q0(r9, sb3));
                                    }
                                    F1.a aVar8 = r9.f3062H;
                                    if (aVar8 != 0) {
                                        aVar8.e(r9);
                                    } else {
                                        r9.I();
                                        r9.startActivity(Intent.createChooser(r9.K(sb3.toString()), r9.getString(2131820656)));
                                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                    }
                                    r9.G(r9.f3063I);
                                    return;
                                }
                                r9.I();
                                r9.startActivity(Intent.createChooser(r9.K(sb3.toString()), r9.getString(2131820656)));
                            }
                        });
                        aVar6.a().show();
                        return;
                    } catch (Exception e6) {
                        e6.printStackTrace();
                        return;
                    }
                }
                return;
            default:
                U2.f fVar = (U2.f) obj;
                EditText editText = fVar.f2398i;
                if (editText != null) {
                    Editable text5 = editText.getText();
                    if (text5 != null) {
                        text5.clear();
                    }
                    fVar.q();
                    return;
                }
                return;
        }
    }
}
