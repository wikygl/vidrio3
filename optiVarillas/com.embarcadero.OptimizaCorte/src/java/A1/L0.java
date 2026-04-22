package A1;

import D.f;
import D1.C0195o;
import D1.C0198s;
import D1.RunnableC0176a;
import E.e;
import K1.C0207a;
import W1.C0324l;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.Bt;
import com.google.android.gms.internal.ads.DM;
import com.google.android.gms.internal.ads.FH;
import com.google.android.gms.internal.ads.Ft;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.JW;
import com.google.android.gms.internal.ads.LW;
import com.google.android.gms.internal.ads.Lc;
import com.google.android.gms.internal.ads.Mc;
import com.google.android.gms.internal.ads.Mt;
import com.google.android.gms.internal.ads.Nt;
import com.google.android.gms.internal.ads.OT;
import com.google.android.gms.internal.ads.Oc;
import com.google.android.gms.internal.ads.PT;
import com.google.android.gms.internal.ads.QT;
import com.google.android.gms.internal.ads.Qc;
import com.google.android.gms.internal.ads.U8;
import com.google.android.gms.internal.ads.Xc;
import com.google.android.gms.internal.ads.YW;
import com.google.android.gms.internal.ads.aY;
import com.google.android.gms.internal.ads.bu;
import com.google.android.gms.internal.ads.cL;
import com.google.android.gms.internal.ads.cO;
import com.google.android.gms.internal.ads.fZ;
import com.google.android.gms.internal.ads.hW;
import com.google.android.gms.internal.ads.iq;
import com.google.android.gms.internal.ads.lZ;
import com.google.android.gms.internal.ads.nU;
import com.google.android.gms.internal.ads.qf;
import com.google.android.gms.internal.ads.t7;
import com.google.android.gms.internal.ads.vb;
import com.google.android.gms.internal.ads.yp;
import com.google.android.gms.internal.ads.zG;
import com.google.android.gms.internal.ads.zj;
import com.google.android.gms.internal.ads.zp;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class L0 implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f56j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f57k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f58l;

    public L0(U8 u8, View view) {
        this.f56j = 5;
        this.f57k = view;
        this.f58l = u8;
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x0078, code lost:
        if (r1 == 0) goto L24;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void a() {
        /*
            r12 = this;
            java.lang.Object r0 = r12.f58l
            com.google.android.gms.internal.ads.U8 r0 = (com.google.android.gms.internal.ads.U8) r0
            java.lang.Object r1 = r12.f57k
            android.view.View r1 = (android.view.View) r1
            r0.getClass()
            com.google.android.gms.internal.ads.M8 r11 = new com.google.android.gms.internal.ads.M8     // Catch: java.lang.Exception -> L5e
            int r3 = r0.o     // Catch: java.lang.Exception -> L5e
            int r4 = r0.p     // Catch: java.lang.Exception -> L5e
            int r5 = r0.q     // Catch: java.lang.Exception -> L5e
            int r6 = r0.r     // Catch: java.lang.Exception -> L5e
            int r7 = r0.s     // Catch: java.lang.Exception -> L5e
            int r8 = r0.t     // Catch: java.lang.Exception -> L5e
            int r9 = r0.u     // Catch: java.lang.Exception -> L5e
            boolean r10 = r0.x     // Catch: java.lang.Exception -> L5e
            r2 = r11
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10)     // Catch: java.lang.Exception -> L5e
            z1.p r2 = z1.p.f6575A     // Catch: java.lang.Exception -> L5e
            com.google.android.gms.internal.ads.Q8 r2 = r2.f     // Catch: java.lang.Exception -> L5e
            android.app.Application r2 = r2.c()     // Catch: java.lang.Exception -> L5e
            if (r2 == 0) goto L60
            java.lang.String r3 = r0.v     // Catch: java.lang.Exception -> L5e
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch: java.lang.Exception -> L5e
            if (r3 != 0) goto L60
            android.content.res.Resources r3 = r2.getResources()     // Catch: java.lang.Exception -> L5e
            com.google.android.gms.internal.ads.zb r4 = com.google.android.gms.internal.ads.Gb.P     // Catch: java.lang.Exception -> L5e
            A1.r r5 = A1.r.f168d     // Catch: java.lang.Exception -> L5e
            com.google.android.gms.internal.ads.Eb r5 = r5.f171c     // Catch: java.lang.Exception -> L5e
            java.lang.Object r4 = r5.a(r4)     // Catch: java.lang.Exception -> L5e
            java.lang.String r4 = (java.lang.String) r4     // Catch: java.lang.Exception -> L5e
            java.lang.String r5 = "id"
            java.lang.String r2 = r2.getPackageName()     // Catch: java.lang.Exception -> L5e
            int r2 = r3.getIdentifier(r4, r5, r2)     // Catch: java.lang.Exception -> L5e
            java.lang.Object r2 = r1.getTag(r2)     // Catch: java.lang.Exception -> L5e
            java.lang.String r2 = (java.lang.String) r2     // Catch: java.lang.Exception -> L5e
            if (r2 == 0) goto L60
            java.lang.String r3 = r0.v     // Catch: java.lang.Exception -> L5e
            boolean r2 = r2.equals(r3)     // Catch: java.lang.Exception -> L5e
            if (r2 != 0) goto La3
            goto L60
        L5e:
            r0 = move-exception
            goto L95
        L60:
            com.google.android.gms.internal.ads.T8 r1 = r0.b(r1, r11)     // Catch: java.lang.Exception -> L5e
            r11.c()     // Catch: java.lang.Exception -> L5e
            int r2 = r1.a     // Catch: java.lang.Exception -> L5e
            if (r2 != 0) goto L6f
            int r2 = r1.b     // Catch: java.lang.Exception -> L5e
            if (r2 == 0) goto La3
        L6f:
            int r1 = r1.b     // Catch: java.lang.Exception -> L5e
            if (r1 != 0) goto L78
            int r1 = r11.k     // Catch: java.lang.Exception -> L5e
            if (r1 == 0) goto La3
            goto L7a
        L78:
            if (r1 != 0) goto L8f
        L7a:
            com.google.android.gms.internal.ads.N8 r1 = r0.m     // Catch: java.lang.Exception -> L5e
            java.lang.Object r2 = r1.a     // Catch: java.lang.Exception -> L5e
            monitor-enter(r2)     // Catch: java.lang.Exception -> L5e
            java.util.LinkedList r1 = r1.c     // Catch: java.lang.Throwable -> L89
            boolean r1 = r1.contains(r11)     // Catch: java.lang.Throwable -> L89
            if (r1 == 0) goto L8b
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L89
            goto La3
        L89:
            r0 = move-exception
            goto L8d
        L8b:
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L89
            goto L8f
        L8d:
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L89
            throw r0     // Catch: java.lang.Exception -> L5e
        L8f:
            com.google.android.gms.internal.ads.N8 r0 = r0.m     // Catch: java.lang.Exception -> L5e
            r0.a(r11)     // Catch: java.lang.Exception -> L5e
            goto La3
        L95:
            java.lang.String r1 = "Exception in fetchContentOnUIThread"
            E1.m.e(r1, r0)
            java.lang.String r1 = "ContentFetchTask.fetchContent"
            z1.p r2 = z1.p.f6575A
            com.google.android.gms.internal.ads.pk r2 = r2.f6581g
            r2.h(r1, r0)
        La3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: A1.L0.a():void");
    }

    private final void b() {
        zj zjVar = (zj) this.f57k;
        zjVar.getClass();
        OT ot = QT.k;
        PT pt = new PT();
        ((Bitmap) this.f58l).compress(Bitmap.CompressFormat.PNG, 0, pt);
        synchronized (zjVar.h) {
            hW hWVar = zjVar.a;
            JW D4 = LW.D();
            QT a4 = pt.a();
            D4.k();
            LW.H(((nU) D4).k, a4);
            D4.k();
            LW.G(((nU) D4).k);
            D4.k();
            LW.F(((nU) D4).k);
            hWVar.k();
            YW.P(((nU) hWVar).k, D4.i());
        }
    }

    private final void c() {
        ViewGroup viewGroup;
        View view;
        View view2;
        ViewGroup viewGroup2;
        Xc a4;
        Drawable drawable;
        Nt nt = (Nt) this.f57k;
        Ft ft = nt.c;
        boolean e4 = ft.e();
        bu buVar = (bu) this.f58l;
        Context context = null;
        if (e4 || ft.d()) {
            String[] strArr = {"1098", "3011"};
            for (int i4 = 0; i4 < 2; i4++) {
                View g22 = buVar.g2(strArr[i4]);
                if (g22 != null && (g22 instanceof ViewGroup)) {
                    viewGroup = (ViewGroup) g22;
                    break;
                }
            }
        }
        viewGroup = null;
        Context context2 = buVar.d().getContext();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        Bt bt = nt.d;
        synchronized (bt) {
            view = bt.d;
        }
        if (view != null) {
            view2 = bt.F();
            Qc qc = nt.i;
            if (qc != null && viewGroup == null) {
                Nt.b(layoutParams, qc.n);
                view2.setLayoutParams(layoutParams);
                viewGroup = null;
            }
        } else if (!(bt.L() instanceof Lc)) {
            view2 = null;
        } else {
            Lc L3 = bt.L();
            if (viewGroup == null) {
                Nt.b(layoutParams, L3.q);
                viewGroup = null;
            }
            RelativeLayout relativeLayout = new RelativeLayout(context2);
            C0324l.d(L3);
            ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(Mc.k, null, null));
            shapeDrawable.getPaint().setColor(L3.m);
            relativeLayout.setLayoutParams(layoutParams);
            relativeLayout.setBackground(shapeDrawable);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
            String str = L3.j;
            if (!TextUtils.isEmpty(str)) {
                RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-2, -2);
                TextView textView = new TextView(context2);
                textView.setLayoutParams(layoutParams3);
                textView.setId(1195835393);
                textView.setTypeface(Typeface.DEFAULT);
                textView.setText(str);
                textView.setTextColor(L3.n);
                textView.setTextSize(L3.o);
                E1.f fVar = C0124p.f.f161a;
                textView.setPadding(E1.f.m(context2, 4), 0, E1.f.j(context2.getResources().getDisplayMetrics(), 4), 0);
                relativeLayout.addView(textView);
                layoutParams2.addRule(1, textView.getId());
            }
            ImageView imageView = new ImageView(context2);
            imageView.setLayoutParams(layoutParams2);
            imageView.setId(1195835394);
            ArrayList arrayList = L3.k;
            if (arrayList != null && arrayList.size() > 1) {
                ((Mc) relativeLayout).j = new AnimationDrawable();
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    try {
                        ((Mc) relativeLayout).j.addFrame((Drawable) c2.b.p0(((Oc) it.next()).d()), L3.p);
                    } catch (Exception e5) {
                        E1.m.e("Error while getting drawable.", e5);
                    }
                }
                imageView.setBackground(((Mc) relativeLayout).j);
            } else if (arrayList.size() == 1) {
                try {
                    imageView.setImageDrawable((Drawable) c2.b.p0(((Oc) arrayList.get(0)).d()));
                } catch (Exception e6) {
                    E1.m.e("Error while getting drawable.", e6);
                }
            }
            relativeLayout.addView(imageView);
            relativeLayout.setContentDescription((CharSequence) r.f168d.f171c.a(Gb.p3));
            view2 = relativeLayout;
        }
        if (view2 != null) {
            if (view2.getParent() instanceof ViewGroup) {
                ((ViewGroup) view2.getParent()).removeView(view2);
            }
            if (viewGroup != null) {
                viewGroup.removeAllViews();
                viewGroup.addView(view2);
            } else {
                RelativeLayout relativeLayout2 = new RelativeLayout(buVar.d().getContext());
                relativeLayout2.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
                relativeLayout2.addView(view2);
                FrameLayout g4 = buVar.g();
                if (g4 != null) {
                    g4.addView(relativeLayout2);
                }
            }
            buVar.i3(view2, buVar.k());
        }
        DM dm = Mt.y;
        int i5 = dm.m;
        int i6 = 0;
        while (true) {
            if (i6 < i5) {
                View g23 = buVar.g2((String) dm.get(i6));
                i6++;
                if (g23 instanceof ViewGroup) {
                    viewGroup2 = (ViewGroup) g23;
                    break;
                }
            } else {
                viewGroup2 = null;
                break;
            }
        }
        nt.h.execute(new B.h(nt, 5, viewGroup2));
        if (viewGroup2 != null) {
            if (nt.c(viewGroup2, true)) {
                if (bt.R() != null) {
                    bt.R().S0(new C0117l0(buVar, 8, viewGroup2));
                    return;
                }
                return;
            }
            vb vbVar = Gb.V8;
            r rVar = r.f168d;
            if (((Boolean) rVar.f171c.a(vbVar)).booleanValue() && nt.c(viewGroup2, false)) {
                if (bt.P() != null) {
                    bt.P().S0(new C0117l0(buVar, 8, viewGroup2));
                    return;
                }
                return;
            }
            viewGroup2.removeAllViews();
            View d4 = buVar.d();
            if (d4 != null) {
                context = d4.getContext();
            }
            if (context != null && (a4 = nt.j.a()) != null) {
                try {
                    InterfaceC0374a f = a4.f();
                    if (f != null && (drawable = (Drawable) c2.b.p0(f)) != null) {
                        ImageView imageView2 = new ImageView(context);
                        imageView2.setImageDrawable(drawable);
                        InterfaceC0374a j4 = buVar.j();
                        if (j4 != null) {
                            if (((Boolean) rVar.f171c.a(Gb.v5)).booleanValue()) {
                                imageView2.setScaleType((ImageView.ScaleType) c2.b.p0(j4));
                                imageView2.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
                                viewGroup2.addView(imageView2);
                            }
                        }
                        imageView2.setScaleType(Nt.k);
                        imageView2.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
                        viewGroup2.addView(imageView2);
                    }
                } catch (RemoteException unused) {
                    E1.m.g("Could not get main image drawable");
                }
            }
        }
    }

    @Override // java.lang.Runnable
    public final void run() {
        zG zGVar;
        Runnable runnable;
        switch (this.f56j) {
            case 0:
                O0 o02 = (O0) this.f57k;
                o02.getClass();
                o02.f77l.addView((View) c2.b.p0((InterfaceC0374a) this.f58l));
                return;
            case 1:
                C0195o c0195o = (C0195o) this.f57k;
                c0195o.getClass();
                z1.p pVar = z1.p.f6575A;
                C0198s c0198s = pVar.f6587m;
                String str = c0195o.f748d;
                String str2 = c0195o.f749e;
                Context context = c0195o.f745a;
                if (!c0198s.f(context, str, str2)) {
                    pVar.f6587m.b(context, c0195o.f748d, c0195o.f749e);
                    return;
                } else {
                    ((cO) this.f58l).execute(new RunnableC0176a(1, c0195o));
                    return;
                }
            case 2:
                f.e eVar = ((e.a) ((B2.a) this.f57k)).f812j;
                if (eVar != null) {
                    eVar.c((Typeface) this.f58l);
                    return;
                }
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                C0207a c0207a = (C0207a) this.f57k;
                c0207a.getClass();
                Uri parse = Uri.parse((String) this.f58l);
                try {
                    boolean booleanValue = ((Boolean) r.f168d.f171c.a(Gb.Ia)).booleanValue();
                    WebView webView = c0207a.f1315b;
                    Context context2 = c0207a.f1314a;
                    if (booleanValue && (zGVar = c0207a.f1317d) != null) {
                        parse = zGVar.a(parse, context2, webView, (Activity) null);
                    } else {
                        parse = c0207a.f1316c.a(parse, context2, webView, (Activity) null);
                    }
                } catch (t7 e4) {
                    E1.m.c("Failed to append the click signal to URL: ", e4);
                    z1.p.f6575A.f6581g.h("TaggingLibraryJsInterface.recordClick", e4);
                }
                c0207a.f1321i.a(parse.toString(), (FH) null);
                return;
            case 4:
                com.google.android.gms.internal.ads.K k4 = (com.google.android.gms.internal.ads.K) this.f57k;
                aY aYVar = (aY) this.f58l;
                k4.getClass();
                synchronized (aYVar) {
                }
                int i4 = cL.a;
                lZ lZVar = k4.b.j.p;
                fZ D4 = lZVar.D(lZVar.m.e);
                lZVar.B(D4, 1020, new iq(D4, aYVar));
                return;
            case 5:
                a();
                return;
            case 6:
                ((qf) this.f57k).j.j.loadData((String) this.f58l, "text/html", "UTF-8");
                return;
            case 7:
                b();
                return;
            case 8:
                yp ypVar = (Runnable) this.f58l;
                zp zpVar = (zp) this.f57k;
                zpVar.getClass();
                try {
                    if (!zpVar.j.W(new c2.b(ypVar)) && (runnable = (Runnable) ypVar.j.getAndSet(null)) != null) {
                        runnable.run();
                        return;
                    }
                    return;
                } catch (RemoteException unused) {
                    Runnable runnable2 = (Runnable) ypVar.j.getAndSet(null);
                    if (runnable2 != null) {
                        runnable2.run();
                        return;
                    }
                    return;
                }
            case 9:
                c();
                return;
            case 10:
                InputStream inputStream = (InputStream) this.f57k;
                try {
                    ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream((ParcelFileDescriptor) this.f58l);
                    try {
                        a2.f.b(inputStream, autoCloseOutputStream, false);
                        autoCloseOutputStream.close();
                        inputStream.close();
                        return;
                    } catch (Throwable th) {
                        try {
                            autoCloseOutputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                } catch (IOException unused2) {
                    return;
                }
            default:
                ((i2.b0) this.f58l).a();
                ((S0.M0) this.f57k).c();
                return;
        }
    }

    public /* synthetic */ L0(Object obj, int i4, Object obj2) {
        this.f56j = i4;
        this.f57k = obj;
        this.f58l = obj2;
    }
}
