package T;

import T.b;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import e0.C0405a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class a extends BaseAdapter implements Filterable, b.a {

    /* renamed from: j  reason: collision with root package name */
    public boolean f2307j;

    /* renamed from: k  reason: collision with root package name */
    public Cursor f2308k;

    /* renamed from: l  reason: collision with root package name */
    public int f2309l;

    /* renamed from: m  reason: collision with root package name */
    public b f2310m;

    public abstract void b(View view, Cursor cursor);

    public void c(Cursor cursor) {
        Cursor cursor2 = this.f2308k;
        if (cursor == cursor2) {
            cursor2 = null;
        } else {
            this.f2308k = cursor;
            if (cursor != null) {
                this.f2309l = cursor.getColumnIndexOrThrow("_id");
                this.f2307j = true;
                notifyDataSetChanged();
            } else {
                this.f2309l = -1;
                this.f2307j = false;
                notifyDataSetInvalidated();
            }
        }
        if (cursor2 != null) {
            cursor2.close();
        }
    }

    @Override // android.widget.Adapter
    public final int getCount() {
        Cursor cursor;
        if (this.f2307j && (cursor = this.f2308k) != null) {
            return cursor.getCount();
        }
        return 0;
    }

    @Override // android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int i4, View view, ViewGroup viewGroup) {
        if (!this.f2307j) {
            return null;
        }
        this.f2308k.moveToPosition(i4);
        if (view != null) {
            b(view, this.f2308k);
            return view;
        }
        throw null;
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [android.widget.Filter, T.b] */
    @Override // android.widget.Filterable
    public final Filter getFilter() {
        if (this.f2310m == null) {
            ?? filter = new Filter();
            filter.f2311a = this;
            this.f2310m = filter;
        }
        return this.f2310m;
    }

    @Override // android.widget.Adapter
    public final Object getItem(int i4) {
        Cursor cursor;
        if (this.f2307j && (cursor = this.f2308k) != null) {
            cursor.moveToPosition(i4);
            return this.f2308k;
        }
        return null;
    }

    @Override // android.widget.Adapter
    public final long getItemId(int i4) {
        Cursor cursor;
        if (!this.f2307j || (cursor = this.f2308k) == null || !cursor.moveToPosition(i4)) {
            return 0L;
        }
        return this.f2308k.getLong(this.f2309l);
    }

    @Override // android.widget.Adapter
    public View getView(int i4, View view, ViewGroup viewGroup) {
        if (this.f2307j) {
            if (this.f2308k.moveToPosition(i4)) {
                if (view != null) {
                    b(view, this.f2308k);
                    return view;
                }
                throw null;
            }
            throw new IllegalStateException(C0405a.c("couldn't move cursor to position ", i4));
        }
        throw new IllegalStateException("this should only be called when the cursor is valid");
    }
}
