package X1;

import com.google.android.gms.internal.ads.ZF;
import com.google.android.gms.internal.play_billing.I;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final /* synthetic */ class b {
    public static /* synthetic */ String a(int i4) {
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 != 3) {
                    if (i4 == 4) {
                        return "unspecified";
                    }
                    throw null;
                }
                return "onePixel";
            }
            return "definedByJavascript";
        }
        return "beginToRender";
    }

    public static /* synthetic */ String b(int i4) {
        switch (i4) {
            case 1:
                return "api-call";
            case 2:
                return "dynamite-enter";
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return "client-signals-start";
            case 4:
                return "client-signals-end";
            case 5:
                return "service-connected";
            case 6:
                return "gms-signals-start";
            case 7:
                return "gms-signals-end";
            case 8:
                return "get-signals-sdkcore-start";
            case 9:
                return "get-signals-sdkcore-end";
            case 10:
                return "get-ad-dictionary-sdkcore-start";
            case 11:
                return "get-ad-dictionary-sdkcore-end";
            case 12:
                return "http-response-ready";
            case 13:
                return "server-response-parse-start";
            case 14:
                return "public-api-callback";
            default:
                throw null;
        }
    }

    public static int c(int i4, int i5, int i6) {
        return I.s(i4) + i5 + i6;
    }

    public static String d(int i4, int i5, String str, String str2) {
        return str + i4 + str2 + i5;
    }

    public static String e(String str, String str2, String str3) {
        return str + str2 + str3;
    }

    public static StringBuilder f(String str, String str2, String str3) {
        StringBuilder sb = new StringBuilder(str);
        sb.append(str2);
        sb.append(str3);
        return sb;
    }

    public static void g(int i4, String str, String str2) {
        ZF.f(str2, str + i4);
    }
}
