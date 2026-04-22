package E;

import D.e;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.fonts.FontVariationAxis;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class h extends f {
    public final Class<?> f;

    /* renamed from: g  reason: collision with root package name */
    public final Constructor<?> f822g;

    /* renamed from: h  reason: collision with root package name */
    public final Method f823h;

    /* renamed from: i  reason: collision with root package name */
    public final Method f824i;

    /* renamed from: j  reason: collision with root package name */
    public final Method f825j;

    /* renamed from: k  reason: collision with root package name */
    public final Method f826k;

    /* renamed from: l  reason: collision with root package name */
    public final Method f827l;

    public h() {
        Method method;
        Constructor<?> constructor;
        Method method2;
        Method method3;
        Method method4;
        Method method5;
        Class<?> cls = null;
        try {
            Class<?> cls2 = Class.forName("android.graphics.FontFamily");
            constructor = cls2.getConstructor(null);
            method2 = n(cls2);
            Class<?> cls3 = Integer.TYPE;
            method3 = cls2.getMethod("addFontFromBuffer", ByteBuffer.class, cls3, FontVariationAxis[].class, cls3, cls3);
            method4 = cls2.getMethod("freeze", null);
            method5 = cls2.getMethod("abortCreation", null);
            method = o(cls2);
            cls = cls2;
        } catch (ClassNotFoundException | NoSuchMethodException e4) {
            Log.e("TypefaceCompatApi26Impl", "Unable to collect necessary methods for class ".concat(e4.getClass().getName()), e4);
            method = null;
            constructor = null;
            method2 = null;
            method3 = null;
            method4 = null;
            method5 = null;
        }
        this.f = cls;
        this.f822g = constructor;
        this.f823h = method2;
        this.f824i = method3;
        this.f825j = method4;
        this.f826k = method5;
        this.f827l = method;
    }

    public static Method n(Class cls) {
        Class<?> cls2 = Integer.TYPE;
        return cls.getMethod("addFontFromAssetManager", AssetManager.class, String.class, cls2, Boolean.TYPE, cls2, cls2, cls2, FontVariationAxis[].class);
    }

    @Override // E.f, E.l
    public final Typeface a(Context context, e.c cVar, Resources resources, int i4) {
        e.d[] dVarArr;
        if (!l()) {
            return super.a(context, cVar, resources, i4);
        }
        Object m4 = m();
        if (m4 == null) {
            return null;
        }
        for (e.d dVar : cVar.f517a) {
            if (!i(context, m4, dVar.f518a, dVar.f522e, dVar.f519b, dVar.f520c ? 1 : 0, FontVariationAxis.fromFontVariationSettings(dVar.f521d))) {
                h(m4);
                return null;
            }
        }
        if (!k(m4)) {
            return null;
        }
        return j(m4);
    }

    @Override // E.f, E.l
    public final Typeface b(Context context, J.l[] lVarArr, int i4) {
        Typeface j4;
        boolean z4;
        if (lVarArr.length < 1) {
            return null;
        }
        if (!l()) {
            J.l e4 = e(i4, lVarArr);
            try {
                ParcelFileDescriptor openFileDescriptor = context.getContentResolver().openFileDescriptor(e4.f1181a, "r", null);
                if (openFileDescriptor == null) {
                    if (openFileDescriptor != null) {
                        openFileDescriptor.close();
                    }
                    return null;
                }
                Typeface build = new Typeface.Builder(openFileDescriptor.getFileDescriptor()).setWeight(e4.f1183c).setItalic(e4.f1184d).build();
                openFileDescriptor.close();
                return build;
            } catch (IOException unused) {
                return null;
            }
        }
        HashMap hashMap = new HashMap();
        for (J.l lVar : lVarArr) {
            if (lVar.f1185e == 0) {
                Uri uri = lVar.f1181a;
                if (!hashMap.containsKey(uri)) {
                    hashMap.put(uri, m.e(context, uri));
                }
            }
        }
        Map unmodifiableMap = Collections.unmodifiableMap(hashMap);
        Object m4 = m();
        if (m4 == null) {
            return null;
        }
        boolean z5 = false;
        for (J.l lVar2 : lVarArr) {
            ByteBuffer byteBuffer = (ByteBuffer) unmodifiableMap.get(lVar2.f1181a);
            if (byteBuffer != null) {
                try {
                    z4 = ((Boolean) this.f824i.invoke(m4, byteBuffer, Integer.valueOf(lVar2.f1182b), null, Integer.valueOf(lVar2.f1183c), Integer.valueOf(lVar2.f1184d ? 1 : 0))).booleanValue();
                } catch (IllegalAccessException | InvocationTargetException unused2) {
                    z4 = false;
                }
                if (!z4) {
                    h(m4);
                    return null;
                }
                z5 = true;
            }
        }
        if (!z5) {
            h(m4);
            return null;
        } else if (!k(m4) || (j4 = j(m4)) == null) {
            return null;
        } else {
            return Typeface.create(j4, i4);
        }
    }

    @Override // E.l
    public final Typeface d(Context context, Resources resources, int i4, String str, int i5) {
        if (!l()) {
            return super.d(context, resources, i4, str, i5);
        }
        Object m4 = m();
        if (m4 == null) {
            return null;
        }
        if (!i(context, m4, str, 0, -1, -1, null)) {
            h(m4);
            return null;
        } else if (!k(m4)) {
            return null;
        } else {
            return j(m4);
        }
    }

    public final void h(Object obj) {
        try {
            this.f826k.invoke(obj, null);
        } catch (IllegalAccessException | InvocationTargetException unused) {
        }
    }

    public final boolean i(Context context, Object obj, String str, int i4, int i5, int i6, FontVariationAxis[] fontVariationAxisArr) {
        try {
            return ((Boolean) this.f823h.invoke(obj, context.getAssets(), str, 0, Boolean.FALSE, Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), fontVariationAxisArr)).booleanValue();
        } catch (IllegalAccessException | InvocationTargetException unused) {
            return false;
        }
    }

    public Typeface j(Object obj) {
        try {
            Object newInstance = Array.newInstance(this.f, 1);
            Array.set(newInstance, 0, obj);
            return (Typeface) this.f827l.invoke(null, newInstance, -1, -1);
        } catch (IllegalAccessException | InvocationTargetException unused) {
            return null;
        }
    }

    public final boolean k(Object obj) {
        try {
            return ((Boolean) this.f825j.invoke(obj, null)).booleanValue();
        } catch (IllegalAccessException | InvocationTargetException unused) {
            return false;
        }
    }

    public final boolean l() {
        Method method = this.f823h;
        if (method == null) {
            Log.w("TypefaceCompatApi26Impl", "Unable to collect necessary private methods. Fallback to legacy implementation.");
        }
        if (method != null) {
            return true;
        }
        return false;
    }

    public final Object m() {
        try {
            return this.f822g.newInstance(null);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException unused) {
            return null;
        }
    }

    public Method o(Class<?> cls) {
        Class cls2 = Integer.TYPE;
        Method declaredMethod = Typeface.class.getDeclaredMethod("createFromFamiliesWithDefault", Array.newInstance(cls, 1).getClass(), cls2, cls2);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }
}
