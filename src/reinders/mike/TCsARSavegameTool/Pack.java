package reinders.mike.TCsARSavegameTool;

import java.util.List;

public class Pack {

    private String pid;
    private String name;
    private int position;
    private int cost;
    private String description;
    private String category;
    private Color color;
    private List<PackItem> packItems;
    private List<PackDino> packDinos;
    private boolean requirement_isAdminOnly;
    private boolean requirement_isPrerequisite;
    private int requirement_levelRestriction;
    private int requirement_purchaseLimit;
    private float requirement_purchaseCooldown;
    private List<String> tags;
    private int packVersion;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<PackItem> getItems() {
        return packItems;
    }

    public void setItems(List<PackItem> packItems) {
        this.packItems = packItems;
    }

    public List<PackDino> getDinos() {
        return packDinos;
    }

    public void setDinos(List<PackDino> packDinos) {
        this.packDinos = packDinos;
    }

    public boolean isRequirementIsAdminOnly() {
        return requirement_isAdminOnly;
    }

    public void setRequirementIsAdminOnly(boolean requirementIsAdminOnly) {
        this.requirement_isAdminOnly = requirementIsAdminOnly;
    }

    public boolean isRequirementIsPrerequisite() {
        return requirement_isPrerequisite;
    }

    public void setRequirementIsPrerequisite(boolean requirementIsPrerequisite) {
        this.requirement_isPrerequisite = requirementIsPrerequisite;
    }

    public int getRequirementLevelRestriction() {
        return requirement_levelRestriction;
    }

    public void setRequirementLevelRestriction(int requirementLevelRestriction) {
        this.requirement_levelRestriction = requirementLevelRestriction;
    }

    public int getRequirementPurchaseLimit() {
        return requirement_purchaseLimit;
    }

    public void setRequirementPurchaseLimit(int requirementPurchaseLimit) {
        this.requirement_purchaseLimit = requirementPurchaseLimit;
    }

    public float getRequirementPurchaseCooldown() {
        return requirement_purchaseCooldown;
    }

    public void setRequirementPurchaseCooldown(float requirementPurchaseCooldown) {
        this.requirement_purchaseCooldown = requirementPurchaseCooldown;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getPackVersion() {
        return packVersion;
    }

    public void setPackVersion(int packVersion) {
        this.packVersion = packVersion;
    }

}