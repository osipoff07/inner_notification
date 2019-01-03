package io.inner.notification.example;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import io.inner.notification.ExcludedRule;
import io.inner.notification.NotificationExtensionKt;
import io.inner.notification.models.identity.NotificationIdentity;

public class SomeActivity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        NotificationExtensionKt.excludeWith(this, new ExcludedRule() {
            @Override
            public boolean isExcluded(@NotNull NotificationIdentity identity) {
                return false;
            }
        });
    }
}
