# FriendsManagement

**Show list friends of an email**
----
Return json that contains list emails and number of emails 
* **URL**

   `/emails/friends`
  
* **METHOD**

  `POST`
  
* **Request Json**

  `{
      "email": "example@gmail.com"
  }`
* **Success Response**

  `{
    "success": "true",
    "friends": [
        "hoauanh@gmail.com",
        "huynhquang@gmail.com",
        "vuiquanghau@gmail.com",
        "alone@gmail.com"
    ],
    "count": 4
}`
    
