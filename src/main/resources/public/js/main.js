$(document).ready(function() {

});

var loadHeader = function() {
    $.getScript("header.js");
}


console.log("vfdvf");
var myApp = angular.module('myApp', []);

myApp.factory('myService', function() {
    return {
        foo: function() {
            alert("I'm foo!");
        }
    };
});

myApp.run(function($rootScope, myService) {
    $rootScope.appData = myService;
});

myApp.controller('MainCtrl', ['$scope', function($scope){

}]);