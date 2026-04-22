package W1;

import W1.InterfaceC0320h;
import android.accounts.Account;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.Scope;
import h2.C0438a;

/* renamed from: W1.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0317e extends X1.a {
    public static final Parcelable.Creator<C0317e> CREATOR = new Object();

    /* renamed from: x  reason: collision with root package name */
    public static final Scope[] f2723x = new Scope[0];

    /* renamed from: y  reason: collision with root package name */
    public static final T1.d[] f2724y = new T1.d[0];

    /* renamed from: j  reason: collision with root package name */
    public final int f2725j;

    /* renamed from: k  reason: collision with root package name */
    public final int f2726k;

    /* renamed from: l  reason: collision with root package name */
    public final int f2727l;

    /* renamed from: m  reason: collision with root package name */
    public String f2728m;

    /* renamed from: n  reason: collision with root package name */
    public IBinder f2729n;

    /* renamed from: o  reason: collision with root package name */
    public Scope[] f2730o;

    /* renamed from: p  reason: collision with root package name */
    public Bundle f2731p;

    /* renamed from: q  reason: collision with root package name */
    public Account f2732q;

    /* renamed from: r  reason: collision with root package name */
    public T1.d[] f2733r;

    /* renamed from: s  reason: collision with root package name */
    public T1.d[] f2734s;

    /* renamed from: t  reason: collision with root package name */
    public final boolean f2735t;

    /* renamed from: u  reason: collision with root package name */
    public final int f2736u;

    /* renamed from: v  reason: collision with root package name */
    public boolean f2737v;

    /* renamed from: w  reason: collision with root package name */
    public final String f2738w;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v4, types: [W1.h] */
    /* JADX WARN: Type inference failed for: r4v7 */
    public C0317e(int i4, int i5, int i6, String str, IBinder iBinder, Scope[] scopeArr, Bundle bundle, Account account, T1.d[] dVarArr, T1.d[] dVarArr2, boolean z4, int i7, boolean z5, String str2) {
        InterfaceC0320h interfaceC0320h;
        scopeArr = scopeArr == null ? f2723x : scopeArr;
        bundle = bundle == null ? new Bundle() : bundle;
        T1.d[] dVarArr3 = f2724y;
        dVarArr = dVarArr == null ? dVarArr3 : dVarArr;
        dVarArr2 = dVarArr2 == null ? dVarArr3 : dVarArr2;
        this.f2725j = i4;
        this.f2726k = i5;
        this.f2727l = i6;
        if ("com.google.android.gms".equals(str)) {
            this.f2728m = "com.google.android.gms";
        } else {
            this.f2728m = str;
        }
        if (i4 < 2) {
            Account account2 = null;
            if (iBinder != null) {
                int i8 = InterfaceC0320h.a.f2744j;
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
                if (queryLocalInterface instanceof InterfaceC0320h) {
                    interfaceC0320h = (InterfaceC0320h) queryLocalInterface;
                } else {
                    interfaceC0320h = new C0438a(iBinder, "com.google.android.gms.common.internal.IAccountAccessor");
                }
                int i9 = BinderC0313a.f2679k;
                if (interfaceC0320h != 0) {
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    try {
                        try {
                            account2 = interfaceC0320h.c();
                        } catch (RemoteException unused) {
                            Log.w("AccountAccessor", "Remote account accessor probably died");
                        }
                    } finally {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                }
            }
            this.f2732q = account2;
        } else {
            this.f2729n = iBinder;
            this.f2732q = account;
        }
        this.f2730o = scopeArr;
        this.f2731p = bundle;
        this.f2733r = dVarArr;
        this.f2734s = dVarArr2;
        this.f2735t = z4;
        this.f2736u = i7;
        this.f2737v = z5;
        this.f2738w = str2;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        U.a(this, parcel, i4);
    }
}
