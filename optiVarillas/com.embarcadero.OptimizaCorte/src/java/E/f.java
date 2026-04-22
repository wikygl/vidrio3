package E;

import D.e;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.ParcelFileDescriptor;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class f extends l {

    /* renamed from: a  reason: collision with root package name */
    public static Class<?> f813a;

    /* renamed from: b  reason: collision with root package name */
    public static Constructor<?> f814b;

    /* renamed from: c  reason: collision with root package name */
    public static Method f815c;

    /* renamed from: d  reason: collision with root package name */
    public static Method f816d;

    /* renamed from: e  reason: collision with root package name */
    public static boolean f817e;

    public static boolean f(Object obj, String str, int i4, boolean z4) {
        g();
        try {
            return ((Boolean) f815c.invoke(obj, str, Integer.valueOf(i4), Boolean.valueOf(z4))).booleanValue();
        } catch (IllegalAccessException | InvocationTargetException e4) {
            throw new RuntimeException(e4);
        }
    }

    public static void g() {
        Method method;
        Class<?> cls;
        Method method2;
        if (f817e) {
            return;
        }
        f817e = true;
        Constructor<?> constructor = null;
        try {
            cls = Class.forName("android.graphics.FontFamily");
            Constructor<?> constructor2 = cls.getConstructor(null);
            method2 = cls.getMethod("addFontWeightStyle", String.class, Integer.TYPE, Boolean.TYPE);
            method = Typeface.class.getMethod("createFromFamiliesWithDefault", Array.newInstance(cls, 1).getClass());
            constructor = constructor2;
        } catch (ClassNotFoundException | NoSuchMethodException e4) {
            Log.e("TypefaceCompatApi21Impl", e4.getClass().getName(), e4);
            method = null;
            cls = null;
            method2 = null;
        }
        f814b = constructor;
        f813a = cls;
        f815c = method2;
        f816d = method;
    }

    @Override // E.l
    public Typeface a(Context context, e.c cVar, Resources resources, int i4) {
        e.d[] dVarArr;
        g();
        try {
            Object newInstance = f814b.newInstance(null);
            for (e.d dVar : cVar.f517a) {
                File d4 = m.d(context);
                if (d4 == null) {
                    return null;
                }
                try {
                    if (!m.b(d4, resources, dVar.f)) {
                        return null;
                    }
                    if (!f(newInstance, d4.getPath(), dVar.f519b, dVar.f520c)) {
                        return null;
                    }
                    d4.delete();
                } catch (RuntimeException unused) {
                    return null;
                } finally {
                    d4.delete();
                }
            }
            g();
            try {
                Object newInstance2 = Array.newInstance(f813a, 1);
                Array.set(newInstance2, 0, newInstance);
                return (Typeface) f816d.invoke(null, newInstance2);
            } catch (IllegalAccessException | InvocationTargetException e4) {
                throw new RuntimeException(e4);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e5) {
            throw new RuntimeException(e5);
        }
    }

    @Override // E.l
    public Typeface b(Context context, J.l[] lVarArr, int i4) {
        File file;
        FileInputStream fileInputStream;
        String readlink;
        if (lVarArr.length < 1) {
            return null;
        }
        try {
            ParcelFileDescriptor openFileDescriptor = context.getContentResolver().openFileDescriptor(e(i4, lVarArr).f1181a, "r", null);
            if (openFileDescriptor == null) {
                if (openFileDescriptor != null) {
                    openFileDescriptor.close();
                }
                return null;
            }
            try {
                readlink = Os.readlink("/proc/self/fd/" + openFileDescriptor.getFd());
            } catch (ErrnoException unused) {
            }
            try {
                if (OsConstants.S_ISREG(Os.stat(readlink).st_mode)) {
                    file = new File(readlink);
                    if (file != null && file.canRead()) {
                        Typeface createFromFile = Typeface.createFromFile(file);
                        openFileDescriptor.close();
                        return createFromFile;
                    }
                    fileInputStream = new FileInputStream(openFileDescriptor.getFileDescriptor());
                    Typeface c4 = c(context, fileInputStream);
                    fileInputStream.close();
                    openFileDescriptor.close();
                    return c4;
                }
                Typeface c42 = c(context, fileInputStream);
                fileInputStream.close();
                openFileDescriptor.close();
                return c42;
            } catch (Throwable th) {
                try {
                    fileInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
            file = null;
            if (file != null) {
                Typeface createFromFile2 = Typeface.createFromFile(file);
                openFileDescriptor.close();
                return createFromFile2;
            }
            fileInputStream = new FileInputStream(openFileDescriptor.getFileDescriptor());
        } catch (IOException unused2) {
            return null;
        }
    }
}
