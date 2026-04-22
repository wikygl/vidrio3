package a;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import o.e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public interface b extends IInterface {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static abstract class a extends Binder implements b {

        /* renamed from: j  reason: collision with root package name */
        public static final /* synthetic */ int f2854j = 0;

        /* renamed from: a.b$a$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
        public static class C0037a implements b {

            /* renamed from: j  reason: collision with root package name */
            public IBinder f2855j;

            @Override // android.os.IInterface
            public final IBinder asBinder() {
                return this.f2855j;
            }

            @Override // a.b
            public final int e2(e eVar, String str, Bundle bundle) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeStrongBinder(eVar);
                    obtain.writeString(str);
                    obtain.writeInt(1);
                    bundle.writeToParcel(obtain, 0);
                    if (!this.f2855j.transact(8, obtain, obtain2, 0)) {
                        int i4 = a.f2854j;
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // a.b
            public final boolean f4() {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeLong(0L);
                    boolean z4 = false;
                    if (!this.f2855j.transact(2, obtain, obtain2, 0)) {
                        int i4 = a.f2854j;
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z4 = true;
                    }
                    return z4;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // a.b
            public final boolean r1(e eVar) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeStrongBinder(eVar);
                    boolean z4 = false;
                    if (!this.f2855j.transact(3, obtain, obtain2, 0)) {
                        int i4 = a.f2854j;
                    }
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z4 = true;
                    }
                    return z4;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // a.b
            public final boolean r2(e eVar, Uri uri) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeStrongBinder(eVar);
                    boolean z4 = true;
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (!this.f2855j.transact(7, obtain, obtain2, 0)) {
                        int i4 = a.f2854j;
                    }
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z4 = false;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z4;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                    throw th;
                }
            }

            @Override // a.b
            public final boolean z2(e eVar, Uri uri, Bundle bundle) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.customtabs.ICustomTabsService");
                    obtain.writeStrongBinder(eVar);
                    boolean z4 = true;
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(1);
                    bundle.writeToParcel(obtain, 0);
                    if (!this.f2855j.transact(11, obtain, obtain2, 0)) {
                        int i4 = a.f2854j;
                    }
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z4 = false;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z4;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                    throw th;
                }
            }
        }
    }

    int e2(e eVar, String str, Bundle bundle);

    boolean f4();

    boolean r1(e eVar);

    boolean r2(e eVar, Uri uri);

    boolean z2(e eVar, Uri uri, Bundle bundle);
}
