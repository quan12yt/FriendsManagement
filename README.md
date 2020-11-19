# FriendsManagement

**Add friend connection between two emails**
----
Return json that contains list emails and number of emails 
* **URL**

   `/emails/add`
  
* **METHOD**

  `POST`
  
* **Request Json**

  `{
      "email": "example@gmail.com"
  }`
* **Success Response**
   * **Code:** 201 CREATED <br />
   **Content:**
  `{
    "success": "true",
   }`
* **Error Response:**
   * **Request Error:** <br />
             **Code:** 400 BAD REQUEST <br /><br />
             **Content:** 
             `{
                "error": [
                    "List email must not be null or empty"
                ],
                "timestamp": "2020-11-19T03:52:02.649+00:00",
                "status": 400
            }`<br /><br />
               OR<br /><br />
             **Code:** 400 BAD REQUEST <br />
             **Content:** `{
                "statusCode": 400,
                "message": "Must contains 2 emails",
                "timestamp": "2020-11-19T03:53:23.759+00:00",
                "description": "uri=/emails/add"
            }` <br /><br />
            OR<br /><br />
             **Code:** 400 BAD REQUEST <br />
             **Content:** `{
                "statusCode": 400,
                "message": "Invalid email",
                "timestamp": "2020-11-19T04:16:27.903+00:00",
                "description": "uri=/emails/add"
            }` <br /><br />
  -------
    * **Data Error:** <br />
             **Code:** 404 NOT FOUND <br />
                   **Content:** `{
                      "statusCode": 404,
                      "message": "Both emails have to be in database",
                      "timestamp": "2020-11-19T04:17:26.364+00:00",
                      "description": "uri=/emails/add"
                  }` <br /><br />
                  OR<br /><br />
             **Code:** 400 BAD REQUEST <br />
             **Content:** `{
                "statusCode": 400,
                "message": "This email has been blocked !!",
                "timestamp": "2020-11-19T04:16:27.903+00:00",
                "description": "uri=/emails/add"
            }` <br /><br />
            OR<br /><br />
             **Code:** 400 BAD REQUEST <br />
             **Content:** `{
                "statusCode": 400,
                "message": "Two Email have already being friend",
                "timestamp": "2020-11-19T04:16:27.903+00:00",
                "description": "uri=/emails/add"
            }` <br /><br />
-------------------------------------------------------------
                  
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
        "huynhquang@gmail.com",
        "vuiquanghau@gmail.com"
    ],
    "count": 2
   }`<br /> OR <br />
   
   * **Code:** 204 NOT FOUND <br />
   **Content:**
  `{
    1
   }`
* **Error Response:**
   * **Request Error:** <br />
             **Code:** 400 BAD REQUEST <br />
             **Content:** 
             `{
                "error": [
                    "Email mustn't be empty or null"
                ],
                "timestamp": "2020-11-19T03:25:33.534+00:00",
                "status": 400
               }`<br /><br />
               OR 
               <br /> <br />
             **Code:** 400 BAD REQUEST <br />
             **Content:** `{
             "statusCode": 400,
             "message": "Invalid email",
             "timestamp": "2020-11-19T03:26:18.821+00:00",
             "description": "uri=/emails/friends"
            }` <br />
   --------
    * **Data Error:** <br />
             **Code:** 404 NOT FOUND <br />
                   **Content:** `{
                      "statusCode": 404,
                      "message": "Email not found in database",
                      "timestamp": "2020-11-19T03:40:50.341+00:00",
                      "description": "uri=/emails/friends"
                  }` <br />
    -----------------------------------------------------------
    
**Return list mutual friend's email between among two emails**
----
Return json that contains list emails and number of emails 
* **URL**

   `/emails/common`
  
* **METHOD**

  `POST`
  
* **Request Json**

  `{
      "email": "example@gmail.com"
  }`
* **Success Response**
   * **Code:** 200 OK <br />
   **Content:**
  `{
    "success": "true",
    "friends": [
        "huynhquang@gmail.com",
        "vuiquanghau@gmail.com"
    ],
    "count": 2
   }`
   `<br /> OR <br />
   
   * **Code:** 204 NOT FOUND <br />
   **Content:**
  `{
    1
   }`
   
* **Error Response:**
   * **Request Error:** <br />
             **Code:** 400 BAD REQUEST <br /><br />
             **Content:** 
             `{
                "error": [
                    "List email must not be null or empty"
                ],
                "timestamp": "2020-11-19T03:52:02.649+00:00",
                "status": 400
            }`<br /><br />
               OR<br /><br />
             **Code:** 400 BAD REQUEST <br />
             **Content:** `{
                "statusCode": 400,
                "message": "Must contains 2 emails",
                "timestamp": "2020-11-19T03:53:23.759+00:00",
                "description": "uri=/emails/add"
            }` <br /><br />
            OR<br /><br />
             **Code:** 400 BAD REQUEST <br />
             **Content:** `{
                "statusCode": 400,
                "message": "Invalid email",
                "timestamp": "2020-11-19T04:16:27.903+00:00",
                "description": "uri=/emails/add"
            }` <br /><br />
  -------
    * **Data Error:** <br />
             **Code:** 404 NOT FOUND <br />
                   **Content:** `{
                      "statusCode": 404,
                      "message": "Email not exist",
                      "timestamp": "2020-11-19T04:17:26.364+00:00",
                      "description": "uri=/emails/add"
                  }` <br /><br />
                 

