package j$.time.zone;

import j$.time.B;
import j$.time.EnumC0496e;
import j$.time.chrono.u;
import j$.time.k;
import j$.time.m;
import j$.time.o;
import j$.time.temporal.p;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class e implements Serializable {
    private static final long serialVersionUID = 6889046316657758795L;

    /* renamed from: a  reason: collision with root package name */
    private final o f4054a;

    /* renamed from: b  reason: collision with root package name */
    private final byte f4055b;

    /* renamed from: c  reason: collision with root package name */
    private final EnumC0496e f4056c;

    /* renamed from: d  reason: collision with root package name */
    private final m f4057d;

    /* renamed from: e  reason: collision with root package name */
    private final boolean f4058e;
    private final d f;

    /* renamed from: g  reason: collision with root package name */
    private final B f4059g;

    /* renamed from: h  reason: collision with root package name */
    private final B f4060h;

    /* renamed from: i  reason: collision with root package name */
    private final B f4061i;

    e(o oVar, int i4, EnumC0496e enumC0496e, m mVar, boolean z4, d dVar, B b4, B b5, B b6) {
        this.f4054a = oVar;
        this.f4055b = (byte) i4;
        this.f4056c = enumC0496e;
        this.f4057d = mVar;
        this.f4058e = z4;
        this.f = dVar;
        this.f4059g = b4;
        this.f4060h = b5;
        this.f4061i = b6;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static e b(DataInput dataInput) {
        int readInt = dataInput.readInt();
        o G4 = o.G(readInt >>> 28);
        int i4 = ((264241152 & readInt) >>> 22) - 32;
        int i5 = (3670016 & readInt) >>> 19;
        EnumC0496e D4 = i5 == 0 ? null : EnumC0496e.D(i5);
        int i6 = (507904 & readInt) >>> 14;
        d dVar = d.values()[(readInt & 12288) >>> 12];
        int i7 = (readInt & 4080) >>> 4;
        int i8 = (readInt & 12) >>> 2;
        int i9 = readInt & 3;
        m M3 = i6 == 31 ? m.M(dataInput.readInt()) : m.K(i6 % 24);
        B M4 = B.M(i7 == 255 ? dataInput.readInt() : (i7 - 128) * 900);
        B M5 = i8 == 3 ? B.M(dataInput.readInt()) : B.M((i8 * 1800) + M4.J());
        B M6 = i9 == 3 ? B.M(dataInput.readInt()) : B.M((i9 * 1800) + M4.J());
        boolean z4 = i6 == 24;
        Objects.requireNonNull(G4, "month");
        Objects.requireNonNull(M3, "time");
        Objects.requireNonNull(dVar, "timeDefnition");
        Objects.requireNonNull(M4, "standardOffset");
        Objects.requireNonNull(M5, "offsetBefore");
        Objects.requireNonNull(M6, "offsetAfter");
        if (i4 < -28 || i4 > 31 || i4 == 0) {
            throw new IllegalArgumentException("Day of month indicator must be between -28 and 31 inclusive excluding zero");
        }
        if (!z4 || M3.equals(m.f3984g)) {
            if (M3.I() == 0) {
                return new e(G4, i4, D4, M3, z4, dVar, M4, M5, M6);
            }
            throw new IllegalArgumentException("Time's nano-of-second must be zero");
        }
        throw new IllegalArgumentException("Time must be midnight when end of day flag is true");
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new a((byte) 3, this);
    }

    public final b a(int i4) {
        j$.time.i P3;
        p pVar;
        int J3;
        B b4;
        EnumC0496e enumC0496e = this.f4056c;
        o oVar = this.f4054a;
        byte b5 = this.f4055b;
        if (b5 < 0) {
            u.f3906d.getClass();
            P3 = j$.time.i.P(i4, oVar, oVar.E(u.m(i4)) + 1 + b5);
            if (enumC0496e != null) {
                final int value = enumC0496e.getValue();
                pVar = new p() { // from class: j$.time.temporal.q
                    @Override // j$.time.temporal.p
                    public final m v(m mVar) {
                        switch (r2) {
                            case 0:
                                int j4 = mVar.j(a.DAY_OF_WEEK);
                                int i5 = value;
                                if (j4 == i5) {
                                    return mVar;
                                }
                                int i6 = j4 - i5;
                                return mVar.e(i6 >= 0 ? 7 - i6 : -i6, b.DAYS);
                            default:
                                int j5 = mVar.j(a.DAY_OF_WEEK);
                                int i7 = value;
                                if (j5 == i7) {
                                    return mVar;
                                }
                                int i8 = i7 - j5;
                                return mVar.z(i8 >= 0 ? 7 - i8 : -i8, b.DAYS);
                        }
                    }
                };
                P3 = P3.l(pVar);
            }
        } else {
            P3 = j$.time.i.P(i4, oVar, b5);
            if (enumC0496e != null) {
                final int value2 = enumC0496e.getValue();
                pVar = new p() { // from class: j$.time.temporal.q
                    @Override // j$.time.temporal.p
                    public final m v(m mVar) {
                        switch (r2) {
                            case 0:
                                int j4 = mVar.j(a.DAY_OF_WEEK);
                                int i5 = value2;
                                if (j4 == i5) {
                                    return mVar;
                                }
                                int i6 = j4 - i5;
                                return mVar.e(i6 >= 0 ? 7 - i6 : -i6, b.DAYS);
                            default:
                                int j5 = mVar.j(a.DAY_OF_WEEK);
                                int i7 = value2;
                                if (j5 == i7) {
                                    return mVar;
                                }
                                int i8 = i7 - j5;
                                return mVar.z(i8 >= 0 ? 7 - i8 : -i8, b.DAYS);
                        }
                    }
                };
                P3 = P3.l(pVar);
            }
        }
        if (this.f4058e) {
            P3 = P3.S(1L);
        }
        k L3 = k.L(P3, this.f4057d);
        d dVar = this.f;
        dVar.getClass();
        int i5 = c.f4052a[dVar.ordinal()];
        B b6 = this.f4060h;
        if (i5 != 1) {
            if (i5 == 2) {
                J3 = b6.J();
                b4 = this.f4059g;
            }
            return new b(L3, b6, this.f4061i);
        }
        J3 = b6.J();
        b4 = B.f3838e;
        L3 = L3.O(J3 - b4.J());
        return new b(L3, b6, this.f4061i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void c(DataOutput dataOutput) {
        m mVar = this.f4057d;
        boolean z4 = this.f4058e;
        int U3 = z4 ? 86400 : mVar.U();
        int J3 = this.f4059g.J();
        B b4 = this.f4060h;
        int J4 = b4.J() - J3;
        B b5 = this.f4061i;
        int J5 = b5.J() - J3;
        int H4 = U3 % 3600 == 0 ? z4 ? 24 : mVar.H() : 31;
        int i4 = J3 % 900 == 0 ? (J3 / 900) + 128 : 255;
        int i5 = (J4 == 0 || J4 == 1800 || J4 == 3600) ? J4 / 1800 : 3;
        int i6 = (J5 == 0 || J5 == 1800 || J5 == 3600) ? J5 / 1800 : 3;
        EnumC0496e enumC0496e = this.f4056c;
        dataOutput.writeInt((this.f4054a.getValue() << 28) + ((this.f4055b + 32) << 22) + ((enumC0496e == null ? 0 : enumC0496e.getValue()) << 19) + (H4 << 14) + (this.f.ordinal() << 12) + (i4 << 4) + (i5 << 2) + i6);
        if (H4 == 31) {
            dataOutput.writeInt(U3);
        }
        if (i4 == 255) {
            dataOutput.writeInt(J3);
        }
        if (i5 == 3) {
            dataOutput.writeInt(b4.J());
        }
        if (i6 == 3) {
            dataOutput.writeInt(b5.J());
        }
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof e) {
            e eVar = (e) obj;
            return this.f4054a == eVar.f4054a && this.f4055b == eVar.f4055b && this.f4056c == eVar.f4056c && this.f == eVar.f && this.f4057d.equals(eVar.f4057d) && this.f4058e == eVar.f4058e && this.f4059g.equals(eVar.f4059g) && this.f4060h.equals(eVar.f4060h) && this.f4061i.equals(eVar.f4061i);
        }
        return false;
    }

    public final int hashCode() {
        int U3 = ((this.f4057d.U() + (this.f4058e ? 1 : 0)) << 15) + (this.f4054a.ordinal() << 11) + ((this.f4055b + 32) << 5);
        EnumC0496e enumC0496e = this.f4056c;
        return ((this.f4059g.hashCode() ^ (this.f.ordinal() + (U3 + ((enumC0496e == null ? 7 : enumC0496e.ordinal()) << 2)))) ^ this.f4060h.hashCode()) ^ this.f4061i.hashCode();
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0083  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0086  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.String toString() {
        /*
            r6 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r1 = "TransitionRule["
            r0.<init>(r1)
            j$.time.B r1 = r6.f4060h
            j$.time.B r2 = r6.f4061i
            int r3 = r1.compareTo(r2)
            if (r3 <= 0) goto L14
            java.lang.String r3 = "Gap "
            goto L16
        L14:
            java.lang.String r3 = "Overlap "
        L16:
            r0.append(r3)
            r0.append(r1)
            java.lang.String r1 = " to "
            r0.append(r1)
            r0.append(r2)
            java.lang.String r1 = ", "
            r0.append(r1)
            r1 = 32
            j$.time.o r2 = r6.f4054a
            byte r3 = r6.f4055b
            j$.time.e r4 = r6.f4056c
            if (r4 == 0) goto L6d
            r5 = -1
            if (r3 != r5) goto L4a
            java.lang.String r1 = r4.name()
            r0.append(r1)
            java.lang.String r1 = " on or before last day of "
        L3f:
            r0.append(r1)
            java.lang.String r1 = r2.name()
            r0.append(r1)
            goto L7a
        L4a:
            if (r3 >= 0) goto L61
            java.lang.String r1 = r4.name()
            r0.append(r1)
            java.lang.String r1 = " on or before last day minus "
            r0.append(r1)
            int r1 = -r3
            int r1 = r1 + (-1)
            r0.append(r1)
            java.lang.String r1 = " of "
            goto L3f
        L61:
            java.lang.String r4 = r4.name()
            r0.append(r4)
            java.lang.String r4 = " on or after "
            r0.append(r4)
        L6d:
            java.lang.String r2 = r2.name()
            r0.append(r2)
            r0.append(r1)
            r0.append(r3)
        L7a:
            java.lang.String r1 = " at "
            r0.append(r1)
            boolean r1 = r6.f4058e
            if (r1 == 0) goto L86
            java.lang.String r1 = "24:00"
            goto L8c
        L86:
            j$.time.m r1 = r6.f4057d
            java.lang.String r1 = r1.toString()
        L8c:
            r0.append(r1)
            java.lang.String r1 = " "
            r0.append(r1)
            j$.time.zone.d r1 = r6.f
            r0.append(r1)
            java.lang.String r1 = ", standard offset "
            r0.append(r1)
            j$.time.B r1 = r6.f4059g
            r0.append(r1)
            r1 = 93
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.zone.e.toString():java.lang.String");
    }
}
