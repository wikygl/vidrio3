package A1;

import C1.C0149c;
import X.a;
import X.g;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import com.google.android.gms.internal.ads.DM;
import com.google.android.gms.internal.ads.Mt;
import com.google.android.gms.internal.ads.O4;
import com.google.android.gms.internal.ads.Op;
import com.google.android.gms.internal.ads.P4;
import com.google.android.gms.internal.ads.Pc;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.U4;
import com.google.android.gms.internal.ads.Ur;
import com.google.android.gms.internal.ads.Wp;
import com.google.android.gms.internal.ads.X4;
import com.google.android.gms.internal.ads.bI;
import com.google.android.gms.internal.ads.bu;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.ks;
import com.google.android.gms.internal.ads.ne;
import com.google.android.gms.internal.ads.o3;
import com.google.android.gms.internal.ads.vK;
import com.google.android.gms.internal.ads.xI;
import com.google.android.gms.internal.ads.xk;
import d.C0376a;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.json.JSONObject;

/* renamed from: A1.l0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0117l0 implements O4, TN, Ur, Pc {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f147j;

    /* renamed from: k  reason: collision with root package name */
    public Object f148k;

    /* renamed from: l  reason: collision with root package name */
    public Object f149l;

    public JSONObject a() {
        return ((bu) this.f148k).n();
    }

    public JSONObject c() {
        return ((bu) this.f148k).p();
    }

    public void d(Object obj) {
        ((ks) obj).a((String) this.f148k, (String) this.f149l);
    }

    public void e() {
        DM dm = Mt.y;
        bu buVar = (bu) this.f148k;
        Map o4 = buVar.o();
        if (o4 != null) {
            int i4 = dm.m;
            int i5 = 0;
            while (i5 < i4) {
                Object obj = o4.get((String) dm.get(i5));
                i5++;
                if (obj != null) {
                    buVar.onClick((ViewGroup) this.f149l);
                    return;
                }
            }
        }
    }

    public void f(xI xIVar) {
        if (xIVar.v() == 0 && (xIVar.v() & 128) != 0) {
            xIVar.j(6);
            int n4 = xIVar.n() / 4;
            int i4 = 0;
            while (true) {
                U4 u4 = (U4) this.f149l;
                if (i4 < n4) {
                    bI bIVar = (bI) this.f148k;
                    xIVar.e((byte[]) bIVar.d, 0, 4);
                    bIVar.h(0);
                    int d4 = bIVar.d(16);
                    bIVar.j(3);
                    if (d4 == 0) {
                        bIVar.j(13);
                    } else {
                        int d5 = bIVar.d(13);
                        if (u4.e.get(d5) == null) {
                            u4.e.put(d5, new P4(new o3(u4, d5)));
                        }
                    }
                    i4++;
                } else {
                    u4.e.remove(0);
                    return;
                }
            }
        }
    }

    public /* bridge */ /* synthetic */ void g(Object obj) {
        switch (this.f147j) {
            case 6:
                xk.e.execute(new com.google.android.gms.internal.ads.H(5, (Wp) this.f149l));
                ((TN) this.f148k).g((Op) obj);
                return;
            default:
                ((em) obj).W((String) this.f148k, (ne) this.f149l);
                return;
        }
    }

    public void h(MotionEvent motionEvent) {
        ((bu) this.f148k).onTouch(null, motionEvent);
    }

    public KeyListener i(KeyListener keyListener) {
        if (!(keyListener instanceof NumberKeyListener)) {
            ((X.a) this.f149l).f2780a.getClass();
            if (!(keyListener instanceof X.e)) {
                if (keyListener == null) {
                    return null;
                }
                if (!(keyListener instanceof NumberKeyListener)) {
                    return new X.e(keyListener);
                }
                return keyListener;
            }
            return keyListener;
        }
        return keyListener;
    }

    public void j(AttributeSet attributeSet, int i4) {
        TypedArray obtainStyledAttributes = ((EditText) this.f148k).getContext().obtainStyledAttributes(attributeSet, C0376a.f3136i, i4, 0);
        try {
            boolean z4 = true;
            if (obtainStyledAttributes.hasValue(14)) {
                z4 = obtainStyledAttributes.getBoolean(14, true);
            }
            obtainStyledAttributes.recycle();
            l(z4);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    public X.c k(InputConnection inputConnection, EditorInfo editorInfo) {
        X.a aVar = (X.a) this.f149l;
        if (inputConnection == null) {
            aVar.getClass();
            inputConnection = null;
        } else {
            a.C0033a c0033a = aVar.f2780a;
            c0033a.getClass();
            if (!(inputConnection instanceof X.c)) {
                inputConnection = new X.c(c0033a.f2781a, inputConnection, editorInfo);
            }
        }
        return (X.c) inputConnection;
    }

    public void l(boolean z4) {
        X.g gVar = ((X.a) this.f149l).f2780a.f2782b;
        if (gVar.f2802m != z4) {
            if (gVar.f2801l != null) {
                androidx.emoji2.text.f a4 = androidx.emoji2.text.f.a();
                g.a aVar = gVar.f2801l;
                a4.getClass();
                C0149c.e(aVar, "initCallback cannot be null");
                ReentrantReadWriteLock reentrantReadWriteLock = a4.a;
                reentrantReadWriteLock.writeLock().lock();
                try {
                    a4.b.remove(aVar);
                } finally {
                    reentrantReadWriteLock.writeLock().unlock();
                }
            }
            gVar.f2802m = z4;
            if (z4) {
                X.g.a(gVar.f2799j, androidx.emoji2.text.f.a().b());
            }
        }
    }

    public void m(Throwable th) {
        switch (this.f147j) {
            case 6:
                xk.e.execute(new com.google.android.gms.internal.ads.H(5, (Wp) this.f149l));
                ((TN) this.f148k).m(th);
                return;
            default:
                return;
        }
    }

    public synchronized String o() {
        return (String) this.f148k;
    }

    public String toString() {
        switch (this.f147j) {
            case 0:
                return (String) this.f148k;
            default:
                return super.toString();
        }
    }

    public C0117l0(U4 u4) {
        this.f147j = 2;
        this.f149l = u4;
        this.f148k = new bI(4, new byte[4]);
    }

    public /* synthetic */ C0117l0(Object obj, int i4, Object obj2) {
        this.f147j = i4;
        this.f148k = obj;
        this.f149l = obj2;
    }

    public /* synthetic */ C0117l0(Object obj, Object obj2, int i4, boolean z4) {
        this.f147j = i4;
        this.f148k = obj2;
        this.f149l = obj;
    }

    public C0117l0(InterfaceC0115k0 interfaceC0115k0) {
        String str;
        this.f147j = 0;
        this.f149l = interfaceC0115k0;
        try {
            str = interfaceC0115k0.b();
        } catch (RemoteException e4) {
            E1.m.e("", e4);
            str = null;
        }
        this.f148k = str;
    }

    public C0117l0(EditText editText) {
        this.f147j = 11;
        this.f148k = editText;
        this.f149l = new X.a(editText);
    }

    public C0117l0(ArrayList arrayList, ArrayList arrayList2) {
        this.f147j = 1;
        int size = arrayList.size();
        this.f148k = new int[size];
        this.f149l = new float[size];
        for (int i4 = 0; i4 < size; i4++) {
            ((int[]) this.f148k)[i4] = ((Integer) arrayList.get(i4)).intValue();
            ((float[]) this.f149l)[i4] = ((Float) arrayList2.get(i4)).floatValue();
        }
    }

    public C0117l0(int i4, int i5) {
        this.f147j = 1;
        this.f148k = new int[]{i4, i5};
        this.f149l = new float[]{0.0f, 1.0f};
    }

    public C0117l0(int i4, int i5, int i6) {
        this.f147j = 1;
        this.f148k = new int[]{i4, i5, i6};
        this.f149l = new float[]{0.0f, 0.5f, 1.0f};
    }

    private final void n(Throwable th) {
    }

    public void b(vK vKVar, com.google.android.gms.internal.ads.n0 n0Var, X4 x4) {
    }
}
