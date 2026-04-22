package N;

import android.view.accessibility.AccessibilityNodeInfo;
import j$.time.Duration;
import j$.time.TimeConversions;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class m {
    public static /* synthetic */ void a(AccessibilityNodeInfo accessibilityNodeInfo, Duration duration) {
        accessibilityNodeInfo.setMinDurationBetweenContentChanges(TimeConversions.convert(duration));
    }
}
