package fashion.coin.wallet.back.api;

import fashion.coin.wallet.back.dto.AddFriendDTO;
import fashion.coin.wallet.back.dto.AddFriendsDTO;
import fashion.coin.wallet.back.dto.ContactDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by JAVA-P on 23.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Controller
public class ContactsController {

    ContactService contactService;

    @PostMapping("/api/v1/addfriends")
    @ResponseBody
    Object addFriendsByPhone(@RequestBody AddFriendsDTO addFriends){
      return  contactService.addFirendsByPhone(addFriends);
    }

    @PostMapping("/api/v1/contacts")
    @ResponseBody
    Object getContactList(@RequestBody AddFriendsDTO addFriendsDTO){
        return contactService.getContactList(addFriendsDTO);
    }


    @PostMapping("/api/v1/addcontact")
    @ResponseBody
    Object addFriend(@RequestBody AddFriendDTO addFriend){
        return  contactService.addFirend(addFriend);
    }

    @Autowired
    public void setContactService(ContactService contactService) {
        this.contactService = contactService;
    }
}
