package C1;

import D1.C0183d0;
import D1.t0;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.vb;

/* renamed from: C1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0147a {
    public static final boolean a(Context context, Intent intent, InterfaceC0148b interfaceC0148b, C c4, boolean z4) {
        int i4;
        if (z4) {
            Uri data = intent.getData();
            try {
                z1.p.f6575A.f6578c.getClass();
                i4 = t0.B(context, data);
                if (interfaceC0148b != null) {
                    interfaceC0148b.h();
                }
            } catch (ActivityNotFoundException e4) {
                E1.m.g(e4.getMessage());
                i4 = 6;
            }
            if (c4 != null) {
                c4.z(i4);
            }
            if (i4 == 5) {
                return true;
            }
            return false;
        }
        try {
            String uri = intent.toURI();
            C0183d0.k("Launching an intent: " + uri);
            t0 t0Var = z1.p.f6575A.f6578c;
            t0.p(context, intent);
            if (interfaceC0148b != null) {
                interfaceC0148b.h();
            }
            if (c4 != null) {
                c4.A(true);
            }
            return true;
        } catch (ActivityNotFoundException e5) {
            E1.m.g(e5.getMessage());
            if (c4 != null) {
                c4.A(false);
            }
            return false;
        }
    }

    public static final boolean b(Context context, i iVar, InterfaceC0148b interfaceC0148b, C c4) {
        int i4 = 0;
        if (iVar == null) {
            E1.m.g("No intent data for launcher overlay.");
            return false;
        }
        Gb.a(context);
        boolean z4 = iVar.f372s;
        Intent intent = iVar.f370q;
        if (intent != null) {
            return a(context, intent, interfaceC0148b, c4, z4);
        }
        Intent intent2 = new Intent();
        String str = iVar.f364k;
        if (TextUtils.isEmpty(str)) {
            E1.m.g("Open GMSG did not contain a URL.");
            return false;
        }
        String str2 = iVar.f365l;
        if (!TextUtils.isEmpty(str2)) {
            intent2.setDataAndType(Uri.parse(str), str2);
        } else {
            intent2.setData(Uri.parse(str));
        }
        intent2.setAction("android.intent.action.VIEW");
        String str3 = iVar.f366m;
        if (!TextUtils.isEmpty(str3)) {
            intent2.setPackage(str3);
        }
        String str4 = iVar.f367n;
        if (!TextUtils.isEmpty(str4)) {
            String[] split = str4.split("/", 2);
            if (split.length < 2) {
                E1.m.g("Could not parse component name from open GMSG: ".concat(str4));
                return false;
            }
            intent2.setClassName(split[0], split[1]);
        }
        String str5 = iVar.f368o;
        if (!TextUtils.isEmpty(str5)) {
            try {
                i4 = Integer.parseInt(str5);
            } catch (NumberFormatException unused) {
                E1.m.g("Could not parse intent flags.");
            }
            intent2.addFlags(i4);
        }
        vb vbVar = Gb.W3;
        A1.r rVar = A1.r.f168d;
        if (((Boolean) rVar.f171c.a(vbVar)).booleanValue()) {
            intent2.addFlags(268435456);
            intent2.putExtra("android.support.customtabs.extra.user_opt_out", true);
        } else {
            if (((Boolean) rVar.f171c.a(Gb.V3)).booleanValue()) {
                t0 t0Var = z1.p.f6575A.f6578c;
                t0.D(context, intent2);
            }
        }
        return a(context, intent2, interfaceC0148b, c4, z4);
    }
}
