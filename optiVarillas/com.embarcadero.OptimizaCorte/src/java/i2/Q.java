package i2;

import e0.C0405a;
import j$.util.Objects;
import java.util.Arrays;
import java.util.Set;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class Q extends M implements Set, j$.util.Set {

    /* renamed from: k  reason: collision with root package name */
    public transient P f3690k;

    public static int o(int i4) {
        int max = Math.max(i4, 2);
        if (max < 751619276) {
            int highestOneBit = Integer.highestOneBit(max - 1);
            do {
                highestOneBit += highestOneBit;
            } while (highestOneBit * 0.7d < max);
            return highestOneBit;
        } else if (max < 1073741824) {
            return 1073741824;
        } else {
            throw new IllegalArgumentException("collection too large");
        }
    }

    public static Q p(int i4, Object... objArr) {
        if (i4 != 0) {
            if (i4 != 1) {
                int o4 = o(i4);
                Object[] objArr2 = new Object[o4];
                int i5 = o4 - 1;
                int i6 = 0;
                int i7 = 0;
                for (int i8 = 0; i8 < i4; i8++) {
                    Object obj = objArr[i8];
                    if (obj != null) {
                        int hashCode = obj.hashCode();
                        int rotateLeft = (int) (Integer.rotateLeft((int) (hashCode * (-862048943)), 15) * 461845907);
                        while (true) {
                            int i9 = rotateLeft & i5;
                            Object obj2 = objArr2[i9];
                            if (obj2 == null) {
                                objArr[i7] = obj;
                                objArr2[i9] = obj;
                                i6 += hashCode;
                                i7++;
                                break;
                            } else if (!obj2.equals(obj)) {
                                rotateLeft++;
                            }
                        }
                    } else {
                        throw new NullPointerException(C0405a.c("at index ", i8));
                    }
                }
                Arrays.fill(objArr, i7, i4, (Object) null);
                if (i7 == 1) {
                    Object obj3 = objArr[0];
                    Objects.requireNonNull(obj3);
                    return new V(obj3);
                }
                if (o(i7) >= o4 / 2) {
                    if (i7 < 3) {
                        objArr = Arrays.copyOf(objArr, i7);
                    }
                    return new U(i6, i5, i7, objArr, objArr2);
                }
                return p(i7, objArr);
            }
            Object obj4 = objArr[0];
            Objects.requireNonNull(obj4);
            return new V(obj4);
        }
        return U.f3697r;
    }

    @Override // java.util.Collection, java.util.Set
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof Q) && (this instanceof U)) {
            Q q4 = (Q) obj;
            q4.getClass();
            if ((q4 instanceof U) && hashCode() != obj.hashCode()) {
                return false;
            }
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Set) {
            Set set = (Set) obj;
            try {
                if (size() == set.size()) {
                    if (containsAll(set)) {
                        return true;
                    }
                }
            } catch (ClassCastException | NullPointerException unused) {
            }
        }
        return false;
    }

    @Override // java.util.Collection, java.util.Set
    public int hashCode() {
        int i4;
        int i5 = 0;
        for (Object obj : this) {
            if (obj != null) {
                i4 = obj.hashCode();
            } else {
                i4 = 0;
            }
            i5 += i4;
        }
        return i5;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    /* renamed from: n */
    public abstract W iterator();
}
