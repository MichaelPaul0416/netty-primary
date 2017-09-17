package com.paul.learn.wq.netty.primary.http.xml.bean;

import javax.xml.bind.annotation.*;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "order")
@XmlType
public class Order {

    @XmlAttribute
    private long orderNumber;

    @XmlElement(name = "customer",required = true)
    private Customer customer;

    @XmlElement(name = "billTo",required = true)
    private Address billTo;

    @XmlElement(name = "shipping",required = true)
    private Shipping shipping;

    @XmlElement(name = "shipTo",required = true)
    private Address shipTo;

    @XmlElement(name = "total",required = true)
    private float total;


    @Override
    public String toString() {
        return "Order{" +
                "orderNumber=" + orderNumber +
                ", customer=" + customer +
                ", billTo=" + billTo +
                ", shipping=" + shipping +
                ", shipTo=" + shipTo +
                ", total=" + total +
                '}';
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address getBillTo() {
        return billTo;
    }

    public void setBillTo(Address billTo) {
        this.billTo = billTo;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public Address getShipTo() {
        return shipTo;
    }

    public void setShipTo(Address shipTo) {
        this.shipTo = shipTo;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public long getOrderNumber() {

        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }
}
