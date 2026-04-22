package j$.time.zone;

import j$.time.AbstractC0481b;
import j$.time.B;
import j$.time.chrono.AbstractC0491i;
import j$.time.k;
import j$.util.Objects;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class f implements Serializable {

    /* renamed from: i  reason: collision with root package name */
    private static final long[] f4062i = new long[0];

    /* renamed from: j  reason: collision with root package name */
    private static final e[] f4063j = new e[0];

    /* renamed from: k  reason: collision with root package name */
    private static final k[] f4064k = new k[0];

    /* renamed from: l  reason: collision with root package name */
    private static final b[] f4065l = new b[0];
    private static final long serialVersionUID = 3044319355680032515L;

    /* renamed from: a  reason: collision with root package name */
    private final long[] f4066a;

    /* renamed from: b  reason: collision with root package name */
    private final B[] f4067b;

    /* renamed from: c  reason: collision with root package name */
    private final long[] f4068c;

    /* renamed from: d  reason: collision with root package name */
    private final k[] f4069d;

    /* renamed from: e  reason: collision with root package name */
    private final B[] f4070e;
    private final e[] f;

    /* renamed from: g  reason: collision with root package name */
    private final TimeZone f4071g;

    /* renamed from: h  reason: collision with root package name */
    private final transient ConcurrentHashMap f4072h = new ConcurrentHashMap();

    private f(B b4) {
        this.f4067b = r0;
        B[] bArr = {b4};
        long[] jArr = f4062i;
        this.f4066a = jArr;
        this.f4068c = jArr;
        this.f4069d = f4064k;
        this.f4070e = bArr;
        this.f = f4063j;
        this.f4071g = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public f(TimeZone timeZone) {
        this.f4067b = r0;
        B[] bArr = {j(timeZone.getRawOffset())};
        long[] jArr = f4062i;
        this.f4066a = jArr;
        this.f4068c = jArr;
        this.f4069d = f4064k;
        this.f4070e = bArr;
        this.f = f4063j;
        this.f4071g = timeZone;
    }

    private f(long[] jArr, B[] bArr, long[] jArr2, B[] bArr2, e[] eVarArr) {
        k l2;
        this.f4066a = jArr;
        this.f4067b = bArr;
        this.f4068c = jArr2;
        this.f4070e = bArr2;
        this.f = eVarArr;
        if (jArr2.length == 0) {
            this.f4069d = f4064k;
        } else {
            ArrayList arrayList = new ArrayList();
            int i4 = 0;
            while (i4 < jArr2.length) {
                int i5 = i4 + 1;
                b bVar = new b(jArr2[i4], bArr2[i4], bArr2[i5]);
                if (bVar.z()) {
                    arrayList.add(bVar.l());
                    l2 = bVar.j();
                } else {
                    arrayList.add(bVar.j());
                    l2 = bVar.l();
                }
                arrayList.add(l2);
                i4 = i5;
            }
            this.f4069d = (k[]) arrayList.toArray(new k[arrayList.size()]);
        }
        this.f4071g = null;
    }

    private static Object a(k kVar, b bVar) {
        k l2 = bVar.l();
        boolean z4 = bVar.z();
        boolean J3 = kVar.J(l2);
        return z4 ? J3 ? bVar.u() : kVar.J(bVar.j()) ? bVar : bVar.r() : !J3 ? bVar.r() : kVar.J(bVar.j()) ? bVar.u() : bVar;
    }

    private b[] b(int i4) {
        long j4;
        Integer valueOf = Integer.valueOf(i4);
        ConcurrentHashMap concurrentHashMap = this.f4072h;
        b[] bVarArr = (b[]) concurrentHashMap.get(valueOf);
        if (bVarArr != null) {
            return bVarArr;
        }
        TimeZone timeZone = this.f4071g;
        if (timeZone == null) {
            e[] eVarArr = this.f;
            b[] bVarArr2 = new b[eVarArr.length];
            for (int i5 = 0; i5 < eVarArr.length; i5++) {
                bVarArr2[i5] = eVarArr[i5].a(i4);
            }
            if (i4 < 2100) {
                concurrentHashMap.putIfAbsent(valueOf, bVarArr2);
            }
            return bVarArr2;
        }
        b[] bVarArr3 = f4065l;
        if (i4 < 1800) {
            return bVarArr3;
        }
        long n4 = AbstractC0491i.n(k.K(i4 - 1), this.f4067b[0]);
        int offset = timeZone.getOffset(n4 * 1000);
        long j5 = 31968000 + n4;
        while (n4 < j5) {
            long j6 = 7776000 + n4;
            long j7 = n4;
            if (offset != timeZone.getOffset(j6 * 1000)) {
                n4 = j7;
                while (j6 - n4 > 1) {
                    int i6 = offset;
                    long j8 = j5;
                    long n5 = j$.com.android.tools.r8.a.n(j6 + n4, 2L);
                    if (timeZone.getOffset(n5 * 1000) == i6) {
                        n4 = n5;
                    } else {
                        j6 = n5;
                    }
                    offset = i6;
                    j5 = j8;
                }
                j4 = j5;
                int i7 = offset;
                if (timeZone.getOffset(n4 * 1000) == i7) {
                    n4 = j6;
                }
                B j9 = j(i7);
                offset = timeZone.getOffset(n4 * 1000);
                B j10 = j(offset);
                if (c(n4, j10) == i4) {
                    bVarArr3 = (b[]) Arrays.copyOf(bVarArr3, bVarArr3.length + 1);
                    bVarArr3[bVarArr3.length - 1] = new b(n4, j9, j10);
                }
            } else {
                j4 = j5;
                n4 = j6;
            }
            j5 = j4;
        }
        if (1916 <= i4 && i4 < 2100) {
            concurrentHashMap.putIfAbsent(valueOf, bVarArr3);
        }
        return bVarArr3;
    }

    private static int c(long j4, B b4) {
        return j$.time.i.Q(j$.com.android.tools.r8.a.n(j4 + b4.J(), 86400)).K();
    }

    private Object e(k kVar) {
        Object obj = null;
        B[] bArr = this.f4067b;
        int i4 = 0;
        TimeZone timeZone = this.f4071g;
        if (timeZone != null) {
            b[] b4 = b(kVar.H());
            if (b4.length == 0) {
                return j(timeZone.getOffset(AbstractC0491i.n(kVar, bArr[0]) * 1000));
            }
            int length = b4.length;
            while (i4 < length) {
                b bVar = b4[i4];
                Object a4 = a(kVar, bVar);
                if ((a4 instanceof b) || a4.equals(bVar.u())) {
                    return a4;
                }
                i4++;
                obj = a4;
            }
            return obj;
        } else if (this.f4068c.length == 0) {
            return bArr[0];
        } else {
            int length2 = this.f.length;
            k[] kVarArr = this.f4069d;
            if (length2 > 0 && kVar.I(kVarArr[kVarArr.length - 1])) {
                b[] b5 = b(kVar.H());
                int length3 = b5.length;
                while (i4 < length3) {
                    b bVar2 = b5[i4];
                    Object a5 = a(kVar, bVar2);
                    if ((a5 instanceof b) || a5.equals(bVar2.u())) {
                        return a5;
                    }
                    i4++;
                    obj = a5;
                }
                return obj;
            }
            int binarySearch = Arrays.binarySearch(kVarArr, kVar);
            B[] bArr2 = this.f4070e;
            if (binarySearch == -1) {
                return bArr2[0];
            }
            if (binarySearch < 0) {
                binarySearch = (-binarySearch) - 2;
            } else if (binarySearch < kVarArr.length - 1) {
                int i5 = binarySearch + 1;
                if (kVarArr[binarySearch].equals(kVarArr[i5])) {
                    binarySearch = i5;
                }
            }
            if ((binarySearch & 1) == 0) {
                k kVar2 = kVarArr[binarySearch];
                k kVar3 = kVarArr[binarySearch + 1];
                int i6 = binarySearch / 2;
                B b6 = bArr2[i6];
                B b7 = bArr2[i6 + 1];
                return b7.J() > b6.J() ? new b(kVar2, b6, b7) : new b(kVar3, b6, b7);
            }
            return bArr2[(binarySearch / 2) + 1];
        }
    }

    public static f i(B b4) {
        Objects.requireNonNull(b4, "offset");
        return new f(b4);
    }

    private static B j(int i4) {
        return B.M(i4 / 1000);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static f k(DataInput dataInput) {
        int readInt = dataInput.readInt();
        long[] jArr = f4062i;
        long[] jArr2 = readInt == 0 ? jArr : new long[readInt];
        for (int i4 = 0; i4 < readInt; i4++) {
            jArr2[i4] = a.a(dataInput);
        }
        int i5 = readInt + 1;
        B[] bArr = new B[i5];
        for (int i6 = 0; i6 < i5; i6++) {
            bArr[i6] = a.b(dataInput);
        }
        int readInt2 = dataInput.readInt();
        if (readInt2 != 0) {
            jArr = new long[readInt2];
        }
        long[] jArr3 = jArr;
        for (int i7 = 0; i7 < readInt2; i7++) {
            jArr3[i7] = a.a(dataInput);
        }
        int i8 = readInt2 + 1;
        B[] bArr2 = new B[i8];
        for (int i9 = 0; i9 < i8; i9++) {
            bArr2[i9] = a.b(dataInput);
        }
        int readByte = dataInput.readByte();
        e[] eVarArr = readByte == 0 ? f4063j : new e[readByte];
        for (int i10 = 0; i10 < readByte; i10++) {
            eVarArr[i10] = e.b(dataInput);
        }
        return new f(jArr2, bArr, jArr3, bArr2, eVarArr);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new a(this.f4071g != null ? (byte) 100 : (byte) 1, this);
    }

    public final B d(j$.time.g gVar) {
        TimeZone timeZone = this.f4071g;
        if (timeZone != null) {
            return j(timeZone.getOffset(gVar.K()));
        }
        long[] jArr = this.f4068c;
        if (jArr.length == 0) {
            return this.f4067b[0];
        }
        long E4 = gVar.E();
        int length = this.f.length;
        B[] bArr = this.f4070e;
        if (length <= 0 || E4 <= jArr[jArr.length - 1]) {
            int binarySearch = Arrays.binarySearch(jArr, E4);
            if (binarySearch < 0) {
                binarySearch = (-binarySearch) - 2;
            }
            return bArr[binarySearch + 1];
        }
        b[] b4 = b(c(E4, bArr[bArr.length - 1]));
        b bVar = null;
        for (int i4 = 0; i4 < b4.length; i4++) {
            bVar = b4[i4];
            if (E4 < bVar.C()) {
                return bVar.u();
            }
        }
        return bVar.r();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof f) {
            f fVar = (f) obj;
            return Objects.equals(this.f4071g, fVar.f4071g) && Arrays.equals(this.f4066a, fVar.f4066a) && Arrays.equals(this.f4067b, fVar.f4067b) && Arrays.equals(this.f4068c, fVar.f4068c) && Arrays.equals(this.f4070e, fVar.f4070e) && Arrays.equals(this.f, fVar.f);
        }
        return false;
    }

    public final b f(k kVar) {
        Object e4 = e(kVar);
        if (e4 instanceof b) {
            return (b) e4;
        }
        return null;
    }

    public final List g(k kVar) {
        Object e4 = e(kVar);
        return e4 instanceof b ? ((b) e4).v() : Collections.singletonList((B) e4);
    }

    public final boolean h() {
        b bVar;
        TimeZone timeZone = this.f4071g;
        if (timeZone == null) {
            return this.f4068c.length == 0;
        } else if (timeZone.useDaylightTime() || timeZone.getDSTSavings() != 0) {
            return false;
        } else {
            j$.time.g G4 = j$.time.g.G();
            long E4 = G4.E();
            if (G4.F() > 0 && E4 < Long.MAX_VALUE) {
                E4++;
            }
            int c4 = c(E4, d(G4));
            b[] b4 = b(c4);
            int length = b4.length - 1;
            while (true) {
                if (length < 0) {
                    if (c4 > 1800) {
                        b[] b5 = b(c4 - 1);
                        int length2 = b5.length - 1;
                        while (true) {
                            if (length2 < 0) {
                                int offset = timeZone.getOffset((E4 - 1) * 1000);
                                long s4 = j$.time.i.O(1800, 1, 1).s() * 86400;
                                for (long min = Math.min(E4 - 31104000, (AbstractC0481b.b().a() / 1000) + 31968000); s4 <= min; min -= 7776000) {
                                    int offset2 = timeZone.getOffset(min * 1000);
                                    if (offset != offset2) {
                                        int c5 = c(min, j(offset2));
                                        b[] b6 = b(c5 + 1);
                                        int length3 = b6.length - 1;
                                        while (true) {
                                            if (length3 < 0) {
                                                b[] b7 = b(c5);
                                                bVar = b7[b7.length - 1];
                                                break;
                                            } else if (E4 > b6[length3].C()) {
                                                bVar = b6[length3];
                                                break;
                                            } else {
                                                length3--;
                                            }
                                        }
                                    }
                                }
                            } else if (E4 > b5[length2].C()) {
                                bVar = b5[length2];
                                break;
                            } else {
                                length2--;
                            }
                        }
                    }
                    bVar = null;
                } else if (E4 > b4[length].C()) {
                    bVar = b4[length];
                    break;
                } else {
                    length--;
                }
            }
            return bVar == null;
        }
    }

    public final int hashCode() {
        return ((((Objects.hashCode(this.f4071g) ^ Arrays.hashCode(this.f4066a)) ^ Arrays.hashCode(this.f4067b)) ^ Arrays.hashCode(this.f4068c)) ^ Arrays.hashCode(this.f4070e)) ^ Arrays.hashCode(this.f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void l(DataOutput dataOutput) {
        long[] jArr = this.f4066a;
        dataOutput.writeInt(jArr.length);
        for (long j4 : jArr) {
            a.c(j4, dataOutput);
        }
        for (B b4 : this.f4067b) {
            a.d(b4, dataOutput);
        }
        long[] jArr2 = this.f4068c;
        dataOutput.writeInt(jArr2.length);
        for (long j5 : jArr2) {
            a.c(j5, dataOutput);
        }
        for (B b5 : this.f4070e) {
            a.d(b5, dataOutput);
        }
        e[] eVarArr = this.f;
        dataOutput.writeByte(eVarArr.length);
        for (e eVar : eVarArr) {
            eVar.c(dataOutput);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void m(DataOutput dataOutput) {
        dataOutput.writeUTF(this.f4071g.getID());
    }

    public final String toString() {
        StringBuilder sb;
        TimeZone timeZone = this.f4071g;
        if (timeZone != null) {
            String id = timeZone.getID();
            sb = new StringBuilder("ZoneRules[timeZone=");
            sb.append(id);
        } else {
            B[] bArr = this.f4067b;
            B b4 = bArr[bArr.length - 1];
            sb = new StringBuilder("ZoneRules[currentStandardOffset=");
            sb.append(b4);
        }
        sb.append("]");
        return sb.toString();
    }
}
