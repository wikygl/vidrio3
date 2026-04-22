package j3;

import G3.g;
import com.google.gson.k;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class a implements Closeable {

    /* renamed from: j  reason: collision with root package name */
    public final Reader f4858j;

    /* renamed from: r  reason: collision with root package name */
    public long f4866r;

    /* renamed from: s  reason: collision with root package name */
    public int f4867s;

    /* renamed from: t  reason: collision with root package name */
    public String f4868t;

    /* renamed from: u  reason: collision with root package name */
    public int[] f4869u;

    /* renamed from: w  reason: collision with root package name */
    public String[] f4871w;

    /* renamed from: x  reason: collision with root package name */
    public int[] f4872x;

    /* renamed from: k  reason: collision with root package name */
    public boolean f4859k = false;

    /* renamed from: l  reason: collision with root package name */
    public final char[] f4860l = new char[1024];

    /* renamed from: m  reason: collision with root package name */
    public int f4861m = 0;

    /* renamed from: n  reason: collision with root package name */
    public int f4862n = 0;

    /* renamed from: o  reason: collision with root package name */
    public int f4863o = 0;

    /* renamed from: p  reason: collision with root package name */
    public int f4864p = 0;

    /* renamed from: q  reason: collision with root package name */
    public int f4865q = 0;

    /* renamed from: v  reason: collision with root package name */
    public int f4870v = 1;

    /* renamed from: j3.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class C0055a extends g {
        public final void F(a aVar) {
            if (aVar instanceof com.google.gson.internal.bind.a) {
                com.google.gson.internal.bind.a aVar2 = (com.google.gson.internal.bind.a) aVar;
                aVar2.P(b.f4877n);
                Map.Entry entry = (Map.Entry) ((Iterator) aVar2.T()).next();
                aVar2.V(entry.getValue());
                aVar2.V(new k((String) entry.getKey()));
                return;
            }
            int i4 = aVar.f4865q;
            if (i4 == 0) {
                i4 = aVar.f();
            }
            if (i4 == 13) {
                aVar.f4865q = 9;
            } else if (i4 == 12) {
                aVar.f4865q = 8;
            } else if (i4 == 14) {
                aVar.f4865q = 10;
            } else {
                throw new IllegalStateException("Expected a name but was " + aVar.H() + aVar.t());
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.Object, j3.a$a] */
    static {
        g.f994j = new Object();
    }

    public a(StringReader stringReader) {
        int[] iArr = new int[32];
        this.f4869u = iArr;
        iArr[0] = 6;
        this.f4871w = new String[32];
        this.f4872x = new int[32];
        this.f4858j = stringReader;
    }

    public final int B(boolean z4) {
        int i4 = this.f4861m;
        int i5 = this.f4862n;
        while (true) {
            if (i4 == i5) {
                this.f4861m = i4;
                if (!k(1)) {
                    if (!z4) {
                        return -1;
                    }
                    throw new EOFException("End of input" + t());
                }
                i4 = this.f4861m;
                i5 = this.f4862n;
            }
            int i6 = i4 + 1;
            char[] cArr = this.f4860l;
            char c4 = cArr[i4];
            if (c4 == '\n') {
                this.f4863o++;
                this.f4864p = i6;
            } else if (c4 != ' ' && c4 != '\r' && c4 != '\t') {
                if (c4 == '/') {
                    this.f4861m = i6;
                    if (i6 == i5) {
                        this.f4861m = i4;
                        boolean k4 = k(2);
                        this.f4861m++;
                        if (!k4) {
                            return c4;
                        }
                    }
                    d();
                    int i7 = this.f4861m;
                    char c5 = cArr[i7];
                    if (c5 != '*') {
                        if (c5 != '/') {
                            return c4;
                        }
                        this.f4861m = i7 + 1;
                        L();
                        i4 = this.f4861m;
                        i5 = this.f4862n;
                    } else {
                        this.f4861m = i7 + 1;
                        while (true) {
                            if (this.f4861m + 2 > this.f4862n && !k(2)) {
                                O("Unterminated comment");
                                throw null;
                            }
                            int i8 = this.f4861m;
                            if (cArr[i8] == '\n') {
                                this.f4863o++;
                                this.f4864p = i8 + 1;
                            } else {
                                for (int i9 = 0; i9 < 2; i9++) {
                                    if (cArr[this.f4861m + i9] != "*/".charAt(i9)) {
                                        break;
                                    }
                                }
                                i4 = this.f4861m + 2;
                                i5 = this.f4862n;
                                break;
                            }
                            this.f4861m++;
                        }
                    }
                } else if (c4 == '#') {
                    this.f4861m = i6;
                    d();
                    L();
                    i4 = this.f4861m;
                    i5 = this.f4862n;
                } else {
                    this.f4861m = i6;
                    return c4;
                }
            }
            i4 = i6;
        }
    }

    public void C() {
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 == 7) {
            this.f4865q = 0;
            int[] iArr = this.f4872x;
            int i5 = this.f4870v - 1;
            iArr[i5] = iArr[i5] + 1;
            return;
        }
        throw new IllegalStateException("Expected null but was " + H() + t());
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x002d, code lost:
        r10.f4861m = r8;
        r8 = r8 - r3;
        r2 = r8 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0032, code lost:
        if (r1 != null) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0034, code lost:
        r1 = new java.lang.StringBuilder(java.lang.Math.max(r8 * 2, 16));
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x005b, code lost:
        if (r1 != null) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x005d, code lost:
        r1 = new java.lang.StringBuilder(java.lang.Math.max((r2 - r3) * 2, 16));
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x006b, code lost:
        r1.append(r7, r3, r2 - r3);
        r10.f4861m = r2;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.String E(char r11) {
        /*
            r10 = this;
            r0 = 0
            r1 = r0
        L2:
            int r2 = r10.f4861m
            int r3 = r10.f4862n
        L6:
            r4 = r3
            r3 = r2
        L8:
            r5 = 1
            r6 = 16
            char[] r7 = r10.f4860l
            if (r2 >= r4) goto L5b
            int r8 = r2 + 1
            char r2 = r7[r2]
            if (r2 != r11) goto L29
            r10.f4861m = r8
            int r8 = r8 - r3
            int r8 = r8 - r5
            if (r1 != 0) goto L21
            java.lang.String r11 = new java.lang.String
            r11.<init>(r7, r3, r8)
            return r11
        L21:
            r1.append(r7, r3, r8)
            java.lang.String r11 = r1.toString()
            return r11
        L29:
            r9 = 92
            if (r2 != r9) goto L4e
            r10.f4861m = r8
            int r8 = r8 - r3
            int r2 = r8 + (-1)
            if (r1 != 0) goto L3f
            int r8 = r8 * 2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            int r4 = java.lang.Math.max(r8, r6)
            r1.<init>(r4)
        L3f:
            r1.append(r7, r3, r2)
            char r2 = r10.J()
            r1.append(r2)
            int r2 = r10.f4861m
            int r3 = r10.f4862n
            goto L6
        L4e:
            r6 = 10
            if (r2 != r6) goto L59
            int r2 = r10.f4863o
            int r2 = r2 + r5
            r10.f4863o = r2
            r10.f4864p = r8
        L59:
            r2 = r8
            goto L8
        L5b:
            if (r1 != 0) goto L6b
            int r1 = r2 - r3
            int r1 = r1 * 2
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            int r1 = java.lang.Math.max(r1, r6)
            r4.<init>(r1)
            r1 = r4
        L6b:
            int r4 = r2 - r3
            r1.append(r7, r3, r4)
            r10.f4861m = r2
            boolean r2 = r10.k(r5)
            if (r2 == 0) goto L79
            goto L2
        L79:
            java.lang.String r11 = "Unterminated string"
            r10.O(r11)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j3.a.E(char):java.lang.String");
    }

    public String F() {
        String str;
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 == 10) {
            str = G();
        } else if (i4 == 8) {
            str = E('\'');
        } else if (i4 == 9) {
            str = E('\"');
        } else if (i4 == 11) {
            str = this.f4868t;
            this.f4868t = null;
        } else if (i4 == 15) {
            str = Long.toString(this.f4866r);
        } else if (i4 == 16) {
            str = new String(this.f4860l, this.f4861m, this.f4867s);
            this.f4861m += this.f4867s;
        } else {
            throw new IllegalStateException("Expected a string but was " + H() + t());
        }
        this.f4865q = 0;
        int[] iArr = this.f4872x;
        int i5 = this.f4870v - 1;
        iArr[i5] = iArr[i5] + 1;
        return str;
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x004a, code lost:
        d();
     */
    /* JADX WARN: Removed duplicated region for block: B:46:0x007c  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0084  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.String G() {
        /*
            r7 = this;
            r0 = 0
            r1 = 0
        L2:
            r2 = 0
        L3:
            int r3 = r7.f4861m
            int r4 = r3 + r2
            int r5 = r7.f4862n
            char[] r6 = r7.f4860l
            if (r4 >= r5) goto L4e
            int r3 = r3 + r2
            char r3 = r6[r3]
            r4 = 9
            if (r3 == r4) goto L5a
            r4 = 10
            if (r3 == r4) goto L5a
            r4 = 12
            if (r3 == r4) goto L5a
            r4 = 13
            if (r3 == r4) goto L5a
            r4 = 32
            if (r3 == r4) goto L5a
            r4 = 35
            if (r3 == r4) goto L4a
            r4 = 44
            if (r3 == r4) goto L5a
            r4 = 47
            if (r3 == r4) goto L4a
            r4 = 61
            if (r3 == r4) goto L4a
            r4 = 123(0x7b, float:1.72E-43)
            if (r3 == r4) goto L5a
            r4 = 125(0x7d, float:1.75E-43)
            if (r3 == r4) goto L5a
            r4 = 58
            if (r3 == r4) goto L5a
            r4 = 59
            if (r3 == r4) goto L4a
            switch(r3) {
                case 91: goto L5a;
                case 92: goto L4a;
                case 93: goto L5a;
                default: goto L47;
            }
        L47:
            int r2 = r2 + 1
            goto L3
        L4a:
            r7.d()
            goto L5a
        L4e:
            int r3 = r6.length
            if (r2 >= r3) goto L5c
            int r3 = r2 + 1
            boolean r3 = r7.k(r3)
            if (r3 == 0) goto L5a
            goto L3
        L5a:
            r1 = r2
            goto L7a
        L5c:
            if (r0 != 0) goto L69
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r3 = 16
            int r3 = java.lang.Math.max(r2, r3)
            r0.<init>(r3)
        L69:
            int r3 = r7.f4861m
            r0.append(r6, r3, r2)
            int r3 = r7.f4861m
            int r3 = r3 + r2
            r7.f4861m = r3
            r2 = 1
            boolean r2 = r7.k(r2)
            if (r2 != 0) goto L2
        L7a:
            if (r0 != 0) goto L84
            java.lang.String r0 = new java.lang.String
            int r2 = r7.f4861m
            r0.<init>(r6, r2, r1)
            goto L8d
        L84:
            int r2 = r7.f4861m
            r0.append(r6, r2, r1)
            java.lang.String r0 = r0.toString()
        L8d:
            int r2 = r7.f4861m
            int r2 = r2 + r1
            r7.f4861m = r2
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j3.a.G():java.lang.String");
    }

    public b H() {
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        switch (i4) {
            case 1:
                return b.f4875l;
            case 2:
                return b.f4876m;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return b.f4873j;
            case 4:
                return b.f4874k;
            case 5:
            case 6:
                return b.f4880q;
            case 7:
                return b.f4881r;
            case 8:
            case 9:
            case 10:
            case 11:
                return b.f4878o;
            case 12:
            case 13:
            case 14:
                return b.f4877n;
            case 15:
            case 16:
                return b.f4879p;
            case 17:
                return b.f4882s;
            default:
                throw new AssertionError();
        }
    }

    public final void I(int i4) {
        int i5 = this.f4870v;
        int[] iArr = this.f4869u;
        if (i5 == iArr.length) {
            int i6 = i5 * 2;
            this.f4869u = Arrays.copyOf(iArr, i6);
            this.f4872x = Arrays.copyOf(this.f4872x, i6);
            this.f4871w = (String[]) Arrays.copyOf(this.f4871w, i6);
        }
        int[] iArr2 = this.f4869u;
        int i7 = this.f4870v;
        this.f4870v = i7 + 1;
        iArr2[i7] = i4;
    }

    public final char J() {
        int i4;
        if (this.f4861m == this.f4862n && !k(1)) {
            O("Unterminated escape sequence");
            throw null;
        }
        int i5 = this.f4861m;
        int i6 = i5 + 1;
        this.f4861m = i6;
        char[] cArr = this.f4860l;
        char c4 = cArr[i5];
        if (c4 != '\n') {
            if (c4 != '\"' && c4 != '\'' && c4 != '/' && c4 != '\\') {
                if (c4 != 'b') {
                    if (c4 != 'f') {
                        if (c4 == 'n') {
                            return '\n';
                        }
                        if (c4 != 'r') {
                            if (c4 != 't') {
                                if (c4 == 'u') {
                                    if (i5 + 5 > this.f4862n && !k(4)) {
                                        O("Unterminated escape sequence");
                                        throw null;
                                    }
                                    int i7 = this.f4861m;
                                    int i8 = i7 + 4;
                                    char c5 = 0;
                                    while (i7 < i8) {
                                        char c6 = cArr[i7];
                                        char c7 = (char) (c5 << 4);
                                        if (c6 >= '0' && c6 <= '9') {
                                            i4 = c6 - '0';
                                        } else if (c6 >= 'a' && c6 <= 'f') {
                                            i4 = c6 - 'W';
                                        } else if (c6 >= 'A' && c6 <= 'F') {
                                            i4 = c6 - '7';
                                        } else {
                                            throw new NumberFormatException("\\u".concat(new String(cArr, this.f4861m, 4)));
                                        }
                                        c5 = (char) (i4 + c7);
                                        i7++;
                                    }
                                    this.f4861m += 4;
                                    return c5;
                                }
                                O("Invalid escape sequence");
                                throw null;
                            }
                            return '\t';
                        }
                        return '\r';
                    }
                    return '\f';
                }
                return '\b';
            }
        } else {
            this.f4863o++;
            this.f4864p = i6;
        }
        return c4;
    }

    public final void K(char c4) {
        do {
            int i4 = this.f4861m;
            int i5 = this.f4862n;
            while (i4 < i5) {
                int i6 = i4 + 1;
                char c5 = this.f4860l[i4];
                if (c5 == c4) {
                    this.f4861m = i6;
                    return;
                } else if (c5 == '\\') {
                    this.f4861m = i6;
                    J();
                    i4 = this.f4861m;
                    i5 = this.f4862n;
                } else {
                    if (c5 == '\n') {
                        this.f4863o++;
                        this.f4864p = i6;
                    }
                    i4 = i6;
                }
            }
            this.f4861m = i4;
        } while (k(1));
        O("Unterminated string");
        throw null;
    }

    public final void L() {
        char c4;
        do {
            if (this.f4861m < this.f4862n || k(1)) {
                int i4 = this.f4861m;
                int i5 = i4 + 1;
                this.f4861m = i5;
                c4 = this.f4860l[i4];
                if (c4 == '\n') {
                    this.f4863o++;
                    this.f4864p = i5;
                    return;
                }
            } else {
                return;
            }
        } while (c4 != '\r');
    }

    /* JADX WARN: Code restructure failed: missing block: B:33:0x0048, code lost:
        d();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void M() {
        /*
            r4 = this;
        L0:
            r0 = 0
        L1:
            int r1 = r4.f4861m
            int r2 = r1 + r0
            int r3 = r4.f4862n
            if (r2 >= r3) goto L51
            char[] r2 = r4.f4860l
            int r1 = r1 + r0
            char r1 = r2[r1]
            r2 = 9
            if (r1 == r2) goto L4b
            r2 = 10
            if (r1 == r2) goto L4b
            r2 = 12
            if (r1 == r2) goto L4b
            r2 = 13
            if (r1 == r2) goto L4b
            r2 = 32
            if (r1 == r2) goto L4b
            r2 = 35
            if (r1 == r2) goto L48
            r2 = 44
            if (r1 == r2) goto L4b
            r2 = 47
            if (r1 == r2) goto L48
            r2 = 61
            if (r1 == r2) goto L48
            r2 = 123(0x7b, float:1.72E-43)
            if (r1 == r2) goto L4b
            r2 = 125(0x7d, float:1.75E-43)
            if (r1 == r2) goto L4b
            r2 = 58
            if (r1 == r2) goto L4b
            r2 = 59
            if (r1 == r2) goto L48
            switch(r1) {
                case 91: goto L4b;
                case 92: goto L48;
                case 93: goto L4b;
                default: goto L45;
            }
        L45:
            int r0 = r0 + 1
            goto L1
        L48:
            r4.d()
        L4b:
            int r1 = r4.f4861m
            int r1 = r1 + r0
            r4.f4861m = r1
            return
        L51:
            int r1 = r1 + r0
            r4.f4861m = r1
            r0 = 1
            boolean r0 = r4.k(r0)
            if (r0 != 0) goto L0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j3.a.M():void");
    }

    public void N() {
        int i4 = 0;
        do {
            int i5 = this.f4865q;
            if (i5 == 0) {
                i5 = f();
            }
            switch (i5) {
                case 1:
                    I(3);
                    i4++;
                    break;
                case 2:
                    if (i4 == 0) {
                        this.f4871w[this.f4870v - 1] = null;
                    }
                    this.f4870v--;
                    i4--;
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    I(1);
                    i4++;
                    break;
                case 4:
                    this.f4870v--;
                    i4--;
                    break;
                case 8:
                    K('\'');
                    break;
                case 9:
                    K('\"');
                    break;
                case 10:
                    M();
                    break;
                case 12:
                    K('\'');
                    if (i4 == 0) {
                        this.f4871w[this.f4870v - 1] = "<skipped>";
                        break;
                    }
                    break;
                case 13:
                    K('\"');
                    if (i4 == 0) {
                        this.f4871w[this.f4870v - 1] = "<skipped>";
                        break;
                    }
                    break;
                case 14:
                    M();
                    if (i4 == 0) {
                        this.f4871w[this.f4870v - 1] = "<skipped>";
                        break;
                    }
                    break;
                case 16:
                    this.f4861m += this.f4867s;
                    break;
                case 17:
                    return;
            }
            this.f4865q = 0;
        } while (i4 > 0);
        int[] iArr = this.f4872x;
        int i6 = this.f4870v - 1;
        iArr[i6] = iArr[i6] + 1;
    }

    public final void O(String str) {
        throw new IOException(str + t());
    }

    public void a() {
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 == 3) {
            I(1);
            this.f4872x[this.f4870v - 1] = 0;
            this.f4865q = 0;
            return;
        }
        throw new IllegalStateException("Expected BEGIN_ARRAY but was " + H() + t());
    }

    public void b() {
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 == 1) {
            I(3);
            this.f4865q = 0;
            return;
        }
        throw new IllegalStateException("Expected BEGIN_OBJECT but was " + H() + t());
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.f4865q = 0;
        this.f4869u[0] = 8;
        this.f4870v = 1;
        this.f4858j.close();
    }

    public final void d() {
        if (this.f4859k) {
            return;
        }
        O("Use JsonReader.setLenient(true) to accept malformed JSON");
        throw null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:124:0x019a, code lost:
        r1 = 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:167:0x0215, code lost:
        if (s(r1) != false) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:169:0x0218, code lost:
        if (r5 != 2) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:170:0x021a, code lost:
        if (r14 == false) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:172:0x0220, code lost:
        if (r9 != Long.MIN_VALUE) goto L46;
     */
    /* JADX WARN: Code restructure failed: missing block: B:173:0x0222, code lost:
        if (r16 == false) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:175:0x0225, code lost:
        r1 = 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:177:0x0229, code lost:
        if (r9 != 0) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:178:0x022b, code lost:
        if (r16 != false) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:179:0x022d, code lost:
        if (r16 == false) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:181:0x0230, code lost:
        r9 = -r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:182:0x0231, code lost:
        r20.f4866r = r9;
        r20.f4861m += r8;
        r9 = 15;
        r20.f4865q = 15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:183:0x023d, code lost:
        if (r5 == r1) goto L67;
     */
    /* JADX WARN: Code restructure failed: missing block: B:185:0x0240, code lost:
        if (r5 == 4) goto L67;
     */
    /* JADX WARN: Code restructure failed: missing block: B:187:0x0243, code lost:
        if (r5 != 7) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:188:0x0245, code lost:
        r20.f4867s = r8;
        r9 = 16;
        r20.f4865q = 16;
     */
    /* JADX WARN: Removed duplicated region for block: B:115:0x017b A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:116:0x017c  */
    /* JADX WARN: Removed duplicated region for block: B:207:0x027d A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:208:0x027e  */
    /* JADX WARN: Removed duplicated region for block: B:233:0x02c7  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x00e6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final int f() {
        /*
            Method dump skipped, instructions count: 817
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: j3.a.f():int");
    }

    public void g() {
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 == 4) {
            int i5 = this.f4870v;
            this.f4870v = i5 - 1;
            int[] iArr = this.f4872x;
            int i6 = i5 - 2;
            iArr[i6] = iArr[i6] + 1;
            this.f4865q = 0;
            return;
        }
        throw new IllegalStateException("Expected END_ARRAY but was " + H() + t());
    }

    public void i() {
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 == 2) {
            int i5 = this.f4870v;
            int i6 = i5 - 1;
            this.f4870v = i6;
            this.f4871w[i6] = null;
            int[] iArr = this.f4872x;
            int i7 = i5 - 2;
            iArr[i7] = iArr[i7] + 1;
            this.f4865q = 0;
            return;
        }
        throw new IllegalStateException("Expected END_OBJECT but was " + H() + t());
    }

    public final boolean k(int i4) {
        int i5;
        int i6;
        int i7 = this.f4864p;
        int i8 = this.f4861m;
        this.f4864p = i7 - i8;
        int i9 = this.f4862n;
        char[] cArr = this.f4860l;
        if (i9 != i8) {
            int i10 = i9 - i8;
            this.f4862n = i10;
            System.arraycopy(cArr, i8, cArr, 0, i10);
        } else {
            this.f4862n = 0;
        }
        this.f4861m = 0;
        do {
            int i11 = this.f4862n;
            int read = this.f4858j.read(cArr, i11, cArr.length - i11);
            if (read == -1) {
                return false;
            }
            i5 = this.f4862n + read;
            this.f4862n = i5;
            if (this.f4863o == 0 && (i6 = this.f4864p) == 0 && i5 > 0 && cArr[0] == 65279) {
                this.f4861m++;
                this.f4864p = i6 + 1;
                i4++;
                continue;
            }
        } while (i5 < i4);
        return true;
    }

    public String l() {
        return p(false);
    }

    public final String p(boolean z4) {
        StringBuilder sb = new StringBuilder("$");
        int i4 = 0;
        while (true) {
            int i5 = this.f4870v;
            if (i4 < i5) {
                int i6 = this.f4869u[i4];
                if (i6 != 1 && i6 != 2) {
                    if (i6 == 3 || i6 == 4 || i6 == 5) {
                        sb.append('.');
                        String str = this.f4871w[i4];
                        if (str != null) {
                            sb.append(str);
                        }
                    }
                } else {
                    int i7 = this.f4872x[i4];
                    if (z4 && i7 > 0 && i4 == i5 - 1) {
                        i7--;
                    }
                    sb.append('[');
                    sb.append(i7);
                    sb.append(']');
                }
                i4++;
            } else {
                return sb.toString();
            }
        }
    }

    public String q() {
        return p(true);
    }

    public boolean r() {
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 != 2 && i4 != 4 && i4 != 17) {
            return true;
        }
        return false;
    }

    public final boolean s(char c4) {
        if (c4 != '\t' && c4 != '\n' && c4 != '\f' && c4 != '\r' && c4 != ' ') {
            if (c4 != '#') {
                if (c4 != ',') {
                    if (c4 != '/' && c4 != '=') {
                        if (c4 != '{' && c4 != '}' && c4 != ':') {
                            if (c4 != ';') {
                                switch (c4) {
                                    case '[':
                                    case ']':
                                        return false;
                                    case '\\':
                                        break;
                                    default:
                                        return true;
                                }
                            }
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
            d();
            return false;
        }
        return false;
    }

    final String t() {
        return " at line " + (this.f4863o + 1) + " column " + ((this.f4861m - this.f4864p) + 1) + " path " + l();
    }

    public String toString() {
        return getClass().getSimpleName() + t();
    }

    public boolean u() {
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 == 5) {
            this.f4865q = 0;
            int[] iArr = this.f4872x;
            int i5 = this.f4870v - 1;
            iArr[i5] = iArr[i5] + 1;
            return true;
        } else if (i4 == 6) {
            this.f4865q = 0;
            int[] iArr2 = this.f4872x;
            int i6 = this.f4870v - 1;
            iArr2[i6] = iArr2[i6] + 1;
            return false;
        } else {
            throw new IllegalStateException("Expected a boolean but was " + H() + t());
        }
    }

    public double v() {
        char c4;
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 == 15) {
            this.f4865q = 0;
            int[] iArr = this.f4872x;
            int i5 = this.f4870v - 1;
            iArr[i5] = iArr[i5] + 1;
            return this.f4866r;
        }
        if (i4 == 16) {
            this.f4868t = new String(this.f4860l, this.f4861m, this.f4867s);
            this.f4861m += this.f4867s;
        } else if (i4 != 8 && i4 != 9) {
            if (i4 == 10) {
                this.f4868t = G();
            } else if (i4 != 11) {
                throw new IllegalStateException("Expected a double but was " + H() + t());
            }
        } else {
            if (i4 == 8) {
                c4 = '\'';
            } else {
                c4 = '\"';
            }
            this.f4868t = E(c4);
        }
        this.f4865q = 11;
        double parseDouble = Double.parseDouble(this.f4868t);
        if (!this.f4859k && (Double.isNaN(parseDouble) || Double.isInfinite(parseDouble))) {
            throw new IOException("JSON forbids NaN and infinities: " + parseDouble + t());
        }
        this.f4868t = null;
        this.f4865q = 0;
        int[] iArr2 = this.f4872x;
        int i6 = this.f4870v - 1;
        iArr2[i6] = iArr2[i6] + 1;
        return parseDouble;
    }

    public int w() {
        char c4;
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 == 15) {
            long j4 = this.f4866r;
            int i5 = (int) j4;
            if (j4 == i5) {
                this.f4865q = 0;
                int[] iArr = this.f4872x;
                int i6 = this.f4870v - 1;
                iArr[i6] = iArr[i6] + 1;
                return i5;
            }
            throw new NumberFormatException("Expected an int but was " + this.f4866r + t());
        }
        if (i4 == 16) {
            this.f4868t = new String(this.f4860l, this.f4861m, this.f4867s);
            this.f4861m += this.f4867s;
        } else if (i4 != 8 && i4 != 9 && i4 != 10) {
            throw new IllegalStateException("Expected an int but was " + H() + t());
        } else {
            if (i4 == 10) {
                this.f4868t = G();
            } else {
                if (i4 == 8) {
                    c4 = '\'';
                } else {
                    c4 = '\"';
                }
                this.f4868t = E(c4);
            }
            try {
                int parseInt = Integer.parseInt(this.f4868t);
                this.f4865q = 0;
                int[] iArr2 = this.f4872x;
                int i7 = this.f4870v - 1;
                iArr2[i7] = iArr2[i7] + 1;
                return parseInt;
            } catch (NumberFormatException unused) {
            }
        }
        this.f4865q = 11;
        double parseDouble = Double.parseDouble(this.f4868t);
        int i8 = (int) parseDouble;
        if (i8 == parseDouble) {
            this.f4868t = null;
            this.f4865q = 0;
            int[] iArr3 = this.f4872x;
            int i9 = this.f4870v - 1;
            iArr3[i9] = iArr3[i9] + 1;
            return i8;
        }
        throw new NumberFormatException("Expected an int but was " + this.f4868t + t());
    }

    public long y() {
        char c4;
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 == 15) {
            this.f4865q = 0;
            int[] iArr = this.f4872x;
            int i5 = this.f4870v - 1;
            iArr[i5] = iArr[i5] + 1;
            return this.f4866r;
        }
        if (i4 == 16) {
            this.f4868t = new String(this.f4860l, this.f4861m, this.f4867s);
            this.f4861m += this.f4867s;
        } else if (i4 != 8 && i4 != 9 && i4 != 10) {
            throw new IllegalStateException("Expected a long but was " + H() + t());
        } else {
            if (i4 == 10) {
                this.f4868t = G();
            } else {
                if (i4 == 8) {
                    c4 = '\'';
                } else {
                    c4 = '\"';
                }
                this.f4868t = E(c4);
            }
            try {
                long parseLong = Long.parseLong(this.f4868t);
                this.f4865q = 0;
                int[] iArr2 = this.f4872x;
                int i6 = this.f4870v - 1;
                iArr2[i6] = iArr2[i6] + 1;
                return parseLong;
            } catch (NumberFormatException unused) {
            }
        }
        this.f4865q = 11;
        double parseDouble = Double.parseDouble(this.f4868t);
        long j4 = (long) parseDouble;
        if (j4 == parseDouble) {
            this.f4868t = null;
            this.f4865q = 0;
            int[] iArr3 = this.f4872x;
            int i7 = this.f4870v - 1;
            iArr3[i7] = iArr3[i7] + 1;
            return j4;
        }
        throw new NumberFormatException("Expected a long but was " + this.f4868t + t());
    }

    public String z() {
        String E4;
        int i4 = this.f4865q;
        if (i4 == 0) {
            i4 = f();
        }
        if (i4 == 14) {
            E4 = G();
        } else if (i4 == 12) {
            E4 = E('\'');
        } else if (i4 == 13) {
            E4 = E('\"');
        } else {
            throw new IllegalStateException("Expected a name but was " + H() + t());
        }
        this.f4865q = 0;
        this.f4871w[this.f4870v - 1] = E4;
        return E4;
    }
}
