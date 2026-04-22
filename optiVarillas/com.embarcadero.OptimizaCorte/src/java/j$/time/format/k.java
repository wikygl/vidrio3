package j$.time.format;

import j$.util.Objects;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class k implements g {

    /* renamed from: d  reason: collision with root package name */
    static final String[] f3937d = {"+HH", "+HHmm", "+HH:mm", "+HHMM", "+HH:MM", "+HHMMss", "+HH:MM:ss", "+HHMMSS", "+HH:MM:SS", "+HHmmss", "+HH:mm:ss", "+H", "+Hmm", "+H:mm", "+HMM", "+H:MM", "+HMMss", "+H:MM:ss", "+HMMSS", "+H:MM:SS", "+Hmmss", "+H:mm:ss"};

    /* renamed from: e  reason: collision with root package name */
    static final k f3938e = new k("+HH:MM:ss", "Z");

    /* renamed from: a  reason: collision with root package name */
    private final String f3939a;

    /* renamed from: b  reason: collision with root package name */
    private final int f3940b;

    /* renamed from: c  reason: collision with root package name */
    private final int f3941c;

    static {
        new k("+HH:MM:ss", "0");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public k(String str, String str2) {
        Objects.requireNonNull(str, "pattern");
        Objects.requireNonNull(str2, "noOffsetText");
        int i4 = 0;
        while (true) {
            String[] strArr = f3937d;
            if (i4 >= 22) {
                throw new IllegalArgumentException("Invalid zone offset pattern: ".concat(str));
            }
            if (strArr[i4].equals(str)) {
                this.f3940b = i4;
                this.f3941c = i4 % 11;
                this.f3939a = str2;
                return;
            }
            i4++;
        }
    }

    private static void a(boolean z4, int i4, StringBuilder sb) {
        sb.append(z4 ? ":" : "");
        sb.append((char) ((i4 / 10) + 48));
        sb.append((char) ((i4 % 10) + 48));
    }

    @Override // j$.time.format.g
    public final boolean j(q qVar, StringBuilder sb) {
        Long e4 = qVar.e(j$.time.temporal.a.OFFSET_SECONDS);
        boolean z4 = false;
        if (e4 == null) {
            return false;
        }
        long longValue = e4.longValue();
        int i4 = (int) longValue;
        if (longValue == i4) {
            String str = this.f3939a;
            if (i4 != 0) {
                int abs = Math.abs((i4 / 3600) % 100);
                int abs2 = Math.abs((i4 / 60) % 60);
                int abs3 = Math.abs(i4 % 60);
                int length = sb.length();
                sb.append(i4 < 0 ? "-" : "+");
                if (this.f3940b >= 11 && abs < 10) {
                    sb.append((char) (abs + 48));
                } else {
                    a(false, abs, sb);
                }
                int i5 = this.f3941c;
                if ((i5 >= 3 && i5 <= 8) || ((i5 >= 9 && abs3 > 0) || (i5 >= 1 && abs2 > 0))) {
                    a(i5 > 0 && i5 % 2 == 0, abs2, sb);
                    abs += abs2;
                    if (i5 == 7 || i5 == 8 || (i5 >= 5 && abs3 > 0)) {
                        if (i5 > 0 && i5 % 2 == 0) {
                            z4 = true;
                        }
                        a(z4, abs3, sb);
                        abs += abs3;
                    }
                }
                if (abs == 0) {
                    sb.setLength(length);
                }
                return true;
            }
            sb.append(str);
            return true;
        }
        throw new ArithmeticException();
    }

    public final String toString() {
        String replace = this.f3939a.replace("'", "''");
        String str = f3937d[this.f3940b];
        return "Offset(" + str + ",'" + replace + "')";
    }
}
