package C;

import com.google.android.gms.internal.ads.ZF;
import u0.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class b implements f.e {
    public static String b(String str, String str2, String str3) {
        return str + str2 + str3;
    }

    public static String c(StringBuilder sb, String str, String str2) {
        sb.append(str);
        sb.append(str2);
        return sb.toString();
    }

    public static void d(String str, String str2, String str3) {
        ZF.f(str3, str2.concat(String.valueOf(str)));
    }

    @Override // u0.f.e
    public void a(f.d dVar, f fVar) {
        dVar.c();
    }
}
