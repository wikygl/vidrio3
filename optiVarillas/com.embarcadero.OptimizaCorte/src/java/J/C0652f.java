package j;

import M.AbstractC0220b;
import M.C0235q;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import d.C0376a;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import k.MenuItemC0680c;
import l.G;
import org.xmlpull.v1.XmlPullParserException;

/* renamed from: j.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0652f extends MenuInflater {

    /* renamed from: e  reason: collision with root package name */
    public static final Class<?>[] f4668e;
    public static final Class<?>[] f;

    /* renamed from: a  reason: collision with root package name */
    public final Object[] f4669a;

    /* renamed from: b  reason: collision with root package name */
    public final Object[] f4670b;

    /* renamed from: c  reason: collision with root package name */
    public final Context f4671c;

    /* renamed from: d  reason: collision with root package name */
    public Object f4672d;

    /* renamed from: j.f$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a implements MenuItem.OnMenuItemClickListener {

        /* renamed from: c  reason: collision with root package name */
        public static final Class<?>[] f4673c = {MenuItem.class};

        /* renamed from: a  reason: collision with root package name */
        public Object f4674a;

        /* renamed from: b  reason: collision with root package name */
        public Method f4675b;

        @Override // android.view.MenuItem.OnMenuItemClickListener
        public final boolean onMenuItemClick(MenuItem menuItem) {
            Method method = this.f4675b;
            try {
                Class<?> returnType = method.getReturnType();
                Class<?> cls = Boolean.TYPE;
                Object obj = this.f4674a;
                if (returnType == cls) {
                    return ((Boolean) method.invoke(obj, menuItem)).booleanValue();
                }
                method.invoke(obj, menuItem);
                return true;
            } catch (Exception e4) {
                throw new RuntimeException(e4);
            }
        }
    }

    /* renamed from: j.f$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class b {

        /* renamed from: A  reason: collision with root package name */
        public CharSequence f4676A;

        /* renamed from: B  reason: collision with root package name */
        public CharSequence f4677B;

        /* renamed from: a  reason: collision with root package name */
        public final Menu f4681a;

        /* renamed from: h  reason: collision with root package name */
        public boolean f4687h;

        /* renamed from: i  reason: collision with root package name */
        public int f4688i;

        /* renamed from: j  reason: collision with root package name */
        public int f4689j;

        /* renamed from: k  reason: collision with root package name */
        public CharSequence f4690k;

        /* renamed from: l  reason: collision with root package name */
        public CharSequence f4691l;

        /* renamed from: m  reason: collision with root package name */
        public int f4692m;

        /* renamed from: n  reason: collision with root package name */
        public char f4693n;

        /* renamed from: o  reason: collision with root package name */
        public int f4694o;

        /* renamed from: p  reason: collision with root package name */
        public char f4695p;

        /* renamed from: q  reason: collision with root package name */
        public int f4696q;

        /* renamed from: r  reason: collision with root package name */
        public int f4697r;

        /* renamed from: s  reason: collision with root package name */
        public boolean f4698s;

        /* renamed from: t  reason: collision with root package name */
        public boolean f4699t;

        /* renamed from: u  reason: collision with root package name */
        public boolean f4700u;

        /* renamed from: v  reason: collision with root package name */
        public int f4701v;

        /* renamed from: w  reason: collision with root package name */
        public int f4702w;

        /* renamed from: x  reason: collision with root package name */
        public String f4703x;

        /* renamed from: y  reason: collision with root package name */
        public String f4704y;

        /* renamed from: z  reason: collision with root package name */
        public AbstractC0220b f4705z;

        /* renamed from: C  reason: collision with root package name */
        public ColorStateList f4678C = null;

        /* renamed from: D  reason: collision with root package name */
        public PorterDuff.Mode f4679D = null;

        /* renamed from: b  reason: collision with root package name */
        public int f4682b = 0;

        /* renamed from: c  reason: collision with root package name */
        public int f4683c = 0;

        /* renamed from: d  reason: collision with root package name */
        public int f4684d = 0;

        /* renamed from: e  reason: collision with root package name */
        public int f4685e = 0;
        public boolean f = true;

        /* renamed from: g  reason: collision with root package name */
        public boolean f4686g = true;

        public b(Menu menu) {
            this.f4681a = menu;
        }

        public final <T> T a(String str, Class<?>[] clsArr, Object[] objArr) {
            try {
                Constructor<?> constructor = Class.forName(str, false, C0652f.this.f4671c.getClassLoader()).getConstructor(clsArr);
                constructor.setAccessible(true);
                return (T) constructor.newInstance(objArr);
            } catch (Exception e4) {
                Log.w("SupportMenuInflater", "Cannot instantiate class: " + str, e4);
                return null;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v33, types: [android.view.MenuItem$OnMenuItemClickListener, j.f$a, java.lang.Object] */
        public final void b(MenuItem menuItem) {
            boolean z4;
            MenuItem enabled = menuItem.setChecked(this.f4698s).setVisible(this.f4699t).setEnabled(this.f4700u);
            boolean z5 = false;
            if (this.f4697r >= 1) {
                z4 = true;
            } else {
                z4 = false;
            }
            enabled.setCheckable(z4).setTitleCondensed(this.f4691l).setIcon(this.f4692m);
            int i4 = this.f4701v;
            if (i4 >= 0) {
                menuItem.setShowAsAction(i4);
            }
            String str = this.f4704y;
            C0652f c0652f = C0652f.this;
            if (str != null) {
                if (!c0652f.f4671c.isRestricted()) {
                    if (c0652f.f4672d == null) {
                        c0652f.f4672d = C0652f.a(c0652f.f4671c);
                    }
                    Object obj = c0652f.f4672d;
                    String str2 = this.f4704y;
                    ?? obj2 = new Object();
                    obj2.f4674a = obj;
                    Class<?> cls = obj.getClass();
                    try {
                        obj2.f4675b = cls.getMethod(str2, a.f4673c);
                        menuItem.setOnMenuItemClickListener(obj2);
                    } catch (Exception e4) {
                        StringBuilder f = X1.b.f("Couldn't resolve menu item onClick handler ", str2, " in class ");
                        f.append(cls.getName());
                        InflateException inflateException = new InflateException(f.toString());
                        inflateException.initCause(e4);
                        throw inflateException;
                    }
                } else {
                    throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context");
                }
            }
            if (this.f4697r >= 2) {
                if (menuItem instanceof androidx.appcompat.view.menu.h) {
                    androidx.appcompat.view.menu.h hVar = (androidx.appcompat.view.menu.h) menuItem;
                    hVar.x = (hVar.x & (-5)) | 4;
                } else if (menuItem instanceof MenuItemC0680c) {
                    MenuItemC0680c menuItemC0680c = (MenuItemC0680c) menuItem;
                    try {
                        Method method = menuItemC0680c.f4915e;
                        G.b bVar = menuItemC0680c.f4914d;
                        if (method == null) {
                            menuItemC0680c.f4915e = bVar.getClass().getDeclaredMethod("setExclusiveCheckable", Boolean.TYPE);
                        }
                        menuItemC0680c.f4915e.invoke(bVar, Boolean.TRUE);
                    } catch (Exception e5) {
                        Log.w("MenuItemWrapper", "Error while calling setExclusiveCheckable", e5);
                    }
                }
            }
            String str3 = this.f4703x;
            if (str3 != null) {
                menuItem.setActionView((View) a(str3, C0652f.f4668e, c0652f.f4669a));
                z5 = true;
            }
            int i5 = this.f4702w;
            if (i5 > 0) {
                if (!z5) {
                    menuItem.setActionView(i5);
                } else {
                    Log.w("SupportMenuInflater", "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
                }
            }
            AbstractC0220b abstractC0220b = this.f4705z;
            if (abstractC0220b != null) {
                if (menuItem instanceof G.b) {
                    ((G.b) menuItem).a(abstractC0220b);
                } else {
                    Log.w("MenuItemCompat", "setActionProvider: item does not implement SupportMenuItem; ignoring");
                }
            }
            CharSequence charSequence = this.f4676A;
            boolean z6 = menuItem instanceof G.b;
            if (z6) {
                ((G.b) menuItem).setContentDescription(charSequence);
            } else if (Build.VERSION.SDK_INT >= 26) {
                C0235q.h(menuItem, charSequence);
            }
            CharSequence charSequence2 = this.f4677B;
            if (z6) {
                ((G.b) menuItem).setTooltipText(charSequence2);
            } else if (Build.VERSION.SDK_INT >= 26) {
                C0235q.m(menuItem, charSequence2);
            }
            char c4 = this.f4693n;
            int i6 = this.f4694o;
            if (z6) {
                ((G.b) menuItem).setAlphabeticShortcut(c4, i6);
            } else if (Build.VERSION.SDK_INT >= 26) {
                C0235q.g(menuItem, c4, i6);
            }
            char c5 = this.f4695p;
            int i7 = this.f4696q;
            if (z6) {
                ((G.b) menuItem).setNumericShortcut(c5, i7);
            } else if (Build.VERSION.SDK_INT >= 26) {
                C0235q.k(menuItem, c5, i7);
            }
            PorterDuff.Mode mode = this.f4679D;
            if (mode != null) {
                if (z6) {
                    ((G.b) menuItem).setIconTintMode(mode);
                } else if (Build.VERSION.SDK_INT >= 26) {
                    C0235q.j(menuItem, mode);
                }
            }
            ColorStateList colorStateList = this.f4678C;
            if (colorStateList != null) {
                if (z6) {
                    ((G.b) menuItem).setIconTintList(colorStateList);
                } else if (Build.VERSION.SDK_INT >= 26) {
                    C0235q.i(menuItem, colorStateList);
                }
            }
        }
    }

    static {
        Class<?>[] clsArr = {Context.class};
        f4668e = clsArr;
        f = clsArr;
    }

    public C0652f(Context context) {
        super(context);
        this.f4671c = context;
        Object[] objArr = {context};
        this.f4669a = objArr;
        this.f4670b = objArr;
    }

    public static Object a(Object obj) {
        if (obj instanceof Activity) {
            return obj;
        }
        if (obj instanceof ContextWrapper) {
            return a(((ContextWrapper) obj).getBaseContext());
        }
        return obj;
    }

    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r4v60 */
    public final void b(XmlResourceParser xmlResourceParser, AttributeSet attributeSet, Menu menu) {
        ?? r4;
        int i4;
        char charAt;
        char charAt2;
        boolean z4;
        ColorStateList colorStateList;
        int resourceId;
        b bVar = new b(menu);
        int eventType = xmlResourceParser.getEventType();
        while (true) {
            r4 = 1;
            i4 = 2;
            if (eventType == 2) {
                String name = xmlResourceParser.getName();
                if (name.equals("menu")) {
                    eventType = xmlResourceParser.next();
                } else {
                    throw new RuntimeException("Expecting menu, got ".concat(name));
                }
            } else {
                eventType = xmlResourceParser.next();
                if (eventType == 1) {
                    break;
                }
            }
        }
        boolean z5 = false;
        boolean z6 = false;
        String str = null;
        while (!z5) {
            if (eventType != r4) {
                if (eventType != i4) {
                    if (eventType == 3) {
                        String name2 = xmlResourceParser.getName();
                        if (z6 && name2.equals(str)) {
                            z6 = false;
                            str = null;
                        } else if (name2.equals("group")) {
                            bVar.f4682b = 0;
                            bVar.f4683c = 0;
                            bVar.f4684d = 0;
                            bVar.f4685e = 0;
                            bVar.f = r4;
                            bVar.f4686g = r4;
                        } else if (name2.equals("item")) {
                            if (!bVar.f4687h) {
                                AbstractC0220b abstractC0220b = bVar.f4705z;
                                if (abstractC0220b != null && abstractC0220b.a()) {
                                    bVar.f4687h = r4;
                                    bVar.b(bVar.f4681a.addSubMenu(bVar.f4682b, bVar.f4688i, bVar.f4689j, bVar.f4690k).getItem());
                                } else {
                                    bVar.f4687h = r4;
                                    bVar.b(bVar.f4681a.add(bVar.f4682b, bVar.f4688i, bVar.f4689j, bVar.f4690k));
                                }
                            }
                        } else if (name2.equals("menu")) {
                            z5 = true;
                        }
                        eventType = xmlResourceParser.next();
                        r4 = 1;
                        i4 = 2;
                    }
                } else if (!z6) {
                    String name3 = xmlResourceParser.getName();
                    boolean equals = name3.equals("group");
                    C0652f c0652f = C0652f.this;
                    if (equals) {
                        TypedArray obtainStyledAttributes = c0652f.f4671c.obtainStyledAttributes(attributeSet, C0376a.f3143p);
                        bVar.f4682b = obtainStyledAttributes.getResourceId(r4, 0);
                        bVar.f4683c = obtainStyledAttributes.getInt(3, 0);
                        bVar.f4684d = obtainStyledAttributes.getInt(4, 0);
                        bVar.f4685e = obtainStyledAttributes.getInt(5, 0);
                        bVar.f = obtainStyledAttributes.getBoolean(2, r4);
                        bVar.f4686g = obtainStyledAttributes.getBoolean(0, r4);
                        obtainStyledAttributes.recycle();
                    } else if (name3.equals("item")) {
                        Context context = c0652f.f4671c;
                        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, C0376a.f3144q);
                        bVar.f4688i = obtainStyledAttributes2.getResourceId(2, 0);
                        bVar.f4689j = (obtainStyledAttributes2.getInt(5, bVar.f4683c) & (-65536)) | (obtainStyledAttributes2.getInt(6, bVar.f4684d) & 65535);
                        bVar.f4690k = obtainStyledAttributes2.getText(7);
                        bVar.f4691l = obtainStyledAttributes2.getText(8);
                        bVar.f4692m = obtainStyledAttributes2.getResourceId(0, 0);
                        String string = obtainStyledAttributes2.getString(9);
                        if (string == null) {
                            charAt = 0;
                        } else {
                            charAt = string.charAt(0);
                        }
                        bVar.f4693n = charAt;
                        bVar.f4694o = obtainStyledAttributes2.getInt(16, 4096);
                        String string2 = obtainStyledAttributes2.getString(10);
                        if (string2 == null) {
                            charAt2 = 0;
                        } else {
                            charAt2 = string2.charAt(0);
                        }
                        bVar.f4695p = charAt2;
                        bVar.f4696q = obtainStyledAttributes2.getInt(20, 4096);
                        if (obtainStyledAttributes2.hasValue(11)) {
                            bVar.f4697r = obtainStyledAttributes2.getBoolean(11, false) ? 1 : 0;
                        } else {
                            bVar.f4697r = bVar.f4685e;
                        }
                        bVar.f4698s = obtainStyledAttributes2.getBoolean(3, false);
                        bVar.f4699t = obtainStyledAttributes2.getBoolean(4, bVar.f);
                        bVar.f4700u = obtainStyledAttributes2.getBoolean(1, bVar.f4686g);
                        bVar.f4701v = obtainStyledAttributes2.getInt(21, -1);
                        bVar.f4704y = obtainStyledAttributes2.getString(12);
                        bVar.f4702w = obtainStyledAttributes2.getResourceId(13, 0);
                        bVar.f4703x = obtainStyledAttributes2.getString(15);
                        String string3 = obtainStyledAttributes2.getString(14);
                        if (string3 != null) {
                            z4 = true;
                        } else {
                            z4 = false;
                        }
                        if (z4 && bVar.f4702w == 0 && bVar.f4703x == null) {
                            bVar.f4705z = (AbstractC0220b) bVar.a(string3, f, c0652f.f4670b);
                        } else {
                            if (z4) {
                                Log.w("SupportMenuInflater", "Ignoring attribute 'actionProviderClass'. Action view already specified.");
                            }
                            bVar.f4705z = null;
                        }
                        bVar.f4676A = obtainStyledAttributes2.getText(17);
                        bVar.f4677B = obtainStyledAttributes2.getText(22);
                        if (obtainStyledAttributes2.hasValue(19)) {
                            bVar.f4679D = G.c(obtainStyledAttributes2.getInt(19, -1), bVar.f4679D);
                        } else {
                            bVar.f4679D = null;
                        }
                        if (obtainStyledAttributes2.hasValue(18)) {
                            if (!obtainStyledAttributes2.hasValue(18) || (resourceId = obtainStyledAttributes2.getResourceId(18, 0)) == 0 || (colorStateList = C.a.c(context, resourceId)) == null) {
                                colorStateList = obtainStyledAttributes2.getColorStateList(18);
                            }
                            bVar.f4678C = colorStateList;
                        } else {
                            bVar.f4678C = null;
                        }
                        obtainStyledAttributes2.recycle();
                        bVar.f4687h = false;
                    } else {
                        if (name3.equals("menu")) {
                            bVar.f4687h = true;
                            SubMenu addSubMenu = bVar.f4681a.addSubMenu(bVar.f4682b, bVar.f4688i, bVar.f4689j, bVar.f4690k);
                            bVar.b(addSubMenu.getItem());
                            b(xmlResourceParser, attributeSet, addSubMenu);
                        } else {
                            str = name3;
                            z6 = true;
                        }
                        eventType = xmlResourceParser.next();
                        r4 = 1;
                        i4 = 2;
                    }
                }
                eventType = xmlResourceParser.next();
                r4 = 1;
                i4 = 2;
            } else {
                throw new RuntimeException("Unexpected end of document");
            }
        }
    }

    @Override // android.view.MenuInflater
    public final void inflate(int i4, Menu menu) {
        if (!(menu instanceof G.a)) {
            super.inflate(i4, menu);
            return;
        }
        XmlResourceParser xmlResourceParser = null;
        boolean z4 = false;
        try {
            try {
                xmlResourceParser = this.f4671c.getResources().getLayout(i4);
                AttributeSet asAttributeSet = Xml.asAttributeSet(xmlResourceParser);
                if (menu instanceof androidx.appcompat.view.menu.f) {
                    androidx.appcompat.view.menu.f fVar = (androidx.appcompat.view.menu.f) menu;
                    if (!fVar.p) {
                        fVar.w();
                        z4 = true;
                    }
                }
                b(xmlResourceParser, asAttributeSet, menu);
                if (z4) {
                    ((androidx.appcompat.view.menu.f) menu).v();
                }
                xmlResourceParser.close();
            } catch (IOException e4) {
                throw new InflateException("Error inflating menu XML", e4);
            } catch (XmlPullParserException e5) {
                throw new InflateException("Error inflating menu XML", e5);
            }
        } catch (Throwable th) {
            if (z4) {
                ((androidx.appcompat.view.menu.f) menu).v();
            }
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }
}
