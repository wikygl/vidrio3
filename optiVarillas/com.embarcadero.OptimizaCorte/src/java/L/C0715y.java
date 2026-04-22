package l;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ThemedSpinnerAdapter;
import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.b;
import j$.util.Objects;

/* renamed from: l.y  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0715y extends Spinner {
    @SuppressLint({"ResourceType"})

    /* renamed from: r  reason: collision with root package name */
    public static final int[] f5208r = {16843505};

    /* renamed from: j  reason: collision with root package name */
    public final C0695d f5209j;

    /* renamed from: k  reason: collision with root package name */
    public final Context f5210k;

    /* renamed from: l  reason: collision with root package name */
    public final C0714x f5211l;

    /* renamed from: m  reason: collision with root package name */
    public SpinnerAdapter f5212m;

    /* renamed from: n  reason: collision with root package name */
    public final boolean f5213n;

    /* renamed from: o  reason: collision with root package name */
    public final g f5214o;

    /* renamed from: p  reason: collision with root package name */
    public int f5215p;

    /* renamed from: q  reason: collision with root package name */
    public final Rect f5216q;

    /* renamed from: l.y$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class a implements ViewTreeObserver.OnGlobalLayoutListener {
        public a() {
        }

        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public final void onGlobalLayout() {
            C0715y c0715y = C0715y.this;
            if (!c0715y.getInternalPopup().c()) {
                c0715y.f5214o.l(c0715y.getTextDirection(), c0715y.getTextAlignment());
            }
            ViewTreeObserver viewTreeObserver = c0715y.getViewTreeObserver();
            if (viewTreeObserver != null) {
                viewTreeObserver.removeOnGlobalLayoutListener(this);
            }
        }
    }

    /* renamed from: l.y$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class b {
        public static void a(ThemedSpinnerAdapter themedSpinnerAdapter, Resources.Theme theme) {
            if (!Objects.equals(themedSpinnerAdapter.getDropDownViewTheme(), theme)) {
                themedSpinnerAdapter.setDropDownViewTheme(theme);
            }
        }
    }

    /* renamed from: l.y$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class c implements g, DialogInterface.OnClickListener {

        /* renamed from: j  reason: collision with root package name */
        public androidx.appcompat.app.b f5218j;

        /* renamed from: k  reason: collision with root package name */
        public ListAdapter f5219k;

        /* renamed from: l  reason: collision with root package name */
        public CharSequence f5220l;

        public c() {
        }

        @Override // l.C0715y.g
        public final int b() {
            return 0;
        }

        @Override // l.C0715y.g
        public final boolean c() {
            androidx.appcompat.app.b bVar = this.f5218j;
            if (bVar != null) {
                return bVar.isShowing();
            }
            return false;
        }

        @Override // l.C0715y.g
        public final void dismiss() {
            androidx.appcompat.app.b bVar = this.f5218j;
            if (bVar != null) {
                bVar.dismiss();
                this.f5218j = null;
            }
        }

        @Override // l.C0715y.g
        public final Drawable e() {
            return null;
        }

        @Override // l.C0715y.g
        public final void f(CharSequence charSequence) {
            this.f5220l = charSequence;
        }

        @Override // l.C0715y.g
        public final void h(Drawable drawable) {
            Log.e("AppCompatSpinner", "Cannot set popup background for MODE_DIALOG, ignoring");
        }

        @Override // l.C0715y.g
        public final void i(int i4) {
            Log.e("AppCompatSpinner", "Cannot set vertical offset for MODE_DIALOG, ignoring");
        }

        @Override // l.C0715y.g
        public final void j(int i4) {
            Log.e("AppCompatSpinner", "Cannot set horizontal (original) offset for MODE_DIALOG, ignoring");
        }

        @Override // l.C0715y.g
        public final void k(int i4) {
            Log.e("AppCompatSpinner", "Cannot set horizontal offset for MODE_DIALOG, ignoring");
        }

        @Override // l.C0715y.g
        public final void l(int i4, int i5) {
            if (this.f5219k == null) {
                return;
            }
            C0715y c0715y = C0715y.this;
            b.a aVar = new b.a(c0715y.getPopupContext());
            CharSequence charSequence = this.f5220l;
            AlertController.b bVar = aVar.a;
            if (charSequence != null) {
                bVar.d = charSequence;
            }
            ListAdapter listAdapter = this.f5219k;
            int selectedItemPosition = c0715y.getSelectedItemPosition();
            bVar.o = listAdapter;
            bVar.p = this;
            bVar.s = selectedItemPosition;
            bVar.r = true;
            androidx.appcompat.app.b a4 = aVar.a();
            this.f5218j = a4;
            AlertController.RecycleListView recycleListView = a4.o.g;
            recycleListView.setTextDirection(i4);
            recycleListView.setTextAlignment(i5);
            this.f5218j.show();
        }

        @Override // l.C0715y.g
        public final int m() {
            return 0;
        }

        @Override // l.C0715y.g
        public final CharSequence o() {
            return this.f5220l;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public final void onClick(DialogInterface dialogInterface, int i4) {
            C0715y c0715y = C0715y.this;
            c0715y.setSelection(i4);
            if (c0715y.getOnItemClickListener() != null) {
                c0715y.performItemClick(null, i4, this.f5219k.getItemId(i4));
            }
            dismiss();
        }

        @Override // l.C0715y.g
        public final void p(ListAdapter listAdapter) {
            this.f5219k = listAdapter;
        }
    }

    /* renamed from: l.y$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class d implements ListAdapter, SpinnerAdapter {

        /* renamed from: j  reason: collision with root package name */
        public SpinnerAdapter f5222j;

        /* renamed from: k  reason: collision with root package name */
        public ListAdapter f5223k;

        @Override // android.widget.ListAdapter
        public final boolean areAllItemsEnabled() {
            ListAdapter listAdapter = this.f5223k;
            if (listAdapter != null) {
                return listAdapter.areAllItemsEnabled();
            }
            return true;
        }

        @Override // android.widget.Adapter
        public final int getCount() {
            SpinnerAdapter spinnerAdapter = this.f5222j;
            if (spinnerAdapter == null) {
                return 0;
            }
            return spinnerAdapter.getCount();
        }

        @Override // android.widget.SpinnerAdapter
        public final View getDropDownView(int i4, View view, ViewGroup viewGroup) {
            SpinnerAdapter spinnerAdapter = this.f5222j;
            if (spinnerAdapter == null) {
                return null;
            }
            return spinnerAdapter.getDropDownView(i4, view, viewGroup);
        }

        @Override // android.widget.Adapter
        public final Object getItem(int i4) {
            SpinnerAdapter spinnerAdapter = this.f5222j;
            if (spinnerAdapter == null) {
                return null;
            }
            return spinnerAdapter.getItem(i4);
        }

        @Override // android.widget.Adapter
        public final long getItemId(int i4) {
            SpinnerAdapter spinnerAdapter = this.f5222j;
            if (spinnerAdapter == null) {
                return -1L;
            }
            return spinnerAdapter.getItemId(i4);
        }

        @Override // android.widget.Adapter
        public final int getItemViewType(int i4) {
            return 0;
        }

        @Override // android.widget.Adapter
        public final View getView(int i4, View view, ViewGroup viewGroup) {
            return getDropDownView(i4, view, viewGroup);
        }

        @Override // android.widget.Adapter
        public final int getViewTypeCount() {
            return 1;
        }

        @Override // android.widget.Adapter
        public final boolean hasStableIds() {
            SpinnerAdapter spinnerAdapter = this.f5222j;
            if (spinnerAdapter != null && spinnerAdapter.hasStableIds()) {
                return true;
            }
            return false;
        }

        @Override // android.widget.Adapter
        public final boolean isEmpty() {
            if (getCount() == 0) {
                return true;
            }
            return false;
        }

        @Override // android.widget.ListAdapter
        public final boolean isEnabled(int i4) {
            ListAdapter listAdapter = this.f5223k;
            if (listAdapter != null) {
                return listAdapter.isEnabled(i4);
            }
            return true;
        }

        @Override // android.widget.Adapter
        public final void registerDataSetObserver(DataSetObserver dataSetObserver) {
            SpinnerAdapter spinnerAdapter = this.f5222j;
            if (spinnerAdapter != null) {
                spinnerAdapter.registerDataSetObserver(dataSetObserver);
            }
        }

        @Override // android.widget.Adapter
        public final void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            SpinnerAdapter spinnerAdapter = this.f5222j;
            if (spinnerAdapter != null) {
                spinnerAdapter.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    /* renamed from: l.y$e */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class e extends L implements g {

        /* renamed from: M  reason: collision with root package name */
        public CharSequence f5224M;

        /* renamed from: N  reason: collision with root package name */
        public ListAdapter f5225N;

        /* renamed from: O  reason: collision with root package name */
        public final Rect f5226O;

        /* renamed from: P  reason: collision with root package name */
        public int f5227P;

        /* renamed from: l.y$e$a */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
        public class a implements AdapterView.OnItemClickListener {
            public a() {
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView<?> adapterView, View view, int i4, long j4) {
                e eVar = e.this;
                C0715y.this.setSelection(i4);
                if (C0715y.this.getOnItemClickListener() != null) {
                    C0715y.this.performItemClick(view, i4, eVar.f5225N.getItemId(i4));
                }
                eVar.dismiss();
            }
        }

        /* renamed from: l.y$e$b */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
        public class b implements ViewTreeObserver.OnGlobalLayoutListener {
            public b() {
            }

            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public final void onGlobalLayout() {
                e eVar = e.this;
                C0715y c0715y = C0715y.this;
                eVar.getClass();
                if (c0715y.isAttachedToWindow() && c0715y.getGlobalVisibleRect(eVar.f5226O)) {
                    eVar.s();
                    eVar.a();
                    return;
                }
                eVar.dismiss();
            }
        }

        /* renamed from: l.y$e$c */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
        public class c implements PopupWindow.OnDismissListener {

            /* renamed from: j  reason: collision with root package name */
            public final /* synthetic */ ViewTreeObserver.OnGlobalLayoutListener f5231j;

            public c(b bVar) {
                this.f5231j = bVar;
            }

            @Override // android.widget.PopupWindow.OnDismissListener
            public final void onDismiss() {
                ViewTreeObserver viewTreeObserver = C0715y.this.getViewTreeObserver();
                if (viewTreeObserver != null) {
                    viewTreeObserver.removeGlobalOnLayoutListener(this.f5231j);
                }
            }
        }

        public e(Context context, AttributeSet attributeSet, int i4) {
            super(context, attributeSet, i4, 0);
            this.f5226O = new Rect();
            this.f5037x = C0715y.this;
            this.f5021H = true;
            this.f5022I.setFocusable(true);
            this.f5038y = new a();
        }

        @Override // l.C0715y.g
        public final void f(CharSequence charSequence) {
            this.f5224M = charSequence;
        }

        @Override // l.C0715y.g
        public final void j(int i4) {
            this.f5227P = i4;
        }

        @Override // l.C0715y.g
        public final void l(int i4, int i5) {
            ViewTreeObserver viewTreeObserver;
            C0708q c0708q = this.f5022I;
            boolean isShowing = c0708q.isShowing();
            s();
            this.f5022I.setInputMethodMode(2);
            a();
            H h4 = this.f5025l;
            h4.setChoiceMode(1);
            h4.setTextDirection(i4);
            h4.setTextAlignment(i5);
            C0715y c0715y = C0715y.this;
            int selectedItemPosition = c0715y.getSelectedItemPosition();
            H h5 = this.f5025l;
            if (c0708q.isShowing() && h5 != null) {
                h5.setListSelectionHidden(false);
                h5.setSelection(selectedItemPosition);
                if (h5.getChoiceMode() != 0) {
                    h5.setItemChecked(selectedItemPosition, true);
                }
            }
            if (!isShowing && (viewTreeObserver = c0715y.getViewTreeObserver()) != null) {
                b bVar = new b();
                viewTreeObserver.addOnGlobalLayoutListener(bVar);
                this.f5022I.setOnDismissListener(new c(bVar));
            }
        }

        @Override // l.C0715y.g
        public final CharSequence o() {
            return this.f5224M;
        }

        @Override // l.L, l.C0715y.g
        public final void p(ListAdapter listAdapter) {
            super.p(listAdapter);
            this.f5225N = listAdapter;
        }

        public final void s() {
            int i4;
            int i5;
            C0708q c0708q = this.f5022I;
            Drawable background = c0708q.getBackground();
            C0715y c0715y = C0715y.this;
            if (background != null) {
                background.getPadding(c0715y.f5216q);
                boolean z4 = i0.f5158a;
                int layoutDirection = c0715y.getLayoutDirection();
                Rect rect = c0715y.f5216q;
                if (layoutDirection == 1) {
                    i4 = rect.right;
                } else {
                    i4 = -rect.left;
                }
            } else {
                Rect rect2 = c0715y.f5216q;
                rect2.right = 0;
                rect2.left = 0;
                i4 = 0;
            }
            int paddingLeft = c0715y.getPaddingLeft();
            int paddingRight = c0715y.getPaddingRight();
            int width = c0715y.getWidth();
            int i6 = c0715y.f5215p;
            if (i6 == -2) {
                int a4 = c0715y.a((SpinnerAdapter) this.f5225N, c0708q.getBackground());
                int i7 = c0715y.getContext().getResources().getDisplayMetrics().widthPixels;
                Rect rect3 = c0715y.f5216q;
                int i8 = (i7 - rect3.left) - rect3.right;
                if (a4 > i8) {
                    a4 = i8;
                }
                r(Math.max(a4, (width - paddingLeft) - paddingRight));
            } else if (i6 == -1) {
                r((width - paddingLeft) - paddingRight);
            } else {
                r(i6);
            }
            boolean z5 = i0.f5158a;
            if (c0715y.getLayoutDirection() == 1) {
                i5 = (((width - paddingRight) - this.f5027n) - this.f5227P) + i4;
            } else {
                i5 = paddingLeft + this.f5227P + i4;
            }
            this.f5028o = i5;
        }
    }

    /* renamed from: l.y$f */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class f extends View.BaseSavedState {
        public static final Parcelable.Creator<f> CREATOR = new Object();

        /* renamed from: j  reason: collision with root package name */
        public boolean f5233j;

        /* renamed from: l.y$f$a */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
        public class a implements Parcelable.Creator<f> {
            /* JADX WARN: Type inference failed for: r0v0, types: [android.view.View$BaseSavedState, l.y$f] */
            @Override // android.os.Parcelable.Creator
            public final f createFromParcel(Parcel parcel) {
                boolean z4;
                ?? baseSavedState = new View.BaseSavedState(parcel);
                if (parcel.readByte() != 0) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                baseSavedState.f5233j = z4;
                return baseSavedState;
            }

            @Override // android.os.Parcelable.Creator
            public final f[] newArray(int i4) {
                return new f[i4];
            }
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public final void writeToParcel(Parcel parcel, int i4) {
            super.writeToParcel(parcel, i4);
            parcel.writeByte(this.f5233j ? (byte) 1 : (byte) 0);
        }
    }

    /* renamed from: l.y$g */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public interface g {
        int b();

        boolean c();

        void dismiss();

        Drawable e();

        void f(CharSequence charSequence);

        void h(Drawable drawable);

        void i(int i4);

        void j(int i4);

        void k(int i4);

        void l(int i4, int i5);

        int m();

        CharSequence o();

        void p(ListAdapter listAdapter);
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x005b, code lost:
        if (r6 == null) goto L13;
     */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00d0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public C0715y(android.content.Context r12, android.util.AttributeSet r13, int r14) {
        /*
            r11 = this;
            r11.<init>(r12, r13, r14)
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r11.f5216q = r0
            android.content.Context r0 = r11.getContext()
            l.W.a(r0, r11)
            int[] r0 = d.C0376a.f3148u
            r1 = 0
            l.b0 r2 = l.b0.e(r12, r13, r0, r14, r1)
            l.d r3 = new l.d
            r3.<init>(r11)
            r11.f5209j = r3
            r3 = 4
            android.content.res.TypedArray r4 = r2.f5104b
            int r3 = r4.getResourceId(r3, r1)
            if (r3 == 0) goto L30
            j.c r5 = new j.c
            r5.<init>(r12, r3)
            r11.f5210k = r5
            goto L32
        L30:
            r11.f5210k = r12
        L32:
            r3 = -1
            r5 = 0
            int[] r6 = l.C0715y.f5208r     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L52
            android.content.res.TypedArray r6 = r12.obtainStyledAttributes(r13, r6, r14, r1)     // Catch: java.lang.Throwable -> L4f java.lang.Exception -> L52
            boolean r7 = r6.hasValue(r1)     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L49
            if (r7 == 0) goto L4b
            int r3 = r6.getInt(r1, r1)     // Catch: java.lang.Throwable -> L45 java.lang.Exception -> L49
            goto L4b
        L45:
            r12 = move-exception
            r5 = r6
            goto Lce
        L49:
            r7 = move-exception
            goto L54
        L4b:
            r6.recycle()
            goto L5e
        L4f:
            r12 = move-exception
            goto Lce
        L52:
            r7 = move-exception
            r6 = r5
        L54:
            java.lang.String r8 = "AppCompatSpinner"
            java.lang.String r9 = "Could not read android:spinnerMode"
            android.util.Log.i(r8, r9, r7)     // Catch: java.lang.Throwable -> L45
            if (r6 == 0) goto L5e
            goto L4b
        L5e:
            r6 = 2
            r7 = 1
            if (r3 == 0) goto L96
            if (r3 == r7) goto L65
            goto La3
        L65:
            l.y$e r3 = new l.y$e
            android.content.Context r8 = r11.f5210k
            r3.<init>(r8, r13, r14)
            android.content.Context r8 = r11.f5210k
            l.b0 r0 = l.b0.e(r8, r13, r0, r14, r1)
            android.content.res.TypedArray r8 = r0.f5104b
            r9 = 3
            r10 = -2
            int r8 = r8.getLayoutDimension(r9, r10)
            r11.f5215p = r8
            android.graphics.drawable.Drawable r8 = r0.b(r7)
            r3.h(r8)
            java.lang.String r6 = r4.getString(r6)
            r3.f5224M = r6
            r0.f()
            r11.f5214o = r3
            l.x r0 = new l.x
            r0.<init>(r11, r11, r3)
            r11.f5211l = r0
            goto La3
        L96:
            l.y$c r0 = new l.y$c
            r0.<init>()
            r11.f5214o = r0
            java.lang.String r3 = r4.getString(r6)
            r0.f5220l = r3
        La3:
            java.lang.CharSequence[] r0 = r4.getTextArray(r1)
            if (r0 == 0) goto Lba
            android.widget.ArrayAdapter r1 = new android.widget.ArrayAdapter
            r3 = 17367048(0x1090008, float:2.5162948E-38)
            r1.<init>(r12, r3, r0)
            r12 = 2131427464(0x7f0b0088, float:1.8476545E38)
            r1.setDropDownViewResource(r12)
            r11.setAdapter(r1)
        Lba:
            r2.f()
            r11.f5213n = r7
            android.widget.SpinnerAdapter r12 = r11.f5212m
            if (r12 == 0) goto Lc8
            r11.setAdapter(r12)
            r11.f5212m = r5
        Lc8:
            l.d r12 = r11.f5209j
            r12.d(r13, r14)
            return
        Lce:
            if (r5 == 0) goto Ld3
            r5.recycle()
        Ld3:
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: l.C0715y.<init>(android.content.Context, android.util.AttributeSet, int):void");
    }

    public final int a(SpinnerAdapter spinnerAdapter, Drawable drawable) {
        int i4 = 0;
        if (spinnerAdapter == null) {
            return 0;
        }
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
        int max = Math.max(0, getSelectedItemPosition());
        int min = Math.min(spinnerAdapter.getCount(), max + 15);
        View view = null;
        int i5 = 0;
        for (int max2 = Math.max(0, max - (15 - (min - max))); max2 < min; max2++) {
            int itemViewType = spinnerAdapter.getItemViewType(max2);
            if (itemViewType != i4) {
                view = null;
                i4 = itemViewType;
            }
            view = spinnerAdapter.getView(max2, view, this);
            if (view.getLayoutParams() == null) {
                view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            }
            view.measure(makeMeasureSpec, makeMeasureSpec2);
            i5 = Math.max(i5, view.getMeasuredWidth());
        }
        if (drawable != null) {
            Rect rect = this.f5216q;
            drawable.getPadding(rect);
            return i5 + rect.left + rect.right;
        }
        return i5;
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void drawableStateChanged() {
        super.drawableStateChanged();
        C0695d c0695d = this.f5209j;
        if (c0695d != null) {
            c0695d.a();
        }
    }

    @Override // android.widget.Spinner
    public int getDropDownHorizontalOffset() {
        g gVar = this.f5214o;
        if (gVar != null) {
            return gVar.b();
        }
        return super.getDropDownHorizontalOffset();
    }

    @Override // android.widget.Spinner
    public int getDropDownVerticalOffset() {
        g gVar = this.f5214o;
        if (gVar != null) {
            return gVar.m();
        }
        return super.getDropDownVerticalOffset();
    }

    @Override // android.widget.Spinner
    public int getDropDownWidth() {
        if (this.f5214o != null) {
            return this.f5215p;
        }
        return super.getDropDownWidth();
    }

    public final g getInternalPopup() {
        return this.f5214o;
    }

    @Override // android.widget.Spinner
    public Drawable getPopupBackground() {
        g gVar = this.f5214o;
        if (gVar != null) {
            return gVar.e();
        }
        return super.getPopupBackground();
    }

    @Override // android.widget.Spinner
    public Context getPopupContext() {
        return this.f5210k;
    }

    @Override // android.widget.Spinner
    public CharSequence getPrompt() {
        g gVar = this.f5214o;
        if (gVar != null) {
            return gVar.o();
        }
        return super.getPrompt();
    }

    public ColorStateList getSupportBackgroundTintList() {
        C0695d c0695d = this.f5209j;
        if (c0695d != null) {
            return c0695d.b();
        }
        return null;
    }

    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0695d c0695d = this.f5209j;
        if (c0695d != null) {
            return c0695d.c();
        }
        return null;
    }

    @Override // android.widget.Spinner, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public final void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        g gVar = this.f5214o;
        if (gVar != null && gVar.c()) {
            gVar.dismiss();
        }
    }

    @Override // android.widget.Spinner, android.widget.AbsSpinner, android.view.View
    public final void onMeasure(int i4, int i5) {
        super.onMeasure(i4, i5);
        if (this.f5214o != null && View.MeasureSpec.getMode(i4) == Integer.MIN_VALUE) {
            setMeasuredDimension(Math.min(Math.max(getMeasuredWidth(), a(getAdapter(), getBackground())), View.MeasureSpec.getSize(i4)), getMeasuredHeight());
        }
    }

    @Override // android.widget.Spinner, android.widget.AbsSpinner, android.view.View
    public final void onRestoreInstanceState(Parcelable parcelable) {
        ViewTreeObserver viewTreeObserver;
        f fVar = (f) parcelable;
        super.onRestoreInstanceState(fVar.getSuperState());
        if (fVar.f5233j && (viewTreeObserver = getViewTreeObserver()) != null) {
            viewTreeObserver.addOnGlobalLayoutListener(new a());
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [android.view.View$BaseSavedState, l.y$f, android.os.Parcelable] */
    @Override // android.widget.Spinner, android.widget.AbsSpinner, android.view.View
    public final Parcelable onSaveInstanceState() {
        boolean z4;
        ?? baseSavedState = new View.BaseSavedState(super.onSaveInstanceState());
        g gVar = this.f5214o;
        if (gVar != null && gVar.c()) {
            z4 = true;
        } else {
            z4 = false;
        }
        baseSavedState.f5233j = z4;
        return baseSavedState;
    }

    @Override // android.widget.Spinner, android.view.View
    public final boolean onTouchEvent(MotionEvent motionEvent) {
        C0714x c0714x = this.f5211l;
        if (c0714x != null && c0714x.onTouch(this, motionEvent)) {
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.widget.Spinner, android.view.View
    public final boolean performClick() {
        g gVar = this.f5214o;
        if (gVar != null) {
            if (!gVar.c()) {
                this.f5214o.l(getTextDirection(), getTextAlignment());
                return true;
            }
            return true;
        }
        return super.performClick();
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0695d c0695d = this.f5209j;
        if (c0695d != null) {
            c0695d.e();
        }
    }

    @Override // android.view.View
    public void setBackgroundResource(int i4) {
        super.setBackgroundResource(i4);
        C0695d c0695d = this.f5209j;
        if (c0695d != null) {
            c0695d.f(i4);
        }
    }

    @Override // android.widget.Spinner
    public void setDropDownHorizontalOffset(int i4) {
        g gVar = this.f5214o;
        if (gVar != null) {
            gVar.j(i4);
            gVar.k(i4);
            return;
        }
        super.setDropDownHorizontalOffset(i4);
    }

    @Override // android.widget.Spinner
    public void setDropDownVerticalOffset(int i4) {
        g gVar = this.f5214o;
        if (gVar != null) {
            gVar.i(i4);
        } else {
            super.setDropDownVerticalOffset(i4);
        }
    }

    @Override // android.widget.Spinner
    public void setDropDownWidth(int i4) {
        if (this.f5214o != null) {
            this.f5215p = i4;
        } else {
            super.setDropDownWidth(i4);
        }
    }

    @Override // android.widget.Spinner
    public void setPopupBackgroundDrawable(Drawable drawable) {
        g gVar = this.f5214o;
        if (gVar != null) {
            gVar.h(drawable);
        } else {
            super.setPopupBackgroundDrawable(drawable);
        }
    }

    @Override // android.widget.Spinner
    public void setPopupBackgroundResource(int i4) {
        setPopupBackgroundDrawable(B2.a.f(getPopupContext(), i4));
    }

    @Override // android.widget.Spinner
    public void setPrompt(CharSequence charSequence) {
        g gVar = this.f5214o;
        if (gVar != null) {
            gVar.f(charSequence);
        } else {
            super.setPrompt(charSequence);
        }
    }

    public void setSupportBackgroundTintList(ColorStateList colorStateList) {
        C0695d c0695d = this.f5209j;
        if (c0695d != null) {
            c0695d.h(colorStateList);
        }
    }

    public void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        C0695d c0695d = this.f5209j;
        if (c0695d != null) {
            c0695d.i(mode);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [android.widget.ListAdapter, java.lang.Object, l.y$d] */
    @Override // android.widget.AdapterView
    public void setAdapter(SpinnerAdapter spinnerAdapter) {
        if (!this.f5213n) {
            this.f5212m = spinnerAdapter;
            return;
        }
        super.setAdapter(spinnerAdapter);
        g gVar = this.f5214o;
        if (gVar != 0) {
            Context context = this.f5210k;
            if (context == null) {
                context = getContext();
            }
            Resources.Theme theme = context.getTheme();
            ?? obj = new Object();
            obj.f5222j = spinnerAdapter;
            if (spinnerAdapter instanceof ListAdapter) {
                obj.f5223k = (ListAdapter) spinnerAdapter;
            }
            if (theme != null) {
                if (Build.VERSION.SDK_INT >= 23 && D2.b.f(spinnerAdapter)) {
                    b.a(H2.i.c(spinnerAdapter), theme);
                } else if (spinnerAdapter instanceof X) {
                    X x4 = (X) spinnerAdapter;
                    if (x4.getDropDownViewTheme() == null) {
                        x4.a();
                    }
                }
            }
            gVar.p(obj);
        }
    }
}
