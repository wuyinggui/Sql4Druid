package com.wyg.druidservice.entity;

import com.wyg.druidservice.annotation.Column;


public class AppInfoLog {
    @Column("api")
    private String api;
    @Column("appid")
    private String appid;
    @Column("channel")
    private String channel;
    @Column("costtime")
    private Long costtime;
    @Column("count")
    private Long count;
    @Column("count_sum")
    private Long countSum;
    @Column("domain")
    private String domain;
    @Column("errorsubtype")
    private String errorsubtype;
    @Column("errortype")
    private String errortype;
    @Column("host")
    private String host;
    @Column("module")
    private String module;
    @Column("network")
    private String network;
    @Column("networkprovider")
    private String networkprovider;
    @Column("platform")
    private String platform;
    @Column("protocol")
    private String protocol;
    @Column("requestmethod")
    private String requestmethod;
    @Column("retrytype")
    private String retrytype;
    @Column("status")
    private String status;
    @Column(value = "__time",istimecolumn = true)
    private String time;

    private String begin;

    private String end;

    @Column(refercolumn = "api",operator = "like")
    private String urlfuzzmatch;
    @Column(refercolumn = "network",operator = "in")
    private String networks;
    @Column(refercolumn = "module",operator = "in")
    private String serviceGroup;


    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Long getCosttime() {
        return costtime;
    }

    public void setCosttime(Long costtime) {
        this.costtime = costtime;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getCountSum() {
        return countSum;
    }

    public void setCountSum(Long countSum) {
        this.countSum = countSum;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getErrorsubtype() {
        return errorsubtype;
    }

    public void setErrorsubtype(String errorsubtype) {
        this.errorsubtype = errorsubtype;
    }

    public String getErrortype() {
        return errortype;
    }

    public void setErrortype(String errortype) {
        this.errortype = errortype;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getNetworkprovider() {
        return networkprovider;
    }

    public void setNetworkprovider(String networkprovider) {
        this.networkprovider = networkprovider;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRequestmethod() {
        return requestmethod;
    }

    public void setRequestmethod(String requestmethod) {
        this.requestmethod = requestmethod;
    }

    public String getRetrytype() {
        return retrytype;
    }

    public void setRetrytype(String retrytype) {
        this.retrytype = retrytype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getUrlfuzzmatch() {
        return urlfuzzmatch;
    }

    public void setUrlfuzzmatch(String urlfuzzmatch) {
        this.urlfuzzmatch = urlfuzzmatch;
    }

    public String getNetworks() {
        return networks;
    }

    public void setNetworks(String networks) {
        this.networks = networks;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }
}
