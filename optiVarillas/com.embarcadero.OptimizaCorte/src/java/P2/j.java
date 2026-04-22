package P2;

import A1.P0;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.BitSet;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class j {

    /* renamed from: a  reason: collision with root package name */
    public final l[] f1882a = new l[4];

    /* renamed from: b  reason: collision with root package name */
    public final Matrix[] f1883b = new Matrix[4];

    /* renamed from: c  reason: collision with root package name */
    public final Matrix[] f1884c = new Matrix[4];

    /* renamed from: d  reason: collision with root package name */
    public final PointF f1885d = new PointF();

    /* renamed from: e  reason: collision with root package name */
    public final Path f1886e = new Path();
    public final Path f = new Path();

    /* renamed from: g  reason: collision with root package name */
    public final l f1887g = new l();

    /* renamed from: h  reason: collision with root package name */
    public final float[] f1888h = new float[2];

    /* renamed from: i  reason: collision with root package name */
    public final float[] f1889i = new float[2];

    /* renamed from: j  reason: collision with root package name */
    public final Path f1890j = new Path();

    /* renamed from: k  reason: collision with root package name */
    public final Path f1891k = new Path();

    /* renamed from: l  reason: collision with root package name */
    public final boolean f1892l = true;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public static final j f1893a = new j();
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface b {
    }

    public j() {
        for (int i4 = 0; i4 < 4; i4++) {
            this.f1882a[i4] = new l();
            this.f1883b[i4] = new Matrix();
            this.f1884c[i4] = new Matrix();
        }
    }

    public final void a(i iVar, float f, RectF rectF, b bVar, Path path) {
        int i4;
        Matrix[] matrixArr;
        float[] fArr;
        Matrix[] matrixArr2;
        l[] lVarArr;
        float abs;
        e eVar;
        c cVar;
        P0 p02;
        int i5;
        path.rewind();
        Path path2 = this.f1886e;
        path2.rewind();
        Path path3 = this.f;
        path3.rewind();
        path3.addRect(rectF, Path.Direction.CW);
        int i6 = 0;
        while (true) {
            matrixArr = this.f1884c;
            fArr = this.f1888h;
            matrixArr2 = this.f1883b;
            lVarArr = this.f1882a;
            if (i6 >= 4) {
                break;
            }
            if (i6 != 1) {
                if (i6 != 2) {
                    if (i6 != 3) {
                        cVar = iVar.f;
                    } else {
                        cVar = iVar.f1864e;
                    }
                } else {
                    cVar = iVar.f1866h;
                }
            } else {
                cVar = iVar.f1865g;
            }
            if (i6 != 1) {
                if (i6 != 2) {
                    if (i6 != 3) {
                        p02 = iVar.f1861b;
                    } else {
                        p02 = iVar.f1860a;
                    }
                } else {
                    p02 = iVar.f1863d;
                }
            } else {
                p02 = iVar.f1862c;
            }
            l lVar = lVarArr[i6];
            p02.getClass();
            p02.a(lVar, f, cVar.a(rectF));
            int i7 = i6 + 1;
            float f4 = (i7 % 4) * 90;
            matrixArr2[i6].reset();
            PointF pointF = this.f1885d;
            if (i6 != 1) {
                if (i6 != 2) {
                    if (i6 != 3) {
                        i5 = i7;
                        pointF.set(rectF.right, rectF.top);
                    } else {
                        i5 = i7;
                        pointF.set(rectF.left, rectF.top);
                    }
                } else {
                    i5 = i7;
                    pointF.set(rectF.left, rectF.bottom);
                }
            } else {
                i5 = i7;
                pointF.set(rectF.right, rectF.bottom);
            }
            matrixArr2[i6].setTranslate(pointF.x, pointF.y);
            matrixArr2[i6].preRotate(f4);
            l lVar2 = lVarArr[i6];
            fArr[0] = lVar2.f1898c;
            fArr[1] = lVar2.f1899d;
            matrixArr2[i6].mapPoints(fArr);
            matrixArr[i6].reset();
            matrixArr[i6].setTranslate(fArr[0], fArr[1]);
            matrixArr[i6].preRotate(f4);
            i6 = i5;
        }
        int i8 = 0;
        for (i4 = 4; i8 < i4; i4 = 4) {
            l lVar3 = lVarArr[i8];
            fArr[0] = lVar3.f1896a;
            fArr[1] = lVar3.f1897b;
            matrixArr2[i8].mapPoints(fArr);
            if (i8 == 0) {
                path.moveTo(fArr[0], fArr[1]);
            } else {
                path.lineTo(fArr[0], fArr[1]);
            }
            lVarArr[i8].c(matrixArr2[i8], path);
            if (bVar != null) {
                l lVar4 = lVarArr[i8];
                Matrix matrix = matrixArr2[i8];
                f fVar = f.this;
                BitSet bitSet = fVar.f1823m;
                lVar4.getClass();
                bitSet.set(i8, false);
                lVar4.b(lVar4.f);
                fVar.f1821k[i8] = new k(new ArrayList(lVar4.f1902h), new Matrix(matrix));
            }
            int i9 = i8 + 1;
            int i10 = i9 % 4;
            l lVar5 = lVarArr[i8];
            fArr[0] = lVar5.f1898c;
            fArr[1] = lVar5.f1899d;
            matrixArr2[i8].mapPoints(fArr);
            l lVar6 = lVarArr[i10];
            float f5 = lVar6.f1896a;
            float[] fArr2 = this.f1889i;
            fArr2[0] = f5;
            fArr2[1] = lVar6.f1897b;
            matrixArr2[i10].mapPoints(fArr2);
            float max = Math.max(((float) Math.hypot(fArr[0] - fArr2[0], fArr[1] - fArr2[1])) - 0.001f, 0.0f);
            l lVar7 = lVarArr[i8];
            fArr[0] = lVar7.f1898c;
            fArr[1] = lVar7.f1899d;
            matrixArr2[i8].mapPoints(fArr);
            if (i8 != 1 && i8 != 3) {
                abs = Math.abs(rectF.centerY() - fArr[1]);
            } else {
                abs = Math.abs(rectF.centerX() - fArr[0]);
            }
            l[] lVarArr2 = lVarArr;
            l lVar8 = this.f1887g;
            lVar8.e(0.0f, 270.0f, 0.0f);
            if (i8 != 1) {
                if (i8 != 2) {
                    if (i8 != 3) {
                        eVar = iVar.f1868j;
                    } else {
                        eVar = iVar.f1867i;
                    }
                } else {
                    eVar = iVar.f1870l;
                }
            } else {
                eVar = iVar.f1869k;
            }
            eVar.a(max, abs, f, lVar8);
            Path path4 = this.f1890j;
            path4.reset();
            lVar8.c(matrixArr[i8], path4);
            if (this.f1892l && (b(path4, i8) || b(path4, i10))) {
                path4.op(path4, path3, Path.Op.DIFFERENCE);
                fArr[0] = lVar8.f1896a;
                fArr[1] = lVar8.f1897b;
                matrixArr[i8].mapPoints(fArr);
                path2.moveTo(fArr[0], fArr[1]);
                lVar8.c(matrixArr[i8], path2);
            } else {
                lVar8.c(matrixArr[i8], path);
            }
            if (bVar != null) {
                Matrix matrix2 = matrixArr[i8];
                f fVar2 = f.this;
                fVar2.f1823m.set(i8 + 4, false);
                lVar8.b(lVar8.f);
                fVar2.f1822l[i8] = new k(new ArrayList(lVar8.f1902h), new Matrix(matrix2));
            }
            i8 = i9;
            lVarArr = lVarArr2;
        }
        path.close();
        path2.close();
        if (!path2.isEmpty()) {
            path.op(path2, Path.Op.UNION);
        }
    }

    public final boolean b(Path path, int i4) {
        Path path2 = this.f1891k;
        path2.reset();
        this.f1882a[i4].c(this.f1883b[i4], path2);
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        path2.computeBounds(rectF, true);
        path.op(path2, Path.Op.INTERSECT);
        path.computeBounds(rectF, true);
        if (!rectF.isEmpty()) {
            return true;
        }
        if (rectF.width() > 1.0f && rectF.height() > 1.0f) {
            return true;
        }
        return false;
    }
}
