package I;

import M.C0229k;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class h implements C0229k.a {
    public static /* synthetic */ String a(int i4) {
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 == 3) {
                    return "video";
                }
                throw null;
            }
            return "nativeDisplay";
        }
        return "htmlDisplay";
    }

    public static String b(int i4, String str, String str2) {
        return str + i4 + str2;
    }

    public static String c(String str, String str2) {
        return str + str2;
    }

    public static /* synthetic */ String d(int i4) {
        return i4 != 1 ? i4 != 2 ? i4 != 3 ? "null" : "VIDEO" : "NATIVE_DISPLAY" : "HTML_DISPLAY";
    }
}
