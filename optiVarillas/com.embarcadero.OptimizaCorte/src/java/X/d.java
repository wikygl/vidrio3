package X;

import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.widget.TextView;
import androidx.emoji2.text.f;
import java.lang.ref.WeakReference;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class d implements InputFilter {

    /* renamed from: a  reason: collision with root package name */
    public final TextView f2788a;

    /* renamed from: b  reason: collision with root package name */
    public a f2789b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class a extends f.f {

        /* renamed from: a  reason: collision with root package name */
        public final WeakReference f2790a;

        /* renamed from: b  reason: collision with root package name */
        public final WeakReference f2791b;

        public a(TextView textView, d dVar) {
            this.f2790a = new WeakReference(textView);
            this.f2791b = new WeakReference(dVar);
        }

        public final void b() {
            InputFilter[] filters;
            int length;
            TextView textView = (TextView) this.f2790a.get();
            InputFilter inputFilter = (InputFilter) this.f2791b.get();
            if (inputFilter != null && textView != null && (filters = textView.getFilters()) != null) {
                for (InputFilter inputFilter2 : filters) {
                    if (inputFilter2 == inputFilter) {
                        if (textView.isAttachedToWindow()) {
                            CharSequence text = textView.getText();
                            androidx.emoji2.text.f a4 = androidx.emoji2.text.f.a();
                            if (text == null) {
                                length = 0;
                            } else {
                                a4.getClass();
                                length = text.length();
                            }
                            CharSequence f = a4.f(text, 0, length);
                            if (text == f) {
                                return;
                            }
                            int selectionStart = Selection.getSelectionStart(f);
                            int selectionEnd = Selection.getSelectionEnd(f);
                            textView.setText(f);
                            if (f instanceof Spannable) {
                                Spannable spannable = (Spannable) f;
                                if (selectionStart >= 0 && selectionEnd >= 0) {
                                    Selection.setSelection(spannable, selectionStart, selectionEnd);
                                    return;
                                } else if (selectionStart >= 0) {
                                    Selection.setSelection(spannable, selectionStart);
                                    return;
                                } else if (selectionEnd >= 0) {
                                    Selection.setSelection(spannable, selectionEnd);
                                    return;
                                } else {
                                    return;
                                }
                            }
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }

    public d(TextView textView) {
        this.f2788a = textView;
    }

    @Override // android.text.InputFilter
    public final CharSequence filter(CharSequence charSequence, int i4, int i5, Spanned spanned, int i6, int i7) {
        TextView textView = this.f2788a;
        if (textView.isInEditMode()) {
            return charSequence;
        }
        int b4 = androidx.emoji2.text.f.a().b();
        if (b4 != 0) {
            if (b4 != 1) {
                if (b4 != 3) {
                    return charSequence;
                }
            } else if ((i7 != 0 || i6 != 0 || spanned.length() != 0 || charSequence != textView.getText()) && charSequence != null) {
                if (i4 != 0 || i5 != charSequence.length()) {
                    charSequence = charSequence.subSequence(i4, i5);
                }
                return androidx.emoji2.text.f.a().f(charSequence, 0, charSequence.length());
            } else {
                return charSequence;
            }
        }
        androidx.emoji2.text.f a4 = androidx.emoji2.text.f.a();
        if (this.f2789b == null) {
            this.f2789b = new a(textView, this);
        }
        a4.g(this.f2789b);
        return charSequence;
    }
}
