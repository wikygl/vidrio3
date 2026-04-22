package g3;

import j$.util.DesugarTimeZone;
import java.util.TimeZone;

/* renamed from: g3.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0428a {

    /* renamed from: a  reason: collision with root package name */
    public static final TimeZone f3502a = DesugarTimeZone.getTimeZone("UTC");

    public static boolean a(String str, int i4, char c4) {
        if (i4 < str.length() && str.charAt(i4) == c4) {
            return true;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:100:0x0205  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x01ea  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01ec  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.Date b(java.lang.String r18, java.text.ParsePosition r19) {
        /*
            Method dump skipped, instructions count: 585
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: g3.C0428a.b(java.lang.String, java.text.ParsePosition):java.util.Date");
    }

    public static int c(int i4, int i5, String str) {
        int i6;
        int i7;
        if (i4 >= 0 && i5 <= str.length() && i4 <= i5) {
            if (i4 < i5) {
                i7 = i4 + 1;
                int digit = Character.digit(str.charAt(i4), 10);
                if (digit >= 0) {
                    i6 = -digit;
                } else {
                    throw new NumberFormatException("Invalid number: " + str.substring(i4, i5));
                }
            } else {
                i6 = 0;
                i7 = i4;
            }
            while (i7 < i5) {
                int i8 = i7 + 1;
                int digit2 = Character.digit(str.charAt(i7), 10);
                if (digit2 >= 0) {
                    i6 = (i6 * 10) - digit2;
                    i7 = i8;
                } else {
                    throw new NumberFormatException("Invalid number: " + str.substring(i4, i5));
                }
            }
            return -i6;
        }
        throw new NumberFormatException(str);
    }
}
