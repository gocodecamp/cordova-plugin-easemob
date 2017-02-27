var exec = require('cordova/exec');
var myFunc = function(){};

myFunc.prototype.initEaseMobile=function(success, error) {
    exec(success, error, "ZJNSHXPlugin", "initEaseMobile", []);
};

myFunc.prototype.login=function(userName, password, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZJNSHXPlugin', 'login', [userName, password]);
};

myFunc.prototype.logout = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZJNSHXPlugin', 'logout', []);
};

myFunc.prototype.getAllConversations = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZJNSHXPlugin', 'getAllConversations', []);
};

myFunc.prototype.delConversationItem = function (conversationId, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZJNSHXPlugin', 'delConversationItem', [conversationId]);
};

myFunc.prototype.gotoChat = function (options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'ZJNSHXPlugin', 'gotoChat', [options]);
};

var zjnsPlugin = new myFunc();
module.exports = zjnsPlugin;
