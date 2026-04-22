package E;

import android.graphics.Path;
import android.util.Log;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class d {
    public static boolean a(a[] aVarArr, a[] aVarArr2) {
        if (aVarArr == null || aVarArr2 == null || aVarArr.length != aVarArr2.length) {
            return false;
        }
        for (int i4 = 0; i4 < aVarArr.length; i4++) {
            a aVar = aVarArr[i4];
            char c4 = aVar.f808a;
            a aVar2 = aVarArr2[i4];
            if (c4 != aVar2.f808a || aVar.f809b.length != aVar2.f809b.length) {
                return false;
            }
        }
        return true;
    }

    public static float[] b(float[] fArr, int i4) {
        if (i4 >= 0) {
            int length = fArr.length;
            if (length >= 0) {
                int min = Math.min(i4, length);
                float[] fArr2 = new float[i4];
                System.arraycopy(fArr, 0, fArr2, 0, min);
                return fArr2;
            }
            throw new ArrayIndexOutOfBoundsException();
        }
        throw new IllegalArgumentException();
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0044  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0098 A[Catch: NumberFormatException -> 0x00ac, LOOP:3: B:25:0x006a->B:44:0x0098, LOOP_END, TryCatch #0 {NumberFormatException -> 0x00ac, blocks: (B:22:0x0056, B:25:0x006a, B:27:0x0070, B:31:0x007c, B:44:0x0098, B:46:0x009e, B:52:0x00b3, B:53:0x00b6), top: B:68:0x0056 }] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x009e A[Catch: NumberFormatException -> 0x00ac, TryCatch #0 {NumberFormatException -> 0x00ac, blocks: (B:22:0x0056, B:25:0x006a, B:27:0x0070, B:31:0x007c, B:44:0x0098, B:46:0x009e, B:52:0x00b3, B:53:0x00b6), top: B:68:0x0056 }] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00b0  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00b3 A[Catch: NumberFormatException -> 0x00ac, TryCatch #0 {NumberFormatException -> 0x00ac, blocks: (B:22:0x0056, B:25:0x006a, B:27:0x0070, B:31:0x007c, B:44:0x0098, B:46:0x009e, B:52:0x00b3, B:53:0x00b6), top: B:68:0x0056 }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x00d9 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0097 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static E.d.a[] c(java.lang.String r17) {
        /*
            Method dump skipped, instructions count: 270
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: E.d.c(java.lang.String):E.d$a[]");
    }

    public static Path d(String str) {
        Path path = new Path();
        try {
            a.b(c(str), path);
            return path;
        } catch (RuntimeException e4) {
            throw new RuntimeException("Error in parsing ".concat(str), e4);
        }
    }

    public static a[] e(a[] aVarArr) {
        a[] aVarArr2 = new a[aVarArr.length];
        for (int i4 = 0; i4 < aVarArr.length; i4++) {
            aVarArr2[i4] = new a(aVarArr[i4]);
        }
        return aVarArr2;
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public char f808a;

        /* renamed from: b  reason: collision with root package name */
        public final float[] f809b;

        public a(char c4, float[] fArr) {
            this.f808a = c4;
            this.f809b = fArr;
        }

        public static void a(Path path, float f, float f4, float f5, float f6, float f7, float f8, float f9, boolean z4, boolean z5) {
            double d4;
            double d5;
            boolean z6;
            double radians = Math.toRadians(f9);
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            double d6 = f;
            double d7 = f4;
            double d8 = (d7 * sin) + (d6 * cos);
            double d9 = d6;
            double d10 = f7;
            double d11 = d8 / d10;
            double d12 = f8;
            double d13 = ((d7 * cos) + ((-f) * sin)) / d12;
            double d14 = d7;
            double d15 = f6;
            double d16 = ((d15 * sin) + (f5 * cos)) / d10;
            double d17 = ((d15 * cos) + ((-f5) * sin)) / d12;
            double d18 = d11 - d16;
            double d19 = d13 - d17;
            double d20 = (d11 + d16) / 2.0d;
            double d21 = (d13 + d17) / 2.0d;
            double d22 = (d19 * d19) + (d18 * d18);
            if (d22 == 0.0d) {
                Log.w("PathParser", " Points are coincident");
                return;
            }
            double d23 = (1.0d / d22) - 0.25d;
            if (d23 < 0.0d) {
                Log.w("PathParser", "Points are too far apart " + d22);
                float sqrt = (float) (Math.sqrt(d22) / 1.99999d);
                a(path, f, f4, f5, f6, f7 * sqrt, f8 * sqrt, f9, z4, z5);
                return;
            }
            double sqrt2 = Math.sqrt(d23);
            double d24 = d18 * sqrt2;
            double d25 = sqrt2 * d19;
            if (z4 == z5) {
                d4 = d20 - d25;
                d5 = d21 + d24;
            } else {
                d4 = d20 + d25;
                d5 = d21 - d24;
            }
            double atan2 = Math.atan2(d13 - d5, d11 - d4);
            double atan22 = Math.atan2(d17 - d5, d16 - d4) - atan2;
            int i4 = 0;
            int i5 = (atan22 > 0.0d ? 1 : (atan22 == 0.0d ? 0 : -1));
            if (i5 >= 0) {
                z6 = true;
            } else {
                z6 = false;
            }
            if (z5 != z6) {
                if (i5 > 0) {
                    atan22 -= 6.283185307179586d;
                } else {
                    atan22 += 6.283185307179586d;
                }
            }
            double d26 = d4 * d10;
            double d27 = d5 * d12;
            double d28 = (d26 * cos) - (d27 * sin);
            double d29 = (d27 * cos) + (d26 * sin);
            int ceil = (int) Math.ceil(Math.abs((atan22 * 4.0d) / 3.141592653589793d));
            double cos2 = Math.cos(radians);
            double sin2 = Math.sin(radians);
            double cos3 = Math.cos(atan2);
            double sin3 = Math.sin(atan2);
            double d30 = -d10;
            double d31 = d30 * cos2;
            double d32 = d12 * sin2;
            double d33 = (d31 * sin3) - (d32 * cos3);
            double d34 = d30 * sin2;
            double d35 = d12 * cos2;
            double d36 = (cos3 * d35) + (sin3 * d34);
            double d37 = atan22 / ceil;
            double d38 = atan2;
            while (i4 < ceil) {
                double d39 = d38 + d37;
                double sin4 = Math.sin(d39);
                double cos4 = Math.cos(d39);
                double d40 = d37;
                double d41 = (((d10 * cos2) * cos4) + d28) - (d32 * sin4);
                double d42 = d28;
                double d43 = (d35 * sin4) + (d10 * sin2 * cos4) + d29;
                double d44 = (d31 * sin4) - (d32 * cos4);
                double d45 = (cos4 * d35) + (sin4 * d34);
                double d46 = d39 - d38;
                double tan = Math.tan(d46 / 2.0d);
                double sqrt3 = ((Math.sqrt(((tan * 3.0d) * tan) + 4.0d) - 1.0d) * Math.sin(d46)) / 3.0d;
                double d47 = (d33 * sqrt3) + d9;
                path.rLineTo(0.0f, 0.0f);
                path.cubicTo((float) d47, (float) ((d36 * sqrt3) + d14), (float) (d41 - (sqrt3 * d44)), (float) (d43 - (sqrt3 * d45)), (float) d41, (float) d43);
                i4++;
                d35 = d35;
                d34 = d34;
                ceil = ceil;
                cos2 = cos2;
                d38 = d39;
                d10 = d10;
                d36 = d45;
                d33 = d44;
                d9 = d41;
                d14 = d43;
                d37 = d40;
                d28 = d42;
            }
        }

        @Deprecated
        public static void b(a[] aVarArr, Path path) {
            int i4;
            int i5;
            char c4;
            int i6;
            int i7;
            a aVar;
            boolean z4;
            boolean z5;
            float f;
            float f4;
            boolean z6;
            boolean z7;
            float f5;
            float f6;
            float f7;
            float f8;
            float f9;
            float f10;
            float f11;
            float f12;
            float f13;
            float f14;
            a[] aVarArr2 = aVarArr;
            float[] fArr = new float[6];
            int length = aVarArr2.length;
            char c5 = 'm';
            int i8 = 0;
            while (i8 < length) {
                a aVar2 = aVarArr2[i8];
                char c6 = aVar2.f808a;
                float f15 = fArr[0];
                float f16 = fArr[1];
                float f17 = fArr[2];
                float f18 = fArr[3];
                float f19 = fArr[4];
                float f20 = fArr[5];
                switch (c6) {
                    case 'A':
                    case 'a':
                        i4 = 7;
                        break;
                    case 'C':
                    case 'c':
                        i4 = 6;
                        break;
                    case 'H':
                    case 'V':
                    case 'h':
                    case 'v':
                        i4 = 1;
                        break;
                    case 'Q':
                    case 'S':
                    case 'q':
                    case 's':
                        i4 = 4;
                        break;
                    case 'Z':
                    case 'z':
                        path.close();
                        path.moveTo(f19, f20);
                        f15 = f19;
                        f17 = f15;
                        f16 = f20;
                        f18 = f16;
                    default:
                        i4 = 2;
                        break;
                }
                float f21 = f19;
                float f22 = f20;
                float f23 = f15;
                float f24 = f16;
                int i9 = 0;
                while (true) {
                    float[] fArr2 = aVar2.f809b;
                    if (i9 < fArr2.length) {
                        if (c6 != 'A') {
                            if (c6 != 'C') {
                                if (c6 != 'H') {
                                    if (c6 != 'Q') {
                                        if (c6 != 'V') {
                                            if (c6 != 'a') {
                                                if (c6 != 'c') {
                                                    if (c6 != 'h') {
                                                        if (c6 != 'q') {
                                                            if (c6 != 'v') {
                                                                if (c6 != 'L') {
                                                                    if (c6 != 'M') {
                                                                        if (c6 != 'S') {
                                                                            if (c6 != 'T') {
                                                                                if (c6 != 'l') {
                                                                                    if (c6 != 'm') {
                                                                                        if (c6 != 's') {
                                                                                            if (c6 != 't') {
                                                                                                i5 = i9;
                                                                                            } else {
                                                                                                if (c5 != 'q' && c5 != 't' && c5 != 'Q' && c5 != 'T') {
                                                                                                    f14 = 0.0f;
                                                                                                    f13 = 0.0f;
                                                                                                } else {
                                                                                                    f13 = f23 - f17;
                                                                                                    f14 = f24 - f18;
                                                                                                }
                                                                                                int i10 = i9 + 1;
                                                                                                path.rQuadTo(f13, f14, fArr2[i9], fArr2[i10]);
                                                                                                float f25 = f13 + f23;
                                                                                                float f26 = f14 + f24;
                                                                                                f23 += fArr2[i9];
                                                                                                f24 += fArr2[i10];
                                                                                                f18 = f26;
                                                                                                i5 = i9;
                                                                                                c4 = c6;
                                                                                                i6 = i8;
                                                                                                i7 = length;
                                                                                                f17 = f25;
                                                                                            }
                                                                                        } else {
                                                                                            if (c5 != 'c' && c5 != 's' && c5 != 'C' && c5 != 'S') {
                                                                                                f11 = 0.0f;
                                                                                                f12 = 0.0f;
                                                                                            } else {
                                                                                                float f27 = f23 - f17;
                                                                                                f11 = f24 - f18;
                                                                                                f12 = f27;
                                                                                            }
                                                                                            int i11 = i9 + 1;
                                                                                            int i12 = i9 + 2;
                                                                                            int i13 = i9 + 3;
                                                                                            i5 = i9;
                                                                                            float f28 = f24;
                                                                                            float f29 = f23;
                                                                                            path.rCubicTo(f12, f11, fArr2[i9], fArr2[i11], fArr2[i12], fArr2[i13]);
                                                                                            f5 = f29 + fArr2[i5];
                                                                                            f6 = f28 + fArr2[i11];
                                                                                            f7 = f29 + fArr2[i12];
                                                                                            f8 = fArr2[i13] + f28;
                                                                                        }
                                                                                    } else {
                                                                                        i5 = i9;
                                                                                        float f30 = fArr2[i5];
                                                                                        f23 += f30;
                                                                                        float f31 = fArr2[i5 + 1];
                                                                                        f24 += f31;
                                                                                        if (i5 > 0) {
                                                                                            path.rLineTo(f30, f31);
                                                                                        } else {
                                                                                            path.rMoveTo(f30, f31);
                                                                                            f22 = f24;
                                                                                            f21 = f23;
                                                                                        }
                                                                                    }
                                                                                } else {
                                                                                    i5 = i9;
                                                                                    int i14 = i5 + 1;
                                                                                    path.rLineTo(fArr2[i5], fArr2[i14]);
                                                                                    f23 += fArr2[i5];
                                                                                    f24 += fArr2[i14];
                                                                                }
                                                                            } else {
                                                                                i5 = i9;
                                                                                float f32 = f24;
                                                                                float f33 = f23;
                                                                                if (c5 != 'q' && c5 != 't' && c5 != 'Q' && c5 != 'T') {
                                                                                    f5 = f33;
                                                                                    f6 = f32;
                                                                                } else {
                                                                                    f5 = (f33 * 2.0f) - f17;
                                                                                    f6 = (f32 * 2.0f) - f18;
                                                                                }
                                                                                int i15 = i5 + 1;
                                                                                path.quadTo(f5, f6, fArr2[i5], fArr2[i15]);
                                                                                f7 = fArr2[i5];
                                                                                f8 = fArr2[i15];
                                                                            }
                                                                        } else {
                                                                            i5 = i9;
                                                                            float f34 = f24;
                                                                            float f35 = f23;
                                                                            if (c5 != 'c' && c5 != 's' && c5 != 'C' && c5 != 'S') {
                                                                                f10 = f35;
                                                                                f9 = f34;
                                                                            } else {
                                                                                f9 = (f34 * 2.0f) - f18;
                                                                                f10 = (f35 * 2.0f) - f17;
                                                                            }
                                                                            int i16 = i5 + 1;
                                                                            int i17 = i5 + 2;
                                                                            int i18 = i5 + 3;
                                                                            path.cubicTo(f10, f9, fArr2[i5], fArr2[i16], fArr2[i17], fArr2[i18]);
                                                                            float f36 = fArr2[i5];
                                                                            float f37 = fArr2[i16];
                                                                            f23 = fArr2[i17];
                                                                            f24 = fArr2[i18];
                                                                            f18 = f37;
                                                                            f17 = f36;
                                                                        }
                                                                    } else {
                                                                        i5 = i9;
                                                                        f23 = fArr2[i5];
                                                                        f24 = fArr2[i5 + 1];
                                                                        if (i5 > 0) {
                                                                            path.lineTo(f23, f24);
                                                                        } else {
                                                                            path.moveTo(f23, f24);
                                                                            f22 = f24;
                                                                            f21 = f23;
                                                                        }
                                                                    }
                                                                } else {
                                                                    i5 = i9;
                                                                    int i19 = i5 + 1;
                                                                    path.lineTo(fArr2[i5], fArr2[i19]);
                                                                    f23 = fArr2[i5];
                                                                    f24 = fArr2[i19];
                                                                }
                                                            } else {
                                                                i5 = i9;
                                                                path.rLineTo(0.0f, fArr2[i5]);
                                                                f24 += fArr2[i5];
                                                            }
                                                        } else {
                                                            i5 = i9;
                                                            float f38 = f24;
                                                            float f39 = f23;
                                                            int i20 = i5 + 1;
                                                            int i21 = i5 + 2;
                                                            int i22 = i5 + 3;
                                                            path.rQuadTo(fArr2[i5], fArr2[i20], fArr2[i21], fArr2[i22]);
                                                            float f40 = f39 + fArr2[i5];
                                                            float f41 = f39 + fArr2[i21];
                                                            f24 = f38 + fArr2[i22];
                                                            f18 = fArr2[i20] + f38;
                                                            f17 = f40;
                                                            c4 = c6;
                                                            i6 = i8;
                                                            i7 = length;
                                                            f23 = f41;
                                                        }
                                                        aVar = aVar2;
                                                    } else {
                                                        i5 = i9;
                                                        path.rLineTo(fArr2[i5], 0.0f);
                                                        f23 += fArr2[i5];
                                                    }
                                                    c4 = c6;
                                                    i6 = i8;
                                                    i7 = length;
                                                    aVar = aVar2;
                                                } else {
                                                    i5 = i9;
                                                    float f42 = f24;
                                                    float f43 = f23;
                                                    int i23 = i5 + 2;
                                                    int i24 = i5 + 3;
                                                    int i25 = i5 + 4;
                                                    int i26 = i5 + 5;
                                                    path.rCubicTo(fArr2[i5], fArr2[i5 + 1], fArr2[i23], fArr2[i24], fArr2[i25], fArr2[i26]);
                                                    f5 = f43 + fArr2[i23];
                                                    f6 = f42 + fArr2[i24];
                                                    f7 = f43 + fArr2[i25];
                                                    f8 = fArr2[i26] + f42;
                                                }
                                                f18 = f6;
                                                f17 = f5;
                                                c4 = c6;
                                                i6 = i8;
                                                i7 = length;
                                                f23 = f7;
                                                f24 = f8;
                                                aVar = aVar2;
                                            } else {
                                                i5 = i9;
                                                float f44 = f24;
                                                float f45 = f23;
                                                int i27 = i5 + 5;
                                                float f46 = fArr2[i27] + f45;
                                                int i28 = i5 + 6;
                                                float f47 = fArr2[i28] + f44;
                                                float f48 = fArr2[i5];
                                                float f49 = fArr2[i5 + 1];
                                                float f50 = fArr2[i5 + 2];
                                                if (fArr2[i5 + 3] != 0.0f) {
                                                    z6 = true;
                                                } else {
                                                    z6 = false;
                                                }
                                                if (fArr2[i5 + 4] != 0.0f) {
                                                    z7 = true;
                                                } else {
                                                    z7 = false;
                                                }
                                                c4 = c6;
                                                i7 = length;
                                                aVar = aVar2;
                                                i6 = i8;
                                                a(path, f45, f44, f46, f47, f48, f49, f50, z6, z7);
                                                f23 = f45 + fArr2[i27];
                                                f24 = f44 + fArr2[i28];
                                            }
                                        } else {
                                            i5 = i9;
                                            c4 = c6;
                                            i6 = i8;
                                            i7 = length;
                                            aVar = aVar2;
                                            path.lineTo(f23, fArr2[i5]);
                                            f24 = fArr2[i5];
                                        }
                                    } else {
                                        i5 = i9;
                                        c4 = c6;
                                        i6 = i8;
                                        i7 = length;
                                        aVar = aVar2;
                                        float f51 = fArr2[i5];
                                        int i29 = i5 + 1;
                                        float f52 = fArr2[i29];
                                        int i30 = i5 + 2;
                                        int i31 = i5 + 3;
                                        path.quadTo(f51, f52, fArr2[i30], fArr2[i31]);
                                        f = fArr2[i5];
                                        f4 = fArr2[i29];
                                        f23 = fArr2[i30];
                                        f24 = fArr2[i31];
                                    }
                                } else {
                                    i5 = i9;
                                    c4 = c6;
                                    i6 = i8;
                                    i7 = length;
                                    aVar = aVar2;
                                    path.lineTo(fArr2[i5], f24);
                                    f23 = fArr2[i5];
                                }
                                i9 = i5 + i4;
                                aVar2 = aVar;
                                length = i7;
                                c5 = c4;
                                c6 = c5;
                                i8 = i6;
                            } else {
                                i5 = i9;
                                c4 = c6;
                                i6 = i8;
                                i7 = length;
                                aVar = aVar2;
                                int i32 = i5 + 2;
                                int i33 = i5 + 3;
                                int i34 = i5 + 4;
                                int i35 = i5 + 5;
                                path.cubicTo(fArr2[i5], fArr2[i5 + 1], fArr2[i32], fArr2[i33], fArr2[i34], fArr2[i35]);
                                f23 = fArr2[i34];
                                f24 = fArr2[i35];
                                f = fArr2[i32];
                                f4 = fArr2[i33];
                            }
                            f17 = f;
                            f18 = f4;
                            i9 = i5 + i4;
                            aVar2 = aVar;
                            length = i7;
                            c5 = c4;
                            c6 = c5;
                            i8 = i6;
                        } else {
                            i5 = i9;
                            float f53 = f24;
                            float f54 = f23;
                            c4 = c6;
                            i6 = i8;
                            i7 = length;
                            aVar = aVar2;
                            int i36 = i5 + 5;
                            float f55 = fArr2[i36];
                            int i37 = i5 + 6;
                            float f56 = fArr2[i37];
                            float f57 = fArr2[i5];
                            float f58 = fArr2[i5 + 1];
                            float f59 = fArr2[i5 + 2];
                            if (fArr2[i5 + 3] != 0.0f) {
                                z4 = true;
                            } else {
                                z4 = false;
                            }
                            if (fArr2[i5 + 4] != 0.0f) {
                                z5 = true;
                            } else {
                                z5 = false;
                            }
                            a(path, f54, f53, f55, f56, f57, f58, f59, z4, z5);
                            f23 = fArr2[i36];
                            f24 = fArr2[i37];
                        }
                        f18 = f24;
                        f17 = f23;
                        i9 = i5 + i4;
                        aVar2 = aVar;
                        length = i7;
                        c5 = c4;
                        c6 = c5;
                        i8 = i6;
                    }
                }
                fArr[0] = f23;
                fArr[1] = f24;
                fArr[2] = f17;
                fArr[3] = f18;
                fArr[4] = f21;
                fArr[5] = f22;
                c5 = aVar2.f808a;
                i8++;
                aVarArr2 = aVarArr;
                length = length;
            }
        }

        public a(a aVar) {
            this.f808a = aVar.f808a;
            float[] fArr = aVar.f809b;
            this.f809b = d.b(fArr, fArr.length);
        }
    }
}
