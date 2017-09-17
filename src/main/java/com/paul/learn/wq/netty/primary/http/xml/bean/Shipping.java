package com.paul.learn.wq.netty.primary.http.xml.bean;

import javax.xml.bind.annotation.*;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shipping",propOrder = {"STANDARD_MAIL","PRIMARY_MAIL","INTERNATIONAL_MAIL","DOMESTIC_EXPRESS","INTERNATIONAL_EXPRESS"})
public enum Shipping {

    @XmlElement
    STANDARD_MAIL,

    @XmlElement
    PRIMARY_MAIL,

    @XmlElement
    INTERNATIONAL_MAIL,

    @XmlElement
    DOMESTIC_EXPRESS,

    @XmlElement
    INTERNATIONAL_EXPRESS;
}
