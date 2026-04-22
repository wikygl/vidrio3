package w;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import v.e;

/* renamed from: w.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0834b {

    /* renamed from: a  reason: collision with root package name */
    public final ArrayList<v.e> f6317a = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    public final a f6318b = new Object();

    /* renamed from: c  reason: collision with root package name */
    public final v.f f6319c;

    /* renamed from: w.b$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public e.a f6320a;

        /* renamed from: b  reason: collision with root package name */
        public e.a f6321b;

        /* renamed from: c  reason: collision with root package name */
        public int f6322c;

        /* renamed from: d  reason: collision with root package name */
        public int f6323d;

        /* renamed from: e  reason: collision with root package name */
        public int f6324e;
        public int f;

        /* renamed from: g  reason: collision with root package name */
        public int f6325g;

        /* renamed from: h  reason: collision with root package name */
        public boolean f6326h;

        /* renamed from: i  reason: collision with root package name */
        public boolean f6327i;

        /* renamed from: j  reason: collision with root package name */
        public boolean f6328j;
    }

    /* renamed from: w.b$b  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface InterfaceC0078b {
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.Object, w.b$a] */
    public C0834b(v.f fVar) {
        this.f6319c = fVar;
    }

    public final boolean a(InterfaceC0078b interfaceC0078b, v.e eVar, boolean z4) {
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        e.a[] aVarArr = eVar.f6075J;
        e.a aVar = aVarArr[0];
        a aVar2 = this.f6318b;
        aVar2.f6320a = aVar;
        boolean z9 = true;
        aVar2.f6321b = aVarArr[1];
        aVar2.f6322c = eVar.l();
        aVar2.f6323d = eVar.i();
        aVar2.f6327i = false;
        aVar2.f6328j = z4;
        e.a aVar3 = aVar2.f6320a;
        e.a aVar4 = e.a.f6123l;
        if (aVar3 == aVar4) {
            z5 = true;
        } else {
            z5 = false;
        }
        if (aVar2.f6321b == aVar4) {
            z6 = true;
        } else {
            z6 = false;
        }
        if (z5 && eVar.f6079N > 0.0f) {
            z7 = true;
        } else {
            z7 = false;
        }
        if (z6 && eVar.f6079N > 0.0f) {
            z8 = true;
        } else {
            z8 = false;
        }
        e.a aVar5 = e.a.f6121j;
        int[] iArr = eVar.f6106l;
        if (z7 && iArr[0] == 4) {
            aVar2.f6320a = aVar5;
        }
        if (z8 && iArr[1] == 4) {
            aVar2.f6321b = aVar5;
        }
        ((ConstraintLayout.b) interfaceC0078b).a(eVar, aVar2);
        eVar.y(aVar2.f6324e);
        eVar.v(aVar2.f);
        eVar.f6117w = aVar2.f6326h;
        int i4 = aVar2.f6325g;
        eVar.f6083R = i4;
        if (i4 <= 0) {
            z9 = false;
        }
        eVar.f6117w = z9;
        aVar2.f6328j = false;
        return aVar2.f6327i;
    }

    public final void b(v.f fVar, int i4, int i5) {
        int i6 = fVar.f6084S;
        int i7 = fVar.f6085T;
        fVar.f6084S = 0;
        fVar.f6085T = 0;
        fVar.y(i4);
        fVar.v(i5);
        if (i6 < 0) {
            fVar.f6084S = 0;
        } else {
            fVar.f6084S = i6;
        }
        if (i7 < 0) {
            fVar.f6085T = 0;
        } else {
            fVar.f6085T = i7;
        }
        this.f6319c.B();
    }
}
