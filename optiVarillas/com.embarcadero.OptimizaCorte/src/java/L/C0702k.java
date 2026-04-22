package l;

import A1.C0117l0;
import M.C0226h;
import M.InterfaceC0243z;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.TextClassifier;
import android.widget.EditText;
import l.C0716z;

/* renamed from: l.k  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class C0702k extends EditText implements InterfaceC0243z, S.j {

    /* renamed from: j  reason: collision with root package name */
    public final C0695d f5169j;

    /* renamed from: k  reason: collision with root package name */
    public final C0688A f5170k;

    /* renamed from: l  reason: collision with root package name */
    public final C0716z f5171l;

    /* renamed from: m  reason: collision with root package name */
    public final S.h f5172m;

    /* renamed from: n  reason: collision with root package name */
    public final C0117l0 f5173n;

    /* renamed from: o  reason: collision with root package name */
    public a f5174o;

    /* renamed from: l.k$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class a {
        public a() {
        }
    }

    public C0702k(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    private a getSuperCaller() {
        if (this.f5174o == null) {
            this.f5174o = new a();
        }
        return this.f5174o;
    }

    @Override // M.InterfaceC0243z
    public final C0226h a(C0226h c0226h) {
        return this.f5172m.a(this, c0226h);
    }

    @Override // android.widget.TextView, android.view.View
    public final void drawableStateChanged() {
        super.drawableStateChanged();
        C0695d c0695d = this.f5169j;
        if (c0695d != null) {
            c0695d.a();
        }
        C0688A c0688a = this.f5170k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public ActionMode.Callback getCustomSelectionActionModeCallback() {
        return S.g.g(super.getCustomSelectionActionModeCallback());
    }

    public ColorStateList getSupportBackgroundTintList() {
        C0695d c0695d = this.f5169j;
        if (c0695d != null) {
            return c0695d.b();
        }
        return null;
    }

    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0695d c0695d = this.f5169j;
        if (c0695d != null) {
            return c0695d.c();
        }
        return null;
    }

    public ColorStateList getSupportCompoundDrawablesTintList() {
        return this.f5170k.d();
    }

    public PorterDuff.Mode getSupportCompoundDrawablesTintMode() {
        return this.f5170k.e();
    }

    @Override // android.widget.TextView
    public TextClassifier getTextClassifier() {
        C0716z c0716z;
        if (Build.VERSION.SDK_INT < 28 && (c0716z = this.f5171l) != null) {
            TextClassifier textClassifier = c0716z.f5235b;
            if (textClassifier == null) {
                return C0716z.a.a(c0716z.f5234a);
            }
            return textClassifier;
        }
        return super.getTextClassifier();
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x0054, code lost:
        if (r1 != null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0056, code lost:
        r6 = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0069, code lost:
        if (r1 != null) goto L18;
     */
    @Override // android.widget.TextView, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.view.inputmethod.InputConnection onCreateInputConnection(android.view.inputmethod.EditorInfo r8) {
        /*
            r7 = this;
            android.view.inputmethod.InputConnection r0 = super.onCreateInputConnection(r8)
            l.A r1 = r7.f5170k
            r1.getClass()
            l.C0688A.h(r7, r0, r8)
            A1.P0.c(r0, r8, r7)
            if (r0 == 0) goto L76
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 30
            if (r1 > r2) goto L76
            java.lang.String[] r2 = M.O.g(r7)
            if (r2 == 0) goto L76
            java.lang.String r3 = "android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES"
            java.lang.String r4 = "androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES"
            r5 = 25
            if (r1 < r5) goto L29
            R.a.a(r8, r2)
            goto L3e
        L29:
            android.os.Bundle r6 = r8.extras
            if (r6 != 0) goto L34
            android.os.Bundle r6 = new android.os.Bundle
            r6.<init>()
            r8.extras = r6
        L34:
            android.os.Bundle r6 = r8.extras
            r6.putStringArray(r4, r2)
            android.os.Bundle r6 = r8.extras
            r6.putStringArray(r3, r2)
        L3e:
            R.d r2 = new R.d
            r2.<init>(r7)
            if (r1 < r5) goto L4c
            R.e r1 = new R.e
            r1.<init>(r0, r2)
        L4a:
            r0 = r1
            goto L76
        L4c:
            java.lang.String[] r6 = R.c.f2029a
            if (r1 < r5) goto L58
            java.lang.String[] r1 = R.b.a(r8)
            if (r1 == 0) goto L6c
        L56:
            r6 = r1
            goto L6c
        L58:
            android.os.Bundle r1 = r8.extras
            if (r1 != 0) goto L5d
            goto L6c
        L5d:
            java.lang.String[] r1 = r1.getStringArray(r4)
            if (r1 != 0) goto L69
            android.os.Bundle r1 = r8.extras
            java.lang.String[] r1 = r1.getStringArray(r3)
        L69:
            if (r1 == 0) goto L6c
            goto L56
        L6c:
            int r1 = r6.length
            if (r1 != 0) goto L70
            goto L76
        L70:
            R.f r1 = new R.f
            r1.<init>(r0, r2)
            goto L4a
        L76:
            A1.l0 r1 = r7.f5173n
            X.c r8 = r1.k(r0, r8)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: l.C0702k.onCreateInputConnection(android.view.inputmethod.EditorInfo):android.view.inputmethod.InputConnection");
    }

    @Override // android.view.View
    public final void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 30 && i4 < 33) {
            ((InputMethodManager) getContext().getSystemService("input_method")).isActive(this);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public final boolean onDragEvent(DragEvent dragEvent) {
        Activity activity;
        int i4 = Build.VERSION.SDK_INT;
        boolean z4 = false;
        if (i4 < 31 && i4 >= 24 && dragEvent.getLocalState() == null && M.O.g(this) != null) {
            Context context = getContext();
            while (true) {
                if (context instanceof ContextWrapper) {
                    if (context instanceof Activity) {
                        activity = (Activity) context;
                        break;
                    }
                    context = ((ContextWrapper) context).getBaseContext();
                } else {
                    activity = null;
                    break;
                }
            }
            if (activity == null) {
                Log.i("ReceiveContent", "Can't handle drop: no activity: view=" + this);
            } else if (dragEvent.getAction() != 1 && dragEvent.getAction() == 3) {
                z4 = C0711u.a(dragEvent, this, activity);
            }
        }
        if (z4) {
            return true;
        }
        return super.onDragEvent(dragEvent);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [M.h$c, java.lang.Object] */
    @Override // android.widget.TextView
    public final boolean onTextContextMenuItem(int i4) {
        ClipData primaryClip;
        C0226h.a aVar;
        int i5;
        int i6 = Build.VERSION.SDK_INT;
        if (i6 < 31 && M.O.g(this) != null && (i4 == 16908322 || i4 == 16908337)) {
            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService("clipboard");
            if (clipboardManager == null) {
                primaryClip = null;
            } else {
                primaryClip = clipboardManager.getPrimaryClip();
            }
            if (primaryClip != null && primaryClip.getItemCount() > 0) {
                if (i6 >= 31) {
                    aVar = new C0226h.a(primaryClip, 1);
                } else {
                    ?? obj = new Object();
                    obj.f1618a = primaryClip;
                    obj.f1619b = 1;
                    aVar = obj;
                }
                if (i4 == 16908322) {
                    i5 = 0;
                } else {
                    i5 = 1;
                }
                aVar.d(i5);
                M.O.l(this, aVar.a());
            }
            return true;
        }
        return super.onTextContextMenuItem(i4);
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0695d c0695d = this.f5169j;
        if (c0695d != null) {
            c0695d.e();
        }
    }

    @Override // android.view.View
    public void setBackgroundResource(int i4) {
        super.setBackgroundResource(i4);
        C0695d c0695d = this.f5169j;
        if (c0695d != null) {
            c0695d.f(i4);
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f5170k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawablesRelative(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f5170k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        super.setCustomSelectionActionModeCallback(S.g.h(callback, this));
    }

    public void setEmojiCompatEnabled(boolean z4) {
        this.f5173n.l(z4);
    }

    @Override // android.widget.TextView
    public void setKeyListener(KeyListener keyListener) {
        super.setKeyListener(this.f5173n.i(keyListener));
    }

    public void setSupportBackgroundTintList(ColorStateList colorStateList) {
        C0695d c0695d = this.f5169j;
        if (c0695d != null) {
            c0695d.h(colorStateList);
        }
    }

    public void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        C0695d c0695d = this.f5169j;
        if (c0695d != null) {
            c0695d.i(mode);
        }
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintList(ColorStateList colorStateList) {
        C0688A c0688a = this.f5170k;
        c0688a.l(colorStateList);
        c0688a.b();
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintMode(PorterDuff.Mode mode) {
        C0688A c0688a = this.f5170k;
        c0688a.m(mode);
        c0688a.b();
    }

    @Override // android.widget.TextView
    public final void setTextAppearance(Context context, int i4) {
        super.setTextAppearance(context, i4);
        C0688A c0688a = this.f5170k;
        if (c0688a != null) {
            c0688a.g(context, i4);
        }
    }

    @Override // android.widget.TextView
    public void setTextClassifier(TextClassifier textClassifier) {
        C0716z c0716z;
        if (Build.VERSION.SDK_INT < 28 && (c0716z = this.f5171l) != null) {
            c0716z.f5235b = textClassifier;
        } else {
            super.setTextClassifier(textClassifier);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Type inference failed for: r4v4, types: [l.z, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r4v5, types: [java.lang.Object, S.h] */
    public C0702k(Context context, AttributeSet attributeSet, int i4) {
        super(context, attributeSet, 2130903431);
        Y.a(context);
        W.a(getContext(), this);
        C0695d c0695d = new C0695d(this);
        this.f5169j = c0695d;
        c0695d.d(attributeSet, 2130903431);
        C0688A c0688a = new C0688A(this);
        this.f5170k = c0688a;
        c0688a.f(attributeSet, 2130903431);
        c0688a.b();
        ?? obj = new Object();
        obj.f5234a = this;
        this.f5171l = obj;
        this.f5172m = new Object();
        C0117l0 c0117l0 = new C0117l0(this);
        this.f5173n = c0117l0;
        c0117l0.j(attributeSet, 2130903431);
        KeyListener keyListener = getKeyListener();
        if (!(keyListener instanceof NumberKeyListener)) {
            boolean isFocusable = super.isFocusable();
            boolean isClickable = super.isClickable();
            boolean isLongClickable = super.isLongClickable();
            int inputType = super.getInputType();
            KeyListener i5 = c0117l0.i(keyListener);
            if (i5 == keyListener) {
                return;
            }
            super.setKeyListener(i5);
            super.setRawInputType(inputType);
            super.setFocusable(isFocusable);
            super.setClickable(isClickable);
            super.setLongClickable(isLongClickable);
        }
    }

    @Override // android.widget.EditText, android.widget.TextView
    public Editable getText() {
        if (Build.VERSION.SDK_INT >= 28) {
            return super.getText();
        }
        return super.getEditableText();
    }
}
