package fashion.coin.wallet.back.telegram.service;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowingRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramSearchUsernameResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUserSummary;
import org.bytedeco.javacv.FrameFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class InstagramService {

    Instagram4j instagram = null;

    @Value("${instgram.bot.username}")
    String username;
    @Value("${instgram.bot.password}")
    String password;
    private Pattern pattern;
    private Matcher matcher;
    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";

    @PostConstruct
    public void start() {
        tryConnect();
        pattern = Pattern.compile(USERNAME_PATTERN);
    }

    public boolean validate(final String username){
        matcher = pattern.matcher(username);
        return matcher.matches();

    }

    private void tryConnect() {
        try {

            Thread.sleep(100000);

            // Login to instagram
            instagram = Instagram4j.builder().username(username).password(password).build();
            instagram.setup();
            instagram.login();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean checkFollowing(String instAccaunt) {

        if (instagram == null || !instagram.isLoggedIn()) {
            tryConnect();
        }

        Long userId = null;

        try {
            if(!validate(instAccaunt)) return false;

            InstagramSearchUsernameResult userResult = instagram.sendRequest(new InstagramSearchUsernameRequest(instAccaunt));
            if (userResult == null || userResult.getUser() == null || userResult.getUser().getUsername() == null
                    || userResult.getUser().getFollowing_count() == 0) {
                return false;
            }
            System.out.println("ID for @annakfashion is " + userResult.getUser().getPk());
            System.out.println("Number of followers: " + userResult.getUser().getFollower_count());
            System.out.println("Number of following: " + userResult.getUser().getFollowing_count());
            userId = userResult.getUser().getPk();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String nextMaxId = null;
        while (true) {
            try {
                InstagramGetUserFollowersResult fr = instagram.sendRequest(new InstagramGetUserFollowingRequest(userId, nextMaxId));

                if (fr != null && fr.getUsers() != null) {
                    for (InstagramUserSummary user : fr.getUsers()) {
                        if ("annakfashion".equals(user.getUsername())) {
                            return true;
                        }
                    }
                }

                nextMaxId = fr.getNext_max_id();
                System.out.println(nextMaxId);
                if (nextMaxId == null) {
                    break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;

    }

}
