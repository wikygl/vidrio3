package b1;

import android.util.Log;

/* renamed from: b1.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0354b {

    /* renamed from: a  reason: collision with root package name */
    public final C0353a f2898a;

    /* renamed from: b  reason: collision with root package name */
    public String f2899b;

    /* renamed from: c  reason: collision with root package name */
    public String f2900c;

    /* renamed from: d  reason: collision with root package name */
    public final int f2901d;

    /* renamed from: e  reason: collision with root package name */
    public double f2902e;
    public String f;

    public C0354b(String str, String str2, C0353a c0353a) {
        this.f2901d = 0;
        this.f2902e = 0.0d;
        this.f = "typeRect";
        this.f2899b = str;
        this.f2900c = str2;
        this.f2898a = c0353a;
    }

    public final double a(C0353a c0353a) {
        try {
            this.f2902e = Double.parseDouble(c0353a.f2892s.split("@")[5]);
            return Double.parseDouble(c0353a.f2892s.split("@")[5]);
        } catch (Exception e4) {
            Log.e("Error", e4.getMessage());
            return 0.0d;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x0076, code lost:
        if (r14 != false) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0083, code lost:
        if (r14 != false) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:?, code lost:
        return r0 / 2.0d;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:?, code lost:
        return r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final double b(boolean r14) {
        /*
            r13 = this;
            b1.a r0 = r13.f2898a
            double r1 = r13.a(r0)
            r3 = 4631530004285489152(0x4046800000000000, double:45.0)
            double r1 = c1.C0373f.c(r1, r3)
            double r5 = r13.a(r0)
            double r5 = r5 + r1
            double r1 = r13.a(r0)
            double r7 = r13.a(r0)
            double r7 = c1.C0373f.c(r7, r3)
            double r7 = r7 + r1
            double r1 = r13.a(r0)
            double r1 = c1.C0373f.c(r1, r3)
            double r9 = r13.a(r0)
            double r3 = c1.C0373f.c(r9, r3)
            double r3 = r3 + r1
            double r0 = r13.a(r0)
            r9 = 4611686018427387904(0x4000000000000000, double:2.0)
            double r0 = r0 * r9
            java.lang.String r2 = r13.f
            r2.getClass()
            r11 = -1
            int r12 = r2.hashCode()
            switch(r12) {
                case -676851237: goto L69;
                case -676563359: goto L5e;
                case -676384706: goto L53;
                case 507033346: goto L48;
                default: goto L47;
            }
        L47:
            goto L73
        L48:
            java.lang.String r12 = "typeRight"
            boolean r2 = r2.equals(r12)
            if (r2 != 0) goto L51
            goto L73
        L51:
            r11 = 3
            goto L73
        L53:
            java.lang.String r12 = "typeRect"
            boolean r2 = r2.equals(r12)
            if (r2 != 0) goto L5c
            goto L73
        L5c:
            r11 = 2
            goto L73
        L5e:
            java.lang.String r12 = "typeLeft"
            boolean r2 = r2.equals(r12)
            if (r2 != 0) goto L67
            goto L73
        L67:
            r11 = 1
            goto L73
        L69:
            java.lang.String r12 = "typeBoth"
            boolean r2 = r2.equals(r12)
            if (r2 != 0) goto L72
            goto L73
        L72:
            r11 = 0
        L73:
            switch(r11) {
                case 0: goto L8c;
                case 1: goto L87;
                case 2: goto L83;
                case 3: goto L7c;
                default: goto L76;
            }
        L76:
            if (r14 == 0) goto L79
            goto L85
        L79:
            double r5 = r0 / r9
            goto L92
        L7c:
            if (r14 == 0) goto L80
            r5 = r7
            goto L92
        L80:
            double r5 = r7 / r9
            goto L92
        L83:
            if (r14 == 0) goto L79
        L85:
            r5 = r0
            goto L92
        L87:
            if (r14 == 0) goto L8a
            goto L92
        L8a:
            double r5 = r5 / r9
            goto L92
        L8c:
            if (r14 == 0) goto L90
            r5 = r3
            goto L92
        L90:
            double r5 = r3 / r9
        L92:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: b1.C0354b.b(boolean):double");
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof C0354b)) {
            return false;
        }
        return this.f2899b.equals(((C0354b) obj).f2899b);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("Medida{medida='");
        sb.append(this.f2899b);
        sb.append("', uds='");
        sb.append(this.f2900c);
        sb.append("', unidades=");
        sb.append(this.f2901d);
        sb.append(", referencia='', anchoDisco=");
        sb.append(this.f2902e);
        sb.append(", cutType='");
        return C.b.c(sb, this.f, "'}");
    }

    public C0354b(String str, int i4) {
        this.f2900c = "";
        this.f2902e = 0.0d;
        this.f = "typeRect";
        this.f2899b = str;
        this.f2901d = i4;
    }

    public C0354b(String str, String str2, String str3, C0353a c0353a) {
        this.f2901d = 0;
        this.f2902e = 0.0d;
        this.f2899b = str;
        this.f2900c = str2;
        this.f = str3;
        this.f2898a = c0353a;
    }
}
