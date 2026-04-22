package U0;

import S0.M;
import android.content.Context;
import android.view.View;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a {
    /* JADX WARN: Code restructure failed: missing block: B:4:0x000f, code lost:
        r0 = r3.getActiveNetwork();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean a(android.content.Context r3) {
        /*
            java.lang.String r0 = "connectivity"
            java.lang.Object r3 = r3.getSystemService(r0)
            android.net.ConnectivityManager r3 = (android.net.ConnectivityManager) r3
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 28
            r2 = 0
            if (r0 < r1) goto L3a
            android.net.Network r0 = H2.i.a(r3)
            if (r0 == 0) goto L3a
            android.net.LinkProperties r3 = r3.getLinkProperties(r0)
            if (r3 == 0) goto L3a
            java.lang.String r0 = D1.C0.e(r3)
            if (r0 == 0) goto L3a
            java.lang.String r3 = D1.C0.e(r3)
            java.lang.String r0 = "dns.adguard.com"
            boolean r0 = r3.equalsIgnoreCase(r0)
            if (r0 != 0) goto L39
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r0 = ".dns.nextdns.io"
            boolean r3 = r3.endsWith(r0)
            if (r3 == 0) goto L3a
        L39:
            r2 = 1
        L3a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: U0.a.a(android.content.Context):boolean");
    }

    public static b b(Context context, View view, String str, String str2, String str3) {
        b.a aVar = new b.a(context);
        AlertController.b bVar = aVar.a;
        bVar.d = str;
        bVar.f = str2;
        aVar.c(str3, new M(view, 2));
        return aVar.a();
    }
}
