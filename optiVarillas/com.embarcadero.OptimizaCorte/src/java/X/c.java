package X;

import android.os.Bundle;
import android.text.Editable;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.TextView;
import androidx.emoji2.text.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class c extends InputConnectionWrapper {

    /* renamed from: a  reason: collision with root package name */
    public final TextView f2786a;

    /* renamed from: b  reason: collision with root package name */
    public final a f2787b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a {
        /* JADX WARN: Code restructure failed: missing block: B:33:0x0049, code lost:
            if (java.lang.Character.isHighSurrogate(r5) != false) goto L37;
         */
        /* JADX WARN: Code restructure failed: missing block: B:53:0x0079, code lost:
            if (r11 != false) goto L89;
         */
        /* JADX WARN: Code restructure failed: missing block: B:58:0x0086, code lost:
            if (java.lang.Character.isLowSurrogate(r5) != false) goto L66;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public static boolean a(android.view.inputmethod.InputConnection r7, android.text.Editable r8, int r9, int r10, boolean r11) {
            /*
                Method dump skipped, instructions count: 244
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: X.c.a.a(android.view.inputmethod.InputConnection, android.text.Editable, int, int, boolean):boolean");
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Type inference failed for: r0v0, types: [X.c$a, java.lang.Object] */
    public c(TextView textView, InputConnection inputConnection, EditorInfo editorInfo) {
        super(inputConnection, false);
        int i4;
        ?? obj = new Object();
        this.f2786a = textView;
        this.f2787b = obj;
        if (androidx.emoji2.text.f.k != null) {
            androidx.emoji2.text.f a4 = androidx.emoji2.text.f.a();
            if (a4.b() == 1 && editorInfo != null) {
                if (editorInfo.extras == null) {
                    editorInfo.extras = new Bundle();
                }
                f.a aVar = a4.e;
                aVar.getClass();
                Bundle bundle = editorInfo.extras;
                W.b bVar = aVar.c.a;
                int a5 = bVar.a(4);
                if (a5 != 0) {
                    i4 = bVar.f2629b.getInt(a5 + bVar.f2628a);
                } else {
                    i4 = 0;
                }
                bundle.putInt("android.support.text.emoji.emojiCompat_metadataVersion", i4);
                Bundle bundle2 = editorInfo.extras;
                ((f.b) aVar).a.getClass();
                bundle2.putBoolean("android.support.text.emoji.emojiCompat_replaceAll", false);
            }
        }
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public final boolean deleteSurroundingText(int i4, int i5) {
        Editable editableText = this.f2786a.getEditableText();
        this.f2787b.getClass();
        if (!a.a(this, editableText, i4, i5, false) && !super.deleteSurroundingText(i4, i5)) {
            return false;
        }
        return true;
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public final boolean deleteSurroundingTextInCodePoints(int i4, int i5) {
        Editable editableText = this.f2786a.getEditableText();
        this.f2787b.getClass();
        if (a.a(this, editableText, i4, i5, true) || super.deleteSurroundingTextInCodePoints(i4, i5)) {
            return true;
        }
        return false;
    }
}
