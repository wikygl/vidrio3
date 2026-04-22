package e;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.View;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import l.C0689B;
import l.C0694c;
import l.C0696e;
import l.C0697f;
import l.C0709s;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class u {

    /* renamed from: b  reason: collision with root package name */
    public static final Class<?>[] f3291b = {Context.class, AttributeSet.class};

    /* renamed from: c  reason: collision with root package name */
    public static final int[] f3292c = {16843375};

    /* renamed from: d  reason: collision with root package name */
    public static final int[] f3293d = {16844160};

    /* renamed from: e  reason: collision with root package name */
    public static final int[] f3294e = {16844156};
    public static final int[] f = {16844148};

    /* renamed from: g  reason: collision with root package name */
    public static final String[] f3295g = {"android.widget.", "android.view.", "android.webkit."};

    /* renamed from: h  reason: collision with root package name */
    public static final r.j<String, Constructor<? extends View>> f3296h = new r.j<>();

    /* renamed from: a  reason: collision with root package name */
    public final Object[] f3297a = new Object[2];

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a implements View.OnClickListener {

        /* renamed from: j  reason: collision with root package name */
        public final View f3298j;

        /* renamed from: k  reason: collision with root package name */
        public final String f3299k;

        /* renamed from: l  reason: collision with root package name */
        public Method f3300l;

        /* renamed from: m  reason: collision with root package name */
        public Context f3301m;

        public a(View view, String str) {
            this.f3298j = view;
            this.f3299k = str;
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            int id;
            String str;
            Method method;
            if (this.f3300l == null) {
                View view2 = this.f3298j;
                Context context = view2.getContext();
                while (true) {
                    String str2 = this.f3299k;
                    if (context != null) {
                        try {
                            if (!context.isRestricted() && (method = context.getClass().getMethod(str2, View.class)) != null) {
                                this.f3300l = method;
                                this.f3301m = context;
                            }
                        } catch (NoSuchMethodException unused) {
                        }
                        if (context instanceof ContextWrapper) {
                            context = ((ContextWrapper) context).getBaseContext();
                        } else {
                            context = null;
                        }
                    } else {
                        if (view2.getId() == -1) {
                            str = "";
                        } else {
                            str = " with id '" + view2.getContext().getResources().getResourceEntryName(id) + "'";
                        }
                        StringBuilder f = X1.b.f("Could not find method ", str2, "(View) in a parent or ancestor Context for android:onClick attribute defined on view ");
                        f.append(view2.getClass());
                        f.append(str);
                        throw new IllegalStateException(f.toString());
                    }
                }
            }
            try {
                this.f3300l.invoke(this.f3301m, view);
            } catch (IllegalAccessException e4) {
                throw new IllegalStateException("Could not execute non-public method for android:onClick", e4);
            } catch (InvocationTargetException e5) {
                throw new IllegalStateException("Could not execute method for android:onClick", e5);
            }
        }
    }

    public C0694c a(Context context, AttributeSet attributeSet) {
        return new C0694c(context, attributeSet);
    }

    public C0696e b(Context context, AttributeSet attributeSet) {
        return new C0696e(context, attributeSet);
    }

    public C0697f c(Context context, AttributeSet attributeSet) {
        return new C0697f(context, attributeSet);
    }

    public C0709s d(Context context, AttributeSet attributeSet) {
        return new C0709s(context, attributeSet);
    }

    public C0689B e(Context context, AttributeSet attributeSet) {
        return new C0689B(context, attributeSet);
    }

    public final View f(Context context, String str, String str2) {
        String concat;
        r.j<String, Constructor<? extends View>> jVar = f3296h;
        Constructor<? extends View> orDefault = jVar.getOrDefault(str, null);
        if (orDefault == null) {
            if (str2 != null) {
                try {
                    concat = str2.concat(str);
                } catch (Exception unused) {
                    return null;
                }
            } else {
                concat = str;
            }
            orDefault = Class.forName(concat, false, context.getClassLoader()).asSubclass(View.class).getConstructor(f3291b);
            jVar.put(str, orDefault);
        }
        orDefault.setAccessible(true);
        return orDefault.newInstance(this.f3297a);
    }
}
