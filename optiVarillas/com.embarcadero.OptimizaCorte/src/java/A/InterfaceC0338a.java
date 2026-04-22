package a;

import K1.q;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import o.C0745a;
import o.c;
import o.d;
import o.e;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: a.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public interface InterfaceC0338a extends IInterface {

    /* renamed from: a.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static abstract class AbstractBinderC0036a extends Binder implements InterfaceC0338a {
        @Override // android.os.Binder
        public final boolean onTransact(int i4, Parcel parcel, Parcel parcel2, int i5) {
            Uri uri;
            boolean z4;
            Bundle bundle;
            if (i4 != 1598968902) {
                Bundle bundle2 = null;
                switch (i4) {
                    case 2:
                        parcel.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                        int readInt = parcel.readInt();
                        if (parcel.readInt() != 0) {
                            bundle2 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        }
                        e eVar = (e) this;
                        if (eVar.f5401k != null) {
                            eVar.f5400j.post(new o.b(eVar, readInt, bundle2));
                        }
                        parcel2.writeNoException();
                        return true;
                    case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                        parcel.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                        String readString = parcel.readString();
                        if (parcel.readInt() != 0) {
                            bundle2 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        }
                        e eVar2 = (e) this;
                        if (eVar2.f5401k != null) {
                            eVar2.f5400j.post(new c(eVar2, readString, bundle2));
                        }
                        parcel2.writeNoException();
                        return true;
                    case 4:
                        parcel.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                        if (parcel.readInt() != 0) {
                            bundle2 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        }
                        e eVar3 = (e) this;
                        if (eVar3.f5401k != null) {
                            eVar3.f5400j.post(new E0.a(eVar3, 9, bundle2));
                        }
                        parcel2.writeNoException();
                        return true;
                    case 5:
                        parcel.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                        String readString2 = parcel.readString();
                        if (parcel.readInt() != 0) {
                            bundle2 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        }
                        e eVar4 = (e) this;
                        if (eVar4.f5401k != null) {
                            eVar4.f5400j.post(new q(eVar4, readString2, bundle2));
                        }
                        parcel2.writeNoException();
                        return true;
                    case 6:
                        parcel.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                        int readInt2 = parcel.readInt();
                        if (parcel.readInt() != 0) {
                            uri = (Uri) Uri.CREATOR.createFromParcel(parcel);
                        } else {
                            uri = null;
                        }
                        if (parcel.readInt() != 0) {
                            z4 = true;
                        } else {
                            z4 = false;
                        }
                        if (parcel.readInt() != 0) {
                            bundle2 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        }
                        Bundle bundle3 = bundle2;
                        e eVar5 = (e) this;
                        if (eVar5.f5401k != null) {
                            eVar5.f5400j.post(new d(eVar5, readInt2, uri, z4, bundle3));
                        }
                        parcel2.writeNoException();
                        return true;
                    case 7:
                        parcel.enforceInterface("android.support.customtabs.ICustomTabsCallback");
                        String readString3 = parcel.readString();
                        if (parcel.readInt() != 0) {
                            bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                        } else {
                            bundle = null;
                        }
                        C0745a c0745a = ((e) this).f5401k;
                        if (c0745a != null) {
                            bundle2 = c0745a.b(readString3, bundle);
                        }
                        parcel2.writeNoException();
                        if (bundle2 != null) {
                            parcel2.writeInt(1);
                            bundle2.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    default:
                        return super.onTransact(i4, parcel, parcel2, i5);
                }
            }
            parcel2.writeString("android.support.customtabs.ICustomTabsCallback");
            return true;
        }

        @Override // android.os.IInterface
        public final IBinder asBinder() {
            return this;
        }
    }
}
