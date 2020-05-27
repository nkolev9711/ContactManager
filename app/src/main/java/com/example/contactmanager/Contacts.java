package com.example.contactmanager;

public class Contacts {

    private String _name, _details;
    private int _phone;
    private int _id;

    public Contacts(int id, String name, int phone, String details){
        _name = name;
        _phone = phone;
        _details = details;
        _id = id;
    }

    public int getId() {return _id; }

    public String getName(){
        return _name;
    }

    public int getPhone(){
        return (_phone);
    }

    public String getDetails(){
        return _details;
    }


}




