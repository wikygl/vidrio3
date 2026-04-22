package j3;

import j$.util.Objects;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class c implements Closeable, Flushable {

    /* renamed from: s  reason: collision with root package name */
    public static final Pattern f4884s = Pattern.compile("-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][-+]?[0-9]+)?");

    /* renamed from: t  reason: collision with root package name */
    public static final String[] f4885t = new String[128];

    /* renamed from: u  reason: collision with root package name */
    public static final String[] f4886u;

    /* renamed from: j  reason: collision with root package name */
    public final Writer f4887j;

    /* renamed from: k  reason: collision with root package name */
    public int[] f4888k;

    /* renamed from: l  reason: collision with root package name */
    public int f4889l;

    /* renamed from: m  reason: collision with root package name */
    public String f4890m;

    /* renamed from: n  reason: collision with root package name */
    public String f4891n;

    /* renamed from: o  reason: collision with root package name */
    public boolean f4892o;

    /* renamed from: p  reason: collision with root package name */
    public boolean f4893p;

    /* renamed from: q  reason: collision with root package name */
    public String f4894q;

    /* renamed from: r  reason: collision with root package name */
    public boolean f4895r;

    static {
        for (int i4 = 0; i4 <= 31; i4++) {
            f4885t[i4] = String.format("\\u%04x", Integer.valueOf(i4));
        }
        String[] strArr = f4885t;
        strArr[34] = "\\\"";
        strArr[92] = "\\\\";
        strArr[9] = "\\t";
        strArr[8] = "\\b";
        strArr[10] = "\\n";
        strArr[13] = "\\r";
        strArr[12] = "\\f";
        String[] strArr2 = (String[]) strArr.clone();
        f4886u = strArr2;
        strArr2[60] = "\\u003c";
        strArr2[62] = "\\u003e";
        strArr2[38] = "\\u0026";
        strArr2[61] = "\\u003d";
        strArr2[39] = "\\u0027";
    }

    public c(Writer writer) {
        int[] iArr = new int[32];
        this.f4888k = iArr;
        this.f4889l = 0;
        if (iArr.length == 0) {
            this.f4888k = Arrays.copyOf(iArr, 0);
        }
        int[] iArr2 = this.f4888k;
        int i4 = this.f4889l;
        this.f4889l = i4 + 1;
        iArr2[i4] = 6;
        this.f4891n = ":";
        this.f4895r = true;
        Objects.requireNonNull(writer, "out == null");
        this.f4887j = writer;
    }

    public final void a() {
        int q4 = q();
        if (q4 != 1) {
            Writer writer = this.f4887j;
            if (q4 != 2) {
                if (q4 != 4) {
                    if (q4 != 6) {
                        if (q4 == 7) {
                            if (!this.f4892o) {
                                throw new IllegalStateException("JSON must have only one top-level value.");
                            }
                        } else {
                            throw new IllegalStateException("Nesting problem.");
                        }
                    }
                    this.f4888k[this.f4889l - 1] = 7;
                    return;
                }
                writer.append((CharSequence) this.f4891n);
                this.f4888k[this.f4889l - 1] = 5;
                return;
            }
            writer.append(',');
            l();
            return;
        }
        this.f4888k[this.f4889l - 1] = 2;
        l();
    }

    public void b() {
        z();
        a();
        int i4 = this.f4889l;
        int[] iArr = this.f4888k;
        if (i4 == iArr.length) {
            this.f4888k = Arrays.copyOf(iArr, i4 * 2);
        }
        int[] iArr2 = this.f4888k;
        int i5 = this.f4889l;
        this.f4889l = i5 + 1;
        iArr2[i5] = 1;
        this.f4887j.write(91);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.f4887j.close();
        int i4 = this.f4889l;
        if (i4 <= 1 && (i4 != 1 || this.f4888k[i4 - 1] == 7)) {
            this.f4889l = 0;
            return;
        }
        throw new IOException("Incomplete document");
    }

    public void d() {
        z();
        a();
        int i4 = this.f4889l;
        int[] iArr = this.f4888k;
        if (i4 == iArr.length) {
            this.f4888k = Arrays.copyOf(iArr, i4 * 2);
        }
        int[] iArr2 = this.f4888k;
        int i5 = this.f4889l;
        this.f4889l = i5 + 1;
        iArr2[i5] = 3;
        this.f4887j.write(123);
    }

    public final void f(int i4, int i5, char c4) {
        int q4 = q();
        if (q4 != i5 && q4 != i4) {
            throw new IllegalStateException("Nesting problem.");
        }
        if (this.f4894q == null) {
            this.f4889l--;
            if (q4 == i5) {
                l();
            }
            this.f4887j.write(c4);
            return;
        }
        throw new IllegalStateException("Dangling name: " + this.f4894q);
    }

    @Override // java.io.Flushable
    public void flush() {
        if (this.f4889l != 0) {
            this.f4887j.flush();
            return;
        }
        throw new IllegalStateException("JsonWriter is closed.");
    }

    public void g() {
        f(1, 2, ']');
    }

    public void i() {
        f(3, 5, '}');
    }

    public void k(String str) {
        Objects.requireNonNull(str, "name == null");
        if (this.f4894q == null) {
            if (this.f4889l != 0) {
                this.f4894q = str;
                return;
            }
            throw new IllegalStateException("JsonWriter is closed.");
        }
        throw new IllegalStateException();
    }

    public final void l() {
        if (this.f4890m == null) {
            return;
        }
        Writer writer = this.f4887j;
        writer.write(10);
        int i4 = this.f4889l;
        for (int i5 = 1; i5 < i4; i5++) {
            writer.write(this.f4890m);
        }
    }

    public c p() {
        if (this.f4894q != null) {
            if (this.f4895r) {
                z();
            } else {
                this.f4894q = null;
                return this;
            }
        }
        a();
        this.f4887j.write("null");
        return this;
    }

    public final int q() {
        int i4 = this.f4889l;
        if (i4 != 0) {
            return this.f4888k[i4 - 1];
        }
        throw new IllegalStateException("JsonWriter is closed.");
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0034  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void r(java.lang.String r9) {
        /*
            r8 = this;
            boolean r0 = r8.f4893p
            if (r0 == 0) goto L7
            java.lang.String[] r0 = j3.c.f4886u
            goto L9
        L7:
            java.lang.String[] r0 = j3.c.f4885t
        L9:
            java.io.Writer r1 = r8.f4887j
            r2 = 34
            r1.write(r2)
            int r3 = r9.length()
            r4 = 0
            r5 = 0
        L16:
            if (r4 >= r3) goto L41
            char r6 = r9.charAt(r4)
            r7 = 128(0x80, float:1.794E-43)
            if (r6 >= r7) goto L25
            r6 = r0[r6]
            if (r6 != 0) goto L32
            goto L3e
        L25:
            r7 = 8232(0x2028, float:1.1535E-41)
            if (r6 != r7) goto L2c
            java.lang.String r6 = "\\u2028"
            goto L32
        L2c:
            r7 = 8233(0x2029, float:1.1537E-41)
            if (r6 != r7) goto L3e
            java.lang.String r6 = "\\u2029"
        L32:
            if (r5 >= r4) goto L39
            int r7 = r4 - r5
            r1.write(r9, r5, r7)
        L39:
            r1.write(r6)
            int r5 = r4 + 1
        L3e:
            int r4 = r4 + 1
            goto L16
        L41:
            if (r5 >= r3) goto L47
            int r3 = r3 - r5
            r1.write(r9, r5, r3)
        L47:
            r1.write(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j3.c.r(java.lang.String):void");
    }

    public void s(double d4) {
        z();
        if (!this.f4892o && (Double.isNaN(d4) || Double.isInfinite(d4))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + d4);
        }
        a();
        this.f4887j.append((CharSequence) Double.toString(d4));
    }

    public void t(long j4) {
        z();
        a();
        this.f4887j.write(Long.toString(j4));
    }

    public void u(Boolean bool) {
        String str;
        if (bool == null) {
            p();
            return;
        }
        z();
        a();
        if (bool.booleanValue()) {
            str = "true";
        } else {
            str = "false";
        }
        this.f4887j.write(str);
    }

    public void v(Number number) {
        if (number == null) {
            p();
            return;
        }
        z();
        String obj = number.toString();
        if (!obj.equals("-Infinity") && !obj.equals("Infinity") && !obj.equals("NaN")) {
            Class<?> cls = number.getClass();
            if (cls != Integer.class && cls != Long.class && cls != Double.class && cls != Float.class && cls != Byte.class && cls != Short.class && cls != BigDecimal.class && cls != BigInteger.class && cls != AtomicInteger.class && cls != AtomicLong.class && !f4884s.matcher(obj).matches()) {
                throw new IllegalArgumentException("String created by " + cls + " is not a valid JSON number: " + obj);
            }
        } else if (!this.f4892o) {
            throw new IllegalArgumentException("Numeric values must be finite, but was ".concat(obj));
        }
        a();
        this.f4887j.append((CharSequence) obj);
    }

    public void w(String str) {
        if (str == null) {
            p();
            return;
        }
        z();
        a();
        r(str);
    }

    public void y(boolean z4) {
        String str;
        z();
        a();
        if (z4) {
            str = "true";
        } else {
            str = "false";
        }
        this.f4887j.write(str);
    }

    public final void z() {
        if (this.f4894q != null) {
            int q4 = q();
            if (q4 == 5) {
                this.f4887j.write(44);
            } else if (q4 != 3) {
                throw new IllegalStateException("Nesting problem.");
            }
            l();
            this.f4888k[this.f4889l - 1] = 4;
            r(this.f4894q);
            this.f4894q = null;
        }
    }
}
