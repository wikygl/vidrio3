package D1;

import A1.L0;
import A1.RunnableC0098e1;
import A1.RunnableC0104g1;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Jw;
import com.google.android.gms.internal.ads.Nw;
import com.google.android.gms.internal.ads.UN;
import com.google.android.gms.internal.ads.WJ;
import com.google.android.gms.internal.ads.wk;
import com.google.android.gms.internal.ads.xk;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/* renamed from: D1.o  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0195o {

    /* renamed from: a  reason: collision with root package name */
    public final Context f745a;

    /* renamed from: b  reason: collision with root package name */
    public final Nw f746b;

    /* renamed from: c  reason: collision with root package name */
    public String f747c;

    /* renamed from: d  reason: collision with root package name */
    public String f748d;

    /* renamed from: e  reason: collision with root package name */
    public String f749e;
    public String f;

    /* renamed from: h  reason: collision with root package name */
    public final int f751h;

    /* renamed from: i  reason: collision with root package name */
    public PointF f752i;

    /* renamed from: j  reason: collision with root package name */
    public PointF f753j;

    /* renamed from: k  reason: collision with root package name */
    public final WJ f754k;

    /* renamed from: g  reason: collision with root package name */
    public int f750g = 0;

    /* renamed from: l  reason: collision with root package name */
    public final RunnableC0186f f755l = new RunnableC0186f(0, this);

    public C0195o(Context context) {
        this.f745a = context;
        this.f751h = ViewConfiguration.get(context).getScaledTouchSlop();
        z1.p pVar = z1.p.f6575A;
        pVar.f6592r.a();
        this.f754k = pVar.f6592r.f639b;
        this.f746b = pVar.f6587m.f772g;
    }

    public static final int e(ArrayList arrayList, String str, boolean z4) {
        if (!z4) {
            return -1;
        }
        arrayList.add(str);
        return arrayList.size() - 1;
    }

    public final void a(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        int historySize = motionEvent.getHistorySize();
        int pointerCount = motionEvent.getPointerCount();
        if (actionMasked == 0) {
            this.f750g = 0;
            this.f752i = new PointF(motionEvent.getX(0), motionEvent.getY(0));
            return;
        }
        int i4 = this.f750g;
        if (i4 != -1) {
            RunnableC0186f runnableC0186f = this.f755l;
            WJ wj = this.f754k;
            if (i4 == 0) {
                if (actionMasked == 5) {
                    this.f750g = 5;
                    this.f753j = new PointF(motionEvent.getX(1), motionEvent.getY(1));
                    wj.postDelayed(runnableC0186f, ((Long) A1.r.f168d.f171c.a(Gb.c4)).longValue());
                }
            } else if (i4 == 5) {
                if (pointerCount == 2) {
                    if (actionMasked == 2) {
                        boolean z4 = false;
                        for (int i5 = 0; i5 < historySize; i5++) {
                            z4 |= !d(motionEvent.getHistoricalX(0, i5), motionEvent.getHistoricalY(0, i5), motionEvent.getHistoricalX(1, i5), motionEvent.getHistoricalY(1, i5));
                        }
                        if (d(motionEvent.getX(), motionEvent.getY(), motionEvent.getX(1), motionEvent.getY(1)) && !z4) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                this.f750g = -1;
                wj.removeCallbacks(runnableC0186f);
            }
        }
    }

    public final void b() {
        String str;
        Context context = this.f745a;
        try {
            if (!(context instanceof Activity)) {
                E1.m.f("Can not create dialog without Activity Context");
                return;
            }
            z1.p pVar = z1.p.f6575A;
            C0198s c0198s = pVar.f6587m;
            synchronized (c0198s.f767a) {
                str = c0198s.f769c;
            }
            String str2 = "Creative preview (enabled)";
            if (true == TextUtils.isEmpty(str)) {
                str2 = "Creative preview";
            }
            String str3 = "Troubleshooting (enabled)";
            if (true != pVar.f6587m.h()) {
                str3 = "Troubleshooting";
            }
            ArrayList arrayList = new ArrayList();
            final int e4 = e(arrayList, "Ad information", true);
            final int e5 = e(arrayList, str2, true);
            final int e6 = e(arrayList, str3, true);
            boolean booleanValue = ((Boolean) A1.r.f168d.f171c.a(Gb.j8)).booleanValue();
            final int e7 = e(arrayList, "Open ad inspector", booleanValue);
            final int e8 = e(arrayList, "Ad inspector settings", booleanValue);
            AlertDialog.Builder i4 = t0.i(context);
            i4.setTitle("Select a debug mode").setItems((CharSequence[]) arrayList.toArray(new String[0]), new DialogInterface.OnClickListener() { // from class: D1.i
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i5) {
                    final C0195o c0195o = C0195o.this;
                    c0195o.getClass();
                    if (i5 == e4) {
                        Context context2 = c0195o.f745a;
                        if (!(context2 instanceof Activity)) {
                            E1.m.f("Can not create dialog without Activity Context");
                            return;
                        }
                        String str4 = c0195o.f747c;
                        final String str5 = "No debug information";
                        if (!TextUtils.isEmpty(str4)) {
                            Uri build = new Uri.Builder().encodedQuery(str4.replaceAll("\\+", "%20")).build();
                            StringBuilder sb = new StringBuilder();
                            t0 t0Var = z1.p.f6575A.f6578c;
                            HashMap l2 = t0.l(build);
                            for (String str6 : l2.keySet()) {
                                sb.append(str6);
                                sb.append(" = ");
                                sb.append((String) l2.get(str6));
                                sb.append("\n\n");
                            }
                            String trim = sb.toString().trim();
                            if (!TextUtils.isEmpty(trim)) {
                                str5 = trim;
                            }
                        }
                        t0 t0Var2 = z1.p.f6575A.f6578c;
                        AlertDialog.Builder i6 = t0.i(context2);
                        i6.setMessage(str5);
                        i6.setTitle("Ad Information");
                        i6.setPositiveButton("Share", new DialogInterface.OnClickListener() { // from class: D1.g
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface2, int i7) {
                                C0195o c0195o2 = C0195o.this;
                                c0195o2.getClass();
                                t0 t0Var3 = z1.p.f6575A.f6578c;
                                t0.p(c0195o2.f745a, Intent.createChooser(new Intent("android.intent.action.SEND").setType("text/plain").putExtra("android.intent.extra.TEXT", str5), "Share via"));
                            }
                        });
                        i6.setNegativeButton("Close", DialogInterface$OnClickListenerC0188h.f687j);
                        i6.create().show();
                    } else if (i5 == e5) {
                        E1.m.b("Debug mode [Creative Preview] selected.");
                        xk.a.execute(new RunnableC0104g1(1, c0195o));
                    } else if (i5 == e6) {
                        E1.m.b("Debug mode [Troubleshooting] selected.");
                        xk.a.execute(new RunnableC0098e1(1, c0195o));
                    } else {
                        int i7 = e7;
                        Nw nw = c0195o.f746b;
                        if (i5 == i7) {
                            wk wkVar = xk.e;
                            wk wkVar2 = xk.a;
                            if (nw.f()) {
                                wkVar.execute(new RunnableC0194n(0, c0195o));
                            } else {
                                wkVar2.execute(new L0(c0195o, 1, wkVar));
                            }
                        } else if (i5 == e8) {
                            wk wkVar3 = xk.e;
                            wk wkVar4 = xk.a;
                            if (nw.f()) {
                                wkVar3.execute(new RunnableC0184e(0, c0195o));
                            } else {
                                wkVar4.execute(new UN(c0195o, 1, wkVar3));
                            }
                        }
                    }
                }
            });
            i4.create().show();
        } catch (WindowManager.BadTokenException e9) {
            C0183d0.l("", e9);
        }
    }

    public final void c(Context context) {
        final int i4;
        ArrayList arrayList = new ArrayList();
        int e4 = e(arrayList, "None", true);
        final int e5 = e(arrayList, "Shake", true);
        final int e6 = e(arrayList, "Flick", true);
        int ordinal = this.f746b.r.ordinal();
        if (ordinal != 1) {
            if (ordinal != 2) {
                i4 = e4;
            } else {
                i4 = e6;
            }
        } else {
            i4 = e5;
        }
        t0 t0Var = z1.p.f6575A.f6578c;
        AlertDialog.Builder i5 = t0.i(context);
        final AtomicInteger atomicInteger = new AtomicInteger(i4);
        i5.setTitle("Setup gesture");
        i5.setSingleChoiceItems((CharSequence[]) arrayList.toArray(new String[0]), i4, new DialogInterface.OnClickListener() { // from class: D1.j
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i6) {
                atomicInteger.set(i6);
            }
        });
        i5.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() { // from class: D1.k
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i6) {
                C0195o.this.b();
            }
        });
        i5.setPositiveButton("Save", new DialogInterface.OnClickListener() { // from class: D1.l
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i6) {
                C0195o c0195o = C0195o.this;
                c0195o.getClass();
                AtomicInteger atomicInteger2 = atomicInteger;
                if (atomicInteger2.get() != i4) {
                    int i7 = atomicInteger2.get();
                    int i8 = e5;
                    Nw nw = c0195o.f746b;
                    if (i7 == i8) {
                        nw.k(Jw.k, true);
                    } else if (atomicInteger2.get() == e6) {
                        nw.k(Jw.l, true);
                    } else {
                        nw.k(Jw.j, true);
                    }
                }
                c0195o.b();
            }
        });
        i5.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: D1.m
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                C0195o.this.b();
            }
        });
        i5.create().show();
    }

    public final boolean d(float f, float f4, float f5, float f6) {
        float abs = Math.abs(this.f752i.x - f);
        int i4 = this.f751h;
        if (abs < i4 && Math.abs(this.f752i.y - f4) < i4 && Math.abs(this.f753j.x - f5) < i4 && Math.abs(this.f753j.y - f6) < i4) {
            return true;
        }
        return false;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("{Dialog: ");
        sb.append(this.f747c);
        sb.append(",DebugSignal: ");
        sb.append(this.f);
        sb.append(",AFMA Version: ");
        sb.append(this.f749e);
        sb.append(",Ad Unit ID: ");
        return C.b.c(sb, this.f748d, "}");
    }
}
