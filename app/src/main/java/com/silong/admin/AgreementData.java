package com.silong.admin;

public class AgreementData {

    private String agreementTitle;
    private String agreementBody;

    public AgreementData ( String agreementTitle, String agreementBody){
        this.agreementTitle = agreementTitle;
        this.agreementBody = agreementBody;
    }

    public String getAgreementTitle() {
        return agreementTitle;
    }

    public void setAgreementTitle(String agreementTitle) {
        this.agreementTitle = agreementTitle;
    }

    public String getAgreementBody() {
        return agreementBody;
    }

    public void setAgreementBody(String agreementBody) {
        this.agreementBody = agreementBody;
    }
}
