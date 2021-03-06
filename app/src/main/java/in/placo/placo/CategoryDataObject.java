package in.placo.placo;


public class CategoryDataObject {
    private String id;
    private String categoryName;
    private String type;
    private String address;
    private String iconUrl;

    public CategoryDataObject(String id,String categoryName,String type,String address,String iconUrl){
        this.id = id;
        this.categoryName = categoryName;
        this.type = type;
        this.address = address;
        this.iconUrl = iconUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String type) {
        this.iconUrl = iconUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}