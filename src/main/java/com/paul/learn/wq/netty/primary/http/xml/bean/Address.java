package com.paul.learn.wq.netty.primary.http.xml.bean;

import javax.xml.bind.annotation.*;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "address",propOrder = {"street1","street2","city","state","postCode","country"})
public class Address {

    @XmlElement(required = true)
    private String street1;

    @XmlElement(required = true)
    private String street2;

    @XmlElement(required = true)
    private String city;

    @XmlElement(required = true)
    private String state;

    @XmlElement(required = true)
    private String postCode;

    @XmlElement(required = true)
    private String country;

    @Override
    public String toString() {
        return "Address{" +
                "street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postCode='" + postCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
