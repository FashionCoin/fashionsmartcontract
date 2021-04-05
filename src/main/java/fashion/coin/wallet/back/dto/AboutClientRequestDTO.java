package fashion.coin.wallet.back.dto;

import java.util.List;

public class AboutClientRequestDTO {

    String apikey;

    String about;

    List<SocialLinkDTO> socialLinks;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<SocialLinkDTO> getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(List<SocialLinkDTO> socialLinks) {
        this.socialLinks = socialLinks;
    }
}
