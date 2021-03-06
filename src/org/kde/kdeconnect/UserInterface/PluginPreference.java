package org.kde.kdeconnect.UserInterface;

import android.preference.CheckBoxPreference;
import android.view.View;

import org.kde.kdeconnect.Device;
import org.kde.kdeconnect.Plugins.Plugin;
import org.kde.kdeconnect.Plugins.PluginFactory;
import org.kde.kdeconnect_tp.R;

class PluginPreference extends CheckBoxPreference {

    private final Device device;
    private final String pluginKey;
    private final View.OnClickListener listener;

    public PluginPreference(final DeviceSettingsActivity activity, final String pluginKey, final Device device) {
        super(activity);

        setLayoutResource(R.layout.preference_with_button);

        this.device = device;
        this.pluginKey = pluginKey;

        PluginFactory.PluginInfo info = PluginFactory.getPluginInfo(activity, pluginKey);
        setTitle(info.getDisplayName());
        setSummary(info.getDescription());
        setChecked(device.isPluginEnabled(pluginKey));

        Plugin plugin = device.getPlugin(pluginKey, true);
        if (info.hasSettings() && plugin != null) {
            this.listener = v -> {
                Plugin plugin1 = device.getPlugin(pluginKey, true);
                if (plugin1 != null) {
                    plugin1.startPreferencesActivity(activity);
                } else { //Could happen if the device is not connected anymore
                    activity.finish(); //End this activity so we go to the "device not reachable" screen
                }
            };
        } else {
            this.listener = null;
        }

    }

    @Override
    protected void onBindView(View root) {
        super.onBindView(root);
        final View button = root.findViewById(R.id.settingsButton);

        if (listener == null) {
            button.setVisibility(View.GONE);
        } else {
            button.setEnabled(isChecked());
            button.setOnClickListener(listener);
        }

        root.setOnClickListener(v -> {
            boolean newState = !device.isPluginEnabled(pluginKey);
            setChecked(newState); //It actually works on API<14
            button.setEnabled(newState);
            device.setPluginEnabled(pluginKey, newState);
        });
    }

}
