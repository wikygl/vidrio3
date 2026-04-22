package B3;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class h extends g {
    /* JADX WARN: Code restructure failed: missing block: B:59:?, code lost:
        return r12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final int c(java.lang.CharSequence r10, java.lang.String r11, int r12, boolean r13) {
        /*
            if (r13 != 0) goto Lf
            boolean r0 = r10 instanceof java.lang.String
            if (r0 != 0) goto L7
            goto Lf
        L7:
            java.lang.String r10 = (java.lang.String) r10
            int r10 = r10.indexOf(r11, r12)
            goto L9b
        Lf:
            int r0 = r10.length()
            y3.c r1 = new y3.c
            r2 = 0
            if (r12 >= 0) goto L19
            r12 = 0
        L19:
            int r3 = r10.length()
            if (r0 <= r3) goto L20
            r0 = r3
        L20:
            r3 = 1
            r1.<init>(r12, r0, r3)
            boolean r0 = r10 instanceof java.lang.String
            int r1 = r1.f6516k
            if (r0 == 0) goto L4d
            if (r12 <= r1) goto L2e
            goto L9a
        L2e:
            r7 = r10
            java.lang.String r7 = (java.lang.String) r7
            int r9 = r11.length()
            r6 = 0
            if (r13 != 0) goto L3d
            boolean r0 = r11.regionMatches(r6, r7, r12, r9)
            goto L44
        L3d:
            r4 = r11
            r5 = r13
            r8 = r12
            boolean r0 = r4.regionMatches(r5, r6, r7, r8, r9)
        L44:
            if (r0 == 0) goto L48
        L46:
            r10 = r12
            goto L9b
        L48:
            if (r12 == r1) goto L9a
            int r12 = r12 + 1
            goto L2e
        L4d:
            if (r12 <= r1) goto L50
            goto L9a
        L50:
            int r0 = r11.length()
            if (r12 < 0) goto L95
            int r4 = r11.length()
            int r4 = r4 - r0
            if (r4 < 0) goto L95
            int r4 = r10.length()
            int r4 = r4 - r0
            if (r12 <= r4) goto L65
            goto L95
        L65:
            r4 = 0
        L66:
            if (r4 >= r0) goto L46
            char r5 = r11.charAt(r4)
            int r6 = r12 + r4
            char r6 = r10.charAt(r6)
            if (r5 != r6) goto L76
        L74:
            r5 = 1
            goto L8f
        L76:
            if (r13 != 0) goto L7a
        L78:
            r5 = 0
            goto L8f
        L7a:
            char r5 = java.lang.Character.toUpperCase(r5)
            char r6 = java.lang.Character.toUpperCase(r6)
            if (r5 == r6) goto L74
            char r5 = java.lang.Character.toLowerCase(r5)
            char r6 = java.lang.Character.toLowerCase(r6)
            if (r5 != r6) goto L78
            goto L74
        L8f:
            if (r5 != 0) goto L92
            goto L95
        L92:
            int r4 = r4 + 1
            goto L66
        L95:
            if (r12 == r1) goto L9a
            int r12 = r12 + 1
            goto L50
        L9a:
            r10 = -1
        L9b:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: B3.h.c(java.lang.CharSequence, java.lang.String, int, boolean):int");
    }

    public static String d(String str) {
        v3.h.e(str, "<this>");
        v3.h.e(str, "missingDelimiterValue");
        int lastIndexOf = str.lastIndexOf(46, str.length() - 1);
        if (lastIndexOf != -1) {
            String substring = str.substring(lastIndexOf + 1, str.length());
            v3.h.d(substring, "this as java.lang.String…ing(startIndex, endIndex)");
            return substring;
        }
        return str;
    }
}
