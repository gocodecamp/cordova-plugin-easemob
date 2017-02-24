var exec = require('cordova/exec');
var myFunc = function(){};

myFunc.prototype.login = function (options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZJNSHXPlugin', 'login',options);
};

myFunc.prototype.logout = function (options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZJNSHXPlugin', 'logout',options);
};

myFunc.prototype.getAllConversations = function (options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZJNSHXPlugin', 'getAllConversations',options);
};

myFunc.prototype.gotoChat = function (options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZJNSHXPlugin', 'gotoChat',options);
};

myFunc.prototype.delConversationItem = function (options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZJNSHXPlugin', 'delConversationItem',options);
};
// myFunc.prototype.initEaseMobile=function(success, error) {
//     exec(success, error, "ZJNSHXPlugin", "initEaseMobile", []);
// };
// myFunc.prototype.login=function(userName, password,success, error) {
//     exec(success, error, "ZJNSHXPlugin", "login", [userName, password]);
// };
// myFunc.prototype.logout=function(success, error) {
//     exec(success, error, "ZJNSHXPlugin", "logout", []);
// };
// myFunc.prototype.getAllConversations=function(success, error) {
//     exec(success, error, "ZJNSHXPlugin", "getAllConversations", []);
// };
// myFunc.prototype.delConversationItem=function(conversation, success, error) {
//     exec(success, error, "ZJNSHXPlugin", "delConversationItem", [conversation]);
// };
// myFunc.prototype.gotoChat=function(ext) {
//     exec(null, null, "ZJNSHXPlugin", "gotoChat", [ext]);
// };

var zjnsPlugin = new myFunc();
module.exports = zjnsPlugin;
