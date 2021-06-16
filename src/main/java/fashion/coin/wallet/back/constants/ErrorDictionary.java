package fashion.coin.wallet.back.constants;

import fashion.coin.wallet.back.dto.ResultDTO;

public class ErrorDictionary {

    public static final ResultDTO error100 = new ResultDTO(false, "Client with this login exists", 100);
    public static final ResultDTO error101 = new ResultDTO(false, "Wallet does not created", 101);
    public static final ResultDTO error102 = new ResultDTO(false, "This is brand name", 102);
    public static final ResultDTO error103 = new ResultDTO(false, "Public Key in transaction not equal wallet", 103);
    public static final ResultDTO error104 = new ResultDTO(false, "Cryptoname must be lower case", 104);
    public static final ResultDTO error105 = new ResultDTO(false, "Emoji error, or short login", 105);
    public static final ResultDTO error106 = new ResultDTO(false, "Can't find pub_key param", 106);
    public static final ResultDTO error107 = new ResultDTO(false, "Can't find apikey param", 107);
    public static final ResultDTO error108 = new ResultDTO(false, "Please, use crypto names to send FSHN", 108);
    public static final ResultDTO error109 = new ResultDTO(false, "Not valid apiKey", 109);
    public static final ResultDTO error110 = new ResultDTO(false, "Unknown  email", 110);
    public static final ResultDTO error111 = new ResultDTO(false, "Email is not confirmed", 111);
    public static final ResultDTO error112 = new ResultDTO(false, "Friend not found", 112);
    public static final ResultDTO error113 = new ResultDTO(false, "Login has been changed once", 113);
    public static final ResultDTO error114 = new ResultDTO(false, "This Email is already use", 114);
    public static final ResultDTO error115 = new ResultDTO(false, "Not valid Signature", 115);
    public static final ResultDTO error116 = new ResultDTO(false, "This wallet already exists", 116);
    public static final ResultDTO error117 = new ResultDTO(false, "This ApiKey has already been used", 117);
    public static final ResultDTO error118 = new ResultDTO(false, "Can't find client param", 118);
    public static final ResultDTO error119 = new ResultDTO(false, "This photo has already been used as a Mnemonic Pic for another Crypto Name. Please choose another photo.", 119);
    public static final ResultDTO error120 = new ResultDTO(false, "This phone already using", 120);
    public static final ResultDTO error121 = new ResultDTO(false, "This Registration Code has already been used", 121);
    public static final ResultDTO error122 = new ResultDTO(false, "Banned", 122);
    public static final ResultDTO error123 = new ResultDTO(false, "This file is already exists", 123);
    public static final ResultDTO error124 = new ResultDTO(false, "Only Main feeds for guest users", 124);
    public static final ResultDTO error125 = new ResultDTO(false, "Use one of feed types: main, proof, proofs", 125);
    public static final ResultDTO error126 = new ResultDTO(false, "Illegal page or perPage", 126);
    public static final ResultDTO error127 = new ResultDTO(false, "Client not found", 127);


    public static final ResultDTO error200 = new ResultDTO(false, "Sender Wallet not found", 200);
    public static final ResultDTO error201 = new ResultDTO(false, "Sender not found", 201);
    public static final ResultDTO error202 = new ResultDTO(false, "Not enough money", 202);
    public static final ResultDTO error203 = new ResultDTO(false, "Please, use crypto names to send FSHN", 203);
    public static final ResultDTO error204 = new ResultDTO(false, "Blockchain transaction not found", 204);
    public static final ResultDTO error205 = new ResultDTO(false, "Blockchain transaction error", 205);
    public static final ResultDTO error206 = new ResultDTO(false, "Data of blockchain transaction does not match the parameters passed", 206);
    public static final ResultDTO error207 = new ResultDTO(false, "Receiver wallet must be MoneyBag", 207);
    public static final ResultDTO error208 = new ResultDTO(false, "Ethereum transaction already exists", 208);
    public static final ResultDTO error209 = new ResultDTO(false, "Ethereum transaction doesn't exists", 209);
    public static final ResultDTO error210 = new ResultDTO(false, "Amount doesn't equal", 210);
    public static final ResultDTO error211 = new ResultDTO(false, "This is not BURN transaction", 211);
    public static final ResultDTO error212 = new ResultDTO(false, "NFT creative value not equal transaction amount", 212);
    public static final ResultDTO error213 = new ResultDTO(false, "NFT not found", 213);
    public static final ResultDTO error214 = new ResultDTO(false, "Client isn't owner NFT", 214);
    public static final ResultDTO error215 = new ResultDTO(false, "Only increase value of NFT", 215);
    public static final ResultDTO error216 = new ResultDTO(false, "NFT value already changed one time", 216);
    public static final ResultDTO error217 = new ResultDTO(false, "Overdraft of Proofs limit. Only 100 proofs per 24h", 217);
    public static final ResultDTO error218 = new ResultDTO(false, "Value must be biggest than 0", 218);
    public static final ResultDTO error219 = new ResultDTO(false, "Creative value limit x100 ", 219);
    public static final ResultDTO error220 = new ResultDTO(false, "Way of allocating funds not found", 220);
    public static final ResultDTO error221 = new ResultDTO(false, "NFT in sale process. Please wait", 221);
    public static final ResultDTO error222 = new ResultDTO(false, "Only one proof to one NFT from one user", 222);
    public static final ResultDTO error223 = new ResultDTO(false, "So small amount to exchange. Add money", 223);
    public static final ResultDTO error224 = new ResultDTO(false, "Incorrect fee transaction", 224);
    public static final ResultDTO error225 = new ResultDTO(false, "Only ten free NFT per day", 225);
    public static final ResultDTO error226 = new ResultDTO(false, "Not enough NFT", 226);
    public static final ResultDTO error227 = new ResultDTO(false, "Tirage NFT can not be Proof", 227);
    public static final ResultDTO error228 = new ResultDTO(false, "Owner does not have tirage this NFT", 228);
    public static final ResultDTO error229 = new ResultDTO(false, "Use another methiod for tirage NFT", 229);
    public static final ResultDTO error230 = new ResultDTO(false, "The currency exchange rate does not match the current one too much", 230);
    public static final ResultDTO error231 = new ResultDTO(false, "Payment bill not found. Create it", 231);
    public static final ResultDTO error232 = new ResultDTO(false, "Invalid card number in 'cc_number' field. Luhn check failed.': must be a String and valid card number", 232);
    public static final ResultDTO error233 = new ResultDTO(false, "You are not a member of this chat", 233);
    public static final ResultDTO error234 = new ResultDTO(false, "You are not a NFT sender", 234);
    public static final ResultDTO error235 = new ResultDTO(false, "Friend are not a NFT receiver", 235);
    public static final ResultDTO error236 = new ResultDTO(false, "Transfer NFT event not found", 236);
    public static final ResultDTO error237 = new ResultDTO(false, "You are not transaction sender", 237);
    public static final ResultDTO error238 = new ResultDTO(false, "Friend are not transaction receiver", 238);
    public static final ResultDTO error239 = new ResultDTO(false, "Transaction not found", 239);
    public static final ResultDTO error240 = new ResultDTO(false, "Timestamp is so small", 240);
    public static final ResultDTO error241 = new ResultDTO(false, "Sender and receiver is a same person", 241);
    public static final ResultDTO error242 = new ResultDTO(false, "the number of FSHN we buy is less than the cost NFT", 242);

}
