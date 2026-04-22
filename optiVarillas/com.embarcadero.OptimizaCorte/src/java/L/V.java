package l;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class V extends T.c implements View.OnClickListener {

    /* renamed from: n  reason: collision with root package name */
    public int f5073n;

    /* renamed from: o  reason: collision with root package name */
    public int f5074o;

    /* renamed from: p  reason: collision with root package name */
    public int f5075p;

    /* renamed from: q  reason: collision with root package name */
    public int f5076q;

    /* renamed from: r  reason: collision with root package name */
    public int f5077r;

    /* renamed from: s  reason: collision with root package name */
    public int f5078s;

    /* renamed from: t  reason: collision with root package name */
    public int f5079t;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class a {
    }

    public V() {
        throw null;
    }

    @Override // T.a
    public final void b(View view, Cursor cursor) {
        a aVar = (a) view.getTag();
        int i4 = this.f5079t;
        int i5 = 0;
        if (i4 != -1) {
            i5 = cursor.getInt(i4);
        }
        aVar.getClass();
        aVar.getClass();
        aVar.getClass();
        aVar.getClass();
        int i6 = this.f5073n;
        aVar.getClass();
        if (i6 != 2 && (i6 != 1 || (i5 & 1) == 0)) {
            throw null;
        }
        throw null;
    }

    @Override // T.a
    public final void c(Cursor cursor) {
        try {
            super.c(cursor);
            if (cursor != null) {
                this.f5074o = cursor.getColumnIndex("suggest_text_1");
                this.f5075p = cursor.getColumnIndex("suggest_text_2");
                this.f5076q = cursor.getColumnIndex("suggest_text_2_url");
                this.f5077r = cursor.getColumnIndex("suggest_icon_1");
                this.f5078s = cursor.getColumnIndex("suggest_icon_2");
                this.f5079t = cursor.getColumnIndex("suggest_flags");
            }
        } catch (Exception e4) {
            Log.e("SuggestionsAdapter", "error changing cursor and caching columns", e4);
        }
    }

    public final Drawable e(String str) {
        if (str == null || str.isEmpty() || "0".equals(str)) {
            return null;
        }
        try {
            Integer.parseInt(str);
            throw null;
        } catch (Resources.NotFoundException unused) {
            Log.w("SuggestionsAdapter", "Icon resource not found: ".concat(str));
            return null;
        } catch (NumberFormatException unused2) {
            throw null;
        }
    }

    @Override // T.a, android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public final View getDropDownView(int i4, View view, ViewGroup viewGroup) {
        try {
            return super.getDropDownView(i4, view, viewGroup);
        } catch (RuntimeException e4) {
            Log.w("SuggestionsAdapter", "Search suggestions cursor threw exception.", e4);
            throw null;
        }
    }

    @Override // T.a, android.widget.Adapter
    public final View getView(int i4, View view, ViewGroup viewGroup) {
        try {
            super.getView(i4, view, viewGroup);
            return view;
        } catch (RuntimeException e4) {
            Log.w("SuggestionsAdapter", "Search suggestions cursor threw exception.", e4);
            throw null;
        }
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public final boolean hasStableIds() {
        return false;
    }

    @Override // android.widget.BaseAdapter
    public final void notifyDataSetChanged() {
        Bundle bundle;
        super.notifyDataSetChanged();
        Cursor cursor = this.f2308k;
        if (cursor != null) {
            bundle = cursor.getExtras();
        } else {
            bundle = null;
        }
        if (bundle != null) {
            bundle.getBoolean("in_progress");
        }
    }

    @Override // android.widget.BaseAdapter
    public final void notifyDataSetInvalidated() {
        Bundle bundle;
        super.notifyDataSetInvalidated();
        Cursor cursor = this.f2308k;
        if (cursor != null) {
            bundle = cursor.getExtras();
        } else {
            bundle = null;
        }
        if (bundle != null) {
            bundle.getBoolean("in_progress");
        }
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        if (!(view.getTag() instanceof CharSequence)) {
            return;
        }
        throw null;
    }
}
