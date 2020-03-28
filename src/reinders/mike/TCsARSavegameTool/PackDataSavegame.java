package reinders.mike.TCsARSavegameTool;

import qowyn.ark.ArkSavFile;
import qowyn.ark.arrays.ArkArrayString;
import qowyn.ark.arrays.ArkArrayStruct;
import qowyn.ark.properties.*;
import qowyn.ark.structs.Struct;
import qowyn.ark.structs.StructColor;
import qowyn.ark.structs.StructLinearColor;
import qowyn.ark.structs.StructPropertyList;
import qowyn.ark.types.ArkName;
import qowyn.ark.types.ObjectReference;
import reinders.mike.TCsARSavegameTool.Exception.ModVersionMismatchException;
import reinders.mike.TCsARSavegameTool.Exception.SaveGameException;
import reinders.mike.TCsARSavegameTool.Util.ObjectA;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

                StructLinearColor colorStruct;
                for (Object obj : ((PropertyArray)prop).getValue()) {
                    StructPropertyList packPropertyList = (StructPropertyList)obj;

                    Pack pack = new Pack();

                    if ((prop = packPropertyList.getProperty(PackKnownProperties.NAME)) != null) {
                        pack.setName(((PropertyStr)prop).getValue());
                    }

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
                        colorStruct = (StructLinearColor)((PropertyStruct)prop).getValue();
                        pack.setColor(new Color(colorStruct.getR(), colorStruct.getG(), colorStruct.getB(), colorStruct.getA()));
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
                                colorStruct = (StructLinearColor)((PropertyStruct)prop).getValue();
                                item.setQualityColour(new Color(colorStruct.getR(), colorStruct.getG(), colorStruct.getB(), colorStruct.getA()));
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_QUALITY)) != null) {
                                item.setQuality(((PropertyByte)prop).getValue().getByteValue());
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_QUANTITY)) != null) {
                                item.setQuantity(((PropertyInt)prop).getValue());
                            }

                            if ((prop = itemProperties.getProperty(PackKnownProperties.ITEM_RATING)) != null) {
                                item.setItemRating(((PropertyFloat)prop).getValue());
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

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_WILD_LEVEL)) != null && ((PropertyStr)prop).getValue() != null) {
                                dino.setWildLevel(Integer.parseInt(((PropertyStr)prop).getValue()));
                            }

                            if ((prop = dinoProperties.getProperty(PackKnownProperties.DINO_TAMED_LEVEL)) != null && ((PropertyStr)prop).getValue() != null) {
                                dino.setTamedLevel(Integer.parseInt(((PropertyStr)prop).getValue()));
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
                            pack.setRequirementIsAdminOnly(((PropertyBool)prop).getValue());
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
        this.modVersion = null;
        this.packs = null;
    }

    public void save(Path path) throws SaveGameException {
        try {
            ArkSavFile file = new ArkSavFile();
            ObjectA.setPrivateField(file, PackKnownProperties.CLASS_NAME, PackDataSavegame.KNOWN__CLASS_NAME);

            List<Property<?>> fileProperties = new ArrayList<>();

            // ModVersion
            PropertyFloat modVersion = new PropertyFloat(PackKnownProperties.MOD_VERSION, this.modVersion);
            fileProperties.add(modVersion);

            // PackData
            ArkArrayStruct packDataArray = new ArkArrayStruct();
            PropertyArray packData = new PropertyArray(PackKnownProperties.PACK_DATA, packDataArray);

            // Pack PackData
            List<Property<?>> packProperties;
            ArkArrayStruct items;
            List<Property<?>> itemProperties;
            ArkArrayStruct itemStats;
            List<Property<?>> itemStatsProperties;
            ArkArrayStruct dinos;
            List<Property<?>> dinoProperties;
            ObjectReference objRef;
            List<Property<?>> requirementProperties;
            ArkArrayString requirementTagsArray;
            for (Pack pack : this.packs) {
                packProperties = new ArrayList<>();

                // add simple properties
                packProperties.add(new PropertyStr(PackKnownProperties.NAME, pack.getName()));
                packProperties.add(new PropertyInt(PackKnownProperties.POSITION, pack.getPosition()));
                packProperties.add(new PropertyInt(PackKnownProperties.COST, pack.getCost()));
                packProperties.add(new PropertyStr(PackKnownProperties.DESCRIPTION, pack.getDescription()));
                packProperties.add(new PropertyStr(PackKnownProperties.CATEGORY, pack.getCategory()));

                // pack color
                packProperties.add(new PropertyStruct(
                        PackKnownProperties.COLOUR,
                        (pack.getColor() == null? new StructLinearColor(): new StructLinearColor(pack.getColor().R, pack.getColor().G, pack.getColor().B, pack.getColor().A)),
                        PackKnownProperties.COLOUR_STRUCT_TYPE
                ));

                // items
                items = new ArkArrayStruct();
                for (PackItem item : pack.getItems()) {
                    itemProperties = new ArrayList<>();
                    itemProperties.add(new PropertyStr(PackKnownProperties.ITEM_NAME, item.getName()));
                    itemProperties.add(new PropertyByte(PackKnownProperties.ITEM_TYPE, item.getType()));

                    objRef = new ObjectReference(ArkName.from(item.getItemClass()));
                    objRef.setObjectType(item.isItemClassShort()? ObjectReference.TYPE_PATH_NO_TYPE: ObjectReference.TYPE_PATH);
                    itemProperties.add(new PropertyObject(PackKnownProperties.ITEM_CLASS, objRef));

                    itemProperties.add(new PropertyBool(PackKnownProperties.ITEM_IS_BLUEPRINT, item.isBlueprint()));
                    itemProperties.add(new PropertyStr(PackKnownProperties.ITEM_QUALITY_NAME, item.getQualityName()));

                    // item color
                    itemProperties.add(new PropertyStruct(
                            PackKnownProperties.ITEM_QUALITY_COLOUR,
                            (item.getQualityColour() == null? new StructLinearColor(): new StructLinearColor(item.getQualityColour().R, item.getQualityColour().G, item.getQualityColour().B, item.getQualityColour().A)),
                            PackKnownProperties.ITEM_QUALITY_COLOUR_STRUCT_TYPE
                    ));

                    // item
                    itemProperties.add(new PropertyByte(PackKnownProperties.ITEM_QUALITY, item.getQuality()));
                    itemProperties.add(new PropertyInt(PackKnownProperties.ITEM_QUANTITY, item.getQuantity()));
                    itemProperties.add(new PropertyFloat(PackKnownProperties.ITEM_RATING, item.getItemRating()));

                    // item stats
                    itemStats = new ArkArrayStruct();
                    for (PackItemStat stat : item.getItemStats()) {
                        itemStatsProperties = new ArrayList<>();
                        itemStatsProperties.add(new PropertyStr(PackKnownProperties.ITEM_STAT_NAME, stat.getName()));
                        itemStatsProperties.add(new PropertyFloat(PackKnownProperties.ITEM_STAT_DISPLAYED, stat.getDisplayed()));
                        itemStatsProperties.add(new PropertyInt(PackKnownProperties.ITEM_STAT_CALCULATED, stat.getCalculated()));
                        itemStatsProperties.add(new PropertyBool(PackKnownProperties.ITEM_STAT_IS_USED, stat.isUsed()));
                        itemStats.add(new StructPropertyList(itemStatsProperties));
                    }
                    itemProperties.add(new PropertyArray(PackKnownProperties.ITEM_STAT_DATA, itemStats));

                    // item
                    itemProperties.add(new PropertyBool(PackKnownProperties.ITEM_IS_MULTIPLE_CHOICE, item.isMultipleChoice()));

                    items.add(new StructPropertyList(itemProperties));
                }
                packProperties.add(new PropertyArray(PackKnownProperties.ITEMS, items));

                // dinos
                dinos = new ArkArrayStruct();
                for (PackDino dino : pack.getDinos()) {
                    dinoProperties = new ArrayList<>();
                    dinoProperties.add(new PropertyStr(PackKnownProperties.DINO_NAME, dino.getName()));
                    dinoProperties.add(new PropertyByte(PackKnownProperties.DINO_TYPE, dino.getType()));

                    objRef = new ObjectReference(ArkName.from(dino.getDinoClass()));
                    objRef.setObjectType(dino.isDinoClassShort()? ObjectReference.TYPE_PATH_NO_TYPE: ObjectReference.TYPE_PATH);
                    dinoProperties.add(new PropertyObject(PackKnownProperties.DINO_CLASS, objRef));

                    dinoProperties.add(new PropertyStr(PackKnownProperties.DINO_WILD_LEVEL, dino.getWildLevel()==null? null: String.valueOf(dino.getWildLevel())));
                    dinoProperties.add(new PropertyStr(PackKnownProperties.DINO_TAMED_LEVEL, dino.getTamedLevel()==null? null: String.valueOf(dino.getTamedLevel())));

                    objRef = new ObjectReference(ArkName.from(dino.getEntry()));
                    objRef.setObjectType(dino.isEntryShort()? ObjectReference.TYPE_PATH_NO_TYPE: ObjectReference.TYPE_PATH);
                    dinoProperties.add(new PropertyObject(PackKnownProperties.DINO_ENTRY, objRef));

                    dinoProperties.add(new PropertyInt(PackKnownProperties.DINO_QUANTITY, dino.getQuantity()));
                    dinoProperties.add(new PropertyBool(PackKnownProperties.DINO_IS_MULTIPLE_CHOICE, dino.isMultipleChoice()));
                    dinoProperties.add(new PropertyBool(PackKnownProperties.DINO_IS_GENDER_CHOICE, dino.isGenderChoice()));
                    dinoProperties.add(new PropertyByte(PackKnownProperties.DINO_GENDER, dino.getGender()));
                    dinoProperties.add(new PropertyBool(PackKnownProperties.DINO_IS_NEUTERED, dino.isNeutered()));

                    dinos.add(new StructPropertyList(dinoProperties));
                }
                packProperties.add(new PropertyArray(PackKnownProperties.DINOS, dinos));

                // pack
                packProperties.add(new PropertyStr(PackKnownProperties.PID, pack.getPid()));

                // requirements
                requirementProperties = new ArrayList<>();
                requirementProperties.add(new PropertyBool(PackKnownProperties.REQUIREMENTS_ADMIN_ONLY, pack.isRequirementIsAdminOnly()));
                requirementProperties.add(new PropertyBool(PackKnownProperties.REQUIREMENTS_IS_PREREQUISITE, pack.isRequirementIsPrerequisite()));
                requirementProperties.add(new PropertyStr(PackKnownProperties.REQUIREMENTS_PREREQUISITE_PID, pack.getRequirementPrerequisitePid()));
                requirementProperties.add(new PropertyInt(PackKnownProperties.REQUIREMENTS_LEVEL_RESTRICTION, pack.getRequirementLevelRestriction()));
                requirementProperties.add(new PropertyInt(PackKnownProperties.REQUIREMENTS_PURCHASE_LIMIT, pack.getRequirementPurchaseLimit()));
                requirementProperties.add(new PropertyFloat(PackKnownProperties.REQUIREMENTS_PURCHASE_COOLDOWN, pack.getRequirementPurchaseCooldown()));

                // requirements - tags
                requirementTagsArray = new ArkArrayString();
                requirementTagsArray.addAll(pack.getTags());
                requirementProperties.add(new PropertyArray(PackKnownProperties.REQUIREMENTS_CUSTOM_TAGS, requirementTagsArray));

                // add requirements property
                packProperties.add(new PropertyStruct(PackKnownProperties.REQUIREMENTS, new StructPropertyList(requirementProperties), PackKnownProperties.PACK_REQUIREMENTS_STRUCT_TYPE));

                // pack
                packProperties.add(new PropertyInt(PackKnownProperties.PACK_VERSION, pack.getPackVersion()));

                // add pack
                packDataArray.add(new StructPropertyList(packProperties));
            }
            fileProperties.add(packData);

            // add fileProperties
            file.setProperties(fileProperties);

            file.writeBinary(path);
        } catch (Throwable throwable) {
            throw new SaveGameException("Failed to save SaveGame", throwable);
        }
    }

    public float getModVersion() {
        return this.modVersion;
    }

    public void setModVersion(float modVersion) {
        this.modVersion = modVersion;
    }

}