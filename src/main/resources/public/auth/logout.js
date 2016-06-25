// Handle logout
function Logout($scope, $http, $rootScope) {

    //$http.defaults.headers.common.Authorization = 'Basic Logout';

    localStorage.setItem("username", "logout");
    localStorage.setItem("password", "logout");

    document.cookie = "JSESSIONID=;";

    $http.defaults.headers.common.Authorization = 'Basic '+btoa(localStorage.getItem("username")+":"+localStorage.getItem("password"));
    $http.get("/api/concert")
        .success(function(data)
    {
        //window.location = "/";
    }).error(function(data) {
            window.location = "/";
    });
}