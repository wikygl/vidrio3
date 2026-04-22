package j$.time.format;

import j$.time.B;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class i implements g {
    @Override // j$.time.format.g
    public final boolean j(q qVar, StringBuilder sb) {
        Long e4 = qVar.e(j$.time.temporal.a.INSTANT_SECONDS);
        j$.time.temporal.o d4 = qVar.d();
        j$.time.temporal.a aVar = j$.time.temporal.a.NANO_OF_SECOND;
        Long valueOf = d4.f(aVar) ? Long.valueOf(qVar.d().r(aVar)) : null;
        int i4 = 0;
        if (e4 == null) {
            return false;
        }
        long longValue = e4.longValue();
        int z4 = aVar.z(valueOf != null ? valueOf.longValue() : 0L);
        if (longValue >= -62167219200L) {
            long j4 = longValue - 253402300800L;
            long n4 = j$.com.android.tools.r8.a.n(j4, 315569520000L) + 1;
            j$.time.k M3 = j$.time.k.M(j$.com.android.tools.r8.a.m(j4, 315569520000L) - 62167219200L, 0, B.f3838e);
            if (n4 > 0) {
                sb.append('+');
                sb.append(n4);
            }
            sb.append(M3);
            if (M3.G() == 0) {
                sb.append(":00");
            }
        } else {
            long j5 = longValue + 62167219200L;
            long j6 = j5 / 315569520000L;
            long j7 = j5 % 315569520000L;
            j$.time.k M4 = j$.time.k.M(j7 - 62167219200L, 0, B.f3838e);
            int length = sb.length();
            sb.append(M4);
            if (M4.G() == 0) {
                sb.append(":00");
            }
            if (j6 < 0) {
                if (M4.H() == -10000) {
                    sb.replace(length, length + 2, Long.toString(j6 - 1));
                } else if (j7 == 0) {
                    sb.insert(length, j6);
                } else {
                    sb.insert(length + 1, Math.abs(j6));
                }
            }
        }
        if (z4 > 0) {
            sb.append('.');
            int i5 = 100000000;
            while (true) {
                if (z4 <= 0 && i4 % 3 == 0 && i4 >= -2) {
                    break;
                }
                int i6 = z4 / i5;
                sb.append((char) (i6 + 48));
                z4 -= i6 * i5;
                i5 /= 10;
                i4++;
            }
        }
        sb.append('Z');
        return true;
    }

    public final String toString() {
        return "Instant()";
    }
}
