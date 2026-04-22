package S;

import C1.C0149c;
import K.f;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.icu.text.DecimalFormatSymbols;
import android.os.Build;
import android.text.Editable;
import android.text.PrecomputedText;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class g {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a {
        public static int a(TextView textView) {
            return textView.getBreakStrategy();
        }

        public static ColorStateList b(TextView textView) {
            return textView.getCompoundDrawableTintList();
        }

        public static PorterDuff.Mode c(TextView textView) {
            return textView.getCompoundDrawableTintMode();
        }

        public static int d(TextView textView) {
            return textView.getHyphenationFrequency();
        }

        public static void e(TextView textView, int i4) {
            textView.setBreakStrategy(i4);
        }

        public static void f(TextView textView, ColorStateList colorStateList) {
            textView.setCompoundDrawableTintList(colorStateList);
        }

        public static void g(TextView textView, PorterDuff.Mode mode) {
            textView.setCompoundDrawableTintMode(mode);
        }

        public static void h(TextView textView, int i4) {
            textView.setHyphenationFrequency(i4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class b {
        public static DecimalFormatSymbols a(Locale locale) {
            return DecimalFormatSymbols.getInstance(locale);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class d {
        public static void a(TextView textView, int i4, float f) {
            textView.setLineHeight(i4, f);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class e implements ActionMode.Callback {

        /* renamed from: a  reason: collision with root package name */
        public final ActionMode.Callback f2102a;

        /* renamed from: b  reason: collision with root package name */
        public final TextView f2103b;

        /* renamed from: c  reason: collision with root package name */
        public Class<?> f2104c;

        /* renamed from: d  reason: collision with root package name */
        public Method f2105d;

        /* renamed from: e  reason: collision with root package name */
        public boolean f2106e;
        public boolean f = false;

        public e(ActionMode.Callback callback, TextView textView) {
            this.f2102a = callback;
            this.f2103b = textView;
        }

        @Override // android.view.ActionMode.Callback
        public final boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return this.f2102a.onActionItemClicked(actionMode, menuItem);
        }

        @Override // android.view.ActionMode.Callback
        public final boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return this.f2102a.onCreateActionMode(actionMode, menu);
        }

        @Override // android.view.ActionMode.Callback
        public final void onDestroyActionMode(ActionMode actionMode) {
            this.f2102a.onDestroyActionMode(actionMode);
        }

        @Override // android.view.ActionMode.Callback
        public final boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            Method declaredMethod;
            boolean z4;
            int checkSelfPermission;
            TextView textView = this.f2103b;
            Context context = textView.getContext();
            PackageManager packageManager = context.getPackageManager();
            if (!this.f) {
                this.f = true;
                try {
                    Class<?> cls = Class.forName("com.android.internal.view.menu.MenuBuilder");
                    this.f2104c = cls;
                    this.f2105d = cls.getDeclaredMethod("removeItemAt", Integer.TYPE);
                    this.f2106e = true;
                } catch (ClassNotFoundException | NoSuchMethodException unused) {
                    this.f2104c = null;
                    this.f2105d = null;
                    this.f2106e = false;
                }
            }
            try {
                if (this.f2106e && this.f2104c.isInstance(menu)) {
                    declaredMethod = this.f2105d;
                } else {
                    declaredMethod = menu.getClass().getDeclaredMethod("removeItemAt", Integer.TYPE);
                }
                for (int size = menu.size() - 1; size >= 0; size--) {
                    MenuItem item = menu.getItem(size);
                    if (item.getIntent() != null && "android.intent.action.PROCESS_TEXT".equals(item.getIntent().getAction())) {
                        declaredMethod.invoke(menu, Integer.valueOf(size));
                    }
                }
                ArrayList arrayList = new ArrayList();
                if (context instanceof Activity) {
                    for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(new Intent().setAction("android.intent.action.PROCESS_TEXT").setType("text/plain"), 0)) {
                        if (!context.getPackageName().equals(resolveInfo.activityInfo.packageName)) {
                            ActivityInfo activityInfo = resolveInfo.activityInfo;
                            if (activityInfo.exported) {
                                String str = activityInfo.permission;
                                if (str != null) {
                                    checkSelfPermission = context.checkSelfPermission(str);
                                    if (checkSelfPermission == 0) {
                                    }
                                }
                            }
                        }
                        arrayList.add(resolveInfo);
                    }
                }
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    ResolveInfo resolveInfo2 = (ResolveInfo) arrayList.get(i4);
                    MenuItem add = menu.add(0, 0, i4 + 100, resolveInfo2.loadLabel(packageManager));
                    Intent type = new Intent().setAction("android.intent.action.PROCESS_TEXT").setType("text/plain");
                    if ((textView instanceof Editable) && textView.onCheckIsTextEditor() && textView.isEnabled()) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    Intent putExtra = type.putExtra("android.intent.extra.PROCESS_TEXT_READONLY", !z4);
                    ActivityInfo activityInfo2 = resolveInfo2.activityInfo;
                    add.setIntent(putExtra.setClassName(activityInfo2.packageName, activityInfo2.name)).setShowAsAction(1);
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused2) {
            }
            return this.f2102a.onPrepareActionMode(actionMode, menu);
        }
    }

    public static f.a a(TextView textView) {
        int i4;
        int i5;
        TextDirectionHeuristic textDirectionHeuristic;
        int i6 = Build.VERSION.SDK_INT;
        if (i6 >= 28) {
            return new f.a(c.c(textView));
        }
        TextPaint textPaint = new TextPaint(textView.getPaint());
        boolean z4 = false;
        if (Build.VERSION.SDK_INT >= 23) {
            i4 = 1;
            i5 = 1;
        } else {
            i4 = 0;
            i5 = 0;
        }
        TextDirectionHeuristic textDirectionHeuristic2 = TextDirectionHeuristics.FIRSTSTRONG_LTR;
        if (i6 >= 23) {
            i4 = a.a(textView);
            i5 = a.d(textView);
        }
        if (textView.getTransformationMethod() instanceof PasswordTransformationMethod) {
            textDirectionHeuristic = TextDirectionHeuristics.LTR;
        } else if (i6 >= 28 && (textView.getInputType() & 15) == 3) {
            byte directionality = Character.getDirectionality(c.b(b.a(textView.getTextLocale()))[0].codePointAt(0));
            if (directionality != 1 && directionality != 2) {
                textDirectionHeuristic = TextDirectionHeuristics.LTR;
            } else {
                textDirectionHeuristic = TextDirectionHeuristics.RTL;
            }
        } else {
            if (textView.getLayoutDirection() == 1) {
                z4 = true;
            }
            switch (textView.getTextDirection()) {
                case 2:
                    textDirectionHeuristic = TextDirectionHeuristics.ANYRTL_LTR;
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    textDirectionHeuristic = TextDirectionHeuristics.LTR;
                    break;
                case 4:
                    textDirectionHeuristic = TextDirectionHeuristics.RTL;
                    break;
                case 5:
                    textDirectionHeuristic = TextDirectionHeuristics.LOCALE;
                    break;
                case 6:
                    textDirectionHeuristic = TextDirectionHeuristics.FIRSTSTRONG_LTR;
                    break;
                case 7:
                    textDirectionHeuristic = TextDirectionHeuristics.FIRSTSTRONG_RTL;
                    break;
                default:
                    if (z4) {
                        textDirectionHeuristic = TextDirectionHeuristics.FIRSTSTRONG_RTL;
                        break;
                    } else {
                        textDirectionHeuristic = TextDirectionHeuristics.FIRSTSTRONG_LTR;
                        break;
                    }
            }
        }
        return new f.a(textPaint, textDirectionHeuristic, i4, i5);
    }

    public static void b(TextView textView, int i4) {
        int i5;
        C0149c.d(i4);
        if (Build.VERSION.SDK_INT >= 28) {
            c.d(textView, i4);
            return;
        }
        Paint.FontMetricsInt fontMetricsInt = textView.getPaint().getFontMetricsInt();
        if (textView.getIncludeFontPadding()) {
            i5 = fontMetricsInt.top;
        } else {
            i5 = fontMetricsInt.ascent;
        }
        if (i4 > Math.abs(i5)) {
            textView.setPadding(textView.getPaddingLeft(), i4 + i5, textView.getPaddingRight(), textView.getPaddingBottom());
        }
    }

    public static void c(TextView textView, int i4) {
        int i5;
        C0149c.d(i4);
        Paint.FontMetricsInt fontMetricsInt = textView.getPaint().getFontMetricsInt();
        if (textView.getIncludeFontPadding()) {
            i5 = fontMetricsInt.bottom;
        } else {
            i5 = fontMetricsInt.descent;
        }
        if (i4 > Math.abs(i5)) {
            textView.setPadding(textView.getPaddingLeft(), textView.getPaddingTop(), textView.getPaddingRight(), i4 - i5);
        }
    }

    public static void d(TextView textView, int i4) {
        C0149c.d(i4);
        int fontMetricsInt = textView.getPaint().getFontMetricsInt(null);
        if (i4 != fontMetricsInt) {
            textView.setLineSpacing(i4 - fontMetricsInt, 1.0f);
        }
    }

    public static void e(TextView textView, K.f fVar) {
        if (Build.VERSION.SDK_INT >= 29) {
            fVar.getClass();
            textView.setText(c.a(null));
            return;
        }
        f.a a4 = a(textView);
        fVar.getClass();
        if (a4.a(null)) {
            textView.setText(fVar);
            return;
        }
        throw new IllegalArgumentException("Given text can not be applied to TextView.");
    }

    public static void f(TextView textView, int i4) {
        if (Build.VERSION.SDK_INT >= 23) {
            textView.setTextAppearance(i4);
        } else {
            textView.setTextAppearance(textView.getContext(), i4);
        }
    }

    public static ActionMode.Callback g(ActionMode.Callback callback) {
        if ((callback instanceof e) && Build.VERSION.SDK_INT >= 26) {
            return ((e) callback).f2102a;
        }
        return callback;
    }

    public static ActionMode.Callback h(ActionMode.Callback callback, TextView textView) {
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 26 && i4 <= 27 && !(callback instanceof e) && callback != null) {
            return new e(callback, textView);
        }
        return callback;
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class c {
        public static String[] b(DecimalFormatSymbols decimalFormatSymbols) {
            return decimalFormatSymbols.getDigitStrings();
        }

        public static PrecomputedText.Params c(TextView textView) {
            return textView.getTextMetricsParams();
        }

        public static void d(TextView textView, int i4) {
            textView.setFirstBaselineToTopHeight(i4);
        }

        public static CharSequence a(PrecomputedText precomputedText) {
            return precomputedText;
        }
    }
}
