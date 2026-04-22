package w;

import v.e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class m implements d {

    /* renamed from: a  reason: collision with root package name */
    public int f6364a;

    /* renamed from: b  reason: collision with root package name */
    public v.e f6365b;

    /* renamed from: c  reason: collision with root package name */
    public k f6366c;

    /* renamed from: d  reason: collision with root package name */
    public e.a f6367d;

    /* renamed from: e  reason: collision with root package name */
    public final g f6368e = new g(this);
    public int f = 0;

    /* renamed from: g  reason: collision with root package name */
    public boolean f6369g = false;

    /* renamed from: h  reason: collision with root package name */
    public final f f6370h = new f(this);

    /* renamed from: i  reason: collision with root package name */
    public final f f6371i = new f(this);

    /* renamed from: j  reason: collision with root package name */
    public a f6372j = a.f6373j;

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class a {

        /* renamed from: j  reason: collision with root package name */
        public static final a f6373j;

        /* renamed from: k  reason: collision with root package name */
        public static final a f6374k;

        /* renamed from: l  reason: collision with root package name */
        public static final /* synthetic */ a[] f6375l;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r4v0, types: [w.m$a, java.lang.Enum] */
        /* JADX WARN: Type inference failed for: r7v1, types: [w.m$a, java.lang.Enum] */
        static {
            ?? r4 = new Enum("NONE", 0);
            f6373j = r4;
            Enum r5 = new Enum("START", 1);
            Enum r6 = new Enum("END", 2);
            ?? r7 = new Enum("CENTER", 3);
            f6374k = r7;
            f6375l = new a[]{r4, r5, r6, r7};
        }

        public a() {
            throw null;
        }

        public static a valueOf(String str) {
            return (a) Enum.valueOf(a.class, str);
        }

        public static a[] values() {
            return (a[]) f6375l.clone();
        }
    }

    public m(v.e eVar) {
        this.f6365b = eVar;
    }

    public static void b(f fVar, f fVar2, int i4) {
        fVar.f6348l.add(fVar2);
        fVar.f = i4;
        fVar2.f6347k.add(fVar);
    }

    public static f h(v.d dVar) {
        v.d dVar2 = dVar.f6054d;
        if (dVar2 == null) {
            return null;
        }
        int ordinal = dVar2.f6053c.ordinal();
        v.e eVar = dVar2.f6052b;
        if (ordinal != 1) {
            if (ordinal != 2) {
                if (ordinal != 3) {
                    if (ordinal != 4) {
                        if (ordinal != 5) {
                            return null;
                        }
                        return eVar.f6100e.f6362k;
                    }
                    return eVar.f6100e.f6371i;
                }
                return eVar.f6098d.f6371i;
            }
            return eVar.f6100e.f6370h;
        }
        return eVar.f6098d.f6370h;
    }

    public static f i(v.d dVar, int i4) {
        m mVar;
        v.d dVar2 = dVar.f6054d;
        if (dVar2 == null) {
            return null;
        }
        v.e eVar = dVar2.f6052b;
        if (i4 == 0) {
            mVar = eVar.f6098d;
        } else {
            mVar = eVar.f6100e;
        }
        int ordinal = dVar2.f6053c.ordinal();
        if (ordinal != 1 && ordinal != 2) {
            if (ordinal != 3 && ordinal != 4) {
                return null;
            }
            return mVar.f6371i;
        }
        return mVar.f6370h;
    }

    public final void c(f fVar, f fVar2, int i4, g gVar) {
        fVar.f6348l.add(fVar2);
        fVar.f6348l.add(this.f6368e);
        fVar.f6344h = i4;
        fVar.f6345i = gVar;
        fVar2.f6347k.add(fVar);
        gVar.f6347k.add(fVar);
    }

    public abstract void d();

    public abstract void e();

    public abstract void f();

    public final int g(int i4, int i5) {
        int max;
        if (i5 == 0) {
            v.e eVar = this.f6365b;
            int i6 = eVar.f6108n;
            max = Math.max(eVar.f6107m, i4);
            if (i6 > 0) {
                max = Math.min(i6, i4);
            }
            if (max == i4) {
                return i4;
            }
        } else {
            v.e eVar2 = this.f6365b;
            int i7 = eVar2.f6111q;
            max = Math.max(eVar2.f6110p, i4);
            if (i7 > 0) {
                max = Math.min(i7, i4);
            }
            if (max == i4) {
                return i4;
            }
        }
        return max;
    }

    public long j() {
        g gVar = this.f6368e;
        if (gVar.f6346j) {
            return gVar.f6343g;
        }
        return 0L;
    }

    public abstract boolean k();

    public final void l(v.d dVar, v.d dVar2, int i4) {
        float f;
        m mVar;
        float f4;
        int i5;
        f h4 = h(dVar);
        f h5 = h(dVar2);
        if (h4.f6346j && h5.f6346j) {
            int c4 = dVar.c() + h4.f6343g;
            int c5 = h5.f6343g - dVar2.c();
            int i6 = c5 - c4;
            g gVar = this.f6368e;
            if (!gVar.f6346j) {
                e.a aVar = this.f6367d;
                e.a aVar2 = e.a.f6123l;
                if (aVar == aVar2) {
                    int i7 = this.f6364a;
                    if (i7 != 0) {
                        if (i7 != 1) {
                            if (i7 != 2) {
                                if (i7 == 3) {
                                    v.e eVar = this.f6365b;
                                    m mVar2 = eVar.f6098d;
                                    e.a aVar3 = mVar2.f6367d;
                                    m mVar3 = eVar.f6100e;
                                    if (aVar3 != aVar2 || mVar2.f6364a != 3 || mVar3.f6367d != aVar2 || mVar3.f6364a != 3) {
                                        if (i4 == 0) {
                                            mVar2 = mVar3;
                                        }
                                        g gVar2 = mVar2.f6368e;
                                        if (gVar2.f6346j) {
                                            float f5 = eVar.f6079N;
                                            if (i4 == 1) {
                                                i5 = (int) ((gVar2.f6343g / f5) + 0.5f);
                                            } else {
                                                i5 = (int) ((f5 * gVar2.f6343g) + 0.5f);
                                            }
                                            gVar.d(i5);
                                        }
                                    }
                                }
                            } else {
                                v.e eVar2 = this.f6365b;
                                v.e eVar3 = eVar2.f6076K;
                                if (eVar3 != null) {
                                    if (i4 == 0) {
                                        mVar = eVar3.f6098d;
                                    } else {
                                        mVar = eVar3.f6100e;
                                    }
                                    g gVar3 = mVar.f6368e;
                                    if (gVar3.f6346j) {
                                        if (i4 == 0) {
                                            f4 = eVar2.f6109o;
                                        } else {
                                            f4 = eVar2.f6112r;
                                        }
                                        gVar.d(g((int) ((gVar3.f6343g * f4) + 0.5f), i4));
                                    }
                                }
                            }
                        } else {
                            gVar.d(Math.min(g(gVar.f6358m, i4), i6));
                        }
                    } else {
                        gVar.d(g(i6, i4));
                    }
                }
            }
            if (!gVar.f6346j) {
                return;
            }
            int i8 = gVar.f6343g;
            f fVar = this.f6371i;
            f fVar2 = this.f6370h;
            if (i8 == i6) {
                fVar2.d(c4);
                fVar.d(c5);
                return;
            }
            v.e eVar4 = this.f6365b;
            if (i4 == 0) {
                f = eVar4.f6086U;
            } else {
                f = eVar4.f6087V;
            }
            if (h4 == h5) {
                c4 = h4.f6343g;
                c5 = h5.f6343g;
                f = 0.5f;
            }
            fVar2.d((int) ((((c5 - c4) - i8) * f) + c4 + 0.5f));
            fVar.d(fVar2.f6343g + gVar.f6343g);
        }
    }

    @Override // w.d
    public void a(d dVar) {
    }
}
