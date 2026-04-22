package j$.time;

import j$.util.concurrent.ConcurrentHashMap;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class B extends A implements j$.time.temporal.o, j$.time.temporal.p, Comparable {

    /* renamed from: c  reason: collision with root package name */
    private static final ConcurrentHashMap f3836c = new ConcurrentHashMap(16, 0.75f, 4);

    /* renamed from: d  reason: collision with root package name */
    private static final ConcurrentHashMap f3837d = new ConcurrentHashMap(16, 0.75f, 4);

    /* renamed from: e  reason: collision with root package name */
    public static final B f3838e = M(0);
    public static final B f = M(-64800);

    /* renamed from: g  reason: collision with root package name */
    public static final B f3839g = M(64800);
    private static final long serialVersionUID = 2357656521762053153L;

    /* renamed from: a  reason: collision with root package name */
    private final int f3840a;

    /* renamed from: b  reason: collision with root package name */
    private final transient String f3841b;

    private B(int i4) {
        String sb;
        this.f3840a = i4;
        if (i4 == 0) {
            sb = "Z";
        } else {
            int abs = Math.abs(i4);
            StringBuilder sb2 = new StringBuilder();
            int i5 = abs / 3600;
            int i6 = (abs / 60) % 60;
            sb2.append(i4 < 0 ? "-" : "+");
            sb2.append(i5 < 10 ? "0" : "");
            sb2.append(i5);
            sb2.append(i6 < 10 ? ":0" : ":");
            sb2.append(i6);
            int i7 = abs % 60;
            if (i7 != 0) {
                sb2.append(i7 < 10 ? ":0" : ":");
                sb2.append(i7);
            }
            sb = sb2.toString();
        }
        this.f3841b = sb;
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x008f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00a8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static j$.time.B K(java.lang.String r7) {
        /*
            java.lang.String r0 = "offsetId"
            j$.util.Objects.requireNonNull(r7, r0)
            j$.util.concurrent.ConcurrentHashMap r0 = j$.time.B.f3837d
            java.lang.Object r0 = r0.get(r7)
            j$.time.B r0 = (j$.time.B) r0
            if (r0 == 0) goto L10
            return r0
        L10:
            int r0 = r7.length()
            r1 = 2
            r2 = 1
            r3 = 0
            if (r0 == r1) goto L63
            r1 = 3
            if (r0 == r1) goto L7f
            r4 = 5
            if (r0 == r4) goto L5a
            r5 = 6
            r6 = 4
            if (r0 == r5) goto L50
            r5 = 7
            if (r0 == r5) goto L43
            r1 = 9
            if (r0 != r1) goto L37
            int r0 = N(r7, r2, r3)
            int r1 = N(r7, r6, r2)
            int r2 = N(r7, r5, r2)
            goto L85
        L37:
            j$.time.c r0 = new j$.time.c
            java.lang.String r1 = "Invalid ID for ZoneOffset, invalid format: "
            java.lang.String r7 = r1.concat(r7)
            r0.<init>(r7)
            throw r0
        L43:
            int r0 = N(r7, r2, r3)
            int r1 = N(r7, r1, r3)
            int r2 = N(r7, r4, r3)
            goto L85
        L50:
            int r0 = N(r7, r2, r3)
            int r1 = N(r7, r6, r2)
        L58:
            r2 = 0
            goto L85
        L5a:
            int r0 = N(r7, r2, r3)
            int r1 = N(r7, r1, r3)
            goto L58
        L63:
            char r0 = r7.charAt(r3)
            char r7 = r7.charAt(r2)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = "0"
            r1.append(r0)
            r1.append(r7)
            java.lang.String r7 = r1.toString()
        L7f:
            int r0 = N(r7, r2, r3)
            r1 = 0
            goto L58
        L85:
            char r3 = r7.charAt(r3)
            r4 = 43
            r5 = 45
            if (r3 == r4) goto L9e
            if (r3 != r5) goto L92
            goto L9e
        L92:
            j$.time.c r0 = new j$.time.c
            java.lang.String r1 = "Invalid ID for ZoneOffset, plus/minus not found when expected: "
            java.lang.String r7 = r1.concat(r7)
            r0.<init>(r7)
            throw r0
        L9e:
            if (r3 != r5) goto La8
            int r7 = -r0
            int r0 = -r1
            int r1 = -r2
            j$.time.B r7 = L(r7, r0, r1)
            return r7
        La8:
            j$.time.B r7 = L(r0, r1, r2)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.B.K(java.lang.String):j$.time.B");
    }

    public static B L(int i4, int i5, int i6) {
        if (i4 < -18 || i4 > 18) {
            throw new RuntimeException("Zone offset hours not in valid range: value " + i4 + " is not in the range -18 to 18");
        }
        if (i4 > 0) {
            if (i5 < 0 || i6 < 0) {
                throw new RuntimeException("Zone offset minutes and seconds must be positive because hours is positive");
            }
        } else if (i4 < 0) {
            if (i5 > 0 || i6 > 0) {
                throw new RuntimeException("Zone offset minutes and seconds must be negative because hours is negative");
            }
        } else if ((i5 > 0 && i6 < 0) || (i5 < 0 && i6 > 0)) {
            throw new RuntimeException("Zone offset minutes and seconds must have the same sign");
        }
        if (i5 < -59 || i5 > 59) {
            throw new RuntimeException("Zone offset minutes not in valid range: value " + i5 + " is not in the range -59 to 59");
        } else if (i6 < -59 || i6 > 59) {
            throw new RuntimeException("Zone offset seconds not in valid range: value " + i6 + " is not in the range -59 to 59");
        } else if (Math.abs(i4) != 18 || (i5 | i6) == 0) {
            return M((i5 * 60) + (i4 * 3600) + i6);
        } else {
            throw new RuntimeException("Zone offset not in valid range: -18:00 to +18:00");
        }
    }

    public static B M(int i4) {
        if (i4 < -64800 || i4 > 64800) {
            throw new RuntimeException("Zone offset not in valid range: -18:00 to +18:00");
        }
        if (i4 % 900 == 0) {
            Integer valueOf = Integer.valueOf(i4);
            ConcurrentHashMap concurrentHashMap = f3836c;
            B b4 = (B) concurrentHashMap.get(valueOf);
            if (b4 == null) {
                concurrentHashMap.putIfAbsent(valueOf, new B(i4));
                B b5 = (B) concurrentHashMap.get(valueOf);
                f3837d.putIfAbsent(b5.f3841b, b5);
                return b5;
            }
            return b4;
        }
        return new B(i4);
    }

    private static int N(CharSequence charSequence, int i4, boolean z4) {
        if (z4 && charSequence.charAt(i4 - 1) != ':') {
            throw new RuntimeException("Invalid ID for ZoneOffset, colon not found when expected: " + ((Object) charSequence));
        }
        char charAt = charSequence.charAt(i4);
        char charAt2 = charSequence.charAt(i4 + 1);
        if (charAt >= '0' && charAt <= '9' && charAt2 >= '0' && charAt2 <= '9') {
            return (charAt2 - '0') + ((charAt - '0') * 10);
        }
        throw new RuntimeException("Invalid ID for ZoneOffset, non numeric characters found: " + ((Object) charSequence));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static B O(DataInput dataInput) {
        byte readByte = dataInput.readByte();
        return readByte == Byte.MAX_VALUE ? M(dataInput.readInt()) : M(readByte * 900);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 8, this);
    }

    @Override // j$.time.A
    public final j$.time.zone.f D() {
        return j$.time.zone.f.i(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.time.A
    public final void H(DataOutput dataOutput) {
        dataOutput.writeByte(8);
        P(dataOutput);
    }

    @Override // java.lang.Comparable
    /* renamed from: I */
    public final int compareTo(B b4) {
        return b4.f3840a - this.f3840a;
    }

    public final int J() {
        return this.f3840a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void P(DataOutput dataOutput) {
        int i4 = this.f3840a;
        int i5 = i4 % 900 == 0 ? i4 / 900 : 127;
        dataOutput.writeByte(i5);
        if (i5 == 127) {
            dataOutput.writeInt(i4);
        }
    }

    @Override // j$.time.A
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof B) {
            return this.f3840a == ((B) obj).f3840a;
        }
        return false;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? rVar == j$.time.temporal.a.OFFSET_SECONDS : rVar != null && rVar.m(this);
    }

    @Override // j$.time.A
    public final int hashCode() {
        return this.f3840a;
    }

    @Override // j$.time.A
    public final String i() {
        return this.f3841b;
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        if (rVar == j$.time.temporal.a.OFFSET_SECONDS) {
            return this.f3840a;
        }
        if (rVar instanceof j$.time.temporal.a) {
            throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
        return j$.time.temporal.n.d(this, rVar).a(r(rVar), rVar);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        return j$.time.temporal.n.d(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        if (rVar == j$.time.temporal.a.OFFSET_SECONDS) {
            return this.f3840a;
        }
        if (rVar instanceof j$.time.temporal.a) {
            throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
        return rVar.l(this);
    }

    @Override // j$.time.A
    public final String toString() {
        return this.f3841b;
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        return (tVar == j$.time.temporal.n.h() || tVar == j$.time.temporal.n.j()) ? this : j$.time.temporal.n.c(this, tVar);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        return mVar.d(this.f3840a, j$.time.temporal.a.OFFSET_SECONDS);
    }
}
