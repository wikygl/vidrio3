package n2;

import U1.a;
import U1.d;
import W1.C0315c;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import o2.C0748a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class b extends a.AbstractC0026a {
    @Override // U1.a.AbstractC0026a
    public final a.e a(Context context, Looper looper, C0315c c0315c, Object obj, d.a aVar, d.b bVar) {
        C0744a c0744a = (C0744a) obj;
        c0315c.getClass();
        Integer num = c0315c.f2712h;
        Bundle bundle = new Bundle();
        bundle.putParcelable("com.google.android.gms.signin.internal.clientRequestedAccount", c0315c.f2706a);
        if (num != null) {
            bundle.putInt("com.google.android.gms.common.internal.ClientSettings.sessionId", num.intValue());
        }
        bundle.putBoolean("com.google.android.gms.signin.internal.offlineAccessRequested", false);
        bundle.putBoolean("com.google.android.gms.signin.internal.idTokenRequested", false);
        bundle.putString("com.google.android.gms.signin.internal.serverClientId", null);
        bundle.putBoolean("com.google.android.gms.signin.internal.usePromptModeForAuthCode", true);
        bundle.putBoolean("com.google.android.gms.signin.internal.forceCodeForRefreshToken", false);
        bundle.putString("com.google.android.gms.signin.internal.hostedDomain", null);
        bundle.putString("com.google.android.gms.signin.internal.logSessionId", null);
        bundle.putBoolean("com.google.android.gms.signin.internal.waitForAccessTokenRefresh", false);
        return new C0748a(context, looper, c0315c, bundle, aVar, bVar);
    }
}
