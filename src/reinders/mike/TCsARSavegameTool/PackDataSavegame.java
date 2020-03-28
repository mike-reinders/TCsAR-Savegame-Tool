package reinders.mike.TCsARSavegameTool;

import qowyn.ark.ArkSavFile;
import qowyn.ark.properties.*;
import qowyn.ark.structs.StructColor;
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

            if ((prop = file.getProperty(PackKnownProperties.MOD_VERSION)) != null) {
                modVersion = ((PropertyFloat)prop).getValue();
            }

            if (!ignoreVersion && !PackDataSavegame.matchModVersion(modVersion)) {
                throw new ModVersionMismatchException(PackDataSavegame.latestModVersion(), modVersion == null? 12.5f: modVersion);
            }


            if ((prop = file.getProperty(PackKnownProperties.PACK_DATA)) != null) {
                packData = new ArrayList<>();

                for (Object obj : ((PropertyArray)prop).getValue()) {
                    StructPropertyList packPropertyList = (StructPropertyList)obj;

                    Pack pack = new Pack();

                    if ((prop = packPropertyList.getProperty(PackKnownProperties.PID)) != null) {
                        pack.setPid(((PropertyStr)prop).getValue());
                    }

                    if ((prop = packPropertyList.getProperty(PackKnownProperties.POSITION)) != null) {
                        pack.setPosition(((PropertyInt)prop).getValue());
                    }

                    if ((prop = packPropertyList.getProperty(PackKnownProperties.COST)) != null) {
                        pack.setCost(((PropertyInt)prop).getValue());
                    }

                    if ((prop = packPropertyList.getProperty(PackKnownProperties.DESCRIPTION)) != null) {
                        pack.setDescription(((PropertyStr)prop).getValue());
                    }

                    if ((prop = packPropertyList.getProperty(PackKnownProperties.CATEGORY)) != null) {
                        pack.setCategory(((PropertyStr)prop).getValue());
                    }

                    if ((prop = packPropertyList.getProperty(PackKnownProperties.COLOUR)) != null) {
                        pack.setColor(new Color(((StructColor)prop).getR(), ((StructColor)prop).getG(), ((StructColor)prop).getB(), ((StructColor)prop).getA()));
                    }

                    if ((prop = packPropertyList.getProperty(PackKnownProperties.ITEMS)) != null) {
                        List<PackItem> items = new ArrayList<>();

                        for (Object obj2 : ((PropertyArray)prop).getValue()) {
                            StructPropertyList itemProperties = ((StructPropertyList)obj2);

                            PackItem item = new PackItem();

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_NAME)) != null) {
                                item.setName(((PropertyStr)prop).getValue());
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_TYPE)) != null) {
                                item.setType(((PropertyByte)prop).getValue().getByteValue());
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_CLASS)) != null) {
                                item.setItemClass(((PropertyObject)prop).getValue().getObjectString().toString());
                                item.setItemClassShort(((PropertyObject)prop).getValue().isPath());
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_IS_BLUEPRINT)) != null) {
                                item.setBlueprint(((PropertyBool)prop).getValue());
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_IS_BLUEPRINT)) != null) {
                                item.setBlueprint(((PropertyBool)prop).getValue());
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_QUALITY_NAME)) != null) {
                                item.setQualityName(((PropertyStr)prop).getValue());
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_QUALITY_COLOUR)) != null) {
                                item.setQualityColour(new Color(((StructColor)prop).getR(), ((StructColor)prop).getG(), ((StructColor)prop).getB(), ((StructColor)prop).getA()));
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_QUALITY)) != null) {
                                item.setQuality(((PropertyByte)prop).getValue().getByteValue());
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_QUANTITY)) != null) {
                                item.setQuantity(((PropertyInt)prop).getValue());
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_RATING)) != null) {
                                item.setQuantity(((PropertyInt)prop).getValue());
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_STAT_DATA)) != null) {
                                List<PackItemStat> itemStats = new ArrayList<>();

                                for (Object obj3 : ((PropertyArray)prop).getValue()) {
                                    StructPropertyList itemStatProperties = (StructPropertyList)obj3;

                                    PackItemStat itemStat = new PackItemStat();

                                    if ((prop = itemStatProperties.getProperty(PackKnownProperties.ITEM_STAT_NAME)) != null) {
                                        itemStat.setName(((PropertyStr)prop).getValue());
                                    }

                                    if ((prop = itemStatProperties.getProperty(PackKnownProperties.ITEM_STAT_DISPLAYED)) != null) {
                                        itemStat.setDisplayed(((PropertyFloat)prop).getValue());
                                    }

                                    if ((prop = itemStatProperties.getProperty(PackKnownProperties.ITEM_STAT_CALCULATED)) != null) {
                                        itemStat.setCalculated(((PropertyInt)prop).getValue());
                                    }

                                    if ((prop = itemStatProperties.getProperty(PackKnownProperties.ITEM_STAT_IS_USED)) != null) {
                                        itemStat.setUsed(((PropertyBool)prop).getValue());
                                    }

                                    itemStats.add(itemStat);
                                }

                                item.setItemStats(itemStats);
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_IS_MULTIPLE_CHOICE)) != null) {
                                item.setMultipleChoice(((PropertyBool)prop).getValue());
                            }

                            items.add(item);
                        }

                        pack.setItems(items);
                    }

                    if ((prop = packPropertyList.getProperty(PackKnownProperties.DINOS)) != null) {
                        List<PackDino> dinos = new ArrayList<>();

                        for (Object obj2 : ((PropertyArray)prop).getValue()) {
                            StructPropertyList dinoProperties = ((StructPropertyList)obj2);

                            PackDino dino = new PackDino();

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_NAME)) != null) {
                                dino.setName(((PropertyStr)prop).getValue());
                            }

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_TYPE)) != null) {
                                dino.setType(((PropertyByte)prop).getValue().getByteValue());
                            }

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_CLASS)) != null) {
                                dino.setDinoClass(((PropertyObject)prop).getValue().getObjectString().toString());
                                dino.setDinoClassShort(((PropertyObject)prop).getValue().isPath());
                            }

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_WILD_LEVEL)) != null) {
                                dino.setWildLevel(((PropertyInt)prop).getValue());
                            }

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_TAMED_LEVEL)) != null) {
                                dino.setTamedLevel(((PropertyStr)prop).getValue());
                            }

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_ENTRY)) != null) {
                                dino.setEntry(((PropertyObject)prop).getValue().getObjectString().toString());
                                dino.setEntryShort(((PropertyObject)prop).getValue().isPath());
                            }

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_QUANTITY)) != null) {
                                dino.setQuantity(((PropertyInt)prop).getValue());
                            }

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_IS_MULTIPLE_CHOICE)) != null) {
                                dino.setMultipleChoice(((PropertyBool)prop).getValue());
                            }

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_IS_GENDER_CHOICE)) != null) {
                                dino.setGenderChoice(((PropertyBool)prop).getValue());
                            }

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_GENDER)) != null) {
                                dino.setGender(((PropertyByte)prop).getValue().getByteValue());
                            }

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_IS_NEUTERED)) != null) {
                                dino.setNeutered(((PropertyBool)prop).getValue());
                            }

                            dinos.add(dino);
                        }

                        pack.setDinos(dinos);
                    }

                    if ((prop = packPropertyList.getProperty(PackKnownProperties.REQUIREMENTS)) != null) {
                        StructPropertyList packRequirements = (StructPropertyList)(((PropertyStruct)prop).getValue());

                        if ((prop = packRequirements.getProperty(PackKnownProperties.REQUIREMENTS_ADMIN_ONLY)) != null) {
                            pack.setName(((PropertyStr)prop).getValue());
                        }

                        if ((prop = packRequirements.getProperty(PackKnownProperties.REQUIREMENTS_IS_PREREQUISITE)) != null) {
                            pack.setRequirementIsPrerequisite(((PropertyBool)prop).getValue());
                        }

                        if ((prop = packRequirements.getProperty(PackKnownProperties.REQUIREMENTS_PREREQUISITE_PID)) != null) {
                            pack.setRequirementPrerequisitePid(((PropertyStr)prop).getValue());
                        }

                        if ((prop = packRequirements.getProperty(PackKnownProperties.REQUIREMENTS_LEVEL_RESTRICTION)) != null) {
                            pack.setRequirementLevelRestriction(((PropertyInt)prop).getValue());
                        }

                        if ((prop = packRequirements.getProperty(PackKnownProperties.REQUIREMENTS_PURCHASE_LIMIT)) != null) {
                            pack.setRequirementPurchaseLimit(((PropertyInt)prop).getValue());
                        }

                        if ((prop = packRequirements.getProperty(PackKnownProperties.REQUIREMENTS_PURCHASE_COOLDOWN)) != null) {
                            pack.setRequirementPurchaseCooldown(((PropertyFloat)prop).getValue());
                        }

                        if ((prop = packRequirements.getProperty(PackKnownProperties.REQUIREMENTS_CUSTOM_TAGS)) != null) {
                            List<String> tags = new ArrayList<>();
                            for (Object obj2 : ((PropertyArray)prop).getValue()) {
                                tags.add((String)obj2);
                            }
                            pack.setTags(tags);
                        }
                    }

                    if ((prop = packPropertyList.getProperty(PackKnownProperties.PACK_VERSION)) != null) {
                        pack.setPackVersion(((PropertyInt)prop).getValue());
                    }

                    packData.add(pack);
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