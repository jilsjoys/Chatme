package com.example.chatme;

public class contacts
{
    public String name,status,imageurl,phonenumber;

    public contacts()
    {

    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getNum() {
        return phonenumber;
    }

    public void setNum(String phonenumber) {
        this.phonenumber = phonenumber;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return imageurl;
    }

    public void setImage(String imageurl) {
        this.imageurl = imageurl;
    }

    public contacts(String name, String status, String imageurl,String phonenumber) {
        this.name = name;
        this.status = status;
        this.imageurl = imageurl;
        this.phonenumber = phonenumber;
    }
}
