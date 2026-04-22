package K;

import K.h;
import android.text.SpannableStringBuilder;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class a {

    /* renamed from: d  reason: collision with root package name */
    public static final String f1243d;

    /* renamed from: e  reason: collision with root package name */
    public static final String f1244e;
    public static final a f;

    /* renamed from: g  reason: collision with root package name */
    public static final a f1245g;

    /* renamed from: a  reason: collision with root package name */
    public final boolean f1246a;

    /* renamed from: b  reason: collision with root package name */
    public final int f1247b;

    /* renamed from: c  reason: collision with root package name */
    public final g f1248c;

    /* renamed from: K.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class C0010a {

        /* renamed from: e  reason: collision with root package name */
        public static final byte[] f1249e = new byte[1792];

        /* renamed from: a  reason: collision with root package name */
        public final CharSequence f1250a;

        /* renamed from: b  reason: collision with root package name */
        public final int f1251b;

        /* renamed from: c  reason: collision with root package name */
        public int f1252c;

        /* renamed from: d  reason: collision with root package name */
        public char f1253d;

        static {
            for (int i4 = 0; i4 < 1792; i4++) {
                f1249e[i4] = Character.getDirectionality(i4);
            }
        }

        public C0010a(CharSequence charSequence) {
            this.f1250a = charSequence;
            this.f1251b = charSequence.length();
        }

        public final byte a() {
            CharSequence charSequence = this.f1250a;
            char charAt = charSequence.charAt(this.f1252c - 1);
            this.f1253d = charAt;
            if (Character.isLowSurrogate(charAt)) {
                int codePointBefore = Character.codePointBefore(charSequence, this.f1252c);
                this.f1252c -= Character.charCount(codePointBefore);
                return Character.getDirectionality(codePointBefore);
            }
            this.f1252c--;
            char c4 = this.f1253d;
            if (c4 < 1792) {
                return f1249e[c4];
            }
            return Character.getDirectionality(c4);
        }
    }

    static {
        h.d dVar = h.f1261c;
        f1243d = Character.toString((char) 8206);
        f1244e = Character.toString((char) 8207);
        f = new a(false);
        f1245g = new a(true);
    }

    public a(boolean z4) {
        h.d dVar = h.f1261c;
        this.f1246a = z4;
        this.f1247b = 2;
        this.f1248c = dVar;
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0068, code lost:
        return 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0070, code lost:
        if (r1 != 0) goto L46;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0073, code lost:
        if (r2 == 0) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0079, code lost:
        if (r0.f1252c <= 0) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x007f, code lost:
        switch(r0.a()) {
            case 14: goto L62;
            case 15: goto L62;
            case 16: goto L57;
            case 17: goto L57;
            case 18: goto L53;
            default: goto L65;
        };
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0083, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0086, code lost:
        if (r1 != r3) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0089, code lost:
        r3 = r3 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x008c, code lost:
        if (r1 != r3) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:?, code lost:
        return r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:?, code lost:
        return 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:?, code lost:
        return 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int a(java.lang.CharSequence r9) {
        /*
            K.a$a r0 = new K.a$a
            r0.<init>(r9)
            r9 = 0
            r0.f1252c = r9
            r1 = 0
            r2 = 0
            r3 = 0
        Lb:
            int r4 = r0.f1252c
            int r5 = r0.f1251b
            r6 = 1
            r7 = -1
            if (r4 >= r5) goto L70
            if (r1 != 0) goto L70
            java.lang.CharSequence r5 = r0.f1250a
            char r4 = r5.charAt(r4)
            r0.f1253d = r4
            boolean r4 = java.lang.Character.isHighSurrogate(r4)
            if (r4 == 0) goto L37
            int r4 = r0.f1252c
            int r4 = java.lang.Character.codePointAt(r5, r4)
            int r5 = r0.f1252c
            int r8 = java.lang.Character.charCount(r4)
            int r8 = r8 + r5
            r0.f1252c = r8
            byte r4 = java.lang.Character.getDirectionality(r4)
            goto L4b
        L37:
            int r4 = r0.f1252c
            int r4 = r4 + r6
            r0.f1252c = r4
            char r4 = r0.f1253d
            r5 = 1792(0x700, float:2.511E-42)
            if (r4 >= r5) goto L47
            byte[] r5 = K.a.C0010a.f1249e
            r4 = r5[r4]
            goto L4b
        L47:
            byte r4 = java.lang.Character.getDirectionality(r4)
        L4b:
            if (r4 == 0) goto L6a
            if (r4 == r6) goto L66
            r5 = 2
            if (r4 == r5) goto L66
            r5 = 9
            if (r4 == r5) goto Lb
            switch(r4) {
                case 14: goto L62;
                case 15: goto L62;
                case 16: goto L5e;
                case 17: goto L5e;
                case 18: goto L5a;
                default: goto L59;
            }
        L59:
            goto L6e
        L5a:
            int r3 = r3 + (-1)
            r2 = 0
            goto Lb
        L5e:
            int r3 = r3 + 1
            r2 = 1
            goto Lb
        L62:
            int r3 = r3 + 1
            r2 = -1
            goto Lb
        L66:
            if (r3 != 0) goto L6e
        L68:
            r9 = 1
            goto L8f
        L6a:
            if (r3 != 0) goto L6e
        L6c:
            r9 = -1
            goto L8f
        L6e:
            r1 = r3
            goto Lb
        L70:
            if (r1 != 0) goto L73
            goto L8f
        L73:
            if (r2 == 0) goto L77
            r9 = r2
            goto L8f
        L77:
            int r2 = r0.f1252c
            if (r2 <= 0) goto L8f
            byte r2 = r0.a()
            switch(r2) {
                case 14: goto L8c;
                case 15: goto L8c;
                case 16: goto L86;
                case 17: goto L86;
                case 18: goto L83;
                default: goto L82;
            }
        L82:
            goto L77
        L83:
            int r3 = r3 + 1
            goto L77
        L86:
            if (r1 != r3) goto L89
            goto L68
        L89:
            int r3 = r3 + (-1)
            goto L77
        L8c:
            if (r1 != r3) goto L89
            goto L6c
        L8f:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: K.a.a(java.lang.CharSequence):int");
    }

    public static int b(CharSequence charSequence) {
        C0010a c0010a = new C0010a(charSequence);
        c0010a.f1252c = c0010a.f1251b;
        int i4 = 0;
        int i5 = 0;
        while (c0010a.f1252c > 0) {
            byte a4 = c0010a.a();
            if (a4 != 0) {
                if (a4 != 1 && a4 != 2) {
                    if (a4 != 9) {
                        switch (a4) {
                            case 14:
                            case 15:
                                if (i5 == i4) {
                                    return -1;
                                }
                                i4--;
                                break;
                            case 16:
                            case 17:
                                if (i5 == i4) {
                                    return 1;
                                }
                                i4--;
                                break;
                            case 18:
                                i4++;
                                break;
                            default:
                                if (i5 != 0) {
                                    break;
                                } else {
                                    i5 = i4;
                                    break;
                                }
                        }
                    } else {
                        continue;
                    }
                } else if (i4 == 0) {
                    return 1;
                } else {
                    if (i5 == 0) {
                        i5 = i4;
                    }
                }
            } else if (i4 == 0) {
                return -1;
            } else {
                if (i5 == 0) {
                    i5 = i4;
                }
            }
        }
        return 0;
    }

    public final SpannableStringBuilder c(CharSequence charSequence) {
        h.d dVar;
        char c4;
        h.d dVar2;
        String str;
        h.d dVar3 = h.f1261c;
        if (charSequence == null) {
            return null;
        }
        boolean b4 = dVar3.b(charSequence, charSequence.length());
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int i4 = this.f1247b & 2;
        String str2 = "";
        String str3 = f1244e;
        String str4 = f1243d;
        boolean z4 = this.f1246a;
        if (i4 != 0) {
            if (b4) {
                dVar2 = h.f1260b;
            } else {
                dVar2 = h.f1259a;
            }
            boolean b5 = dVar2.b(charSequence, charSequence.length());
            if (!z4 && (b5 || a(charSequence) == 1)) {
                str = str4;
            } else if (!z4 || (b5 && a(charSequence) != -1)) {
                str = "";
            } else {
                str = str3;
            }
            spannableStringBuilder.append((CharSequence) str);
        }
        if (b4 != z4) {
            if (b4) {
                c4 = 8235;
            } else {
                c4 = 8234;
            }
            spannableStringBuilder.append(c4);
            spannableStringBuilder.append(charSequence);
            spannableStringBuilder.append((char) 8236);
        } else {
            spannableStringBuilder.append(charSequence);
        }
        if (b4) {
            dVar = h.f1260b;
        } else {
            dVar = h.f1259a;
        }
        boolean b6 = dVar.b(charSequence, charSequence.length());
        if (!z4 && (b6 || b(charSequence) == 1)) {
            str2 = str4;
        } else if (z4 && (!b6 || b(charSequence) == -1)) {
            str2 = str3;
        }
        spannableStringBuilder.append((CharSequence) str2);
        return spannableStringBuilder;
    }
}
