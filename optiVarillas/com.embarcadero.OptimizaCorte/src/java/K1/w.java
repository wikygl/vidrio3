package K1;

import A1.Q;
import A1.y1;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Zv;
import com.google.android.gms.internal.ads.xk;
import com.google.android.gms.internal.ads.yG;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class w {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static String a(String str) {
        char c4;
        if (TextUtils.isEmpty(str)) {
            return "unspecified";
        }
        switch (str.hashCode()) {
            case 1743582862:
                if (str.equals("requester_type_0")) {
                    c4 = 0;
                    break;
                }
                c4 = 65535;
                break;
            case 1743582863:
                if (str.equals("requester_type_1")) {
                    c4 = 1;
                    break;
                }
                c4 = 65535;
                break;
            case 1743582864:
                if (str.equals("requester_type_2")) {
                    c4 = 2;
                    break;
                }
                c4 = 65535;
                break;
            case 1743582865:
                if (str.equals("requester_type_3")) {
                    c4 = 3;
                    break;
                }
                c4 = 65535;
                break;
            case 1743582866:
                if (str.equals("requester_type_4")) {
                    c4 = 4;
                    break;
                }
                c4 = 65535;
                break;
            case 1743582867:
                if (str.equals("requester_type_5")) {
                    c4 = 5;
                    break;
                }
                c4 = 65535;
                break;
            case 1743582868:
                if (str.equals("requester_type_6")) {
                    c4 = 6;
                    break;
                }
                c4 = 65535;
                break;
            case 1743582869:
                if (str.equals("requester_type_7")) {
                    c4 = 7;
                    break;
                }
                c4 = 65535;
                break;
            case 1743582870:
                if (str.equals("requester_type_8")) {
                    c4 = '\b';
                    break;
                }
                c4 = 65535;
                break;
            default:
                c4 = 65535;
                break;
        }
        switch (c4) {
            case 0:
                return "0";
            case 1:
                return "1";
            case 2:
                return "2";
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case '\b':
                return "8";
            default:
                return str;
        }
    }

    public static String b(y1 y1Var) {
        Bundle bundle;
        if (y1Var != null && (bundle = y1Var.f191l) != null) {
            return bundle.getString("query_info_type");
        }
        return "unspecified";
    }

    public static void c(Zv zv, String str, Pair... pairArr) {
        if (!((Boolean) A1.r.f168d.f171c.a(Gb.n6)).booleanValue()) {
            return;
        }
        xk.a.execute(new K0.b(zv, str, pairArr));
    }

    public static int d(yG yGVar) {
        if (yGVar.q) {
            return 2;
        }
        y1 y1Var = yGVar.d;
        Q q4 = y1Var.f181B;
        String str = y1Var.f186G;
        if (q4 == null && str == null) {
            return 1;
        }
        if (q4 != null && str != null) {
            return 5;
        }
        if (q4 != null) {
            return 3;
        }
        return 4;
    }
}
