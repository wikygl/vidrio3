package l;

import F.a;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.CompoundButton;

/* renamed from: l.i  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0700i {

    /* renamed from: a  reason: collision with root package name */
    public final CompoundButton f5153a;

    /* renamed from: b  reason: collision with root package name */
    public ColorStateList f5154b = null;

    /* renamed from: c  reason: collision with root package name */
    public PorterDuff.Mode f5155c = null;

    /* renamed from: d  reason: collision with root package name */
    public boolean f5156d = false;

    /* renamed from: e  reason: collision with root package name */
    public boolean f5157e = false;
    public boolean f;

    public C0700i(CompoundButton compoundButton) {
        this.f5153a = compoundButton;
    }

    public final void a() {
        CompoundButton compoundButton = this.f5153a;
        Drawable a4 = S.b.a(compoundButton);
        if (a4 != null) {
            if (this.f5156d || this.f5157e) {
                Drawable mutate = F.a.g(a4).mutate();
                if (this.f5156d) {
                    a.C0006a.h(mutate, this.f5154b);
                }
                if (this.f5157e) {
                    a.C0006a.i(mutate, this.f5155c);
                }
                if (mutate.isStateful()) {
                    mutate.setState(compoundButton.getDrawableState());
                }
                compoundButton.setButtonDrawable(mutate);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0054 A[Catch: all -> 0x0034, TryCatch #1 {all -> 0x0034, blocks: (B:3:0x001c, B:5:0x0022, B:7:0x0028, B:16:0x004d, B:18:0x0054, B:19:0x005b, B:21:0x0062, B:11:0x0036, B:13:0x003c, B:15:0x0042), top: B:29:0x001c }] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0062 A[Catch: all -> 0x0034, TRY_LEAVE, TryCatch #1 {all -> 0x0034, blocks: (B:3:0x001c, B:5:0x0022, B:7:0x0028, B:16:0x004d, B:18:0x0054, B:19:0x005b, B:21:0x0062, B:11:0x0036, B:13:0x003c, B:15:0x0042), top: B:29:0x001c }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void b(android.util.AttributeSet r11, int r12) {
        /*
            r10 = this;
            android.widget.CompoundButton r6 = r10.f5153a
            android.content.Context r0 = r6.getContext()
            int[] r2 = d.C0376a.f3140m
            r7 = 0
            l.b0 r8 = l.b0.e(r0, r11, r2, r12, r7)
            android.content.res.TypedArray r9 = r8.f5104b
            android.content.Context r1 = r6.getContext()
            android.content.res.TypedArray r4 = r8.f5104b
            r0 = r6
            r3 = r11
            r5 = r12
            M.O.o(r0, r1, r2, r3, r4, r5)
            r11 = 1
            boolean r12 = r9.hasValue(r11)     // Catch: java.lang.Throwable -> L34
            if (r12 == 0) goto L36
            int r11 = r9.getResourceId(r11, r7)     // Catch: java.lang.Throwable -> L34
            if (r11 == 0) goto L36
            android.content.Context r12 = r6.getContext()     // Catch: java.lang.Throwable -> L34 android.content.res.Resources.NotFoundException -> L36
            android.graphics.drawable.Drawable r11 = B2.a.f(r12, r11)     // Catch: java.lang.Throwable -> L34 android.content.res.Resources.NotFoundException -> L36
            r6.setButtonDrawable(r11)     // Catch: java.lang.Throwable -> L34 android.content.res.Resources.NotFoundException -> L36
            goto L4d
        L34:
            r11 = move-exception
            goto L73
        L36:
            boolean r11 = r9.hasValue(r7)     // Catch: java.lang.Throwable -> L34
            if (r11 == 0) goto L4d
            int r11 = r9.getResourceId(r7, r7)     // Catch: java.lang.Throwable -> L34
            if (r11 == 0) goto L4d
            android.content.Context r12 = r6.getContext()     // Catch: java.lang.Throwable -> L34
            android.graphics.drawable.Drawable r11 = B2.a.f(r12, r11)     // Catch: java.lang.Throwable -> L34
            r6.setButtonDrawable(r11)     // Catch: java.lang.Throwable -> L34
        L4d:
            r11 = 2
            boolean r12 = r9.hasValue(r11)     // Catch: java.lang.Throwable -> L34
            if (r12 == 0) goto L5b
            android.content.res.ColorStateList r11 = r8.a(r11)     // Catch: java.lang.Throwable -> L34
            S.b.a.c(r6, r11)     // Catch: java.lang.Throwable -> L34
        L5b:
            r11 = 3
            boolean r12 = r9.hasValue(r11)     // Catch: java.lang.Throwable -> L34
            if (r12 == 0) goto L6f
            r12 = -1
            int r11 = r9.getInt(r11, r12)     // Catch: java.lang.Throwable -> L34
            r12 = 0
            android.graphics.PorterDuff$Mode r11 = l.G.c(r11, r12)     // Catch: java.lang.Throwable -> L34
            S.b.a.d(r6, r11)     // Catch: java.lang.Throwable -> L34
        L6f:
            r8.f()
            return
        L73:
            r8.f()
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: l.C0700i.b(android.util.AttributeSet, int):void");
    }
}
