package C1;

import V1.InterfaceC0304j;
import W1.C0327o;
import android.os.Parcel;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.nK;
import g2.C0426b;
import i2.Z;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import p2.C0758g;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class A implements InterfaceC0304j, X2.e, Z {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f351j;

    /* renamed from: k  reason: collision with root package name */
    public Object f352k;

    public A() {
        this.f351j = 4;
    }

    @Override // i2.Z
    public Object a() {
        Z z4 = (Z) this.f352k;
        if (z4 != null) {
            return z4.a();
        }
        throw new IllegalStateException();
    }

    public void b(nK nKVar) {
        B b4 = (B) this.f352k;
        b4.getClass();
        String str = nKVar.b;
        if (!TextUtils.isEmpty(str)) {
            if (!((Boolean) A1.r.f168d.f171c.a(Gb.ka)).booleanValue()) {
                b4.f353a = str;
            }
        }
        int i4 = nKVar.a;
        switch (i4) {
            case 8152:
                b4.a("onLMDOverlayOpened", new HashMap());
                return;
            case 8153:
                b4.a("onLMDOverlayClicked", new HashMap());
                return;
            case 8154:
            case 8156:
            case 8158:
            case 8159:
            default:
                return;
            case 8155:
                b4.a("onLMDOverlayClose", new HashMap());
                return;
            case 8157:
                b4.f353a = null;
                b4.f354b = null;
                b4.f357e = false;
                return;
            case 8160:
            case 8161:
            case 8162:
                HashMap hashMap = new HashMap();
                hashMap.put("error", String.valueOf(i4));
                b4.a("onLMDOverlayFailedToOpen", hashMap);
                return;
        }
    }

    @Override // X2.e
    public void c(X2.a aVar) {
        ((AtomicReference) this.f352k).set(aVar);
    }

    @Override // V1.InterfaceC0304j
    public void d(Object obj, Object obj2) {
        C0758g c0758g = (C0758g) obj2;
        U1.a aVar = Y1.c.f2829i;
        Y1.a aVar2 = (Y1.a) ((Y1.d) obj).w();
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken(aVar2.f3497k);
        int i4 = C0426b.f3498a;
        C0327o c0327o = (C0327o) this.f352k;
        if (c0327o == null) {
            obtain.writeInt(0);
        } else {
            obtain.writeInt(1);
            c0327o.writeToParcel(obtain, 0);
        }
        try {
            aVar2.f3496j.transact(1, obtain, null, 1);
            obtain.recycle();
            c0758g.f5552a.m(null);
        } catch (Throwable th) {
            obtain.recycle();
            throw th;
        }
    }

    public String toString() {
        switch (this.f351j) {
            case 1:
                return "<" + ((String) this.f352k) + '>';
            default:
                return super.toString();
        }
    }

    public /* synthetic */ A(int i4, Object obj) {
        this.f351j = i4;
        this.f352k = obj;
    }
}
