# Latoken handler

HOST: ```https://api.coin.fashion```

POST: ```/api/latoken/checkemail```

Request **body** JSON: 

```
{"email":"vasya@pupkin.com"}
```

Responce JSON:
```
{"email":"vasya@pupkin.com","status":1}
```

status:

1 - exist

0 - not found

Access only from Latoken IP