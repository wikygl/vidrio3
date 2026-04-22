package D1;

import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class A {

    /* renamed from: a  reason: collision with root package name */
    public final String[] f620a;

    /* renamed from: b  reason: collision with root package name */
    public final double[] f621b;

    /* renamed from: c  reason: collision with root package name */
    public final double[] f622c;

    /* renamed from: d  reason: collision with root package name */
    public final int[] f623d;

    /* renamed from: e  reason: collision with root package name */
    public int f624e;

    public A(C0205z c0205z) {
        ArrayList arrayList = (ArrayList) c0205z.f795k;
        int size = arrayList.size();
        this.f620a = (String[]) ((ArrayList) c0205z.f794j).toArray(new String[size]);
        int size2 = arrayList.size();
        double[] dArr = new double[size2];
        for (int i4 = 0; i4 < size2; i4++) {
            dArr[i4] = ((Double) arrayList.get(i4)).doubleValue();
        }
        this.f621b = dArr;
        ArrayList arrayList2 = (ArrayList) c0205z.f796l;
        int size3 = arrayList2.size();
        double[] dArr2 = new double[size3];
        for (int i5 = 0; i5 < size3; i5++) {
            dArr2[i5] = ((Double) arrayList2.get(i5)).doubleValue();
        }
        this.f622c = dArr2;
        this.f623d = new int[size];
        this.f624e = 0;
    }
}
