package V;

import N.l;
import V.a;
import android.graphics.Rect;
import java.util.Comparator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class b {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface a<T> {
    }

    /* renamed from: V.b$b  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class C0030b<T> implements Comparator<T> {

        /* renamed from: j  reason: collision with root package name */
        public final Rect f2497j = new Rect();

        /* renamed from: k  reason: collision with root package name */
        public final Rect f2498k = new Rect();

        /* renamed from: l  reason: collision with root package name */
        public final boolean f2499l;

        /* renamed from: m  reason: collision with root package name */
        public final a<T> f2500m;

        public C0030b(boolean z4, a.C0029a c0029a) {
            this.f2499l = z4;
            this.f2500m = c0029a;
        }

        @Override // java.util.Comparator
        public final int compare(T t3, T t4) {
            a.C0029a c0029a = (a.C0029a) this.f2500m;
            c0029a.getClass();
            Rect rect = this.f2497j;
            ((l) t3).f(rect);
            c0029a.getClass();
            Rect rect2 = this.f2498k;
            ((l) t4).f(rect2);
            int i4 = rect.top;
            int i5 = rect2.top;
            if (i4 < i5) {
                return -1;
            }
            if (i4 > i5) {
                return 1;
            }
            int i6 = rect.left;
            int i7 = rect2.left;
            boolean z4 = this.f2499l;
            if (i6 < i7) {
                if (!z4) {
                    return -1;
                }
                return 1;
            } else if (i6 > i7) {
                if (z4) {
                    return -1;
                }
                return 1;
            } else {
                int i8 = rect.bottom;
                int i9 = rect2.bottom;
                if (i8 < i9) {
                    return -1;
                }
                if (i8 > i9) {
                    return 1;
                }
                int i10 = rect.right;
                int i11 = rect2.right;
                if (i10 < i11) {
                    if (!z4) {
                        return -1;
                    }
                    return 1;
                } else if (i10 > i11) {
                    if (z4) {
                        return -1;
                    }
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0026, code lost:
        if (r10.bottom <= r12.top) goto L13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0033, code lost:
        if (r10.right <= r12.left) goto L13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x003a, code lost:
        if (r10.top >= r12.bottom) goto L13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0041, code lost:
        if (r10.left >= r12.right) goto L13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0043, code lost:
        if (r9 == 17) goto L11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0045, code lost:
        if (r9 != 66) goto L15;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0048, code lost:
        r11 = d(r9, r10, r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x004c, code lost:
        if (r9 == 17) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x004e, code lost:
        if (r9 == 33) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0050, code lost:
        if (r9 == 66) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0052, code lost:
        if (r9 != 130) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0054, code lost:
        r9 = r12.bottom;
        r10 = r10.bottom;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x005f, code lost:
        throw new java.lang.IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0060, code lost:
        r9 = r12.right;
        r10 = r10.right;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0065, code lost:
        r9 = r10.top;
        r10 = r12.top;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x006a, code lost:
        r9 = r10.left;
        r10 = r12.left;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0073, code lost:
        if (r11 >= java.lang.Math.max(1, r9 - r10)) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0075, code lost:
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:?, code lost:
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean a(int r9, android.graphics.Rect r10, android.graphics.Rect r11, android.graphics.Rect r12) {
        /*
            boolean r0 = b(r9, r10, r11)
            boolean r1 = b(r9, r10, r12)
            r2 = 0
            if (r1 != 0) goto L78
            if (r0 != 0) goto Lf
            goto L78
        Lf:
            java.lang.String r0 = "direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}."
            r1 = 130(0x82, float:1.82E-43)
            r3 = 33
            r4 = 66
            r5 = 17
            r6 = 1
            if (r9 == r5) goto L3d
            if (r9 == r3) goto L36
            if (r9 == r4) goto L2f
            if (r9 != r1) goto L29
            int r7 = r10.bottom
            int r8 = r12.top
            if (r7 > r8) goto L77
            goto L43
        L29:
            java.lang.IllegalArgumentException r9 = new java.lang.IllegalArgumentException
            r9.<init>(r0)
            throw r9
        L2f:
            int r7 = r10.right
            int r8 = r12.left
            if (r7 > r8) goto L77
            goto L43
        L36:
            int r7 = r10.top
            int r8 = r12.bottom
            if (r7 < r8) goto L77
            goto L43
        L3d:
            int r7 = r10.left
            int r8 = r12.right
            if (r7 < r8) goto L77
        L43:
            if (r9 == r5) goto L77
            if (r9 != r4) goto L48
            goto L77
        L48:
            int r11 = d(r9, r10, r11)
            if (r9 == r5) goto L6a
            if (r9 == r3) goto L65
            if (r9 == r4) goto L60
            if (r9 != r1) goto L5a
            int r9 = r12.bottom
            int r10 = r10.bottom
        L58:
            int r9 = r9 - r10
            goto L6f
        L5a:
            java.lang.IllegalArgumentException r9 = new java.lang.IllegalArgumentException
            r9.<init>(r0)
            throw r9
        L60:
            int r9 = r12.right
            int r10 = r10.right
            goto L58
        L65:
            int r9 = r10.top
            int r10 = r12.top
            goto L58
        L6a:
            int r9 = r10.left
            int r10 = r12.left
            goto L58
        L6f:
            int r9 = java.lang.Math.max(r6, r9)
            if (r11 >= r9) goto L76
            r2 = 1
        L76:
            return r2
        L77:
            return r6
        L78:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: V.b.a(int, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect):boolean");
    }

    public static boolean b(int i4, Rect rect, Rect rect2) {
        if (i4 != 17) {
            if (i4 != 33) {
                if (i4 != 66) {
                    if (i4 != 130) {
                        throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
                    }
                }
            }
            if (rect2.right < rect.left || rect2.left > rect.right) {
                return false;
            }
            return true;
        }
        if (rect2.bottom < rect.top || rect2.top > rect.bottom) {
            return false;
        }
        return true;
    }

    public static boolean c(int i4, Rect rect, Rect rect2) {
        if (i4 != 17) {
            if (i4 != 33) {
                if (i4 != 66) {
                    if (i4 == 130) {
                        int i5 = rect.top;
                        int i6 = rect2.top;
                        if ((i5 >= i6 && rect.bottom > i6) || rect.bottom >= rect2.bottom) {
                            return false;
                        }
                        return true;
                    }
                    throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
                }
                int i7 = rect.left;
                int i8 = rect2.left;
                if ((i7 >= i8 && rect.right > i8) || rect.right >= rect2.right) {
                    return false;
                }
                return true;
            }
            int i9 = rect.bottom;
            int i10 = rect2.bottom;
            if ((i9 <= i10 && rect.top < i10) || rect.top <= rect2.top) {
                return false;
            }
            return true;
        }
        int i11 = rect.right;
        int i12 = rect2.right;
        if ((i11 <= i12 && rect.left < i12) || rect.left <= rect2.left) {
            return false;
        }
        return true;
    }

    public static int d(int i4, Rect rect, Rect rect2) {
        int i5;
        int i6;
        if (i4 != 17) {
            if (i4 != 33) {
                if (i4 != 66) {
                    if (i4 == 130) {
                        i5 = rect2.top;
                        i6 = rect.bottom;
                    } else {
                        throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
                    }
                } else {
                    i5 = rect2.left;
                    i6 = rect.right;
                }
            } else {
                i5 = rect.top;
                i6 = rect2.bottom;
            }
        } else {
            i5 = rect.left;
            i6 = rect2.right;
        }
        return Math.max(0, i5 - i6);
    }

    public static int e(int i4, Rect rect, Rect rect2) {
        if (i4 != 17) {
            if (i4 != 33) {
                if (i4 != 66) {
                    if (i4 != 130) {
                        throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
                    }
                }
            }
            return Math.abs(((rect.width() / 2) + rect.left) - ((rect2.width() / 2) + rect2.left));
        }
        return Math.abs(((rect.height() / 2) + rect.top) - ((rect2.height() / 2) + rect2.top));
    }
}
