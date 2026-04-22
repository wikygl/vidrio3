package o2;

import A1.Q0;
import U1.d;
import V1.G;
import W1.A;
import W1.AbstractC0314b;
import W1.AbstractC0318f;
import W1.C0315c;
import W1.C0324l;
import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import g2.C0425a;
import g2.C0426b;
import org.json.JSONException;

/* renamed from: o2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0748a extends AbstractC0318f<g> implements n2.f {

    /* renamed from: A  reason: collision with root package name */
    public final boolean f5486A;

    /* renamed from: B  reason: collision with root package name */
    public final C0315c f5487B;

    /* renamed from: C  reason: collision with root package name */
    public final Bundle f5488C;

    /* renamed from: D  reason: collision with root package name */
    public final Integer f5489D;

    public C0748a(Context context, Looper looper, C0315c c0315c, Bundle bundle, d.a aVar, d.b bVar) {
        super(context, looper, 44, c0315c, aVar, bVar);
        this.f5486A = true;
        this.f5487B = c0315c;
        this.f5488C = bundle;
        this.f5489D = c0315c.f2712h;
    }

    @Override // W1.AbstractC0314b, U1.a.e
    public final int f() {
        return 12451000;
    }

    @Override // n2.f
    public final void j(f fVar) {
        GoogleSignInAccount googleSignInAccount;
        C0324l.e(fVar, "Expecting a valid ISignInCallbacks");
        try {
            Account account = this.f5487B.f2706a;
            if (account == null) {
                account = new Account("<<default account>>", "com.google");
            }
            if ("<<default account>>".equals(account.name)) {
                S1.a a4 = S1.a.a(this.f2685c);
                String b4 = a4.b("defaultGoogleSignInAccount");
                if (!TextUtils.isEmpty(b4)) {
                    String b5 = a4.b("googleSignInAccount:" + b4);
                    if (b5 != null) {
                        try {
                            googleSignInAccount = GoogleSignInAccount.i(b5);
                        } catch (JSONException unused) {
                        }
                        Integer num = this.f5489D;
                        C0324l.d(num);
                        A a5 = new A(2, account, num.intValue(), googleSignInAccount);
                        g gVar = (g) w();
                        j jVar = new j(1, a5);
                        Parcel obtain = Parcel.obtain();
                        obtain.writeInterfaceToken(gVar.f3497k);
                        int i4 = C0426b.f3498a;
                        obtain.writeInt(1);
                        jVar.writeToParcel(obtain, 0);
                        obtain.writeStrongBinder(fVar.asBinder());
                        Parcel obtain2 = Parcel.obtain();
                        gVar.f3496j.transact(12, obtain, obtain2, 0);
                        obtain2.readException();
                        obtain.recycle();
                        obtain2.recycle();
                    }
                }
            }
            googleSignInAccount = null;
            Integer num2 = this.f5489D;
            C0324l.d(num2);
            A a52 = new A(2, account, num2.intValue(), googleSignInAccount);
            g gVar2 = (g) w();
            j jVar2 = new j(1, a52);
            Parcel obtain3 = Parcel.obtain();
            obtain3.writeInterfaceToken(gVar2.f3497k);
            int i42 = C0426b.f3498a;
            obtain3.writeInt(1);
            jVar2.writeToParcel(obtain3, 0);
            obtain3.writeStrongBinder(fVar.asBinder());
            Parcel obtain22 = Parcel.obtain();
            gVar2.f3496j.transact(12, obtain3, obtain22, 0);
            obtain22.readException();
            obtain3.recycle();
            obtain22.recycle();
        } catch (RemoteException e4) {
            Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
            try {
                G g4 = (G) fVar;
                g4.f2544l.post(new Q0(g4, new l(1, new T1.b(8, null), null), 1, false));
            } catch (RemoteException unused2) {
                Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", e4);
            }
        }
    }

    @Override // W1.AbstractC0314b, U1.a.e
    public final boolean o() {
        return this.f5486A;
    }

    @Override // n2.f
    public final void p() {
        k(new AbstractC0314b.d());
    }

    @Override // W1.AbstractC0314b
    public final IInterface r(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.signin.internal.ISignInService");
        if (queryLocalInterface instanceof g) {
            return (g) queryLocalInterface;
        }
        return new C0425a(iBinder, "com.google.android.gms.signin.internal.ISignInService");
    }

    @Override // W1.AbstractC0314b
    public final Bundle u() {
        C0315c c0315c = this.f5487B;
        boolean equals = this.f2685c.getPackageName().equals(c0315c.f2710e);
        Bundle bundle = this.f5488C;
        if (!equals) {
            bundle.putString("com.google.android.gms.signin.internal.realClientPackageName", c0315c.f2710e);
        }
        return bundle;
    }

    @Override // W1.AbstractC0314b
    public final String x() {
        return "com.google.android.gms.signin.internal.ISignInService";
    }

    @Override // W1.AbstractC0314b
    public final String y() {
        return "com.google.android.gms.signin.service.START";
    }
}
