import webapp2

from google.appengine.ext import db
from google.appengine.ext.webapp import template

class Msg(db.Model):
    msg = db.StringProperty(
                required=True)



class Users(db.Model):
    usrname = db.StringProperty(required = True)
    pwd = db.StringProperty(required = True)

class Message(db.Model):
    usrname = db.StringProperty(required = True)
    friendId = db.StringProperty(required = True)
    type = db.StringProperty(required = True)
    data = db.BlobProperty(required = True)


class DefaultHandler(webapp2.RequestHandler):
    def get(self):
        msgs = db.GqlQuery('SELECT * FROM Message')
        users = db.GqlQuery('SELECT * FROM Users')
        values = {
            'msgs': msgs,
            'users':users
        }
        self.response.out.write(template.render('helloworld.html',
                                                values))



class PostMessageHandler(webapp2.RequestHandler):
    def post(self):
        usrname = self.request.get("usrname")
        friendId = self.request.get("friendId")
        type = self.request.get("type")
        data = self.request.get("data")
        new_message = Message(usrname=usrname,friendId=friendId,type=type,data=data)
        new_message.put()
        self.response.write("OK\n")


class GetBlobHandler(webapp2.RequestHandler):
    def post(self):
        id = self.request.get("id")
        msg = Message.get_by_id(long(id))
        self.response.headers['Content-Type'] = 'application/octet-stream'
        self.response.write(msg.data)

class GetMsgs(webapp2.RequestHandler):
    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        usrname = self.request.get("usrname")
        friendId = self.request.get("friendId")
        for msg in Message.all().filter('usrname = ',usrname).filter('friendId = ',friendId).run():
            response = ";".join([str(msg.key().id()),
                                msg.type]) + "\n"
            self.response.write(response) # return all food item's string property


class SignUpHandler(webapp2.RequestHandler):
    def post(self):
        usrname = self.request.get("usrname")
        pwd = self.request.get("pwd")
        if usrname=="" or pwd=="":
            self.response.write("require both username and password\n")
            return            
        user = Users.all().filter('usrname = ', usrname).get()
        
        if user:
            self.response.write("error: user exists\n")
        else:
            new_user = Users(usrname=usrname,
                             pwd=pwd)
            new_user.put()
            self.response.write("OK\n")


class SignInHandler(webapp2.RequestHandler):
    def post(self):
        usrname = self.request.get("usrname")
        pwd = self.request.get("pwd")
        if usrname=="" or pwd=="":
            self.response.write("require both username and password\n")
            return           
        user = Users.all().filter('usrname = ', usrname).get()
        if not user:
            self.response.write("error: no such user\n")
        elif pwd != user.pwd:
            self.response.write("error: wrong password\n")
        else:
            self.response.write("OK\n")

class AddHandler(webapp2.RequestHandler):
    def get(self):
        msg = Msg(msg=self.request.get('msg'))
        msg.put()
        self.response.out.write('OK')

app = webapp2.WSGIApplication([('/',DefaultHandler),('/add',AddHandler),('/signup',SignUpHandler),('/signin',SignInHandler),('/postmessage',PostMessageHandler),('/getmsgs',GetMsgs),('/getmsgdata',GetBlobHandler)],debug=True)
