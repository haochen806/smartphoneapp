smartphoneapp
=============

Instruction for Autograph Book(ABook)
ruiliu, haochen, lxi
Basically, our ABook is an application which combines the cloud contact storage and user friends maintenance. To run our application, simply download our autograph.apk package and install it.
There first comes the Welcome page(WelcomeActivity) with welcome image.

Then it renders to the Log in page(SignInActivity), which asks the registered user to get the access to his account date on the Google App Engine cloud.

If you do not have an account, you can simply click “Register now!”, then it renders to the Sign Up page(SignUpActivity), where you can register your account on the cloud.

Therefore, you can get into our view all friends page(ViewAllFriendsActivity), in this page, you can see list of all your friends, search your friend by his/her name, and add one friend as you wish.

If you add one friend, you can add all his information, including his first name, last name, email, home address, mobile phone number, and even change his/her selfie by camera. Only first name is necessary. After clicking “add!”, you get your friend’s profile is saved on the local memory protected by the identity authentication.

If you want to delete one of friends or make some modification, then you can long press that friend’s name, then you can delete him/her, or edit him/her.

If you click one of your friends name, you will be lead to the view friend page(ViewFriendActivity). In this page, you can obtain all his information including his profile and all message left by him from the cloud storage. If you want  your friends leave something(message,image) for you by your phone, you(or your friend) can click the respective buttons.

Clicking “Leave Message”, leave a message, later you can see that on your “view friend” page.

Clicking “Take Picture”, use a camera, later you can see that on your “view friend” page.

