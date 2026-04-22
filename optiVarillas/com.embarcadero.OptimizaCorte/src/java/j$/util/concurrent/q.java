package j$.util.concurrent;

import java.util.concurrent.locks.LockSupport;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class q extends l {

    /* renamed from: h  reason: collision with root package name */
    private static final j$.sun.misc.a f4179h;

    /* renamed from: i  reason: collision with root package name */
    private static final long f4180i;

    /* renamed from: e  reason: collision with root package name */
    r f4181e;
    volatile r f;

    /* renamed from: g  reason: collision with root package name */
    volatile Thread f4182g;
    volatile int lockState;

    static {
        j$.sun.misc.a h4 = j$.sun.misc.a.h();
        f4179h = h4;
        f4180i = h4.j(q.class, "lockState");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public q(r rVar) {
        super(-2, null, null);
        int i4;
        this.f = rVar;
        r rVar2 = null;
        while (rVar != null) {
            r rVar3 = (r) rVar.f4167d;
            rVar.f4184g = null;
            rVar.f = null;
            if (rVar2 == null) {
                rVar.f4183e = null;
                rVar.f4186i = false;
            } else {
                Object obj = rVar.f4165b;
                int i5 = rVar.f4164a;
                r rVar4 = rVar2;
                Class<?> cls = null;
                while (true) {
                    Object obj2 = rVar4.f4165b;
                    int i6 = rVar4.f4164a;
                    if (i6 > i5) {
                        i4 = -1;
                    } else if (i6 < i5) {
                        i4 = 1;
                    } else {
                        if (cls != null || (cls = ConcurrentHashMap.c(obj)) != null) {
                            int i7 = ConcurrentHashMap.f4132g;
                            int compareTo = (obj2 == null || obj2.getClass() != cls) ? 0 : ((Comparable) obj).compareTo(obj2);
                            if (compareTo != 0) {
                                i4 = compareTo;
                            }
                        }
                        i4 = i(obj, obj2);
                    }
                    r rVar5 = i4 <= 0 ? rVar4.f : rVar4.f4184g;
                    if (rVar5 == null) {
                        break;
                    }
                    rVar4 = rVar5;
                }
                rVar.f4183e = rVar4;
                if (i4 <= 0) {
                    rVar4.f = rVar;
                } else {
                    rVar4.f4184g = rVar;
                }
                rVar = c(rVar2, rVar);
            }
            rVar2 = rVar;
            rVar = rVar3;
        }
        this.f4181e = rVar2;
    }

    static r b(r rVar, r rVar2) {
        while (rVar2 != null && rVar2 != rVar) {
            r rVar3 = rVar2.f4183e;
            if (rVar3 == null) {
                rVar2.f4186i = false;
                return rVar2;
            } else if (rVar2.f4186i) {
                rVar2.f4186i = false;
                return rVar;
            } else {
                r rVar4 = rVar3.f;
                if (rVar4 == rVar2) {
                    rVar4 = rVar3.f4184g;
                    if (rVar4 != null && rVar4.f4186i) {
                        rVar4.f4186i = false;
                        rVar3.f4186i = true;
                        rVar = g(rVar, rVar3);
                        rVar3 = rVar2.f4183e;
                        rVar4 = rVar3 == null ? null : rVar3.f4184g;
                    }
                    if (rVar4 == null) {
                        rVar2 = rVar3;
                    } else {
                        r rVar5 = rVar4.f;
                        r rVar6 = rVar4.f4184g;
                        if ((rVar6 != null && rVar6.f4186i) || (rVar5 != null && rVar5.f4186i)) {
                            if (rVar6 == null || !rVar6.f4186i) {
                                if (rVar5 != null) {
                                    rVar5.f4186i = false;
                                }
                                rVar4.f4186i = true;
                                rVar = h(rVar, rVar4);
                                rVar3 = rVar2.f4183e;
                                rVar4 = rVar3 != null ? rVar3.f4184g : null;
                            }
                            if (rVar4 != null) {
                                rVar4.f4186i = rVar3 == null ? false : rVar3.f4186i;
                                r rVar7 = rVar4.f4184g;
                                if (rVar7 != null) {
                                    rVar7.f4186i = false;
                                }
                            }
                            if (rVar3 != null) {
                                rVar3.f4186i = false;
                                rVar = g(rVar, rVar3);
                            }
                            rVar2 = rVar;
                        }
                        rVar4.f4186i = true;
                        rVar2 = rVar3;
                    }
                } else {
                    if (rVar4 != null && rVar4.f4186i) {
                        rVar4.f4186i = false;
                        rVar3.f4186i = true;
                        rVar = h(rVar, rVar3);
                        rVar3 = rVar2.f4183e;
                        rVar4 = rVar3 == null ? null : rVar3.f;
                    }
                    if (rVar4 == null) {
                        rVar2 = rVar3;
                    } else {
                        r rVar8 = rVar4.f;
                        r rVar9 = rVar4.f4184g;
                        if ((rVar8 != null && rVar8.f4186i) || (rVar9 != null && rVar9.f4186i)) {
                            if (rVar8 == null || !rVar8.f4186i) {
                                if (rVar9 != null) {
                                    rVar9.f4186i = false;
                                }
                                rVar4.f4186i = true;
                                rVar = g(rVar, rVar4);
                                rVar3 = rVar2.f4183e;
                                rVar4 = rVar3 != null ? rVar3.f : null;
                            }
                            if (rVar4 != null) {
                                rVar4.f4186i = rVar3 == null ? false : rVar3.f4186i;
                                r rVar10 = rVar4.f;
                                if (rVar10 != null) {
                                    rVar10.f4186i = false;
                                }
                            }
                            if (rVar3 != null) {
                                rVar3.f4186i = false;
                                rVar = h(rVar, rVar3);
                            }
                            rVar2 = rVar;
                        }
                        rVar4.f4186i = true;
                        rVar2 = rVar3;
                    }
                }
            }
        }
        return rVar;
    }

    static r c(r rVar, r rVar2) {
        r rVar3;
        rVar2.f4186i = true;
        while (true) {
            r rVar4 = rVar2.f4183e;
            if (rVar4 == null) {
                rVar2.f4186i = false;
                return rVar2;
            } else if (!rVar4.f4186i || (rVar3 = rVar4.f4183e) == null) {
                break;
            } else {
                r rVar5 = rVar3.f;
                if (rVar4 == rVar5) {
                    rVar5 = rVar3.f4184g;
                    if (rVar5 == null || !rVar5.f4186i) {
                        if (rVar2 == rVar4.f4184g) {
                            rVar = g(rVar, rVar4);
                            r rVar6 = rVar4.f4183e;
                            rVar3 = rVar6 == null ? null : rVar6.f4183e;
                            rVar4 = rVar6;
                            rVar2 = rVar4;
                        }
                        if (rVar4 != null) {
                            rVar4.f4186i = false;
                            if (rVar3 != null) {
                                rVar3.f4186i = true;
                                rVar = h(rVar, rVar3);
                            }
                        }
                    } else {
                        rVar5.f4186i = false;
                        rVar4.f4186i = false;
                        rVar3.f4186i = true;
                        rVar2 = rVar3;
                    }
                } else if (rVar5 == null || !rVar5.f4186i) {
                    if (rVar2 == rVar4.f) {
                        rVar = h(rVar, rVar4);
                        r rVar7 = rVar4.f4183e;
                        rVar3 = rVar7 == null ? null : rVar7.f4183e;
                        rVar4 = rVar7;
                        rVar2 = rVar4;
                    }
                    if (rVar4 != null) {
                        rVar4.f4186i = false;
                        if (rVar3 != null) {
                            rVar3.f4186i = true;
                            rVar = g(rVar, rVar3);
                        }
                    }
                } else {
                    rVar5.f4186i = false;
                    rVar4.f4186i = false;
                    rVar3.f4186i = true;
                    rVar2 = rVar3;
                }
            }
        }
        return rVar;
    }

    private final void d() {
        if (f4179h.c(this, f4180i, 0, 1)) {
            return;
        }
        boolean z4 = false;
        while (true) {
            int i4 = this.lockState;
            if ((i4 & (-3)) == 0) {
                if (f4179h.c(this, f4180i, i4, 1)) {
                    break;
                }
            } else if ((i4 & 2) == 0) {
                if (f4179h.c(this, f4180i, i4, i4 | 2)) {
                    this.f4182g = Thread.currentThread();
                    z4 = true;
                }
            } else if (z4) {
                LockSupport.park(this);
            }
        }
        if (z4) {
            this.f4182g = null;
        }
    }

    static r g(r rVar, r rVar2) {
        r rVar3;
        if (rVar2 != null && (rVar3 = rVar2.f4184g) != null) {
            r rVar4 = rVar3.f;
            rVar2.f4184g = rVar4;
            if (rVar4 != null) {
                rVar4.f4183e = rVar2;
            }
            r rVar5 = rVar2.f4183e;
            rVar3.f4183e = rVar5;
            if (rVar5 == null) {
                rVar3.f4186i = false;
                rVar = rVar3;
            } else if (rVar5.f == rVar2) {
                rVar5.f = rVar3;
            } else {
                rVar5.f4184g = rVar3;
            }
            rVar3.f = rVar2;
            rVar2.f4183e = rVar3;
        }
        return rVar;
    }

    static r h(r rVar, r rVar2) {
        r rVar3;
        if (rVar2 != null && (rVar3 = rVar2.f) != null) {
            r rVar4 = rVar3.f4184g;
            rVar2.f = rVar4;
            if (rVar4 != null) {
                rVar4.f4183e = rVar2;
            }
            r rVar5 = rVar2.f4183e;
            rVar3.f4183e = rVar5;
            if (rVar5 == null) {
                rVar3.f4186i = false;
                rVar = rVar3;
            } else if (rVar5.f4184g == rVar2) {
                rVar5.f4184g = rVar3;
            } else {
                rVar5.f = rVar3;
            }
            rVar3.f4184g = rVar2;
            rVar2.f4183e = rVar3;
        }
        return rVar;
    }

    static int i(Object obj, Object obj2) {
        int compareTo;
        return (obj == null || obj2 == null || (compareTo = obj.getClass().getName().compareTo(obj2.getClass().getName())) == 0) ? System.identityHashCode(obj) <= System.identityHashCode(obj2) ? -1 : 1 : compareTo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.concurrent.l
    public final l a(Object obj, int i4) {
        Object obj2;
        Thread thread;
        Thread thread2;
        r rVar = null;
        if (obj != null) {
            l lVar = this.f;
            while (lVar != null) {
                int i5 = this.lockState;
                if ((i5 & 3) == 0) {
                    j$.sun.misc.a aVar = f4179h;
                    long j4 = f4180i;
                    if (aVar.c(this, j4, i5, i5 + 4)) {
                        try {
                            r rVar2 = this.f4181e;
                            if (rVar2 != null) {
                                rVar = rVar2.b(i4, obj, null);
                            }
                            if (aVar.f(this, j4) == 6 && (thread2 = this.f4182g) != null) {
                                LockSupport.unpark(thread2);
                            }
                            return rVar;
                        } catch (Throwable th) {
                            if (f4179h.f(this, f4180i) == 6 && (thread = this.f4182g) != null) {
                                LockSupport.unpark(thread);
                            }
                            throw th;
                        }
                    }
                } else if (lVar.f4164a == i4 && ((obj2 = lVar.f4165b) == obj || (obj2 != null && obj.equals(obj2)))) {
                    return lVar;
                } else {
                    lVar = lVar.f4167d;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0070, code lost:
        return r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00b3, code lost:
        return null;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final j$.util.concurrent.r e(int r16, java.lang.Object r17, java.lang.Object r18) {
        /*
            Method dump skipped, instructions count: 188
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.q.e(int, java.lang.Object, java.lang.Object):j$.util.concurrent.r");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0091 A[Catch: all -> 0x0052, TryCatch #0 {all -> 0x0052, blocks: (B:22:0x0030, B:26:0x0039, B:29:0x003f, B:31:0x004d, B:41:0x0068, B:43:0x006e, B:44:0x0070, B:59:0x0091, B:66:0x00a2, B:62:0x0099, B:64:0x009d, B:65:0x00a0, B:67:0x00a8, B:71:0x00b1, B:73:0x00b5, B:75:0x00b9, B:77:0x00bd, B:81:0x00c6, B:78:0x00c0, B:80:0x00c4, B:70:0x00ad, B:47:0x007a, B:49:0x007e, B:50:0x0081, B:34:0x0055, B:36:0x005b, B:38:0x005f, B:39:0x0062, B:40:0x0064), top: B:87:0x0030 }] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x00ac  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x00ad A[Catch: all -> 0x0052, TryCatch #0 {all -> 0x0052, blocks: (B:22:0x0030, B:26:0x0039, B:29:0x003f, B:31:0x004d, B:41:0x0068, B:43:0x006e, B:44:0x0070, B:59:0x0091, B:66:0x00a2, B:62:0x0099, B:64:0x009d, B:65:0x00a0, B:67:0x00a8, B:71:0x00b1, B:73:0x00b5, B:75:0x00b9, B:77:0x00bd, B:81:0x00c6, B:78:0x00c0, B:80:0x00c4, B:70:0x00ad, B:47:0x007a, B:49:0x007e, B:50:0x0081, B:34:0x0055, B:36:0x005b, B:38:0x005f, B:39:0x0062, B:40:0x0064), top: B:87:0x0030 }] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x00b5 A[Catch: all -> 0x0052, TryCatch #0 {all -> 0x0052, blocks: (B:22:0x0030, B:26:0x0039, B:29:0x003f, B:31:0x004d, B:41:0x0068, B:43:0x006e, B:44:0x0070, B:59:0x0091, B:66:0x00a2, B:62:0x0099, B:64:0x009d, B:65:0x00a0, B:67:0x00a8, B:71:0x00b1, B:73:0x00b5, B:75:0x00b9, B:77:0x00bd, B:81:0x00c6, B:78:0x00c0, B:80:0x00c4, B:70:0x00ad, B:47:0x007a, B:49:0x007e, B:50:0x0081, B:34:0x0055, B:36:0x005b, B:38:0x005f, B:39:0x0062, B:40:0x0064), top: B:87:0x0030 }] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x00bd A[Catch: all -> 0x0052, TryCatch #0 {all -> 0x0052, blocks: (B:22:0x0030, B:26:0x0039, B:29:0x003f, B:31:0x004d, B:41:0x0068, B:43:0x006e, B:44:0x0070, B:59:0x0091, B:66:0x00a2, B:62:0x0099, B:64:0x009d, B:65:0x00a0, B:67:0x00a8, B:71:0x00b1, B:73:0x00b5, B:75:0x00b9, B:77:0x00bd, B:81:0x00c6, B:78:0x00c0, B:80:0x00c4, B:70:0x00ad, B:47:0x007a, B:49:0x007e, B:50:0x0081, B:34:0x0055, B:36:0x005b, B:38:0x005f, B:39:0x0062, B:40:0x0064), top: B:87:0x0030 }] */
    /* JADX WARN: Removed duplicated region for block: B:78:0x00c0 A[Catch: all -> 0x0052, TryCatch #0 {all -> 0x0052, blocks: (B:22:0x0030, B:26:0x0039, B:29:0x003f, B:31:0x004d, B:41:0x0068, B:43:0x006e, B:44:0x0070, B:59:0x0091, B:66:0x00a2, B:62:0x0099, B:64:0x009d, B:65:0x00a0, B:67:0x00a8, B:71:0x00b1, B:73:0x00b5, B:75:0x00b9, B:77:0x00bd, B:81:0x00c6, B:78:0x00c0, B:80:0x00c4, B:70:0x00ad, B:47:0x007a, B:49:0x007e, B:50:0x0081, B:34:0x0055, B:36:0x005b, B:38:0x005f, B:39:0x0062, B:40:0x0064), top: B:87:0x0030 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean f(j$.util.concurrent.r r11) {
        /*
            Method dump skipped, instructions count: 207
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.q.f(j$.util.concurrent.r):boolean");
    }
}
