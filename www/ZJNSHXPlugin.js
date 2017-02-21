var exec = require('cordova/exec');
var myFunc = function(){};

myFunc.prototype.initEaseMobile=function(success, error) {
    exec(success, error, "ZJNSHXPlugin", "initEaseMobile", []);
};
myFunc.prototype.login=function(userName, password,success, error) {
    exec(success, error, "ZJNSHXPlugin", "login", [userName, password]);
};
myFunc.prototype.logout=function(success, error) {
    exec(success, error, "ZJNSHXPlugin", "logout", []);
};
myFunc.prototype.getAllConversations=function(success, error) {
    exec(success, error, "ZJNSHXPlugin", "getAllConversations", []);
};
myFunc.prototype.delConversationItem=function(conversation, success, error) {
    exec(success, error, "ZJNSHXPlugin", "delConversationItem", [conversation]);
};
myFunc.prototype.gotoChat=function(ext) {
    exec(null, null, "ZJNSHXPlugin", "gotoChat", [ext]);
};

var zjnsPlugin = new myFunc();
module.exports = zjnsPlugin;
