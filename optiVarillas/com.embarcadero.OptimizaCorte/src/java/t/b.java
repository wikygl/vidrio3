package T;

import android.database.Cursor;
import android.util.Log;
import android.widget.Filter;
import l.V;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class b extends Filter {

    /* renamed from: a  reason: collision with root package name */
    public a f2311a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface a {
    }

    @Override // android.widget.Filter
    public final CharSequence convertResultToString(Object obj) {
        String string;
        Cursor cursor = (Cursor) obj;
        ((V) this.f2311a).getClass();
        if (cursor == null) {
            return null;
        }
        int columnIndex = cursor.getColumnIndex("suggest_intent_query");
        if (columnIndex != -1) {
            try {
                string = cursor.getString(columnIndex);
            } catch (Exception e4) {
                Log.e("SuggestionsAdapter", "unexpected error retrieving valid column from cursor, did the remote process die?", e4);
            }
            string.getClass();
            return string;
        }
        string = null;
        string.getClass();
        return string;
    }

    @Override // android.widget.Filter
    public final Filter.FilterResults performFiltering(CharSequence charSequence) {
        V v4 = (V) this.f2311a;
        if (charSequence != null) {
            v4.getClass();
            charSequence.toString();
        }
        v4.getClass();
        throw null;
    }

    @Override // android.widget.Filter
    public final void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
        a aVar = this.f2311a;
        Cursor cursor = ((T.a) aVar).f2308k;
        Object obj = filterResults.values;
        if (obj != null && obj != cursor) {
            ((V) aVar).c((Cursor) obj);
        }
    }
}
