package com.nettm.idea.plugin.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "com.nettm.idea.plugin.settings.TestPersistentState",
        storages = {@com.intellij.openapi.components.Storage("$APP_CONFIG$/generateUnitTest.xml")})
public class TestSettingsState implements PersistentStateComponent<TestSettingsState> {

    public String projectPath = "";

    public static TestSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(TestSettingsState.class);
    }

    @Nullable
    @Override
    public TestSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TestSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getProjectPath() {
        return projectPath;
    }
}
