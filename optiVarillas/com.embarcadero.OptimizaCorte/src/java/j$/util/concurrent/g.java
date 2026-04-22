package j$.util.concurrent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class g extends l {

    /* renamed from: e  reason: collision with root package name */
    final l[] f4157e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public g(l[] lVarArr) {
        super(-1, null, null);
        this.f4157e = lVarArr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0029, code lost:
        if ((r0 instanceof j$.util.concurrent.g) == false) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x002b, code lost:
        r0 = ((j$.util.concurrent.g) r0).f4157e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0034, code lost:
        return r0.a(r5, r6);
     */
    @Override // j$.util.concurrent.l
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final j$.util.concurrent.l a(java.lang.Object r5, int r6) {
        /*
            r4 = this;
            j$.util.concurrent.l[] r0 = r4.f4157e
        L2:
            r1 = 0
            if (r5 == 0) goto L39
            if (r0 == 0) goto L39
            int r2 = r0.length
            if (r2 == 0) goto L39
            int r2 = r2 + (-1)
            r2 = r2 & r6
            j$.util.concurrent.l r0 = j$.util.concurrent.ConcurrentHashMap.k(r0, r2)
            if (r0 != 0) goto L14
            goto L39
        L14:
            int r2 = r0.f4164a
            if (r2 != r6) goto L25
            java.lang.Object r3 = r0.f4165b
            if (r3 == r5) goto L24
            if (r3 == 0) goto L25
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L25
        L24:
            return r0
        L25:
            if (r2 >= 0) goto L35
            boolean r1 = r0 instanceof j$.util.concurrent.g
            if (r1 == 0) goto L30
            j$.util.concurrent.g r0 = (j$.util.concurrent.g) r0
            j$.util.concurrent.l[] r0 = r0.f4157e
            goto L2
        L30:
            j$.util.concurrent.l r5 = r0.a(r5, r6)
            return r5
        L35:
            j$.util.concurrent.l r0 = r0.f4167d
            if (r0 != 0) goto L14
        L39:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.g.a(java.lang.Object, int):j$.util.concurrent.l");
    }
}
