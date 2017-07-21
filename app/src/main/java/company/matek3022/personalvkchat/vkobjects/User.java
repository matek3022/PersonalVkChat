package company.matek3022.personalvkchat.vkobjects;

import java.io.Serializable;

/**
 * Created by Каракатица on 07.10.2016.
 */

public class User implements Serializable {

    public User (String fn,String ln, String ph, int on) {
        first_name=fn;
        last_name=ln;
        photo_200=ph;
        online=on;
    }
    public User (int us_id,int on,String fn,String ln, String ph){
        id=us_id;
        online=on;
        first_name=fn;
        last_name=ln;
        photo_100=ph;
    }
    public User () {
        first_name="";
        last_name="";
        photo_200="";
        photo_50="";
        body="";
        online=10;
        id=0;
    }
    private String first_name;
    private String last_name;
    private String photo_max_orig;
    private String photo_400_orig;
    private String photo_200;
    private String photo_100;
    private String photo_50;
    private String body;
    private int online;
    private int id;
    private city city;
    private country country;
    private String university_name;
    private String faculty_name;
    private String education_form;
    private String education_status;
    private String bdate;
    private String mobile_phone;
    private String home_phone;

    public String getUniversity_name() {
        return university_name;
    }

    public String getFaculty_name() {
        return faculty_name;
    }

    public String getEducation_form() {
        return education_form;
    }

    public String getEducation_status() {
        return education_status;
    }

    public String getBdate() {
        return bdate;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public String getHome_phone() {
        return home_phone;
    }

    public  int getOnline(){
        return online;
    }
    public int getId() {

        return id;
    }
    public String getFirst_name() {
        return first_name;
    }

    public String getPhoto_max_orig() {
        return photo_max_orig;
    }

    public String getPhoto_400_orig() {
        return photo_400_orig;
    }

    public String getPhoto_100() {
        return photo_100;
    }

    public String getPhoto_200(){
        return photo_200;
    }
    public String getPhoto_50(){
        return photo_50;
    }

    public String getLast_name() {
        return last_name;
    }
    public void setId (int i) {id=i;}
    public void setFirst_name(String fn) {
        first_name=fn;
    }
    public void setLast_name(String fn){
        last_name=fn;
    }
    public void setPhoto_200(String fn){
        photo_200=fn;
    }
    public void setPhoto_50(String fn){
        photo_50=fn;
    }
    public void setOnline (int i) {
        online=i;
    }

    public User.city getCity() {
        return city;
    }

    public User.country getCountry() {
        return country;
    }


    public  class city{
        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }
    public  class country{
        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }

}
