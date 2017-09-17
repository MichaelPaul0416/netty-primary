package com.paul.learn.wq.netty.primary.http.xml.bean;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customer",propOrder = {"customerNumber","firstName","lastName","middleName"})
public class Customer {

    @XmlAttribute(required = true)
    private long customerNumber;

    @XmlElement(required = true)
    private String firstName;

    @XmlElement(required = true)
    private String lastName;

    @XmlElement(required = true)
    private List<String> middleName;

    @Override
    public String toString() {
        return "Customer{" +
                "customerNumber=" + customerNumber +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName=" + middleName +
                '}';
    }

    public long getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(long customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getMiddleName() {
        return middleName;
    }

    public void setMiddleName(List<String> middleName) {
        this.middleName = middleName;
    }
}
