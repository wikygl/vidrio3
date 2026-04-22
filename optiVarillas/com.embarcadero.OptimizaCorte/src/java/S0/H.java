package S0;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;
import b1.C0354b;
import c1.C0373f;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import j$.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class H implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2131j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ ActivityListaCorte f2132k;

    public /* synthetic */ H(ActivityListaCorte activityListaCorte, int i4) {
        this.f2131j = i4;
        this.f2132k = activityListaCorte;
    }

    /* JADX WARN: Type inference failed for: r3v0, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte] */
    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        String str;
        C0354b c0354b;
        C0354b c0354b2;
        ?? r32 = this.f2132k;
        switch (this.f2131j) {
            case 0:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                C0373f.l(10L, r32.getApplicationContext());
                Editable text = r32.f3003I.getText();
                Objects.requireNonNull(text);
                String trim = text.toString().trim();
                Editable text2 = r32.f3004J.getText();
                Objects.requireNonNull(text2);
                String trim2 = text2.toString().trim();
                Editable text3 = r32.f3005K.getText();
                Objects.requireNonNull(text3);
                String trim3 = text3.toString().trim();
                String str2 = r32.f3011Q[r32.f3012R];
                if (trim2.isEmpty()) {
                    trim2 = "1";
                }
                boolean isEmpty = r32.f3003I.getText().toString().trim().isEmpty();
                ArrayList<C0354b> arrayList = r32.f3017W;
                if (isEmpty) {
                    str = "@";
                } else {
                    String trim4 = r32.f3003I.getText().toString().trim();
                    if (!trim.equals("0") && !trim2.equals("0")) {
                        try {
                            if (!r32.f3025e0.f2888o.equals(r32.getString(2131820882)) || r32.f3025e0.f2892s.split("@")[0].equals("2")) {
                                trim4 = C0373f.g(Double.parseDouble(r32.f3003I.getText().toString().trim()));
                            }
                        } catch (NumberFormatException unused) {
                            Toast.makeText((Context) r32, r32.getString(2131820701), 1).show();
                            return;
                        }
                    } else {
                        Toast.makeText((Context) r32, r32.getString(2131820701), 1).show();
                    }
                    if (!r32.f3025e0.f2888o.equals(r32.getString(2131820882)) || r32.f3025e0.f2892s.split("@")[0].equals("2")) {
                        str = "@";
                    } else {
                        try {
                            str = "@";
                            trim4 = C0373f.j(C0373f.h(trim4), r32.f3015U);
                            if (trim4.equals("0")) {
                                Toast.makeText((Context) r32, r32.getString(2131820701), 1).show();
                                return;
                            }
                        } catch (Exception unused2) {
                            Toast.makeText((Context) r32, r32.getString(2131820701), 1).show();
                            return;
                        }
                    }
                    try {
                        if (r32.f3005K.getText().toString().trim().isEmpty()) {
                            if (!r32.f3025e0.f2897x) {
                                c0354b2 = new C0354b(trim4, trim2 + " " + r32.getString(2131820739), "typeRect", r32.f3025e0);
                            } else {
                                c0354b2 = new C0354b(trim4, trim2 + " " + r32.getString(2131820739), str2, r32.f3025e0);
                            }
                            arrayList.add(0, c0354b2);
                        } else {
                            if (!r32.f3025e0.f2897x) {
                                c0354b = new C0354b(trim4 + "[" + trim3 + "]", trim2 + " " + r32.getString(2131820739), "typeRect", r32.f3025e0);
                            } else {
                                c0354b = new C0354b(trim4 + "[" + trim3 + "]", trim2 + " " + r32.getString(2131820739), str2, r32.f3025e0);
                            }
                            arrayList.add(0, c0354b);
                        }
                        r32.f3019Y.notifyDataSetChanged();
                        r32.f3006L.smoothScrollToPosition(0);
                        r32.f3003I.setText("");
                        r32.f3004J.setText("");
                        r32.f3005K.setText("");
                        r32.f3003I.requestFocus();
                    } catch (NumberFormatException unused3) {
                        Toast.makeText((Context) r32, r32.getString(2131820701), 1).show();
                        return;
                    }
                }
                StringBuilder sb = new StringBuilder();
                if (!arrayList.isEmpty()) {
                    for (int i4 = 0; i4 < arrayList.size(); i4++) {
                        sb.append(arrayList.get(i4).f2899b);
                        sb.append(str);
                        sb.append(arrayList.get(i4).f2900c);
                        sb.append(str);
                        sb.append(arrayList.get(i4).f);
                        sb.append(str);
                    }
                    ArrayList<String> arrayList2 = new ArrayList<>(Arrays.asList(new StringBuilder(sb.substring(0, sb.lastIndexOf(str))).toString().split(str)));
                    arrayList2.trimToSize();
                    r32.f3025e0.f2883j = arrayList2;
                }
                r32.F();
                return;
            default:
                ArrayList<C0354b> arrayList3 = r32.f3017W;
                arrayList3.addAll(r32.f3018X);
                arrayList3.trimToSize();
                StringBuilder sb2 = new StringBuilder();
                if (!arrayList3.isEmpty()) {
                    for (int i5 = 0; i5 < arrayList3.size(); i5++) {
                        sb2.append(arrayList3.get(i5).f2899b);
                        sb2.append("@");
                        sb2.append(arrayList3.get(i5).f2900c);
                        sb2.append("@");
                        sb2.append(arrayList3.get(i5).f);
                        sb2.append("@");
                    }
                    ArrayList<String> arrayList4 = new ArrayList<>(Arrays.asList(new StringBuilder(sb2.substring(0, sb2.lastIndexOf("@"))).toString().split("@")));
                    arrayList4.trimToSize();
                    r32.f3025e0.f2883j = arrayList4;
                    r32.f3019Y.notifyDataSetChanged();
                    r32.F();
                    return;
                }
                return;
        }
    }
}
