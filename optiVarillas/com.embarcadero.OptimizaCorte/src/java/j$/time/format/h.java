package j$.time.format;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class h extends j {

    /* renamed from: g  reason: collision with root package name */
    private final boolean f3931g;

    /* JADX INFO: Access modifiers changed from: package-private */
    public h(j$.time.temporal.r rVar, int i4, int i5, boolean z4, int i6) {
        super(rVar, i4, i5, v.NOT_NEGATIVE, i6);
        this.f3931g = z4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.time.format.j
    public final j b() {
        if (this.f3936e == -1) {
            return this;
        }
        return new h(this.f3932a, this.f3933b, this.f3934c, this.f3931g, -1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.time.format.j
    public final j c(int i4) {
        return new h(this.f3932a, this.f3933b, this.f3934c, this.f3931g, this.f3936e + i4);
    }

    @Override // j$.time.format.j, j$.time.format.g
    public final boolean j(q qVar, StringBuilder sb) {
        j$.time.temporal.r rVar = this.f3932a;
        Long e4 = qVar.e(rVar);
        if (e4 == null) {
            return false;
        }
        t b4 = qVar.b();
        long longValue = e4.longValue();
        j$.time.temporal.w j4 = rVar.j();
        j4.b(longValue, rVar);
        BigDecimal valueOf = BigDecimal.valueOf(j4.e());
        BigDecimal add = BigDecimal.valueOf(j4.d()).subtract(valueOf).add(BigDecimal.ONE);
        BigDecimal subtract = BigDecimal.valueOf(longValue).subtract(valueOf);
        RoundingMode roundingMode = RoundingMode.FLOOR;
        BigDecimal divide = subtract.divide(add, 9, roundingMode);
        BigDecimal bigDecimal = BigDecimal.ZERO;
        if (divide.compareTo(bigDecimal) != 0) {
            bigDecimal = divide.signum() == 0 ? new BigDecimal(BigInteger.ZERO, 0) : divide.stripTrailingZeros();
        }
        int scale = bigDecimal.scale();
        boolean z4 = this.f3931g;
        int i4 = this.f3933b;
        if (scale != 0) {
            String substring = bigDecimal.setScale(Math.min(Math.max(bigDecimal.scale(), i4), this.f3934c), roundingMode).toPlainString().substring(2);
            b4.getClass();
            if (z4) {
                sb.append('.');
            }
            sb.append(substring);
            return true;
        } else if (i4 > 0) {
            if (z4) {
                b4.getClass();
                sb.append('.');
            }
            for (int i5 = 0; i5 < i4; i5++) {
                b4.getClass();
                sb.append('0');
            }
            return true;
        } else {
            return true;
        }
    }

    @Override // j$.time.format.j
    public final String toString() {
        String str = this.f3931g ? ",DecimalPoint" : "";
        return "Fraction(" + this.f3932a + "," + this.f3933b + "," + this.f3934c + str + ")";
    }
}
