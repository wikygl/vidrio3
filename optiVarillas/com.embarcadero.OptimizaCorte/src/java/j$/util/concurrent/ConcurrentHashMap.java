package j$.util.concurrent;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable, v {

    /* renamed from: g  reason: collision with root package name */
    static final int f4132g = Runtime.getRuntime().availableProcessors();

    /* renamed from: h  reason: collision with root package name */
    private static final j$.sun.misc.a f4133h;

    /* renamed from: i  reason: collision with root package name */
    private static final long f4134i;

    /* renamed from: j  reason: collision with root package name */
    private static final long f4135j;

    /* renamed from: k  reason: collision with root package name */
    private static final long f4136k;

    /* renamed from: l  reason: collision with root package name */
    private static final long f4137l;

    /* renamed from: m  reason: collision with root package name */
    private static final long f4138m;

    /* renamed from: n  reason: collision with root package name */
    private static final int f4139n;

    /* renamed from: o  reason: collision with root package name */
    private static final int f4140o;
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = 7249069246763182397L;

    /* renamed from: a  reason: collision with root package name */
    volatile transient l[] f4141a;

    /* renamed from: b  reason: collision with root package name */
    private volatile transient l[] f4142b;
    private volatile transient long baseCount;

    /* renamed from: c  reason: collision with root package name */
    private volatile transient c[] f4143c;
    private volatile transient int cellsBusy;

    /* renamed from: d  reason: collision with root package name */
    private transient i f4144d;

    /* renamed from: e  reason: collision with root package name */
    private transient s f4145e;
    private transient e f;
    private volatile transient int sizeCtl;
    private volatile transient int transferIndex;

    static {
        ObjectStreamField objectStreamField = new ObjectStreamField("segments", n[].class);
        Class cls = Integer.TYPE;
        serialPersistentFields = new ObjectStreamField[]{objectStreamField, new ObjectStreamField("segmentMask", cls), new ObjectStreamField("segmentShift", cls)};
        j$.sun.misc.a h4 = j$.sun.misc.a.h();
        f4133h = h4;
        f4134i = h4.j(ConcurrentHashMap.class, "sizeCtl");
        f4135j = h4.j(ConcurrentHashMap.class, "transferIndex");
        f4136k = h4.j(ConcurrentHashMap.class, "baseCount");
        f4137l = h4.j(ConcurrentHashMap.class, "cellsBusy");
        f4138m = h4.j(c.class, "value");
        f4139n = h4.a(l[].class);
        int b4 = h4.b(l[].class);
        if (((b4 - 1) & b4) != 0) {
            throw new ExceptionInInitializerError("array index scale not a power of two");
        }
        f4140o = 31 - Integer.numberOfLeadingZeros(b4);
    }

    public ConcurrentHashMap() {
    }

    public ConcurrentHashMap(int i4) {
        this(i4, 0.75f, 1);
    }

    public ConcurrentHashMap(int i4, float f, int i5) {
        if (f <= 0.0f || i4 < 0 || i5 <= 0) {
            throw new IllegalArgumentException();
        }
        long j4 = (long) (((i4 < i5 ? i5 : i4) / f) + 1.0d);
        this.sizeCtl = j4 >= 1073741824 ? 1073741824 : l((int) j4);
    }

    public ConcurrentHashMap(Map<? extends K, ? extends V> map) {
        this.sizeCtl = 16;
        putAll(map);
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x001a, code lost:
        if (r1.d(r25, r3, r5, r14) == false) goto L53;
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x013f, code lost:
        if (r25.f4143c != r7) goto L159;
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x0141, code lost:
        r25.f4143c = (j$.util.concurrent.c[]) java.util.Arrays.copyOf(r7, r8 << 1);
     */
    /* JADX WARN: Removed duplicated region for block: B:149:0x019f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:165:0x00ba A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void a(long r26, int r28) {
        /*
            Method dump skipped, instructions count: 416
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.a(long, int):void");
    }

    static final boolean b(l[] lVarArr, int i4, l lVar) {
        return f4133h.e(lVarArr, (i4 << f4140o) + f4139n, lVar);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Class c(Object obj) {
        Type[] actualTypeArguments;
        if (obj instanceof Comparable) {
            Class<?> cls = obj.getClass();
            if (cls == String.class) {
                return cls;
            }
            Type[] genericInterfaces = cls.getGenericInterfaces();
            if (genericInterfaces != null) {
                for (Type type : genericInterfaces) {
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        if (parameterizedType.getRawType() == Comparable.class && (actualTypeArguments = parameterizedType.getActualTypeArguments()) != null && actualTypeArguments.length == 1 && actualTypeArguments[0] == cls) {
                            return cls;
                        }
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private final l[] e() {
        while (true) {
            l[] lVarArr = this.f4141a;
            if (lVarArr != null && lVarArr.length != 0) {
                return lVarArr;
            }
            int i4 = this.sizeCtl;
            if (i4 < 0) {
                Thread.yield();
            } else if (f4133h.c(this, f4134i, i4, -1)) {
                try {
                    l[] lVarArr2 = this.f4141a;
                    if (lVarArr2 != null) {
                        if (lVarArr2.length == 0) {
                        }
                        this.sizeCtl = i4;
                        return lVarArr2;
                    }
                    int i5 = i4 > 0 ? i4 : 16;
                    l[] lVarArr3 = new l[i5];
                    this.f4141a = lVarArr3;
                    i4 = i5 - (i5 >>> 2);
                    lVarArr2 = lVarArr3;
                    this.sizeCtl = i4;
                    return lVarArr2;
                } catch (Throwable th) {
                    this.sizeCtl = i4;
                    throw th;
                }
            }
        }
    }

    static final void h(l[] lVarArr, int i4, l lVar) {
        f4133h.l(lVarArr, (i4 << f4140o) + f4139n, lVar);
    }

    static final int i(int i4) {
        return (i4 ^ (i4 >>> 16)) & Integer.MAX_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final l k(l[] lVarArr, int i4) {
        return (l) f4133h.g(lVarArr, (i4 << f4140o) + f4139n);
    }

    private static final int l(int i4) {
        int numberOfLeadingZeros = (-1) >>> Integer.numberOfLeadingZeros(i4 - 1);
        if (numberOfLeadingZeros < 0) {
            return 1;
        }
        if (numberOfLeadingZeros >= 1073741824) {
            return 1073741824;
        }
        return 1 + numberOfLeadingZeros;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v10, types: [j$.util.concurrent.l] */
    /* JADX WARN: Type inference failed for: r13v12, types: [j$.util.concurrent.l] */
    /* JADX WARN: Type inference failed for: r5v17, types: [j$.util.concurrent.l] */
    /* JADX WARN: Type inference failed for: r5v22, types: [j$.util.concurrent.l] */
    private final void m(l[] lVarArr, l[] lVarArr2) {
        l[] lVarArr3;
        int i4;
        int i5;
        g gVar;
        ConcurrentHashMap<K, V> concurrentHashMap;
        int i6;
        r rVar;
        ConcurrentHashMap<K, V> concurrentHashMap2 = this;
        l[] lVarArr4 = lVarArr;
        int length = lVarArr4.length;
        int i7 = f4132g;
        int i8 = i7 > 1 ? (length >>> 3) / i7 : length;
        int i9 = i8 < 16 ? 16 : i8;
        if (lVarArr2 == null) {
            try {
                l[] lVarArr5 = new l[length << 1];
                concurrentHashMap2.f4142b = lVarArr5;
                concurrentHashMap2.transferIndex = length;
                lVarArr3 = lVarArr5;
            } catch (Throwable unused) {
                concurrentHashMap2.sizeCtl = Integer.MAX_VALUE;
                return;
            }
        } else {
            lVarArr3 = lVarArr2;
        }
        int length2 = lVarArr3.length;
        g gVar2 = new g(lVarArr3);
        int i10 = 0;
        int i11 = 0;
        boolean z4 = true;
        boolean z5 = false;
        while (true) {
            if (z4) {
                int i12 = i11 - 1;
                if (i12 >= i10 || z5) {
                    i10 = i10;
                    i11 = i12;
                } else {
                    int i13 = concurrentHashMap2.transferIndex;
                    if (i13 <= 0) {
                        i11 = -1;
                    } else {
                        j$.sun.misc.a aVar = f4133h;
                        long j4 = f4135j;
                        int i14 = i13 > i9 ? i13 - i9 : 0;
                        int i15 = i10;
                        if (aVar.c(this, j4, i13, i14)) {
                            i11 = i13 - 1;
                            i10 = i14;
                        } else {
                            i10 = i15;
                            i11 = i12;
                        }
                    }
                }
                z4 = false;
            } else {
                int i16 = i10;
                r rVar2 = null;
                if (i11 < 0 || i11 >= length || (i6 = i11 + length) >= length2) {
                    i4 = i9;
                    i5 = length2;
                    gVar = gVar2;
                    concurrentHashMap = this;
                    if (z5) {
                        concurrentHashMap.f4142b = null;
                        concurrentHashMap.f4141a = lVarArr3;
                        concurrentHashMap.sizeCtl = (length << 1) - (length >>> 1);
                        return;
                    }
                    j$.sun.misc.a aVar2 = f4133h;
                    long j5 = f4134i;
                    int i17 = concurrentHashMap.sizeCtl;
                    int i18 = i11;
                    if (!aVar2.c(this, j5, i17, i17 - 1)) {
                        i11 = i18;
                    } else if (i17 - 2 != ((Integer.numberOfLeadingZeros(length) | 32768) << 16)) {
                        return;
                    } else {
                        i11 = length;
                        z4 = true;
                        z5 = true;
                    }
                } else {
                    l k4 = k(lVarArr4, i11);
                    if (k4 == null) {
                        z4 = b(lVarArr4, i11, gVar2);
                        concurrentHashMap = concurrentHashMap2;
                        i4 = i9;
                        i5 = length2;
                        gVar = gVar2;
                    } else {
                        int i19 = k4.f4164a;
                        if (i19 == -1) {
                            concurrentHashMap = concurrentHashMap2;
                            i4 = i9;
                            i5 = length2;
                            gVar = gVar2;
                            z4 = true;
                        } else {
                            synchronized (k4) {
                                try {
                                    if (k(lVarArr4, i11) == k4) {
                                        if (i19 >= 0) {
                                            int i20 = i19 & length;
                                            r rVar3 = k4;
                                            for (r rVar4 = k4.f4167d; rVar4 != null; rVar4 = rVar4.f4167d) {
                                                int i21 = rVar4.f4164a & length;
                                                if (i21 != i20) {
                                                    rVar3 = rVar4;
                                                    i20 = i21;
                                                }
                                            }
                                            if (i20 == 0) {
                                                rVar = null;
                                                rVar2 = rVar3;
                                            } else {
                                                rVar = rVar3;
                                            }
                                            l lVar = k4;
                                            while (lVar != rVar3) {
                                                int i22 = lVar.f4164a;
                                                Object obj = lVar.f4165b;
                                                int i23 = i9;
                                                Object obj2 = lVar.f4166c;
                                                int i24 = length2;
                                                if ((i22 & length) == 0) {
                                                    rVar2 = new l(i22, obj, obj2, rVar2);
                                                } else {
                                                    rVar = new l(i22, obj, obj2, rVar);
                                                }
                                                lVar = lVar.f4167d;
                                                i9 = i23;
                                                length2 = i24;
                                            }
                                            i4 = i9;
                                            i5 = length2;
                                            h(lVarArr3, i11, rVar2);
                                            h(lVarArr3, i6, rVar);
                                            h(lVarArr4, i11, gVar2);
                                            gVar = gVar2;
                                        } else {
                                            i4 = i9;
                                            i5 = length2;
                                            if (k4 instanceof q) {
                                                q qVar = (q) k4;
                                                r rVar5 = null;
                                                r rVar6 = null;
                                                l lVar2 = qVar.f;
                                                int i25 = 0;
                                                int i26 = 0;
                                                r rVar7 = null;
                                                while (lVar2 != null) {
                                                    q qVar2 = qVar;
                                                    int i27 = lVar2.f4164a;
                                                    g gVar3 = gVar2;
                                                    r rVar8 = new r(i27, lVar2.f4165b, lVar2.f4166c, null, null);
                                                    if ((i27 & length) == 0) {
                                                        rVar8.f4185h = rVar6;
                                                        if (rVar6 == null) {
                                                            rVar2 = rVar8;
                                                        } else {
                                                            rVar6.f4167d = rVar8;
                                                        }
                                                        i25++;
                                                        rVar6 = rVar8;
                                                    } else {
                                                        rVar8.f4185h = rVar5;
                                                        if (rVar5 == null) {
                                                            rVar7 = rVar8;
                                                        } else {
                                                            rVar5.f4167d = rVar8;
                                                        }
                                                        i26++;
                                                        rVar5 = rVar8;
                                                    }
                                                    lVar2 = lVar2.f4167d;
                                                    qVar = qVar2;
                                                    gVar2 = gVar3;
                                                }
                                                q qVar3 = qVar;
                                                g gVar4 = gVar2;
                                                l p4 = i25 <= 6 ? p(rVar2) : i26 != 0 ? new q(rVar2) : qVar3;
                                                l p5 = i26 <= 6 ? p(rVar7) : i25 != 0 ? new q(rVar7) : qVar3;
                                                h(lVarArr3, i11, p4);
                                                h(lVarArr3, i6, p5);
                                                lVarArr4 = lVarArr;
                                                gVar = gVar4;
                                                h(lVarArr4, i11, gVar);
                                            }
                                        }
                                        z4 = true;
                                    } else {
                                        i4 = i9;
                                        i5 = length2;
                                    }
                                    gVar = gVar2;
                                } finally {
                                }
                            }
                            concurrentHashMap = this;
                        }
                    }
                }
                gVar2 = gVar;
                concurrentHashMap2 = concurrentHashMap;
                i10 = i16;
                i9 = i4;
                length2 = i5;
            }
        }
    }

    private final void n(l[] lVarArr, int i4) {
        int length = lVarArr.length;
        if (length < 64) {
            o(length << 1);
            return;
        }
        l k4 = k(lVarArr, i4);
        if (k4 == null || k4.f4164a < 0) {
            return;
        }
        synchronized (k4) {
            try {
                if (k(lVarArr, i4) == k4) {
                    r rVar = null;
                    l lVar = k4;
                    r rVar2 = null;
                    while (lVar != null) {
                        r rVar3 = new r(lVar.f4164a, lVar.f4165b, lVar.f4166c, null, null);
                        rVar3.f4185h = rVar2;
                        if (rVar2 == null) {
                            rVar = rVar3;
                        } else {
                            rVar2.f4167d = rVar3;
                        }
                        lVar = lVar.f4167d;
                        rVar2 = rVar3;
                    }
                    h(lVarArr, i4, new q(rVar));
                }
            } finally {
            }
        }
    }

    private final void o(int i4) {
        int length;
        int l2 = i4 >= 536870912 ? 1073741824 : l(i4 + (i4 >>> 1) + 1);
        while (true) {
            int i5 = this.sizeCtl;
            if (i5 < 0) {
                return;
            }
            l[] lVarArr = this.f4141a;
            if (lVarArr == null || (length = lVarArr.length) == 0) {
                int i6 = i5 > l2 ? i5 : l2;
                if (f4133h.c(this, f4134i, i5, -1)) {
                    try {
                        if (this.f4141a == lVarArr) {
                            this.f4141a = new l[i6];
                            i5 = i6 - (i6 >>> 2);
                        }
                    } finally {
                        this.sizeCtl = i5;
                    }
                } else {
                    continue;
                }
            } else if (l2 <= i5 || length >= 1073741824) {
                return;
            } else {
                if (lVarArr == this.f4141a && f4133h.c(this, f4134i, i5, ((Integer.numberOfLeadingZeros(length) | 32768) << 16) + 2)) {
                    m(lVarArr, null);
                }
            }
        }
    }

    static l p(l lVar) {
        l lVar2 = null;
        l lVar3 = null;
        while (lVar != null) {
            l lVar4 = new l(lVar.f4164a, lVar.f4165b, lVar.f4166c);
            if (lVar3 == null) {
                lVar2 = lVar4;
            } else {
                lVar3.f4167d = lVar4;
            }
            lVar = lVar.f4167d;
            lVar3 = lVar4;
        }
        return lVar2;
    }

    private void readObject(ObjectInputStream objectInputStream) {
        long j4;
        boolean z4;
        boolean z5;
        Object obj;
        this.sizeCtl = -1;
        objectInputStream.defaultReadObject();
        long j5 = 0;
        long j6 = 0;
        l lVar = null;
        while (true) {
            Object readObject = objectInputStream.readObject();
            Object readObject2 = objectInputStream.readObject();
            j4 = 1;
            if (readObject == null || readObject2 == null) {
                break;
            }
            j6++;
            lVar = new l(i(readObject.hashCode()), readObject, readObject2, lVar);
        }
        if (j6 == 0) {
            this.sizeCtl = 0;
            return;
        }
        long j7 = (long) ((((float) j6) / 0.75f) + 1.0d);
        int l2 = j7 >= 1073741824 ? 1073741824 : l((int) j7);
        l[] lVarArr = new l[l2];
        int i4 = l2 - 1;
        while (lVar != null) {
            l lVar2 = lVar.f4167d;
            int i5 = lVar.f4164a;
            int i6 = i5 & i4;
            l k4 = k(lVarArr, i6);
            if (k4 == null) {
                z5 = true;
            } else {
                Object obj2 = lVar.f4165b;
                if (k4.f4164a >= 0) {
                    int i7 = 0;
                    for (l lVar3 = k4; lVar3 != null; lVar3 = lVar3.f4167d) {
                        if (lVar3.f4164a == i5 && ((obj = lVar3.f4165b) == obj2 || (obj != null && obj2.equals(obj)))) {
                            z4 = false;
                            break;
                        }
                        i7++;
                    }
                    z4 = true;
                    if (!z4 || i7 < 8) {
                        z5 = z4;
                    } else {
                        long j8 = j5 + 1;
                        lVar.f4167d = k4;
                        l lVar4 = lVar;
                        r rVar = null;
                        r rVar2 = null;
                        while (lVar4 != null) {
                            long j9 = j8;
                            r rVar3 = new r(lVar4.f4164a, lVar4.f4165b, lVar4.f4166c, null, null);
                            rVar3.f4185h = rVar2;
                            if (rVar2 == null) {
                                rVar = rVar3;
                            } else {
                                rVar2.f4167d = rVar3;
                            }
                            lVar4 = lVar4.f4167d;
                            rVar2 = rVar3;
                            j8 = j9;
                        }
                        h(lVarArr, i6, new q(rVar));
                        j5 = j8;
                    }
                } else if (((q) k4).e(i5, obj2, lVar.f4166c) == null) {
                    j5 += j4;
                }
                z5 = false;
            }
            j4 = 1;
            if (z5) {
                j5++;
                lVar.f4167d = k4;
                h(lVarArr, i6, lVar);
            }
            lVar = lVar2;
        }
        this.f4141a = lVarArr;
        this.sizeCtl = l2 - (l2 >>> 2);
        this.baseCount = j5;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void writeObject(ObjectOutputStream objectOutputStream) {
        int i4 = 1;
        int i5 = 0;
        while (i4 < 16) {
            i5++;
            i4 <<= 1;
        }
        int i6 = 32 - i5;
        int i7 = i4 - 1;
        n[] nVarArr = new n[16];
        for (int i8 = 0; i8 < 16; i8++) {
            nVarArr[i8] = new ReentrantLock();
        }
        ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("segments", nVarArr);
        putFields.put("segmentShift", i6);
        putFields.put("segmentMask", i7);
        objectOutputStream.writeFields();
        l[] lVarArr = this.f4141a;
        if (lVarArr != null) {
            p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
            while (true) {
                l a4 = pVar.a();
                if (a4 == null) {
                    break;
                }
                objectOutputStream.writeObject(a4.f4165b);
                objectOutputStream.writeObject(a4.f4166c);
            }
        }
        objectOutputStream.writeObject(null);
        objectOutputStream.writeObject(null);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final void clear() {
        l k4;
        l[] lVarArr = this.f4141a;
        long j4 = 0;
        loop0: while (true) {
            int i4 = 0;
            while (lVarArr != null && i4 < lVarArr.length) {
                k4 = k(lVarArr, i4);
                if (k4 == null) {
                    i4++;
                } else {
                    int i5 = k4.f4164a;
                    if (i5 == -1) {
                        break;
                    }
                    synchronized (k4) {
                        try {
                            if (k(lVarArr, i4) == k4) {
                                for (l lVar = i5 >= 0 ? k4 : k4 instanceof q ? ((q) k4).f : null; lVar != null; lVar = lVar.f4167d) {
                                    j4--;
                                }
                                h(lVarArr, i4, null);
                                i4++;
                            }
                        } finally {
                        }
                    }
                }
            }
            lVarArr = d(lVarArr, k4);
        }
        if (j4 != 0) {
            a(j4, -1);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:100:0x0112, code lost:
        if (r4 == 0) goto L91;
     */
    /* JADX WARN: Code restructure failed: missing block: B:101:0x0114, code lost:
        a(r4, r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:102:0x0118, code lost:
        return r5;
     */
    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object compute(java.lang.Object r14, java.util.function.BiFunction r15) {
        /*
            Method dump skipped, instructions count: 290
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.compute(java.lang.Object, java.util.function.BiFunction):java.lang.Object");
    }

    /* JADX WARN: Code restructure failed: missing block: B:97:0x00f0, code lost:
        if (r5 == null) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x00f2, code lost:
        a(1, r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x00f7, code lost:
        return r5;
     */
    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object computeIfAbsent(java.lang.Object r12, java.util.function.Function r13) {
        /*
            Method dump skipped, instructions count: 257
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.computeIfAbsent(java.lang.Object, java.util.function.Function):java.lang.Object");
    }

    /* JADX WARN: Code restructure failed: missing block: B:61:0x00aa, code lost:
        throw new java.lang.IllegalStateException("Recursive update");
     */
    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object computeIfPresent(java.lang.Object r14, java.util.function.BiFunction r15) {
        /*
            r13 = this;
            r0 = 0
            if (r14 == 0) goto Lbd
            if (r15 == 0) goto Lbd
            int r1 = r14.hashCode()
            int r1 = i(r1)
            j$.util.concurrent.l[] r2 = r13.f4141a
            r3 = 0
            r5 = r0
            r4 = 0
        L12:
            if (r2 == 0) goto Lb7
            int r6 = r2.length
            if (r6 != 0) goto L19
            goto Lb7
        L19:
            int r6 = r6 + (-1)
            r6 = r6 & r1
            j$.util.concurrent.l r7 = k(r2, r6)
            if (r7 != 0) goto L24
            goto Lae
        L24:
            int r8 = r7.f4164a
            r9 = -1
            if (r8 != r9) goto L2e
            j$.util.concurrent.l[] r2 = r13.d(r2, r7)
            goto L12
        L2e:
            monitor-enter(r7)
            j$.util.concurrent.l r10 = k(r2, r6)     // Catch: java.lang.Throwable -> L4b
            if (r10 != r7) goto Lab
            if (r8 < 0) goto L70
            r4 = 1
            r10 = r0
            r8 = r7
        L3a:
            int r11 = r8.f4164a     // Catch: java.lang.Throwable -> L4b
            if (r11 != r1) goto L65
            java.lang.Object r11 = r8.f4165b     // Catch: java.lang.Throwable -> L4b
            if (r11 == r14) goto L4e
            if (r11 == 0) goto L65
            boolean r11 = r14.equals(r11)     // Catch: java.lang.Throwable -> L4b
            if (r11 == 0) goto L65
            goto L4e
        L4b:
            r14 = move-exception
            goto Lb5
        L4e:
            java.lang.Object r5 = r8.f4166c     // Catch: java.lang.Throwable -> L4b
            java.lang.Object r5 = r15.apply(r14, r5)     // Catch: java.lang.Throwable -> L4b
            if (r5 == 0) goto L59
            r8.f4166c = r5     // Catch: java.lang.Throwable -> L4b
            goto Lab
        L59:
            j$.util.concurrent.l r3 = r8.f4167d     // Catch: java.lang.Throwable -> L4b
            if (r10 == 0) goto L60
            r10.f4167d = r3     // Catch: java.lang.Throwable -> L4b
            goto L63
        L60:
            h(r2, r6, r3)     // Catch: java.lang.Throwable -> L4b
        L63:
            r3 = -1
            goto Lab
        L65:
            j$.util.concurrent.l r10 = r8.f4167d     // Catch: java.lang.Throwable -> L4b
            if (r10 != 0) goto L6a
            goto Lab
        L6a:
            int r4 = r4 + 1
            r12 = r10
            r10 = r8
            r8 = r12
            goto L3a
        L70:
            boolean r8 = r7 instanceof j$.util.concurrent.q     // Catch: java.lang.Throwable -> L4b
            if (r8 == 0) goto L9e
            r4 = r7
            j$.util.concurrent.q r4 = (j$.util.concurrent.q) r4     // Catch: java.lang.Throwable -> L4b
            j$.util.concurrent.r r8 = r4.f4181e     // Catch: java.lang.Throwable -> L4b
            if (r8 == 0) goto L9c
            j$.util.concurrent.r r8 = r8.b(r1, r14, r0)     // Catch: java.lang.Throwable -> L4b
            if (r8 == 0) goto L9c
            java.lang.Object r5 = r8.f4166c     // Catch: java.lang.Throwable -> L4b
            java.lang.Object r5 = r15.apply(r14, r5)     // Catch: java.lang.Throwable -> L4b
            if (r5 == 0) goto L8c
            r8.f4166c = r5     // Catch: java.lang.Throwable -> L4b
            goto L9c
        L8c:
            boolean r3 = r4.f(r8)     // Catch: java.lang.Throwable -> L4b
            if (r3 == 0) goto L9b
            j$.util.concurrent.r r3 = r4.f     // Catch: java.lang.Throwable -> L4b
            j$.util.concurrent.l r3 = p(r3)     // Catch: java.lang.Throwable -> L4b
            h(r2, r6, r3)     // Catch: java.lang.Throwable -> L4b
        L9b:
            r3 = -1
        L9c:
            r4 = 2
            goto Lab
        L9e:
            boolean r6 = r7 instanceof j$.util.concurrent.m     // Catch: java.lang.Throwable -> L4b
            if (r6 != 0) goto La3
            goto Lab
        La3:
            java.lang.IllegalStateException r14 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L4b
            java.lang.String r15 = "Recursive update"
            r14.<init>(r15)     // Catch: java.lang.Throwable -> L4b
            throw r14     // Catch: java.lang.Throwable -> L4b
        Lab:
            monitor-exit(r7)     // Catch: java.lang.Throwable -> L4b
            if (r4 == 0) goto L12
        Lae:
            if (r3 == 0) goto Lb4
            long r14 = (long) r3
            r13.a(r14, r4)
        Lb4:
            return r5
        Lb5:
            monitor-exit(r7)     // Catch: java.lang.Throwable -> L4b
            throw r14
        Lb7:
            j$.util.concurrent.l[] r2 = r13.e()
            goto L12
        Lbd:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.computeIfPresent(java.lang.Object, java.util.function.BiFunction):java.lang.Object");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object obj) {
        return get(obj) != null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean containsValue(Object obj) {
        obj.getClass();
        l[] lVarArr = this.f4141a;
        if (lVarArr != null) {
            p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
            while (true) {
                l a4 = pVar.a();
                if (a4 == null) {
                    break;
                }
                Object obj2 = a4.f4166c;
                if (obj2 == obj) {
                    return true;
                }
                if (obj2 != null && obj.equals(obj2)) {
                    return true;
                }
            }
        }
        return false;
    }

    final l[] d(l[] lVarArr, l lVar) {
        l[] lVarArr2;
        int i4;
        if (!(lVar instanceof g) || (lVarArr2 = ((g) lVar).f4157e) == null) {
            return this.f4141a;
        }
        int numberOfLeadingZeros = Integer.numberOfLeadingZeros(lVarArr.length) | 32768;
        while (true) {
            if (lVarArr2 != this.f4142b || this.f4141a != lVarArr || (i4 = this.sizeCtl) >= 0 || (i4 >>> 16) != numberOfLeadingZeros || i4 == numberOfLeadingZeros + 1 || i4 == 65535 + numberOfLeadingZeros || this.transferIndex <= 0) {
                break;
            } else if (f4133h.c(this, f4134i, i4, i4 + 1)) {
                m(lVarArr, lVarArr2);
                break;
            }
        }
        return lVarArr2;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [j$.util.concurrent.b, j$.util.concurrent.e, java.util.Set<java.util.Map$Entry<K, V>>] */
    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        e eVar = this.f;
        if (eVar != null) {
            return eVar;
        }
        ?? r02 = (Set<Map.Entry<K, V>>) new b(this);
        this.f = r02;
        return r02;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean equals(Object obj) {
        V value;
        V v4;
        if (obj != this) {
            if (obj instanceof Map) {
                Map map = (Map) obj;
                l[] lVarArr = this.f4141a;
                int length = lVarArr == null ? 0 : lVarArr.length;
                p pVar = new p(lVarArr, length, 0, length);
                while (true) {
                    l a4 = pVar.a();
                    if (a4 == null) {
                        for (Map.Entry<K, V> entry : map.entrySet()) {
                            K key = entry.getKey();
                            if (key == null || (value = entry.getValue()) == null || (v4 = get(key)) == null || (value != v4 && !value.equals(v4))) {
                                return false;
                            }
                        }
                        return true;
                    }
                    Object obj2 = a4.f4166c;
                    Object obj3 = map.get(a4.f4165b);
                    if (obj3 == null || (obj3 != obj2 && !obj3.equals(obj2))) {
                        break;
                    }
                }
                return false;
            }
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x00a5, code lost:
        throw new java.lang.IllegalStateException("Recursive update");
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x00b4, code lost:
        a(1, r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x00b9, code lost:
        return null;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object f(java.lang.Object r9, java.lang.Object r10, boolean r11) {
        /*
            Method dump skipped, instructions count: 195
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.f(java.lang.Object, java.lang.Object, boolean):java.lang.Object");
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public final void forEach(BiConsumer biConsumer) {
        biConsumer.getClass();
        l[] lVarArr = this.f4141a;
        if (lVarArr == null) {
            return;
        }
        p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
        while (true) {
            l a4 = pVar.a();
            if (a4 == null) {
                return;
            }
            biConsumer.accept(a4.f4165b, a4.f4166c);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x00ab, code lost:
        throw new java.lang.IllegalStateException("Recursive update");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object g(java.lang.Object r13, java.lang.Object r14, java.lang.Object r15) {
        /*
            r12 = this;
            int r0 = r13.hashCode()
            int r0 = i(r0)
            j$.util.concurrent.l[] r1 = r12.f4141a
        La:
            r2 = 0
            if (r1 == 0) goto Lbd
            int r3 = r1.length
            if (r3 == 0) goto Lbd
            int r3 = r3 + (-1)
            r3 = r3 & r0
            j$.util.concurrent.l r4 = k(r1, r3)
            if (r4 != 0) goto L1b
            goto Lbd
        L1b:
            int r5 = r4.f4164a
            r6 = -1
            if (r5 != r6) goto L25
            j$.util.concurrent.l[] r1 = r12.d(r1, r4)
            goto La
        L25:
            monitor-enter(r4)
            j$.util.concurrent.l r7 = k(r1, r3)     // Catch: java.lang.Throwable -> L42
            if (r7 != r4) goto Lac
            r7 = 1
            if (r5 < 0) goto L6e
            r8 = r2
            r5 = r4
        L31:
            int r9 = r5.f4164a     // Catch: java.lang.Throwable -> L42
            if (r9 != r0) goto L65
            java.lang.Object r9 = r5.f4165b     // Catch: java.lang.Throwable -> L42
            if (r9 == r13) goto L45
            if (r9 == 0) goto L65
            boolean r9 = r13.equals(r9)     // Catch: java.lang.Throwable -> L42
            if (r9 == 0) goto L65
            goto L45
        L42:
            r13 = move-exception
            goto Lbb
        L45:
            java.lang.Object r9 = r5.f4166c     // Catch: java.lang.Throwable -> L42
            if (r15 == 0) goto L53
            if (r15 == r9) goto L53
            if (r9 == 0) goto Lad
            boolean r10 = r15.equals(r9)     // Catch: java.lang.Throwable -> L42
            if (r10 == 0) goto Lad
        L53:
            if (r14 == 0) goto L58
            r5.f4166c = r14     // Catch: java.lang.Throwable -> L42
            goto Lae
        L58:
            if (r8 == 0) goto L5f
            j$.util.concurrent.l r3 = r5.f4167d     // Catch: java.lang.Throwable -> L42
            r8.f4167d = r3     // Catch: java.lang.Throwable -> L42
            goto Lae
        L5f:
            j$.util.concurrent.l r5 = r5.f4167d     // Catch: java.lang.Throwable -> L42
        L61:
            h(r1, r3, r5)     // Catch: java.lang.Throwable -> L42
            goto Lae
        L65:
            j$.util.concurrent.l r8 = r5.f4167d     // Catch: java.lang.Throwable -> L42
            if (r8 != 0) goto L6a
            goto Lad
        L6a:
            r11 = r8
            r8 = r5
            r5 = r11
            goto L31
        L6e:
            boolean r5 = r4 instanceof j$.util.concurrent.q     // Catch: java.lang.Throwable -> L42
            if (r5 == 0) goto L9f
            r5 = r4
            j$.util.concurrent.q r5 = (j$.util.concurrent.q) r5     // Catch: java.lang.Throwable -> L42
            j$.util.concurrent.r r8 = r5.f4181e     // Catch: java.lang.Throwable -> L42
            if (r8 == 0) goto Lad
            j$.util.concurrent.r r8 = r8.b(r0, r13, r2)     // Catch: java.lang.Throwable -> L42
            if (r8 == 0) goto Lad
            java.lang.Object r9 = r8.f4166c     // Catch: java.lang.Throwable -> L42
            if (r15 == 0) goto L8d
            if (r15 == r9) goto L8d
            if (r9 == 0) goto Lad
            boolean r10 = r15.equals(r9)     // Catch: java.lang.Throwable -> L42
            if (r10 == 0) goto Lad
        L8d:
            if (r14 == 0) goto L92
            r8.f4166c = r14     // Catch: java.lang.Throwable -> L42
            goto Lae
        L92:
            boolean r8 = r5.f(r8)     // Catch: java.lang.Throwable -> L42
            if (r8 == 0) goto Lae
            j$.util.concurrent.r r5 = r5.f     // Catch: java.lang.Throwable -> L42
            j$.util.concurrent.l r5 = p(r5)     // Catch: java.lang.Throwable -> L42
            goto L61
        L9f:
            boolean r3 = r4 instanceof j$.util.concurrent.m     // Catch: java.lang.Throwable -> L42
            if (r3 != 0) goto La4
            goto Lac
        La4:
            java.lang.IllegalStateException r13 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L42
            java.lang.String r14 = "Recursive update"
            r13.<init>(r14)     // Catch: java.lang.Throwable -> L42
            throw r13     // Catch: java.lang.Throwable -> L42
        Lac:
            r7 = 0
        Lad:
            r9 = r2
        Lae:
            monitor-exit(r4)     // Catch: java.lang.Throwable -> L42
            if (r7 == 0) goto La
            if (r9 == 0) goto Lbd
            if (r14 != 0) goto Lba
            r13 = -1
            r12.a(r13, r6)
        Lba:
            return r9
        Lbb:
            monitor-exit(r4)     // Catch: java.lang.Throwable -> L42
            throw r13
        Lbd:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.g(java.lang.Object, java.lang.Object, java.lang.Object):java.lang.Object");
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x004d, code lost:
        return (V) r1.f4166c;
     */
    @Override // java.util.AbstractMap, java.util.Map
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public V get(java.lang.Object r5) {
        /*
            r4 = this;
            int r0 = r5.hashCode()
            int r0 = i(r0)
            j$.util.concurrent.l[] r1 = r4.f4141a
            r2 = 0
            if (r1 == 0) goto L4e
            int r3 = r1.length
            if (r3 <= 0) goto L4e
            int r3 = r3 + (-1)
            r3 = r3 & r0
            j$.util.concurrent.l r1 = k(r1, r3)
            if (r1 == 0) goto L4e
            int r3 = r1.f4164a
            if (r3 != r0) goto L2c
            java.lang.Object r3 = r1.f4165b
            if (r3 == r5) goto L29
            if (r3 == 0) goto L37
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L37
        L29:
            java.lang.Object r5 = r1.f4166c
            return r5
        L2c:
            if (r3 >= 0) goto L37
            j$.util.concurrent.l r5 = r1.a(r5, r0)
            if (r5 == 0) goto L36
            java.lang.Object r2 = r5.f4166c
        L36:
            return r2
        L37:
            j$.util.concurrent.l r1 = r1.f4167d
            if (r1 == 0) goto L4e
            int r3 = r1.f4164a
            if (r3 != r0) goto L37
            java.lang.Object r3 = r1.f4165b
            if (r3 == r5) goto L4b
            if (r3 == 0) goto L37
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L37
        L4b:
            java.lang.Object r5 = r1.f4166c
            return r5
        L4e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.get(java.lang.Object):java.lang.Object");
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public final Object getOrDefault(Object obj, Object obj2) {
        V v4 = get(obj);
        return v4 == null ? obj2 : v4;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final int hashCode() {
        l[] lVarArr = this.f4141a;
        int i4 = 0;
        if (lVarArr != null) {
            p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
            while (true) {
                l a4 = pVar.a();
                if (a4 == null) {
                    break;
                }
                i4 += a4.f4166c.hashCode() ^ a4.f4165b.hashCode();
            }
        }
        return i4;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean isEmpty() {
        return j() <= 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final long j() {
        c[] cVarArr = this.f4143c;
        long j4 = this.baseCount;
        if (cVarArr != null) {
            for (c cVar : cVarArr) {
                if (cVar != null) {
                    j4 += cVar.value;
                }
            }
        }
        return j4;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [j$.util.concurrent.b, java.util.Set<K>, j$.util.concurrent.i] */
    @Override // java.util.AbstractMap, java.util.Map
    public Set<K> keySet() {
        i iVar = this.f4144d;
        if (iVar != null) {
            return iVar;
        }
        ?? r02 = (Set<K>) new b(this);
        this.f4144d = r02;
        return r02;
    }

    /* JADX WARN: Code restructure failed: missing block: B:69:0x00dd, code lost:
        throw new java.lang.IllegalStateException("Recursive update");
     */
    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object merge(java.lang.Object r18, java.lang.Object r19, java.util.function.BiFunction r20) {
        /*
            Method dump skipped, instructions count: 250
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.ConcurrentHashMap.merge(java.lang.Object, java.lang.Object, java.util.function.BiFunction):java.lang.Object");
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K k4, V v4) {
        return (V) f(k4, v4, false);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        o(map.size());
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            f(entry.getKey(), entry.getValue(), false);
        }
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public V putIfAbsent(K k4, V v4) {
        return (V) f(k4, v4, true);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        return (V) g(obj, null, null);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public boolean remove(Object obj, Object obj2) {
        obj.getClass();
        return (obj2 == null || g(obj, null, obj2) == null) ? false : true;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public final Object replace(Object obj, Object obj2) {
        if (obj == null || obj2 == null) {
            throw null;
        }
        return g(obj, obj2, null);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public final boolean replace(Object obj, Object obj2, Object obj3) {
        if (obj == null || obj2 == null || obj3 == null) {
            throw null;
        }
        return g(obj, obj3, obj2) != null;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap, j$.util.Map
    public final void replaceAll(BiFunction biFunction) {
        biFunction.getClass();
        l[] lVarArr = this.f4141a;
        if (lVarArr == null) {
            return;
        }
        p pVar = new p(lVarArr, lVarArr.length, 0, lVarArr.length);
        while (true) {
            l a4 = pVar.a();
            if (a4 == null) {
                return;
            }
            Object obj = a4.f4166c;
            Object obj2 = a4.f4165b;
            do {
                Object apply = biFunction.apply(obj2, obj);
                apply.getClass();
                if (g(obj2, apply, obj) == null) {
                    obj = get(obj2);
                }
            } while (obj != null);
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        long j4 = j();
        if (j4 < 0) {
            return 0;
        }
        if (j4 > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) j4;
    }

    @Override // java.util.AbstractMap
    public final String toString() {
        l[] lVarArr = this.f4141a;
        int length = lVarArr == null ? 0 : lVarArr.length;
        p pVar = new p(lVarArr, length, 0, length);
        StringBuilder sb = new StringBuilder("{");
        l a4 = pVar.a();
        if (a4 != null) {
            while (true) {
                Object obj = a4.f4165b;
                Object obj2 = a4.f4166c;
                if (obj == this) {
                    obj = "(this Map)";
                }
                sb.append(obj);
                sb.append('=');
                if (obj2 == this) {
                    obj2 = "(this Map)";
                }
                sb.append(obj2);
                a4 = pVar.a();
                if (a4 == null) {
                    break;
                }
                sb.append(", ");
            }
        }
        sb.append('}');
        return sb.toString();
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [java.util.Collection<V>, j$.util.concurrent.b, j$.util.concurrent.s] */
    @Override // java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        s sVar = this.f4145e;
        if (sVar != null) {
            return sVar;
        }
        b bVar = new b(this);
        this.f4145e = bVar;
        return bVar;
    }
}
