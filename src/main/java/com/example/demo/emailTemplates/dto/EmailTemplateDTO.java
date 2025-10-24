package com.example.demo.emailTemplates.dto;

import com.example.demo.emailTemplates.model.EmailTemplates;

public class EmailTemplateDTO {
    private Integer id;
    private String type;
    private String greeting;
    private String salutation;
    private String body;
    private String linkCta;
    private String support;
    private String copyrightNotice;
    private String campusName;
    private String campusAddress;
    private String linkFacebook;
    private String linkYoutube;
    private String linkTiktok;
    private byte[] headerImage;
    private byte[] bannerImage;
    private String creatorName;

    // --- Constructor để map từ Entity sang DTO ---
    public EmailTemplateDTO(EmailTemplates template) {
        this.id = template.getId();
        this.type = template.getType() != null ? template.getType().name() : null;
        this.greeting = template.getGreeting();
        this.salutation = template.getSalutation();
        this.body = template.getBody();
        this.linkCta = template.getLinkCta();
        this.support = template.getSupport();
        this.copyrightNotice = template.getCopyrightNotice();

        if (template.getCampus() != null) {
            this.campusName = template.getCampus().getCampusName();
            this.campusAddress = template.getCampus().getCampusName();
        }

        this.linkFacebook = template.getLinkFacebook();
        this.linkYoutube = template.getLinkYoutube();
        this.linkTiktok = template.getLinkTiktok();
        this.headerImage = template.getHeaderImage();
        this.bannerImage = template.getBannerImage();

        if (template.getCreator() != null) {
            this.creatorName = template.getCreator().getFullName(); // giả sử Admins có fullName
        }
    }

    // --- Getters & Setters ---
    public Integer getId() { return id; }
    public String getType() { return type; }
    public String getGreeting() { return greeting; }
    public String getSalutation() { return salutation; }
    public String getBody() { return body; }
    public String getLinkCta() { return linkCta; }
    public String getSupport() { return support; }
    public String getCopyrightNotice() { return copyrightNotice; }
    public String getCampusName() { return campusName; }
    public String getCampusAddress() { return campusAddress; }
    public String getLinkFacebook() { return linkFacebook; }
    public String getLinkYoutube() { return linkYoutube; }
    public String getLinkTiktok() { return linkTiktok; }
    public byte[] getHeaderImage() { return headerImage; }
    public byte[] getBannerImage() { return bannerImage; }
    public String getCreatorName() { return creatorName; }

    public void setId(Integer id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setGreeting(String greeting) { this.greeting = greeting; }
    public void setSalutation(String salutation) { this.salutation = salutation; }
    public void setBody(String body) { this.body = body; }
    public void setLinkCta(String linkCta) { this.linkCta = linkCta; }
    public void setSupport(String support) { this.support = support; }
    public void setCopyrightNotice(String copyrightNotice) { this.copyrightNotice = copyrightNotice; }
    public void setCampusName(String campusName) { this.campusName = campusName; }
    public void setCampusAddress(String campusAddress) { this.campusAddress = campusAddress; }
    public void setLinkFacebook(String linkFacebook) { this.linkFacebook = linkFacebook; }
    public void setLinkYoutube(String linkYoutube) { this.linkYoutube = linkYoutube; }
    public void setLinkTiktok(String linkTiktok) { this.linkTiktok = linkTiktok; }
    public void setHeaderImage(byte[] headerImage) { this.headerImage = headerImage; }
    public void setBannerImage(byte[] bannerImage) { this.bannerImage = bannerImage; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
}
