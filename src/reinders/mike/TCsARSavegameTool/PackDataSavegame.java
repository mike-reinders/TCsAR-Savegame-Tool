package reinders.mike.TCsARSavegameTool;

import qowyn.ark.ArkSavFile;
import qowyn.ark.properties.Property;
import qowyn.ark.properties.PropertyArray;
import qowyn.ark.properties.PropertyFloat;
import qowyn.ark.structs.StructPropertyList;
import reinders.mike.TCsARSavegameTool.Exception.ModVersionMismatchException;
import reinders.mike.TCsARSavegameTool.Exception.SaveGameException;
import reinders.mike.TCsARSavegameTool.Util.ObjectA;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PackDataSavegame {

    public static final String KNOWN__CLASS_NAME = "TCsAR_SavedPackData_C";
    public static final Float[] KNOWN__MOD_VERSIONS = new Float[] { 12.6f, 12.7f, 12.8f };

    private Float modVersion = null;
    private List<Pack> packs = null;

    public PackDataSavegame() {
        this.modVersion = PackDataSavegame.latestModVersion();
        this.packs = new ArrayList<>();
    }

    public PackDataSavegame(Path path) throws SaveGameException {
        this.load(path);
    }

    public PackDataSavegame(Path path, boolean ignoreVersion) throws SaveGameException {
        this.load(path, ignoreVersion);
    }

    private static Float latestModVersion() {
        if (PackDataSavegame.KNOWN__MOD_VERSIONS.length > 1) {
            return PackDataSavegame.KNOWN__MOD_VERSIONS[PackDataSavegame.KNOWN__MOD_VERSIONS.length - 1];
        } else {
            return null;
        }
    }

    private static boolean matchModVersion(Float modVersion) {
        if (modVersion == null) {
            return false;
        }

        for (float f : PackDataSavegame.KNOWN__MOD_VERSIONS) {
            if (f == modVersion) {
                return true;
            }
        }

        return false;
    }

    public void load(Path path) throws SaveGameException {
        this.load(path, false);
    }

    public void load(Path path, boolean ignoreVersion) throws SaveGameException {
        try {
            ArkSavFile file = new ArkSavFile(path);

            String className = (String) ObjectA.getPrivateField(file, PlayerKnownProperties.CLASS_NAME);
            if (className == null || !className.equals(PackDataSavegame.KNOWN__CLASS_NAME)) {
                throw new SaveGameException("File is not a PackData-Savegame!");
            }

            // Result Variables
            Float modVersion = null;
            List<Pack> packData = null;

            // Cache-References
            Property<?> prop;

            if ((prop = file.getProperty(PlayerKnownProperties.MOD_VERSION)) != null) {
                modVersion = ((PropertyFloat)prop).getValue();
            }

            if (!ignoreVersion && !PackDataSavegame.matchModVersion(modVersion)) {
                throw new ModVersionMismatchException(PackDataSavegame.latestModVersion(), modVersion == null? 12.5f: modVersion);
            }


            if ((prop = file.getProperty(PlayerKnownProperties.PLAYER_DATA)) != null) {
                packData = new ArrayList<>();

                for (Object obj : ((PropertyArray)prop).getValue()) {
                    StructPropertyList playerPropertyList = (StructPropertyList)obj;

                    Pack pack = new Pack();


                }
            }

            this.modVersion = modVersion;
            this.packs = packData;
        } catch (SaveGameException ex) {
            throw ex;
        } catch (Throwable throwable) {
            throw new SaveGameException("Failed to load SaveGame", throwable);
        }
    }

    public void unload() {

    }

    public void save(Path path) throws SaveGameException {

    }

    public void saveJson(Path path) throws SaveGameException {
        this.saveJson(path, false);
    }

    public void saveJson(Path path, boolean pretty) throws SaveGameException {

    }

    public float getModVersion() {
        return this.modVersion;
    }

    public void setModVersion(float modVersion) {
        this.modVersion = modVersion;
    }

}