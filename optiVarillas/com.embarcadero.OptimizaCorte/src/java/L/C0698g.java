package l;

import A1.P0;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.ActionMode;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.CheckedTextView;

/* renamed from: l.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0698g extends CheckedTextView implements S.j {

    /* renamed from: j  reason: collision with root package name */
    public final C0699h f5137j;

    /* renamed from: k  reason: collision with root package name */
    public final C0695d f5138k;

    /* renamed from: l  reason: collision with root package name */
    public final C0688A f5139l;

    /* renamed from: m  reason: collision with root package name */
    public C0703l f5140m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0080 A[Catch: all -> 0x0060, TryCatch #0 {all -> 0x0060, blocks: (B:3:0x0048, B:5:0x004e, B:7:0x0054, B:16:0x0079, B:18:0x0080, B:19:0x0087, B:21:0x008e, B:11:0x0062, B:13:0x0068, B:15:0x006e), top: B:27:0x0048 }] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x008e A[Catch: all -> 0x0060, TRY_LEAVE, TryCatch #0 {all -> 0x0060, blocks: (B:3:0x0048, B:5:0x004e, B:7:0x0054, B:16:0x0079, B:18:0x0080, B:19:0x0087, B:21:0x008e, B:11:0x0062, B:13:0x0068, B:15:0x006e), top: B:27:0x0048 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public C0698g(android.content.Context r10, android.util.AttributeSet r11) {
        /*
            r9 = this;
            l.Y.a(r10)
            r6 = 2130903216(0x7f0300b0, float:1.7413244E38)
            r9.<init>(r10, r11, r6)
            android.content.Context r10 = r9.getContext()
            l.W.a(r10, r9)
            l.A r10 = new l.A
            r10.<init>(r9)
            r9.f5139l = r10
            r10.f(r11, r6)
            r10.b()
            l.d r10 = new l.d
            r10.<init>(r9)
            r9.f5138k = r10
            r10.d(r11, r6)
            l.h r10 = new l.h
            r10.<init>(r9)
            r9.f5137j = r10
            android.content.Context r10 = r9.getContext()
            int[] r2 = d.C0376a.f3139l
            r7 = 0
            l.b0 r10 = l.b0.e(r10, r11, r2, r6, r7)
            android.content.res.TypedArray r8 = r10.f5104b
            android.content.Context r1 = r9.getContext()
            android.content.res.TypedArray r4 = r10.f5104b
            r0 = r9
            r3 = r11
            r5 = r6
            M.O.o(r0, r1, r2, r3, r4, r5)
            r0 = 1
            boolean r1 = r8.hasValue(r0)     // Catch: java.lang.Throwable -> L60
            if (r1 == 0) goto L62
            int r0 = r8.getResourceId(r0, r7)     // Catch: java.lang.Throwable -> L60
            if (r0 == 0) goto L62
            android.content.Context r1 = r9.getContext()     // Catch: java.lang.Throwable -> L60 android.content.res.Resources.NotFoundException -> L62
            android.graphics.drawable.Drawable r0 = B2.a.f(r1, r0)     // Catch: java.lang.Throwable -> L60 android.content.res.Resources.NotFoundException -> L62
            r9.setCheckMarkDrawable(r0)     // Catch: java.lang.Throwable -> L60 android.content.res.Resources.NotFoundException -> L62
            goto L79
        L60:
            r11 = move-exception
            goto La6
        L62:
            boolean r0 = r8.hasValue(r7)     // Catch: java.lang.Throwable -> L60
            if (r0 == 0) goto L79
            int r0 = r8.getResourceId(r7, r7)     // Catch: java.lang.Throwable -> L60
            if (r0 == 0) goto L79
            android.content.Context r1 = r9.getContext()     // Catch: java.lang.Throwable -> L60
            android.graphics.drawable.Drawable r0 = B2.a.f(r1, r0)     // Catch: java.lang.Throwable -> L60
            r9.setCheckMarkDrawable(r0)     // Catch: java.lang.Throwable -> L60
        L79:
            r0 = 2
            boolean r1 = r8.hasValue(r0)     // Catch: java.lang.Throwable -> L60
            if (r1 == 0) goto L87
            android.content.res.ColorStateList r0 = r10.a(r0)     // Catch: java.lang.Throwable -> L60
            r9.setCheckMarkTintList(r0)     // Catch: java.lang.Throwable -> L60
        L87:
            r0 = 3
            boolean r1 = r8.hasValue(r0)     // Catch: java.lang.Throwable -> L60
            if (r1 == 0) goto L9b
            r1 = -1
            int r0 = r8.getInt(r0, r1)     // Catch: java.lang.Throwable -> L60
            r1 = 0
            android.graphics.PorterDuff$Mode r0 = l.G.c(r0, r1)     // Catch: java.lang.Throwable -> L60
            r9.setCheckMarkTintMode(r0)     // Catch: java.lang.Throwable -> L60
        L9b:
            r10.f()
            l.l r10 = r9.getEmojiTextViewHelper()
            r10.b(r11, r6)
            return
        La6:
            r10.f()
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: l.C0698g.<init>(android.content.Context, android.util.AttributeSet):void");
    }

    private C0703l getEmojiTextViewHelper() {
        if (this.f5140m == null) {
            this.f5140m = new C0703l(this);
        }
        return this.f5140m;
    }

    @Override // android.widget.CheckedTextView, android.widget.TextView, android.view.View
    public final void drawableStateChanged() {
        super.drawableStateChanged();
        C0688A c0688a = this.f5139l;
        if (c0688a != null) {
            c0688a.b();
        }
        C0695d c0695d = this.f5138k;
        if (c0695d != null) {
            c0695d.a();
        }
        C0699h c0699h = this.f5137j;
        if (c0699h != null) {
            c0699h.a();
        }
    }

    @Override // android.widget.TextView
    public ActionMode.Callback getCustomSelectionActionModeCallback() {
        return S.g.g(super.getCustomSelectionActionModeCallback());
    }

    public ColorStateList getSupportBackgroundTintList() {
        C0695d c0695d = this.f5138k;
        if (c0695d != null) {
            return c0695d.b();
        }
        return null;
    }

    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0695d c0695d = this.f5138k;
        if (c0695d != null) {
            return c0695d.c();
        }
        return null;
    }

    public ColorStateList getSupportCheckMarkTintList() {
        C0699h c0699h = this.f5137j;
        if (c0699h != null) {
            return c0699h.f5148b;
        }
        return null;
    }

    public PorterDuff.Mode getSupportCheckMarkTintMode() {
        C0699h c0699h = this.f5137j;
        if (c0699h != null) {
            return c0699h.f5149c;
        }
        return null;
    }

    public ColorStateList getSupportCompoundDrawablesTintList() {
        return this.f5139l.d();
    }

    public PorterDuff.Mode getSupportCompoundDrawablesTintMode() {
        return this.f5139l.e();
    }

    @Override // android.widget.TextView, android.view.View
    public final InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
        P0.c(onCreateInputConnection, editorInfo, this);
        return onCreateInputConnection;
    }

    @Override // android.widget.TextView
    public void setAllCaps(boolean z4) {
        super.setAllCaps(z4);
        getEmojiTextViewHelper().c(z4);
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0695d c0695d = this.f5138k;
        if (c0695d != null) {
            c0695d.e();
        }
    }

    @Override // android.view.View
    public void setBackgroundResource(int i4) {
        super.setBackgroundResource(i4);
        C0695d c0695d = this.f5138k;
        if (c0695d != null) {
            c0695d.f(i4);
        }
    }

    @Override // android.widget.CheckedTextView
    public void setCheckMarkDrawable(Drawable drawable) {
        super.setCheckMarkDrawable(drawable);
        C0699h c0699h = this.f5137j;
        if (c0699h != null) {
            if (c0699h.f) {
                c0699h.f = false;
                return;
            }
            c0699h.f = true;
            c0699h.a();
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f5139l;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawablesRelative(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f5139l;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        super.setCustomSelectionActionModeCallback(S.g.h(callback, this));
    }

    public void setEmojiCompatEnabled(boolean z4) {
        getEmojiTextViewHelper().d(z4);
    }

    public void setSupportBackgroundTintList(ColorStateList colorStateList) {
        C0695d c0695d = this.f5138k;
        if (c0695d != null) {
            c0695d.h(colorStateList);
        }
    }

    public void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        C0695d c0695d = this.f5138k;
        if (c0695d != null) {
            c0695d.i(mode);
        }
    }

    public void setSupportCheckMarkTintList(ColorStateList colorStateList) {
        C0699h c0699h = this.f5137j;
        if (c0699h != null) {
            c0699h.f5148b = colorStateList;
            c0699h.f5150d = true;
            c0699h.a();
        }
    }

    public void setSupportCheckMarkTintMode(PorterDuff.Mode mode) {
        C0699h c0699h = this.f5137j;
        if (c0699h != null) {
            c0699h.f5149c = mode;
            c0699h.f5151e = true;
            c0699h.a();
        }
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintList(ColorStateList colorStateList) {
        C0688A c0688a = this.f5139l;
        c0688a.l(colorStateList);
        c0688a.b();
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintMode(PorterDuff.Mode mode) {
        C0688A c0688a = this.f5139l;
        c0688a.m(mode);
        c0688a.b();
    }

    @Override // android.widget.TextView
    public final void setTextAppearance(Context context, int i4) {
        super.setTextAppearance(context, i4);
        C0688A c0688a = this.f5139l;
        if (c0688a != null) {
            c0688a.g(context, i4);
        }
    }

    @Override // android.widget.CheckedTextView
    public void setCheckMarkDrawable(int i4) {
        setCheckMarkDrawable(B2.a.f(getContext(), i4));
    }
}
