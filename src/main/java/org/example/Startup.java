package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Startup {
    private String name;
    private String countryLocated;
    private Boolean funded;
    private List<String> techStack;
    private Boolean hiring;
    private List<String> emails;
    private List<String> links;
    private String additionalInformation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryLocated() {
        return countryLocated;
    }

    public void setCountryLocated(String countryLocated) {
        this.countryLocated = countryLocated;
    }

    public Boolean getFunded() {
        return funded;
    }

    public void setFunded(Boolean funded) {
        this.funded = funded;
    }

    public List<String> getTechStack() {
        return techStack;
    }

    public void setTechStack(List<String> techStack) {
        this.techStack = techStack;
    }

    public Boolean getHiring() {
        return hiring;
    }

    public void setHiring(Boolean hiring) {
        this.hiring = hiring;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
