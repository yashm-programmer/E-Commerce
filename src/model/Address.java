package model;

public class Address {
    private int id;
    private int userId;
    private String houseNo;
    private String buildingName;
    private String area;
    private String landmark;
    private String city;
    private String state;
    private String pincode;

    public Address(int id, int userId, String houseNo, String buildingName, String area, String landmark, String city, String state, String pincode) {
        this.id = id;
        this.userId = userId;
        this.houseNo = houseNo;
        this.buildingName = buildingName;
        this.area = area;
        this.landmark = landmark;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getArea() {
        return area;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}