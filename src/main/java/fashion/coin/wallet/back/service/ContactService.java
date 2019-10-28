package fashion.coin.wallet.back.service;

import fashion.coin.wallet.back.dto.AddFriendDTO;
import fashion.coin.wallet.back.dto.AddFriendsDTO;
import fashion.coin.wallet.back.dto.ContactDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.Contact;
import fashion.coin.wallet.back.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JAVA-P on 23.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Service
public class ContactService {

    ContactRepository contactRepository;
    ClientService clientService;

    @Autowired
    public void setContactRepository(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    public void connectFriends(Client sender, Client receiver) {
        if(sender==null || receiver==null) return;
        connect(sender, receiver);
        connect(receiver, sender);
    }

    private void connect(Client listOwner, Client friend) {
        List<Contact> ownersFriend = contactRepository.findAllByListOwnerAndFriend(listOwner, friend);
        if (ownersFriend == null || ownersFriend.isEmpty()) {
            contactRepository.save(new Contact(listOwner, friend));
        }
    }

    public Object addFirendsByPhone(AddFriendsDTO data) {
        try {
            if (!clientService.checkApiKey(data.getLogin(), data.getApikey())) return error109;
            Client client = clientService.findByCryptoname(data.getLogin());
            if (data.getPhones() != null && !data.getPhones().isEmpty()) {
                for (String phone : data.getPhones()) {
                    if(phone.length()<6) continue;
                    List<Client> clientList = clientService.findByPhoneEndingWith(phone);
                    if(clientList != null && !clientList.isEmpty()) {
                        for (Client friend : clientList) {
                            if(friend.isShowPhone()) {
                                connectFriends(client, friend);
                            }
                        }
                    }
                }
            }
            return getAllContacts(client);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private List<ContactDTO> getAllContacts(Client client) {
        List<Contact> contactList = contactRepository.findAllByListOwnerAndDeleted(client, false);
        List<ContactDTO> result = new ArrayList<>();
        if(contactList != null && !contactList.isEmpty()) {
            for (Contact contact : contactList) {
                result.add(new ContactDTO(
                        contact.getFriend().getCryptoname(),
                        contact.getFriend().getPhone(),
                        contact.getFriend().getWalletAddress(),
                        contact.getFriend().getAvatar(),
                        contact.getFriend().avaExists()));
            }
        }
        return result;
    }

    private static final ResultDTO error109 = new ResultDTO(false, "Not valid apikey", 109);
    private static final ResultDTO error112 = new ResultDTO(false, "Friend not found", 112);

    public Object getContactList(AddFriendsDTO data) {
        try {
            if (!clientService.checkApiKey(data.getLogin(), data.getApikey())) return error109;
            Client client = clientService.findByCryptoname(data.getLogin());
            return getAllContacts(client);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public Object addFirend(AddFriendDTO data) {
        try {
            if (!clientService.checkApiKey(data.getLogin(), data.getApikey())) return error109;
            Client client = clientService.findByCryptoname(data.getLogin());
            Client friend = clientService.findByCryptoname(data.getFriend());
            if (friend == null) return error112;
            connectFriends(client, friend);
            return getAllContacts(client);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public void hidePhone(Client client) {
        System.out.println("Togle hide phone for "+client.getCryptoname());

    }
}
