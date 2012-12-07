import webapp2

from google.appengine.ext import db
from google.appengine.ext.webapp import template

class Msg(db.Model):
    msg = db.StringProperty(
                required=True)



class Users(db.Model):
    usrname = db.StringProperty(required = True)
    pwd = db.StringProperty(required = True)


class DefaultHandler(webapp2.RequestHandler):
    def get(self):
        msgs = db.GqlQuery('SELECT * FROM Msg')
        values = {
            'msgs': msgs
        }
        self.response.out.write(template.render('helloworld.html',
                                                values))
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

app = webapp2.WSGIApplication([('/',DefaultHandler),('/add',AddHandler),('/signup',SignUpHandler),('/signin',SignInHandler)],debug=True)
