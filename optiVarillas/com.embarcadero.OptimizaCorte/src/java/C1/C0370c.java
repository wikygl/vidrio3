package c1;

import S0.C0249c0;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import j$.util.Comparator$CC;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* renamed from: c1.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0370c {

    /* renamed from: a  reason: collision with root package name */
    public static boolean f2938a;

    /* renamed from: b  reason: collision with root package name */
    public static ArrayList<Double> f2939b = new ArrayList<>();

    /* renamed from: c  reason: collision with root package name */
    public static double f2940c = 0.0d;

    /* renamed from: d  reason: collision with root package name */
    public static double f2941d = 0.0d;

    /* renamed from: e  reason: collision with root package name */
    public static R2.d f2942e = new R2.d(1);
    public static boolean f;

    public static R2.d a(ArrayList<Double> arrayList, double d4, int i4) {
        f2939b = new ArrayList<>();
        f2940c = 0.0d;
        f2941d = 0.0d;
        f2942e = new R2.d(1);
        f = false;
        R2.d dVar = new R2.d(1);
        ArrayList arrayList2 = (ArrayList) C0373f.f(d4, arrayList).f736k;
        for (int i5 = 1; i5 <= i4; i5++) {
            try {
                if (!f) {
                    ArrayList g4 = g(i5, new ArrayList(arrayList2));
                    c(g4, new Double[i5], 0, g4.size() - 1, 0, i5, d4);
                    dVar = f2942e;
                }
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        }
        return dVar;
    }

    public static ArrayList b(double d4, ArrayList arrayList) {
        int ceil = (int) Math.ceil(d4);
        int[] iArr = new int[arrayList.size()];
        int size = arrayList.size();
        ArrayList arrayList2 = new ArrayList(arrayList);
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            iArr[i4] = (int) Math.ceil(((Double) arrayList.get(i4)).doubleValue());
        }
        ArrayList arrayList3 = new ArrayList();
        try {
            int[] iArr2 = new int[(ceil + 1) * (size + 1)];
            for (int i5 = 0; i5 <= size; i5++) {
                for (int i6 = 0; i6 <= ceil; i6++) {
                    if (i5 != 0 && i6 != 0) {
                        int i7 = i5 - 1;
                        int i8 = iArr[i7];
                        if (i8 <= i6) {
                            int i9 = i7 * ceil;
                            iArr2[(i5 * ceil) + i6] = Math.max(i8 + iArr2[(i6 - i8) + i9], iArr2[i9 + i6]);
                        } else {
                            iArr2[(i5 * ceil) + i6] = iArr2[(i7 * ceil) + i6];
                        }
                    } else {
                        iArr2[(i5 * ceil) + i6] = 0;
                    }
                }
            }
            int i10 = iArr2[(size * ceil) + ceil];
            int i11 = ceil;
            while (size > 0 && i10 > 0) {
                int i12 = size - 1;
                if (i10 != iArr2[(i12 * ceil) + i11]) {
                    arrayList3.add(Double.valueOf(iArr[i12]));
                    int i13 = iArr[i12];
                    i10 -= i13;
                    i11 -= i13;
                }
                size--;
            }
            for (int i14 = 0; i14 < arrayList3.size(); i14++) {
                int i15 = 0;
                while (true) {
                    if (i15 >= arrayList2.size()) {
                        break;
                    } else if (((Double) arrayList3.get(i14)).equals(Double.valueOf(Math.ceil(((Double) arrayList2.get(i15)).doubleValue())))) {
                        arrayList3.set(i14, (Double) arrayList2.get(i15));
                        arrayList2.remove(i15);
                        break;
                    } else {
                        i15++;
                    }
                }
            }
            return arrayList3;
        } catch (OutOfMemoryError unused) {
            throw new OutOfMemoryError("There is not enough memory to perform the operation.");
        }
    }

    public static void c(ArrayList<Double> arrayList, Double[] dArr, int i4, int i5, int i6, int i7, double d4) {
        if (i6 == i7) {
            f2939b.clear();
            f2939b.addAll(Arrays.asList(dArr).subList(0, i7));
            f2939b.trimToSize();
            Iterator<Double> it = f2939b.iterator();
            double d5 = 0.0d;
            while (it.hasNext()) {
                d5 += it.next().doubleValue();
            }
            f2941d = d5;
            if (d5 <= d4 && d5 > f2940c) {
                if (d5 == d4) {
                    f = true;
                }
                R2.d dVar = f2942e;
                ArrayList<Double> arrayList2 = f2939b;
                dVar.getClass();
                dVar.f2063k = new ArrayList(arrayList2);
                f2942e.getClass();
                f2940c = f2941d;
            }
            f2939b.clear();
        } else if (!f) {
            int i8 = i4;
            while (i8 <= i5 && (i5 - i8) + 1 >= i7 - i6) {
                dArr[i6] = arrayList.get(i8);
                int i9 = i8 + 1;
                c(arrayList, dArr, i9, i5, i6 + 1, i7, d4);
                i8 = i9;
            }
        }
    }

    public static C0368a d(ArrayList<Double> arrayList, double d4, int i4, boolean z4, boolean z5, boolean z6, int i5) {
        double d5 = d4;
        C0368a c0368a = new C0368a();
        ArrayList<Double> arrayList2 = new ArrayList<>();
        c0368a.f2934a = d5;
        ArrayList arrayList3 = new ArrayList();
        Collections.sort(arrayList, Collections.reverseOrder());
        int i6 = 0;
        if (z6) {
            ArrayList arrayList4 = new ArrayList(arrayList);
            if (z4) {
                arrayList4 = new ArrayList();
                for (int i7 = 0; i7 < arrayList.size(); i7++) {
                    if (arrayList.get(i7).doubleValue() <= d5) {
                        arrayList4.add(arrayList.get(i7));
                    }
                }
            }
            double d6 = 0.0d;
            while (true) {
                try {
                    ArrayList arrayList5 = new ArrayList(g(i5, arrayList4));
                    Collections.sort(arrayList5, Collections.reverseOrder());
                    ArrayList arrayList6 = new ArrayList();
                    if (!arrayList5.isEmpty()) {
                        if (!z4) {
                            d6 = ((Double) arrayList5.get(i6)).doubleValue();
                            arrayList5.remove(i6);
                        }
                        double d7 = 0.0d;
                        if (!arrayList3.isEmpty()) {
                            for (int i8 = 0; i8 < arrayList3.size(); i8++) {
                                d7 += ((Double) arrayList3.get(i8)).doubleValue();
                            }
                        }
                        double d8 = c0368a.f2934a;
                        if (d7 + d6 <= d8) {
                            arrayList6 = b((d8 - d6) - d7, arrayList5);
                            if (!z4) {
                                arrayList6.add(Double.valueOf(d6));
                            }
                        } else {
                            arrayList6 = arrayList6;
                        }
                    }
                    if (!arrayList6.isEmpty()) {
                        for (int i9 = 0; i9 < arrayList6.size(); i9++) {
                            int i10 = 0;
                            while (true) {
                                if (i10 >= arrayList4.size()) {
                                    break;
                                } else if (((Double) arrayList4.get(i10)).equals(arrayList6.get(i9))) {
                                    arrayList4.remove(i10);
                                    break;
                                } else {
                                    i10++;
                                }
                            }
                        }
                        arrayList3.addAll(arrayList6);
                    }
                    if (arrayList6.isEmpty()) {
                        break;
                    }
                    i6 = 0;
                } catch (OutOfMemoryError unused) {
                    throw new OutOfMemoryError("There is not enough memory to perform the operation.");
                }
            }
            Collections.sort(arrayList3, Collections.reverseOrder());
            c0368a.f2936c = new ArrayList<>(arrayList3);
        } else {
            int i11 = 0;
            while (i11 < arrayList.size()) {
                if (d5 >= arrayList.get(i11).doubleValue()) {
                    arrayList2.add(arrayList.get(i11));
                    d5 -= arrayList.get(i11).doubleValue();
                    arrayList.remove(i11);
                    i11--;
                }
                i11++;
            }
            if (((arrayList2.size() != 2 && (!z4 || arrayList2.size() != 1)) || d5 <= 0.0d) && (arrayList2.size() < 2 || !z5)) {
                if (arrayList2.size() > 2 && d5 > 0.0d) {
                    try {
                        double doubleValue = d5 + arrayList2.get(arrayList2.size() - 1).doubleValue();
                        arrayList.add(arrayList2.get(arrayList2.size() - 1));
                        arrayList2.remove(arrayList2.size() - 1);
                        double doubleValue2 = doubleValue + arrayList2.get(arrayList2.size() - 1).doubleValue();
                        arrayList.add(arrayList2.get(arrayList2.size() - 1));
                        arrayList2.remove(arrayList2.size() - 1);
                        Collections.sort(arrayList, Collections.reverseOrder());
                        arrayList2.addAll((ArrayList) a(arrayList, doubleValue2, i4).f2063k);
                    } catch (NullPointerException e4) {
                        e4.printStackTrace();
                    }
                }
            } else {
                try {
                    double doubleValue3 = d5 + arrayList2.get(arrayList2.size() - 1).doubleValue();
                    arrayList.add(arrayList2.get(arrayList2.size() - 1));
                    arrayList2.remove(arrayList2.size() - 1);
                    Collections.sort(arrayList, Collections.reverseOrder());
                    arrayList2.addAll((ArrayList) a(arrayList, doubleValue3, i4).f2063k);
                } catch (NullPointerException e5) {
                    e5.printStackTrace();
                }
            }
            try {
                Collections.sort(arrayList2, Collections.reverseOrder());
            } catch (NullPointerException e6) {
                e6.printStackTrace();
            }
            c0368a.f2936c = arrayList2;
        }
        return c0368a;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.Object, java.util.Comparator] */
    /* JADX WARN: Type inference failed for: r8v5, types: [java.util.function.ToDoubleFunction, java.lang.Object] */
    public static ArrayList<C0368a> e(ArrayList<Double> arrayList, ArrayList<Double> arrayList2, int i4, boolean z4, boolean z5, int i5) {
        C0368a c0368a;
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        ArrayList<C0368a> arrayList5 = new ArrayList<>();
        if (!arrayList.isEmpty() && !arrayList2.isEmpty()) {
            Collections.sort(arrayList2, Collections.reverseOrder());
            Collections.sort(arrayList, Collections.reverseOrder());
            double size = arrayList.size();
            while (!arrayList.isEmpty() && !f2938a) {
                arrayList3.clear();
                arrayList4.clear();
                for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                    arrayList3.add(new ArrayList((ArrayList) C0373f.f(arrayList2.get(i6).doubleValue(), arrayList).f736k));
                }
                for (int i7 = 0; i7 < arrayList2.size(); i7++) {
                    Collections.sort((List) arrayList3.get(i7), Collections.reverseOrder());
                    if (!((ArrayList) C0373f.f(arrayList2.get(i7).doubleValue(), arrayList).f736k).isEmpty()) {
                        try {
                            arrayList4.add(d((ArrayList) arrayList3.get(i7), arrayList2.get(i7).doubleValue(), i4, false, z4, z5, i5));
                        } catch (OutOfMemoryError unused) {
                            throw new OutOfMemoryError("OutOfMemoryError: There is not enough memory to perform the operation.");
                        }
                    }
                }
                Iterator it = arrayList4.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (((C0368a) it.next()) == null) {
                            c0368a = null;
                            break;
                        }
                    } else {
                        Collections.sort(arrayList4, Comparator$CC.comparingDouble(new Object()));
                        c0368a = (C0368a) arrayList4.get(0);
                        break;
                    }
                }
                if (c0368a != null) {
                    boolean d4 = C0373f.d(c0368a, arrayList);
                    while (d4) {
                        arrayList5.add(c0368a);
                        for (int i8 = 0; i8 < c0368a.f2936c.size(); i8++) {
                            int i9 = 0;
                            while (true) {
                                if (i9 >= arrayList.size()) {
                                    break;
                                } else if (c0368a.f2936c.get(i8).equals(arrayList.get(i9))) {
                                    arrayList.remove(i9);
                                    arrayList.trimToSize();
                                    break;
                                } else {
                                    i9++;
                                }
                            }
                        }
                        ActivityListaCorte.l0.setProgress(100 - ((int) ((arrayList.size() / size) * 100.0d)));
                        d4 = C0373f.d(c0368a, arrayList);
                    }
                } else {
                    throw new OutOfMemoryError("OutOfMemoryError: There is not enough memory to perform the operation.");
                }
            }
            Collections.sort(arrayList5, new Object());
        }
        return arrayList5;
    }

    public static ArrayList f(ArrayList arrayList, ArrayList arrayList2, int i4, boolean z4, boolean z5) {
        ArrayList arrayList3 = new ArrayList();
        double size = arrayList2.size();
        Collections.sort(arrayList2);
        Collections.sort(arrayList, Collections.reverseOrder());
        int i5 = 0;
        while (i5 < arrayList2.size()) {
            if (!f2938a) {
                ArrayList arrayList4 = new ArrayList(arrayList);
                Double d4 = (Double) arrayList2.get(i5);
                C0368a d5 = d(arrayList4, ((Double) arrayList2.get(i5)).doubleValue(), i4, true, z4, z5, 10);
                boolean d6 = C0373f.d(d5, arrayList);
                while (d6 && !f2938a) {
                    arrayList3.add(d5);
                    Iterator<Double> it = d5.f2936c.iterator();
                    while (it.hasNext()) {
                        Double next = it.next();
                        int i6 = 0;
                        while (true) {
                            if (i6 >= arrayList.size()) {
                                break;
                            } else if (next.equals(arrayList.get(i6))) {
                                arrayList.remove(i6);
                                break;
                            } else {
                                i6++;
                            }
                        }
                    }
                    if (i5 < arrayList2.size() - 1) {
                        if (C0373f.d(d5, arrayList) && ((Double) arrayList2.get(i5 + 1)).equals(d4)) {
                            d6 = true;
                        } else {
                            d6 = false;
                        }
                        if (d6) {
                            i5++;
                        }
                    }
                }
            }
            i5++;
            ActivityListaCorte.l0.setProgress(100 - ((int) ((i5 / size) * 100.0d)));
        }
        Collections.sort(arrayList3, new C0249c0(1));
        return arrayList3;
    }

    public static ArrayList g(int i4, ArrayList arrayList) {
        ArrayList arrayList2 = new ArrayList();
        Collections.sort(arrayList, Collections.reverseOrder());
        if (arrayList.size() >= i4) {
            arrayList2.add((Double) arrayList.get(0));
            int size = arrayList2.size();
            int i5 = 1;
            for (int i6 = 1; i6 < arrayList.size(); i6++) {
                if (((Double) arrayList2.get(size - 1)).equals(arrayList.get(i6))) {
                    if (i5 < i4) {
                        arrayList2.add((Double) arrayList.get(i6));
                        i5++;
                    }
                } else {
                    arrayList2.add((Double) arrayList.get(i6));
                    size = arrayList2.size();
                    i5 = 1;
                }
            }
            return arrayList2;
        }
        return new ArrayList(arrayList);
    }
}
