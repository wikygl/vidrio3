package A1;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.view.View;
import c2.InterfaceC0374a;
import com.google.android.gms.ads.internal.ClientApi;
import com.google.android.gms.internal.ads.Fe;
import com.google.android.gms.internal.ads.Gh;
import com.google.android.gms.internal.ads.I4;
import com.google.android.gms.internal.ads.Lt;
import com.google.android.gms.internal.ads.Mm;
import com.google.android.gms.internal.ads.Zj;
import com.google.android.gms.internal.ads.dd;
import com.google.android.gms.internal.ads.dg;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.ej;
import com.google.android.gms.internal.ads.kG;
import com.google.android.gms.internal.ads.kw;
import com.google.android.gms.internal.ads.pX;
import com.google.android.gms.internal.ads.pn;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;
import com.google.android.gms.internal.ads.zh;
import com.google.android.gms.internal.ads.zn;
import e0.C0405a;
import java.util.HashMap;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class U extends y8 implements V {
    public U() {
        super("com.google.android.gms.ads.internal.client.IClientApi");
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        Fe fe = null;
        switch (i4) {
            case 1:
                InterfaceC0374a Z3 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                C1 c12 = (C1) z8.a(parcel, C1.CREATOR);
                String readString = parcel.readString();
                eg C4 = dg.C4(parcel.readStrongBinder());
                int readInt = parcel.readInt();
                z8.b(parcel);
                L N12 = ((ClientApi) this).N1(Z3, c12, readString, C4, readInt);
                parcel2.writeNoException();
                z8.e(parcel2, N12);
                return true;
            case 2:
                InterfaceC0374a Z4 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                C1 c13 = (C1) z8.a(parcel, C1.CREATOR);
                String readString2 = parcel.readString();
                eg C42 = dg.C4(parcel.readStrongBinder());
                int readInt2 = parcel.readInt();
                z8.b(parcel);
                L P0 = ((ClientApi) this).P0(Z4, c13, readString2, C42, readInt2);
                parcel2.writeNoException();
                z8.e(parcel2, P0);
                return true;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                InterfaceC0374a Z5 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                String readString3 = parcel.readString();
                eg C43 = dg.C4(parcel.readStrongBinder());
                int readInt3 = parcel.readInt();
                z8.b(parcel);
                G w32 = ((ClientApi) this).w3(Z5, readString3, C43, readInt3);
                parcel2.writeNoException();
                z8.e(parcel2, w32);
                return true;
            case 4:
                InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                z8.b(parcel);
                parcel2.writeNoException();
                parcel2.writeStrongBinder(null);
                return true;
            case 5:
                dd O3 = ((ClientApi) this).O3(InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder()), C0405a.b(parcel, parcel));
                parcel2.writeNoException();
                z8.e(parcel2, O3);
                return true;
            case 6:
                InterfaceC0374a Z6 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                eg C44 = dg.C4(parcel.readStrongBinder());
                int readInt4 = parcel.readInt();
                z8.b(parcel);
                Context context = (Context) c2.b.p0(Z6);
                I4 o4 = Mm.c(context, C44, readInt4).o();
                context.getClass();
                o4.k = context;
                parcel2.writeNoException();
                z8.e(parcel2, (kG) ((pX) o4.a().k).c());
                return true;
            case 7:
                InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                z8.b(parcel);
                parcel2.writeNoException();
                parcel2.writeStrongBinder(null);
                return true;
            case 8:
                Gh X3 = ((ClientApi) this).X(C0405a.b(parcel, parcel));
                parcel2.writeNoException();
                z8.e(parcel2, X3);
                return true;
            case 9:
                InterfaceC0374a Z7 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                int readInt5 = parcel.readInt();
                z8.b(parcel);
                InterfaceC0100f0 e02 = ((ClientApi) this).e0(Z7, readInt5);
                parcel2.writeNoException();
                z8.e(parcel2, e02);
                return true;
            case 10:
                String readString4 = parcel.readString();
                int readInt6 = parcel.readInt();
                z8.b(parcel);
                L I3 = ((ClientApi) this).I3(InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder()), (C1) z8.a(parcel, C1.CREATOR), readString4, readInt6);
                parcel2.writeNoException();
                z8.e(parcel2, I3);
                return true;
            case 11:
                Lt lt = new Lt((View) c2.b.p0(InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder())), (HashMap) c2.b.p0(InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder())), (HashMap) c2.b.p0(C0405a.b(parcel, parcel)));
                parcel2.writeNoException();
                z8.e(parcel2, lt);
                return true;
            case 12:
                InterfaceC0374a Z8 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                String readString5 = parcel.readString();
                eg C45 = dg.C4(parcel.readStrongBinder());
                int readInt7 = parcel.readInt();
                z8.b(parcel);
                ej z12 = ((ClientApi) this).z1(Z8, readString5, C45, readInt7);
                parcel2.writeNoException();
                z8.e(parcel2, z12);
                return true;
            case 13:
                InterfaceC0374a Z9 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                C1 c14 = (C1) z8.a(parcel, C1.CREATOR);
                String readString6 = parcel.readString();
                eg C46 = dg.C4(parcel.readStrongBinder());
                int readInt8 = parcel.readInt();
                z8.b(parcel);
                L p22 = ((ClientApi) this).p2(Z9, c14, readString6, C46, readInt8);
                parcel2.writeNoException();
                z8.e(parcel2, p22);
                return true;
            case 14:
                InterfaceC0374a Z10 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                eg C47 = dg.C4(parcel.readStrongBinder());
                int readInt9 = parcel.readInt();
                z8.b(parcel);
                Zj Z12 = ((ClientApi) this).Z1(Z10, C47, readInt9);
                parcel2.writeNoException();
                z8.e(parcel2, Z12);
                return true;
            case 15:
                InterfaceC0374a Z11 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                eg C48 = dg.C4(parcel.readStrongBinder());
                int readInt10 = parcel.readInt();
                z8.b(parcel);
                zh D12 = ((ClientApi) this).D1(Z11, C48, readInt10);
                parcel2.writeNoException();
                z8.e(parcel2, D12);
                return true;
            case 16:
                InterfaceC0374a Z13 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                eg C49 = dg.C4(parcel.readStrongBinder());
                int readInt11 = parcel.readInt();
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder != null) {
                    Fe queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.h5.client.IH5AdsEventListener");
                    if (queryLocalInterface instanceof Fe) {
                        fe = queryLocalInterface;
                    } else {
                        fe = new x8(readStrongBinder, "com.google.android.gms.ads.internal.h5.client.IH5AdsEventListener");
                    }
                }
                z8.b(parcel);
                Context context2 = (Context) c2.b.p0(Z13);
                pn c4 = Mm.c(context2, C49, readInt11);
                context2.getClass();
                fe.getClass();
                parcel2.writeNoException();
                z8.e(parcel2, (kw) new zn(c4.c, context2, fe).e.c());
                return true;
            case 17:
                InterfaceC0374a Z14 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                eg C410 = dg.C4(parcel.readStrongBinder());
                int readInt12 = parcel.readInt();
                z8.b(parcel);
                InterfaceC0140x0 y22 = ((ClientApi) this).y2(Z14, C410, readInt12);
                parcel2.writeNoException();
                z8.e(parcel2, y22);
                return true;
            default:
                return false;
        }
    }
}
