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
   * **Code:** 200 <br />
   **Content:**
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
* **Error Response:**
   * **Request Error:** <br />
           * **Code:** 400 BAD REQUEST <br />
             **Content:** 
             `{
                "error": [
                    "Email mustn't be empty or null"
                ],
                "timestamp": "2020-11-19T03:25:33.534+00:00",
                "status": 400
               }`<br />
           OR

           * **Code:** 400 BAD REQUEST <br />
             **Content:** `{
             "statusCode": 400,
             "message": "Invalid email",
             "timestamp": "2020-11-19T03:26:18.821+00:00",
             "description": "uri=/emails/friends"
            }`
    
