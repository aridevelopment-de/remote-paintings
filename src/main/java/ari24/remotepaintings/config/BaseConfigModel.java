package ari24.remotepaintings.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId="remote-paintings")
@Config(name="remote-paintings-config", wrapperName="RemotePaintingsConfig")
public class BaseConfigModel {
    public String hasteUrl = "https://haste.pinofett.de/documents";
    public String currentConfigUrl = "https://haste.pinofett.de/raw/hj0cv6gemv";
}
