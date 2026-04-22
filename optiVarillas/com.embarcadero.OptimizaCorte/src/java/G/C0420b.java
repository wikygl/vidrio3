package g;

import F.a;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.SparseArray;
import g.d;

/* renamed from: g.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class C0420b extends Drawable implements Drawable.Callback {

    /* renamed from: v  reason: collision with root package name */
    public static final /* synthetic */ int f3424v = 0;

    /* renamed from: j  reason: collision with root package name */
    public c f3425j;

    /* renamed from: k  reason: collision with root package name */
    public Rect f3426k;

    /* renamed from: l  reason: collision with root package name */
    public Drawable f3427l;

    /* renamed from: m  reason: collision with root package name */
    public Drawable f3428m;

    /* renamed from: o  reason: collision with root package name */
    public boolean f3430o;

    /* renamed from: q  reason: collision with root package name */
    public boolean f3432q;

    /* renamed from: r  reason: collision with root package name */
    public a f3433r;

    /* renamed from: s  reason: collision with root package name */
    public long f3434s;

    /* renamed from: t  reason: collision with root package name */
    public long f3435t;

    /* renamed from: u  reason: collision with root package name */
    public C0048b f3436u;

    /* renamed from: n  reason: collision with root package name */
    public int f3429n = 255;

    /* renamed from: p  reason: collision with root package name */
    public int f3431p = -1;

    /* renamed from: g.b$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class a implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public final /* synthetic */ C0420b f3437j;

        public a(d dVar) {
            this.f3437j = dVar;
        }

        @Override // java.lang.Runnable
        public final void run() {
            C0420b c0420b = this.f3437j;
            c0420b.a(true);
            c0420b.invalidateSelf();
        }
    }

    /* renamed from: g.b$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static abstract class c extends Drawable.ConstantState {

        /* renamed from: A  reason: collision with root package name */
        public boolean f3439A;

        /* renamed from: B  reason: collision with root package name */
        public ColorFilter f3440B;

        /* renamed from: C  reason: collision with root package name */
        public boolean f3441C;

        /* renamed from: D  reason: collision with root package name */
        public ColorStateList f3442D;

        /* renamed from: E  reason: collision with root package name */
        public PorterDuff.Mode f3443E;

        /* renamed from: F  reason: collision with root package name */
        public boolean f3444F;

        /* renamed from: G  reason: collision with root package name */
        public boolean f3445G;

        /* renamed from: a  reason: collision with root package name */
        public final C0420b f3446a;

        /* renamed from: b  reason: collision with root package name */
        public Resources f3447b;

        /* renamed from: c  reason: collision with root package name */
        public int f3448c;

        /* renamed from: d  reason: collision with root package name */
        public int f3449d;

        /* renamed from: e  reason: collision with root package name */
        public int f3450e;
        public SparseArray<Drawable.ConstantState> f;

        /* renamed from: g  reason: collision with root package name */
        public Drawable[] f3451g;

        /* renamed from: h  reason: collision with root package name */
        public int f3452h;

        /* renamed from: i  reason: collision with root package name */
        public boolean f3453i;

        /* renamed from: j  reason: collision with root package name */
        public boolean f3454j;

        /* renamed from: k  reason: collision with root package name */
        public Rect f3455k;

        /* renamed from: l  reason: collision with root package name */
        public boolean f3456l;

        /* renamed from: m  reason: collision with root package name */
        public boolean f3457m;

        /* renamed from: n  reason: collision with root package name */
        public int f3458n;

        /* renamed from: o  reason: collision with root package name */
        public int f3459o;

        /* renamed from: p  reason: collision with root package name */
        public int f3460p;

        /* renamed from: q  reason: collision with root package name */
        public int f3461q;

        /* renamed from: r  reason: collision with root package name */
        public boolean f3462r;

        /* renamed from: s  reason: collision with root package name */
        public int f3463s;

        /* renamed from: t  reason: collision with root package name */
        public boolean f3464t;

        /* renamed from: u  reason: collision with root package name */
        public boolean f3465u;

        /* renamed from: v  reason: collision with root package name */
        public boolean f3466v;

        /* renamed from: w  reason: collision with root package name */
        public boolean f3467w;

        /* renamed from: x  reason: collision with root package name */
        public int f3468x;

        /* renamed from: y  reason: collision with root package name */
        public int f3469y;

        /* renamed from: z  reason: collision with root package name */
        public int f3470z;

        public c(c cVar, C0420b c0420b, Resources resources) {
            Resources resources2;
            int i4;
            this.f3453i = false;
            this.f3456l = false;
            this.f3467w = true;
            this.f3469y = 0;
            this.f3470z = 0;
            this.f3446a = c0420b;
            if (resources != null) {
                resources2 = resources;
            } else if (cVar != null) {
                resources2 = cVar.f3447b;
            } else {
                resources2 = null;
            }
            this.f3447b = resources2;
            if (cVar != null) {
                i4 = cVar.f3448c;
            } else {
                i4 = 0;
            }
            int i5 = C0420b.f3424v;
            i4 = resources != null ? resources.getDisplayMetrics().densityDpi : i4;
            i4 = i4 == 0 ? 160 : i4;
            this.f3448c = i4;
            if (cVar != null) {
                this.f3449d = cVar.f3449d;
                this.f3450e = cVar.f3450e;
                this.f3465u = true;
                this.f3466v = true;
                this.f3453i = cVar.f3453i;
                this.f3456l = cVar.f3456l;
                this.f3467w = cVar.f3467w;
                this.f3468x = cVar.f3468x;
                this.f3469y = cVar.f3469y;
                this.f3470z = cVar.f3470z;
                this.f3439A = cVar.f3439A;
                this.f3440B = cVar.f3440B;
                this.f3441C = cVar.f3441C;
                this.f3442D = cVar.f3442D;
                this.f3443E = cVar.f3443E;
                this.f3444F = cVar.f3444F;
                this.f3445G = cVar.f3445G;
                if (cVar.f3448c == i4) {
                    if (cVar.f3454j) {
                        this.f3455k = cVar.f3455k != null ? new Rect(cVar.f3455k) : null;
                        this.f3454j = true;
                    }
                    if (cVar.f3457m) {
                        this.f3458n = cVar.f3458n;
                        this.f3459o = cVar.f3459o;
                        this.f3460p = cVar.f3460p;
                        this.f3461q = cVar.f3461q;
                        this.f3457m = true;
                    }
                }
                if (cVar.f3462r) {
                    this.f3463s = cVar.f3463s;
                    this.f3462r = true;
                }
                if (cVar.f3464t) {
                    this.f3464t = true;
                }
                Drawable[] drawableArr = cVar.f3451g;
                this.f3451g = new Drawable[drawableArr.length];
                this.f3452h = cVar.f3452h;
                SparseArray<Drawable.ConstantState> sparseArray = cVar.f;
                if (sparseArray != null) {
                    this.f = sparseArray.clone();
                } else {
                    this.f = new SparseArray<>(this.f3452h);
                }
                int i6 = this.f3452h;
                for (int i7 = 0; i7 < i6; i7++) {
                    Drawable drawable = drawableArr[i7];
                    if (drawable != null) {
                        Drawable.ConstantState constantState = drawable.getConstantState();
                        if (constantState != null) {
                            this.f.put(i7, constantState);
                        } else {
                            this.f3451g[i7] = drawableArr[i7];
                        }
                    }
                }
                return;
            }
            this.f3451g = new Drawable[10];
            this.f3452h = 0;
        }

        public final int a(Drawable drawable) {
            int i4 = this.f3452h;
            if (i4 >= this.f3451g.length) {
                int i5 = i4 + 10;
                d.a aVar = (d.a) this;
                Drawable[] drawableArr = new Drawable[i5];
                Drawable[] drawableArr2 = aVar.f3451g;
                if (drawableArr2 != null) {
                    System.arraycopy(drawableArr2, 0, drawableArr, 0, i4);
                }
                aVar.f3451g = drawableArr;
                int[][] iArr = new int[i5];
                System.arraycopy(aVar.f3474H, 0, iArr, 0, i4);
                aVar.f3474H = iArr;
            }
            drawable.mutate();
            drawable.setVisible(false, true);
            drawable.setCallback(this.f3446a);
            this.f3451g[i4] = drawable;
            this.f3452h++;
            this.f3450e = drawable.getChangingConfigurations() | this.f3450e;
            this.f3462r = false;
            this.f3464t = false;
            this.f3455k = null;
            this.f3454j = false;
            this.f3457m = false;
            this.f3465u = false;
            return i4;
        }

        public final void b() {
            this.f3457m = true;
            c();
            int i4 = this.f3452h;
            Drawable[] drawableArr = this.f3451g;
            this.f3459o = -1;
            this.f3458n = -1;
            this.f3461q = 0;
            this.f3460p = 0;
            for (int i5 = 0; i5 < i4; i5++) {
                Drawable drawable = drawableArr[i5];
                int intrinsicWidth = drawable.getIntrinsicWidth();
                if (intrinsicWidth > this.f3458n) {
                    this.f3458n = intrinsicWidth;
                }
                int intrinsicHeight = drawable.getIntrinsicHeight();
                if (intrinsicHeight > this.f3459o) {
                    this.f3459o = intrinsicHeight;
                }
                int minimumWidth = drawable.getMinimumWidth();
                if (minimumWidth > this.f3460p) {
                    this.f3460p = minimumWidth;
                }
                int minimumHeight = drawable.getMinimumHeight();
                if (minimumHeight > this.f3461q) {
                    this.f3461q = minimumHeight;
                }
            }
        }

        public final void c() {
            SparseArray<Drawable.ConstantState> sparseArray = this.f;
            if (sparseArray != null) {
                int size = sparseArray.size();
                for (int i4 = 0; i4 < size; i4++) {
                    int keyAt = this.f.keyAt(i4);
                    Drawable[] drawableArr = this.f3451g;
                    Drawable newDrawable = this.f.valueAt(i4).newDrawable(this.f3447b);
                    if (Build.VERSION.SDK_INT >= 23) {
                        F.a.c(newDrawable, this.f3468x);
                    }
                    Drawable mutate = newDrawable.mutate();
                    mutate.setCallback(this.f3446a);
                    drawableArr[keyAt] = mutate;
                }
                this.f = null;
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final boolean canApplyTheme() {
            int i4 = this.f3452h;
            Drawable[] drawableArr = this.f3451g;
            for (int i5 = 0; i5 < i4; i5++) {
                Drawable drawable = drawableArr[i5];
                if (drawable != null) {
                    if (a.C0006a.b(drawable)) {
                        return true;
                    }
                } else {
                    Drawable.ConstantState constantState = this.f.get(i5);
                    if (constantState != null && constantState.canApplyTheme()) {
                        return true;
                    }
                }
            }
            return false;
        }

        public final Drawable d(int i4) {
            int indexOfKey;
            Drawable drawable = this.f3451g[i4];
            if (drawable != null) {
                return drawable;
            }
            SparseArray<Drawable.ConstantState> sparseArray = this.f;
            if (sparseArray == null || (indexOfKey = sparseArray.indexOfKey(i4)) < 0) {
                return null;
            }
            Drawable newDrawable = this.f.valueAt(indexOfKey).newDrawable(this.f3447b);
            if (Build.VERSION.SDK_INT >= 23) {
                F.a.c(newDrawable, this.f3468x);
            }
            Drawable mutate = newDrawable.mutate();
            mutate.setCallback(this.f3446a);
            this.f3451g[i4] = mutate;
            this.f.removeAt(indexOfKey);
            if (this.f.size() == 0) {
                this.f = null;
            }
            return mutate;
        }

        public abstract void e();

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return this.f3449d | this.f3450e;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x003f  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0065  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x006a A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:26:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void a(boolean r14) {
        /*
            r13 = this;
            r0 = 1
            r13.f3430o = r0
            long r1 = android.os.SystemClock.uptimeMillis()
            android.graphics.drawable.Drawable r3 = r13.f3427l
            r4 = 255(0xff, double:1.26E-321)
            r6 = 0
            r8 = 0
            if (r3 == 0) goto L38
            long r9 = r13.f3434s
            int r11 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r11 == 0) goto L3a
            int r11 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r11 > 0) goto L22
            int r9 = r13.f3429n
            r3.setAlpha(r9)
            r13.f3434s = r6
            goto L3a
        L22:
            long r9 = r9 - r1
            long r9 = r9 * r4
            int r10 = (int) r9
            g.b$c r9 = r13.f3425j
            int r9 = r9.f3469y
            int r10 = r10 / r9
            int r9 = 255 - r10
            int r10 = r13.f3429n
            int r9 = r9 * r10
            int r9 = r9 / 255
            r3.setAlpha(r9)
            r3 = 1
            goto L3b
        L38:
            r13.f3434s = r6
        L3a:
            r3 = 0
        L3b:
            android.graphics.drawable.Drawable r9 = r13.f3428m
            if (r9 == 0) goto L65
            long r10 = r13.f3435t
            int r12 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r12 == 0) goto L67
            int r12 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r12 > 0) goto L52
            r9.setVisible(r8, r8)
            r0 = 0
            r13.f3428m = r0
            r13.f3435t = r6
            goto L67
        L52:
            long r10 = r10 - r1
            long r10 = r10 * r4
            int r3 = (int) r10
            g.b$c r4 = r13.f3425j
            int r4 = r4.f3470z
            int r3 = r3 / r4
            int r4 = r13.f3429n
            int r3 = r3 * r4
            int r3 = r3 / 255
            r9.setAlpha(r3)
            goto L68
        L65:
            r13.f3435t = r6
        L67:
            r0 = r3
        L68:
            if (r14 == 0) goto L74
            if (r0 == 0) goto L74
            g.b$a r14 = r13.f3433r
            r3 = 16
            long r1 = r1 + r3
            r13.scheduleSelf(r14, r1)
        L74:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: g.C0420b.a(boolean):void");
    }

    @Override // android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme theme) {
        c cVar = this.f3425j;
        if (theme != null) {
            cVar.c();
            int i4 = cVar.f3452h;
            Drawable[] drawableArr = cVar.f3451g;
            for (int i5 = 0; i5 < i4; i5++) {
                Drawable drawable = drawableArr[i5];
                if (drawable != null && a.C0006a.b(drawable)) {
                    a.C0006a.a(drawableArr[i5], theme);
                    cVar.f3450e |= drawableArr[i5].getChangingConfigurations();
                }
            }
            Resources resources = theme.getResources();
            if (resources != null) {
                cVar.f3447b = resources;
                int i6 = resources.getDisplayMetrics().densityDpi;
                if (i6 == 0) {
                    i6 = 160;
                }
                int i7 = cVar.f3448c;
                cVar.f3448c = i6;
                if (i7 != i6) {
                    cVar.f3457m = false;
                    cVar.f3454j = false;
                    return;
                }
                return;
            }
            return;
        }
        cVar.getClass();
    }

    public c b() {
        throw null;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [g.b$b, java.lang.Object] */
    public final void c(Drawable drawable) {
        if (this.f3436u == null) {
            this.f3436u = new Object();
        }
        C0048b c0048b = this.f3436u;
        c0048b.f3438j = drawable.getCallback();
        drawable.setCallback(c0048b);
        try {
            if (this.f3425j.f3469y <= 0 && this.f3430o) {
                drawable.setAlpha(this.f3429n);
            }
            c cVar = this.f3425j;
            if (cVar.f3441C) {
                drawable.setColorFilter(cVar.f3440B);
            } else {
                if (cVar.f3444F) {
                    a.C0006a.h(drawable, cVar.f3442D);
                }
                c cVar2 = this.f3425j;
                if (cVar2.f3445G) {
                    a.C0006a.i(drawable, cVar2.f3443E);
                }
            }
            drawable.setVisible(isVisible(), true);
            drawable.setDither(this.f3425j.f3467w);
            drawable.setState(getState());
            drawable.setLevel(getLevel());
            drawable.setBounds(getBounds());
            if (Build.VERSION.SDK_INT >= 23) {
                F.a.c(drawable, F.a.b(this));
            }
            drawable.setAutoMirrored(this.f3425j.f3439A);
            Rect rect = this.f3426k;
            if (rect != null) {
                a.C0006a.f(drawable, rect.left, rect.top, rect.right, rect.bottom);
            }
            C0048b c0048b2 = this.f3436u;
            Drawable.Callback callback = c0048b2.f3438j;
            c0048b2.f3438j = null;
            drawable.setCallback(callback);
        } catch (Throwable th) {
            C0048b c0048b3 = this.f3436u;
            Drawable.Callback callback2 = c0048b3.f3438j;
            c0048b3.f3438j = null;
            drawable.setCallback(callback2);
            throw th;
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean canApplyTheme() {
        return this.f3425j.canApplyTheme();
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x006b  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0076  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean d(int r10) {
        /*
            r9 = this;
            int r0 = r9.f3431p
            r1 = 0
            if (r10 != r0) goto L6
            return r1
        L6:
            long r2 = android.os.SystemClock.uptimeMillis()
            g.b$c r0 = r9.f3425j
            int r0 = r0.f3470z
            r4 = 0
            r5 = 0
            if (r0 <= 0) goto L2e
            android.graphics.drawable.Drawable r0 = r9.f3428m
            if (r0 == 0) goto L1a
            r0.setVisible(r1, r1)
        L1a:
            android.graphics.drawable.Drawable r0 = r9.f3427l
            if (r0 == 0) goto L29
            r9.f3428m = r0
            g.b$c r0 = r9.f3425j
            int r0 = r0.f3470z
            long r0 = (long) r0
            long r0 = r0 + r2
            r9.f3435t = r0
            goto L35
        L29:
            r9.f3428m = r4
            r9.f3435t = r5
            goto L35
        L2e:
            android.graphics.drawable.Drawable r0 = r9.f3427l
            if (r0 == 0) goto L35
            r0.setVisible(r1, r1)
        L35:
            if (r10 < 0) goto L55
            g.b$c r0 = r9.f3425j
            int r1 = r0.f3452h
            if (r10 >= r1) goto L55
            android.graphics.drawable.Drawable r0 = r0.d(r10)
            r9.f3427l = r0
            r9.f3431p = r10
            if (r0 == 0) goto L5a
            g.b$c r10 = r9.f3425j
            int r10 = r10.f3469y
            if (r10 <= 0) goto L51
            long r7 = (long) r10
            long r2 = r2 + r7
            r9.f3434s = r2
        L51:
            r9.c(r0)
            goto L5a
        L55:
            r9.f3427l = r4
            r10 = -1
            r9.f3431p = r10
        L5a:
            long r0 = r9.f3434s
            r10 = 1
            int r2 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r2 != 0) goto L67
            long r0 = r9.f3435t
            int r2 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r2 == 0) goto L7c
        L67:
            g.b$a r0 = r9.f3433r
            if (r0 != 0) goto L76
            g.b$a r0 = new g.b$a
            r1 = r9
            g.d r1 = (g.d) r1
            r0.<init>(r1)
            r9.f3433r = r0
            goto L79
        L76:
            r9.unscheduleSelf(r0)
        L79:
            r9.a(r10)
        L7c:
            r9.invalidateSelf()
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: g.C0420b.d(int):boolean");
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas canvas) {
        Drawable drawable = this.f3427l;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        Drawable drawable2 = this.f3428m;
        if (drawable2 != null) {
            drawable2.draw(canvas);
        }
    }

    public void e(c cVar) {
        throw null;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getAlpha() {
        return this.f3429n;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.f3425j.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable.ConstantState getConstantState() {
        boolean z4;
        c cVar = this.f3425j;
        if (cVar.f3465u) {
            z4 = cVar.f3466v;
        } else {
            cVar.c();
            cVar.f3465u = true;
            int i4 = cVar.f3452h;
            Drawable[] drawableArr = cVar.f3451g;
            int i5 = 0;
            while (true) {
                if (i5 < i4) {
                    if (drawableArr[i5].getConstantState() == null) {
                        cVar.f3466v = false;
                        z4 = false;
                        break;
                    }
                    i5++;
                } else {
                    cVar.f3466v = true;
                    z4 = true;
                    break;
                }
            }
        }
        if (z4) {
            this.f3425j.f3449d = getChangingConfigurations();
            return this.f3425j;
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable getCurrent() {
        return this.f3427l;
    }

    @Override // android.graphics.drawable.Drawable
    public final void getHotspotBounds(Rect rect) {
        Rect rect2 = this.f3426k;
        if (rect2 != null) {
            rect.set(rect2);
        } else {
            super.getHotspotBounds(rect);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicHeight() {
        c cVar = this.f3425j;
        if (cVar.f3456l) {
            if (!cVar.f3457m) {
                cVar.b();
            }
            return cVar.f3459o;
        }
        Drawable drawable = this.f3427l;
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return -1;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicWidth() {
        c cVar = this.f3425j;
        if (cVar.f3456l) {
            if (!cVar.f3457m) {
                cVar.b();
            }
            return cVar.f3458n;
        }
        Drawable drawable = this.f3427l;
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return -1;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getMinimumHeight() {
        c cVar = this.f3425j;
        if (cVar.f3456l) {
            if (!cVar.f3457m) {
                cVar.b();
            }
            return cVar.f3461q;
        }
        Drawable drawable = this.f3427l;
        if (drawable != null) {
            return drawable.getMinimumHeight();
        }
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getMinimumWidth() {
        c cVar = this.f3425j;
        if (cVar.f3456l) {
            if (!cVar.f3457m) {
                cVar.b();
            }
            return cVar.f3460p;
        }
        Drawable drawable = this.f3427l;
        if (drawable != null) {
            return drawable.getMinimumWidth();
        }
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        Drawable drawable = this.f3427l;
        int i4 = -2;
        if (drawable == null || !drawable.isVisible()) {
            return -2;
        }
        c cVar = this.f3425j;
        if (cVar.f3462r) {
            return cVar.f3463s;
        }
        cVar.c();
        int i5 = cVar.f3452h;
        Drawable[] drawableArr = cVar.f3451g;
        if (i5 > 0) {
            i4 = drawableArr[0].getOpacity();
        }
        for (int i6 = 1; i6 < i5; i6++) {
            i4 = Drawable.resolveOpacity(i4, drawableArr[i6].getOpacity());
        }
        cVar.f3463s = i4;
        cVar.f3462r = true;
        return i4;
    }

    @Override // android.graphics.drawable.Drawable
    public final void getOutline(Outline outline) {
        Drawable drawable = this.f3427l;
        if (drawable != null) {
            drawable.getOutline(outline);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean getPadding(Rect rect) {
        c cVar = this.f3425j;
        boolean z4 = false;
        Rect rect2 = null;
        if (!cVar.f3453i) {
            Rect rect3 = cVar.f3455k;
            if (rect3 == null && !cVar.f3454j) {
                cVar.c();
                Rect rect4 = new Rect();
                int i4 = cVar.f3452h;
                Drawable[] drawableArr = cVar.f3451g;
                for (int i5 = 0; i5 < i4; i5++) {
                    if (drawableArr[i5].getPadding(rect4)) {
                        if (rect2 == null) {
                            rect2 = new Rect(0, 0, 0, 0);
                        }
                        int i6 = rect4.left;
                        if (i6 > rect2.left) {
                            rect2.left = i6;
                        }
                        int i7 = rect4.top;
                        if (i7 > rect2.top) {
                            rect2.top = i7;
                        }
                        int i8 = rect4.right;
                        if (i8 > rect2.right) {
                            rect2.right = i8;
                        }
                        int i9 = rect4.bottom;
                        if (i9 > rect2.bottom) {
                            rect2.bottom = i9;
                        }
                    }
                }
                cVar.f3454j = true;
                cVar.f3455k = rect2;
            } else {
                rect2 = rect3;
            }
        }
        if (rect2 != null) {
            rect.set(rect2);
            if ((rect2.left | rect2.top | rect2.bottom | rect2.right) != 0) {
                z4 = true;
            }
        } else {
            Drawable drawable = this.f3427l;
            if (drawable != null) {
                z4 = drawable.getPadding(rect);
            } else {
                z4 = super.getPadding(rect);
            }
        }
        if (this.f3425j.f3439A && F.a.b(this) == 1) {
            int i10 = rect.left;
            rect.left = rect.right;
            rect.right = i10;
        }
        return z4;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public final void invalidateDrawable(Drawable drawable) {
        c cVar = this.f3425j;
        if (cVar != null) {
            cVar.f3462r = false;
            cVar.f3464t = false;
        }
        if (drawable == this.f3427l && getCallback() != null) {
            getCallback().invalidateDrawable(this);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isAutoMirrored() {
        return this.f3425j.f3439A;
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        boolean z4;
        Drawable drawable = this.f3428m;
        boolean z5 = true;
        if (drawable != null) {
            drawable.jumpToCurrentState();
            this.f3428m = null;
            z4 = true;
        } else {
            z4 = false;
        }
        Drawable drawable2 = this.f3427l;
        if (drawable2 != null) {
            drawable2.jumpToCurrentState();
            if (this.f3430o) {
                this.f3427l.setAlpha(this.f3429n);
            }
        }
        if (this.f3435t != 0) {
            this.f3435t = 0L;
            z4 = true;
        }
        if (this.f3434s != 0) {
            this.f3434s = 0L;
        } else {
            z5 = z4;
        }
        if (z5) {
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.f3432q && super.mutate() == this) {
            c b4 = b();
            b4.e();
            e(b4);
            this.f3432q = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public final void onBoundsChange(Rect rect) {
        Drawable drawable = this.f3428m;
        if (drawable != null) {
            drawable.setBounds(rect);
        }
        Drawable drawable2 = this.f3427l;
        if (drawable2 != null) {
            drawable2.setBounds(rect);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean onLayoutDirectionChanged(int i4) {
        boolean z4;
        c cVar = this.f3425j;
        int i5 = this.f3431p;
        int i6 = cVar.f3452h;
        Drawable[] drawableArr = cVar.f3451g;
        boolean z5 = false;
        for (int i7 = 0; i7 < i6; i7++) {
            Drawable drawable = drawableArr[i7];
            if (drawable != null) {
                if (Build.VERSION.SDK_INT >= 23) {
                    z4 = F.a.c(drawable, i4);
                } else {
                    z4 = false;
                }
                if (i7 == i5) {
                    z5 = z4;
                }
            }
        }
        cVar.f3468x = i4;
        return z5;
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean onLevelChange(int i4) {
        Drawable drawable = this.f3428m;
        if (drawable != null) {
            return drawable.setLevel(i4);
        }
        Drawable drawable2 = this.f3427l;
        if (drawable2 != null) {
            return drawable2.setLevel(i4);
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean onStateChange(int[] iArr) {
        Drawable drawable = this.f3428m;
        if (drawable != null) {
            return drawable.setState(iArr);
        }
        Drawable drawable2 = this.f3427l;
        if (drawable2 != null) {
            return drawable2.setState(iArr);
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public final void scheduleDrawable(Drawable drawable, Runnable runnable, long j4) {
        if (drawable == this.f3427l && getCallback() != null) {
            getCallback().scheduleDrawable(this, runnable, j4);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int i4) {
        if (!this.f3430o || this.f3429n != i4) {
            this.f3430o = true;
            this.f3429n = i4;
            Drawable drawable = this.f3427l;
            if (drawable != null) {
                if (this.f3434s == 0) {
                    drawable.setAlpha(i4);
                } else {
                    a(false);
                }
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAutoMirrored(boolean z4) {
        c cVar = this.f3425j;
        if (cVar.f3439A != z4) {
            cVar.f3439A = z4;
            Drawable drawable = this.f3427l;
            if (drawable != null) {
                drawable.setAutoMirrored(z4);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
        c cVar = this.f3425j;
        cVar.f3441C = true;
        if (cVar.f3440B != colorFilter) {
            cVar.f3440B = colorFilter;
            Drawable drawable = this.f3427l;
            if (drawable != null) {
                drawable.setColorFilter(colorFilter);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setDither(boolean z4) {
        c cVar = this.f3425j;
        if (cVar.f3467w != z4) {
            cVar.f3467w = z4;
            Drawable drawable = this.f3427l;
            if (drawable != null) {
                drawable.setDither(z4);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setHotspot(float f, float f4) {
        Drawable drawable = this.f3427l;
        if (drawable != null) {
            a.C0006a.e(drawable, f, f4);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setHotspotBounds(int i4, int i5, int i6, int i7) {
        Rect rect = this.f3426k;
        if (rect == null) {
            this.f3426k = new Rect(i4, i5, i6, i7);
        } else {
            rect.set(i4, i5, i6, i7);
        }
        Drawable drawable = this.f3427l;
        if (drawable != null) {
            a.C0006a.f(drawable, i4, i5, i6, i7);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTint(int i4) {
        setTintList(ColorStateList.valueOf(i4));
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintList(ColorStateList colorStateList) {
        c cVar = this.f3425j;
        cVar.f3444F = true;
        if (cVar.f3442D != colorStateList) {
            cVar.f3442D = colorStateList;
            F.a.e(this.f3427l, colorStateList);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintMode(PorterDuff.Mode mode) {
        c cVar = this.f3425j;
        cVar.f3445G = true;
        if (cVar.f3443E != mode) {
            cVar.f3443E = mode;
            F.a.f(this.f3427l, mode);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean z4, boolean z5) {
        boolean visible = super.setVisible(z4, z5);
        Drawable drawable = this.f3428m;
        if (drawable != null) {
            drawable.setVisible(z4, z5);
        }
        Drawable drawable2 = this.f3427l;
        if (drawable2 != null) {
            drawable2.setVisible(z4, z5);
        }
        return visible;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public final void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        if (drawable == this.f3427l && getCallback() != null) {
            getCallback().unscheduleDrawable(this, runnable);
        }
    }

    /* renamed from: g.b$b  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class C0048b implements Drawable.Callback {

        /* renamed from: j  reason: collision with root package name */
        public Drawable.Callback f3438j;

        @Override // android.graphics.drawable.Drawable.Callback
        public final void scheduleDrawable(Drawable drawable, Runnable runnable, long j4) {
            Drawable.Callback callback = this.f3438j;
            if (callback != null) {
                callback.scheduleDrawable(drawable, runnable, j4);
            }
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public final void unscheduleDrawable(Drawable drawable, Runnable runnable) {
            Drawable.Callback callback = this.f3438j;
            if (callback != null) {
                callback.unscheduleDrawable(drawable, runnable);
            }
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public final void invalidateDrawable(Drawable drawable) {
        }
    }
}
