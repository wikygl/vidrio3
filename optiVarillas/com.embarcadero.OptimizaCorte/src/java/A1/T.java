package A1;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.Eh;
import com.google.android.gms.internal.ads.Fh;
import com.google.android.gms.internal.ads.Gh;
import com.google.android.gms.internal.ads.Xj;
import com.google.android.gms.internal.ads.Yj;
import com.google.android.gms.internal.ads.Zj;
import com.google.android.gms.internal.ads.bd;
import com.google.android.gms.internal.ads.cd;
import com.google.android.gms.internal.ads.cj;
import com.google.android.gms.internal.ads.dd;
import com.google.android.gms.internal.ads.dj;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.ej;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.xh;
import com.google.android.gms.internal.ads.yh;
import com.google.android.gms.internal.ads.z8;
import com.google.android.gms.internal.ads.zh;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class T extends x8 implements V {
    @Override // A1.V
    public final zh D1(InterfaceC0374a interfaceC0374a, eg egVar, int i4) {
        zh xhVar;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        z8.e(B4, egVar);
        B4.writeInt(241199000);
        Parcel Z3 = Z(B4, 15);
        IBinder readStrongBinder = Z3.readStrongBinder();
        int i5 = yh.j;
        if (readStrongBinder == null) {
            xhVar = null;
        } else {
            zh queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.offline.IOfflineUtils");
            if (queryLocalInterface instanceof zh) {
                xhVar = queryLocalInterface;
            } else {
                xhVar = new xh(readStrongBinder);
            }
        }
        Z3.recycle();
        return xhVar;
    }

    @Override // A1.V
    public final L I3(InterfaceC0374a interfaceC0374a, C1 c12, String str, int i4) {
        L j4;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        z8.c(B4, c12);
        B4.writeString(str);
        B4.writeInt(241199000);
        Parcel Z3 = Z(B4, 10);
        IBinder readStrongBinder = Z3.readStrongBinder();
        if (readStrongBinder == null) {
            j4 = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdManager");
            if (queryLocalInterface instanceof L) {
                j4 = (L) queryLocalInterface;
            } else {
                j4 = new J(readStrongBinder);
            }
        }
        Z3.recycle();
        return j4;
    }

    @Override // A1.V
    public final L N1(InterfaceC0374a interfaceC0374a, C1 c12, String str, eg egVar, int i4) {
        L j4;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        z8.c(B4, c12);
        B4.writeString(str);
        z8.e(B4, egVar);
        B4.writeInt(241199000);
        Parcel Z3 = Z(B4, 1);
        IBinder readStrongBinder = Z3.readStrongBinder();
        if (readStrongBinder == null) {
            j4 = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdManager");
            if (queryLocalInterface instanceof L) {
                j4 = (L) queryLocalInterface;
            } else {
                j4 = new J(readStrongBinder);
            }
        }
        Z3.recycle();
        return j4;
    }

    @Override // A1.V
    public final dd O3(InterfaceC0374a interfaceC0374a, InterfaceC0374a interfaceC0374a2) {
        dd bdVar;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        z8.e(B4, interfaceC0374a2);
        Parcel Z3 = Z(B4, 5);
        IBinder readStrongBinder = Z3.readStrongBinder();
        int i4 = cd.j;
        if (readStrongBinder == null) {
            bdVar = null;
        } else {
            dd queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.formats.client.INativeAdViewDelegate");
            if (queryLocalInterface instanceof dd) {
                bdVar = queryLocalInterface;
            } else {
                bdVar = new bd(readStrongBinder);
            }
        }
        Z3.recycle();
        return bdVar;
    }

    @Override // A1.V
    public final L P0(InterfaceC0374a interfaceC0374a, C1 c12, String str, eg egVar, int i4) {
        L j4;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        z8.c(B4, c12);
        B4.writeString(str);
        z8.e(B4, egVar);
        B4.writeInt(241199000);
        Parcel Z3 = Z(B4, 2);
        IBinder readStrongBinder = Z3.readStrongBinder();
        if (readStrongBinder == null) {
            j4 = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdManager");
            if (queryLocalInterface instanceof L) {
                j4 = (L) queryLocalInterface;
            } else {
                j4 = new J(readStrongBinder);
            }
        }
        Z3.recycle();
        return j4;
    }

    @Override // A1.V
    public final Gh X(InterfaceC0374a interfaceC0374a) {
        Gh eh;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        Parcel Z3 = Z(B4, 8);
        IBinder readStrongBinder = Z3.readStrongBinder();
        int i4 = Fh.j;
        if (readStrongBinder == null) {
            eh = null;
        } else {
            Gh queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.overlay.client.IAdOverlay");
            if (queryLocalInterface instanceof Gh) {
                eh = queryLocalInterface;
            } else {
                eh = new Eh(readStrongBinder);
            }
        }
        Z3.recycle();
        return eh;
    }

    @Override // A1.V
    public final Zj Z1(InterfaceC0374a interfaceC0374a, eg egVar, int i4) {
        Zj xj;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        z8.e(B4, egVar);
        B4.writeInt(241199000);
        Parcel Z3 = Z(B4, 14);
        IBinder readStrongBinder = Z3.readStrongBinder();
        int i5 = Yj.j;
        if (readStrongBinder == null) {
            xj = null;
        } else {
            Zj queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.signals.ISignalGenerator");
            if (queryLocalInterface instanceof Zj) {
                xj = queryLocalInterface;
            } else {
                xj = new Xj(readStrongBinder);
            }
        }
        Z3.recycle();
        return xj;
    }

    @Override // A1.V
    public final InterfaceC0100f0 e0(InterfaceC0374a interfaceC0374a, int i4) {
        InterfaceC0100f0 c0094d0;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        B4.writeInt(241199000);
        Parcel Z3 = Z(B4, 9);
        IBinder readStrongBinder = Z3.readStrongBinder();
        if (readStrongBinder == null) {
            c0094d0 = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IMobileAdsSettingManager");
            if (queryLocalInterface instanceof InterfaceC0100f0) {
                c0094d0 = (InterfaceC0100f0) queryLocalInterface;
            } else {
                c0094d0 = new C0094d0(readStrongBinder);
            }
        }
        Z3.recycle();
        return c0094d0;
    }

    @Override // A1.V
    public final L p2(InterfaceC0374a interfaceC0374a, C1 c12, String str, eg egVar, int i4) {
        L j4;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        z8.c(B4, c12);
        B4.writeString(str);
        z8.e(B4, egVar);
        B4.writeInt(241199000);
        Parcel Z3 = Z(B4, 13);
        IBinder readStrongBinder = Z3.readStrongBinder();
        if (readStrongBinder == null) {
            j4 = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdManager");
            if (queryLocalInterface instanceof L) {
                j4 = (L) queryLocalInterface;
            } else {
                j4 = new J(readStrongBinder);
            }
        }
        Z3.recycle();
        return j4;
    }

    @Override // A1.V
    public final G w3(InterfaceC0374a interfaceC0374a, String str, eg egVar, int i4) {
        G e4;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        B4.writeString(str);
        z8.e(B4, egVar);
        B4.writeInt(241199000);
        Parcel Z3 = Z(B4, 3);
        IBinder readStrongBinder = Z3.readStrongBinder();
        if (readStrongBinder == null) {
            e4 = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdLoaderBuilder");
            if (queryLocalInterface instanceof G) {
                e4 = (G) queryLocalInterface;
            } else {
                e4 = new E(readStrongBinder);
            }
        }
        Z3.recycle();
        return e4;
    }

    @Override // A1.V
    public final InterfaceC0140x0 y2(InterfaceC0374a interfaceC0374a, eg egVar, int i4) {
        InterfaceC0140x0 c0136v0;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        z8.e(B4, egVar);
        B4.writeInt(241199000);
        Parcel Z3 = Z(B4, 17);
        IBinder readStrongBinder = Z3.readStrongBinder();
        if (readStrongBinder == null) {
            c0136v0 = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IOutOfContextTester");
            if (queryLocalInterface instanceof InterfaceC0140x0) {
                c0136v0 = (InterfaceC0140x0) queryLocalInterface;
            } else {
                c0136v0 = new C0136v0(readStrongBinder);
            }
        }
        Z3.recycle();
        return c0136v0;
    }

    @Override // A1.V
    public final ej z1(InterfaceC0374a interfaceC0374a, String str, eg egVar, int i4) {
        ej cjVar;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        B4.writeString(str);
        z8.e(B4, egVar);
        B4.writeInt(241199000);
        Parcel Z3 = Z(B4, 12);
        IBinder readStrongBinder = Z3.readStrongBinder();
        int i5 = dj.j;
        if (readStrongBinder == null) {
            cjVar = null;
        } else {
            ej queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.rewarded.client.IRewardedAd");
            if (queryLocalInterface instanceof ej) {
                cjVar = queryLocalInterface;
            } else {
                cjVar = new cj(readStrongBinder);
            }
        }
        Z3.recycle();
        return cjVar;
    }
}
