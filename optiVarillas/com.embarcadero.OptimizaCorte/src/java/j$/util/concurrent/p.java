package j$.util.concurrent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class p {

    /* renamed from: a  reason: collision with root package name */
    l[] f4172a;

    /* renamed from: b  reason: collision with root package name */
    l f4173b = null;

    /* renamed from: c  reason: collision with root package name */
    o f4174c;

    /* renamed from: d  reason: collision with root package name */
    o f4175d;

    /* renamed from: e  reason: collision with root package name */
    int f4176e;
    int f;

    /* renamed from: g  reason: collision with root package name */
    int f4177g;

    /* renamed from: h  reason: collision with root package name */
    final int f4178h;

    /* JADX INFO: Access modifiers changed from: package-private */
    public p(l[] lVarArr, int i4, int i5, int i6) {
        this.f4172a = lVarArr;
        this.f4178h = i4;
        this.f4176e = i5;
        this.f = i5;
        this.f4177g = i6;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public final l a() {
        l[] lVarArr;
        int length;
        int i4;
        o oVar;
        o oVar2;
        l lVar = this.f4173b;
        if (lVar != null) {
            lVar = lVar.f4167d;
        }
        while (lVar == null) {
            if (this.f >= this.f4177g || (lVarArr = this.f4172a) == null || (length = lVarArr.length) <= (i4 = this.f4176e) || i4 < 0) {
                this.f4173b = null;
                return null;
            }
            l k4 = ConcurrentHashMap.k(lVarArr, i4);
            if (k4 == null || k4.f4164a >= 0) {
                lVar = k4;
            } else if (k4 instanceof g) {
                this.f4172a = ((g) k4).f4157e;
                o oVar3 = this.f4175d;
                if (oVar3 != null) {
                    this.f4175d = oVar3.f4171d;
                    oVar2 = oVar3;
                } else {
                    oVar2 = new Object();
                }
                oVar2.f4170c = lVarArr;
                oVar2.f4168a = length;
                oVar2.f4169b = i4;
                oVar2.f4171d = this.f4174c;
                this.f4174c = oVar2;
                lVar = null;
            } else {
                lVar = k4 instanceof q ? ((q) k4).f : null;
            }
            if (this.f4174c != null) {
                while (true) {
                    oVar = this.f4174c;
                    if (oVar == null) {
                        break;
                    }
                    int i5 = this.f4176e;
                    int i6 = oVar.f4168a;
                    int i7 = i5 + i6;
                    this.f4176e = i7;
                    if (i7 < length) {
                        break;
                    }
                    this.f4176e = oVar.f4169b;
                    this.f4172a = oVar.f4170c;
                    oVar.f4170c = null;
                    o oVar4 = oVar.f4171d;
                    oVar.f4171d = this.f4175d;
                    this.f4174c = oVar4;
                    this.f4175d = oVar;
                    length = i6;
                }
                if (oVar == null) {
                    int i8 = this.f4176e + this.f4178h;
                    this.f4176e = i8;
                    if (i8 >= length) {
                        int i9 = this.f + 1;
                        this.f = i9;
                        this.f4176e = i9;
                    }
                }
            } else {
                int i10 = i4 + this.f4178h;
                this.f4176e = i10;
                if (i10 >= length) {
                    int i11 = this.f + 1;
                    this.f = i11;
                    this.f4176e = i11;
                }
            }
        }
        this.f4173b = lVar;
        return lVar;
    }
}
