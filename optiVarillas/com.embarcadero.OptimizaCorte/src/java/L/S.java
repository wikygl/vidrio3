package l;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import g.C0419a;
import h.C0429a;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import l.C0701j;
import r.C0778g;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class S {

    /* renamed from: i  reason: collision with root package name */
    public static S f5058i;

    /* renamed from: a  reason: collision with root package name */
    public WeakHashMap<Context, r.k<ColorStateList>> f5060a;

    /* renamed from: b  reason: collision with root package name */
    public r.j<String, e> f5061b;

    /* renamed from: c  reason: collision with root package name */
    public r.k<String> f5062c;

    /* renamed from: d  reason: collision with root package name */
    public final WeakHashMap<Context, C0778g<WeakReference<Drawable.ConstantState>>> f5063d = new WeakHashMap<>(0);

    /* renamed from: e  reason: collision with root package name */
    public TypedValue f5064e;
    public boolean f;

    /* renamed from: g  reason: collision with root package name */
    public f f5065g;

    /* renamed from: h  reason: collision with root package name */
    public static final PorterDuff.Mode f5057h = PorterDuff.Mode.SRC_IN;

    /* renamed from: j  reason: collision with root package name */
    public static final c f5059j = new r.h(6);

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class a implements e {
        @Override // l.S.e
        public final Drawable a(Context context, XmlResourceParser xmlResourceParser, AttributeSet attributeSet, Resources.Theme theme) {
            try {
                return C0419a.g(context, context.getResources(), xmlResourceParser, attributeSet, theme);
            } catch (Exception e4) {
                Log.e("AsldcInflateDelegate", "Exception while inflating <animated-selector>", e4);
                return null;
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class b implements e {
        @Override // l.S.e
        public final Drawable a(Context context, XmlResourceParser xmlResourceParser, AttributeSet attributeSet, Resources.Theme theme) {
            try {
                Resources resources = context.getResources();
                v0.d dVar = new v0.d(context);
                dVar.inflate(resources, xmlResourceParser, attributeSet, theme);
                return dVar;
            } catch (Exception e4) {
                Log.e("AvdcInflateDelegate", "Exception while inflating <animated-vector>", e4);
                return null;
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class c extends r.h<Integer, PorterDuffColorFilter> {
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class d implements e {
        @Override // l.S.e
        public final Drawable a(Context context, XmlResourceParser xmlResourceParser, AttributeSet attributeSet, Resources.Theme theme) {
            String classAttribute = attributeSet.getClassAttribute();
            if (classAttribute != null) {
                try {
                    Drawable drawable = (Drawable) d.class.getClassLoader().loadClass(classAttribute).asSubclass(Drawable.class).getDeclaredConstructor(null).newInstance(null);
                    C0429a.c(drawable, context.getResources(), xmlResourceParser, attributeSet, theme);
                    return drawable;
                } catch (Exception e4) {
                    Log.e("DrawableDelegate", "Exception while inflating <drawable>", e4);
                }
            }
            return null;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public interface e {
        Drawable a(Context context, XmlResourceParser xmlResourceParser, AttributeSet attributeSet, Resources.Theme theme);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public interface f {
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class g implements e {
        @Override // l.S.e
        public final Drawable a(Context context, XmlResourceParser xmlResourceParser, AttributeSet attributeSet, Resources.Theme theme) {
            try {
                Resources resources = context.getResources();
                v0.h hVar = new v0.h();
                hVar.inflate(resources, xmlResourceParser, attributeSet, theme);
                return hVar;
            } catch (Exception e4) {
                Log.e("VdcInflateDelegate", "Exception while inflating <vector>", e4);
                return null;
            }
        }
    }

    public static synchronized S d() {
        S s4;
        synchronized (S.class) {
            try {
                if (f5058i == null) {
                    S s5 = new S();
                    f5058i = s5;
                    j(s5);
                }
                s4 = f5058i;
            } catch (Throwable th) {
                throw th;
            }
        }
        return s4;
    }

    public static synchronized PorterDuffColorFilter h(int i4, PorterDuff.Mode mode) {
        PorterDuffColorFilter a4;
        synchronized (S.class) {
            c cVar = f5059j;
            cVar.getClass();
            int i5 = (31 + i4) * 31;
            a4 = cVar.a(Integer.valueOf(mode.hashCode() + i5));
            if (a4 == null) {
                a4 = new PorterDuffColorFilter(i4, mode);
                cVar.b(Integer.valueOf(mode.hashCode() + i5), a4);
            }
        }
        return a4;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.Object, l.S$e] */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.Object, l.S$e] */
    /* JADX WARN: Type inference failed for: r0v3, types: [java.lang.Object, l.S$e] */
    public static void j(S s4) {
        if (Build.VERSION.SDK_INT < 24) {
            s4.a("vector", new Object());
            s4.a("animated-vector", new Object());
            s4.a("animated-selector", new Object());
            s4.a("drawable", new d());
        }
    }

    public final void a(String str, e eVar) {
        if (this.f5061b == null) {
            this.f5061b = new r.j<>();
        }
        this.f5061b.put(str, eVar);
    }

    public final synchronized void b(Context context, long j4, Drawable drawable) {
        try {
            Drawable.ConstantState constantState = drawable.getConstantState();
            if (constantState != null) {
                C0778g<WeakReference<Drawable.ConstantState>> c0778g = this.f5063d.get(context);
                if (c0778g == null) {
                    c0778g = new C0778g<>();
                    this.f5063d.put(context, c0778g);
                }
                c0778g.g(j4, new WeakReference<>(constantState));
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public final Drawable c(Context context, int i4) {
        if (this.f5064e == null) {
            this.f5064e = new TypedValue();
        }
        TypedValue typedValue = this.f5064e;
        context.getResources().getValue(i4, typedValue, true);
        long j4 = (typedValue.assetCookie << 32) | typedValue.data;
        Drawable e4 = e(j4, context);
        if (e4 != null) {
            return e4;
        }
        LayerDrawable layerDrawable = null;
        if (this.f5065g != null) {
            if (i4 == 2131165240) {
                layerDrawable = new LayerDrawable(new Drawable[]{f(context, 2131165239), f(context, 2131165241)});
            } else if (i4 == 2131165275) {
                layerDrawable = C0701j.a.c(this, context, 2131099707);
            } else if (i4 == 2131165274) {
                layerDrawable = C0701j.a.c(this, context, 2131099708);
            } else if (i4 == 2131165276) {
                layerDrawable = C0701j.a.c(this, context, 2131099709);
            }
        }
        if (layerDrawable != null) {
            layerDrawable.setChangingConfigurations(typedValue.changingConfigurations);
            b(context, j4, layerDrawable);
        }
        return layerDrawable;
    }

    public final synchronized Drawable e(long j4, Context context) {
        C0778g<WeakReference<Drawable.ConstantState>> c0778g = this.f5063d.get(context);
        if (c0778g == null) {
            return null;
        }
        WeakReference weakReference = (WeakReference) c0778g.e(j4, null);
        if (weakReference != null) {
            Drawable.ConstantState constantState = (Drawable.ConstantState) weakReference.get();
            if (constantState != null) {
                return constantState.newDrawable(context.getResources());
            }
            c0778g.h(j4);
        }
        return null;
    }

    public final synchronized Drawable f(Context context, int i4) {
        return g(context, i4, false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x004b, code lost:
        if (r11.f5061b.getOrDefault(r0, null) != null) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x0110, code lost:
        F.a.C0006a.i(r12, r2);
     */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00e1 A[Catch: all -> 0x00e6, TryCatch #1 {all -> 0x00e6, blocks: (B:3:0x0001, B:12:0x0026, B:14:0x002b, B:16:0x0031, B:18:0x0037, B:21:0x0045, B:25:0x0057, B:27:0x005b, B:28:0x0062, B:54:0x00e1, B:58:0x00eb, B:60:0x00f1, B:62:0x00f7, B:69:0x0110, B:67:0x010c, B:71:0x0116, B:75:0x012d, B:82:0x0163, B:83:0x018a, B:90:0x0197, B:31:0x007c, B:33:0x0080, B:35:0x008c, B:36:0x0094, B:41:0x00a0, B:43:0x00b3, B:47:0x00c0, B:48:0x00c9, B:49:0x00d0, B:50:0x00d1, B:52:0x00da, B:24:0x0050, B:6:0x0007, B:8:0x0012, B:10:0x0016, B:94:0x019d, B:95:0x01a6), top: B:99:0x0001, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00eb A[Catch: all -> 0x00e6, TryCatch #1 {all -> 0x00e6, blocks: (B:3:0x0001, B:12:0x0026, B:14:0x002b, B:16:0x0031, B:18:0x0037, B:21:0x0045, B:25:0x0057, B:27:0x005b, B:28:0x0062, B:54:0x00e1, B:58:0x00eb, B:60:0x00f1, B:62:0x00f7, B:69:0x0110, B:67:0x010c, B:71:0x0116, B:75:0x012d, B:82:0x0163, B:83:0x018a, B:90:0x0197, B:31:0x007c, B:33:0x0080, B:35:0x008c, B:36:0x0094, B:41:0x00a0, B:43:0x00b3, B:47:0x00c0, B:48:0x00c9, B:49:0x00d0, B:50:0x00d1, B:52:0x00da, B:24:0x0050, B:6:0x0007, B:8:0x0012, B:10:0x0016, B:94:0x019d, B:95:0x01a6), top: B:99:0x0001, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x00f1 A[Catch: all -> 0x00e6, TryCatch #1 {all -> 0x00e6, blocks: (B:3:0x0001, B:12:0x0026, B:14:0x002b, B:16:0x0031, B:18:0x0037, B:21:0x0045, B:25:0x0057, B:27:0x005b, B:28:0x0062, B:54:0x00e1, B:58:0x00eb, B:60:0x00f1, B:62:0x00f7, B:69:0x0110, B:67:0x010c, B:71:0x0116, B:75:0x012d, B:82:0x0163, B:83:0x018a, B:90:0x0197, B:31:0x007c, B:33:0x0080, B:35:0x008c, B:36:0x0094, B:41:0x00a0, B:43:0x00b3, B:47:0x00c0, B:48:0x00c9, B:49:0x00d0, B:50:0x00d1, B:52:0x00da, B:24:0x0050, B:6:0x0007, B:8:0x0012, B:10:0x0016, B:94:0x019d, B:95:0x01a6), top: B:99:0x0001, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x0197 A[Catch: all -> 0x00e6, TRY_LEAVE, TryCatch #1 {all -> 0x00e6, blocks: (B:3:0x0001, B:12:0x0026, B:14:0x002b, B:16:0x0031, B:18:0x0037, B:21:0x0045, B:25:0x0057, B:27:0x005b, B:28:0x0062, B:54:0x00e1, B:58:0x00eb, B:60:0x00f1, B:62:0x00f7, B:69:0x0110, B:67:0x010c, B:71:0x0116, B:75:0x012d, B:82:0x0163, B:83:0x018a, B:90:0x0197, B:31:0x007c, B:33:0x0080, B:35:0x008c, B:36:0x0094, B:41:0x00a0, B:43:0x00b3, B:47:0x00c0, B:48:0x00c9, B:49:0x00d0, B:50:0x00d1, B:52:0x00da, B:24:0x0050, B:6:0x0007, B:8:0x0012, B:10:0x0016, B:94:0x019d, B:95:0x01a6), top: B:99:0x0001, inners: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final synchronized android.graphics.drawable.Drawable g(android.content.Context r12, int r13, boolean r14) {
        /*
            Method dump skipped, instructions count: 425
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: l.S.g(android.content.Context, int, boolean):android.graphics.drawable.Drawable");
    }

    public final synchronized ColorStateList i(Context context, int i4) {
        ColorStateList colorStateList;
        r.k<ColorStateList> kVar;
        WeakHashMap<Context, r.k<ColorStateList>> weakHashMap = this.f5060a;
        ColorStateList colorStateList2 = null;
        if (weakHashMap != null && (kVar = weakHashMap.get(context)) != null) {
            colorStateList = (ColorStateList) kVar.c(i4, null);
        } else {
            colorStateList = null;
        }
        if (colorStateList == null) {
            f fVar = this.f5065g;
            if (fVar != null) {
                colorStateList2 = ((C0701j.a) fVar).d(context, i4);
            }
            if (colorStateList2 != null) {
                if (this.f5060a == null) {
                    this.f5060a = new WeakHashMap<>();
                }
                r.k<ColorStateList> kVar2 = this.f5060a.get(context);
                if (kVar2 == null) {
                    kVar2 = new r.k<>();
                    this.f5060a.put(context, kVar2);
                }
                kVar2.a(i4, colorStateList2);
            }
            colorStateList = colorStateList2;
        }
        return colorStateList;
    }

    public final synchronized void k(Context context) {
        C0778g<WeakReference<Drawable.ConstantState>> c0778g = this.f5063d.get(context);
        if (c0778g != null) {
            c0778g.b();
        }
    }

    public final synchronized void l(C0701j.a aVar) {
        this.f5065g = aVar;
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0055  */
    /* JADX WARN: Removed duplicated region for block: B:30:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean m(android.content.Context r7, int r8, android.graphics.drawable.Drawable r9) {
        /*
            r6 = this;
            l.S$f r0 = r6.f5065g
            r1 = 0
            if (r0 == 0) goto L6a
            l.j$a r0 = (l.C0701j.a) r0
            android.graphics.PorterDuff$Mode r2 = l.C0701j.f5161b
            int[] r3 = r0.f5164a
            boolean r3 = l.C0701j.a.a(r3, r8)
            r4 = 1
            r5 = -1
            if (r3 == 0) goto L19
            r8 = 2130903271(0x7f0300e7, float:1.7413355E38)
        L16:
            r0 = -1
        L17:
            r3 = 1
            goto L53
        L19:
            int[] r3 = r0.f5166c
            boolean r3 = l.C0701j.a.a(r3, r8)
            if (r3 == 0) goto L25
            r8 = 2130903269(0x7f0300e5, float:1.7413351E38)
            goto L16
        L25:
            int[] r0 = r0.f5167d
            boolean r0 = l.C0701j.a.a(r0, r8)
            r3 = 16842801(0x1010031, float:2.3693695E-38)
            if (r0 == 0) goto L36
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
        L32:
            r8 = 16842801(0x1010031, float:2.3693695E-38)
            goto L16
        L36:
            r0 = 2131165261(0x7f07004d, float:1.7944734E38)
            if (r8 != r0) goto L4a
            r8 = 1109603123(0x42233333, float:40.8)
            int r8 = java.lang.Math.round(r8)
            r0 = 16842800(0x1010030, float:2.3693693E-38)
            r0 = r8
            r8 = 16842800(0x1010030, float:2.3693693E-38)
            goto L17
        L4a:
            r0 = 2131165243(0x7f07003b, float:1.7944698E38)
            if (r8 != r0) goto L50
            goto L32
        L50:
            r8 = 0
            r0 = -1
            r3 = 0
        L53:
            if (r3 == 0) goto L6a
            android.graphics.drawable.Drawable r9 = r9.mutate()
            int r7 = l.W.c(r7, r8)
            android.graphics.PorterDuffColorFilter r7 = l.C0701j.c(r7, r2)
            r9.setColorFilter(r7)
            if (r0 == r5) goto L69
            r9.setAlpha(r0)
        L69:
            r1 = 1
        L6a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: l.S.m(android.content.Context, int, android.graphics.drawable.Drawable):boolean");
    }
}
