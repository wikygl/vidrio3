package D1;

import K1.C0208b;
import M.InterfaceC0241x;
import M.O;
import M.e0;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import androidx.appcompat.widget.ActionBarContextView;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.J5;
import com.google.android.gms.internal.ads.N5;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.Zv;
import com.google.android.gms.internal.ads.pJ;
import com.google.android.gms.internal.ads.vb;
import com.google.android.gms.internal.ads.zk;
import e.LayoutInflater$Factory2C0401j;
import i2.C0469o;
import java.lang.reflect.Method;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import l.i0;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class E implements J5, TN, InterfaceC0241x, i2.Z, pJ {

    /* renamed from: j  reason: collision with root package name */
    public final Object f633j;

    public /* synthetic */ E(Object obj) {
        this.f633j = obj;
    }

    public static ContentValues h(String str, String str2, String str3, String str4, String str5) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOMBRE", str);
        contentValues.put("LISTA_MEDIDAS", str2);
        contentValues.put("FECHA", str3);
        contentValues.put("CONFIG", str4);
        contentValues.put("RETALES", str5);
        return contentValues;
    }

    @Override // i2.Z
    public /* synthetic */ Object a() {
        i2.F f = i2.G.f3680b;
        H.a.t(f);
        return new C0469o((i2.Z) this.f633j, f);
    }

    public void b(int i4, long j4) {
        ((z1.f) this.f633j).f6540q.c(i4, System.currentTimeMillis() - j4);
    }

    public void c(int i4, long j4, String str) {
        ((z1.f) this.f633j).f6540q.d(i4, System.currentTimeMillis() - j4, (Exception) null, (String) null, str);
    }

    public void d(N5 n5) {
        ((zk) this.f633j).c(n5);
    }

    public Cursor e() {
        return ((SQLiteDatabase) this.f633j).query("PROYECTOS", new String[]{"_id", "NOMBRE", "FECHA", "LISTA_MEDIDAS", "CONFIG", "RETALES"}, null, null, null, null, null);
    }

    @Override // M.InterfaceC0241x
    public M.e0 f(View view, M.e0 e0Var) {
        boolean z4;
        M.e0 e0Var2;
        e0.e bVar;
        int i4;
        boolean z5;
        int b4;
        int c4;
        boolean z6;
        boolean z7;
        int b5;
        int d4 = e0Var.d();
        LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = (LayoutInflater$Factory2C0401j) this.f633j;
        layoutInflater$Factory2C0401j.getClass();
        int d5 = e0Var.d();
        ActionBarContextView actionBarContextView = layoutInflater$Factory2C0401j.f3204E;
        if (actionBarContextView != null && (actionBarContextView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutInflater$Factory2C0401j.f3204E.getLayoutParams();
            if (layoutInflater$Factory2C0401j.f3204E.isShown()) {
                if (layoutInflater$Factory2C0401j.f3237m0 == null) {
                    layoutInflater$Factory2C0401j.f3237m0 = new Rect();
                    layoutInflater$Factory2C0401j.f3238n0 = new Rect();
                }
                Rect rect = layoutInflater$Factory2C0401j.f3237m0;
                Rect rect2 = layoutInflater$Factory2C0401j.f3238n0;
                rect.set(e0Var.b(), e0Var.d(), e0Var.c(), e0Var.a());
                ViewGroup viewGroup = layoutInflater$Factory2C0401j.f3210K;
                if (Build.VERSION.SDK_INT >= 29) {
                    boolean z8 = l.i0.f5158a;
                    i0.a.a(viewGroup, rect, rect2);
                } else {
                    if (!l.i0.f5158a) {
                        l.i0.f5158a = true;
                        try {
                            Method declaredMethod = View.class.getDeclaredMethod("computeFitSystemWindows", Rect.class, Rect.class);
                            l.i0.f5159b = declaredMethod;
                            if (!declaredMethod.isAccessible()) {
                                l.i0.f5159b.setAccessible(true);
                            }
                        } catch (NoSuchMethodException unused) {
                            Log.d("ViewUtils", "Could not find method computeFitSystemWindows. Oh well.");
                        }
                    }
                    Method method = l.i0.f5159b;
                    if (method != null) {
                        try {
                            method.invoke(viewGroup, rect, rect2);
                        } catch (Exception e4) {
                            Log.d("ViewUtils", "Could not invoke computeFitSystemWindows", e4);
                        }
                    }
                }
                int i5 = rect.top;
                int i6 = rect.left;
                int i7 = rect.right;
                M.e0 h4 = M.O.h(layoutInflater$Factory2C0401j.f3210K);
                if (h4 == null) {
                    b4 = 0;
                } else {
                    b4 = h4.b();
                }
                if (h4 == null) {
                    c4 = 0;
                } else {
                    c4 = h4.c();
                }
                if (marginLayoutParams.topMargin == i5 && marginLayoutParams.leftMargin == i6 && marginLayoutParams.rightMargin == i7) {
                    z6 = false;
                } else {
                    marginLayoutParams.topMargin = i5;
                    marginLayoutParams.leftMargin = i6;
                    marginLayoutParams.rightMargin = i7;
                    z6 = true;
                }
                Context context = layoutInflater$Factory2C0401j.f3243t;
                if (i5 > 0 && layoutInflater$Factory2C0401j.f3212M == null) {
                    View view2 = new View(context);
                    layoutInflater$Factory2C0401j.f3212M = view2;
                    view2.setVisibility(8);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, marginLayoutParams.topMargin, 51);
                    layoutParams.leftMargin = b4;
                    layoutParams.rightMargin = c4;
                    layoutInflater$Factory2C0401j.f3210K.addView(layoutInflater$Factory2C0401j.f3212M, -1, layoutParams);
                } else {
                    View view3 = layoutInflater$Factory2C0401j.f3212M;
                    if (view3 != null) {
                        ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) view3.getLayoutParams();
                        int i8 = marginLayoutParams2.height;
                        int i9 = marginLayoutParams.topMargin;
                        if (i8 != i9 || marginLayoutParams2.leftMargin != b4 || marginLayoutParams2.rightMargin != c4) {
                            marginLayoutParams2.height = i9;
                            marginLayoutParams2.leftMargin = b4;
                            marginLayoutParams2.rightMargin = c4;
                            layoutInflater$Factory2C0401j.f3212M.setLayoutParams(marginLayoutParams2);
                        }
                    }
                }
                View view4 = layoutInflater$Factory2C0401j.f3212M;
                if (view4 != null) {
                    z7 = true;
                } else {
                    z7 = false;
                }
                if (z7 && view4.getVisibility() != 0) {
                    View view5 = layoutInflater$Factory2C0401j.f3212M;
                    if ((view5.getWindowSystemUiVisibility() & 8192) != 0) {
                        b5 = C.a.b(context, 2131034119);
                    } else {
                        b5 = C.a.b(context, 2131034118);
                    }
                    view5.setBackgroundColor(b5);
                }
                if (!layoutInflater$Factory2C0401j.f3217R && z7) {
                    d5 = 0;
                }
                z4 = z7;
                z5 = z6;
            } else {
                if (marginLayoutParams.topMargin != 0) {
                    marginLayoutParams.topMargin = 0;
                    z5 = true;
                } else {
                    z5 = false;
                }
                z4 = false;
            }
            if (z5) {
                layoutInflater$Factory2C0401j.f3204E.setLayoutParams(marginLayoutParams);
            }
        } else {
            z4 = false;
        }
        View view6 = layoutInflater$Factory2C0401j.f3212M;
        if (view6 != null) {
            if (z4) {
                i4 = 0;
            } else {
                i4 = 8;
            }
            view6.setVisibility(i4);
        }
        if (d4 != d5) {
            int b6 = e0Var.b();
            int c5 = e0Var.c();
            int a4 = e0Var.a();
            int i10 = Build.VERSION.SDK_INT;
            if (i10 >= 30) {
                bVar = new e0.d(e0Var);
            } else if (i10 >= 29) {
                bVar = new e0.c(e0Var);
            } else {
                bVar = new e0.b(e0Var);
            }
            bVar.g(E.b.b(b6, d5, c5, a4));
            e0Var2 = bVar.b();
        } else {
            e0Var2 = e0Var;
        }
        WeakHashMap<View, M.V> weakHashMap = M.O.f1526a;
        WindowInsets f = e0Var2.f();
        if (f != null) {
            WindowInsets b7 = O.c.b(view, f);
            if (!b7.equals(f)) {
                return M.e0.g(view, b7);
            }
            return e0Var2;
        }
        return e0Var2;
    }

    public void g(Object obj) {
        K1.o oVar = (K1.o) obj;
        E1.m.b("Initialized webview successfully for SDKCore.");
        if (((Boolean) A1.r.f168d.f171c.a(Gb.I8)).booleanValue()) {
            C0208b c0208b = (C0208b) this.f633j;
            K1.w.c(c0208b.f1352v, "sgs", new Pair("se", "query_g"), new Pair("ad_format", "BANNER"), new Pair("rtype", Integer.toString(6)), new Pair("scar", "true"), new Pair("sgi_rn", Integer.toString(c0208b.f1339N.get())));
            c0208b.f1338M.set(true);
        }
    }

    public void m(Throwable th) {
        z1.p.f6575A.f6581g.h("SignalGeneratorImpl.initializeWebViewForSignalCollection", th);
        C0208b c0208b = (C0208b) this.f633j;
        Zv zv = c0208b.f1352v;
        Pair pair = new Pair("sgf_reason", th.getMessage());
        Pair pair2 = new Pair("se", "query_g");
        Pair pair3 = new Pair("ad_format", "BANNER");
        Pair pair4 = new Pair("rtype", Integer.toString(6));
        Pair pair5 = new Pair("scar", "true");
        AtomicInteger atomicInteger = c0208b.f1339N;
        K1.w.c(zv, "sgf", pair, pair2, pair3, pair4, pair5, new Pair("sgi_rn", Integer.toString(atomicInteger.get())));
        E1.m.e("Failed to initialize webview for loading SDKCore. ", th);
        vb vbVar = Gb.I8;
        A1.r rVar = A1.r.f168d;
        if (((Boolean) rVar.f171c.a(vbVar)).booleanValue() && !c0208b.f1338M.get() && atomicInteger.getAndIncrement() < ((Integer) rVar.f171c.a(Gb.J8)).intValue()) {
            c0208b.E4();
        }
    }

    public E(Context context) {
        this.f633j = new SQLiteOpenHelper(context, "CUTTER_DB", (SQLiteDatabase.CursorFactory) null, 1).getWritableDatabase();
    }
}
