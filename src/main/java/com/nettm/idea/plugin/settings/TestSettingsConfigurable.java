package com.nettm.idea.plugin.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TestSettingsConfigurable implements Configurable {

    private TestSettingsComponent mySettingsComponent;

    @Override
    @Nls(capitalization = Nls.Capitalization.Title)
    public String getDisplayName() {
        return "Generate UnitTest";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new TestSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        TestSettingsState settings = TestSettingsState.getInstance();
        boolean modified = !mySettingsComponent.getUserNameText().equals(settings.projectPath);
        return modified;
    }

    @Override
    public void apply() {
        TestSettingsState settings = TestSettingsState.getInstance();
        settings.projectPath = mySettingsComponent.getUserNameText();
    }

    @Override
    public void reset() {
        TestSettingsState settings = TestSettingsState.getInstance();
        mySettingsComponent.setUserNameText(settings.projectPath);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
