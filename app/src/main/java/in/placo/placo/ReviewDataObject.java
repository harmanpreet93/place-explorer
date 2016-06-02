package in.placo.placo;


public class ReviewDataObject {
    private String review;
    private String user_name;

    public ReviewDataObject(String review, String user_name){
        this.review = review;
        this.user_name = user_name;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


}