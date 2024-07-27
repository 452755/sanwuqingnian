package com.emms.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-10-24.
 */

public class StyleChangeInfo {
    private String operationName;
    private String operationType;
    private String requestqty;
    private String attachmentType;
    private String attachmentId;
    private String version;
    private String status;
    private String scanQty;
    private String returnQty;
    private String location;
    private String issueQty;
    private String orderNo;
    private String joCloseDate;
    private String receiptQty;
    private String des;//每个工序的描述
    private List<String> qrCodeList = new ArrayList<>();

    private String receiver; // 接收人 2021/04/21 mark

    public List<String> getQrCodeList() {
        return qrCodeList;
    }

    public void setQrCodeList(List<String> qrCodeList) {
        this.qrCodeList = qrCodeList;
    }

    public void setIssueQty(String issueQty) {
        this.issueQty = issueQty;
    }

    public void setReceiptQty(String receiptQty) {
        this.receiptQty = receiptQty;
    }

    public String getReceiptQty() {
        return receiptQty;
    }

    public String getIssueQty() {

        return issueQty;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {

        return location;
    }

    public void setScanQty(String scanQty) {
        this.scanQty = scanQty;
    }

    public String getScanQty() {

        return scanQty;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {

        return status;
    }

    public void setVersion(String version) {

        this.version = version;
    }

    public String getVersion() {

        return version;
    }

    public void setAttachmentId(String attachmentId) {

        this.attachmentId = attachmentId;
    }

    public String getAttachmentId() {

        return attachmentId;
    }

    public void setAttachmentType(String attachmentType) {

        this.attachmentType = attachmentType;
    }

    public String getAttachmentType() {

        return attachmentType;
    }

    public void setRequestqty(String requestqty) {

        this.requestqty = requestqty;
    }

    public String getRequestqty() {

        return requestqty;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {

        return operationType;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getJoCloseDate() {
        return joCloseDate;
    }

    public void setJoCloseDate(String joCloseDate) {
        this.joCloseDate = joCloseDate;
    }

    public String getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(String returnQty) {
        this.returnQty = returnQty;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
